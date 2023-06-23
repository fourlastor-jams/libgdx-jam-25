package io.github.fourlastor.game.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.List;
import javax.inject.Inject;
import squidpony.squidmath.GWTRNG;

public class LevelScreen extends ScreenAdapter {

    private final InputMultiplexer inputMultiplexer;

    private final Stage stage;
    private final GWTRNG rng;

    private final GameState state;

    @Inject
    public LevelScreen(
            InputMultiplexer inputMultiplexer, Stage stage, TextureAtlas atlas, GWTRNG rng, GameState state) {
        this.inputMultiplexer = inputMultiplexer;
        this.stage = stage;
        this.rng = rng;
        this.state = state;
        Image image = new Image(atlas.findRegion("main_art"));
        stage.addActor(image);

        Player firstPlayer = rng.getRandomElement(Player.values());

        scheduleRound(firstPlayer);
    }

    private Player nextPlayer = Player.ONE;

    private void scheduleRound(Player player) {
        nextPlayer = player;
    }

    private void doRound(Player player) {
        // TODO this can happen either automatically or via player interaction ("roll" button)
        Gdx.app.debug("Round", "Starting round for " + player);
        int rollAmount = rollDice();
        Gdx.app.debug("Round", "Player rolled " + rollAmount);
        if (rollAmount <= 0) {
            Gdx.app.debug("Round", "Player rolled a zero");
            scheduleRound(next(player));
            return;
        }

        List<Move> moves = state.getAvailableMoves(player, rollAmount);

        if (moves.isEmpty()) {
            Gdx.app.debug("Round", "No available moves: " + player);
            scheduleRound(next(player));
            return;
        }

        // TODO: here is where human interaction should occur - for now it picks a move randomly
        Move move = rng.getRandomElement(moves);
        // TODO: this will go in the callback of the human interaction
        move.play(state);
        Gdx.app.debug("Round", "Playing move: " + move);
        scheduleRound(move.next());
    }

    private int rollDice() {
        // 1. roll 4 1d2 (values 0, 1)
        int rollAmount = 0;
        for (int i = 0; i < 4; i++) {
            rollAmount += rng.nextInt(2);
        }
        return rollAmount;
    }

    private Player next(Player player) {
        return player == Player.ONE ? Player.TWO : Player.ONE;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY, true);
        stage.getViewport().apply();
        stage.act();
        stage.draw();
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            doRound(nextPlayer);
        }
    }

    @Override
    public void show() {
        // TODO: remove this eventually
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
}
