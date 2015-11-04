package com.example.indoorgps_prototype;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;











import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class IndoorRoomsMapActivity extends Activity {

	private CustomIndoorMapLayoutView  indoorMapView ;
	ArrayList<String> list = new ArrayList<String>();
	ArrayAdapter adapter;
	private final Handler handler = new Handler();
	WifiReceiver receiverWifi;
	ArrayList<String> accessPointList = new ArrayList<String>();
	private List<ScanResult> listResult;
	private ProgressDialog progress;
	WifiManager myWifiManager;
	BroadcastReceiver receiver = new WifiReceiver();;
	int ap1Strength=0;
	int ap2Strength=0;
	int ap3Strength=0;
	long startTime ;//= System.currentTimeMillis();
	long count ;
	long timeToFinishScan;
	boolean bMatchNearest;
	private Menu OptionMenu;
	
	private final Runnable runnable = new Runnable() {
		public void run() {
			WifiManager myWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			Log.d("WIFI3","starting scan");
			myWifiManager.startScan();
			startTime = System.currentTimeMillis();
			handler.postDelayed(runnable, 4000);
			
		}
	};
	
	private final Runnable runnableMessage = new Runnable() {
		public void run() {
			Toast.makeText(getApplicationContext(), "Wifi updated", Toast.LENGTH_LONG);
		}
	};
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_options, menu);
	    OptionMenu = menu;
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		MenuItem otherMenuItem;
		  switch (item.getItemId()) {
		    case R.id.about:
		    	AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
		    	dlgAlert.setMessage("        Zurn Indoor Map Prototype\n    Developed by Bader Aldawsari");
		    	dlgAlert.setTitle("Indoor Map");
		    	dlgAlert.setPositiveButton("OK", null);
		    	dlgAlert.setCancelable(true);
		    	dlgAlert.create().show();
		      break;
		    case R.id.fmExact:
		    	 otherMenuItem = (MenuItem)OptionMenu.findItem(R.id.fmNearest);
		    	otherMenuItem.setChecked(false);
		    	item.setChecked(true);
		    	bMatchNearest = false;
		    	break;		    	
		    case R.id.fmNearest:
		    	 otherMenuItem = (MenuItem)OptionMenu.findItem(R.id.fmExact);
		    	otherMenuItem.setChecked(false);
		    	item.setChecked(true);
		    	bMatchNearest=true;
		    	break;		    	
		    default:
		      ;
		  }
		  return super.onOptionsItemSelected(item);
		}
	
	private final String orangeColor = "#E99139";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bMatchNearest = true;
		indoorMapView = new CustomIndoorMapLayoutView(getApplicationContext());		
		setContentView(indoorMapView);
		count =0 ;
	
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor(orangeColor));
	    getActionBar().setBackgroundDrawable(colorDrawable);
	    
		myWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		
		 if(!myWifiManager.isWifiEnabled()){

		     if(myWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING){
		        Toast.makeText(getApplicationContext(), "Putting wi-fi on..", Toast.LENGTH_SHORT).show();
		        myWifiManager.setWifiEnabled(true);
		            }
		     else{
		    	 Toast.makeText(getApplicationContext(), "already enabled on..", Toast.LENGTH_SHORT).show();
		     }
		 }

		 
		// DisplayWifiState();
		if(receiver == null){
			receiver = new WifiReceiver();
		}else{
			Log.d("WIFI4","not null");
		}
		
		registerReceiver(receiver, new IntentFilter(
						WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
				
		handler.post(runnable);
	}
	
	
	public void DisplayWifiState() {
		Log.d("WIFI1", "rcvd");

		Log.d("WIFI1", "Updated1");

		//accessPointList.clear();
		//listResult = myWifiManager.getScanResults();
		List<ScanResult> wifiList = myWifiManager.getScanResults();
		
		
		for (ScanResult result : listResult) {
			String newAccessPoint = result.SSID + " : dBm:" + result.level
					+ " : Freq:" + result.frequency;
				
			Log.d("WIFIList", newAccessPoint);
	
		}
		
		Log.d("WIFI1", "Updated2");
	}
	
	long totalTime=0;
	

	class WifiReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {
			timeToFinishScan = System.currentTimeMillis();
			boolean ap1Found=false;
			boolean ap2Found=false;
			boolean ap3Found=false;
			ap1Strength = -100;
			ap2Strength = -100;
			ap3Strength = -100;

			
	
			List<ScanResult> wifiList = myWifiManager.getScanResults();
			for (ScanResult result : wifiList) {
					
				if(result.BSSID.toLowerCase().equals("00:7f:28:96:5a:1f")){
					ap1Strength = result.level;
					indoorMapView.setAp1Strength(ap1Strength);
					Log.d("WIFI", "found 1 = "+ ap1Strength);
							
					ap1Found=true;
					
				}else if(result.BSSID.toLowerCase().equals("30:85:a9:6a:cb:ee")){
					ap2Strength = result.level;
					ap2Found=true;
					indoorMapView.setAp2Strength(ap2Strength);

					Log.d("WIFI", "found 2 = "+ ap2Strength);
				}else if(result.BSSID.toLowerCase().equals("58:bf:ea:61:82:b0")){			
					ap3Strength = result.level;
					indoorMapView.setAp3Strength(ap3Strength);
					ap3Found=true;

					Log.d("WIFI", "found 3 = "+ ap3Strength);
				}
		
			}
			

			ap1Found=true;
			ap2Found=true;
			ap3Found=true;
			
			
		
		
		    
			int detectedLocation  = 3; // default is hallway
			
			if(bMatchNearest){
			if(ap1Found && ap2Found && ap3Found){
				detectedLocation = FingerprintMatching.getNearestLocation(ap1Strength, ap2Strength, ap3Strength);
			
			}
			else if(ap3Found && ap1Found){
				ap2Strength = -100;
				detectedLocation = FingerprintMatching.getNearestLocation(ap1Strength, ap2Strength, ap3Strength);
			}
			else if(ap1Found && ap2Found){
				ap3Strength = -100;
				detectedLocation = FingerprintMatching.getNearestLocation(ap1Strength, ap2Strength, ap3Strength);
			}
			else if(ap2Found && ap3Found){
				ap1Strength = -100;
				detectedLocation = FingerprintMatching.getNearestLocation(ap1Strength, ap2Strength, ap3Strength);
			}
			}
			else{				
				detectedLocation = FingerprintMatching.getExactLocation(ap1Strength, ap2Strength, ap3Strength);
				
			}
			
			indoorMapView.setAp1Strength(ap1Strength);
			indoorMapView.setAp2Strength(ap2Strength);
			indoorMapView.setAp3Strength(ap3Strength);
			//indoorMapView.invalidate();
			
			
	    	indoorMapView.setDetectedLocation(detectedLocation);	
			//indoorMapView.invalidate();
	    	indoorMapView.StartFlickeringWifiIcon();
	    	
	    	long stopTime = System.currentTimeMillis();
		    long elapsedTime = stopTime - startTime;
		    
		    long scanTime = timeToFinishScan - startTime;
		    long fingerprintMatchingTime = stopTime - timeToFinishScan;
		  //  Log.d("TIMEE", "total time = " + elapsedTime);
		  //  Log.d("TIMEE", "scan time = " + scanTime);
		   // Log.d("TIMEE", "fingerprint matching time = " + fingerprintMatchingTime);
		    totalTime += elapsedTime;
		    count++;
		  //  Log.d("TIMEE", "total time AVG= " + (float)totalTime/(float)count);
		   // Log.d("TIMEE", "count= " + count);
		    
			//Log.d("WIFI1", "Updated1");	    	
			
			Toast.makeText(getApplicationContext(), "Wifi updated", Toast.LENGTH_LONG);
		}		
	}
	

	

}
