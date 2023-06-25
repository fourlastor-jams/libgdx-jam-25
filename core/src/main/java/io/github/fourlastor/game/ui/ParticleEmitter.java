package io.github.fourlastor.game.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ParticleEmitter extends Actor  {

    private final ParticleEffect effect;

    public ParticleEmitter(ParticleEffect effect, float scale) {
        this.effect = effect;
        effect.scaleEffect(scale);
        effect.reset(false);
    }

    @Override
    public void act(float delta) {
        effect.update(delta);
        if (effect.isComplete()) {
            remove();
        }
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        effect.setPosition(x, y);
    }

    @Override
    public void setPosition(float x, float y, int align) {
        super.setPosition(x, y, align);
        effect.setPosition(x, y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        effect.draw(batch);
    }
}
