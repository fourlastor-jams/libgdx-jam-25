package io.github.fourlastor.ldtk.model;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Null;
import io.github.fourlastor.json.JsonParser;
import java.util.List;
import javax.inject.Inject;

public class LdtkLayerInstance {
    /**
     * Grid-based height
     * SerialName("__cHei")
     */
    public final int cHei;

    /**
     * Grid-based width
     * SerialName("__cWid")
     */
    public final int cWid;

    /**
     * Grid size
     * SerialName("__gridSize")
     */
    public final int gridSize;

    /**
     * Layer definition identifier
     * SerialName("__identifier")
     */
    public final String identifier;

    /**
     * Layer opacity as Float [0-1]
     * SerialName("__opacity")
     */
    public final float opacity;

    /**
     * Total layer X pixel offset, including both instance and definition offsets.
     * SerialName("__pxTotalOffsetX")
     */
    public final int pxTotalOffsetX;

    /**
     * Total layer Y pixel offset, including both instance and definition offsets.
     * SerialName("__pxTotalOffsetY")
     */
    public final int pxTotalOffsetY;

    /**
     * The definition UID of corresponding Tileset, if any.
     * SerialName("__tilesetDefUid")
     */
    @Null
    public final Integer tilesetDefUid;

    /**
     * The relative path to corresponding Tileset, if any.
     * SerialName("__tilesetRelPath")
     */
    @Null
    public final String tilesetRelPath;

    /**
     * Layer type (possible values: IntGrid, Entities, Tiles or AutoLayer)
     * SerialName("__type")
     */
    public final String type;

    /**
     * An array containing all tiles generated by Auto-layer rules. The array is already sorted in
     * display order (i.e. 1st tile is beneath 2nd, which is beneath 3rd etc.).<br/><br/> Note: if
     * multiple tiles are stacked in the same cell as the result of different rules, all tiles
     * behind opaque ones will be discarded.
     */
    public final List<LdtkTileInstance> autoLayerTiles;

    public final List<LdtkEntityInstance> entityInstances;
    public final List<LdtkTileInstance> gridTiles;

    /**
     * A list of all values in the IntGrid layer, stored from left to right, and top to bottom (i.e.
     * first row from left to right, followed by second row, etc.). `0` means "empty cell" and
     * IntGrid values start at 1. This array size is `__cWid` x `__cHei` cells.
     */
    @Null
    public final IntArray intGridCsv;

    /**
     * Reference the Layer definition UID
     */
    public final int layerDefUid;

    /**
     * Reference to the UID of the level containing this layer instance
     */
    public final int levelId;

    /**
     * This layer can use another tileset by overriding the tileset UID here.
     */
    @Null
    public final Integer overrideTilesetUid;

    /**
     * X offset in pixels to render this layer, usually 0 (IMPORTANT: this should be added to the
     * `LayerDef` optional offset, see `__pxTotalOffsetX`)
     */
    public final int pxOffsetX;

    /**
     * Y offset in pixels to render this layer, usually 0 (IMPORTANT: this should be added to the
     * `LayerDef` optional offset, see `__pxTotalOffsetY`)
     */
    public final int pxOffsetY;

    /**
     * Random seed used for Auto-Layers rendering
     */
    public final int seed;

    /**
     * Layer instance visibility
     */
    public final boolean visible;

    /**
     * Unique instance id
     */
    public final String iid;

    public LdtkLayerInstance(
            int cHei,
            int cWid,
            int gridSize,
            String identifier,
            float opacity,
            int pxTotalOffsetX,
            int pxTotalOffsetY,
            @Null Integer tilesetDefUid,
            @Null String tilesetRelPath,
            String type,
            List<LdtkTileInstance> autoLayerTiles,
            List<LdtkEntityInstance> entityInstances,
            List<LdtkTileInstance> gridTiles,
            @Null IntArray intGridCsv,
            int layerDefUid,
            int levelId,
            @Null Integer overrideTilesetUid,
            int pxOffsetX,
            int pxOffsetY,
            int seed,
            boolean visible,
            String iid) {
        this.cHei = cHei;
        this.cWid = cWid;
        this.gridSize = gridSize;
        this.identifier = identifier;
        this.opacity = opacity;
        this.pxTotalOffsetX = pxTotalOffsetX;
        this.pxTotalOffsetY = pxTotalOffsetY;
        this.tilesetDefUid = tilesetDefUid;
        this.tilesetRelPath = tilesetRelPath;
        this.type = type;
        this.autoLayerTiles = autoLayerTiles;
        this.entityInstances = entityInstances;
        this.gridTiles = gridTiles;
        this.intGridCsv = intGridCsv;
        this.layerDefUid = layerDefUid;
        this.levelId = levelId;
        this.overrideTilesetUid = overrideTilesetUid;
        this.pxOffsetX = pxOffsetX;
        this.pxOffsetY = pxOffsetY;
        this.seed = seed;
        this.visible = visible;
        this.iid = iid;
    }

    public static class Parser extends JsonParser<LdtkLayerInstance> {

        private final JsonParser<LdtkTileInstance> tilesParser;
        private final JsonParser<LdtkEntityInstance> entityInstancesTilesParser;

        @Inject
        public Parser(
                JsonParser<LdtkTileInstance> tilesParser, JsonParser<LdtkEntityInstance> entityInstancesTilesParser) {
            this.tilesParser = tilesParser;
            this.entityInstancesTilesParser = entityInstancesTilesParser;
        }

        @Override
        public LdtkLayerInstance parse(JsonValue value) {
            return new LdtkLayerInstance(
                    value.getInt("__cHei"),
                    value.getInt("__cWid"),
                    value.getInt("__gridSize"),
                    value.getString("__identifier"),
                    value.getFloat("__opacity"),
                    value.getInt("__pxTotalOffsetX"),
                    value.getInt("__pxTotalOffsetY"),
                    getOptionalInt(value, "__tilesetDefUid"),
                    value.getString("__tilesetRelPath", null),
                    value.getString("__type"),
                    getList(value.get("autoLayerTiles"), tilesParser::parse),
                    getList(value.get("entityInstances"), entityInstancesTilesParser::parse),
                    getList(value.get("gridTiles"), tilesParser::parse),
                    getOptional(value, "intGridCsv", this::getIntArray),
                    value.getInt("layerDefUid"),
                    value.getInt("levelId"),
                    getOptionalInt(value, "overrideTilesetUid"),
                    value.getInt("pxOffsetX"),
                    value.getInt("pxOffsetY"),
                    value.getInt("seed"),
                    value.getBoolean("visible"),
                    value.getString("iid"));
        }
    }
}
