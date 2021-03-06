package com.crackedcarrot.multiplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.crackedcarrot.menu.*;

public class MultiplayerOp extends Activity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplayer);
        
        /** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
    	Button HostButton = (Button)findViewById(R.id.host);
    	HostButton.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		Intent StartServer = new Intent(v.getContext(),Server.class);
        		startActivity(StartServer);
        	}
        });
    	
    	Button JoinButton = (Button)findViewById(R.id.join);
    	JoinButton.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		Intent StartClient = new Intent(v.getContext(),Client.class);
        		startActivity(StartClient);
        	}
        });
    	
        Button BackButton = (Button)findViewById(R.id.back);
        BackButton.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		Intent Back = new Intent(v.getContext(),MainMenu.class);
        		startActivity(Back);
        		finish();
        	}
        });
  
    }
}