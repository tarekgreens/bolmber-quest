package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.map.Bomb;
import de.tum.cit.ase.bomberquest.map.Enemy;
import de.tum.cit.ase.bomberquest.map.Flowers;
import de.tum.cit.ase.bomberquest.map.Player;
import de.tum.cit.ase.bomberquest.map.PowerUp;
import de.tum.cit.ase.bomberquest.map.WallPath;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.map.GameMap;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {
    
    /**
     * The size of a grid cell in pixels.
     * This allows us to think of coordinates in terms of square grid tiles
     * (e.g. x=1, y=1 is the bottom left corner of the map)
     * rather than absolute pixel coordinates.
     */
    public static final int TILE_SIZE_PX =16;
    
    /**
     * The scale of the game.
     * This is used to make everything in the game look bigger or smaller.
     */
    public static final int SCALE = 2;

    private final BomberQuestGame game;
    private final SpriteBatch spriteBatch;
    private final GameMap map;
    private final Hud hud;
    private final OrthographicCamera mapCamera;
    // For countdown
    private float levelTime = 30f; // 5 minutes


    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(BomberQuestGame game) {
        this.game = game;
        this.spriteBatch = game.getSpriteBatch();
        this.map = game.getMap();
        // Create and configure the camera for the game view
        this.mapCamera = new OrthographicCamera();
        this.mapCamera.setToOrtho(false);
        // Create the HUD and pass references it needs
        this.hud = new Hud(game.getSpriteBatch(), game.getSkin().getFont("font"), this.map);
    }
    
    /**
     * The render method is called every frame to render the game.
     * @param deltaTime The time in seconds since the last render.
     */
    @Override
    public void render(float deltaTime) {
        // Check for escape key press to go back to the menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.goToMenu();
        }
        
        // Clear the previous frame from the screen, or else the picture smears
        ScreenUtils.clear(Color.WHITE);

         // Decrement countdown
         levelTime -= deltaTime;
         if (levelTime < 0f) {
             levelTime = 0f;
         }
        
        // Cap frame time to 250ms to prevent spiral of death
        float frameTime = Math.min(deltaTime, 0.250f);
        
        // Update the map state
        map.tick(frameTime);
        
        // Update the camera
        updateCamera();

        // Render the map
        game.getSpriteBatch().setProjectionMatrix(mapCamera.combined);
        game.getSpriteBatch().begin();
        
        // Render the map on the screen
        renderMap();

        game.getSpriteBatch().end();

        // Finally, render the HUD on top
        hud.setTimeRemaining(levelTime); // pass updated time to the HUD
        
        // Render the HUD on the screen
        hud.render();


    }


    /**
     * Updates the camera to match the current state of the game.
     * Currently, this just centers the camera at the origin.
     */
    private void updateCamera() {
        mapCamera.setToOrtho(false);
        mapCamera.position.x = 19.5f * TILE_SIZE_PX * SCALE;
        mapCamera.position.y = 11.5f * TILE_SIZE_PX * SCALE;
        mapCamera.update();
    }
    
    private void renderMap() {
        // This configures the spriteBatch to use the camera's perspective when rendering
        spriteBatch.setProjectionMatrix(mapCamera.combined);




    if (map.getChest() != null) {
        draw(spriteBatch, map.getChest());
    }

    // Player should never be null if you instantiate it, but just in case:
    if (map.getPlayer() != null) {
        draw(spriteBatch, map.getPlayer());
    }

    // For walls, skip null entries
    for (WallPath wall : map.getWalls()) {
        if (wall != null) {
            draw(spriteBatch, wall);
        }
    }

    for (Bomb b : map.getBombs()) {
        b.render(spriteBatch);
    }

    if (map.getEntrance() != null) {
        draw(spriteBatch, map.getEntrance());
    }
    for (Enemy e : map.getEnemies()) {
        draw(spriteBatch, e);
    }
    for (PowerUp p : map.getPowerUps()) {
        draw(spriteBatch, p);
    }

    draw(spriteBatch, map.getExit());
    }
    /**
     * Draws this object on the screen.
     * The texture will be scaled by the game scale and the tile size.
     * This should only be called between spriteBatch.begin() and spriteBatch.end(), e.g. in the renderMap() method.
     * @param spriteBatch The SpriteBatch to draw with.
     */
    private static void draw(SpriteBatch spriteBatch, Drawable drawable) {
        if (drawable instanceof WallPath wall && wall.isDestroyed()) {
            return; // Skip drawing destroyed walls
        }
        TextureRegion texture = drawable.getCurrentAppearance();

        if (texture == null) {
            System.out.println("DEBUG: getCurrentAppearance() is null for " + drawable.getClass().getSimpleName()
                + " => x=" + drawable.getX() + " y=" + drawable.getY());
            return;  // skip drawing, avoid NPE
        }

        float x, y;
        if (drawable instanceof Player) {
            Player player = (Player) drawable;
            x = player.getX() * TILE_SIZE_PX * SCALE - 0.3f * TILE_SIZE_PX * SCALE; // Adjust for radius
            y = player.getY() * TILE_SIZE_PX * SCALE - 0.3f * TILE_SIZE_PX * SCALE;
        } else {
            x = drawable.getX() * TILE_SIZE_PX * SCALE;
            y = drawable.getY() * TILE_SIZE_PX * SCALE;
        }

        float width = texture.getRegionWidth() * SCALE;
        float height = texture.getRegionHeight() * SCALE;
        spriteBatch.draw(texture, x, y, width, height);
    }
    
    /**
     * Called when the window is resized.
     * This is where the camera is updated to match the new window size.
     * @param width The new window width.
     * @param height The new window height.
     */
    @Override
    public void resize(int width, int height) {
        mapCamera.setToOrtho(false);
        hud.resize(width, height);
    }

    // Unused methods from the Screen interface
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }

}
