package com.example.indoorgps_prototype;

import java.util.HashMap;

import android.util.Log;

public class FingerprintMatching {	
	static class Range{
		public double min;
		public double max;
		
		public Range(double max,double min){
			this.min = min;
			this.max = max;
		}
		
	}

	static Range [][]roomAverages = new Range[][]{{new Range(-64,-68),new Range(-29,-35),new Range(-59,	-65)},
			{new Range(-49,	-59),	new Range(-75,-100),	new Range(-75,	-80)},
			{new Range(-74,	-78),new Range(-64,	-78),new Range(-58,	-60)}		
	};
	
	
	static Range [][]roomAveragesExact = new Range[][]{{new Range(-74,-100),new Range(-25,-51),new Range(-55,	-75)},
			{new Range(-32,	-64),	new Range(-70,-100),	new Range(-64,	-79)},
			{new Range(-65,	-88),new Range(-52,	-70),new Range(-48,	-57)}		
	};
		
	private static  double minimumDifference=100;
	private static  int minimumIndex = -1;	
		
	private FingerprintMatching() {
	}

	public static int getExactLocation(double ap1Strength,double ap2Strength,double ap3Strength){
		minimumDifference = 100;
		int detectedIndex = -1;
		
		boolean bAP1IsInRange= false;
		boolean bAP2IsInRange= false;
		boolean bAP3IsInRange= false;
		
		for( int i = 0; i < 3; i++){
			bAP1IsInRange = rangeContains(roomAveragesExact[i][0].min, roomAveragesExact[i][0].max, ap1Strength);
			bAP2IsInRange = rangeContains(roomAveragesExact[i][1].min, roomAveragesExact[i][1].max, ap2Strength);
			bAP3IsInRange = rangeContains(roomAveragesExact[i][2].min, roomAveragesExact[i][2].max, ap3Strength);
			
			if(bAP1IsInRange && bAP2IsInRange && bAP3IsInRange){
					detectedIndex = i;
				
			}
		}
		
		if(detectedIndex < 0){
			// if no location was found -> detected index will be 3 which is hallway
			detectedIndex = 3;
			
		}
		return detectedIndex;
	}

	public static int getNearestLocation(double ap1Strength,double ap2Strength,double ap3Strength){
		minimumDifference = 100;
		minimumIndex = 0;
		
		
		// calculate distance from room 340
		double ap1Difference ;
		double ap2Difference ;
		double ap3Difference ;
		
		
		if(!rangeContains(roomAverages[0][0].min, roomAverages[0][0].max, ap1Strength)){
			if(ap1Strength < roomAverages[0][0].min){
				ap1Difference = Math.pow( (double) (roomAverages[0][0].min - ap1Strength),2);
			}else{
				ap1Difference = Math.pow( (double) (roomAverages[0][0].max - ap1Strength),2);
			}
		}
		else{
			ap1Difference = 0;
		}
		
		if(!rangeContains(roomAverages[0][1].min, roomAverages[0][1].max, ap2Strength)){
			if(ap2Strength < roomAverages[0][1].min){
				ap2Difference = Math.pow( (double) (roomAverages[0][1].min - ap2Strength),2);
			}else{
				ap2Difference = Math.pow( (double) (roomAverages[0][1].max - ap2Strength),2);
			}
		}
		else{
			ap2Difference = 0;
		}
		
		if(!rangeContains(roomAverages[0][2].min, roomAverages[0][2].max, ap3Strength)){
			if(ap3Strength < roomAverages[0][2].min){
				ap3Difference = Math.pow( (double) (roomAverages[0][2].min - ap3Strength),2);
			}else{
				ap3Difference = Math.pow( (double) (roomAverages[0][2].max - ap3Strength),2);
			}
		}
		else{
			ap3Difference = 0;
		}
		
		double totalDifference = ap1Difference + ap2Difference + ap3Difference;
		
		Log.d("TRI","Distance RM340 = " + totalDifference);		
		minimumDifference = totalDifference;
		
		
		for(int i =1;i<3;i++){			
			
			if(!rangeContains(roomAverages[i][0].min, roomAverages[i][0].max, ap1Strength)){
				if(ap1Strength < roomAverages[i][0].min){
					ap1Difference = Math.pow( (double) (roomAverages[i][0].min - ap1Strength),2);
				}else{
					ap1Difference = Math.pow( (double) (roomAverages[i][0].max - ap1Strength),2);
				}
			}
			else{
				ap1Difference = 0;
			}
			
			if(!rangeContains(roomAverages[i][1].min, roomAverages[i][1].max, ap2Strength)){
				if(ap2Strength < roomAverages[i][1].min){
					ap2Difference = Math.pow( (double) (roomAverages[i][1].min - ap2Strength),2);
				}else{
					ap2Difference = Math.pow( (double) (roomAverages[i][1].max - ap2Strength),2);
				}
			}
			else{
				ap2Difference = 0;
			}
			
			if(!rangeContains(roomAverages[i][2].min, roomAverages[i][2].max, ap3Strength)){
				if(ap3Strength < roomAverages[i][2].min){
					ap3Difference = Math.pow( (double) (roomAverages[i][2].min - ap3Strength),2);
				}else{
					ap3Difference = Math.pow( (double) (roomAverages[i][2].max - ap3Strength),2);
				}
			}
			else{
				ap3Difference = 0;
			}
			
			
			
			totalDifference = ap1Difference + ap2Difference + ap3Difference;
			if(totalDifference < minimumDifference){
				minimumDifference = totalDifference;
				minimumIndex = i;
			}												
		}
		
		return minimumIndex;
	}
	
	public static boolean rangeContains(double min, double max, double n) {
	    return (n >= min) && (n <= max);
	}
}
