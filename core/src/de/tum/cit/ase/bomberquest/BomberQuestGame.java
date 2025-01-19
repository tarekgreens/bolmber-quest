package de.tum.cit.ase.bomberquest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.screen.GameScreen;
import de.tum.cit.ase.bomberquest.screen.MenuScreen;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

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

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public Skin getSkin() {
        return skin;
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
        if (previous != null) {
            previous.dispose();
        }
    }
}
