package com.rogersmarin.chessboard.test;


import static org.junit.Assert.*;


import org.junit.BeforeClass;
import org.junit.Test;

import com.rogersmarin.chessboard.ChessBoard;
import com.rogersmarin.chessboard.Coordinate;
import com.rogersmarin.chessboard.Piece;
import com.rogersmarin.chessboard.PieceType;


public class BoardAPITest {
	
	private static ChessBoard board = null;
	
	 @BeforeClass
	 public static void setUpBeforeClass() throws Exception {
		 board = new ChessBoard();
	 }
	
	 
	 @Test public void testPieceExistsAtCoordinate() {
	        Coordinate coord = new Coordinate(4, 1);
			assertNotNull(board.getPieceAt(coord));
     }
	 
	 @Test public void removePieceFrom(){
		Coordinate from = new Coordinate(4, 1);
		assertNotNull(board.getPieceAt(from));
		board.removePieceAt(from);
		assertNull(board.getPieceAt(from));
	 }
	 
	 @Test public void testSetPieceAtPosition() {
        Coordinate from = new Coordinate(5, 1);
        Piece piece = board.getPieceAt(from);
        Coordinate to = new Coordinate(5, 3);
		board.movePiece(piece, from , to);
		piece = board.getPieceAt(to);
		assertNull(board.getPieceAt(from));
		assertNotNull(board.getPieceAt(to));
		assertTrue(board.getPieceAt(to).getType().equals(PieceType.KING));
	  }
	 
	 
	 

}
