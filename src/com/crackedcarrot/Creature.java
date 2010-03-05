package com.crackedcarrot;

/**
* Class defining creature in the game
*/
public class Creature extends Sprite{
    // A creatures health
    public int health;
    // The next way point for a given creature
    public int nextWayPoint;
    // The speed of the creature
    public float velocity;
    // The different directions for a creature
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int UP = 2;
    public static final int DOWN = 3;
    // The current direction of the creature
    public int direction;
    // Delay before spawning the creature to the map
    public long spawndelay;
    // How much gold this creature gives when it's killed.
    public int goldValue;
    // Creature special ability
    public int specialAbility;
    
	public Creature(int resourceId){
		super(resourceId);
		this.draw = false;
		nextWayPoint = 0;
	}
	
	public void cloneCreature(Creature cr) {
	    this.health = cr.health;
	    this.nextWayPoint = cr.nextWayPoint;
	    this.velocity = cr.velocity;
	    this.draw  = cr.draw;
	    this.width = cr.width;
	    this.height = cr.height;
	    this.goldValue = cr.goldValue;
	    this.specialAbility = cr.goldValue;
<<<<<<< HEAD
	    //super.mResourceId = cr.mResourceId;
=======
	    super.mResourceId = cr.mResourceId;
	    super.mTextureName = cr.mTextureName;
>>>>>>> 638994b4a2b43cde9f3de12b9036d85e8f34474d
	}

	public void updateWayPoint (){
		nextWayPoint++;
	}
	
}

