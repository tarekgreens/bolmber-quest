package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * Represents the exit point in the game.
 */
public class Exit {
    private final int tileX, tileY;
    private final TextureRegion sprite;
    
    public Exit(int tileX, int tileY, TextureRegion sprite) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.sprite = sprite;
    }

    public void render(SpriteBatch batch, float tileSizePx) {
        float px = tileX * tileSizePx;
        float py = tileY * tileSizePx;
        batch.draw(sprite, px, py);
    }

    public int getTileX() {
        return tileX;
    }
    public int getTileY() {
        return tileY;
    }
}
