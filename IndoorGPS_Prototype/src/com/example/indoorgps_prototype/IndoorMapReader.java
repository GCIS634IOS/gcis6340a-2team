package com.example.indoorgps_prototype;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

public class IndoorMapReader {
	private static final String Tag = "XMLREAD";
	private Context context;
	private String xmlFileName;
	private boolean bInsideRoomInfoTag=false;
	private boolean bInsideObjectTag;
	private MapInfo mapInfo;
		
	
	public IndoorMapReader(Context context,String xmlFileName) {		
		// Map info
		 int id=0;
		 String title=null;
		 String version=null;
		 String description=null;
		 int numberOfObjects=0;
		 int numberOfRooms=0;
		// ----------------------
		 // room/object info
		 
		 int roomID=0;
		 String roomName=null;
		 Point startPosition=null;
		 int width=0;
		 int height=0;
		 
		 //------------------
		 
		 
		 
		this.context = context;
		this.xmlFileName = xmlFileName;
		
		
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);			
	        XmlPullParser xpp = factory.newPullParser();
	        InputStream xmlFileStream=null;
	        
	        try {
				xmlFileStream = context.getAssets().open("ZurnMap.xml");
				
				Log.d("XMLREAD", "file opened");			 
		        xpp.setInput(xmlFileStream,null);
	
		        int event;
		        while ((event = xpp.next()) != XmlPullParser.END_DOCUMENT)
		        {
		        	 int type = xpp.getEventType();
		        	 
		        	 if(type == XmlPullParser.START_DOCUMENT) {
	
		                 Log.d(Tag, "In start document");
		             }
		             else if(type == XmlPullParser.START_TAG) {
		                 
		            	 if(bInsideRoomInfoTag){
		            		 if(xpp.getName().equalsIgnoreCase("title")){
		            			 xpp.next();
		            			 title = xpp.getText();
		            			 xpp.next();
		            		 }
		            		 else if(xpp.getName().equalsIgnoreCase("id")){
		            			 xpp.next();
		            			 id = Integer.parseInt(xpp.getText());
		            			 xpp.next();
		            		 }
		            		 else if(xpp.getName().equalsIgnoreCase("Description")){
		            			 xpp.next();
		            			 description = xpp.getText();
		            			 xpp.next();
		            		 }
		            		 else if(xpp.getName().equalsIgnoreCase("version")){
		            			 xpp.next();
		            			 version = xpp.getText();
		            			 xpp.next();
		            		 }
		            		 else if(xpp.getName().equalsIgnoreCase("NumberOfRooms")){
		            			 xpp.next();
		            			 numberOfRooms = Integer.parseInt(xpp.getText());
		            			 xpp.next();
		            		 } else if(xpp.getName().equalsIgnoreCase("NumberOfObjects")){
		            			 xpp.next();
		            			 numberOfRooms = Integer.parseInt(xpp.getText());
		            			 xpp.next();
		            		 }
		            	 }else if(bInsideObjectTag){
		            		 if(xpp.getName().equalsIgnoreCase("ID")){
		            			 xpp.next();
		            			 roomID = Integer.parseInt(xpp.getText());
		            			 xpp.next();
		            		 }else if(xpp.getName().equalsIgnoreCase("Width")){
		            			 xpp.next();
		            			 width = Integer.parseInt(xpp.getText());
		            			 xpp.next();
		            		 }else if(xpp.getName().equalsIgnoreCase("Height")){
		            			 xpp.next();
		            			 height = Integer.parseInt(xpp.getText());
		            			 xpp.next();
		            		 }else if(xpp.getName().equalsIgnoreCase("Name")){
		            			 xpp.next();
		            			 roomName = xpp.getText();
		            			 xpp.next();
		            		 }else if(xpp.getName().equalsIgnoreCase("StartPosition")){
		            			 xpp.next();
		            			 String startPositionText = xpp.getText();
		            			 String []tokens= startPositionText.split(",");
		            			 startPosition = new Point(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
		            			 xpp.next();
		            		 }
		            		 
		            	 }
		            	 else{
			            	 if(xpp.getName().equalsIgnoreCase("MapInfo")){
			            		 bInsideRoomInfoTag = true;		            		 
			            	 }else if(xpp.getName().equalsIgnoreCase("Object")){
			            		 bInsideObjectTag = true;		            		 
			            	 }
		            	 }
		             }
		             else if(type == XmlPullParser.END_TAG) {
		                 //Log.d(Tag, "In end tag = "+xpp.getName());
		                 if(xpp.getName().equalsIgnoreCase("MapInfo")){
		            		 bInsideRoomInfoTag = false;		           
		            		 // Creating Map Info Object
		            		 mapInfo = new MapInfo(id, title, version, numberOfObjects, numberOfRooms);
		            		 Log.d(Tag, "created map info object");
		            		 
		            	 }else if(xpp.getName().equalsIgnoreCase("Object")){
		            		 bInsideObjectTag = false;
		            		 // creating object based on read info
		            		 IndoorMapObject drawObject = new IndoorMapObject(roomID, roomName, startPosition, width, height);
		            		 // adding object to map info
		            		 mapInfo.addObject(drawObject);
		            		 Log.d(Tag, "added object");
		            	 }
		                 
	
		             }
		             else if(type == XmlPullParser.TEXT) {
		                // Log.d(Tag, "Have text = "+xpp.getText());
		                 if(xpp.isWhitespace())
		                 {
	
		                 }
		                 else
		                 {
		                     String strquery = xpp.getText();
		                   //  db.execSQL(strquery);
		                 }
	
		             }
		        }
	        } catch (IOException e) {				
				e.printStackTrace();
			}
	        
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
        
	}

	public String readFileToString(){
		 	StringBuilder buf=new StringBuilder();
		    InputStream json;
			try {
				json = context.getAssets().open("ZurnMap.xml");
				Log.d("XMLREAD", "file opened");
				 BufferedReader in=
					        new BufferedReader(new InputStreamReader(json, "UTF-8"));
					    String str;

					    while ((str=in.readLine()) != null) {
					      buf.append(str);
					    }

					    in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//	Log.d("XMLREAD", buf.toString());
			return buf.toString();
		   
	}
	public MapInfo getrMapInfo(){
		return mapInfo;
	}
}
