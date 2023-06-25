package io.github.fourlastor.game.level;

public enum Player {
    ONE("red"),
    TWO("blue");

    public final String color;

    Player(String color) {
        this.color = color;
    }

    public int pnum() {
        return ordinal() + 1;
    }
}
