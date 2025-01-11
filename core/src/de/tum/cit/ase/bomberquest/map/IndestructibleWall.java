package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * IndestructibleWall is a subclass of WallPath that cannot be destroyed.
 */
public class IndestructibleWall extends WallPath  {

    /**
     * Constructs an IndestructibleWall with the specified position, dimensions, and texture region.
     *
     * @param x             The initial x-coordinate of the wall.
     * @param y             The initial y-coordinate of the wall.
     * @param width         The width of the wall.
     * @param height        The height of the wall.
     * @param textureRegion The TextureRegion for the static appearance of the wall.
     */
    public IndestructibleWall(float x, float y, float width, float height, TextureRegion textureRegion) {
        super(x, y, width, height, textureRegion);
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
        // No action needed for indestructible walls.
    }

}