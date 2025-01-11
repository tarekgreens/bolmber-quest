package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * WallPath is an abstract class that represents a wall or path in the game map.
 * It no longer uses animation, and it directly handles a static texture.
 */
public abstract class WallPath extends Object implements Drawable{

    private static final float TILE_SIZE = 32.0f;

    protected Rectangle bounds;
    protected TextureRegion textureRegion;
    protected boolean isDestroyed;
    protected final Body body;

    /**
     * Constructs a WallPath object.
     *
     * @param x             X position of the wall/path.
     * @param y             Y position of the wall/path.
     * @param width         Width of the wall/path.
     * @param height        Height of the wall/path.
     * @param textureRegion TextureRegion to render for the wall/path.
     */
    public WallPath(World world, float x, float y, float width, float height, TextureRegion textureRegion) {
        super(x, y, null);  // No animation for the wall, passing null for animation.

        if (textureRegion == null) {
            throw new IllegalArgumentException("TextureRegion cannot be null for WallPath object.");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive.");
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
     * Renders the wall/path if it is not destroyed.
     *
     * @param batch The SpriteBatch used to draw the texture.
     */
    @Override
    public void draw(SpriteBatch batch, float x, float y, float delta, float width, float height) {
        if (!isDestroyed && textureRegion != null) {
            batch.draw(textureRegion, x, y, width, height);
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
            // Optionally dispose the texture associated with the texture region
            textureRegion.getTexture().dispose();
        }
    }
    public TextureRegion getCurrentAppearance() {
        return textureRegion;
    }

}