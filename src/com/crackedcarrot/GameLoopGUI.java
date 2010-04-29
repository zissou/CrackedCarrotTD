package com.crackedcarrot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crackedcarrot.UI.UIHandler;
import com.crackedcarrot.fileloader.Level;
import com.crackedcarrot.menu.InstructionWebView;
import com.crackedcarrot.menu.R;
import com.scoreninja.adapter.ScoreNinjaAdapter;

	/*
	 * 
	 * This class builds on the GameInit-class with the purpose of containing all the
	 * required GUI elements for the GameLoop-class, for cleanliness/readability.
	 * 
	 */

public class GameLoopGUI {
	
	private GameInit gameInit;
	
	private Dialog dialog = null;

    private int          healthBarState = 3;
    private int          healthProgress = 100;
    private Drawable     healthBarDrawable;
    private ExpandMenu   expandMenu = null;
    private ImageView    enImView;
	private LinearLayout statusBar;
    private ProgressBar  healthProgressBar;
    private TextView     currencyView;
    private TextView     nrCreText;
    private TextView     playerHealthView;
    private UIHandler	 hud;

    // Used when we ask for the instruction view
    private int 		currentSelectedTower;
	
    private ScoreNinjaAdapter scoreNinjaAdapter;
    
    	// For readability-reasons.
    final int DIALOG_NEXTLEVEL_ID = 1;
    final int DIALOG_WON_ID       = 2;
    final int DIALOG_LOST_ID      = 3;
    final int DIALOG_HIGHSCORE_ID = 4;
    final int DIALOG_QUIT_ID      = 5;
    
    final int GUI_PLAYERMONEY_ID     = 10;
    final int GUI_PLAYERHEALTH_ID    = 11;
    final int GUI_CREATUREVIEW_ID    = 12;
    final int GUI_CREATURELEFT_ID    = 13;
    final int GUI_PROGRESSBAR_ID     = 14;
    final int GUI_NEXTLEVELINTEXT_ID = 15;
    final int GUI_SHOWSTATUSBAR_ID   = 16;
    final int GUI_SHOWHEALTHBAR_ID   = 17;
    final int GUI_HIDEHEALTHBAR_ID   = 18;
    
    final Button towerbutton1;
    final Button towerbutton2;
    final Button towerbutton3;
    final Button towerbutton4;
    final LinearLayout towertext;
    final Button tower2Information;

    
   	// Constructor. A good place to initiate all our different GUI-components.
    public GameLoopGUI(GameInit gi, final UIHandler hud) {
    	gameInit = gi;
    	this.hud = hud;
    	
        towertext = (LinearLayout) gameInit.findViewById(R.id.ttext);
        towerbutton1 = (Button) gameInit.findViewById(R.id.t1);
        towerbutton2 = (Button) gameInit.findViewById(R.id.t2);
        towerbutton3 = (Button) gameInit.findViewById(R.id.t3);
        towerbutton4 = (Button) gameInit.findViewById(R.id.t4);
        
        // Tower information. Clicking this will open information about this tower
        tower2Information = (Button) gameInit.findViewById(R.id.t2info);
        tower2Information.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		try {
    	    		GameInit.pauseSemaphore.acquire();
    	    		GameInit.pause = true;
    			} catch (InterruptedException e1) {}
        		Intent ShowInstr = new Intent(v.getContext(),InstructionWebView.class);
        		ShowInstr.putExtra("com.crackedcarrot.menu.tower", currentSelectedTower);
        		gameInit.startActivity(ShowInstr);
        	}
        });
        
        // Register on ScoreNinja.
        scoreNinjaAdapter = new ScoreNinjaAdapter(gi, "crackedcarrotd", "25912218B4FA767CCBE9F34735C93589");
    	
        // Create an pointer to the statusbar
        statusBar = (LinearLayout) gameInit.findViewById(R.id.status_menu);
        
		// Create the TextView showing number of enemies left
        nrCreText = (TextView) gameInit.findViewById(R.id.nrEnemyLeft);
        
        // Create the progress bar, showing the enemies total health
        healthProgressBar = (ProgressBar) gameInit.findViewById(R.id.health_progress);
        healthProgressBar.setMax(healthProgress);
        healthProgressBar.setProgress(healthProgress);
        healthBarDrawable = healthProgressBar.getProgressDrawable();
		healthBarDrawable.setColorFilter(Color.parseColor("#339900"),PorterDuff.Mode.MULTIPLY);

        // Create the ImageView showing current creature
        enImView = (ImageView) gameInit.findViewById(R.id.enemyImVi);
        
        // Create the text view showing the amount of currency
        currencyView = (TextView)gameInit.findViewById(R.id.currency);
        
        // Create the text view showing a players health
        playerHealthView = (TextView) gameInit.findViewById(R.id.playerHealth);

        
        // Create the expandable menu
        expandMenu =(ExpandMenu) gameInit.findViewById(R.id.expand_menu);
        
        // Create the instruction view
        //instructionView = (InstructionView)findViewById(R.id.instruction_view);
        

        /** Listeners for the six icons in the in-game menu.
         *  When clicked on, it's possible to place a tower
         *  on an empty space on the map. The first button
         *  expands the menu. */
        final Button forward = (Button) gameInit.findViewById(R.id.forward);
        final Button play = (Button) gameInit.findViewById(R.id.play);

        forward.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		forward.setVisibility(View.GONE);
        		gameInit.gameLoop.setGameSpeed(4);
        		play.setVisibility(View.VISIBLE);
        		//expandMenu.switchMenu();
        	}
        });

        play.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		play.setVisibility(View.GONE);
        		gameInit.gameLoop.setGameSpeed(1);
        		forward.setVisibility(View.VISIBLE);
        		//expandMenu.switchMenu();
        	}
        });

        towerbutton1.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		// A tower of type 1 has been chosen, where to put it?
        		gameInit.mGLSurfaceView.setTowerType(0);
        		openTowerBuildMenu(0);
        		hud.showGrid();
        	}
        });
        towerbutton2.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		// A tower of type 2 has been chosen, where to put it?
        		gameInit.mGLSurfaceView.setTowerType(1);
        		openTowerBuildMenu(1);
        		hud.showGrid();
        	}
        });
        towerbutton3.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		// A tower of type 3 has been chosen, where to put it?
        		gameInit.mGLSurfaceView.setTowerType(2);
        		openTowerBuildMenu(2);
        		hud.showGrid();
        	}
        });
        towerbutton4.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		// A tower of type 4 has been chosen, where to put it?
        		gameInit.mGLSurfaceView.setTowerType(3);
        		openTowerBuildMenu(3);
        		hud.showGrid();
        	}
        });

        // Button that removes towerInformation
        final Button inMenu6 = (Button) gameInit.findViewById(R.id.inmenu6);
        inMenu6.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		gameInit.mGLSurfaceView.setTowerType(-1);
        		towertext.setVisibility(View.GONE);
           		towerbutton1.setVisibility(View.VISIBLE);
        		towerbutton2.setVisibility(View.VISIBLE);
        		towerbutton3.setVisibility(View.VISIBLE);
        		towerbutton4.setVisibility(View.VISIBLE);
        		hud.hideGrid();
        	}
        });
        
	    ////////////////////////////////////////////////////////////////
	    // First button in Expand Menu
	    ////////////////////////////////////////////////////////////////
	    
	    Button removeExpand = (Button) gameInit.findViewById(R.id.removeExpand);
	    removeExpand.setOnClickListener(new OnClickListener() {
	    	
	    	public void onClick(View v) {
	    		expandMenu.switchMenu(false);
	    	}
	    });
	    
	    // Second set normal gameSpeed
	    Button normalSpeed = (Button) gameInit.findViewById(R.id.normalSpeed);
	    normalSpeed.setOnClickListener(new OnClickListener() {
	    	
	    	public void onClick(View v) {
	    		gameInit.gameLoop.setGameSpeed(1);
	    		// And den remove menu
	    		expandMenu.switchMenu(false);
	    	}
	    });

	    // Third set fast gameSpeed
	    Button fastSpeed = (Button) gameInit.findViewById(R.id.fastSpeed);
	    fastSpeed.setOnClickListener(new OnClickListener() {
	    	
	    	public void onClick(View v) {
	    		gameInit.gameLoop.setGameSpeed(4);
	    		//And then remove menu
	    			// TODO: we leave the menu raised in case the user wants to
	    			//       return to normal speed quickly again... good or bad? :)
	    		//expandMenu.switchMenu(false);
	    	}
	    });
        
	    // The second information button, activates the level info activity
	    Button infoButton = (Button) gameInit.findViewById(R.id.infobutton);
        infoButton.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		try {
    	    		GameInit.pauseSemaphore.acquire();
    	    		GameInit.pause = true;
    			} catch (InterruptedException e1) {}
        		Intent ShowInstr = new Intent(v.getContext(),InstructionWebView.class);
        		gameInit.startActivity(ShowInstr);
        	}
        });
        
        // The pause button
        Button pauseButton = (Button) gameInit.findViewById(R.id.pause);
        pauseButton.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		try {
    	    		GameInit.pauseSemaphore.acquire();
    	    		GameInit.pause = true;
    			} catch (InterruptedException e1) {}
        		gameInit.onPause();
        		Intent ShowInstr = new Intent(v.getContext(),PauseView.class);
        		gameInit.startActivity(ShowInstr);
        	}
        });

    }
    

    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuItem sound = menu.add(0, Menu.NONE, 0, "Sound");
    	sound.setIcon(R.drawable.button_sound_on);
        MenuItem quit = menu.add(0, Menu.NONE, 0, "Quit");
        quit.setIcon(R.drawable.button_quit);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
    	MenuItem sound = menu.getItem(0);
		if (gameInit.gameLoop.soundManager.playSound)
	    	sound.setIcon(R.drawable.button_sound_on);
		else
	    	sound.setIcon(R.drawable.button_sound_off);
    	
    	return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getTitle().toString().startsWith("Sound")) {
    		if (gameInit.gameLoop.soundManager.playSound)
    			gameInit.gameLoop.soundManager.playSound = false;
    		else
    			gameInit.gameLoop.soundManager.playSound = true;
    	} else if (item.getTitle().toString().startsWith("Quit")) {
    			// User clicked Quit.
    			// Doesnt save or prompt or anything, this just quits.
    		gameInit.finish();
    	}

        return false;
    }
    
    
	/*
	 *  Creates all of our dialogs.
	 *  
	 *  Note: This functions is only called ONCE for each dialog.
	 *  If you need a dynamic dialog this code does NOT go here!
	 *  
	 */
	protected Dialog onCreateDialog(int id) {
	    AlertDialog alertDialog;
    	AlertDialog.Builder builder;
    	Context mContext;
    	LayoutInflater inflater;
    	View layout;
    	
	    switch(id) {
	    case DIALOG_NEXTLEVEL_ID:
	    	dialog = new Dialog(gameInit,R.style.NextlevelTheme);
	        dialog.setContentView(R.layout.nextlevel);
	    	//dialog.setOwnerActivity(this);	    	
	    	dialog.setCancelable(true);
	    	// Info button
	    	Button infoButton2 = (Button) dialog.findViewById(R.id.infobutton2);
	        infoButton2.setOnClickListener(new OnClickListener() {
	        	
	        	public void onClick(View v) {
	        		try {
	    	    		GameInit.pauseSemaphore.acquire();
	    	    		GameInit.pause = true;
	    			} catch (InterruptedException e1) {}
	        		Intent ShowInstr = new Intent(v.getContext(),InstructionWebView.class);
	        		gameInit.startActivity(ShowInstr);
	        	}
	        });
	    	
	    	// A button	    	
	    	Button butt = (Button) dialog.findViewById(R.id.NextLevelButton);
	    	butt.setOnClickListener(
	    			new View.OnClickListener() {
	    				public void onClick(View v) {
	    					dialog.dismiss();

				    }
				});
	    	
	    	dialog.setOnDismissListener(
	    			new DialogInterface.OnDismissListener() {
						public void onDismiss(DialogInterface dialog) {
							gameInit.gameLoop.dialogClick();
						}
	    			});
	    	break;
	    case DIALOG_WON_ID:
	    	mContext = gameInit;
	    	inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	layout = inflater.inflate(R.layout.levelwon,
	    	                               (ViewGroup) gameInit.findViewById(R.id.layout_root));

	    	builder = new AlertDialog.Builder(mContext);
	    	builder.setView(layout)
	    	       .setCancelable(true)
	    	       .setPositiveButton("Yeah!", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   gameInit.gameLoop.dialogClick();
	    	           }
	    	       });
	    	alertDialog = builder.create();

	    	dialog = alertDialog;
	    	//dialog.setOwnerActivity(this);
	    	break;
	    case DIALOG_LOST_ID:
	    	mContext = gameInit;
	    	inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	layout = inflater.inflate(R.layout.levellost,
	    	                               (ViewGroup) gameInit.findViewById(R.id.layout_root));

	    	builder = new AlertDialog.Builder(mContext);
	    	builder.setView(layout)
	    	       .setCancelable(true)
	    	       .setPositiveButton("Yeah!", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   gameInit.gameLoop.dialogClick();
	    	           }
	    	       });
	    	alertDialog = builder.create();
	    	dialog = alertDialog;
	    	
	    	break;
	    case DIALOG_QUIT_ID:
	    	mContext = gameInit;
	    	inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	layout = inflater.inflate(R.layout.levelquit,
	    	                               (ViewGroup) gameInit.findViewById(R.id.layout_root));

	    	builder = new AlertDialog.Builder(mContext);
	    	builder.setView(layout)
	    	       .setCancelable(true)
	    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   gameInit.finish();
	    	           }
	    	       })
	    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
	    	    	   public void onClick(DialogInterface dialog, int id) {
	    	    		   // TODO: do nothing?
	    	    	   }
	    	       });

	    	alertDialog = builder.create();

	    	dialog = alertDialog;
	    	//dialog.setOwnerActivity(this);
	    	break;

	    case 255: // TODO: This is the old UpgradeTower-dialog, remove???
	    	mContext = gameInit;
	    	inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	layout = inflater.inflate(R.layout.upgradetower,
	    	                               (ViewGroup) gameInit.findViewById(R.id.layout_root));

	    	builder = new AlertDialog.Builder(mContext);
	    	builder.setView(layout)
	    	       .setCancelable(true)
	    	       ;

	    	/*
	        Button upgradeButton1 = (Button) findViewById(R.id.UpgradeTower1);
	        upgradeButton1.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		Log.d("TEST", "test");
	        	}
	        });

	        Button upgradeButton2 = (Button) findViewById(R.id.UpgradeTower2);
	        upgradeButton2.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		simulationRuntime.upgradeTower(1);
	        	}
	        });
	        
	        Button upgradeButton3 = (Button) findViewById(R.id.UpgradeTower3);
	        upgradeButton3.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		//dialog.cancel();
	        	}
	        });
	        */
	    	
	    	alertDialog = builder.create();
	    	dialog = alertDialog;

	    		// This will remove the fading effect of the dialog.
	    		// It's not suitable for upgrading towers to dim the screen...
	    	WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
	    	dialog.getWindow().setAttributes(lp); // sets the updated windows attributes
	    	dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	    	
	    	break;
	    default:
	    	Log.d("GAMEINIT", "onCreateDialog got unknown dialog id: " + id);
	        dialog = null;
	    }
	    return dialog;
	}

	
	/*
	 *  Creates our NextLevel-dialog.
	 *  
	 *  This is called every time a dialog is presented.
	 *  If you want dynamic dialogs, put your code here.
	 *  
	 */
	protected void onPrepareDialog(int id, Dialog dialog) {
	    switch(id) {
	    case DIALOG_NEXTLEVEL_ID:

	    	int currLvlnbr = gameInit.gameLoop.getLevelNumber() + 1;

	    	Level currLvl = gameInit.gameLoop.getLevelData();

	    	// Title:
	    	TextView title = (TextView) dialog.findViewById(R.id.NextLevelTitle);
	    	String titleText ="<b>Level " + currLvlnbr + "</b><br>" + currLvl.creepTitle +"<br>";
		    CharSequence styledText = Html.fromHtml(titleText);
	    	title.setText(styledText);
	    	
    		// And an icon.
	    	ImageView image = (ImageView) dialog.findViewById(R.id.NextLevelImage);
	    	image.setImageResource(currLvl.getDisplayResourceId());

	    	
	    	
	    	// Text for next level goes here.
	    	TextView text = (TextView) dialog.findViewById(R.id.NextLevelText);
	    	Player currPlayer = gameInit.gameLoop.getPlayerData();
	    	String lvlText ="<b>Number of creeps:</b> " + currLvl.nbrCreatures +"<br>";
	    	lvlText += 		"<b>Bounty:</b> " + currLvl.goldValue + "g/creep<br>";
	    	lvlText += 		"<b>Health:</b> " + (int)currLvl.getHealth() + "hp/creep<br>";
	    	lvlText += 		"<br>";
	    	lvlText += 		"<b>Special abillites:</b><br>";
	    	int tmpAbil = 0;
	    	if (currLvl.creatureFast) {
		    	lvlText += 		"<font color=yellow>Fast level</font><br>";
		    	tmpAbil++;
	    	}
		    if (currLvl.creatureFireResistant) {
		    	lvlText += 		"<font color=red>Fire resistant</font><br>";
		    	tmpAbil++;
		    }
		    if (currLvl.creatureFrostResistant) {
		    	lvlText += 		"<font color=blue>Frost resistant</font><br>";
		    	tmpAbil++;
		    }
		    if (currLvl.creaturePoisonResistant) {
		    	lvlText += 		"<font color=green>Posion resistant</font><br>";
		    	tmpAbil++;
		    }
		    if (tmpAbil == 0)
		    	lvlText += 		"No special abbilities<br>";
		    
		    if (currLvlnbr > 1) {
		    	lvlText += 		"<br><b>Previous level:</b><br>";
		    	lvlText += 		"Interest gained:" + currPlayer.getInterestGainedThisLvl() + "<br>";
		    	lvlText += 		"Health lost:" + currPlayer.getHealthLostThisLvl();		    	
		    }
		    else {
		    	lvlText += 		"<br><b>Tip:</b><br>";
		    	lvlText += 		"If you have trouble <br>understanding this game.<br> Use the information<br> button below or ingame";
		    }
		    styledText = Html.fromHtml(lvlText);
		    text.setText(styledText);

		    if (currLvl.creaturePoisonResistant && !currLvl.creatureFireResistant && !currLvl.creatureFrostResistant) {
		    	image.setColorFilter(Color.rgb(178, 255, 178),PorterDuff.Mode.MULTIPLY);
		    }
		    else if (!currLvl.creaturePoisonResistant && currLvl.creatureFireResistant && !currLvl.creatureFrostResistant) {
		    	image.setColorFilter(Color.rgb(255, 178, 178),PorterDuff.Mode.MULTIPLY);
		    }
		    else if (!currLvl.creaturePoisonResistant && !currLvl.creatureFireResistant && currLvl.creatureFrostResistant) {
		    	image.setColorFilter(Color.rgb(178, 178, 255),PorterDuff.Mode.MULTIPLY);
		    }
		    else if (currLvl.creaturePoisonResistant && currLvl.creatureFrostResistant && !currLvl.creatureFireResistant) {
		    	image.setColorFilter(Color.rgb(178, 255, 255),PorterDuff.Mode.MULTIPLY);
		    }
		    else if (currLvl.creaturePoisonResistant && !currLvl.creatureFrostResistant && currLvl.creatureFireResistant) {
		    	image.setColorFilter(Color.rgb(255, 255, 178),PorterDuff.Mode.MULTIPLY);
		    }
		    else if (!currLvl.creaturePoisonResistant && currLvl.creatureFrostResistant && currLvl.creatureFireResistant) {
		    	image.setColorFilter(Color.rgb(255, 178, 255),PorterDuff.Mode.MULTIPLY);
		    }
		    else 
		    	image.setColorFilter(Color.rgb(255, 255, 255),PorterDuff.Mode.MULTIPLY);
		    
		    break;
	    default:
	    	Log.d("GAMEINIT", "onPrepareDialog got unknown dialog id: " + id);
	        dialog = null;
	    }
	}

	
		// This is used to handle calls from the GameLoop to show
		// our dialogs.
	public Handler guiHandler = new Handler() {
	
	    @Override
	    public void handleMessage(Message msg) {
	
	        switch (msg.what) {
	        	 case DIALOG_NEXTLEVEL_ID:
	        		 gameInit.showDialog(DIALOG_NEXTLEVEL_ID);
	        		 break;
	        	 case DIALOG_WON_ID:
	        		 gameInit.showDialog(DIALOG_WON_ID);
	        		 break;
	        	 case DIALOG_LOST_ID:
	        		 gameInit.showDialog(DIALOG_LOST_ID);
	        		 break;
	        	 case DIALOG_HIGHSCORE_ID:
	        	     SharedPreferences settings = gameInit.getSharedPreferences("Options", 0);
	        	     if (settings.getBoolean("optionsHighscore", false)) {
	        	    	 	// If ScoreNinja is enabled we show it to the player: 
	        	    	 scoreNinjaAdapter.show(msg.arg1);
	        	     }
	        		 break;
	        		 
	        	 case GUI_PLAYERMONEY_ID:
	        		 // Update currencyView (MONEY)
	        		 currencyView.setText("" + msg.arg1);
	        		 break;
	        	 case GUI_PLAYERHEALTH_ID:
	        		 // Update player-health.
	        		 playerHealthView.setText("" + msg.arg1);
	        		 break;
	        	 case GUI_CREATUREVIEW_ID:
	        		 // Update Enemy-ImageView
	        		 enImView.setImageResource(msg.arg1);
	        		 break;
	        	 case GUI_CREATURELEFT_ID:
	        		 // update number of creatures still alive on GUI.
	        		 String tt = String.valueOf(msg.arg1);
	        		 if (msg.arg1 < 10)
	        			 tt = "  " + tt;
	        		 nrCreText.setText("" + tt);
	        		 break;
	        		 
	        	 case GUI_PROGRESSBAR_ID: // update progressbar with creatures health.
	        		 // The code below is used to change color of healthbar when health drops
	        		 if (msg.arg1 >= 66 && healthBarState == 1) {
	       				 healthBarDrawable.setColorFilter(Color.parseColor("#339900"),PorterDuff.Mode.MULTIPLY);
	        			 healthBarState = 3;
	        		 }
	        		 if (msg.arg1 <= 66 && healthBarState == 3) {
	        			 healthBarDrawable.setColorFilter(Color.parseColor("#FFBB00"),PorterDuff.Mode.MULTIPLY);
	        			 healthBarState = 2;
	        		 }
	        		 if (msg.arg1 <= 33 && healthBarState == 2) {
	        			 healthBarDrawable.setColorFilter(Color.parseColor("#CC0000"),PorterDuff.Mode.MULTIPLY);
	        			 healthBarState = 1;
	        		 }
	        		 healthProgressBar.setProgress(msg.arg1);
	        		 break;
	        		 
	        	 case GUI_NEXTLEVELINTEXT_ID: // This is used to show how long time until next lvl.
	        		 tt = String.valueOf(msg.arg1);
	        		 if (msg.arg1 < 10)
	        			 tt = "  " + tt;
	        		 nrCreText.setText("Next level in: " + tt);
	        		 break;
	        		 
	        	 case GUI_SHOWSTATUSBAR_ID:
	        		 //Show statusbar
		    			statusBar.setVisibility(View.VISIBLE);
		    			break;
	        	 case GUI_SHOWHEALTHBAR_ID:
	        		 //If we want to switch back to healthbar
        		 	 nrCreText.setText("");
	        		 healthProgressBar.setVisibility(View.VISIBLE);
	        		 enImView.setVisibility(View.VISIBLE);
	        		 break;
	        	 case GUI_HIDEHEALTHBAR_ID:
	        		 //If we want to use space in statusbar to show time to next level counter
	        		 healthProgressBar.setVisibility(View.GONE);
	        		 enImView.setVisibility(View.GONE);
	        		 break;
	    			 
	        	 case -1: // GAME IS DONE, CLOSE ACTIVITY.
	        		 gameInit.finish();
	        		 break;
	        	 case -2: // SAVE THE GAME.
	        		 	// arg 1 = save game, 2 = remove saved game.
	        		 gameInit.saveGame(msg.arg1);
	        		 break;
	        		 
	        	 default:
	                 Log.e("GAMELOOPGUI", "guiHandler error! msg.what: " + msg.what);
	                 break;
	        }
	    }
	};
	
	public ScoreNinjaAdapter getScoreNinjaAdapter() {
		return this.scoreNinjaAdapter;
	}
	
	
	protected void sendMessage(int i, int j, int k) {
		// TODO: remove this when done debugging msgs.
		//Log.d("GAMELOOPGUI", "sendMessage: " + i);
		
		Message msg = Message.obtain();
		msg.what = i;
		msg.arg1 = j;
		msg.arg2 = k;
		guiHandler.sendMessage(msg);
	}

	private void openTowerBuildMenu(int towerId) {
		//Tower info = gameInit.gameLoop.getTower(towerId);
		//String text =  info.getTitle() + "<b> Price:</b>" + info.getPrice() + "<br>";
		//text		+= "<b>Speed:</b> Fast <b>Range:</b> " + (int)info.getRange() + "<br>";
   		//text 		+= "<b> Damage:</b>" + (int)info.getMinDamage() + "-" + (int)info.getMaxDamage();
	    //CharSequence styledText = Html.fromHtml(text);
	    //if (towerId == 0) {
		    //tower1Information.setVisibility(View.GONE);
		    tower2Information.setVisibility(View.VISIBLE);
		    //tower3Information.setVisibility(View.GONE);
		    //tower4Information.setVisibility(View.GONE);
	    //}
		this.currentSelectedTower = towerId;
   		towerbutton1.setVisibility(View.GONE);
		towerbutton2.setVisibility(View.GONE);
		towerbutton3.setVisibility(View.GONE);
		towerbutton4.setVisibility(View.GONE);
		towertext.setVisibility(View.VISIBLE);
	}	
}