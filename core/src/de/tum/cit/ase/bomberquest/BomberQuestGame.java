package de.tum.cit.ase.bomberquest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.screen.GameScreen;
import de.tum.cit.ase.bomberquest.screen.MenuScreen;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;

/**
 * The BomberQuestGame class represents the core of the Bomber Quest game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class BomberQuestGame extends Game {

    private SpriteBatch spriteBatch;
    private Skin skin;
    private final NativeFileChooser fileChooser;

    public BomberQuestGame(NativeFileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("skin/craftacular/craftacular-ui.json"));

        // Play background music
        MusicTrack.BACKGROUND.play();

        // Start on menu
        goToMenu();
    }

    public void goToMenu() {
        setScreen(new MenuScreen(this));
    }

    public void goToGame() {
        setScreen(new GameScreen(this, "maps/map.properties")); 
        // We pass the path to the map file. 
        // 'GameScreen' will parse it into a TileMap, create all objects, etc.
    }

    public void goToGame(String mapPath) {
        // Overloaded to start the game with a custom map file
        setScreen(new GameScreen(this, mapPath));
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public Skin getSkin() {
        return skin;
    }

    public void openMapFileChooser() {
    NativeFileChooserConfiguration config = new NativeFileChooserConfiguration();
    // Optional: You can set an initial directory where to open, e.g.:
    config.directory = Gdx.files.internal("maps");

    fileChooser.chooseFile(config, new NativeFileChooserCallback() {
        @Override
        public void onFileChosen(FileHandle file) {
            // We must schedule LibGDX actions on its main thread:
            Gdx.app.postRunnable(() -> {
                // Start the game with the chosen file path
                goToGame(file.path());
            });
        }

        @Override
        public void onCancellation() {
            Gdx.app.postRunnable(() -> {
                System.out.println("User canceled file selection.");
            });
        }

        @Override
        public void onError(Exception ex) {
            Gdx.app.postRunnable(() -> {
                ex.printStackTrace();
            });
        }
    });
}

    @Override
    public void dispose() {
        super.dispose();
        spriteBatch.dispose();
        skin.dispose();
    }

    @Override
    public void setScreen(Screen screen) {
        Screen previous = getScreen();
        super.setScreen(screen);
        // If we are switching away from a screen that is *not* the GameScreen, or
        // if we truly want to discard it, we can dispose. Otherwise, keep it.
        if (previous != null
            && !(previous instanceof GameScreen)) { 
            previous.dispose();
        }
    }
}
