package io.github.fourlastor.game.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

public class Pawn extends Image {

    public final Vector2 originalPosition;

    public Pawn(Drawable drawable, Vector2 originalPosition) {
        super(drawable);
        this.originalPosition = originalPosition;
        setPosition(originalPosition.x, originalPosition.y, Align.center);
    }
}
