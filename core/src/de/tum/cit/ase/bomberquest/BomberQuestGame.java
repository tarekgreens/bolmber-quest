package de.tum.cit.ase.bomberquest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.files.FileHandle;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.map.GameMap;
import de.tum.cit.ase.bomberquest.screen.GameScreen;
import de.tum.cit.ase.bomberquest.screen.MenuScreen;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;

/**
 * The BomberQuestGame class represents the core of the Bomber Quest game.
 * It manages resources like the SpriteBatch, Skin, and GameMap, and handles screen transitions.
 */
public class BomberQuestGame extends Game {

    private SpriteBatch spriteBatch; // Shared SpriteBatch for rendering
    private Skin skin;               // UI skin for styling
    private final NativeFileChooser fileChooser; // File chooser for selecting map files
    private GameMap map;             // Current game map

    /**
     * Constructor for BomberQuestGame.
     *
     * @param fileChooser The file chooser for selecting custom maps.
     */
    public BomberQuestGame(NativeFileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }

    /**
     * Initializes the game resources and sets the initial screen.
     */
    @Override
    public void create() {
        // Initialize resources
        this.spriteBatch = new SpriteBatch();
        this.skin = new Skin(Gdx.files.internal("skin/craftacular/craftacular-ui.json"));

        // Start background music
        MusicTrack.BACKGROUND.play();

        // Start with the menu screen
        goToMenu();
    }

    /**
     * Navigates to the menu screen.
     */
    public void goToMenu() {
        this.setScreen(new MenuScreen(this));
    }

    /**
     * Navigates to the game screen.
     */
    public void goToGame() {
        this.setScreen(new GameScreen(this));
    }

    /**
     * Loads a map using the NativeFileChooser and starts the game.
     */
    public void loadMap() {
        // Configure the file chooser
        NativeFileChooserConfiguration configuration = new NativeFileChooserConfiguration();
        configuration.mimeFilter = "text/plain"; // Allow plain text files
        configuration.nameFilter = (dir, name) -> name.endsWith(".properties"); // Only accept .properties files
        configuration.title = "Select a Map File"; // Dialog title

        // Launch the file chooser
        fileChooser.chooseFile(configuration, new NativeFileChooserCallback() {
            @Override
            public void onFileChosen(FileHandle file) {
                try {
                    // Parse the selected map file and initialize the game
                    map = GameMap.parseMap(file.readString(), BomberQuestGame.this);
                    goToGame();
                } catch (Exception e) {
                    Gdx.app.error("MapLoader", "Error loading map: " + e.getMessage(), e);
                }
            }

            @Override
            public void onCancellation() {
                Gdx.app.log("MapLoader", "Map loading was canceled by the user.");
            }

            @Override
            public void onError(Exception e) {
                Gdx.app.error("MapLoader", "Error during file selection: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Returns the UI skin for styling.
     *
     * @return The Skin object.
     */
    public Skin getSkin() {
        return skin;
    }

    /**
     * Returns the shared SpriteBatch for rendering.
     *
     * @return The SpriteBatch object.
     */
    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    /**
     * Returns the current game map.
     *
     * @return The GameMap object.
     */
    public GameMap getMap() {
        return map;
    }

    /**
     * Disposes of global resources when the game is closed.
     */
    @Override
    public void dispose() {
        // Dispose of the current screen and global resources
        if (getScreen() != null) {
            getScreen().hide();
            getScreen().dispose();
        }
        spriteBatch.dispose();
        skin.dispose();
    }
}
