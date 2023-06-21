package io.github.fourlastor.game.level.di;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import dagger.Module;
import dagger.Provides;
import io.github.fourlastor.game.di.ScreenScoped;

@Module
public class LevelModule {

    @Provides
    @ScreenScoped
    public Stage stage(SpriteBatch batch) {
        return new Stage(new FitViewport(533, 300f), batch);
    }

    @Provides
    @ScreenScoped
    public Camera camera(Stage stage) {
        return stage.getCamera();
    }

    @Provides
    @ScreenScoped
    public MessageDispatcher messageDispatcher() {
        return new MessageDispatcher();
    }
}
