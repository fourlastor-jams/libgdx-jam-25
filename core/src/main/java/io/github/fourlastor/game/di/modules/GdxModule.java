package io.github.fourlastor.game.di.modules;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.JsonReader;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class GdxModule {

    @Provides
    @Singleton
    public JsonReader jsonReader() {
        return new JsonReader();
    }

    @Provides
    @Singleton
    public InputMultiplexer inputMultiplexer() {
        return new InputMultiplexer();
    }

    @Provides
    @Singleton
    public SpriteBatch batch() {
        return new SpriteBatch();
    }
}
