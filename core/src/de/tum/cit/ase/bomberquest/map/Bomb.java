package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.bomberquest.screen.GameScreen;
import de.tum.cit.ase.bomberquest.texture.Textures;

public class Bomb {

    private static final float FUSE_TIME_SECONDS = 3.0f;

    private final int tileX;
    private final int tileY;
    private final int blastRadius;
    private final long placeTimeMillis;
    private boolean exploded;

    // We store a bomb texture region so we can draw it
    private final TextureRegion texture;

    public Bomb(int tileX, int tileY, int radius, long placeTimeMillis) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.blastRadius = radius;
        this.placeTimeMillis = placeTimeMillis;
        this.exploded = false;

        // Example: if you have a BOMB texture region in your Textures class
        this.texture = Textures.BOMB;  // e.g. loaded from a spritesheet
    }

    public void update(float deltaTime, GameMap map) {
        if (!exploded) {
            float elapsed = (System.currentTimeMillis() - placeTimeMillis) / 1000f;
            if (elapsed >= FUSE_TIME_SECONDS) {
                explode(map);
            }
        }
    }

    private void explode(GameMap map) {
        exploded = true;

        // 1) Damage outward in cardinal directions
        causeBlast(map,  1,  0); // right
        causeBlast(map, -1,  0); // left
        causeBlast(map,  0,  1); // up
        causeBlast(map,  0, -1); // down

        // 2) Damage the tile the bomb is on
        damageTile(map, tileX, tileY);

        // 3) Inform the player so bomb capacity can free up
        if (map.getPlayer() != null) {
            map.getPlayer().bombHasExploded();
        }

        // Optionally: sound effect, explosion animation, etc.
    }

    private void causeBlast(GameMap map, int dx, int dy) {
        int currentX = tileX;
        int currentY = tileY;
        for (int i = 1; i <= blastRadius; i++) {
            currentX += dx;
            currentY += dy;
            if (currentX < 0 || currentX >= 40 || currentY < 0 || currentY >= 24) {
                break;
            }
            boolean blocked = damageTile(map, currentX, currentY);
            if (blocked) {
                // Indestructible or we destroyed a destructible (end blast)
                break;
            }
        }
    }

    private boolean damageTile(GameMap map, int x, int y) {
        // Check walls
        for (WallPath wall : map.getWalls()) {
            if (!wall.isDestroyed()) {
                int wx = (int) wall.getX();
                int wy = (int) wall.getY();
                if (wx == x && wy == y) {
                    if (wall.isDestructible()) {
                        wall.destroy();  // remove from Box2D, mark destroyed
                        return false;   // continue blast
                    } else {
                        return true;    // block blast
                    }
                }
            }
        }
        // Check enemies
        for (Enemy e : map.getEnemies()) {
            int ex = (int) e.getX();
            int ey = (int) e.getY();
            if (ex == x && ey == y) {
                map.getEnemies().remove(e);
                break;
            }
        }
        // Check player
        Player p = map.getPlayer();
        if (p != null) {
            int px = (int) p.getX();
            int py = (int) p.getY();
            if (px == x && py == y) {
                System.out.println("Player killed by bomb at (" + x + "," + y + ")!");
                // e.g. map.gameOver();
            }
        }
        return false; // no block
    }

    public boolean isExploded() {
        return exploded;
    }

    public void render(SpriteBatch batch) {
        if (!exploded) {
            float px = tileX * GameScreen.TILE_SIZE_PX * GameScreen.SCALE;
            float py = tileY * GameScreen.TILE_SIZE_PX * GameScreen.SCALE;
            batch.draw(texture, px, py,
                texture.getRegionWidth() * GameScreen.SCALE,
                texture.getRegionHeight() * GameScreen.SCALE
            );
        }
    }
}
