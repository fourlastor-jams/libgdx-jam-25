package io.github.fourlastor.game.di;

import dagger.Component;
import io.github.fourlastor.game.GdxGame;
import io.github.fourlastor.game.di.modules.AssetsModule;
import io.github.fourlastor.game.di.modules.GameModule;
import io.github.fourlastor.game.di.modules.GdxModule;
import io.github.fourlastor.game.di.modules.LogModule;
import io.github.fourlastor.game.di.modules.ScreensModule;
import javax.inject.Singleton;

@Singleton
@Component(
        modules = {
            GameModule.class,
            AssetsModule.class,
            GdxModule.class,
            ScreensModule.class,
            LogModule.class,
        })
public interface GameComponent {
    GdxGame game();

    static GameComponent component(boolean enableLogs) {
        return DaggerGameComponent.builder()
                .logModule(new LogModule(enableLogs))
                .build();
    }
}
