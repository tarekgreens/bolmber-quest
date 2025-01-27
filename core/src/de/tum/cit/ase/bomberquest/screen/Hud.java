package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.tum.cit.ase.bomberquest.map.Exit;
import de.tum.cit.ase.bomberquest.map.Player;

/**
 * A Heads-Up Display (HUD) that displays information on the screen.
 * It uses a separate camera so that it is always fixed on the screen.
 */
public class Hud {
    private OrthographicCamera hudCamera;
    private SpriteBatch batch;
    private BitmapFont font;

    public Hud(SpriteBatch batch, BitmapFont font) {
        this.batch = batch;
        this.font = font;
        this.font.getData().setScale(2.5f);
        hudCamera = new OrthographicCamera();
    }

    public void render(Player player, float timeLeft, int enemiesLeft, boolean exitUnlocked) {
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        // Build a single line of text
        // You can break this into two lines or more if you want to split it.
        String hudLine = String.format(
            "BombRadius: %d   BombCap: %d   TimeLeft: %d   Enemies: %d   Exit: %s",
            player.getBombRadius(),
            player.getBombCapacity(),
            (int) timeLeft,
            enemiesLeft,
            exitUnlocked ? "Unlocked" : "Locked"
        );

        // Draw near the top-left. Subtract e.g. 10px from the top so it’s not offscreen
        float margin = 10;
        font.getData().setScale(2.5f);
        font.draw(batch, hudLine, margin, hudCamera.viewportHeight - margin);

        batch.end();
    }

    public void resize(int width,int height) {
        hudCamera.setToOrtho(false,width,height);
    }
}
