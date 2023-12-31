package io.github.fourlastor.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import io.github.fourlastor.game.di.GameComponent;
import io.github.fourlastor.game.di.modules.LogEnabled;
import io.github.fourlastor.game.intro.IntroComponent;
import io.github.fourlastor.game.level.di.LevelComponent;
import io.github.fourlastor.game.route.Router;
import io.github.fourlastor.game.route.RouterModule;
import javax.inject.Inject;

public class GdxGame extends Game implements Router {

    private final InputMultiplexer multiplexer;

    private final LevelComponent.Builder levelScreenFactory;
    private final IntroComponent.Builder introScreenFactory;
    private final boolean enableLogs;

    private Screen pendingScreen = null;

    @Inject
    public GdxGame(
            InputMultiplexer multiplexer,
            LevelComponent.Builder levelScreenFactory,
            IntroComponent.Builder introScreenFactory,
            @LogEnabled boolean enableLogs) {
        this.multiplexer = multiplexer;
        this.levelScreenFactory = levelScreenFactory;
        this.introScreenFactory = introScreenFactory;
        this.enableLogs = enableLogs;
    }

    @Override
    public void create() {
        //        if (Gdx.app.getType() != Application.ApplicationType.Android) {
        //
        //            Cursor customCursor =
        //                    Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("images/included/whitePixel.png")),
        // 0, 0);
        //            Gdx.graphics.setCursor(customCursor);
        //        }
        if (enableLogs) {
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
        }
        Gdx.input.setInputProcessor(multiplexer);
        goToLevel();
    }

    @Override
    public void render() {
        if (pendingScreen != null) {
            setScreen(pendingScreen);
            pendingScreen = null;
        }
        super.render();
    }

    public static GdxGame createGame(boolean enableLogs) {
        return GameComponent.component(enableLogs).game();
    }

    @Override
    public void goToIntro() {
        pendingScreen =
                introScreenFactory.router(new RouterModule(this)).build().screen();
    }

    @Override
    public void goToLevel() {
        pendingScreen =
                levelScreenFactory.router(new RouterModule(this)).build().screen();
    }
}
