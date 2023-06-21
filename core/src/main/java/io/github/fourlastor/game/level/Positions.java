package io.github.fourlastor.game.level;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Positions {

    private static final int TILE_WIDTH = 64;
    private static final float HALF_WIDTH = TILE_WIDTH / 2f;
    private static final int TILE_HEIGHT = 32;
    private static final float HALF_HEIGHT = TILE_HEIGHT / 2f;
    private static final int TILE_THICKNESS = 16;

    private static final int ORIGIN_BOTTOM = 3;
    private static final int ORIGIN_LEFT = 230;
    private static final List<GridPoint2> P1_INITIAL_POSITIONS =
            Arrays.asList(new GridPoint2(2, 4), new GridPoint2(2, 5), new GridPoint2(2, 6), new GridPoint2(2, 7));
    private static final List<GridPoint2> P2_INITIAL_POSITIONS =
            Arrays.asList(new GridPoint2(0, 4), new GridPoint2(0, 5), new GridPoint2(0, 6), new GridPoint2(0, 7));
    private static final List<GridPoint2> SHARED_POSITIONS = Arrays.asList(
            new GridPoint2(1, 7),
            new GridPoint2(1, 6),
            new GridPoint2(1, 5),
            new GridPoint2(1, 4),
            new GridPoint2(1, 3),
            new GridPoint2(1, 2),
            new GridPoint2(1, 1),
            new GridPoint2(1, 0));
    private static final List<GridPoint2> P1_FINAL_POSITIONS =
            Arrays.asList(new GridPoint2(2, 0), new GridPoint2(2, 1));
    private static final List<GridPoint2> P2_FINAL_POSITIONS =
            Arrays.asList(new GridPoint2(0, 0), new GridPoint2(0, 1));

    public static final List<GridPoint2> P1_POSITIONS = Stream.of(
                    P1_INITIAL_POSITIONS, SHARED_POSITIONS, P1_FINAL_POSITIONS)
            .flatMap(List::stream)
            .collect(Collectors.toList());

    public static final List<GridPoint2> P2_POSITIONS = Stream.of(
                    P2_INITIAL_POSITIONS, SHARED_POSITIONS, P2_FINAL_POSITIONS)
            .flatMap(List::stream)
            .collect(Collectors.toList());

    public boolean isMoveOutOfBounds(Player player, int position) {
        return positions(player).size() >= position;
    }

    public static GridPoint2 positionFor(Player player, int position) {
        return positions(player).get(position);
    }

    public static Vector2 toWorldAtOrigin(Player player, int position, Vector2 result) {
        GridPoint2 map = positionFor(player, position);
        return result.set(ORIGIN_LEFT, ORIGIN_BOTTOM).add((map.x - map.y) * HALF_WIDTH, (map.x + map.y) * HALF_HEIGHT);
    }

    public static Vector2 toWorldAtCenter(Player player, int position, Vector2 result) {
        return toWorldAtOrigin(player, position, result).add(HALF_WIDTH, TILE_THICKNESS + HALF_HEIGHT);
    }

    private static List<GridPoint2> positions(Player player) {
        return player == Player.ONE ? P1_POSITIONS : P2_POSITIONS;
    }
}
