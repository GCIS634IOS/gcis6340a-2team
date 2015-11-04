package com.example.indoorgps_prototype;

import android.graphics.Point;

public class IndoorMapObject {
	private int id;
	private String description;
	private Point startPosition;
	private int width;
	private int height;
	
	public IndoorMapObject(int id, String description, Point startPosition, int width, int height) {
		super();
		this.id = id;
		this.description = description;
		this.startPosition = startPosition;
		this.width = width;
		this.height = height;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Point getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(Point startPosition) {
		this.startPosition = startPosition;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	
	
	

}
