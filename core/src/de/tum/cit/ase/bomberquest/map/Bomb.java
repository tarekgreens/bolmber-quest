package de.tum.cit.ase.bomberquest.map;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import de.tum.cit.ase.bomberquest.texture.Animations;

/**
 * A tile-based bomb with a 3s fuse, calls back into GameMapLogic for explosion logic.
 */
public class Bomb {

    private final int bombX, bombY;
    private final int blastRadius;
    private final long placeTime;
    private boolean exploded = false;

    private float fuseTime = 3f;  // how many seconds until explosion
    private float fuseTimer = 0f;
    private float blastDuration = 0.28f; // total explosion frames time
    private float explosionTimer = 0f;

    private float animTime = 0f; 
    private final List<ExplosionTile> affectedTiles = new ArrayList<>();

    private static final float FUSE_TIME_SECONDS = 3f;

    private final GameMapLogic logic;

    private boolean done = false; // means "completely finished, remove from list"

    public boolean isDone() {
    return done;
}

    public Bomb(int x, int y, int radius, GameMapLogic logic) {
        this.bombX = x;
        this.bombY = y;
        this.blastRadius = radius;
        this.logic = logic;
        this.placeTime = System.currentTimeMillis();
    }

    public void update(float delta) {
        if (!exploded) {
            // Fuse phase: accumulate time
            animTime += delta;
            fuseTimer += delta;
            if (fuseTimer >= fuseTime) {
                // Bomb goes off
                exploded = true;
                explosionTimer = 0f;
                explode();
            }
        } else {
            // Explosion phase: track time
            explosionTimer += delta;
            if (explosionTimer > blastDuration) {
                // Explosion animation ended => remove bomb from map
                done = true;
            }
        }
    }

    private void explode() {
        exploded = true;
        // damage center tile
        logic.handleExplosionTile(bombX, bombY);

        // center
        for (int i = 1; i <= blastRadius; i++) {
            int tx = bombX;
            int ty = bombY;
            boolean blocked = handleExplosionTile(tx, ty);
            if (!blocked) {
                // We managed to hit tile (tx, ty)
            } else {
                break; // wall found, stop
            }
        }

        // up
        for (int i = 1; i <= blastRadius; i++) {
            int tx = bombX;
            int ty = bombY + i;
            boolean blocked = handleExplosionTile(tx, ty);
            if (!blocked) {
                // We managed to hit tile (tx, ty)
            } else {
                break; // wall found, stop
            }
        }

        // down
        for (int i = 1; i <= blastRadius; i++) {
            int tx = bombX;
            int ty = bombY - i;
            boolean blocked = handleExplosionTile(tx, ty);
            if (!blocked) {
                // We managed to hit tile (tx, ty)
            } else {
                break; // wall found, stop
            }
        }

        // right
        for (int i = 1; i <= blastRadius; i++) {
            int tx = bombX + i;
            int ty = bombY;
            boolean blocked = handleExplosionTile(tx, ty);
            if (!blocked) {
                // We managed to hit tile (tx, ty)
            } else {
                break; // wall found, stop
            }
        }

        // left
        for (int i = 1; i <= blastRadius; i++) {
            int tx = bombX - i;
            int ty = bombY;
            boolean blocked = handleExplosionTile(tx, ty);
            if (!blocked) {
                // We managed to hit tile (tx, ty)
            } else {
                break; // wall found, stop
            }
        }
    }

        // A wrapper to call logic.handleExplosionTile(...) AND store the tile if not blocked
    private boolean handleExplosionTile(int x, int y) {
        boolean blocked = logic.handleExplosionTile(x, y);
        // If it's not blocked, that tile is part of the blast area
        // (The tile might be destructible or free. If destructible, we still show the flame)
        if (!blocked) {
            affectedTiles.add(new ExplosionTile(x, y));
        }
        return blocked;
    }

    public boolean isExploded() {
        return exploded;
    }

    public void renderFuse(SpriteBatch batch, float tileSize) {
        if (!exploded) {
            // Draw the fuse animation
            float px = bombX * tileSize;
            float py = bombY * tileSize;
            TextureRegion fuseFrame = Animations.BOMB_FUSE.getKeyFrame(animTime, true);
            batch.draw(fuseFrame, px, py);
        }
    }

    public void renderExplosion(ShapeRenderer shapeRenderer, float tileSize) {
        if (!exploded || done) return;

        float progress = explosionTimer / blastDuration; // 0..1
    
        for (ExplosionTile tile : affectedTiles) {
            // Flicker alpha a bit
            float alpha = (1 - progress) * (0.5f + MathUtils.random(0.2f));
    
            // Outer color
            shapeRenderer.setColor(1f, 0.2f, 0f, alpha);
            shapeRenderer.rect(tile.x*tileSize, tile.y*tileSize, tileSize, tileSize);
    
            // Inner color is brighter, smaller
            shapeRenderer.setColor(1f, 0.8f, 0f, alpha + 0.2f);
            float inset = tileSize * 0.90f;
            shapeRenderer.rect(tile.x*tileSize+inset, tile.y*tileSize+inset, tileSize-(inset), tileSize-(inset));
        }
    

    }


    public int getX() {return bombX;}
    public int getY() {return bombY;}
}
