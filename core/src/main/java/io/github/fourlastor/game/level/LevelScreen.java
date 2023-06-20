package io.github.fourlastor.game.level;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import javax.inject.Inject;

public class LevelScreen extends ScreenAdapter {

    private final Stage stage;

    @Inject
    public LevelScreen(Stage stage, TextureAtlas atlas) {
        this.stage = stage;
        Image image = new Image(atlas.findRegion("royal-game-bg"));
        stage.addActor(image);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.TEAL);
        stage.getViewport().apply();
        stage.act();
        stage.draw();
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}
}
