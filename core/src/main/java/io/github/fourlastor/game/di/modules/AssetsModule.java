package io.github.fourlastor.game.di.modules;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import dagger.Module;
import dagger.Provides;
import io.github.fourlastor.game.level.DiceTextures;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.inject.Singleton;
import squidpony.squidmath.GWTRNG;

@Module
public class AssetsModule {

    private static final String PATH_TEXTURE_ATLAS = "images/packed/images.pack.atlas";
    private static final String PATH_WAVE_SHADER = "shaders/wave.fs";
    public static final String WHITE_PIXEL = "white-pixel";
    private static final String PATH_UNDERWATER_SHADER = "shaders/underwater.fs";
    private static final String MUSIC_PATH =
            "audio/music/relax-chill-out-music-for-landscapes-under-water-animals-forests-8105.ogg";

    @Provides
    @Singleton
    public AssetManager assetManager() {
        AssetManager assetManager = new AssetManager();
        assetManager.load(PATH_TEXTURE_ATLAS, TextureAtlas.class);
        ShaderProgramLoader.ShaderProgramParameter useDefaultVertexShader =
                new ShaderProgramLoader.ShaderProgramParameter();
        useDefaultVertexShader.vertexFile = "shaders/default.vs";
        assetManager.load(PATH_WAVE_SHADER, ShaderProgram.class, useDefaultVertexShader);
        assetManager.load(PATH_UNDERWATER_SHADER, ShaderProgram.class, useDefaultVertexShader);
        assetManager.load("fonts/play-24.fnt", BitmapFont.class);
        assetManager.load(MUSIC_PATH, Music.class);
        ParticleEffectLoader.ParticleEffectParameter parameter = new ParticleEffectLoader.ParticleEffectParameter();
        parameter.atlasFile = PATH_TEXTURE_ATLAS;
        parameter.atlasPrefix = "effects/";
        assetManager.load("effects/bubbles.pfx", ParticleEffect.class, parameter);
        assetManager.finishLoading();
        return assetManager;
    }

    @Provides
    @Singleton
    public TextureAtlas textureAtlas(AssetManager assetManager) {
        return assetManager.get(PATH_TEXTURE_ATLAS, TextureAtlas.class);
    }

    @Provides
    public Music music(AssetManager assetManager) {
        return assetManager.get(MUSIC_PATH);
    }

    @Provides
    @Singleton
    @Named(WHITE_PIXEL)
    public TextureRegion whitePixel(TextureAtlas atlas) {
        return atlas.findRegion("whitePixel");
    }

    @Provides
    @Singleton
    public DiceTextures dices(TextureAtlas atlas, GWTRNG rng) {
        Array<TextureAtlas.AtlasRegion> d0ts = atlas.findRegions("dices/d0");
        Array<TextureAtlas.AtlasRegion> d1ts = atlas.findRegions("dices/d1");
        List<Drawable> d0s = new ArrayList<>(d0ts.size);
        List<Drawable> d1s = new ArrayList<>(d0ts.size);
        for (int i = 0; i < d0ts.size; i++) {
            d0s.add(new TextureRegionDrawable(d0ts.get(i)));
        }
        for (int i = 0; i < d0ts.size; i++) {
            d1s.add(new TextureRegionDrawable(d1ts.get(i)));
        }
        return new DiceTextures(rng, d0s, d1s);
    }
}
