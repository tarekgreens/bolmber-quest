package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PowerUp {

    private final int tileX, tileY;
    private final int powerType; // 5 => concurrency, 6 => blast radius
    private final TextureRegion sprite;

    public PowerUp(int x, int y, int type, TextureRegion sprite) {
        this.tileX = x;
        this.tileY = y;
        this.powerType = type;
        this.sprite = sprite;
    }

    public void render(SpriteBatch batch, float tileSizePx) {
        float px = tileX * tileSizePx;
        float py = tileY * tileSizePx;
        batch.draw(sprite, px, py);
    }

    public int getTileX(){return tileX;}
    public int getTileY(){return tileY;}
    public int getPowerType(){return powerType;}
}
