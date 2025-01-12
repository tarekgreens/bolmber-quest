package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Represents the exit point in the game.
 */
public class Exit {

    private final Body body;
    private final TextureRegion textureRegion;
    private static final float EXIT_SIZE = 1.0f;

    public Exit(World world, float x, float y, TextureRegion textureRegion) {
        this.textureRegion = textureRegion;


        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);


        PolygonShape shape = new PolygonShape();
        shape.setAsBox(EXIT_SIZE / 2, EXIT_SIZE / 2);
        body.createFixture(shape, 0.0f);
        shape.dispose();
    }

    /**
     * Renders the exit on the screen.
     *
     * @param batch The SpriteBatch used for rendering.
     */
    public void render(SpriteBatch batch) {
        Vector2 position = body.getPosition();
        batch.draw(textureRegion, position.x - EXIT_SIZE / 2, position.y - EXIT_SIZE / 2, EXIT_SIZE, EXIT_SIZE);
    }
}
