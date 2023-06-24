package io.github.fourlastor.game.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import squidpony.squidmath.GWTRNG;

public class LevelScreen extends ScreenAdapter {

    private final InputMultiplexer inputMultiplexer;

    private final Stage stage;
    private final GWTRNG rng;

    private final GameState state;
    private final TextButton.TextButtonStyle buttonStyle;

    @Inject
    public LevelScreen(
            InputMultiplexer inputMultiplexer,
            Stage stage,
            TextureAtlas atlas,
            GWTRNG rng,
            GameState state,
            AssetManager assetManager) {
        this.inputMultiplexer = inputMultiplexer;
        this.stage = stage;
        this.rng = rng;
        this.state = state;
        BitmapFont font = assetManager.get("fonts/quan-pixel-32.fnt");
        buttonStyle = new TextButton.TextButtonStyle(null, null, null, font);
        Image image = new Image(atlas.findRegion("main_art"));
        stage.addActor(image);

        Player firstPlayer = rng.getRandomElement(Player.values());

        presentRoll(firstPlayer);
    }

    private Player nextPlayer = Player.ONE;

    private void scheduleRound(Player player) {
        nextPlayer = player;
    }

    private void presentRoll(Player player) {
        Gdx.app.debug("Round", "Starting round for " + player);
        Button rollButton = new TextButton("Roll", buttonStyle);

        rollButton.setPosition(10, 10);
        rollButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pickMove(player, rollDice());
                rollButton.remove();
            }
        });
        stage.addActor(rollButton);
    }

    private void pickMove(Player player, int rollAmount) {
        Gdx.app.debug("Round", "Player rolled " + rollAmount);
        if (rollAmount <= 0) {
            Gdx.app.debug("Round", "Player rolled a zero");
            presentRoll(next(player));
            return;
        }

        List<Move> moves = state.getAvailableMoves(player, rollAmount);

        if (moves.isEmpty()) {
            Gdx.app.debug("Round", "No available moves: " + player);
            presentRoll(next(player));
            return;
        }

        List<Actor> buttons = new ArrayList<>(moves.size());

        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            if (move instanceof Move.MoveFromBoard) {
                Move.MoveFromBoard moveFromBoard = (Move.MoveFromBoard) move;
                Image pawn = state.pawnAt(player, moveFromBoard.origin);
                Action highlight = Actions.forever(
                        Actions.sequence(Actions.color(Color.BLACK, 0.5f), Actions.color(Color.WHITE, 0.5f)));
                pawn.addAction(highlight);
                pawn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        onMovePicked(buttons, move);
                        pawn.removeListener(this);
                        pawn.removeAction(highlight);
                        pawn.setColor(Color.WHITE);
                    }
                });
            } else {
                TextButton moveButton = new TextButton(move.name(), buttonStyle);
                moveButton.setPosition(10, i * 36 + 10);
                moveButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        onMovePicked(buttons, move);
                    }
                });
                buttons.add(moveButton);
                stage.addActor(moveButton);
            }
        }
    }

    private void onMovePicked(List<Actor> buttons, Move move) {
        for (Actor button : buttons) {
            button.remove();
        }
        move.play(state);
        Gdx.app.debug("Round", "Playing move: " + move);
        presentRoll(move.next());
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
        //        inputMultiplexer.addProcessor(new InputAdapter() {
        //            @Override
        //            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        //                Vector2 position = stage.getViewport().unproject(new Vector2(screenX, screenY));
        //                GridPoint2 coordinate = Positions.toCoordinate(position);
        //                Gdx.app.log("position", "Unprojected to " + coordinate);
        //                return false;
        //            }
        //        });
        inputMultiplexer.addProcessor(stage);
    }

    @Override
    public void hide() {
        inputMultiplexer.removeProcessor(stage);
    }
}
