package io.github.fourlastor.game.level;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import io.github.fourlastor.game.di.modules.AssetsModule;
import io.github.fourlastor.perceptual.Perceptual;
import squidpony.squidmath.GWTRNG;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SoundPlayer {

    private final Sound bubbles;
    private final Sound pawn;
    private final Sound roll;
    private final Sound tada;
    private final Sound powerup;

    private final GWTRNG rng;

    private boolean muted = false;

    @Inject
    public SoundPlayer(AssetManager manager, GWTRNG rng) {
        bubbles = manager.get(AssetsModule.BUBBLE_SOUND_PATH);
        pawn = manager.get(AssetsModule.PAWN_SOUND_PATH);
        roll = manager.get(AssetsModule.ROLL_SOUND_PATH);
        tada = manager.get(AssetsModule.TADA_SOUND_PATH);
        powerup = manager.get(AssetsModule.POWERUP_SOUND_PATH);
        this.rng = rng;
    }

    public void toggle() {
        muted = !muted;
    }

    public void bubbles() {
        play(bubbles);
    }

    public void pawn() {
        play(pawn);
    }

    public void roll() {
        play(roll);
    }
    public void tada() {
        play(tada);
    }

    public void powerup() {
        play(powerup);
    }

    private void play(Sound sound) {
        if (muted) {
            return;
        }

        sound.play(Perceptual.amplitudeToPerceptual(
                0.3f + rng.nextFloat(0.2f)
        ));
    }
}
