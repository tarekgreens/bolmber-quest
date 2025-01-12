package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Represents an enemy in the game.
 * Enemies can move and interact with bombs and players.
 */
public class Enemy {

    private final Body body;
    private final TextureRegion textureRegion;
    private static final float ENEMY_SIZE = 1.0f;

    public Enemy(World world, float x, float y, TextureRegion textureRegion) {
        this.textureRegion = textureRegion;


        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);


        PolygonShape shape = new PolygonShape();
        shape.setAsBox(ENEMY_SIZE / 2, ENEMY_SIZE / 2);
        body.createFixture(shape, 1.0f);
        shape.dispose();
    }

    /**
     * Renders the enemy on the screen.
     *
     * @param batch The SpriteBatch used for rendering.
     */
    public void render(SpriteBatch batch) {
        Vector2 position = body.getPosition();
        batch.draw(textureRegion, position.x - ENEMY_SIZE / 2, position.y - ENEMY_SIZE / 2, ENEMY_SIZE, ENEMY_SIZE);
    }

    /**
     * Updates the enemy's state.
     *
     * @param deltaTime The time elapsed since the last frame.
     */
    public void update(float deltaTime) {

    }

    public Body getBody() {
        return body;
    }
}

