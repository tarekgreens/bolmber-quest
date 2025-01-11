package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.BomberQuestGame;

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

    private final Chest chest;

    private final Flowers[][] flowers;

    private final List<WallPath> walls;
    private Texture indestructibleWallTexture;
    private Texture destructibleWallTexture;

    public GameMap(BomberQuestGame game) {
        this.game = game;
        this.world = new World(Vector2.Zero, true);
        // Create a player with initial position (1, 3)
        this.player = new Player(this.world, 1, 3);
        // Create a chest in the middle of the map
        this.chest = new Chest(world, 3, 3);
        // Create flowers in a 7x7 grid
        this.flowers = new Flowers[7][7];
        for (int i = 0; i < flowers.length; i++) {
            for (int j = 0; j < flowers[i].length; j++) {
                this.flowers[i][j] = new Flowers(i, j);
            }
        }
        // Initialize the walls list
        this.walls = new ArrayList<>();

        indestructibleWallTexture = new Texture("assets/texture/ind.png");
        destructibleWallTexture = new Texture("assets/texture/destructablewall.png");

        // Example of adding walls
        addWalls(indestructibleWallTexture,destructibleWallTexture);


    }


    private void addWalls(Texture indestructibleWallTexture, Texture destructibleWallTexture) {
        // Create indestructible walls
        TextureRegion indestructibleWallRegion = new TextureRegion(indestructibleWallTexture);

        // Add indestructible walls around the border
        int mapWidth = flowers.length;  // Assuming flowers array represents the map size
        int mapHeight = flowers[0].length;

        for (int x = 0; x < mapWidth; x++) {
            // Top and bottom rows
            walls.add(new IndestructibleWall(x, 0, 1, 1, indestructibleWallRegion));
            walls.add(new IndestructibleWall(x, mapHeight - 1, 1, 1, indestructibleWallRegion));
        }

        for (int y = 0; y < mapHeight; y++) {
            // Left and right columns
            walls.add(new IndestructibleWall(0, y, 1, 1, indestructibleWallRegion));
            walls.add(new IndestructibleWall(mapWidth - 1, y, 1, 1, indestructibleWallRegion));
        }

        // Create destructible walls
        TextureRegion destructibleWallRegion = new TextureRegion(destructibleWallTexture);

        // Example of adding destructible walls in the middle of the map
        WallPath destructibleWall1 = new DestructibleWall(2, 3, 1, 1, destructibleWallRegion);
        walls.add(destructibleWall1);

        WallPath destructibleWall2 = new DestructibleWall(4, 3, 1, 1, destructibleWallRegion);
        walls.add(destructibleWall2);
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


}
