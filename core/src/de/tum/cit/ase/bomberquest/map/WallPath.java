package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * Abstract base class for all walls and paths in the Bomber Quest game.
 * Implements the {@link Drawable} interface for rendering and coordinates.
 */
public abstract class WallPath implements Drawable {

    protected Rectangle bounds;
    protected TextureRegion textureRegion;
    protected boolean isDestroyed;
    protected final Body body;

    /**
     * Constructs a WallPath object.
     *
     * @param world         The Box2D world to associate the wall/path with.
     * @param x             X position of the wall/path in the grid.
     * @param y             Y position of the wall/path in the grid.
     * @param width         Width of the wall/path.
     * @param height        Height of the wall/path.
     * @param textureRegion The texture to render for this wall/path.
     */
    public WallPath(World world, float x, float y, float width, float height, TextureRegion textureRegion) {
        if (textureRegion == null) {
            throw new IllegalArgumentException("TextureRegion cannot be null for WallPath object.");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive.");
        }
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null.");
        }

        this.bounds = new Rectangle(x, y, width, height);
        this.textureRegion = textureRegion;
        this.isDestroyed = false;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x + width / 2, y + height / 2);
        this.body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.0f;

        this.body.createFixture(fixtureDef);
        this.body.setUserData(this);

        shape.dispose();
    }

    /**
     * Gets the current appearance of the WallPath.
     *
     * @return The texture region representing the appearance of the WallPath.
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        return textureRegion;
    }

    /**
     * Gets the X coordinate of the WallPath.
     *
     * @return The X coordinate (bottom-left corner) in tile coordinates.
     */
    @Override
    public float getX() {
        return bounds.x;
    }

    /**
     * Gets the Y coordinate of the WallPath.
     *
     * @return The Y coordinate (bottom-left corner) in tile coordinates.
     */
    @Override
    public float getY() {
        return bounds.y;
    }

    /**
     * Renders the wall/path if it is not destroyed.
     *
     * @param batch The SpriteBatch used to draw the texture.
     */
    public void draw(SpriteBatch batch) {
        if (!isDestroyed && textureRegion != null) {
            batch.draw(textureRegion, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    /**
     * Determines if the wall/path is destructible.
     *
     * @return True if destructible, false otherwise.
     */
    public abstract boolean isDestructible();

    /**
     * Destroys the wall/path, if applicable.
     */
    public abstract void destroy();

    /**
     * Gets the bounding rectangle for collision or placement purposes.
     *
     * @return The bounds of the wall/path.
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Checks if the wall/path has been destroyed.
     *
     * @return True if destroyed, false otherwise.
     */
    public boolean isDestroyed() {
        return isDestroyed;
    }

    /**
     * Disposes of resources associated with this wall/path.
     */
    public void dispose() {
        if (textureRegion != null) {
            textureRegion.getTexture().dispose();
        }
    }
}
