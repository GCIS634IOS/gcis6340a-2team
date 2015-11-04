package com.example.indoorgps_prototype;

import java.util.ArrayList;

public class MapInfo {
	private int id;
	private String title;
	private String description;
	private String version;
	private int numberOfObjects;
	private int numberOfRooms;
	private ArrayList <IndoorMapObject>listOfObjects;
	
	public MapInfo(int id, String title, String version, int numberOfObjects, int numberOfRooms) {
		super();
		this.id = id;
		this.title = title;
		this.version = version;
		this.numberOfObjects = numberOfObjects;
		this.numberOfRooms = numberOfRooms;
		listOfObjects = new ArrayList<IndoorMapObject>();
	}
	
	public void addObject(IndoorMapObject object){
		listOfObjects.add(object);
	}
	
	public IndoorMapObject getObject(int index){
		return listOfObjects.get(index);
	}
	
	public int getNumberOfObjects(){
		return listOfObjects.size();
	}
}
