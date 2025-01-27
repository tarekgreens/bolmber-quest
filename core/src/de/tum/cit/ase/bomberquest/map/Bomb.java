package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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

    private static final float FUSE_TIME_SECONDS = 3f;

    private final GameMapLogic logic;

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
                isExploded();
            }
        }
    }

    private void explode() {
        exploded = true;
        // damage center tile
        logic.handleExplosionTile(bombX, bombY);

        // up
        for(int i=1; i<=blastRadius; i++){
            int tx=bombX; int ty=bombY+i;
            if (logic.handleExplosionTile(tx,ty)) break; 
        }
        // down
        for(int i=1; i<=blastRadius; i++){
            int tx=bombX; int ty=bombY-i;
            if (logic.handleExplosionTile(tx,ty)) break;
        }
        // right
        for(int i=1; i<=blastRadius; i++){
            int tx=bombX+i; int ty=bombY;
            if (logic.handleExplosionTile(tx,ty)) break;
        }
        // left
        for(int i=1; i<=blastRadius; i++){
            int tx=bombX-i; int ty=bombY;
            if (logic.handleExplosionTile(tx,ty)) break;
        }
    }

    public boolean isExploded() {
        return exploded;
    }

        public void render(SpriteBatch batch, float tileSize) {
        float px = bombX * tileSize;
        float py = bombY * tileSize;

        if (!exploded) {
            // Show fuse
            TextureRegion fuseFrame = Animations.BOMB_FUSE.getKeyFrame(animTime, true);
            batch.draw(fuseFrame, px, py);
        } else {
            // Show explosion cross
            TextureRegion blastFrame = Animations.BOMB_BLAST.getKeyFrame(explosionTimer, false);
            if (blastFrame != null) {
                // Each cross is 32×32 => might need scaling if your tileSize is 16
                // e.g. batch.draw(blastFrame, px - 8, py - 8) so it's centered 
                // or scale to tileSize, etc. 
                batch.draw(blastFrame, px, py);
            }
        }
    }

    public int getX() {return bombX;}
    public int getY() {return bombY;}
}
