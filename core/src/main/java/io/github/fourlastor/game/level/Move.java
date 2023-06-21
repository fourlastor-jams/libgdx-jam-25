package io.github.fourlastor.game.level;

public abstract class Move {

    public final Player player;
    public final int destination;

    Move(Player player, int destination) {
        this.player = player;
        this.destination = destination;
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
            return "PlaceFromReserve{" +
                    "player=" + player +
                    ", destination=" + destination +
                    '}';
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
            return "MoveFromBoard{" +
                    "origin=" + origin +
                    ", player=" + player +
                    ", destination=" + destination +
                    '}';
        }
    }
}
