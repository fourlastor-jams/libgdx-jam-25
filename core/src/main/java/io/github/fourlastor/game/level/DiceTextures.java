package io.github.fourlastor.game.level;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import squidpony.squidmath.GWTRNG;

import java.util.List;


public class DiceTextures {
    private final List<Drawable> dice0;
    private final List<Drawable> dice1;

    private final GWTRNG rng;

    public DiceTextures(GWTRNG rng, List<Drawable> dice0, List<Drawable> dice1) {
        this.dice0 = dice0;
        this.dice1 = dice1;
        this.rng = rng;
    }

    public Drawable d0() {
        return rng.getRandomElement(dice0);
    }
    public Drawable d1() {
        return rng.getRandomElement(dice1);
    }
}
