package de.tum.cit.ase.bomberquest.texture;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Contains all animation constants used in the game.
 * It is good practice to keep all textures and animations in constants
 * to avoid loading them multiple times.
 * These can be referenced anywhere they are needed.
 */
public class Animations {

    public static final Animation<TextureRegion> PLAYER_DOWN;
    public static final Animation<TextureRegion> PLAYER_LEFT;
    public static final Animation<TextureRegion> PLAYER_RIGHT;
    public static final Animation<TextureRegion> PLAYER_UP;

    public static final Animation<TextureRegion> ENEMY_DOWN;
    public static final Animation<TextureRegion> ENEMY_LEFT;
    public static final Animation<TextureRegion> ENEMY_RIGHT;
    public static final Animation<TextureRegion> ENEMY_UP;

    // Bomb fuse: 3 frames (16x16) from top row in BOMB_SHEET
    public static final Animation<TextureRegion> BOMB_FUSE;
    // Bomb blast: 4 frames (32x32) from BLAST_SHEET
    public static final Animation<TextureRegion> BOMB_BLAST;

    static {
        // We have 4 frames across columns 5..8
        // Row 1 => down, Row 2 => left, Row 3 => right, Row 4 => up
    
        TextureRegion[] downFrames = new TextureRegion[4];
        for (int col = 8; col <= 11; col++) {
            downFrames[col - 8] = SpriteSheet.MOBS.at(1, col);
        }
        PLAYER_DOWN = new Animation<>(0.1f, downFrames);
    
        TextureRegion[] leftFrames = new TextureRegion[4];
        for (int col = 8; col <= 11; col++) {
            leftFrames[col - 8] = SpriteSheet.MOBS.at(2, col);
        }
        PLAYER_LEFT = new Animation<>(0.1f, leftFrames);
    
        TextureRegion[] rightFrames = new TextureRegion[4];
        for (int col = 8; col <= 11; col++) {
            rightFrames[col - 8] = SpriteSheet.MOBS.at(3, col);
        }
        PLAYER_RIGHT = new Animation<>(0.1f, rightFrames);
    
        TextureRegion[] upFrames = new TextureRegion[4];
        for (int col = 8; col <= 11; col++) {
            upFrames[col - 8] = SpriteSheet.MOBS.at(4, col);
        }
        PLAYER_UP = new Animation<>(0.1f, upFrames);
    }

    static {
        // Single-frame "animation" from row=5, col=1 => ENEMY_DOWN
        TextureRegion[] downFrames = new TextureRegion[1];
        downFrames[0] = SpriteSheet.MOBS.at(5, 1);
        ENEMY_DOWN = new Animation<>(0.1f, downFrames);
    
        // Single-frame from row=6, col=1 => ENEMY_LEFT
        TextureRegion[] leftFrames = new TextureRegion[1];
        leftFrames[0] = SpriteSheet.MOBS.at(6, 1);
        ENEMY_LEFT = new Animation<>(0.1f, leftFrames);
    
        // Single-frame from row=7, col=1 => ENEMY_RIGHT
        TextureRegion[] rightFrames = new TextureRegion[1];
        rightFrames[0] = SpriteSheet.MOBS.at(7, 1);
        ENEMY_RIGHT = new Animation<>(0.1f, rightFrames);
    
        // Single-frame from row=8, col=1 => ENEMY_UP
        TextureRegion[] upFrames = new TextureRegion[1];
        upFrames[0] = SpriteSheet.MOBS.at(8, 1);
        ENEMY_UP = new Animation<>(0.1f, upFrames);
    }

    static {
        /*
         * Suppose your bombs appear in row=1 of original-bomberman.png
         * (the top row), columns 1..3, each 16x16. 
         */
        TextureRegion[] fuseFrames = new TextureRegion[3];
        for (int col = 1; col <= 3; col++) {
            fuseFrames[col - 1] = SpriteSheet.BOMB_SHEET.at(4, col);
        }
        BOMB_FUSE = new Animation<>(0.15f, fuseFrames);
        BOMB_FUSE.setPlayMode(Animation.PlayMode.LOOP);

        /*
         * Suppose the big cross shapes each occupy a 32x32 tile
         * in BLAST_SHEET. Let's guess they are in rows 1..4, col=1. 
         * (So 4 total frames of the explosion.)
         */
        TextureRegion[] blastFrames = new TextureRegion[4];
        for (int row = 1; row <= 4; row++) {
            blastFrames[row - 1] = SpriteSheet.BLAST_SHEET.at(row, 1);
        }
        BOMB_BLAST = new Animation<>(0.07f, blastFrames);
        BOMB_BLAST.setPlayMode(Animation.PlayMode.NORMAL); 
        // We only want to play the explosion once
    }
}
