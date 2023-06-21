package io.github.fourlastor.game.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import javax.inject.Inject;

public class LevelScreen extends ScreenAdapter {

    private final InputMultiplexer inputMultiplexer;

    private final Stage stage;
    private final Drawable pawn;

    @Inject
    public LevelScreen(InputMultiplexer inputMultiplexer, Stage stage, TextureAtlas atlas) {
        this.inputMultiplexer = inputMultiplexer;
        this.stage = stage;
        Image image = new Image(atlas.findRegion("main_art"));
        stage.addActor(image);
        pawn = new TextureRegionDrawable(atlas.findRegion("whitePixel")).tint(Color.BLACK);

        for (int i = 0; i < 14; i++) {
            addPawnAt(Player.ONE, i);
            addPawnAt(Player.TWO, i);
        }
    }

    private final Vector2 location = new Vector2();

    private void addPawnAt(Player player, int position) {
        Image image = new Image(pawn);
        image.setSize(18, 18);
        Vector2 pawnPosition = Positions.toWorldAtCenter(player, position, location);
        image.setPosition(pawnPosition.x, pawnPosition.y, Align.bottom);
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
    public void show() {
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                Vector2 position = stage.getViewport().unproject(new Vector2(screenX, screenY));
                GridPoint2 coordinate = Positions.toCoordinate(position);
                Gdx.app.log("position", "Unprojected to " + coordinate);
                return false;
            }
        });
    }

    @Override
    public void hide() {}
}
