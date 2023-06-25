package io.github.fourlastor.game.level;

import com.badlogic.gdx.scenes.scene2d.Action;
import io.github.fourlastor.game.ui.Pawn;

public abstract class Move {

    public final Player player;
    public final int destination;

    Move(Player player, int destination) {
        this.player = player;
        this.destination = destination;
    }

    public Player next() {
        if (Positions.ROSETTE_POSITIONS.contains(destination)) {
            return player;
        } else {
            return player == Player.ONE ? Player.TWO : Player.ONE;
        }
    }

    public abstract Action play(GameState state, Pawn pawn, Action captureBubbles);

    public static class PlaceFromReserve extends Move {

        PlaceFromReserve(Player player, int destination) {
            super(player, destination);
        }

        @Override
        public Action play(GameState state, Pawn pawn, Action captureBubbles) {
            return state.placeFromReserve(player, destination, pawn);
        }

        @Override
        public String toString() {
            return "PlaceFromReserve{" + "player=" + player + ", destination=" + destination + '}';
        }
    }

    public static class MoveFromBoard extends Move {

        public final int origin;

        MoveFromBoard(Player player, int origin, int destination) {
            super(player, destination);
            this.origin = origin;
        }

        @Override
        public Action play(GameState state, Pawn pawn, Action captureBubbles) {
            return state.moveFromBoard(player, origin, destination, captureBubbles);
        }

        @Override
        public String toString() {
            return "MoveFromBoard{" + "origin=" + origin + ", player=" + player + ", destination=" + destination + '}';
        }
    }
}
