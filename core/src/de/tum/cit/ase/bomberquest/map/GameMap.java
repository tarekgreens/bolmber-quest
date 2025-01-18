package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.texture.Textures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the game map.
 * Holds all the objects and entities in the game.
 */
public class GameMap {

    // A static block is executed once when the class is referenced for the first time.
    static {
        // Initialize the Box2D physics engine.
        com.badlogic.gdx.physics.box2d.Box2D.init();
    }

    // Box2D physics simulation parameters (you can experiment with these if you want, but they work well as they are)
    /**
     * The time step for the physics simulation.
     * This is the amount of time that the physics simulation advances by in each frame.
     * It is set to 1/refreshRate, where refreshRate is the refresh rate of the monitor, e.g., 1/60 for 60 Hz.
     */
    private static final float TIME_STEP = 1f / Gdx.graphics.getDisplayMode().refreshRate;
    /** The number of velocity iterations for the physics simulation. */
    private static final int VELOCITY_ITERATIONS = 6;
    /** The number of position iterations for the physics simulation. */
    private static final int POSITION_ITERATIONS = 2;
    /**
     * The accumulated time since the last physics step.
     * We use this to keep the physics simulation at a constant rate even if the frame rate is variable.
     */
    private float physicsTime = 0;

    /** The game, in case the map needs to access it. */
    private final BomberQuestGame game;
    /** The Box2D world for physics simulation. */
    private final World world;

    // Game objects
    private final Player player;
    private Entrance entrance;
    private final Chest chest;

    private final Flowers[][] flowers;

    private final List<WallPath> walls;
    private Texture indestructibleWallTexture;
    private Texture destructibleWallTexture;

    public GameMap(BomberQuestGame game) {
        this.game = game;
        this.world = new World(Vector2.Zero, true);
    
        // Create the player (so we have it ready for when the entrance is parsed).
        this.player = new Player(this.world, 0, 0);
        
        // Initialize empty data structures:
        this.walls = new ArrayList<>();
        this.chest= null;
        this.entrance = null;
        this.flowers = new Flowers[1][4];

        indestructibleWallTexture = new Texture("assets/texture/ind.png");
        destructibleWallTexture = new Texture("assets/texture/destructablewall.png");
    }

    /**
     * Updates the game state. This is called once per frame.
     * Every dynamic object in the game should update its state here.
     * @param frameTime the time that has passed since the last update
     */
    public void tick(float frameTime) {
        this.player.tick(frameTime);
        doPhysicsStep(frameTime);

    }


    /**
     * Performs as many physics steps as necessary to catch up to the given frame time.
     * This will update the Box2D world by the given time step.
     * @param frameTime Time since last frame in seconds
     */
    private void doPhysicsStep(float frameTime) {
        this.physicsTime += frameTime;
        while (this.physicsTime >= TIME_STEP) {
            this.world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            this.physicsTime -= TIME_STEP;
        }

    }

    /** Returns the player on the map. */
    public Player getPlayer() {
        return player;
    }

    /** Returns the chest on the map. */
    public Chest getChest() {
        return chest;
    }

    /** Returns the flowers on the map. */
    public List<Flowers> getFlowers() {
        return Arrays.stream(flowers).flatMap(Arrays::stream).toList();
    }
    public List<WallPath> getWalls() {
        return walls;
    }

    public void loadFromProperties(String relativePath) {
        String fileContent = Gdx.files.internal(relativePath).readString();
        String[] lines = fileContent.split("\\r?\\n");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            String[] parts = line.split("=");
            if (parts.length < 2) {
                continue;
            }
            String key = parts[0].trim();
            String val = parts[1].trim();

            // 5. Split key into coordinates x,y
            String[] coords = key.split(",");
            if (coords.length < 2) {
                continue; // malformed coordinates
            }
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            
            // 6. Parse the object type
            int type = Integer.parseInt(val);
    
            // 7. Create objects in the Box2D world
            switch (type) {
                case 0:
                    // 0 = Indestructible wall
                    spawnIndestructibleWall(x, y);
                    break;
                case 1:
                    // 1 = Destructible wall
                    spawnDestructibleWall(x, y);
                    break;
                case 2:
                    // 2 = Entrance
                    spawnEntrance(x, y);
                    break;
                case 3:
                    // 3 = Enemy
                    spawnEnemy(x, y);
                    break;
                case 4:
                    // 4 = Exit (with destructible wall on top)
                    spawnExit(x, y);
                    break;
                case 5:
                    // 5 = Concurrent bomb power-up (also under destructible wall)
                    spawnConcurrentPowerUp(x, y);
                    break;
                case 6:
                    // 6 = Blast radius power-up (also under destructible wall)
                    spawnBlastRadiusPowerUp(x, y);
                    break;
                default:
                    System.out.println("Unknown map object type: " + type);
                    break;
            }
        }
    
        // 8. If no exit was found, pick a random destructible wall to hide an exit
        //    (only if you want to fulfill the rule that a map might not specify exit).
        ensureExitIfMissing();
    }

        private void spawnIndestructibleWall(int x, int y) {
        TextureRegion wallTexture = new TextureRegion(new Texture("assets/texture/ind.png"));
        float wallWidth = 1f;
        float wallHeight = 1f;
        IndestructibleWall wall = new IndestructibleWall(
            this.world, x, y,
            wallWidth, wallHeight,
            wallTexture
        );
        this.walls.add(wall);
    }

    private void spawnDestructibleWall(int x, int y) {
        TextureRegion wallTexture = new TextureRegion(new Texture("assets/texture/destructablewall.png"));
        float wallWidth = 1f;
        float wallHeight = 1f;
        DestructibleWall wall = new DestructibleWall(
            this.world, x, y,
            wallWidth, wallHeight,
            wallTexture
        );
        this.walls.add(wall);
    }

    private void spawnEntrance(int x, int y) {
        // This sets the player's spawn location or store it separately
        // If you want only 1 entrance, you could do:
        this.entrance = new Entrance(x, y, Textures.ENTRANCE);
        // Possibly position the player here
        this.player.getHitbox().setTransform(x, y, 0);
    }

    private void spawnExit(int x, int y) {
        // Typically you hide the exit behind a destructible wall. 
        // One approach is to spawn a destructible wall at (x,y) AND keep track 
        // that the exit is "under" that wall. You might store it in a dedicated list or object.
    }

    private void spawnEnemy(int x, int y) {
        // Create an Enemy body in the Box2D world at (x, y)
        // Add it to a list of enemies
    }

    private void spawnConcurrentPowerUp(int x, int y) {
        // Usually you also spawn a destructible wall on top. 
        // Keep track of the power-up so that once the wall is destroyed, 
        // the power-up can be revealed.
    }

    private void spawnBlastRadiusPowerUp(int x, int y) {
        // Same concept as concurrency power-up, but different type.
    }

    private void ensureExitIfMissing() {
        // Check if you spawned an exit at all.
        // If not, pick a random destructible wall from `walls` to place an exit under it.
    }
}
