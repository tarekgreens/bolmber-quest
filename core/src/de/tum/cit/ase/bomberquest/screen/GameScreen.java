package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.map.*;
import de.tum.cit.ase.bomberquest.texture.Textures;

public class GameScreen implements Screen {

    private final BomberQuestGame game;

    // We'll use a 320×240 "world size" to start with. If the window is bigger,
    // the user just sees more of the map.
    private static final float WORLD_WIDTH = 320;
    private static final float WORLD_HEIGHT = 240;

    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private TileMap tileMap;
    private GameMapLogic logic; 
    private Player player;
    private Hud hud;

    private float tileSizePx = 16f; // each tile is 16×16
    private float timeLeft = 120f;

    public GameScreen(BomberQuestGame game, String mapFile) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        batch = game.getSpriteBatch();

        shapeRenderer = new ShapeRenderer();

        // 1) Load the tileMap from map.properties
        tileMap = new TileMap(40,24); // if you know it's 40×24
        tileMap.loadFromProperties(mapFile);

        // 3) Create the logic that holds bombs, enemies, powerups
        logic = new GameMapLogic(tileMap, player);

        // Suppose you have Textures.CHARACTER or something
        player = new Player(tileMap, tileMap.getEntranceX(), tileMap.getEntranceY(), logic);
        logic.setPlayer(player);

        // 4) Spawn enemies from tileMap enemySpawns
        for (TileMap.EnemySpawn es : tileMap.getEnemySpawns()) {
            TextureRegion eSprite = Textures.ENEMY; 
            Enemy e = new Enemy(tileMap, es.x, es.y, logic);
            logic.addEnemy(e);
        }

        // 5) Spawn powerUps
        for (TileMap.PowerUpSpawn pus : tileMap.getPowerUpSpawns()) {
            TextureRegion pSprite = (pus.type==5) ? Textures.POWER_UP_GREEN : Textures.POWER_UP_RED;
            PowerUp p = new PowerUp(pus.x, pus.y, pus.type, pSprite);
            logic.addPowerUp(p);
        }

        // Instantiate HUD (reusing the same SpriteBatch and a simple BitmapFont):
        // If you already have a separate font, pass that instead of a new BitmapFont().
        this.hud = new Hud(game.getSpriteBatch(), new BitmapFont());
    }

    @Override
    public void render(float delta) {
            // If the player hits ESC, pause the game
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseMenuScreen(game, this));
            return; // skip the rest of the render
        }
        
        if (logic.isGameOver()) {
            game.setScreen(
                new GameOverScreen(
                    game,
                    logic.getGameOverReason(),  // e.g. "Bomb explosion" or "Timer expired"
                    game.getSkin()
                )
            );
            return;
        }

        // -- 1) Update countdown timer
        timeLeft -= delta;
        if (timeLeft <= 0) {
            // Time ran out -> end the game
            System.out.println("Game Over - Timer has expired!");
            game.goToMenu();
            return;
        }

        // -- 2) Update gameplay logic
        player.update(delta);
        logic.update(delta);

        // -- 3) Update camera and draw map/objects
        clampCamera80Percent();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        ScreenUtils.clear(Color.BLACK);

        batch.begin();

        // draw the tile-based map
        for (int y=0; y<tileMap.getHeight(); y++) {
            for (int x=0; x<tileMap.getWidth(); x++) {
                int t = tileMap.getTile(x,y);
                TextureRegion region = getTileTexture(t);
                float px = x*tileSizePx;
                float py = y*tileSizePx;
                batch.draw(region, px, py);
            }
        }

        // draw powerUps
        for (PowerUp p : logic.getPowerUps()) {
            p.render(batch, tileSizePx);
        }

        for (Bomb b : logic.getBombs()) {
            b.renderFuse(batch, tileSizePx); // only the fuse animation
        }

        // draw enemies
        for (Enemy e : logic.getEnemies()) {
            e.render(batch, tileSizePx);
        }

        // draw player
        player.render(batch, tileSizePx);

        batch.end();

        // B) Now draw the shape-based explosions in one pass
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Bomb b : logic.getBombs()) {
            b.renderExplosion(shapeRenderer, tileSizePx);
        }

        shapeRenderer.end();

                // -- 4) Render the HUD on top of everything
        // Number of enemies left
        int enemiesLeft = logic.getEnemies().size();
        // For a simple “exit unlocked” check, assume “unlocked if all enemies are dead”
        boolean exitUnlocked = (enemiesLeft == 0);

        hud.render(
            player,
            timeLeft,
            enemiesLeft,
            exitUnlocked
        );
    }

    /**
     * Clamps the camera so the player is within the middle 80% of the screen,
     * and we don't show beyond the map edges.
     */
    private void clampCamera80Percent() {
        float camW = viewport.getWorldWidth();
        float camH = viewport.getWorldHeight();

        float marginX = camW*0.1f;
        float marginY = camH*0.1f;

        // player's pixel coords
        float playerPxX = player.getTileX()*tileSizePx;
        float playerPxY = player.getTileY()*tileSizePx;

        float leftLimit = camera.position.x - camW/2f + marginX;
        float rightLimit= camera.position.x + camW/2f - marginX;
        float downLimit = camera.position.y - camH/2f + marginY;
        float upLimit   = camera.position.y + camH/2f - marginY;

        // adjust horizontally
        if (playerPxX < leftLimit) {
            camera.position.x -= (leftLimit - playerPxX);
        } else if (playerPxX > rightLimit) {
            camera.position.x += (playerPxX - rightLimit);
        }

        // adjust vertically
        if (playerPxY < downLimit) {
            camera.position.y -= (downLimit - playerPxY);
        } else if (playerPxY > upLimit) {
            camera.position.y += (playerPxY - upLimit);
        }

        // clamp so we don't go outside map
        float mapW = tileMap.getWidth()*tileSizePx;
        float mapH = tileMap.getHeight()*tileSizePx;

        float halfW = camW/2f;
        float halfH = camH/2f;

        // clamp X
        if (camW < mapW) {
            if (camera.position.x < halfW) camera.position.x=halfW;
            if (camera.position.x > mapW-halfW) camera.position.x= mapW-halfW;
        } else {
            // if map is narrower than cam => center
            camera.position.x = mapW/2f;
        }

        // clamp Y
        if (camH < mapH) {
            if (camera.position.y < halfH) camera.position.y=halfH;
            if (camera.position.y > mapH-halfH) camera.position.y= mapH-halfH;
        } else {
            camera.position.y = mapH/2f;
        }
    }

    private TextureRegion getTileTexture(int tileType){
        // 0 => floor, 1 => inde, 2 => destructive
        switch(tileType) {
            case TileMap.WALL_INDESTRUCTIBLE: return Textures.INDEST_WALL;
            case TileMap.WALL_DESTRUCTIBLE: return Textures.DEST_WALL;
            default: 
                // treat as floor
                return Textures.TILES; 
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,false);
        // Also let the HUD know about the new screen size
        hud.resize(width, height);
    }

    @Override
    public void show() {}
    @Override
    public void hide() {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
