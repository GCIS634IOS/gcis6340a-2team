package com.example.indoorgps_prototype;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

public class MainTabbedActivity extends  TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main_tabbed);
		
		TabHost mTabHost =getTabHost();

	    mTabHost.addTab(mTabHost.newTabSpec("first").setIndicator("First").setContent(new Intent(this  ,IndoorRoomsMapActivity.class )));
	    mTabHost.addTab(mTabHost.newTabSpec("second").setIndicator("Second").setContent(new Intent(this , IndoorRoomsMapActivity.class )));
	    mTabHost.setCurrentTab(0);
	    
	}
}
