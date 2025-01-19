package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

/**
 * A tile-based enemy that moves randomly every 1 second.
 */
public class Enemy {

    private int tileX, tileY;
    private final TileMap tileMap;
    private final GameMapLogic logic;
    private TextureRegion sprite;

    private float dirTimer=0f;
    private float dirInterval=1f;
    private int dx=0, dy=0;

    public Enemy(TileMap tileMap, int startX, int startY, TextureRegion sprite, GameMapLogic logic) {
        this.tileMap = tileMap;
        this.tileX = startX;
        this.tileY = startY;
        this.sprite = sprite;
        this.logic = logic;
    }

    public void update(float delta) {
        dirTimer += delta;
        if (dirTimer>= dirInterval) {
            dirTimer=0f;
            pickRandomDirection();
        }
        tryMove(dx, dy);

        // If same tile as player => kill player
        if (tileX == logic.getPlayer().getTileX() && tileY == logic.getPlayer().getTileY()) {
            logic.killPlayer("Enemy touched you!");
        }
    }

    private void pickRandomDirection() {
        int r = MathUtils.random(3);
        switch(r) {
            case 0: dx=1; dy=0; break;
            case 1: dx=-1; dy=0; break;
            case 2: dx=0; dy=1; break;
            case 3: dx=0; dy=-1; break;
        }
    }

    private void tryMove(int dx,int dy) {
        int nx= tileX+dx;
        int ny= tileY+dy;
        if (!tileMap.isBlocked(nx, ny)) {
            tileX=nx; tileY=ny;
        }
    }

    public void render(SpriteBatch batch, float tileSizePx) {
        float px = tileX*tileSizePx;
        float py = tileY*tileSizePx;
        batch.draw(sprite, px, py);
    }

    public int getTileX(){return tileX;}
    public int getTileY(){return tileY;}
}
