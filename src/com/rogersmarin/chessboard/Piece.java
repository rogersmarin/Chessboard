package com.rogersmarin.chessboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that represents a Chess Piece
 * @author roger
 *
 */
public class Piece {
	private Coordinate position;
	private PieceType pieceType;
	private Colour colour;
	private ChessBoard chessBoard;
	private Coordinate move;
	private boolean moved = false;
	private Set<Coordinate> validMoves;
	private Map<String, Coordinate> specialMoves;
	
	Piece(Coordinate position, PieceType type, Colour colour) {
		this.position = position;
		this.colour = colour;
		this.pieceType = type;
	}

	/**
	 * Checks the specified board and move to determine the possible piece moves
	 * @param move
	 * @param board
	 * @return
	 * @throws Exception
	 */
	public List<Boolean> checkBoard(Coordinate move, ChessBoard board) throws Exception {
		this.move = move;
		this.chessBoard = board;
		List<Boolean> moveChecks = new ArrayList<Boolean>(); 
		setPieceMoves(position);
		moveChecks.add(isValidMove());
		boolean check = isCheck(oppositePlayer());
		board.setCheck(check);
		moveChecks.add(check);
		moveChecks.add(isCheckmate(check));
		return moveChecks;
	}
	
	/**
	 * Returns the piece Colour
	 * @return
	 */
	public Colour getColour() {
		return colour;
	}
	
	/**
	 * Returns the pieces position
	 * @return
	 */
	public Coordinate getPosition() {
		return position;
	}
	
	/**
	 * Returns the piece type
	 * @return
	 */
	public PieceType getType() {
		return pieceType;
	}
	
	
	/**
	 * Checks diagonal piece moves for a particular coordinate set
	 * @param move
	 * @return
	 */
	private boolean checkDiagMove(Coordinate move) {
		int row, col;
		if (position.y < move.y && position.x < move.x) {
			row = position.x + 1;
			for (col = position.y + 1; col<move.y; col++) {
				if (!chessBoard.isPositionEmpty(new Coordinate(col,row))) return true;
				row += 1;
			}
		} else if (position.y < move.y && position.x > move.x) {
			row = position.x - 1;
			for (col = position.y + 1; col<move.y; col++) {
				if (!chessBoard.isPositionEmpty(new Coordinate(col,row))) return true;
				row -= 1;
			}
		} else if (position.y > move.y && position.x < move.x) {
			row = position.x + 1;
			for (col = position.y - 1; col>move.y; col--) {
				if (!chessBoard.isPositionEmpty(new Coordinate(col,row))) return true;
				row += 1;

			}
		} else if (position.y > move.y && position.x > move.x) {
			row = position.x - 1;
			for (col = position.y - 1; col > move.y; col--) {
				if (!chessBoard.isPositionEmpty(new Coordinate(col,row))) return true;
				row -= 1;
			}
		}
		return false;
	}
	
    /**
     * Returns all the valid moves for the piece in the board
     * @param board
     * @param attack if the piece belongs to an attacked coordinate set
     * @return
     */
	public Set<Coordinate> getValidCoordinates(ChessBoard board, boolean attack) {
		this.chessBoard = board;
		BlockAttack bc = new BlockAttack(attack);
		Set<Coordinate> validMoves = new HashSet<Coordinate>();
		if (pieceType.equals(PieceType.ROOK)) {
			validMoves = validRookMoves(bc);
		} else if (pieceType.equals(PieceType.KNIGHT)) {
			validMoves = validKnightMoves(bc);
		} else if (pieceType.equals(PieceType.BISHOP)) {
			validMoves = validBishopMoves(bc);
		} else if (pieceType.equals(PieceType.QUEEN)) {
			validMoves = validQueenMoves(bc);
		} else if (pieceType.equals(PieceType.KING)) {
			validMoves = validKingMoves(bc);
		} else if (pieceType.equals(PieceType.PAWN)) {
			validMoves = validPawnMoves(bc);
		}
		//If the piece belongs to an attacked coordinate set verify the possible moves if under check
		if (!attack) validMoves = setCheckMoves(validMoves);
		
		return validMoves;
	}
	
	/**
	 * Returns true if the piece has been moved
	 * @return
	 */
	public boolean moved() {
		return moved;
	}
	
	
	/**
	 * Returns the ASCII code for the piece
	 */
	public String toString() {
		return isBlack()? pieceType.getBlackASCII() : pieceType.getWhiteASCII();
	}
	
	/**
	 * Sets the pieces position
	 * @param coordinate
	 */
	public void setPosition(Coordinate coordinate) {
		position = coordinate;
	}

	private boolean isCheck(Colour oppositePlayer) {
		try {
			for (Coordinate coord : chessBoard.getAttackedCoordinates(oppositePlayer)) {
				Piece attackedPiece = chessBoard.getPieceAt(coord);
				if (attackedPiece != null) {
					if (attackedPiece.pieceType.equals(PieceType.KING) 
							&& attackedPiece.getColour().equals(oppositePlayer)){
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	

	private boolean isCheckmate(boolean check) throws Exception {
		if (check) {
			Piece oppositeKing = chessBoard.getKing(oppositePlayer());
			BlockAttack bc = new BlockAttack();
			if (oppositeKing.getValidCoordinates(chessBoard, false).isEmpty()) {
				for (Piece p : chessBoard.getPieces(oppositePlayer())) {
					for (Coordinate m : p.getValidCoordinates(chessBoard, false)) {
						chessBoard.movePiece(p, m);
						if (bc.blocked(chessBoard, position, oppositeKing.getPosition())) {
							chessBoard.movePieceBack(p, m);
							return false;
						}
						chessBoard.movePieceBack(p, m);
					}
				}
				return true;
			}
		}
		return false;
	}
	
	private boolean isHorizontalMove(Coordinate move) {
		return position.x == move.x;
	}
	
	private boolean isVerticalMove(Coordinate move) {
		return position.y == move.y;
	}
	
	private boolean isValidMove () {
		try {
			if (position != move && isValidMove(move)) {
				chessBoard.movePiece(this, move);
				setPosition(move);
				chessBoard.setAttackedCoordinates();
				moved = true;
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean onBoard(Coordinate s) {
		if (s.x >= 1 && s.y >=1 && s.x <= 8 && s.y <= 8) {
			return true;
		}
		return false;
	}
	
	protected boolean isBlack() {
		return colour.equals(Colour.BLACK); 
	}
	
	private void reduceToCheckMoves() {
		validMoves = setCheckMoves(validMoves);
	}
	
	private Set<Coordinate> setCheckMoves(Set<Coordinate> validMoves) {
		Set<Coordinate> checkMoves = new HashSet<Coordinate>();
		for (Coordinate m : validMoves) {
			if (!inCheck(m))	checkMoves.add(m);
		}
		
		try {
			int row = colour.equals(Colour.WHITE) ? 1 : 8;
			if (pieceType.equals(PieceType.KING)) {
				if (!inCheck(specialMoves.get("king1-4")) && !inCheck(specialMoves.get("king1-3"))) {
					checkMoves.add(new Coordinate(3,row));
				}
			}
		} catch (NullPointerException e){}
		return checkMoves;
	}
	
	
	
	private void setPieceMoves(Coordinate position) throws Exception {
		BlockAttack bc = new BlockAttack();
		if (pieceType.equals(PieceType.ROOK)) {
			validMoves = validRookMoves(bc);
		} else if (pieceType.equals(PieceType.KNIGHT)) {
			validMoves = validKnightMoves(bc);
		} else if (pieceType.equals(PieceType.BISHOP)) {
			validMoves = validBishopMoves(bc);
		} else if (pieceType.equals(PieceType.QUEEN)) {
			validMoves = validQueenMoves(bc);
		} else if (pieceType.equals(PieceType.KING)) {
			validMoves = validKingMoves(bc);
		} else if (pieceType.equals(PieceType.PAWN)) {
			validMoves = validPawnMoves(bc);
		} else {
			throw new Exception("invalid piece type!");
		}
		reduceToCheckMoves();
	}
	
	
	
	protected Colour oppositePlayer() {
		if (colour.equals(Colour.WHITE)) return Colour.BLACK;
		return Colour.WHITE;
	}
	
	
	private boolean isValidMove(Coordinate move) throws Exception {
		if (validMoves.contains(move)) {
			return true;
		}
		return false;
	}
	
	private Set<Coordinate> validRookMoves(BlockAttack bc) {
		Set<Coordinate> validMoves = new HashSet<Coordinate>();
		try {
			for (int i = 1; i<=8; i++) {
				Coordinate xMove = new Coordinate(i, position.x);
				Coordinate yMove = new Coordinate(position.y, i);
				if (!xMove.equals(position) && !bc.blocked(position, xMove)) validMoves.add(xMove);
				if (!yMove.equals(position) && !bc.blocked(position, yMove)) validMoves.add(yMove);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return validMoves;
	}
	
	private Set<Coordinate> validKnightMoves(BlockAttack bc) {
		Set<Coordinate> validMoves = new HashSet<Coordinate>();
		try {
			for (int i = -2; i<=2; i++) {
				if (i==0) continue;
				int rowOffset = (Math.abs(i) == 1) ? 2 : 1;
				Coordinate coord1 = new Coordinate(position.y + i, position.x + rowOffset);
				Coordinate coord2 = new Coordinate(position.y + i, position.x - rowOffset);
				if (onBoard(coord1) && !bc.blocked(position, coord1)) validMoves.add(coord1);
				if (onBoard(coord2) && !bc.blocked(position, coord2)) validMoves.add(coord2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return validMoves;
	}
	
	private Set<Coordinate> validBishopMoves(BlockAttack bc) {
		Set<Coordinate> validMoves = new HashSet<Coordinate>();
		try {
			int x = position.x, y = position.y;
			while (x < 8 && y < 8) {
				x += 1;
				y += 1;
				Coordinate coord = new Coordinate(y, x);
				if (!bc.blocked(position, coord)) validMoves.add(coord);
			}
			x = position.x; y = position.y;
			while (x < 8 && y > 1) {
				x += 1;
				y -= 1;
				Coordinate coord = new Coordinate(y, x);
				if (!bc.blocked(position, coord)) validMoves.add(coord);
			}
			x = position.x; y = position.y;
			while (x > 1 && y < 8) {
				x -= 1;
				y += 1;
				Coordinate coord = new Coordinate(y, x);
				if (!bc.blocked(position, coord)) validMoves.add(coord);
			}
			x = position.x; y = position.y;
			while (x > 1 && y > 1) {
				x -= 1;
				y -= 1;
				Coordinate coord = new Coordinate(y, x);
				if (!bc.blocked(position, coord)) validMoves.add(coord);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return validMoves;
	}
	
	private Set<Coordinate> validQueenMoves(BlockAttack bc) {
		Set<Coordinate> validMoves = new HashSet<Coordinate>();
		validMoves = validRookMoves(bc);
		validMoves.addAll(validBishopMoves(bc));
		return validMoves;
	}
	
	private Set<Coordinate> validKingMoves(BlockAttack bc) {
		Set<Coordinate> validMoves = new HashSet<Coordinate>();
		try {
			for (int i=-1; i<=1; i++) {
				for (int j=-1; j<= 1; j++) {
					if (i==0 && j==0) continue;
					Coordinate coordinate = new Coordinate(position.y+i, position.x+j);
					if (onBoard(coordinate) && !bc.blocked(position, coordinate) && !chessBoard.getAttackedCoordinates(colour).contains(coordinate)) {
						validMoves.add(coordinate);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return validMoves;
	}
	
	private void setSpecialMoves(BlockAttack bc) {
		specialMoves = new HashMap<String, Coordinate>();
		int row = (!isBlack()) ? 1 : 8;
		int[] cols = {1,5,8};
		Piece king = chessBoard.getPieceAt(new Coordinate(cols[1], row));
		Piece rook1 = chessBoard.getPieceAt(new Coordinate(cols[0], row));
		Piece rook8 = chessBoard.getPieceAt(new Coordinate(cols[2], row));
		
		try {
			if (king != null) {
				if (!king.moved() && !isCheck(colour)) {
					if (rook1 != null) {
						if (!rook1.moved()) {
							if (!bc.blocked(king.getPosition(), rook1.getPosition())) {
								specialMoves.put("king1-3", new Coordinate(3,row));
								specialMoves.put("king1-4", new Coordinate(4,row));
								specialMoves.put("rook1", new Coordinate(4, row));
							}
						}
					}
					if (rook8 != null) {
						if (!rook8.moved() && this == rook8) {
							if (!bc.blocked(king.getPosition(), rook8.getPosition())) {
								specialMoves.put("king8-7", new Coordinate(7, row));
								specialMoves.put("king8-6", new Coordinate(6,row));
								specialMoves.put("rook8", new Coordinate(6,row));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Set<Coordinate> validPawnMoves(BlockAttack bc) {
		Set<Coordinate> validMoves = new HashSet<Coordinate>();
		Coordinate moveUp, moveUp2, capture1, capture2;
		try {
			if (colour.equals(Colour.WHITE)) {
				moveUp = new Coordinate(position.y, position.x+1);
				moveUp2 = new Coordinate(position.y, position.x+2);
				capture1 = new Coordinate(position.y-1, position.x+1);
				capture2 = new Coordinate(position.y+1, position.x+1);
			} else {
				moveUp = new Coordinate(position.y, position.x-1);
				moveUp2 = new Coordinate(position.y, position.x-2);
				capture1 = new Coordinate(position.y-1, position.x-1);
				capture2 = new Coordinate(position.y+1, position.x-1);
			}
			if (onBoard(moveUp) && !!chessBoard.isPositionEmpty(moveUp) && !bc.blocked(position, moveUp)) {
				validMoves.add(moveUp);
			}
			if (validPawnCapture(capture1)) validMoves.add(capture1);
			if (validPawnCapture(capture2)) validMoves.add(capture2);
			if (!moved && !chessBoard.isPositionEmpty(moveUp2) && !bc.blocked(position, moveUp2)) {
				validMoves.add(moveUp2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return validMoves;
	}
	
	private boolean validPawnCapture(Coordinate coordinate) {
		if (!chessBoard.isPositionEmpty(coordinate)) {
			if (chessBoard.getPieceAt(coordinate).colour.equals(oppositePlayer())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean inCheck(Coordinate move) {
		return inCheck(this, move);
	}
	
	private boolean inCheck(Piece p, Coordinate coordinate) {
		try {
			chessBoard.movePiece(p, coordinate);
			if (isCheck(colour)) {
				chessBoard.movePieceBack(p, coordinate);
				return true;
			}
			chessBoard.movePieceBack(p, coordinate);
		} catch (NullPointerException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Class that represents the blocked check attacks for a piece
	 * @author roger
	 *
	 */
	public class BlockAttack {
		boolean attack = false;
		
		public BlockAttack() {}
		
		public BlockAttack(boolean attack) {
			this.attack = attack;
		}
		
		private boolean blocked(Coordinate position, Coordinate move) throws Exception {
			if (attack) {
				return blockedAttack(chessBoard, position, move);
			}
			return blockedMove(chessBoard, position, move);
		}
		
		boolean blocked(ChessBoard b, Coordinate position, Coordinate move) {
			if (attack) {
				return blockedAttack(b, position, move);
			}
			return blockedMove(b, position, move);
		}
		
		private boolean blockedMove(ChessBoard b, Coordinate position, Coordinate move) {
			Integer lowRow, lowCol, highRow, highCol;
			
			if (position.x < move.x) {
				lowRow = position.x;
				highRow = move.x;
			} else {
				lowRow = move.x;
				highRow = position.x;
			}
			
			if (position.y < move.y) {
				lowCol = position.y;
				highCol = move.y;
			} else {
				lowCol = move.y;
				highCol = position.y;
			}
			
			Piece oppositePiece = null;
			boolean pieceFlag = true;
			if (!chessBoard.isPositionEmpty(move)) {
				oppositePiece = b.getPieceAt(move);
			} else {
				pieceFlag = false;
			}
			if (isHorizontalMove(move)) {
				for (int i = lowCol+1; i<highCol; i++) {
					if (!chessBoard.isPositionEmpty(new Coordinate(i, position.x))) return true;
				}
			} else if (isVerticalMove(move)) {
				for (int i = lowRow+1; i<highRow; i++) {
					if (!chessBoard.isPositionEmpty(new Coordinate(position.y, i))) return true;
				}
			} else if (pieceType.equals(PieceType.KNIGHT)) {
				if (pieceFlag) {
					if (oppositePiece.colour.equals(colour)) return true;
				}
				return false;
			} else {
				if (checkDiagMove(move)) return true;
			}
			if (pieceFlag) {
				if (colour.equals(oppositePiece.colour)) return true;
			}
			return false;
		}
		
		private boolean blockedAttack (ChessBoard b, Coordinate position, Coordinate move) {
			Integer lowRow, lowCol, highRow, highCol;
			
			if (position.x < move.x) {
				lowRow = position.x;
				highRow = move.x;
			} else {
				lowRow = move.x;
				highRow = position.x;
			}
			
			if (position.y < move.y) {
				lowCol = position.y;
				highCol = move.y;
			} else {
				lowCol = move.y;
				highCol = position.y;
			}
			
			if (isHorizontalMove(move)) {
				for (int i = lowCol+1; i<highCol; i++) {
					if (!chessBoard.isPositionEmpty(new Coordinate(i, position.x))) return true;
				}
			} else if (isVerticalMove(move)) {
				for (int i = lowRow+1; i<highRow; i++) {
					if (!chessBoard.isPositionEmpty(new Coordinate(position.y, i))) return true;
				}
			} else if (pieceType.equals(PieceType.KNIGHT)) {
				return false;
			} else {
				if (checkDiagMove(move)) return true;
			}
			return false;
		}
	}
}
