package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * The GameObject class serves as a base class for game objects in the scene.(Enemy, Entrance, Exit, Heart, Key, PlayerCharacter, Trap, Wall)
 * It extends the LibGDX Actor class and provides common properties and methods for game objects.
 *
 * @see com.badlogic.gdx.scenes.scene2d.Actor
 */
public class Object extends Actor {

    /** The x-coordinate of the game object. */
    protected float x;

    /** The y-coordinate of the game object. */
    protected float y;

    /** The width of the game object. */
    protected float width = 64;

    /** The height of the game object. */
    protected float height = 64;

    /** The animation used for dynamic game objects. */
    protected Animation<TextureRegion> animation;

    /** The current animation time for dynamic game objects. */
    protected float animationTime = 0;

    /** The sinus input used for certain animations or behaviors. */
    protected float sinusInput = 0f;

    /** The texture region representing the visual appearance of the game object. */
    protected TextureRegion textureRegion;

    /** The texture of the game object (might be null if using animation). */
    protected Texture texture;

    /** The speed of the game object (might be used in subclasses). */
    protected float speed;

    /**
     * Constructs a GameObject with the specified position, dimensions, and animation.
     *
     * @param x           The x-coordinate of the game object.
     * @param y           The y-coordinate of the game object.
     * @param animation   The animation for dynamic game objects.
     */
    public Object(float x, float y, Animation<TextureRegion> animation) {
        this.x = x;
        this.y = y;
        this.animation = animation;
    }


    /**
     * Checks if this game object collides with another game object.
     *
     * @param object  The other game object to check for collision.
     * @return True if a collision occurs, false otherwise.
     */
    public boolean collidesWith(Object object) {
        boolean above = y + height - 20 > object.getY();
        boolean below = y + 10 < object.getY() + object.getHeight();
        boolean left = x + 3 < object.getX() + object.getWidth();
        boolean right = x + width > object.getX();
        return above && below && left && right;
    }

    /**
     * Draws the game object using the specified SpriteBatch, position, dimensions, and delta time.
     *
     * @param batch   The SpriteBatch used for drawing.
     * @param x       The x-coordinate at which to draw the game object.
     * @param y       The y-coordinate at which to draw the game object.
     * @param delta   The time elapsed since the last draw.
     * @param width   The width at which to draw the game object.
     * @param height  The height at which to draw the game object.
     */
    public void draw(SpriteBatch batch, float x, float y, float delta, float width, float height) {
        animationTime += delta;
        TextureRegion currentFrame = animation.getKeyFrame(animationTime, true);
        batch.draw(currentFrame, x, y, width, height);
    }

    /**
     * Updates the game object based on the elapsed time since the last update.
     *
     * @param delta  The time elapsed since the last update.
     */
    public void update(float delta) {
    }

    // Getters and setters
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getSinusInput() {
        return sinusInput;
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public Texture getTexture() {
        return texture;
    }

    public float getSpeed() {
        return speed;
    }
}
