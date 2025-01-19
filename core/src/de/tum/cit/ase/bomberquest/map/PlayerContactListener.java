package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.physics.box2d.*;

public class PlayerContactListener implements ContactListener {

    private final GameMap map;

    public PlayerContactListener(GameMap map) {
        this.map = map;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // Each fixture can have a userData or Body can have userData
        // We set the "body.setUserData(this)" to reference the Object or Player or Enemy instance
        Object userA = (Object) fixA.getBody().getUserData();
        Object userB = (Object) fixB.getBody().getUserData();

        handleCollision(userA, userB);
        handleCollision(userB, userA);
    }

    private void handleCollision(Object userA, Object userB) {
        // If userA is Player and userB is Enemy => kill player
        if (userA instanceof Player && userB instanceof Enemy) {
            System.out.println("Player collided with Enemy => Game Over!");
            map.gameOver();
        }
        // If userA is Player and userB is something else => handle if needed
        // e.g. you might detect power-ups, but you already do that by tile-based approach, so no need here.
    }

    @Override public void endContact(Contact contact) {}
    @Override public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}
