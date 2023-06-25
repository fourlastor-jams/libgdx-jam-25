package io.github.fourlastor.game.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.TypingLabel;
import io.github.fourlastor.game.ui.ParticleEmitter;
import io.github.fourlastor.game.ui.Pawn;
import io.github.fourlastor.game.ui.YSort;
import io.github.fourlastor.harlequin.animation.Animation;
import io.github.fourlastor.harlequin.animation.FixedFrameAnimation;
import io.github.fourlastor.perceptual.Perceptual;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import squidpony.squidmath.GWTRNG;

public class LevelScreen extends ScreenAdapter {

    private static final float FADE_OUT_DURATION = 0.6f;
    private static final float LONG_FADE_OUT_DURATION = FADE_OUT_DURATION * 1.5f;
    private static final String TAG = "Round";
    private final InputMultiplexer inputMultiplexer;

    private final Stage stage;
    private final TextureAtlas atlas;
    private final GWTRNG rng;

    private final GameState state;

    private final TextureRegion p1name;
    private final TextureRegion p2name;
    private final Image playerName;
    private final TypingLabel instructions;
    private final AssetManager assetManager;
    private final DiceTextures textures;
    private final ShaderProgram underwaterShader;
    private final Music music;
    private final Sound bubbleSound;
    private float time = 0f;

    @Inject
    public LevelScreen(
            InputMultiplexer inputMultiplexer,
            Stage stage,
            TextureAtlas atlas,
            GWTRNG rng,
            AssetManager assetManager,
            DiceTextures textures,
            Music music,
            Sound bubbleSound) {
        this.inputMultiplexer = inputMultiplexer;
        this.stage = stage;
        this.atlas = atlas;
        this.rng = rng;
        this.assetManager = assetManager;
        this.textures = textures;
        this.underwaterShader = assetManager.get("shaders/underwater.fs");
        this.music = music;
        this.bubbleSound = bubbleSound;
        music.setVolume(Perceptual.perceptualToAmplitude(0.5f));
        music.setLooping(true);
        BitmapFont font = assetManager.get("fonts/play-24.fnt");
        Image image = new Image(atlas.findRegion("main_art"));
        stage.addActor(image);
        YSort ySort = new YSort();
        stage.addActor(ySort);
        instructions = new TypingLabel("", new Font(font));
        instructions.setPosition(10, 245);
        instructions.setVisible(false);
        stage.addActor(instructions);
        p1name = atlas.findRegion("text/p1");
        p2name = atlas.findRegion("text/p2");
        playerName = new Image(p1name);
        playerName.setPosition(stage.getWidth() / 2, stage.getHeight() - 20, Align.center);
        stage.addActor(playerName);
        List<Animation<Drawable>> p1Drawables = Arrays.asList(
                createAnimation(atlas.findRegions("pawns/clam/idle 1/idle")),
                createAnimation(atlas.findRegions("pawns/clam/idle 2/idle")));
        List<Animation<Drawable>> p2Drawables = Arrays.asList(
                createAnimation(atlas.findRegions("pawns/starfish/idle 1/idle")),
                createAnimation(atlas.findRegions("pawns/starfish/idle 2/idle")));

        List<Vector2> p1Pos = Arrays.asList(
                new Vector2(110, 120),
                new Vector2(112, 90),
                new Vector2(135, 100),
                new Vector2(136, 300 - 228),
                new Vector2(157, 300 - 214),
                new Vector2(165, 300 - 234),
                new Vector2(188, 300 - 224));
        List<Vector2> p2Pos = Arrays.asList(
                new Vector2(339, 300 - 180),
                new Vector2(351, 300 - 196),
                new Vector2(381, 300 - 177),
                new Vector2(412, 300 - 196),
                new Vector2(433, 300 - 184),
                new Vector2(452, 300 - 200),
                new Vector2(456, 300 - 221));

        List<Pawn> p1Pawns = new ArrayList<>(7);
        List<Pawn> p2Pawns = new ArrayList<>(7);

        for (Vector2 pos : p1Pos) {
            Pawn actor = new Pawn(rng.getRandomElement(p1Drawables), pos);
            actor.setProgress(rng.nextFloat(3));
            p1Pawns.add(actor);
            ySort.addActor(actor);
        }

        for (Vector2 pos : p2Pos) {
            Pawn actor = new Pawn(rng.getRandomElement(p2Drawables), pos);
            actor.setProgress(rng.nextFloat(3));
            p2Pawns.add(actor);
            ySort.addActor(actor);
        }
        this.state = new GameState(p1Pawns, p2Pawns);

        Player firstPlayer = rng.getRandomElement(Player.values());

        presentRoll(firstPlayer);
    }

    private Animation<Drawable> createAnimation(Array<TextureAtlas.AtlasRegion> regions) {
        Array<Drawable> frames = new Array<>(regions.size);
        for (TextureAtlas.AtlasRegion region : regions) {
            frames.add(new TextureRegionDrawable(region));
        }
        return new FixedFrameAnimation<>(0.25f, frames, Animation.PlayMode.LOOP);
    }

    private void updateInstructions(Player player, String instruction) {
        instructions.setText("Player " + player + ":\n[%25]" + instruction);
        instructions.restart();
    }

    private void presentRoll(Player player) {
        Gdx.app.debug(TAG, "Starting round for " + player);
        Image rollButton = new Image(atlas.findRegion("text/p" + (player.pnum()) + "-throw-dice"));
        playerName.setDrawable(new TextureRegionDrawable(player == Player.ONE ? p1name : p2name));
        updateInstructions(player, "Roll the dice");

        if (player == Player.ONE) {
            rollButton.setPosition(10, stage.getHeight() - 60);
        } else {
            rollButton.setPosition(stage.getWidth() - 10, stage.getHeight() - 60, Align.right);
        }
        RepeatAction highlight =
                Actions.forever(Actions.sequence(Actions.color(Color.BLACK, 0.5f), Actions.color(Color.WHITE, 0.5f)));
        rollButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 1. roll 4 1d2 (values 0, 1)
                int rollAmount = 0;
                Array<Drawable> dices = new Array<>();
                for (int i = 0; i < 4; i++) {
                    int rolled = rng.nextInt(2);
                    if (rolled == 0) {
                        dices.add(textures.d0());
                    } else {
                        dices.add(textures.d1());
                    }
                    rollAmount += rolled;
                }
                pickMove(player, 4, dices);
                rollButton.remove();
                rollButton.removeAction(highlight);
                rollButton.setColor(Color.WHITE);
            }
        });
        rollButton.addAction(highlight);
        stage.addActor(rollButton);
    }

    private void pickMove(Player player, int rollAmount, Array<Drawable> dices) {
        Gdx.app.debug(TAG, "Player rolled " + rollAmount);
        updateInstructions(player, "Pick a pawn to move " + rollAmount + " spaces");

        Image d0 = new Image(dices.get(0));
        Image d1 = new Image(dices.get(1));
        Image d2 = new Image(dices.get(2));
        Image d3 = new Image(dices.get(3));
        Image rollText = new Image(atlas.findRegion("text/p" + (player.pnum()) + "-num-" + rollAmount));
        int multiplier;
        int sign;
        if (player == Player.ONE) {
            multiplier = 1;
            sign = -1;
        } else {
            multiplier = 2;
            sign = 1;
        }

        d0.setPosition(multiplier * stage.getWidth() / 3 + sign * 150, stage.getHeight() - 80, Align.center);
        d1.setPosition(multiplier * stage.getWidth() / 3 + sign * 110, stage.getHeight() - 80, Align.center);
        rollText.setPosition(multiplier * stage.getWidth() / 3 + sign * 90, stage.getHeight() - 110, Align.center);
        d2.setPosition(multiplier * stage.getWidth() / 3 + sign * 70, stage.getHeight() - 80, Align.center);
        d3.setPosition(multiplier * stage.getWidth() / 3 + sign * 30, stage.getHeight() - 80, Align.center);
        stage.addActor(d0);
        stage.addActor(d1);
        stage.addActor(d2);
        stage.addActor(d3);
        stage.addActor(rollText);

        List<Runnable> cleanups = new LinkedList<>();
        cleanups.add(d0::remove);
        cleanups.add(d1::remove);
        cleanups.add(d2::remove);
        cleanups.add(d3::remove);
        cleanups.add(rollText::remove);

        if (rollAmount <= 0) {
            Gdx.app.debug(TAG, "Player rolled a zero");
            stage.addAction(skipRound(player, cleanups));
            return;
        }

        List<Move> moves = state.getAvailableMoves(player, rollAmount);

        if (moves.isEmpty()) {
            Gdx.app.debug(TAG, "No available moves: " + player);
            stage.addAction(skipRound(player, cleanups));
            return;
        }

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

    private SequenceAction skipRound(Player player, List<Runnable> cleanups) {
        return Actions.sequence(Actions.delay(2), Actions.run(() -> {
            for (Runnable cleanup : cleanups) {
                cleanup.run();
            }
            presentRoll(next(player));
        }));
    }

    private Runnable highlightPawn(Player player, List<Runnable> cleanups, Move move, Pawn pawn) {
        Action blinking =
                Actions.forever(Actions.sequence(Actions.color(Color.BLACK, 0.5f), Actions.color(Color.WHITE, 0.5f)));
        pawn.addAction(blinking);
        Vector2 pawnPosition = move.destination == Positions.LAST_POSITION
                ? null
                : Positions.toWorldAtCenter(player, move.destination);
        Image highlight;
        if (pawnPosition != null) {
            highlight = new Image(atlas.findRegion("effects/highlight-" + player.color));
            highlight.setTouchable(Touchable.disabled);
            highlight.setPosition(pawnPosition.x, pawnPosition.y + 2, Align.center);
            highlight.setVisible(false);
            highlight.addAction(
                    Actions.forever(Actions.sequence(Actions.moveBy(0, 5, 0.5f), Actions.moveBy(0, -5, 0.5f))));
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
                onMovePicked(player, cleanups, move, pawn);
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

    private void onMovePicked(Player player, List<Runnable> cleanups, Move move, Pawn pawn) {
        for (Runnable cleanup : cleanups) {
            cleanup.run();
        }
        boolean pawnCaptured = move.destination >= 4 && move.destination <= 11 && state.isPawnAtPosition(next(player), move.destination);
        Action bubbles = pawnCaptured || move.isLastStep() ? Actions.run(() -> showParticles(Positions.toWorldAtCenter(player, move.destination))) : Actions.run(() -> {});
        stage.addAction(Actions.sequence(move.play(state, pawn, bubbles), Actions.run(() -> {
            if (state.hasPlayerWon(player)) {
                displayWinner(player);
                showParticles(new Vector2(stage.getWidth() * 0.15f, 0), 1f);
                showParticles(new Vector2(stage.getWidth() * 0.35f, 0), 1f);
                showParticles(new Vector2(stage.getWidth() * 0.55f, 0), 1f);
                showParticles(new Vector2(stage.getWidth() * 0.75f, 0), 1f);
            } else {
                Player next = move.next();
                float fadeOutDuration = FADE_OUT_DURATION;
                if (move.destination == Positions.BATTLE_ROSETTE_POSITION) {
                    displayProtected(player, move.destination);
                    fadeOutDuration = LONG_FADE_OUT_DURATION;
                }
                if (next == player) {
                    displayRollAgain(player, move.destination, fadeOutDuration);
                }
                presentRoll(next);
            }
        })));
        Gdx.app.debug(TAG, "Playing move: " + move);
    }

    private void displayRollAgain(Player player, int destination, float fadeOutDuration) {
        Vector2 position = Positions.toWorldAtCenter(player, destination);
        Image image = new Image(atlas.findRegion("text/roll-again"));
        image.setPosition(position.x, position.y + 30, Align.center);
        image.addAction(Actions.sequence(
                Actions.parallel(Actions.fadeOut(fadeOutDuration), Actions.moveBy(0f, 20f, fadeOutDuration)),
                Actions.run(image::remove)));
        stage.addActor(image);
        showParticles(position);
    }

    private void showParticles(Vector2 position, float scale) {
        ParticleEmitter particles = new ParticleEmitter(new ParticleEffect(assetManager.get("effects/bubbles.pfx")), scale);
        particles.setPosition(position.x, position.y, Align.center);
        stage.addActor(particles);
        bubbleSound.play(Perceptual.amplitudeToPerceptual(0.5f));
    }

    private void showParticles(Vector2 position) {
        showParticles(position, 0.5f);
    }

    private void displayProtected(Player player, int destination) {
        Vector2 position = Positions.toWorldAtCenter(player, destination);
        Image image = new Image(atlas.findRegion("text/safe"));
        image.setPosition(position.x, position.y - 30, Align.center);
        image.addAction(Actions.sequence(
                Actions.parallel(
                        Actions.fadeOut(LONG_FADE_OUT_DURATION), Actions.moveBy(0f, -20f, LONG_FADE_OUT_DURATION)),
                Actions.run(image::remove)));
        stage.addActor(image);
    }

    private void displayWinner(Player player) {
        Image actor = new Image(atlas.findRegion("text/p" + player.pnum() + "-won"));
        actor.setPosition(stage.getWidth() / 2f, stage.getHeight() / 2f, Align.center);
        playerName.setVisible(false);
        stage.addActor(actor);
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            if (music.isPlaying()) {
                music.pause();
            } else {
                music.play();
            }
            Gdx.app.log(TAG, "Music playing: " + music.isPlaying());
        }
        underwaterShader.bind();
        underwaterShader.setUniformf("u_time", time);
        time += delta;
        stage.getViewport().apply();
        stage.act();
        stage.draw();
    }

    @Override
    public void show() {
        inputMultiplexer.addProcessor(stage);
        stage.getBatch().setShader(underwaterShader);
        music.play();
    }

    @Override
    public void hide() {
        music.stop();
        stage.getBatch().setShader(null);
        time = 0f;
        inputMultiplexer.removeProcessor(stage);
    }
}
