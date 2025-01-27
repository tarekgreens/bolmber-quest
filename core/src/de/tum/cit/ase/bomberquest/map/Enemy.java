package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import de.tum.cit.ase.bomberquest.texture.Animations;

/**
 * A tile-based enemy that moves randomly every 1 second
 * and uses direction-based animations.
 */
public class Enemy {

    private enum Direction { UP, DOWN, LEFT, RIGHT }

    private int tileX, tileY;
    private final TileMap tileMap;
    private final GameMapLogic logic;

    // For random movement:
    private float dirTimer = 0f;
    private float dirInterval = 1f;
    private int dx = 0, dy = 0;
    private Direction currentDirection = Direction.DOWN;

    // For animations:
    private float animTime = 0f;

    public Enemy(TileMap tileMap, int startX, int startY, GameMapLogic logic) {
        this.tileMap = tileMap;
        this.tileX = startX;
        this.tileY = startY;
        this.logic = logic;
    }

    public void update(float delta) {
        // Accumulate time for animation:
        animTime += delta;

        // Every 'dirInterval' seconds, pick a new random direction and move 1 tile:
        dirTimer += delta;
        if (dirTimer >= dirInterval) {
            dirTimer = 0f;
            pickRandomDirection();
            tryMove(dx, dy);
        }

        // Check collision with player:
        if (tileX == logic.getPlayer().getTileX() && tileY == logic.getPlayer().getTileY()) {
            logic.killPlayer("Enemy touched you!");
        }
    }

    /**
     * Picks a random direction among (R, L, U, D), 
     * sets dx/dy accordingly, and updates currentDirection
     * so we select the correct animation.
     */
    private void pickRandomDirection() {
        int r = MathUtils.random(3);
        switch(r) {
            case 0:
                dx = 1; dy = 0;
                currentDirection = Direction.RIGHT;
                break;
            case 1:
                dx = -1; dy = 0;
                currentDirection = Direction.LEFT;
                break;
            case 2:
                dx = 0; dy = 1;
                currentDirection = Direction.UP;
                break;
            case 3:
                dx = 0; dy = -1;
                currentDirection = Direction.DOWN;
                break;
        }
    }

    /**
     * Attempt to move one tile in the chosen direction,
     * checking whether the tile is blocked.
     */
    private void tryMove(int dx,int dy) {
        int nx = tileX + dx;
        int ny = tileY + dy;
        if (!tileMap.isBlocked(nx, ny)) {
            tileX = nx;
            tileY = ny;
        }
    }

    /**
     * Renders the enemy by selecting the correct direction-based 
     * animation from Animations.java, then drawing the current frame.
     */
    public void render(SpriteBatch batch, float tileSizePx) {
        float px = tileX * tileSizePx;
        float py = tileY * tileSizePx;

        // Pick the animation for the current direction:
        Animation<TextureRegion> currentAnim;
        switch (currentDirection) {
            case UP:
                currentAnim = Animations.ENEMY_UP;
                break;
            case DOWN:
                currentAnim = Animations.ENEMY_DOWN;
                break;
            case LEFT:
                currentAnim = Animations.ENEMY_LEFT;
                break;
            case RIGHT:
            default:
                currentAnim = Animations.ENEMY_RIGHT;
                break;
        }

        // Get the current frame (looping = true)
        TextureRegion frame = currentAnim.getKeyFrame(animTime, true);
        batch.draw(frame, px, py);
    }

    public int getTileX() { return tileX; }
    public int getTileY() { return tileY; }
}
