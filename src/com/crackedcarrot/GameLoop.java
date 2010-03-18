package com.crackedcarrot;

import android.os.SystemClock;
import android.util.Log;

import com.crackedcarrot.fileloader.Level;
import com.crackedcarrot.fileloader.Map;
import com.crackedcarrot.fileloader.TowerGrid;
import com.crackedcarrot.menu.R;

/**
 * A runnable that updates the position of each creature and projectile
 * every frame by applying a simple movement simulation. Also keeps
 * track of player life, level count etc.
 */
public class GameLoop implements Runnable {
    private Player player;
    private Map mGameMap;

    private Creature[] mCreatures;
    private int remainingCreatures;

    private Level[] mLvl;
    private int lvlNbr;
    
    private Tower[] mTower;
    private TowerGrid[][] mTowerGrid;
    private Tower[] mTTypes;
    private int totalNumberOfTowers = 0;
    private Shot[] mShots;
    
    private long mLastTime;
    private boolean run = true;

    private int gameSpeed;
    
    private SoundManager soundManager;
    private Scaler mScaler;
    private NativeRender renderHandle;
    
    public GameLoop(NativeRender renderHandle, Map gameMap, Level[] waveList, Tower[] tTypes,
			Player p, SoundManager sm){
    	this.renderHandle = renderHandle;
		this.mGameMap = gameMap;
   		this.mTowerGrid = gameMap.getTowerGrid();
   		this.mScaler = gameMap.getScaler();
		this.mTTypes = tTypes;
        this.mLvl = waveList;
    	this.soundManager = sm;
    	this.player = p;
    }
    
	private void initializeDataStructures() {
		//this allocates the space we need for shots towers and creatures.
	    this.mTower = new Tower[60];
	    this.mShots = new Shot[60];
	    this.mCreatures = new Creature[50];

	    //Initialize the all the elements in the arrays with garbage data
	    for (int i = 0; i < mTower.length; i++) {
	    	mTower[i] = new Tower(R.drawable.tower1);
	    	mShots[i] = new Shot(R.drawable.cannonball, mTower[i]);
	    	mTower[i].relatedShot = mShots[i];
	    	mTower[i].draw = false;
	    	mShots[i].draw = false;
	    } 

	    //same as for the towers and shots.
	    for (int i = 0; i < mCreatures.length; i++) {
	    	mCreatures[i] = new Creature(R.drawable.bunny_pink_alive, player, soundManager, mGameMap.getWaypoints().getCoords());
	    	mCreatures[i].draw = false;
	    } 
		
	    //Free all allocated data in the render
	    //Not needed really.. but now we know for sure that
	    //we don't have any garbage anywhere.
		try {
			renderHandle.freeSprites();
			renderHandle.freeAllTextures();		
			
			//Load textures for towers.

			for (int i = 0; i < mTTypes.length; i++) {
				renderHandle.loadTexture(mTTypes[i].getResourceId());
				renderHandle.loadTexture(mTTypes[i].relatedShot.getResourceId());
			}

			//Load textures for all creature types.
			for(int i = 0; i < mLvl.length; i++){
				renderHandle.loadTexture(mLvl[i].getResourceId());
				renderHandle.loadTexture(mLvl[i].getDeadResourceId());
			}
			//Ok, here comes something superduper mega important.
			//The folowing looks up what names the render assigned
			//To every texture from their resource ids 
			//And assigns that id to the template objects for
			//Towers shots and creatures.
			
			for(int i = 0; i < mTTypes.length; i++){
				mTTypes[i].setTextureName(
						renderHandle.getTextureName(mTTypes[i].getResourceId()));
				
				mTTypes[i].relatedShot.setTextureName(
						renderHandle.getTextureName(mTTypes[i].relatedShot.getResourceId()));
				
			}
			
			for(int i = 0; i < mLvl.length; i++){
				mLvl[i].setTextureName(renderHandle.getTextureName(mLvl[i].getResourceId()));
				mLvl[i].setDeadTextureName(renderHandle.getTextureName(mLvl[i].getDeadResourceId()));
			}
						
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Sends an array with sprites to the renderer
		renderHandle.setSprites(mGameMap.getBackground(), NativeRender.BACKGROUND);
		renderHandle.setSprites(mCreatures, NativeRender.CREATURE);
		renderHandle.setSprites(mTower, NativeRender.TOWER);
		renderHandle.setSprites(mShots, NativeRender.SHOT);
		
        // Now's a good time to run the GC.  Since we won't do any explicit
        // allocation during the test, the GC should stay dormant and not
        // influence our results.
		Runtime r = Runtime.getRuntime();
        r.gc();
		
	}
    
	private void initializeLvl() {
		try {
			//Free last levels sprites to clear the video mem and ram from
			//Unused creatures and settings that are no longer valid.
			renderHandle.freeSprites();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	final long starttime = SystemClock.uptimeMillis();
		
    	//Set the creatures texture size and other atributes.
    	remainingCreatures = mLvl[lvlNbr].nbrCreatures;
    	//Need to reverse the list for to draw correctly.
    	int reverse = remainingCreatures; 
		for (int z = 0; z < remainingCreatures; z++) {
			reverse--;
			// The following line is used to add the following wave of creatures to the list of creatures.
			mCreatures[z].setResourceId(mLvl[lvlNbr].getResourceId());
			mCreatures[z].setDeadResourceId(mLvl[lvlNbr].getDeadResourceId());
			mCreatures[z].setDeadTextureName(mLvl[lvlNbr].getDeadTextureName());
			mCreatures[z].setTextureName(mLvl[lvlNbr].getTextureName());
			mCreatures[z].setDeadTextureName(mLvl[lvlNbr].getDeadTextureName());
			
			mCreatures[z].setCreatureFast(mLvl[lvlNbr].isCreatureFast());
			mCreatures[z].setCreatureFireResistant(mLvl[lvlNbr].isCreatureFireResistant());
			mCreatures[z].setCreatureFrostResistant(mLvl[lvlNbr].isCreatureFrostResistant());
			mCreatures[z].setCreaturePoisonResistant(mLvl[lvlNbr].isCreaturePoisonResistant());
    		
			mCreatures[z].moveToWaypoint(0);
    		
    		mCreatures[z].setHealth(mLvl[lvlNbr].getHealth());
    		mCreatures[z].setNextWayPoint(mLvl[lvlNbr].getNextWayPoint());
    		mCreatures[z].setVelocity(mLvl[lvlNbr].getVelocity());
    		
    		mCreatures[z].width = mLvl[lvlNbr].width;
    		mCreatures[z].height = mLvl[lvlNbr].height;
    		
    		mCreatures[z].setGoldValue(mLvl[lvlNbr].getGoldValue());
    		
    		mCreatures[z].setCreatureFireResistant(mLvl[lvlNbr].isCreatureFireResistant());
    		mCreatures[z].setCreatureFrostResistant(mLvl[lvlNbr].isCreatureFrostResistant());
    		mCreatures[z].setCreaturePoisonResistant(mLvl[lvlNbr].isCreaturePoisonResistant());
    		
    		mCreatures[z].draw = false;
    		mCreatures[z].opacity = 1;
    		// In some way we have to determine when to spawn the creature. Since we dont want to spawn them all at once.
			int special = 1;
    		if (mCreatures[z].isCreatureFast())
    			special = 2;
    		mCreatures[z].setSpawndelay((long)(starttime + (player.timeBetweenLevels + (reverse * (500/special)))/gameSpeed));
		}
		try {
			
			//Finally send of the sprites to the render to be allocated
			//And after that drawn.
			renderHandle.finalizeSprites();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

    public void run() {
    	
	    initializeDataStructures();
    	lvlNbr = 0;
	    gameSpeed = 1;
	    Log.d("GAMELOOP","INIT GAMELOOP");

	    while(run){
	    	// Tries to create a test tower
	    	Coords tmp = mScaler.getPosFromGrid(2, 9);
	    	createTower(tmp,0);
	    	tmp = mScaler.getPosFromGrid(4, 6);
	    	createTower(tmp,1);
	    	
	    	//It is important that ALL SIZES OF SPRITES ARE SET BEFORE! THIS!
    		//OR they will be infinitely small.
    		initializeLvl();
    		
            // The LEVEL loop. Will run until all creatures are dead or done or player are dead.
    		while(remainingCreatures > 0 && run){

    			//Systemclock. Used to help determine speed of the game. 
				final long time = SystemClock.uptimeMillis();
				
	            // Used to calculate creature movement.
				final long timeDelta = time - mLastTime;
	            final float timeDeltaSeconds = 
	                mLastTime > 0.0f ? timeDelta / 1000.0f : 0.0f;
	            mLastTime = time;
	            
	            // Shows how long it is left until next level
	            player.timeUntilNextLevel = (int)(player.timeUntilNextLevel - mLastTime);	            

	            //Calls the method that moves the creature.
	            moveCreatures(timeDeltaSeconds,time);
	            //Calls the method that handles the monsterkilling.
	            killCreature(timeDeltaSeconds);
	            
	            // Check if the GameLoop are to run the level loop one more time.
	            if (player.health < 1) {
            		//If you have lost all your lives then the game ends.
	            	run = false;
            	}
	        }
    		player.calculateInterest();

    		// Check if the GameLoop are to run the level loop one more time.
            if (player.health < 1) {
        		//If you have lost all your lives then the game ends.
            	Log.d("GAMETHREAD", "You are dead");
            	run = false;
        	} 
        	else if (remainingCreatures < 1) {
        		//If you have survied the entire wave without dying. Proceed to next next level.
            	Log.d("GAMETHREAD", "Wave complete");
        		lvlNbr++;
        		if (lvlNbr >= mLvl.length) {
        			// You have completed this map
                	Log.d("GAMETHREAD", "You have completed this map");
        			run = false;
        		}
        	}
	    }
    	Log.d("GAMETHREAD", "dead thread");
    }

	/**
	 * Will go through all of the creatures from this level and
	 * calculate the movement. 
	 * <p>
	 * This method runs every loop the gameLoop takes. 
	 *
	 * @param  timeDeltaSeconds  	Time since last GameLoop lap 
	 * @param  time	 				Time since this level started
	 * @return      				void
	 */
    public void moveCreatures(float timeDeltaSeconds, long time) {
    	// If the list of creatures is empty we will end this method
    	if (mCreatures == null) {
    		return;
    	}
    	for (int x = 0; x < mLvl[lvlNbr].nbrCreatures; x++) {
    		this.remainingCreatures -= mCreatures[x].move(timeDeltaSeconds, time, gameSpeed);
    	}
    }

	/**
	 * Will go through all of the towews and try to find targets
	 * <p>
	 * This method runs every loop the gameLoop takes. 
	 *
	 * @param  timeDeltaSeconds  	Time since last GameLoop lap 
	 * @return      				void
	 */
    public void killCreature(float timeDeltaSeconds) {
    	// If the list of shots is empty we will end this method
    	if (mTower == null) {
    		return;
    	}
    	
    	for (int x = 0; x <= totalNumberOfTowers; x++) {
    		Tower towerObject = mTower[x];
    		// Decrease the coolDown variable and check if it has reached zero
    		towerObject.tmpCoolDown = towerObject.tmpCoolDown - (timeDeltaSeconds * gameSpeed);

    		// This code is used to display the AOE for a pure AOE shot. This is for testing.
    		if (towerObject.towerType == towerObject.PUREAOE) {
    			
    			if (towerObject.tmpCoolDown <= towerObject.coolDown/2)
    				towerObject.relatedShot.draw = false;
    			
    			if (towerObject.tmpCoolDown <= 0) {
    				if (towerObject.createPureAOEDamage(mCreatures,mLvl[lvlNbr].nbrCreatures)) {
    					soundManager.playSound(0);
    					towerObject.tmpCoolDown = towerObject.coolDown;
    					towerObject.relatedShot.draw = true;
    		    		towerObject.relatedShot.x = towerObject.x + towerObject.width/2 - towerObject.relatedShot.width/2;
    		    		towerObject.relatedShot.y = towerObject.y + towerObject.height/2 - towerObject.relatedShot.height/2;
    				}
    			}
    		}
    		// This code is for towers that use projectile damage.
    		else {
    			if (!towerObject.relatedShot.draw && (towerObject.tmpCoolDown <= 0)) {
	    			// If the tower/shot is existing start calculations.
	    			towerObject.trackEnemy(mCreatures,mLvl[lvlNbr].nbrCreatures);
	    			if (towerObject.targetCreature != null) {
	    					// play shot1.mp3
	    					soundManager.playSound(0);
	    					towerObject.tmpCoolDown = towerObject.coolDown;
	    					towerObject.relatedShot.draw = true;
	    			}
    			}
	    		// if the creature is still alive or have not reached the goal
	    		if (towerObject.towerType != towerObject.PUREAOE && towerObject.relatedShot.draw && towerObject.targetCreature.draw && towerObject.targetCreature.opacity == 1.0) {
	    			Creature targetCreature = towerObject.targetCreature;
	
	    			float yDistance = (targetCreature.y+(targetCreature.height/2)) - towerObject.relatedShot.y;
	    			float xDistance = (targetCreature.x+(targetCreature.width/2)) - towerObject.relatedShot.x;
	    			double xyMovement = (towerObject.velocity * timeDeltaSeconds * gameSpeed);
	    			
	    			if ((Math.abs(yDistance) <= xyMovement) && (Math.abs(xDistance) <= xyMovement)) {
			    		towerObject.relatedShot.draw = false;
			    		towerObject.resetShotCordinates();
			    		//More advanced way of implementing damage
			    		towerObject.createProjectileDamage();
			    		//IF A CANNONTOWER FIRES A SHOT we also have to damage surrounding creatures
			    		if (towerObject.towerType == towerObject.PROJECTILEAOE){
					    	towerObject.createProjectileAOEDamage(mCreatures,mLvl[lvlNbr].nbrCreatures);
			    		}
			    		if (targetCreature.health <= 0) {
			    			creatureDied(targetCreature);
			    		}
	    			}
	    			else {
	        			double radian = Math.atan2(yDistance, xDistance);
	        			towerObject.relatedShot.x += Math.cos(radian) * xyMovement;
	        			towerObject.relatedShot.y += Math.sin(radian) * xyMovement;
	    			}
				}
	    		else if (towerObject.towerType != towerObject.PUREAOE) {
	    			towerObject.relatedShot.draw = false;
		    		towerObject.resetShotCordinates();
	    		}
    		}
    	}
	}

    public boolean createTower(Coords TowerPos, int towerType) {
		if (mTTypes.length > towerType && totalNumberOfTowers < mTower.length) {
			if (!mScaler.insideGrid(TowerPos.x,TowerPos.y)) {
				//You are trying to place a tower on a spot outside the grid
				return false;
			}
			
			Coords tmpC = mScaler.getGridXandY(TowerPos.x,TowerPos.y);
			int tmpx = tmpC.x;
			int tmpy = tmpC.y;
			
			if (mTowerGrid[tmpx][tmpy].empty) {
				
				//Use the textureNames that we preloaded into the towerTypes at startup
				mTower[totalNumberOfTowers].setTextureName(mTTypes[towerType].getTextureName());
				mTower[totalNumberOfTowers].relatedShot.setTextureName(mTTypes[towerType].relatedShot.getTextureName());
				mTower[totalNumberOfTowers].setResourceId(mTTypes[towerType].getResourceId())
				;
				mTower[totalNumberOfTowers].coolDown = mTTypes[towerType].coolDown;
				mTower[totalNumberOfTowers].height = mTTypes[towerType].height;
				mTower[totalNumberOfTowers].width = mTTypes[towerType].width;
				mTower[totalNumberOfTowers].level = mTTypes[towerType].level;
				mTower[totalNumberOfTowers].maxDamage = mTTypes[towerType].maxDamage;
				mTower[totalNumberOfTowers].minDamage = mTTypes[towerType].minDamage;
				mTower[totalNumberOfTowers].price = mTTypes[towerType].price;
				mTower[totalNumberOfTowers].range = mTTypes[towerType].range;
				mTower[totalNumberOfTowers].resellPrice = mTTypes[towerType].resellPrice;
				mTower[totalNumberOfTowers].hasFireDamage = mTTypes[towerType].hasFireDamage;
				mTower[totalNumberOfTowers].hasFrostDamage = mTTypes[towerType].hasFrostDamage;
				mTower[totalNumberOfTowers].frostTime = mTTypes[towerType].frostTime;
				mTower[totalNumberOfTowers].hasPoisonDamage = mTTypes[towerType].hasPoisonDamage;
				mTower[totalNumberOfTowers].poisonDamage = mTTypes[towerType].poisonDamage;
				mTower[totalNumberOfTowers].poisonTime = mTTypes[towerType].poisonTime;
				mTower[totalNumberOfTowers].title = mTTypes[towerType].title;
				mTower[totalNumberOfTowers].upgrade1 = mTTypes[towerType].upgrade1;
				mTower[totalNumberOfTowers].upgrade2 = mTTypes[towerType].upgrade2;
				mTower[totalNumberOfTowers].velocity = mTTypes[towerType].velocity;
				mTower[totalNumberOfTowers].rangeAOE = mTTypes[towerType].rangeAOE;
				mTower[totalNumberOfTowers].aoeDamage = mTTypes[towerType].aoeDamage;
				mTower[totalNumberOfTowers].towerType = mTTypes[towerType].towerType;
				mTower[totalNumberOfTowers].draw = true; //Tower drawable
				mTower[totalNumberOfTowers].relatedShot.setResourceId(mTTypes[towerType].relatedShot.getResourceId());
				mTower[totalNumberOfTowers].relatedShot.height = mTTypes[towerType].relatedShot.height;
				mTower[totalNumberOfTowers].relatedShot.width = mTTypes[towerType].relatedShot.width;
				mTower[totalNumberOfTowers].relatedShot.draw = false;
				
				Coords tmp = mScaler.getPosFromGrid(tmpx, tmpy);
				
				mTower[totalNumberOfTowers].x = tmp.x;
				mTower[totalNumberOfTowers].y = tmp.y;
				mTower[totalNumberOfTowers].resetShotCordinates();//Same location of Shot as midpoint of Tower
				
				totalNumberOfTowers++;
				mTowerGrid[tmpx][tmpy].empty = false;
				mTowerGrid[tmpx][tmpy].tower = totalNumberOfTowers;
	    		return true;
			}
		}
		return false;
    }
    
    public void stopGameLoop(){
    	run = false;
    }
}