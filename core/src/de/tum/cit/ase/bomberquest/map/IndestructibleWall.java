package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.Texture;

public class IndestructibleWall extends WallPath{
    public IndestructibleWall(float x, float y, float width, float height, Texture texture) {
        super(x, y, width, height, texture);
    }

    @Override
    public boolean isDestructible() {
        return false;
    }

    @Override
    public void destroy() {

    }
}
