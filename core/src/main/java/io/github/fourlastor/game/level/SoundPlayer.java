package io.github.fourlastor.game.level;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import io.github.fourlastor.game.di.modules.AssetsModule;
import io.github.fourlastor.perceptual.Perceptual;
import javax.inject.Inject;
import javax.inject.Singleton;
import squidpony.squidmath.GWTRNG;

@Singleton
public class SoundPlayer {

    private final Sound bubbles;
    private final Sound pawn;
    private final Sound roll;
    private final Sound tada;
    private final Sound powerup;
    private final Sound hurt;

    private final GWTRNG rng;

    private boolean muted = false;

    @Inject
    public SoundPlayer(AssetManager manager, GWTRNG rng) {
        bubbles = manager.get(AssetsModule.BUBBLE_SOUND_PATH);
        pawn = manager.get(AssetsModule.PAWN_SOUND_PATH);
        roll = manager.get(AssetsModule.ROLL_SOUND_PATH);
        tada = manager.get(AssetsModule.TADA_SOUND_PATH);
        powerup = manager.get(AssetsModule.POWERUP_SOUND_PATH);
        hurt = manager.get(AssetsModule.HURT_SOUND_PATH);
        this.rng = rng;
    }

    public void toggle() {
        muted = !muted;
    }

    public void bubbles() {
        play(bubbles, 0.5f);
    }

    public void pawn() {
        play(pawn, 0.5f);
    }

    public void roll() {
        play(roll, 0.5f);
    }

    public void tada() {
        play(tada, 0.5f);
    }

    public void powerup() {
        play(powerup, 0.35f);
    }

    public void hurt() {
        play(hurt, 0.35f);
    }

    private void play(Sound sound, float volume) {
        if (muted) {
            return;
        }

        sound.play(Perceptual.perceptualToAmplitude(volume), 0.9f + rng.nextFloat(0.2f), 0f);
    }
}
