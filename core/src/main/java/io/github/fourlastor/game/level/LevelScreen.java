package io.github.fourlastor.game.level;

import com.badlogic.gdx.ScreenAdapter;
import javax.inject.Inject;

public class LevelScreen extends ScreenAdapter {

    @Inject
    public LevelScreen() {}

    @Override
    public void resize(int width, int height) {}

    @Override
    public void render(float delta) {}

    @Override
    public void show() {
        // entitiesFactory.create(...)
    }

    @Override
    public void hide() {}
}
