package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.List;

/**
 * Tile-based map storage. Each (x,y) holds a tile code:
 * 0 => Floor (free)
 * 1 => Indestructible wall
 * 2 => Destructible wall
 * 
 *  We also keep track of:
 *    - the player's spawn (entranceX, entranceY)
 *    - enemies positions
 *    - power-ups positions
 *    - exit position
 */
public class TileMap {

    public static final int FLOOR = 0;
    public static final int WALL_INDESTRUCTIBLE = 1;
    public static final int WALL_DESTRUCTIBLE = 2;

    private final int width;
    private final int height;
    private final int[][] tiles; // tile[y][x]

    // These are read from map file for convenience:
    private int entranceX, entranceY;
    private int exitX, exitY;

    // We’ll store a separate list for enemies, powerUps, etc.
    private final List<EnemySpawn> enemySpawns = new ArrayList<>();
    private final List<PowerUpSpawn> powerUpSpawns = new ArrayList<>();

    /**
     * Creates a tile map with given dimensions (like 40×24).
     */
    public TileMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new int[height][width];
        // default everything to floor
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                tiles[y][x] = FLOOR;
            }
        }
    }

    /**
     * Return true if tile is blocked (either indestructible or destructible).
     */
    public boolean isBlocked(int x, int y) {
        if (x<0 || x>=width || y<0 || y>=height) return true; 
        int t = tiles[y][x];
        return (t == WALL_INDESTRUCTIBLE || t == WALL_DESTRUCTIBLE);
    }

    public void setTile(int x, int y, int type) {
        if (x<0||x>=width||y<0||y>=height) return;
        tiles[y][x] = type;
    }

    public int getTile(int x, int y) {
        if (x<0||x>=width||y<0||y>=height) return WALL_INDESTRUCTIBLE;
        return tiles[y][x];
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public int getEntranceX() { return entranceX; }
    public int getEntranceY() { return entranceY; }
    public int getExitX() { return exitX; }
    public int getExitY() { return exitY; }

    public List<EnemySpawn> getEnemySpawns() { return enemySpawns; }
    public List<PowerUpSpawn> getPowerUpSpawns() { return powerUpSpawns; }

    /**
     * Loads from a properties style file, reading lines like "x,y=type".
     */
    public void loadFromProperties(String path) {
        FileHandle handle = Gdx.files.internal(path);
        String content = handle.readString();
        String[] lines = content.split("\\r?\\n");

        for (String line : lines) {
            line=line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] kv = line.split("=");
            if (kv.length<2) continue;

            String coords = kv[0].trim(); 
            String val = kv[1].trim();
            String[] xy = coords.split(",");
            if (xy.length<2) continue;

            int x = Integer.parseInt(xy[0]);
            int y = Integer.parseInt(xy[1]);
            int type = Integer.parseInt(val);

            switch(type) {
                case 0: // Indestructible
                    setTile(x,y, WALL_INDESTRUCTIBLE);
                    break;
                case 1: // Destructible
                    setTile(x,y, WALL_DESTRUCTIBLE);
                    break;
                case 2: // Entrance
                    entranceX = x; entranceY = y;
                    break;
                case 3: // Enemy
                    enemySpawns.add(new EnemySpawn(x,y));
                    break;
                case 4: // Exit
                    exitX=x; exitY=y;
                    // We can also place a destructible wall here if you want
                    break;
                case 5: // concurrency power-up
                    powerUpSpawns.add(new PowerUpSpawn(x,y,5));
                    break;
                case 6: // blast radius power-up
                    powerUpSpawns.add(new PowerUpSpawn(x,y,6));
                    break;
                // etc. 
                default:
                    // unknown
                    break;
            }
        }
    }

    /**
     * Destroy a destructible wall at (x, y), turning it into floor.
     */
    public void destroyWall(int x, int y) {
        if (getTile(x,y) == WALL_DESTRUCTIBLE) {
            setTile(x,y, FLOOR);
        }
    }

    // Small record for storing enemy spawns
    public static class EnemySpawn {
        public final int x, y;
        public EnemySpawn(int x, int y) { this.x=x; this.y=y; }
    }

    public static class PowerUpSpawn {
        public final int x, y;
        public final int type; // 5 => concurrency, 6 => blast radius
        public PowerUpSpawn(int x,int y,int type){
            this.x=x; this.y=y; this.type=type;
        }
    }
}
