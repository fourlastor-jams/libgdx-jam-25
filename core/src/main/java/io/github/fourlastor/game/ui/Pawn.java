package io.github.fourlastor.game.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import io.github.fourlastor.harlequin.animation.Animation;
import io.github.fourlastor.harlequin.ui.AnimatedImage;

public class Pawn extends AnimatedImage {

    public final Vector2 originalPosition;

    public Pawn(Animation<Drawable> drawable, Vector2 originalPosition) {
        super(drawable);
        this.originalPosition = originalPosition;
        setPosition(originalPosition.x, originalPosition.y, Align.center);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        ShaderProgram oldShader = batch.getShader();
        batch.setShader(null);
        super.draw(batch, parentAlpha);
        if (oldShader != null) {
            batch.setShader(oldShader);
            oldShader.bind();
        }
    }
}
