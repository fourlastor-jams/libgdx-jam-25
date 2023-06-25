package io.github.fourlastor.game.level;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AfterAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.IntMap;
import io.github.fourlastor.game.ui.Pawn;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class GameState {

    private static final Runnable EMPTY = () -> {};
    private final Board p1Board;
    private final Board p2Board;
    private final SoundPlayer player;

    public GameState(List<Pawn> p1Pawns, List<Pawn> p2Pawns, SoundPlayer player) {
        p1Board = new Board(p1Pawns);
        p2Board = new Board(p2Pawns);
        this.player = player;
    }

    public List<Move> getAvailableMoves(Player player, int rollAmount) {
        Board own = ownBoard(player);
        Board other = otherBoard(player);
        LinkedList<Move> moves = new LinkedList<>();
        int desiredPosition1 = rollAmount - 1;
        if (own.reserveAvailable() && placeAvailable(desiredPosition1, own, other)) {
            moves.add(new Move.PlaceFromReserve(player, desiredPosition1));
        }

        for (IntMap.Entry<Image> entry : new IntMap.Entries<Image>(own.pawns)) {
            int pawnPosition = entry.key;
            int desiredPosition = pawnPosition + rollAmount;
            if (placeAvailable(desiredPosition, own, other)) {
                moves.add(new Move.MoveFromBoard(player, pawnPosition, desiredPosition));
            }
        }
        return moves;
    }

    public boolean placeAvailable(int desiredPosition, Board own, Board other) {
        // overshoot
        if (desiredPosition > Positions.LAST_POSITION) {
            return false;
        }
        // correct finish
        if (desiredPosition == Positions.LAST_POSITION) {
            return true;
        }
        // check for own pawn already at desired position
        if (own.isPawnAtPosition(desiredPosition)) {
            return false;
        }
        // shared board on rosette, check also the opponent
        if (desiredPosition == Positions.BATTLE_ROSETTE_POSITION && other.isPawnAtPosition(desiredPosition)) {
            return false;
        }
        return true;
    }

    public boolean hasPlayerWon(Player player) {
        Board board = ownBoard(player);
        return board.pawns.isEmpty() && board.availablePawns.isEmpty();
    }

    public Pawn pawnAt(Player player, int position) {
        return ownBoard(player).pawnAt(position);
    }

    public boolean isPawnAtPosition(Player player, int position) {
        return ownBoard(player).isPawnAtPosition(position);
    }

    public List<Pawn> availablePawns(Player player) {
        return ownBoard(player).availablePawns;
    }

    private Board ownBoard(Player player) {
        return player == Player.ONE ? p1Board : p2Board;
    }

    private Board otherBoard(Player player) {
        return player == Player.ONE ? p2Board : p1Board;
    }

    public Action placeFromReserve(Player player, int destination, Pawn pawn) {
        return Actions.parallel(
                ownBoard(player).add(player, destination, pawn),
                maybeCapturePawn(player, destination, Actions.run(EMPTY)));
    }

    public Action moveFromBoard(Player player, int origin, int destination, Action bubbles) {
        Board ownBoard = ownBoard(player);
        return Actions.sequence(
                ownBoard.move(origin, destination, player, bubbles), maybeCapturePawn(player, destination, bubbles));
    }

    private Action maybeCapturePawn(Player player, int destination, Action bubbles) {
        if (destination > 3 && destination < 12) {
            return otherBoard(player).remove(destination, bubbles);
        }

        return Actions.run(EMPTY);
    }

    public class Board {
        final IntMap<Pawn> pawns = new IntMap<>();
        private final List<Pawn> availablePawns;

        public Board(List<Pawn> pawns) {
            this.availablePawns = pawns;
        }

        boolean reserveAvailable() {
            return !availablePawns.isEmpty();
        }

        boolean isPawnAtPosition(int desiredPosition) {
            for (IntMap.Entry<Image> entry : new IntMap.Entries<Image>(pawns)) {
                if (entry.key == desiredPosition) {
                    return true;
                }
            }
            return false;
        }

        Action add(Player player, int position, Pawn pawn) {
            pawns.put(position, pawn);
            availablePawns.remove(pawn);
            List<Action> actions = new ArrayList<>(position);
            for (int i = 0; i <= position; i++) {
                AfterAction after = Actions.after(adjustPosition(player, pawn, i));
                after.setTarget(pawn);
                actions.add(after);
            }
            return Actions.sequence(actions.toArray(new Action[0]));
        }

        private Action adjustPosition(Player player, Image image, int position) {
            Vector2 pawnPosition = Positions.toWorldAtCenter(player, position);
            return adjustPosition(image, pawnPosition);
        }

        private Action adjustPosition(Image image, Vector2 pawnPosition) {
            MoveToAction moveToAction =
                    Actions.moveToAligned(pawnPosition.x, pawnPosition.y, Align.center, 0.25f, Interpolation.exp5Out);
            moveToAction.setActor(image);
            return Actions.parallel(
                    moveToAction,
                    Actions.run(player::pawn)
            );
        }

        Action move(int origin, int destination, Player player, Action bubbles) {
            Pawn pawn = Objects.requireNonNull(pawns.remove(origin));
            int steps = destination - origin;
            List<Action> actions = new ArrayList<>(steps);
            for (int i = 0; i <= steps; i++) {
                int currentStep = origin + i;
                if (currentStep == Positions.LAST_POSITION) {
                    ScaleToAction scale = Actions.scaleTo(0.1f, 0.1f, 0.2f);
                    if (player == Player.TWO) {
                        pawn.setOrigin(Align.right | Align.top);
                    }
                    scale.setActor(pawn);
                    actions.add(Actions.sequence(scale, bubbles, Actions.run(pawn::remove)));
                } else {
                    AfterAction after = Actions.after(adjustPosition(player, pawn, currentStep));
                    after.setTarget(pawn);
                    actions.add(after);
                }
            }
            if (destination != Positions.LAST_POSITION) {
                pawns.put(destination, pawn);
            }
            return Actions.sequence(actions.toArray(new Action[0]));
        }

        public Action remove(int destination, Action captureBubbles) {
            Pawn pawn = pawns.remove(destination);
            if (pawn == null) {
                return Actions.run(EMPTY);
            }
            availablePawns.add(pawn);

            return Actions.parallel(adjustPosition(pawn, pawn.originalPosition), captureBubbles);
        }

        public Pawn pawnAt(int position) {
            return pawns.get(position);
        }
    }
}
