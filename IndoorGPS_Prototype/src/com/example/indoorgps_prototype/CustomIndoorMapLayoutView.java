package com.example.indoorgps_prototype;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;

public class CustomIndoorMapLayoutView extends View {
	Paint paint = new Paint();
	private final Handler handler = new Handler();
	private ArrayList<IndoorMapObject> listOfDrawObjects;

	int roomSize = 260;
	int hallway1Width = 160;
	int hallway1Height = 880;
	int hallway2Width = 200;
	int hallway2Height = 100;
	int room344_x = 30;
	int room344_y = 300;
	int room340_x = room344_x;
	int room340_y = room344_y + roomSize;
	int room343_x = room344_x + roomSize + hallway1Width + hallway2Width;
	int room343_y = room344_y;
	int hallway1_x = roomSize + room344_x;
	int hallway1_y = room344_y - roomSize;
	int hallway2_x = roomSize + room344_x + hallway1Width;
	int hallway2_y = room344_y - roomSize / 2;

	Rect room343;
	Rect room340;
	Rect room344;
	Rect hallway1;
	Rect hallway2;

	private final String backgroundColor = "#FFF8EB";
	private final String borderColor = "#E99139";
	private final String hallwayColor = "#ffffff";
	private final String otherColor = "#DFDBD4";
	private final String strokeColor = "#A09B93";

	int ap1Strength = 0;
	int ap2Strength = 0;
	int ap3Strength = 0;

	private int width;
	private int height;
	private int currentLocationID = 4;

	private boolean bDisplayIcon = false;

	private Bitmap locationIcon;
	private Bitmap wifiIcon;

	private static float MIN_ZOOM = 1f;
	private static float MAX_ZOOM = 5f;
	private float scaleFactor = 1.f;
	private ScaleGestureDetector detector;

	int indexCount =0;
	MapInfo mapInfo;
	

	private void initObjectLengths() {
		room340_x = room344_x;
		room340_y = room344_y + roomSize;
		room343_x = room344_x + roomSize + hallway1Width + hallway2Width;
		room343_y = room344_y;
		hallway1_x = roomSize + room344_x;
		hallway1_y = room344_y - roomSize;
		hallway2_x = roomSize + room344_x + hallway1Width;
		hallway2_y = room343_y + roomSize - hallway2Height;

		room343 = new Rect(room343_x, room343_y, room343_x + roomSize, room343_y + roomSize);
		room340 = new Rect(room340_x, room340_y, room340_x + roomSize, room340_y + roomSize);
		room344 = new Rect(room344_x, room344_y, room344_x + roomSize, room344_y + roomSize);
		hallway1 = new Rect(hallway1_x, hallway1_y, hallway1_x + hallway1Width, hallway1_y + hallway1Height);
		hallway2 = new Rect(hallway2_x, hallway2_y, hallway2_x + hallway2Width, hallway2_y + hallway2Height);

	}

	
	public CustomIndoorMapLayoutView(Context context) {
		super(context);

		// reading map infor from file
		IndoorMapReader mapReader = new IndoorMapReader(context, "ZurnMap.xml");
		mapReader.readFileToString();
		
		mapInfo = mapReader.getrMapInfo();
		
		// processing all objects to be drawn in the room
		for(int i=0;i<mapInfo.getNumberOfObjects();i++){
			IndoorMapObject indoorMapObject = mapInfo.getObject(i);
			if(indoorMapObject.getDescription().equalsIgnoreCase("room 343")){
				room343_x = indoorMapObject.getStartPosition().x;
				room343_y = indoorMapObject.getStartPosition().y;
				roomSize = indoorMapObject.getWidth();
				Log.d("TTT", "test1");
			}else if(indoorMapObject.getDescription().equalsIgnoreCase("room 344")){
				room344_x = indoorMapObject.getStartPosition().x;
				room344_y = indoorMapObject.getStartPosition().y;
				roomSize = indoorMapObject.getWidth();
				Log.d("TTT", "test2");
			}else if(indoorMapObject.getDescription().equalsIgnoreCase("room 340")){
				room340_x = indoorMapObject.getStartPosition().x;
				room340_y = indoorMapObject.getStartPosition().y;
				roomSize = indoorMapObject.getWidth();
				Log.d("TTT", "test3");
			}else if(indoorMapObject.getDescription().equalsIgnoreCase("Hallway1")){
				hallway1_x = indoorMapObject.getStartPosition().x;
				hallway1_y = indoorMapObject.getStartPosition().y;
				hallway1Width = indoorMapObject.getWidth();
				hallway1Height = indoorMapObject.getHeight();
				Log.d("TTT", "test4");
			}else if(indoorMapObject.getDescription().equalsIgnoreCase("Hallway")){
				hallway2_x = indoorMapObject.getStartPosition().x;
				hallway2_y = indoorMapObject.getStartPosition().y;
				hallway2Width = indoorMapObject.getWidth();
				hallway2Height = indoorMapObject.getHeight();
				Log.d("TTT", "test5");
			}
		}
		
		//detector = new ScaleGestureDetector(getContext(), new ScaleListener());

		// initializing array list of draw objects
		//listOfDrawObjects = new ArrayList<IndoorMapObject>();

		locationIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.dot2);
		wifiIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.wifiicon);

		DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
		width = metrics.widthPixels;
		height = metrics.heightPixels;

		if (width > 1000) {
			Log.d("CView", "width < 1000");
			roomSize = roomSize * 2;
			hallway1Width = hallway1Width * 2;
			hallway1Height = hallway1Height * 2;
			hallway2Width = hallway2Width * 2;
			hallway2Height = hallway2Height *2;
		}

		initObjectLengths();

		setWillNotDraw(false);
		paint.setColor(Color.LTGRAY);
	}

	private void drawObjectsBackground(Canvas canvas) {
		// drawing room blocks
		// first part filling with white color
		paint.setColor(Color.parseColor(backgroundColor));
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(room343, paint);
		canvas.drawRect(room344, paint);
		canvas.drawRect(room340, paint);
		paint.setColor(Color.parseColor(hallwayColor));
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(hallway1, paint);
		canvas.drawRect(hallway2, paint);

	}

	private void drawObjectsLabel(Canvas canvas) {
		// drawing lables
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.FILL);
		paint.setTextSize(20);
		canvas.drawText("Room343", room343_x + roomSize / 10, room343_y + 40, paint);
		canvas.drawText("Room344", room344_x + roomSize / 10, room344_y + 40, paint);
		canvas.drawText("Room340", room340_x + roomSize / 10, room340_y + 40, paint);
		canvas.save();
		canvas.rotate(90f, hallway1_x + hallway1Width / 2 - 10, hallway1_y + hallway1Height / 2 - 40);
		canvas.drawText("Hallway", hallway1_x + hallway1Width / 2 - 10, hallway1_y + hallway1Height / 2 - 40, paint);
		canvas.restore();
		// canvas.drawText("", hallway2_x+hallway2Width/10, hallway2_y+40,
		// paint);
	}

	private void drawObjectsBorder(Canvas canvas) {
		// drawing room blocks borders
		paint.setColor(Color.parseColor(borderColor));
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(room343, paint);
		canvas.drawRect(room344, paint);
		canvas.drawRect(room340, paint);
		canvas.drawRect(hallway1, paint);
		canvas.drawRect(hallway2, paint);
		// clearing left border of hallway2 because it should be connected to
		// hallway1
		paint.setColor(Color.parseColor(backgroundColor));
		paint.setStyle(Paint.Style.FILL);
		canvas.drawLine(hallway2_x, hallway2_y, hallway2_x, hallway2_y + hallway2Height, paint);
	}

	public void drawFloorPlan(Canvas canvas) {

		// canvas.drawText("Hallway", 30, 340, paint);
		drawObjectsBackground(canvas);
		drawObjectsBorder(canvas);
		drawObjectsLabel(canvas);

		paint.setColor(Color.BLACK);
		paint.setTextSize(30);

		Paint fillPaint = new Paint();
		fillPaint.setColor(Color.BLACK);
		fillPaint.setTextSize(30);
		canvas.drawText("Zurn Building Indoor Map", width / 2 - 160, 50, fillPaint);

	

		paint.setColor(Color.RED);
		paint.setTextSize(18);
		// canvas.drawText("Ap1=" + ap1Strength + " ; " + "Ap2=" + ap2Strength +
		// " ; " + "Ap2=" + ap3Strength , 10, 110, paint);

	

		// highlighting current location
		highlightCurrentLocation(canvas);

		
		// drawing refresh icon
		if (!bDisplayIcon) {
			// paint.setColor(Color.BLUE);
			// paint.setStyle(Paint.Style.FILL);
			// canvas.drawRect(new Rect(width-40, 0,width, 40), paint);
			// bRefreshIcon = !bRefreshIcon;
			Log.d("AAA"," NO draw icon");
		} else {

			// paint.setColor(Color.RED);
			// paint.setStyle(Paint.Style.FILL);
			// canvas.drawRect(new Rect(width-40, 0,width, 40), paint);
			// bRefreshIcon = !bRefreshIcon;
			Log.d("AAA","draw icon");
			
			canvas.drawBitmap(wifiIcon, width - wifiIcon.getWidth(), this.height - 160-wifiIcon.getHeight(), null);
		}
		
	}

	private void highlightCurrentLocation(Canvas canvas) {
		paint.setColor(Color.rgb(255, 132, 112));
		paint.setStyle(Paint.Style.FILL);

		// rm340
		// rm343
		// rm344
		// hallway
		switch (currentLocationID) {
		case 0:
			canvas.drawBitmap(locationIcon, room340_x + roomSize / 2 - locationIcon.getWidth() / 2, room340_y
					+ roomSize / 2 - locationIcon.getHeight() / 2, null);

			break;
		case 1:
			canvas.drawBitmap(locationIcon, room343_x + roomSize / 2 - locationIcon.getWidth() / 2, room343_y
					+ roomSize / 2 - locationIcon.getHeight() / 2, null);

			break;
		case 2:
			canvas.drawBitmap(locationIcon, room344_x + roomSize / 2 - locationIcon.getWidth() / 2, room344_y
					+ roomSize / 2 - locationIcon.getHeight() / 2, null);

			break;
		case 3:
			canvas.drawBitmap(locationIcon, hallway2_x - locationIcon.getWidth() / 2 - 10, hallway2_y + hallway2Height
					/ 2 - locationIcon.getHeight() / 2, null);

			break;

		default:
			break;
		}

	}

	private void ClearBackground(Canvas canvas) {
		canvas.drawColor(Color.parseColor(otherColor));

	}

	public void setDetectedLocation(int locationID) {
		currentLocationID = locationID;
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		canvas.scale(scaleFactor, scaleFactor);

		ClearBackground(canvas);
		drawFloorPlan(canvas);
		drawLocalityLabel(canvas);
		drawSignalStrengths(canvas);
		canvas.restore();
	}

	private void drawSignalStrengths(Canvas canvas) {
		// TODO Auto-generated method stub
	//	paint.setColor(Color.BLACK);
	//	canvas.drawText(""+ap1Strength, 20, 100, paint);
	//	canvas.drawText(""+ap2Strength, 120, 100, paint);
	//	canvas.drawText(""+ap3Strength, 220, 100, paint);
		
	}

	public int getAp1Strength() {
		return ap1Strength;
	}

	public void setAp1Strength(int ap1Strength) {
		this.ap1Strength = ap1Strength;
	}

	public int getAp2Strength() {
		return ap2Strength;
	}

	public void setAp2Strength(int ap2Strength) {
		this.ap2Strength = ap2Strength;
	}

	public int getAp3Strength() {
		return ap3Strength;
	}

	public void setAp3Strength(int ap3Strength) {
		this.ap3Strength = ap3Strength;
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {

			scaleFactor *= detector.getScaleFactor();

			scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));

			invalidate();

			return true;

		}

	}

	private void drawLocalityLabel(Canvas canvas) {
		TextView textView = new TextView(getContext());
		int width = this.width;
		int height = (int) (60);
		textView.layout(0, 0, width, height);
		
		switch(currentLocationID){
		case 0:
			textView.setText("Current Locality: Room 340");
			break;
		case 1:
			textView.setText("Current Locality: Room 343");
			break;
		case 2:
			textView.setText("Current Locality: Room 344");
			break;
		case 3:
			textView.setText("Current Locality: Hallway");
			break;
				
		
		}
		textView.setBackgroundColor(Color.parseColor(borderColor));
		textView.setTextColor(Color.WHITE);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		// textView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		Bitmap bitmapText = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvasText = new Canvas(bitmapText);
		textView.draw(canvasText);

		canvas.drawBitmap(bitmapText, 0, this.height - 160, null);
	}

	public boolean isbDisplayIcon() {
		return bDisplayIcon;
	}

	public void setbDisplayIcon(boolean bDisplayIcon) {
		this.bDisplayIcon = bDisplayIcon;
	}
	
	public void StartFlickeringWifiIcon(){
		indexCount=0;
		handler.post(flickerWifiRunnable);
	}
	Runnable flickerWifiRunnable = new Runnable() {

		@Override
		public void run() {
			
			switch(indexCount){
			case 0:
				bDisplayIcon = true;
				invalidate();
				break;
			case 1:
				bDisplayIcon = false;
				invalidate();
				break;
			case 2:
				bDisplayIcon = true;
				invalidate();
				break;
			case 3:
				bDisplayIcon = false;
				invalidate();
				break;
			}
			indexCount++;
			if(indexCount<4){
				handler.postDelayed(flickerWifiRunnable, 250);
			}
			

		}
	};
}

	
	
