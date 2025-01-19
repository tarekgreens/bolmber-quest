package de.tum.cit.ase.bomberquest.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages the tile-based logic: bombs, enemies, powerups, 
 * destruction of walls, killing player, etc.
 */
public class GameMapLogic {

    private final TileMap tileMap;
    private final Player player;

    private final List<Bomb> bombs = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();

    private boolean gameOver = false;
    private String gameOverReason = "";

    public GameMapLogic(TileMap tileMap, Player player) {
        this.tileMap = tileMap;
        this.player = player;
    }

    public void addBomb(Bomb bomb) {
        bombs.add(bomb);
    }

    public void bombHasExploded(Bomb bomb) {
        bombs.remove(bomb);
        player.bombExploded(); 
    }

    /**
     * Called each frame from GameScreen.
     */
    public void update(float delta) {
        if (gameOver) return; // Freeze logic if game ended

        // 1) Update bombs
        for (Bomb b : bombs) {
            b.update(delta);
        }

        // 2) Update enemies
        for (Enemy e : enemies) {
            e.update(delta);
        }

        // 3) Check if player stands on a power-up
        Iterator<PowerUp> it = powerUps.iterator();
        while(it.hasNext()) {
            PowerUp p = it.next();
            if (p.getTileX() == player.getTileX() && p.getTileY() == player.getTileY()) {
                // pick it up
                if (p.getPowerType()==5) { // concurrency
                    player.increaseBombCapacity();
                } else if (p.getPowerType()==6) { // blast radius
                    player.increaseBombRadius();
                }
                it.remove();
            }
        }
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
            // destroy it
            tileMap.destroyWall(x, y);
            // we do not block further
            return false;
        }

        // kill enemies in that tile
        for (int i=enemies.size()-1; i>=0; i--) {
            Enemy e = enemies.get(i);
            if (e.getTileX()==x && e.getTileY()==y) {
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
    }

    public boolean isGameOver() {
        return gameOver;
    }
    public String getGameOverReason(){return gameOverReason;}

    public Player getPlayer() { return player; }

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
}
