package io.github.fourlastor.game.level;

public enum Player {
    ONE("blue"),
    TWO("red");

    public final String color;

    Player(String color) {
        this.color = color;
    }
}
