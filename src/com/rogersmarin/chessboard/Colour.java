package com.rogersmarin.chessboard;

/**
 * Enum that represents the player colour
 * @author roger
 *
 */
public enum Colour {

	WHITE("white"),
	BLACK("black");
	
	private String colour;
	
	private Colour(String colour){	
		this.colour = colour;
	}
	
	public String getColour(){
		return this.colour;
	}
}
