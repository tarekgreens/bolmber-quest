package de.tum.cit.ase.bomberquest.map;

/**
 * A tile-based bomb with a 3s fuse, calls back into GameMapLogic for explosion logic.
 */
public class Bomb {

    private final int bombX, bombY;
    private final int blastRadius;
    private final long placeTime;
    private boolean exploded = false;

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
            float elapsed = (System.currentTimeMillis() - placeTime)/1000f;
            if (elapsed >= FUSE_TIME_SECONDS) {
                explode();
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

    public int getX() {return bombX;}
    public int getY() {return bombY;}
}
