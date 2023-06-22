package io.github.fourlastor.game.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.IntMap;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class GameState {

    private final Stage stage;

    private final Board p1Board = new Board();
    private final Board p2Board = new Board();
    private final Drawable p1Drawable;
    private final Drawable p2Drawable;

    @Inject
    public GameState(Stage stage, TextureAtlas atlas) {
        this.stage = stage;
        p1Drawable = new TextureRegionDrawable(atlas.findRegion("whitePixel")).tint(Color.BLUE);
        p2Drawable = new TextureRegionDrawable(atlas.findRegion("whitePixel")).tint(Color.YELLOW);
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
        // overshoot - the pawn would
        if (desiredPosition >= 14) {
            return false;
        }
        // check for own pawn already at desired position
        if (own.pawnAtPosition(desiredPosition)) {
            return false;
        }
        // shared board on rosette, check also the opponent
        if (desiredPosition == 7 && other.pawnAtPosition(desiredPosition)) {
            return false;
        }
        return true;
    }

    private Board ownBoard(Player player) {
        return player == Player.ONE ? p1Board : p2Board;
    }
    private Board otherBoard(Player player) {
        return player == Player.ONE ? p2Board : p1Board;
    }

    public void placeFromReserve(Player player, int destination) {
        Drawable drawable = player == Player.ONE ? p1Drawable : p2Drawable;
        ownBoard(player).add(player, drawable, destination, stage);
        maybeCapturePawn(player, destination);
    }

    public void moveFromBoard(Player player, int origin, int destination) {
        ownBoard(player).move(origin, destination, player);
        maybeCapturePawn(player, destination);
    }

    private void maybeCapturePawn(Player player, int destination) {
        if (destination > 3 && destination < 12)
            otherBoard(player).remove(destination);
    }

    public

    static class Board {
        final IntMap<Image> pawns = new IntMap<>();

        boolean reserveAvailable() {
            return pawns.size < 7;
        }

        boolean pawnAtPosition(int desiredPosition) {
            for (IntMap.Entry<Image> entry :  new IntMap.Entries<Image>(pawns)) {
                Gdx.app.debug("Round", "Checking position: " + entry.key);
                if (entry.key == desiredPosition) {
                    return true;
                }
            }
            Gdx.app.debug("Round", "All good");
            return false;
        }

        void add(Player player, Drawable drawable, int position, Stage stage) {
            Image pawn = new Image(drawable);
            int width = player == Player.ONE ? 18 : 6;
            int height = player == Player.ONE ? 6 : 18;
            pawn.setSize(width, height);
            pawns.put(position, pawn);
            adjustPosition(player, pawn, position);
            stage.addActor(pawn);
        }

        private static void adjustPosition(Player player, Image image, int position) {
            Vector2 pawnPosition = Positions.toWorldAtCenter(player, position);
            image.setPosition(pawnPosition.x, pawnPosition.y, Align.bottom);
        }

        void move(int origin, int destination, Player player) {
            Image pawn = Objects.requireNonNull(pawns.remove(origin));
            pawns.put(destination, pawn);
            adjustPosition(player, pawn, destination);
        }


        public void remove(int destination) {
            Image pawn = pawns.remove(destination);
            if (pawn == null) {
                return;
            }

            pawn.remove();
        }
    }

}
