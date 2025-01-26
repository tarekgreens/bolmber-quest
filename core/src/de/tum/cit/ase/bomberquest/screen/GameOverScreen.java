package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import de.tum.cit.ase.bomberquest.BomberQuestGame;

/**
 * The GameOverScreen class represents a dedicated screen displayed when the player loses.
 */
public class GameOverScreen implements Screen {

    private final BomberQuestGame game;
    private final String gameOverReason;
    private final Skin skin;
    private Stage stage;

    /**
     * Creates a new GameOverScreen.
     *
     * @param game           The main game instance.
     * @param gameOverReason Reason for the game over (e.g. "Bomb explosion" or "Time ran out").
     * @param skin           The UI skin for styling widgets.
     */
    public GameOverScreen(BomberQuestGame game, String gameOverReason, Skin skin) {
        this.game = game;
        this.gameOverReason = gameOverReason;
        this.skin = skin;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Main "Game Over" label
        Label gameOverLabel = new Label("GAME OVER", skin, "title");
        rootTable.add(gameOverLabel).padBottom(20).row();

        // Optional: show the reason the player died
        // (If you don't want to show a reason, you can remove this label.)
        if (gameOverReason != null && !gameOverReason.isEmpty()) {
            Label reasonLabel = new Label("Reason: " + gameOverReason, skin);
            rootTable.add(reasonLabel).padBottom(40).row();
        }

        // Button to go back to the main menu
        TextButton menuButton = new TextButton("Return to Main Menu", skin);
        rootTable.add(menuButton).width(300).height(50).row();

        // When button clicked => go to the menu
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.goToMenu();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() { }
    @Override
    public void resume() { }
    @Override
    public void hide() { }
    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
    }
}
