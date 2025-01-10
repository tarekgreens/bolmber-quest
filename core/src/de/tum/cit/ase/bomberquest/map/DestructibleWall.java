package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.Texture;

public class DestructibleWall extends WallPath{
    private boolean destructible = true;
    private boolean hasExit;

    public DestructibleWall(float x, float y, float width, float height, Texture texture, boolean hasExit) {
        super(x, y, width, height, texture);
        this.hasExit = hasExit;
    }

    @Override
    public boolean isDestructible() {
        return destructible;
    }

    @Override
    public void destroy() {
        if (destructible) {
            destructible = false;
            if (hasExit) {
                texture = new Texture("exit_texture.png"); // Reveal exit
            } else {
                texture = new Texture("path_texture.png"); // Replace with path texture
            }
        }
    }

    public boolean hasExit() {
        return hasExit;
    }
}

