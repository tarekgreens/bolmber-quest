package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.ase.bomberquest.BomberQuestGame;

public class PauseMenuScreen implements Screen {

    private final BomberQuestGame game;
    private final Screen oldGameScreen; // The screen we paused
    private Stage stage;

    public PauseMenuScreen(BomberQuestGame game, Screen oldGameScreen) {
        this.game = game;
        this.oldGameScreen = oldGameScreen;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(new OrthographicCamera()));
        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Title label
        Label title = new Label("PAUSED", game.getSkin(), "title");
        root.add(title).padBottom(40).row();

        // "Continue" button
        TextButton btnContinue = new TextButton("Continue", game.getSkin());
        root.add(btnContinue).padBottom(15).row();
        btnContinue.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Return to the old game screen
                game.setScreen(oldGameScreen);
            }
        });


        // "Exit to Menu" button
        TextButton btnExit = new TextButton("Exit to Menu", game.getSkin());
        root.add(btnExit).padBottom(15).row();
        btnExit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Return to main menu screen
                game.goToMenu();
            }
        });
    }

    @Override
    public void render(float delta) {
        // Clears screen behind stage automatically if needed
        // or you can do: ScreenUtils.clear(Color.BLACK);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    // Don't need these methods to do anything:
    @Override
    public void pause() { }
    @Override
    public void resume() { }
    @Override
    public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
