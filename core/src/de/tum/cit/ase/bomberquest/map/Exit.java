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
public class Exit extends Object implements Drawable {
    /** A flag indicating whether the exit has been unlocked (all enemies defeated). */
    private boolean unlocked;

    /** A flag indicating whether the exit is revealed after the wall is destroyed. */
    private boolean revealed;

    /** The wall above the exit (a destructible wall). */
    private WallPath wallAboveExit;

    /**
     * Constructs an Exit object with the specified position, dimensions, animation, and wall above it.
     *
     * @param x           The x-coordinate of the exit.
     * @param y           The y-coordinate of the exit.
     * @param textureRegion  The animation representing the exit's appearance.
     * @param wallAboveExit  The wall above the exit that must be destroyed to reveal the exit.
     */
    public Exit(float x, float y, TextureRegion textureRegion, WallPath wallAboveExit) {
        super(x, y, null);
        this.textureRegion = textureRegion; // Set the visual representation of the entrance.
        this.width = 64; // Default width
        this.height = 64; // Default height
        this.wallAboveExit = wallAboveExit;
        this.unlocked = false;
        this.revealed = false;
    }

    /**
     * Unlocks the exit after all enemies are defeated in the maze.
     */
    public void unlockExit() {
        this.unlocked = true;
    }

    /**
     * Reveals the exit by destroying the wall above it.
     */
    public void revealExit() {
        if (wallAboveExit != null && wallAboveExit.isDestroyed()) {
            this.revealed = true;
        }
    }

    /**
     * Checks if the exit is accessible (unlocked and revealed).
     *
     * @return True if the exit is unlocked and revealed, false otherwise.
     */
    public boolean isAccessible() {
        return unlocked && revealed;
    }

    /**
     * Updates the exit based on the elapsed time since the last update.
     * This method also checks if the wall above it is destroyed and updates the revealed status.
     *
     * @param delta  The time elapsed since the last update.
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        // Check if the exit should be revealed after the wall is destroyed.
        revealExit();
    }

    /**
     * Renders the exit using the specified SpriteBatch.
     * If the exit is revealed, the exit will be drawn.
     * If not, the exit will remain hidden beneath the destructible wall.
     *
     * @param spriteBatch  The SpriteBatch used for rendering.
     */
    @Override
    public void draw(SpriteBatch spriteBatch, float x, float y, float delta, float width, float height) {
        if (revealed && isAccessible()) {
            // Draw the exit only if it's revealed and accessible
            super.draw(spriteBatch, x, y, delta, width, height);
        }
    }

    // Getters and setters
    public boolean isUnlocked() {
        return unlocked;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public WallPath getWallAboveExit() {
        return wallAboveExit;
    }

    public void setWallAboveExit(WallPath wallAboveExit) {
        this.wallAboveExit = wallAboveExit;
    }
    @Override
    public TextureRegion getCurrentAppearance() {
        // just return this.textureRegion
        return textureRegion;
    }

    @Override
    public float getX() {
        return x;  // ‘x’ is inherited from parent
    }

    @Override
    public float getY() {
        return y;
    }
}
