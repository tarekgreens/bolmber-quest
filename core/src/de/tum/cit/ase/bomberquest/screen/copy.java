package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.tum.cit.ase.bomberquest.BomberQuestGame;

public class copy {
    /**

    package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.tum.cit.ase.bomberquest.BomberQuestGame;

/**
 * The VictoryScreen class represents the screen displayed when the player achieves victory.
 * It provides options to return to the main menu.
 */
    public class VictoryScreen implements Screen {

        private final BomberQuestGame game;
        private final Screen previousScreen;
        private Stage stage;
        private final Skin skin;

        /**
         * Creates a new VictoryScreen.
         *
         * @param game           The main game instance
         * @param previousScreen The previous screen to potentially return to
         * @param skin          The skin used for UI elements
         */
        public VictoryScreen(BomberQuestGame game, Screen previousScreen, Skin skin) {
            this.game = game;
            this.previousScreen = previousScreen;
            this.skin = skin;
        }

        @Override
        public void show() {
            stage = new Stage();
            Table table = new Table();
            table.setFillParent(true);
            stage.addActor(table);
            Gdx.input.setInputProcessor(stage);

            // Create victory label with animation
            Label victoryLabel = new Label("Victory!", skin, "title");
            table.add(victoryLabel).padBottom(80).row();

            // Add blinking animation to the victory label
            victoryLabel.addAction(Actions.forever(Actions.sequence(
                    Actions.fadeIn(0.7f),
                    Actions.fadeOut(0.7f)
            )));

            // Create and add the back to menu button
            TextButton backToMenuButton = new TextButton("Return to Menu", skin);
            table.add(backToMenuButton).padBottom(20).row();

            // Add click listener for the menu button
            backToMenuButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.goToMenu();
                }
            });

            // Optional: Add more stats or information about the victory
            Label statsLabel = new Label("Congratulations! You've completed the level!", skin);
            table.add(statsLabel).padTop(20).row();
        }

        @Override
        public void render(float delta) {
            // Clear the screen
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // Update and draw the stage
            stage.act(delta);
            stage.draw();
        }

        @Override
        public void resize(int width, int height) {
            stage.getViewport().update(width, height, true);
        }

        @Override
        public void pause() {
        }

        @Override
        public void resume() {
        }

        @Override
        public void hide() {
        }

        @Override
        public void dispose() {
            if (stage != null) {
                stage.dispose();
            }
        }
        //test
    }


     **/
}
