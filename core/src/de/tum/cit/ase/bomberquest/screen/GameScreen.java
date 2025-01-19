package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
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

    private TileMap tileMap;
    private GameMapLogic logic; 
    private Player player;

    private float tileSizePx = 16f; // each tile is 16×16

    public GameScreen(BomberQuestGame game, String mapFile) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        batch = game.getSpriteBatch();

        // 1) Load the tileMap from map.properties
        tileMap = new TileMap(40,24); // if you know it's 40×24
        tileMap.loadFromProperties(mapFile);

        // 2) Create a Player at the tileMap's entrance
        TextureRegion playerSprite = Textures.ENEMY; 
        // Suppose you have Textures.CHARACTER or something
        player = new Player(tileMap, tileMap.getEntranceX(), tileMap.getEntranceY(), playerSprite, null);

        // 3) Create the logic that holds bombs, enemies, powerups
        logic = new GameMapLogic(tileMap, player);
        // Now that we have logic, fix the player's logic reference
        // (We used a 'null' above, let's fix that)
        player = new Player(tileMap, tileMap.getEntranceX(), tileMap.getEntranceY(), playerSprite, logic);
        logic.getPlayer().bombExploded(); // ignore this line if desired, just re-init

        // Actually re-create the player with the correct reference
        logic = new GameMapLogic(tileMap, player);

        // 4) Spawn enemies from tileMap enemySpawns
        for (TileMap.EnemySpawn es : tileMap.getEnemySpawns()) {
            TextureRegion eSprite = Textures.ENEMY; 
            Enemy e = new Enemy(tileMap, es.x, es.y, eSprite, logic);
            logic.addEnemy(e);
        }

        // 5) Spawn powerUps
        for (TileMap.PowerUpSpawn pus : tileMap.getPowerUpSpawns()) {
            TextureRegion pSprite = (pus.type==5) ? Textures.POWER_UP_GREEN : Textures.POWER_UP_RED;
            PowerUp p = new PowerUp(pus.x, pus.y, pus.type, pSprite);
            logic.addPowerUp(p);
        }
    }

    @Override
    public void render(float delta) {
        if (logic.isGameOver()) {
            // if game is over, show message or go menu
            System.out.println("GAME OVER => " + logic.getGameOverReason());
            game.goToMenu();
            return;
        }

        ScreenUtils.clear(Color.BLACK);

        // 1) Update
        player.update();
        logic.update(delta);

        // 2) Update camera
        clampCamera80Percent();

        // 3) Render
        camera.update();
        batch.setProjectionMatrix(camera.combined);
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

        // draw bombs
        for (Bomb b : logic.getBombs()) {
            if (!b.isExploded()) {
                float px = b.getX()*tileSizePx;
                float py = b.getY()*tileSizePx;
                // we can draw a bomb sprite
                batch.draw(Textures.BOMB, px, py);
            }
        }

        // draw enemies
        for (Enemy e : logic.getEnemies()) {
            e.render(batch, tileSizePx);
        }

        // draw player
        player.render(batch, tileSizePx);

        batch.end();
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
                return Textures.FLOWERS; 
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,false);
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
    public void dispose() {}
}
