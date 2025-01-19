package de.tum.cit.ase.bomberquest.texture;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Contains all texture constants used in the game.
 * It is good practice to keep all textures and animations in constants to avoid loading them multiple times.
 * These can be referenced anywhere they are needed.
 */
public class Textures {
    
    public static final TextureRegion FLOWERS = SpriteSheet.BASIC_TILES.at(2, 5);
    public static final TextureRegion DEST_WALL = SpriteSheet.BASIC_TILES.at(1,4 );
    public static final TextureRegion INDEST_WALL = SpriteSheet.BASIC_TILES.at(1,6 );
    public static final TextureRegion CHEST = SpriteSheet.BASIC_TILES.at(5, 5);
    public static final TextureRegion ENTRANCE = SpriteSheet.BASIC_TILES.at(4, 6);
    public static final TextureRegion EXIT = SpriteSheet.BASIC_TILES.at(2,4);
    public static final TextureRegion POWER_UP_GREEN = SpriteSheet.BASIC_TILES.at(3,4);
    public static final TextureRegion POWER_UP_RED = SpriteSheet.BASIC_TILES.at(3,2);
    public static final TextureRegion ENEMY = SpriteSheet.ORIGINAL_BOMBERMAN.at(22,1);
    public static final TextureRegion BOMB = SpriteSheet.BASIC_TILES.at(3, 3);
    
}

