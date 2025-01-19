package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * The Player moves tile-by-tile and can place bombs.
 */
public class Player {

    private int tileX, tileY;
    private final TileMap tileMap;
    private final GameMapLogic logic; // We'll store or reference an external logic that holds bombs etc.

    private TextureRegion sprite;
    private int bombCapacity = 1;
    private int bombsActive = 0;
    private int bombRadius = 1;

    public Player(TileMap tileMap, int startX, int startY, TextureRegion sprite, GameMapLogic logic) {
        this.tileMap = tileMap;
        this.tileX = startX;
        this.tileY = startY;
        this.sprite = sprite;
        this.logic = logic;
    }

    public void update() {
        // Simple input-based movement
        int dx=0, dy=0;
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) { dy=1; }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) { dy=-1; }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) { dx=-1; }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) { dx=1; }

        if (dx!=0 || dy!=0) {
            tryMove(dx, dy);
        }

        // Place bomb if SPACE pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            attemptPlaceBomb();
        }
    }

    private void tryMove(int dx, int dy) {
        int nx = tileX + dx;
        int ny = tileY + dy;
        // Check if blocked
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
        batch.draw(sprite, px, py);
    }

    public int getTileX() { return tileX; }
    public int getTileY() { return tileY; }
    public int getBombRadius() { return bombRadius; }
    public int getBombCapacity() { return bombCapacity; }
}
