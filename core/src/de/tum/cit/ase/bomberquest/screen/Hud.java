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
        hudCamera = new OrthographicCamera();
    }
    
    public void render(Player player) {
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        font.draw(batch, "Bomb Radius: " + player.getBombRadius(), 10, 460);
        font.draw(batch, "Bomb Capacity: " + player.getBombCapacity(), 10, 440);
        batch.end();
    }

    public void resize(int width,int height) {
        hudCamera.setToOrtho(false,width,height);
    }
}
