package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.tum.cit.ase.bomberquest.map.Exit;
import de.tum.cit.ase.bomberquest.map.GameMap;
import de.tum.cit.ase.bomberquest.map.Player;

/**
 * A Heads-Up Display (HUD) that displays information on the screen.
 * It uses a separate camera so that it is always fixed on the screen.
 */
public class Hud {
    
    /** The SpriteBatch used to draw the HUD. This is the same as the one used in the GameScreen. */
    private final SpriteBatch spriteBatch;
    /** The font used to draw text on the screen. */
    private final BitmapFont font;
    /** The camera used to render the HUD. */
    private final OrthographicCamera camera;

    private final GameMap map;

    // The time left or elapsed
    private float timeRemaining = 0f;
    
    public Hud(SpriteBatch spriteBatch, BitmapFont font, GameMap map) {
        this.spriteBatch = spriteBatch;
        this.font = font;
        this.camera = new OrthographicCamera();
        this.map = map;
        this.camera.setToOrtho(false);
    }
    
    /**
     * Renders the HUD on the screen.
     * This uses a different OrthographicCamera so that the HUD is always fixed on the screen.
     */
    public void render() {
        // Render from the camera's perspective
        spriteBatch.setProjectionMatrix(camera.combined);
        // Start drawing
        spriteBatch.begin();

        // For convenience
        Player player = map.getPlayer();
        Exit exit = map.getExit();

        // Draw the HUD elements
        font.draw(spriteBatch, "Press Esc to Pause!", 10, Gdx.graphics.getHeight() - 10);

        // 1) Bomb radius
        int radius = (player != null) ? player.getBombRadius() : 0;
        font.draw(spriteBatch, "Bomb Radius: " + radius, 10, 460);

        // 2) Bomb capacity (concurrent bombs)
        int capacity = (player != null) ? player.getBombCapacity() : 0;
        font.draw(spriteBatch, "Bomb Capacity: " + capacity, 10, 440);

        // 3) Countdown/time left
        font.draw(spriteBatch, "Time Left: " + (int) timeRemaining, 10, 420);

        // 4) Remaining enemies
        int enemyCount = map.getEnemies().size();
        font.draw(spriteBatch, "Enemies Left: " + enemyCount, 10, 400);

        if (exit != null && exit.isUnlocked()) {
            font.draw(spriteBatch, "EXIT UNLOCKED!", 10, 380);
        }
        // Finish drawing
        spriteBatch.end();
    }
    
    /**
     * Resizes the HUD when the screen size changes.
     * This is called when the window is resized.
     * @param width The new width of the screen.
     * @param height The new height of the screen.
     */
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

     /**
     * Let the GameScreen pass the updated time each frame
     */
    public void setTimeRemaining(float time) {
        this.timeRemaining = time;
    }
    
}
