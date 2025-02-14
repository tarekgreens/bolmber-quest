package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * DestructibleWall is a subclass of WallPath that can be destroyed.
 */
public class DestructibleWall extends WallPath {

    /**
     * Constructs a DestructibleWall with the specified position, dimensions, and texture region.
     *
     * @param x             The initial x-coordinate of the wall.
     * @param y             The initial y-coordinate of the wall.
     * @param width         The width of the wall.
     * @param height        The height of the wall.
     * @param textureRegion The TextureRegion for the static appearance of the wall.
     */
    public DestructibleWall(World world, float x, float y, float width, float height, TextureRegion textureRegion) {
        super(world, x, y, width, height, textureRegion);
    }

    /**
     * Determines if the wall/path is destructible.
     *
     * @return True because this is a destructible wall.
     */
    @Override
    public boolean isDestructible() {
        return true;
    }

    /**
     * Destroys the wall/path by setting the isDestroyed flag to true.
     */
    @Override
    public void destroy() {
        if (!isDestroyed) {
            this.isDestroyed = true;
            body.getWorld().destroyBody(body);
        }
    }
}