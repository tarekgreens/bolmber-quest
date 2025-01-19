package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * Represents an enemy in the game.
 * Enemies can move and interact with bombs and players.
 */
public class Enemy extends Object implements Drawable {


    private final Body body;
    private final TextureRegion textureRegion;
    private static final float ENEMY_SIZE = 1.0f;
    private float dirChangeTimer = 0f;
    private final float DIR_CHANGE_INTERVAL = 1.0f;  // how often to pick a new direction
    private Vector2 currentVelocity = new Vector2();

    private float elapsedTime;


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
        body.setSleepingAllowed(false);

                CircleShape circle = new CircleShape();
        circle.setRadius(0.3f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.isSensor = false;

        body.createFixture(fixtureDef);
        body.setUserData(this);
        circle.dispose();
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


    public void update(float deltaTime, GameMap map) {
        this.elapsedTime += deltaTime;
        // 1) Possibly pick a new random direction
        dirChangeTimer += deltaTime;
        if (dirChangeTimer >= DIR_CHANGE_INTERVAL) {
            dirChangeTimer = 0f;

            // random integer from 0..3 => up/down/left/right
            int dir = com.badlogic.gdx.math.MathUtils.random(3);
            switch (dir) {
                case 0: currentVelocity.set(2, 0); break;  // right
                case 1: currentVelocity.set(-2, 0); break; // left
                case 2: currentVelocity.set(0, 2); break;  // up
                case 3: currentVelocity.set(0, -2); break; // down
            }
        }

        // 2) Move according to currentVelocity
        body.setLinearVelocity(currentVelocity.x, currentVelocity.y);
        System.out.println("Enemy velocity: " + body.getLinearVelocity());
        System.out.println("Enemy position: " + body.getPosition());
        body.setAwake(true);

        // 3) Check if enemy touches the player => kill the player
        Player player = map.getPlayer();
        if (player != null) {
            float dist = body.getPosition().dst(player.getHitbox().getPosition());
            // 0.8 or 0.5 => depends how "close" is a collision.
            // We can also rely on Box2D collisions, but let's do a simple distance check.
            if (dist < 0.5f) {
                System.out.println("Player killed by enemy at " + body.getPosition());
                // e.g. do some map.gameOver() or kill the player
            }
        }
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

