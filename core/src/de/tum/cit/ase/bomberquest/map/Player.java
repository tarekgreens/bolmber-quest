package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.tum.cit.ase.bomberquest.texture.Animations;

/**
 * The Player moves tile-by-tile and can place bombs.
 */
public class Player {

    private int tileX, tileY;
    private Direction currentDirection = Direction.DOWN;
    private boolean isMoving = false;
    private final TileMap tileMap;
    private final GameMapLogic logic; // We'll store or reference an external logic that holds bombs etc.
    // We'll have four separate animations
    private float animationTime = 0f;

    private int bombCapacity = 1;
    private int bombsActive = 0;
    private int bombRadius = 1;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Player(TileMap tileMap, int startX, int startY, GameMapLogic logic) {
        this.tileMap = tileMap;
        this.tileX = startX;
        this.tileY = startY;
        this.logic = logic;

        this.animationTime = 0f;
    }

    public void update(float delta) {
        isMoving = false;
        int dx=0, dy=0;

        // Check input
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            tryMove(0, 1);
            currentDirection = Direction.UP;
        }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            tryMove(0, -1);
            currentDirection = Direction.DOWN;
        }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            tryMove(-1, 0);
            currentDirection = Direction.LEFT;
        }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            tryMove(1, 0);
            currentDirection = Direction.RIGHT;
        }

        // If any movement happened, isMoving = true
        // (You could also do this inside tryMove if it actually changes tileX/tileY.)
        if (dx != 0 || dy != 0) {
            isMoving = true;
        }

        // Update animation time only if moving
        if (isMoving) {
            animationTime += delta;
        }

        // Place bomb if SPACE pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            attemptPlaceBomb();
        }
    }

    private void tryMove(int dx, int dy) {
        int nx = tileX + dx;
        int ny = tileY + dy;
        if (!tileMap.isBlocked(nx, ny)) {
            tileX = nx;
            tileY = ny;
        }
    }

    private void attemptPlaceBomb() {
        if (bombsActive < bombCapacity) {
            // place a bomb
            logic.addBomb(new Bomb(tileX, tileY, bombRadius, logic));
            bombsActive++;
        }
    }

    public void bombExploded() {
        bombsActive = Math.max(0, bombsActive-1);
    }

    public void increaseBombRadius() {
        bombRadius = Math.min(bombRadius+1, 8);
    }

    public void increaseBombCapacity() {
        bombCapacity = Math.min(bombCapacity+1, 8);
    }

    public void render(SpriteBatch batch, float tileSizePx) {
        float px = tileX * tileSizePx;
        float py = tileY * tileSizePx;

        // Decide which direction's animation we want
        TextureRegion currentFrame;
        switch (currentDirection) {
            case UP:
                currentFrame = Animations.PLAYER_UP.getKeyFrame(animationTime, true);
                break;
            case DOWN:
                currentFrame = Animations.PLAYER_DOWN.getKeyFrame(animationTime, true);
                break;
            case LEFT:
                currentFrame = Animations.PLAYER_LEFT.getKeyFrame(animationTime, true);
                break;
            case RIGHT:
            default:
                currentFrame = Animations.PLAYER_RIGHT.getKeyFrame(animationTime, true);
                break;
        }

        batch.draw(currentFrame, px, py);
    }



    public int getTileX() { return tileX; }
    public int getTileY() { return tileY; }
    public int getBombRadius() { return bombRadius; }
    public int getBombCapacity() { return bombCapacity; }
}
