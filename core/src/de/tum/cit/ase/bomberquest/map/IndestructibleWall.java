package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;

/**
 * IndestructibleWall is a subclass of WallPath that cannot be destroyed.
 */
public class IndestructibleWall extends WallPath {

    /**
     * Constructs an IndestructibleWall with the specified position, dimensions, and texture region.
     *
     * @param world         The Box2D world to associate the wall with.
     * @param x             The initial x-coordinate of the wall.
     * @param y             The initial y-coordinate of the wall.
     * @param width         The width of the wall.
     * @param height        The height of the wall.
     * @param textureRegion The TextureRegion for the static appearance of the wall.
     */
    public IndestructibleWall(World world, float x, float y, float width, float height, TextureRegion textureRegion) {
        super(world, x, y, width, height, textureRegion);
    }

    /**
     * Determines if the wall/path is destructible.
     *
     * @return False because this is an indestructible wall.
     */
    @Override
    public boolean isDestructible() {
        return false;
    }

    /**
     * Indestructible walls cannot be destroyed, so this method does nothing.
     */
    @Override
    public void destroy() {

    }
}
