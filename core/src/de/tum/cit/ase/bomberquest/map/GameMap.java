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
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private float physicsTime = 0;

    private final BomberQuestGame game;
    private final World world;
    private final Player player;

    private Entrance entrance;
    private Exit exit;
    private final Chest chest; // if used
    private final Flowers[][] flowers;

    private final List<WallPath> walls;
    private List<Enemy> enemies;
    private final List<PowerUp> powerUps;
    private final List<Bomb> bombs;

    public GameMap(BomberQuestGame game) {
        this.game = game;
        this.world = new World(Vector2.Zero, true);
        this.world.setContactListener(new PlayerContactListener(this));

        this.player = new Player(this.world, 0, 0, this);
        this.enemies = new ArrayList<>();
        this.walls = new ArrayList<>();
        this.chest = null;   // or spawn if needed
        this.flowers = new Flowers[1][4];
        this.powerUps = new ArrayList<>();
        this.bombs = new ArrayList<>();
    }

    public Entrance getEntrance() {
        return entrance;
    }

    public Exit getExit() {
        return exit;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }


    public void tick(float frameTime) {
        // 1) Let player handle input
        player.tick(frameTime);

        // 2) Update physics (player movement, collisions)
        doPhysicsStep(frameTime);

        checkPlayerPowerUpPickup();

        // 3) Update bombs
        List<Bomb> toRemove = new ArrayList<>();
        for (Bomb bomb : bombs) {
            bomb.update(frameTime, this);
            if (bomb.isExploded()) {
                toRemove.add(bomb);
            }
        }
        bombs.removeAll(toRemove);

        // 4) Update enemies
        for (Enemy e : enemies) {
            e.update(frameTime, this);
        }

        // 5) If there's an exit, check if all enemies dead => unlock
        if (exit != null && !exit.isUnlocked() && allEnemiesDead()) {
            exit.unlockExit();
        }
    }

    private void doPhysicsStep(float frameTime) {
        physicsTime += frameTime;
        while (physicsTime >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            physicsTime -= TIME_STEP;
        }
    }

    
    private void checkPlayerPowerUpPickup() {
        // (x,y) of player, truncated or rounded to int
        int px = (int) player.getX();
        int py = (int) player.getY();
    
        // We gather all power-ups that have been picked up
        List<PowerUp> toRemove = new ArrayList<>();
    
        for (PowerUp p : powerUps) {
            int puX = (int) p.getX();
            int puY = (int) p.getY();
    
            // If same tile, pick up
            if (puX == px && puY == py) {
                // Apply effect
                applyPowerUpEffect(p);
                toRemove.add(p);
            }
        }
    
        // Remove them from the map
        powerUps.removeAll(toRemove);
    }
    
    private void applyPowerUpEffect(PowerUp p) {
        switch (p.getType()) {
            case 5:
                // concurrency bomb
                player.increaseBombCapacity();
                break;
            case 6:
                // blast radius
                player.increaseBombRadius();
                break;
        }
        // Possibly play a pickup sound effect, etc.
    }

    public void addBomb(Bomb bomb) {
        this.bombs.add(bomb);
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    private boolean allEnemiesDead() {
        return enemies.isEmpty();
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
        TextureRegion wallTexture = new TextureRegion(Textures.INDEST_WALL);
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
        TextureRegion wallTexture = new TextureRegion(Textures.DEST_WALL);
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
        // 1) Make a destructible wall covering (x, y)
        TextureRegion destructibleRegion = new TextureRegion(
            new Texture("assets/texture/destructablewall.png")
        );
        DestructibleWall coverWall = new DestructibleWall(
            this.world,
            x, y,
            1f, 1f,
            destructibleRegion
        );
        this.walls.add(coverWall);

        // 2) Create an Exit object referencing that wall
        //    The Exit constructor can take the same coordinates plus a reference to the wall
        //    (We pass 'null' for animation or a custom exit texture if you prefer.)
        this.exit = new Exit(x, y, Textures.EXIT, coverWall);
    }

    private void spawnEnemy(int x, int y) {
        // This is a placeholder. You might do real logic like new Enemy(world, x, y, someTexture)
        // For now, let's just store a dummy instance in enemies list.
        TextureRegion concurrencyRegion = new TextureRegion(Textures.ENEMY);
        Enemy e = new Enemy(world, x, y, 3, concurrencyRegion);
        // e.setX(x); e.setY(y); or actual Box2D logic if you prefer
        enemies.add(e);
    }

    private void spawnConcurrentPowerUp(int x, int y) {
        // For concurrency bombs, use a ‘blue’ power-up sprite or something
        TextureRegion concurrencyRegion = new TextureRegion(Textures.POWER_UP_GREEN);
        PowerUp p = new PowerUp(world, x, y, 5, concurrencyRegion);
        powerUps.add(p);
    }
    
    private void spawnBlastRadiusPowerUp(int x, int y) {
        // For blast radius bombs, use a ‘red’ power-up sprite or something else
        TextureRegion radiusRegion = new TextureRegion(Textures.POWER_UP_RED);
        PowerUp p = new PowerUp(world, x, y, 6, radiusRegion);
        powerUps.add(p);
    }
    

    private void ensureExitIfMissing() {
        // Check if you spawned an exit at all.
        // If not, pick a random destructible wall from `walls` to place an exit under it.
    }

    public void gameOver() {
        System.out.println("Game Over triggered. You died.");
        // You can do any logic here: e.g. remove the player’s body, or go to a GameOverScreen.
        // Example:
        game.goToMenu(); 
        // or game.goToGameOverScreen();
    }
}
