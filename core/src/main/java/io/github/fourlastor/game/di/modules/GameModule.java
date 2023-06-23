package io.github.fourlastor.game.di.modules;

import com.badlogic.gdx.InputMultiplexer;
import dagger.Module;
import dagger.Provides;
import io.github.fourlastor.game.GdxGame;
import io.github.fourlastor.game.intro.IntroComponent;
import io.github.fourlastor.game.level.di.LevelComponent;
import javax.inject.Singleton;
import squidpony.squidmath.GWTRNG;

@Module
public class GameModule {

    @Provides
    @Singleton
    public GdxGame game(
            InputMultiplexer multiplexer, LevelComponent.Builder levelBuilder, IntroComponent.Builder introBuilder) {
        return new GdxGame(multiplexer, levelBuilder, introBuilder);
    }

    @Provides
    @Singleton
    public GWTRNG random() {
        return new GWTRNG();
    }
}
