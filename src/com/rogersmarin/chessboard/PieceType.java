package com.rogersmarin.chessboard;

/**
 * Enum that represents a piece type including its ASCII Code
 * @author roger
 *
 */
public enum PieceType {
	
	KING   ("\u265B", "\u2655"),
	QUEEN  ("\u265B", "\u2655"),
	BISHOP ("\u265D", "\u2657"),
	ROOK   ("\u265C", "\u2656"),
	PAWN   ("\u265F", "\u2659"),
	KNIGHT ("\u265E", "\u2658");
	
	private String blackASCII;
	private String whiteASCII;
	
	private PieceType(String blackASCII,String whiteASCII){
		this.blackASCII = blackASCII;
		this.whiteASCII = whiteASCII;
	}
	
	public String getWhiteASCII(){
		return this.whiteASCII;
	}
	
	public String getBlackASCII(){
		return this.blackASCII;
	}

}
