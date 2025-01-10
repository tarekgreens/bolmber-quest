# Bomber Quest

Add your game description here!
Player class: To move the player, I used Tick method to check the key pressed by the player. If the player pressed the key,
the player will move to the direction that the player pressed. I also used the method to check if the player is in the map or not.

WallPath class:
We created an abstract class WallPath which represents a wall or path in the game map.
It contains the bounds of the wall or path and the texture used to render it.
WallPath is extended by DestructibleWall and IndestructibleWall classes that implement specific behaviors.
Added hasExit method to check if the player has reached the exit.
Added Render method in GameScreen class to render the wall or path.

