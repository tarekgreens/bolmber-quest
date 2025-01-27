package de.tum.cit.ase.bomberquest.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * Manages short sound effects in the game.
 */
public enum SoundEffects {

    BOMB_PLACE("fuse.ogg", 1.0f),
    BOMB_EXPLODE("blast.wav", 1.0f),
    POWER_UP("powerup.wav", 1.0f),
    WIN("win.wav", 1.0f),
    DIE("die.wav", 1.0f);

    private final Sound sound;
    private final float volume;

    SoundEffects(String fileName, float volume) {
        this.sound = Gdx.audio.newSound(Gdx.files.internal("audio/" + fileName));
        this.volume = volume;
    }

    public void play() {
        sound.play(volume);
    }

    // Overload if you want to set a different volume
    public void play(float volume) {
        sound.play(volume);
    }
}
