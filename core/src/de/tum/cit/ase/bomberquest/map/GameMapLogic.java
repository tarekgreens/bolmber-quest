package de.tum.cit.ase.bomberquest.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.tum.cit.ase.bomberquest.audio.SoundEffects;
import de.tum.cit.ase.bomberquest.texture.Textures;

/**
 * Manages the tile-based logic: bombs, enemies, powerups, 
 * destruction of walls, killing player, etc.
 */
public class GameMapLogic {

    private final TileMap tileMap;
    private Player player;

    private final List<Bomb> bombs = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();

    private boolean gameOver = false;
    private String gameOverReason = "";
    private boolean exitUnlocked = false;

    public GameMapLogic(TileMap tileMap, Player player) {
        this.tileMap = tileMap;
        this.player = player;
    }

    public void addBomb(Bomb bomb) {
        bombs.add(bomb);
    }

    /**
     * Called each frame from GameScreen.
     */
    public void update(float delta) {
        if (gameOver) return; // Freeze logic if game ended

        // 1) Update bombs in a safe iterator loop
        Iterator<Bomb> bombIt = bombs.iterator();
        while (bombIt.hasNext()) {
            Bomb b = bombIt.next();
            b.update(delta);
            if (b.isDone()) {
                bombIt.remove();
                player.bombExploded();
            }
        }
        // => While bombs update, any enemies killed by handleExplosionTile(...) are removed from enemies.

        // 2) Now update enemies AFTER bombs are finished,
        //    using a simple index loop. That way, if handleExplosionTile
        //    removed some enemies, we won't trigger concurrency issues.
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);
            e.update(delta);

            // If you ever needed to remove an enemy from inside e.update(...),
            // you would mark it dead in Enemy and remove it here:
            //   if (e.isDead()) enemies.remove(i);
        }

        // 3) Check if player stands on a power‐up
        Iterator<PowerUp> pIt = powerUps.iterator();
        while (pIt.hasNext()) {
            PowerUp p = pIt.next();
            // If the player is on that tile => apply powerup => remove
            if (p.getTileX() == player.getTileX() && p.getTileY() == player.getTileY()) {
                if (p.getPowerType() == 5) player.increaseBombCapacity();
                else if (p.getPowerType() == 6) player.increaseBombRadius();

                // PLAY SOUND
                SoundEffects.POWER_UP.play();
                pIt.remove();
            }
        }

        // Check if all enemies are dead => unlock exit
        if (!exitUnlocked && enemies.isEmpty()) {
            exitUnlocked = true;
            System.out.println("All enemies dead -> exit is unlocked!");
        }

        if (exitUnlocked) {
            if (player.getTileX() == tileMap.getExitX() 
                && player.getTileY() == tileMap.getExitY()) {
                // player stands on the exit => victory
                victory();
            }
        }
    }

    private void victory() {
        this.gameOver = true;
        this.gameOverReason = "Victory";
        System.out.println("Player reached the exit => Victory!");
    }

    /**
     * The bomb calls this to damage the tile (x,y). 
     * If it's an indestructible wall => returns true => stop blast.
     * If it's destructible => destroy & return false => keep going
     * Also kills any enemy or the player if they occupy that tile.
     */
    public boolean handleExplosionTile(int x, int y) {
        // Out of bounds => block
        if (x<0 || x>=tileMap.getWidth()||y<0||y>=tileMap.getHeight()) {
            return true;
        }

        int t = tileMap.getTile(x,y);
        if (t == TileMap.WALL_INDESTRUCTIBLE) {
            // block
            return true;
        } else if (t == TileMap.WALL_DESTRUCTIBLE) {
            // This destroys the wall => now we can see if there's a hidden power-up
            tileMap.destroyWall(x, y);

            // See if there's a powerUp spawn at that tile
            TileMap.PowerUpSpawn hiddenPU = tileMap.popPowerUpSpawnAt(x, y);
            if (hiddenPU != null) {
                // Create the actual PowerUp object
                TextureRegion pSprite = (hiddenPU.type == 5)
                                        ? Textures.POWER_UP_GREEN
                                        : Textures.POWER_UP_RED;
                PowerUp p = new PowerUp(x, y, hiddenPU.type, pSprite);
                powerUps.add(p);
            }

            // we do not block further
            return false;
        }

        // kill enemies in that tile
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);
            if (e.getTileX() == x && e.getTileY() == y) {
                enemies.remove(i);
            }
        }

        // kill player if in that tile
        if (player.getTileX()==x && player.getTileY()==y) {
            killPlayer("Bomb explosion");
        }

        return false;
    }

    public void killPlayer(String reason) {
        this.gameOver = true;
        this.gameOverReason = reason;
        System.out.println("Player died: " + reason);

        SoundEffects.DIE.play();
    }

    public boolean isGameOver() {
        return gameOver;
    }
    public String getGameOverReason(){return gameOverReason;}

    public Player getPlayer() { return player; }
    public GameMapLogic setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public List<Enemy> getEnemies() { return enemies; }
    public List<Bomb> getBombs() { return bombs; }
    public List<PowerUp> getPowerUps(){ return powerUps; }

    // Add convenience for spawning
    public void addEnemy(Enemy e) {
        enemies.add(e);
    }
    public void addPowerUp(PowerUp p){
        powerUps.add(p);
    }

    public boolean isExitUnlocked() {
        return exitUnlocked;
    }
}
