package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * Represents a power-up in the game.
 * Power-ups can be collected by the player to enhance abilities.
 */
public class PowerUp extends Object implements Drawable{

        private final Body body;
        private final TextureRegion textureRegion;
        private final int type; // 5 for concurrent bomb, 6 for blast radius
        private static final float POWERUP_SIZE = 1.0f;

        public PowerUp(World world, float x, float y, int type, TextureRegion textureRegion) {
            super(x, y, null);
            this.type = type;
            this.textureRegion = textureRegion;

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(x, y);


            body = world.createBody(bodyDef);


            PolygonShape shape = new PolygonShape();
            shape.setAsBox(POWERUP_SIZE / 2, POWERUP_SIZE / 2);
            body.createFixture(shape, 0.0f);
            shape.dispose();
        }



        /**
         * Renders the power-up on the screen.
         *
         * @param batch The SpriteBatch used for rendering.
         */

    public void render(SpriteBatch batch) {
        Vector2 position = body.getPosition();
        batch.draw(textureRegion, position.x - POWERUP_SIZE / 2, position.y - POWERUP_SIZE / 2, POWERUP_SIZE, POWERUP_SIZE);
    }



    /**
     * Gets the type of the power-up.
     *
     * @return The type of the power-up.
     */

    public int getType() {
        return type;
    }
    @Override
    public TextureRegion getCurrentAppearance() {
        // just return this.textureRegion
        return textureRegion;
    }

    @Override
    public float getX() {
        return x;  // ‘x’ is inherited from parent
    }

    @Override
    public float getY() {
        return y;
    }

}
