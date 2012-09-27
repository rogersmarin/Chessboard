package com.rogersmarin.chessboard;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that represents an 8x8 chess board
 * @author roger
 *
 */
public class ChessBoard implements Cloneable {
	
	private boolean check;
	private Piece capture = null;
	private HashMap<Coordinate, Piece> positions;
	private EnumMap<Colour, Set<Coordinate>> attackedCoordinates;
	
	/**
	 * 
	 * @throws Exception
	 */
	public ChessBoard() throws Exception {
		positions = new HashMap<Coordinate, Piece>();
		init();
		setAttackedCoordinates();
	}
	
	
	/**
	 * Returns the set of pieces of a given colour on the board
	 * @param colour
	 * @return
	 */
	public Set<Piece> getPieces(Colour colour) {
		Set<Piece> pieces = new HashSet<Piece>();
		for (Piece p : positions.values()) {
			if (p != null) {
				if (p.getColour().equals(colour)) pieces.add(p);
			}
		}
		return pieces;
	}

	/**
	 * 
	 * @param check
	 */
	public void setCheck(boolean check) { 
		this.check = check;
	}
	
	/**
	 * Returns the piece at the specified position
	 * @param coordinate
	 * @return
	 */
	public Piece getPieceAt(Coordinate coordinate) {
		return positions.get(coordinate);
	}
	
	/**
	 * Sets the piece to the specified position(coordinate)
	 * if the position is not empty then the piece occupying the position 
	 * is removed before setting the piece to the position
	 * @param coordinate the position to set the piece to
	 * @param piece
	 */
	public void setPieceAt(Coordinate coordinate, Piece piece) {
		if(!isPositionEmpty(coordinate)){
			removePieceAt(coordinate);
		}
		positions.put(coordinate, piece);
	}
	
	/**
	 * Removes a piece from a given coordinate
	 * @param coordinate
	 */
	public void removePieceAt(Coordinate coordinate) {
		positions.put(coordinate, null);
		try {
			setAttackedCoordinates();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns true if the specified coordinate set is empty
	 * @param coordinate
	 * @return
	 */
	public boolean isPositionEmpty(Coordinate coordinate){
		return !(positions.get(coordinate)!=null);
	}
	
	/**
	 * Sets the coordinates attacked 
	 * @throws Exception
	 */
	public void setAttackedCoordinates() throws Exception {
		attackedCoordinates = new EnumMap<Colour, Set<Coordinate>>(Colour.class);
		attackedCoordinates.put(Colour.WHITE, new HashSet<Coordinate>());
		attackedCoordinates.put(Colour.BLACK, new HashSet<Coordinate>());
		for (Coordinate coordinate : new HashSet<Coordinate>(positions.keySet())) {
			Piece piece = positions.get(coordinate);
			if (piece != null) {
				attackedCoordinates.get(piece.oppositePlayer()).addAll(piece.getValidCoordinates(this, true));
			}
		}
	}
	
	/**
	 * Return the set of attacked coordinates
	 * @param colour
	 * @return
	 * @throws Exception
	 */
	public Set<Coordinate> getAttackedCoordinates(Colour colour) throws Exception {
		return attackedCoordinates.get(colour);
	}
	
	
	/**
	 * Returns true if the specified colour is under check
	 * @param colour
	 * @return
	 */
	public boolean isCheck(Colour colour) {
		try {
			for (Coordinate coord : getAttackedCoordinates(colour)) {
				Piece attackedPiece = getPieceAt(coord);
				if (attackedPiece != null) {
					if (attackedPiece.getType().equals(PieceType.KING) 
							&& attackedPiece.getColour().equals(colour)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Returns the king for a given colour
	 * @param colour
	 * @return
	 */
	public Piece getKing(Colour colour) {
		for (Piece p : positions.values()) {
			if (p != null) {
				if (p.getType().equals(PieceType.KING) 
						&& p.getColour().equals(colour)) {
					return p;
				}
			}
		}
		return null;
	}
	
	/**
	 * Moves a piece to the specified coordinate, if the coordinate set is not empty it does not remove the piece
	 * but instead tries to capture the piece
	 * @param piece
	 * @param move
	 */
	public void movePiece(Piece piece, Coordinate move) {
		positions.put(piece.getPosition(), null);
		Piece opposite;
		capture =  ((opposite = positions.get(move)) != null) ? opposite : null;
		positions.put(move, piece);
		try {
			setAttackedCoordinates();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Moves a piece between 2 coordinates, if the to coordinate set is not empty it does not remove the piece
	 * but instead tries to capture the piece 
	 * @param piece
	 * @param from
	 * @param to
	 */
	public void movePiece(Piece piece, Coordinate from, Coordinate to) {
		positions.put(from, null);
		Piece opposite;
		capture =  ((opposite = positions.get(to)) != null) ? opposite : null;
		positions.put(to, piece);
		try {
			setAttackedCoordinates();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Moves a piece back to the given coordinate, used to move piece under check
	 * @param piece
	 * @param move
	 */
	public void movePieceBack(Piece piece, Coordinate move) {
		positions.put(move, capture);
		positions.put(piece.getPosition(), piece);
		try {
			setAttackedCoordinates();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Resets the board to starting point
	 * @throws Exception
	 */
	public void reset() throws Exception{
		positions = new HashMap<Coordinate, Piece>();
		init();
		setAttackedCoordinates();
	}
	
	/**
	 * Displays the board as ASCII characters.
	 */
	public String toString() {
		String rowNames = "\t     A   B   C   D   E   F   G   H";
		StringBuffer buff = new StringBuffer();
		buff.append("\n" + rowNames + "\n\n");
		for (int row = 8; row >= 1; row--) {
			buff.append("\t" + row + "  | ");
			for (int col = 1; col <= 8; col++) {
				Piece piece = getPieceAt(new Coordinate(col, row));
				if (piece == null) {
					buff.append("- | ");
				} else {
					buff.append(piece.toString() + " | ");
				}
			}
			buff.append(" " + row + "\n\n");
		}
		buff.append(rowNames + "\n");
		return buff.toString();
	}
	
	
	public Object clone() {
		try {
			ChessBoard clonedBoard = (ChessBoard) super.clone();
			for (Coordinate coord : clonedBoard.positions.keySet()) {
				Piece p = clonedBoard.getPieceAt(coord);
				if (p != null) {
					clonedBoard.setPieceAt(coord, new Piece(coord, p.getType(), p.getColour()));
				}
			}
			return clonedBoard;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void addPiece(int column, int row, PieceType type) {
		Coordinate coord = new Coordinate(column, row);
		Colour colour = ((row < 3) ? Colour.WHITE : Colour.BLACK);
		positions.put(coord, new Piece(coord, type, colour));
	}
	
	
	private void init() {
		int[] columns = {1,2,3,4,5,6,7,8};
		int[] rows = {1,2,7,8};
		
		for (int col : columns) {
			for (int row : rows) {
				if (row==2 || row==7) {
					addPiece(col,row,PieceType.PAWN);
				} else if (col==1 || col==8) {
					addPiece(col,row,PieceType.ROOK);
				} else if (col==2 || col==7) {
					addPiece(col, row, PieceType.KNIGHT);
				} else if (col==3 || col==6) {
					addPiece(col, row,PieceType.BISHOP);
				} else if (col==4) {
					addPiece(col, row, PieceType.QUEEN);
				} else {
					addPiece(col, row, PieceType.KING);
				}
			}
		}
	}
	
}
