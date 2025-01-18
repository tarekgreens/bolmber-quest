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
 * Represents an enemy in the game.
 * Enemies can move and interact with bombs and players.
 */
public class Enemy extends Object implements Drawable {


    private final Body body;
    private final TextureRegion textureRegion;
    private static final float ENEMY_SIZE = 1.0f;

    public Enemy() {
        super(0, 0, null);
        this.textureRegion = null;
        this.body = null;
    }

    public Enemy(World world, float x, float y, int type, TextureRegion textureRegion) {
        super(x, y, null);
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

