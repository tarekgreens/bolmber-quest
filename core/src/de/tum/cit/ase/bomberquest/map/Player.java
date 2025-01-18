package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * Represents the player character in the game.
 * The player has a hitbox, so it can collide with other objects in the game.
 */
public class Player implements Drawable {

    /** Total time elapsed since the game started. We use this for calculating the player movement and animating it. */
    private float elapsedTime;

    /** The Box2D hitbox of the player, used for position and collision detection. */
    private final Body hitbox;

    // New fields for bombs
    private int bombCapacity = 10;    // how many bombs can be active at once
    private int bombsCurrentlyPlaced = 0;
    private int bombRadius = 1;       // default radius

    // A reference back to the GameMap so we can place bombs there.
    private final GameMap map;

    public Player(World world, float x, float y, GameMap map) {
        this.map = map;
        this.hitbox = createHitbox(world, x, y);
    }

    /**
     * Creates a Box2D body for the player.
     * This is what the physics engine uses to move the player around and detect collisions with other bodies.
     *
     * @param world  The Box2D world to add the body to.
     * @param startX The initial X position.
     * @param startY The initial Y position.
     * @return The created body.
     */
    private Body createHitbox(World world, float startX, float startY) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startX, startY);
        Body body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(0.3f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.0f;

        body.createFixture(fixtureDef);
        body.setUserData(this);
        circle.dispose();
        return body;
    }

    /**
     * Handles movement each frame, plus checks if SPACE is pressed to place a bomb.
     */
    public void tick(float frameTime) {
        this.elapsedTime += frameTime;

        // Movement logic
        float xVelocity = 0;
        float yVelocity = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            yVelocity = 2;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            yVelocity = -2;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            xVelocity = -2;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            xVelocity = 2;
        }
        this.hitbox.setLinearVelocity(xVelocity, yVelocity);

        // If pressing SPACE, try to place bomb
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            attemptToPlaceBomb();
        }
    }

    private void attemptToPlaceBomb() {
        // Only place if we haven't exceeded capacity
        if (bombsCurrentlyPlaced < bombCapacity) {
            // Round the player's float position to the nearest tile
            int bombX = Math.round(getHitbox().getPosition().x);
            int bombY = Math.round(getHitbox().getPosition().y);

            // Create and add Bomb to the GameMap
            Bomb newBomb = new Bomb(
                bombX,
                bombY,
                bombRadius,
                System.currentTimeMillis()
            );

            // Actually add the bomb to the map so it can be updated & rendered
            map.addBomb(newBomb);

            bombsCurrentlyPlaced++;
        }
    }

    /**
     * Called by the Bomb when it finishes exploding (so the Player can place another).
     */
    public void bombHasExploded() {
        bombsCurrentlyPlaced = Math.max(0, bombsCurrentlyPlaced - 1);
    }

    // For picking up power-ups type=5 (more bombs) or type=6 (larger radius)
    public void increaseBombCapacity() {
        bombCapacity++;
    }

    public void increaseBombRadius() {
        bombRadius++;
    }

    public Body getHitbox() {
        return hitbox;
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        // Get the frame of the walk down animation that corresponds to the current time.
        return Animations.CHARACTER_WALK_DOWN.getKeyFrame(this.elapsedTime, true);
    }

    @Override
    public float getX() {
        return hitbox.getPosition().x;
    }

    @Override
    public float getY() {
        return hitbox.getPosition().y;
    }

    public void render(SpriteBatch batch) {
        // Draw the player's current sprite at (x, y)
        batch.draw(getCurrentAppearance(), getX(), getY(), 1f, 1f);
    }
}
