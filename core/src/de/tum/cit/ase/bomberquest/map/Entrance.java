package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * The Entrance class represents the starting point of the player in the game.
 * It extends the Object class and provides specific functionality related to the entrance object.
 *
 * The Entrance serves as the spawn point for the player at the beginning of the game.
 * It is visually represented by a texture and does not require any dynamic animation.
 *
 * @see Object
 */
public class Entrance extends Object {

    /**
     * Constructs an Entrance object with the specified position and texture region.
     *
     * @param x              The x-coordinate of the entrance.
     * @param y              The y-coordinate of the entrance.
     * @param textureRegion  The texture region representing the entrance's visual appearance.
     */
    public Entrance(float x, float y, TextureRegion textureRegion) {
        super(x, y, null); //We don;t need animation
        this.textureRegion = textureRegion; // Set the visual representation of the entrance.
        this.width = 64; // Default width
        this.height = 64; // Default height
    }

    /**
     * Draws the entrance using the specified SpriteBatch and dimensions.
     *
     * @param spriteBatch  The SpriteBatch used for rendering the entrance.
     * @param x            The x-coordinate at which to draw the entrance.
     * @param y            The y-coordinate at which to draw the entrance.
     * @param width        The width at which to draw the entrance.
     * @param height       The height at which to draw the entrance.
     */

    public void draw(SpriteBatch spriteBatch, float x, float y, float width, float height) {
        if (textureRegion != null) {
            spriteBatch.draw(textureRegion, x, y, width, height);
        }
    }

    /**
     * Updates the entrance object.
     * Since the entrance does not have dynamic behavior, this method is empty.
     *
     * @param delta  The time elapsed since the last update.
     */
    @Override
    public void update(float delta) {

    }
}

