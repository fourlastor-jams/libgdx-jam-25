package io.github.fourlastor.game.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.fourlastor.game.ui.Pawn;
import io.github.fourlastor.game.ui.YSort;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import squidpony.squidmath.GWTRNG;

public class LevelScreen extends ScreenAdapter {

    private final InputMultiplexer inputMultiplexer;

    private final Stage stage;
    private final TextureAtlas atlas;
    private final GWTRNG rng;

    private final GameState state;
    private final TextButton.TextButtonStyle buttonStyle;

    @Inject
    public LevelScreen(
            InputMultiplexer inputMultiplexer, Stage stage, TextureAtlas atlas, GWTRNG rng, AssetManager assetManager) {
        this.inputMultiplexer = inputMultiplexer;
        this.stage = stage;
        this.atlas = atlas;
        this.rng = rng;
        BitmapFont font = assetManager.get("fonts/quan-pixel-32.fnt");
        buttonStyle = new TextButton.TextButtonStyle(null, null, null, font);
        Image image = new Image(atlas.findRegion("main_art"));
        stage.addActor(image);
        YSort ySort = new YSort();
        stage.addActor(ySort);

        Drawable p1Drawable = new TextureRegionDrawable(atlas.findRegion("pawns/starfish"));
        Drawable p2Drawable = new TextureRegionDrawable(atlas.findRegion("pawns/clam"));

        List<Vector2> p1Pos = Arrays.asList(
                new Vector2(339, 300 - 180),
                new Vector2(351, 300 - 196),
                new Vector2(381, 300 - 177),
                new Vector2(412, 300 - 196),
                new Vector2(433, 300 - 184),
                new Vector2(452, 300 - 200),
                new Vector2(456, 300 - 221));
        List<Vector2> p2Pos = Arrays.asList(
                new Vector2(110, 120),
                new Vector2(112, 90),
                new Vector2(135, 100),
                new Vector2(136, 300 - 228),
                new Vector2(157, 300 - 214),
                new Vector2(165, 300 - 234),
                new Vector2(188, 300 - 224));

        List<Pawn> p1Pawns = new ArrayList<>(7);
        List<Pawn> p2Pawns = new ArrayList<>(7);

        for (Vector2 pos : p1Pos) {
            Pawn actor = new Pawn(p1Drawable, pos);
            p1Pawns.add(actor);
            ySort.addActor(actor);
        }

        for (Vector2 pos : p2Pos) {
            Pawn actor = new Pawn(p2Drawable, pos);
            p2Pawns.add(actor);
            ySort.addActor(actor);
        }
        this.state = new GameState(p1Pawns, p2Pawns);

        Player firstPlayer = rng.getRandomElement(Player.values());

        presentRoll(firstPlayer);
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

        List<Runnable> cleanups = new LinkedList<>();

        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            if (move instanceof Move.MoveFromBoard) {
                Move.MoveFromBoard moveFromBoard = (Move.MoveFromBoard) move;
                Pawn pawn = state.pawnAt(player, moveFromBoard.origin);
                cleanups.add(highlightPawn(player, cleanups, move, pawn));
            } else {
                for (Pawn pawn : state.availablePawns(player)) {
                    cleanups.add(highlightPawn(player, cleanups, move, pawn));
                }
            }
        }
    }

    private Runnable highlightPawn(Player player, List<Runnable> cleanups, Move move, Pawn pawn) {
        Action blinking =
                Actions.forever(Actions.sequence(Actions.color(Color.BLACK, 0.5f), Actions.color(Color.WHITE, 0.5f)));
        pawn.addAction(blinking);
        Vector2 pawnPosition = move.destination == GameState.LAST_POSITION
                ? null
                : Positions.toWorldAtCenter(player, move.destination);
        Image highlight;
        if (pawnPosition != null) {
            highlight = new Image(atlas.findRegion("effects/highlight-" + player.color));
            highlight.setTouchable(Touchable.disabled);
            highlight.setPosition(pawnPosition.x, pawnPosition.y, Align.center);
            highlight.setVisible(false);
            stage.addActor(highlight);
        } else {
            highlight = null;
        }
        InputListener hoverListener = new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, @Null Actor fromActor) {
                if (highlight != null) {
                    highlight.setVisible(true);
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, @Null Actor toActor) {
                if (highlight != null) {
                    highlight.setVisible(false);
                }
            }
        };
        pawn.addListener(hoverListener);
        ClickListener clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onMovePicked(cleanups, move, pawn);
            }
        };
        pawn.addListener(clickListener);
        return () -> {
            pawn.removeListener(clickListener);
            pawn.removeListener(hoverListener);
            pawn.removeAction(blinking);
            pawn.setColor(Color.WHITE);
            if (highlight != null) {
                highlight.remove();
            }
        };
    }

    private void onMovePicked(List<Runnable> cleanups, Move move, Pawn pawn) {
        for (Runnable cleanup : cleanups) {
            cleanup.run();
        }
        stage.addAction(Actions.sequence(move.play(state, pawn), Actions.run(() -> presentRoll(move.next()))));
        Gdx.app.debug("Round", "Playing move: " + move);
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
