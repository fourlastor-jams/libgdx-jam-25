package io.github.fourlastor.game.level;

import java.util.Arrays;
import java.util.List;

public abstract class Move {
    private static final List<Integer> ROSETTE_POSITIONS = Arrays.asList(3, 7, 13);

    public final Player player;
    public final int destination;

    Move(Player player, int destination) {
        this.player = player;
        this.destination = destination;
    }

    public Player next() {
        if (ROSETTE_POSITIONS.contains(destination)) {
            return player;
        } else {
            return player == Player.ONE ? Player.TWO : Player.ONE;
        }
    }

    public abstract void play(GameState state);

    public static class PlaceFromReserve extends Move {

        PlaceFromReserve(Player player, int destination) {
            super(player, destination);
        }

        @Override
        public void play(GameState state) {
            state.placeFromReserve(player, destination);
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
        public void play(GameState state) {
            state.moveFromBoard(player, origin, destination);
        }

        @Override
        public String toString() {
            return "MoveFromBoard{" + "origin=" + origin + ", player=" + player + ", destination=" + destination + '}';
        }
    }
}
