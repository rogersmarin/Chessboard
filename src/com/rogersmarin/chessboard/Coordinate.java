package com.rogersmarin.chessboard;

import java.util.List;

/**
 * Class that represents a set of coordinates of the board
 * @author roger
 *
 */
public class Coordinate {
	protected Integer y;
	protected Integer x;
	
	public Coordinate(Integer y, Integer x) {
		this.y = y;
		this.x = x;
	}
	
	public Coordinate(List<Integer> coordinates) {
		y = coordinates.get(0);
		x = coordinates.get(1);
	}
	
	public Integer getX() {
		return x;
	}
	
	public Integer getY() {
		return y;
	}
	
	
	public boolean equals(Object o) {
		if (o instanceof Coordinate) {
			Coordinate coordinate = (Coordinate) o;
			return (y == coordinate.y && x == coordinate.x);
		}
		return false;
	}
	
	public String toLetter(int i) {
		if (i == 1) return "a";
		else if (i == 2) return "b";
		else if (i == 3) return "c";
		else if (i == 4) return "d";
		else if (i == 5) return "e";
		else if (i == 6) return "f";
		else if (i == 7) return "g";
		else if (i == 8) return "h";
		return "";
	}
	
	public int hashCode() {
		return (y*31)^x;
	}
	
	public String toString() {
		return toLetter(y).toUpperCase() + x;
	}
}
