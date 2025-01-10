package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/*
* WallPath is an abstract class that represents a wall or path in the game map.
*
 */
public abstract class WallPath {
    protected Rectangle bounds;
    protected Texture texture;

    // Constructor
    public WallPath(float x, float y, float width, float height, Texture texture) {
        this.bounds = new Rectangle(x, y, width, height);
        this.texture = texture;
    }

    // Render method for drawing the wall or path
    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    // Abstract methods for specific behaviors
    public abstract boolean isDestructible();
    public abstract void destroy();
    public Rectangle getBounds() {
        return bounds;
    }
}

