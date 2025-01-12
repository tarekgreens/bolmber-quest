package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.texture.Textures;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents the game map.
 * Holds all the objects and entities in the game.
 */
public class GameMap {

    private final BomberQuestGame game;
    private final World world;
    private final Player player;
    private final List<WallPath> walls;
    private Entrance entrance;
    private Exit exit;
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();

    public GameMap(BomberQuestGame game) {
        this.game = game;
        this.world = new World(Vector2.Zero, true);
        this.player = new Player(this.world, 1, 3);
        this.walls = new ArrayList<>();
    }

    /**
     * Parses a .properties map file and populates the game map with objects.
     *
     * @param fileContent The content of the map file.
     * @param game        The BomberQuestGame instance.
     * @return A fully initialized GameMap object.
     */
    public static GameMap parseMap(String fileContent, BomberQuestGame game) {
        GameMap map = new GameMap(game);
        String[] lines = fileContent.split("\n");
        Random random = new Random();
        boolean exitPlaced = false;

        TextureRegion indestructibleWallRegion = new TextureRegion(new Texture("assets/texture/ind.png"));
        TextureRegion destructibleWallRegion = new TextureRegion(new Texture("assets/texture/destructablewall.png"));
        TextureRegion entranceRegion = new TextureRegion(new Texture("assets/texture/entrance.png"));
        TextureRegion exitRegion = new TextureRegion(new Texture("assets/texture/exit.png"));

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            String[] parts = line.split("=");
            if (parts.length != 2) continue;

            String[] coords = parts[0].split(",");
            int x = Integer.parseInt(coords[0].trim());
            int y = Integer.parseInt(coords[1].trim());
            int type = Integer.parseInt(parts[1].trim());

            switch (type) {
                case 0 -> map.addWall(new IndestructibleWall(map.world, x, y, 1, 1, indestructibleWallRegion));
                case 1 -> map.addWall(new DestructibleWall(map.world, x, y, 1, 1, destructibleWallRegion));
                case 2 -> map.setEntrance(new Entrance(x, y, entranceRegion));

                case 3 -> map.addEnemy(new Enemy(map.world, x, y, new TextureRegion(new Texture("assets/texture/enemy.png"))));
                case 4 -> {
                    map.setExit(new Exit(map.world, x, y, exitRegion));
                    exitPlaced = true;
                }
                case 5, 6 -> {

                    map.addPowerUp(new PowerUp(map.world, x, y, type, new TextureRegion(new Texture("assets/texture/powerup.png"))));
                    map.addWall(new DestructibleWall(map.world, x, y, 1, 1, destructibleWallRegion));
                }
            }
        }

        if (!exitPlaced) {
            map.placeRandomExit(destructibleWallRegion, exitRegion);
        }

        return map;
    }

    /**
     * Adds a wall to the map.
     *
     * @param wall The wall to add.
     */
    public void addWall(WallPath wall) {
        walls.add(wall);
    }

    /**
     * Sets the entrance point of the map.
     *
     * @param entrance The entrance object.
     */
    public void setEntrance(Entrance entrance) {
        this.entrance = entrance;
    }

    /**
     * Sets the exit point of the map.
     *
     * @param exit The exit object.
     */
    public void setExit(Exit exit) {
        this.exit = exit;
    }

    /**
     * Places a random exit under a destructible wall if none is specified.
     *
     * @param destructibleWallRegion Texture region for destructible walls.
     * @param exitRegion             Texture region for the exit.
     */
    private void placeRandomExit(TextureRegion destructibleWallRegion, TextureRegion exitRegion) {
        Random random = new Random();
        List<DestructibleWall> destructibleWalls = walls.stream()
                .filter(w -> w instanceof DestructibleWall)
                .map(w -> (DestructibleWall) w)
                .toList();

        if (destructibleWalls.isEmpty()) {
            throw new IllegalStateException("No destructible walls available for placing an exit.");
        }

        DestructibleWall randomWall = destructibleWalls.get(random.nextInt(destructibleWalls.size()));
        walls.remove(randomWall); // Remove the destructible wall
        this.setExit(new Exit(world, randomWall.getX(), randomWall.getY(), exitRegion));
    }

    public Player getPlayer() {
        return player;
    }

    public List<WallPath> getWalls() {
        return walls;
    }

    public Entrance getEntrance() {
        return entrance;
    }

    public Exit getExit() {
        return exit;
    }

    /**
     * Updates the game state.
     *
     * @param frameTime The time elapsed since the last frame.
     */
    public void tick(float frameTime) {
        player.tick(frameTime);
        for (Enemy enemy : enemies) {
            enemy.update(frameTime);
        }
        doPhysicsStep(frameTime);
    }

    private void doPhysicsStep(float frameTime) {
    }
    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }
    public void addPowerUp(PowerUp powerUp) {
        powerUps.add(powerUp);
    }
}

