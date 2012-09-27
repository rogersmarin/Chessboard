package com.rogersmarin.chessboard.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.rogersmarin.chessboard.ChessBoard;
import com.rogersmarin.chessboard.Colour;
import com.rogersmarin.chessboard.Coordinate;
import com.rogersmarin.chessboard.Piece;

/**
 * Class to run the chessboard Game
 * @author roger
 *
 */
public class Main {
	//Default to white's turn
	private static Colour player = Colour.WHITE;
	private static ChessBoard board;
	private static boolean checkmate = false;
	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	public Main() {
		printBoard();
	}
	
	private static Coordinate parseCoordinate(String alpha, int row) {
		int column = getBoardNumbers(alpha);
		Coordinate square = new Coordinate(column, row);
		return square;
	}
	
	private static void checkCoordinate(String coordinate) {
		if (validMoveString("a1 " + coordinate)) {
			Coordinate parsedCoordinate = parseCoordinate(coordinate.substring(0,1), Integer.parseInt(coordinate.substring(1)));
			Piece piece = board.getPieceAt(parsedCoordinate);
			if (piece != null) {
				System.out.println(coordinate + " contains a " + piece.getColour() + " " + piece.getType());
			} else {
				System.out.println("No piece on " + coordinate);
			}
		} else {
			System.out.println("Please enter a valid coordinate to check.");
		}
	}
	
	private static boolean checkMoveString(String m) {
		try {
			String column = m.substring(0,1).toLowerCase();
			Integer row = Integer.parseInt(m.substring(1));
			int columnNum = getBoardNumbers(column);
			if (columnNum >= 1 && columnNum <= 8 && row >= 1 && row <= 8) {
				return true;
			}
		} catch (Exception e) {}
		return false;
	}
	
	private static boolean validMoveString(String move) {
		try {
			String[] moves = getMoves(move);
			if (moves.length == 2) {
				for (String m : moves) {
					if (!checkMoveString(m)) return false;
				}
				return true;
			}
		} catch (StringIndexOutOfBoundsException e) {}
		return false;
	}
	
	private static void init() throws Exception {
		board = new ChessBoard();
		printBoard();
	}
	
	public static int getBoardNumbers(String s) {
		if (s.equals("a")) return 1;
		else if (s.equals("b")) return 2;
		else if (s.equals("c")) return 3;
		else if (s.equals("d")) return 4;
		else if (s.equals("e")) return 5;
		else if (s.equals("f")) return 6;
		else if (s.equals("g")) return 7;
		else if (s.equals("h")) return 8;
		return 0;
	}
	
	public static String getBoardLetters(int i) {
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
	
	private static String[] getMoves(String move) {
		String[] moves = new String[2];
		moves[0] = move.replaceAll(" ", "").substring(0,2);
		moves[1] = move.replaceAll(" ", "").substring(2);
		return moves;
	}
	
	private static void parseMove(String move) throws IOException, Exception {
		if (validMoveString(move)) {
			Piece movePiece;
			String[] moves = getMoves(move);
			List<Coordinate> intMoves = new ArrayList<Coordinate>();
			for (int i = 0; i <=1; i++) {
				intMoves.add(parseCoordinate(moves[i].substring(0,1).toLowerCase(),Integer.parseInt(moves[i].substring(1))));
			}
			if ((movePiece = board.getPieceAt(intMoves.get(0))) != null) {
				if (movePiece.getColour().equals(player)) {
					List<Boolean> moveChecks = movePiece.checkBoard(intMoves.get(1), board);
					if (moveChecks.get(0)) {
						System.out.print("\n" + player + " ---> " + movePiece.getType() + " " + moves[0] + " " + moves[1]);
						if (moveChecks.get(2)) {
							checkmate = true;
							System.out.println(" Checkmate\n\n" + player + " WINS!");
						} else if (moveChecks.get(1)) {
							System.out.println("  Check\n");
						}
					} else {
						throw new IOException(moves[0] + " to " + moves[1] + " is not a valid move.  Try again.");
					}
				} else {
					throw new IOException("Piece is wrong color.");
				}
			} else {
				throw new IOException("No piece on the given position.");
			}
		} else {
			throw new IOException("Could not parse coordinate set from given string");
		}
		
	}
	
	private static void printAttackedSquares() throws Exception {
		for (Coordinate coordinate : board.getAttackedCoordinates(player)) {
			System.out.println(coordinate);
		}
	}
	
	private static void printBoard() {
		System.out.println(board.toString());
	}
	
	private static void printValidMoves(String command) throws Exception {
        try {
            String coordinateStr = command.split(" ")[1];
            if (checkMoveString(coordinateStr)) {
                Coordinate square = parseCoordinate(coordinateStr.toLowerCase().substring(0,1), Integer.parseInt(coordinateStr.substring(1)));
                Piece p;
                if ((p = board.getPieceAt(square)) != null) {
                    for (Coordinate s : p.getValidCoordinates(board, false)) {
                        System.out.println(s);
                    }
                } else {
                    System.out.println("No piece on coordinate.");
                }
            } else {
                System.out.println("Please enter a valid coordinate.");
            }
        } catch( Exception e ) {
            System.out.println("Please enter a valid coordinate.");
        }
	}
	
	private static void setPlayerTurn () {
		if (player.equals(Colour.WHITE)) {
			player = Colour.BLACK;
		} else {
			player = Colour.WHITE;
		}
	}
	
	private static void reset() throws Exception{
		board.reset();
		player = Colour.WHITE;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		init();
		while (!checkmate) {
			try {
				System.out.print("\n" + player + "'s Turn: ");
				String move = br.readLine();
				if (move.contains("check")) {
					checkCoordinate(move.split(" ")[1]);
				} else if (move.contains("exit") || move.contains("quit")) {
					System.exit(0);
				} else if (move.contains("valid")) {
					printValidMoves(move);
				} else if (move.contains("attacked")) {
					printAttackedSquares();
				} else if (move.contains("show")) {
					printBoard();
				} else if (move.contains("reset")) {
					reset();
					printBoard();
				} else {
					parseMove(move);
					printBoard();
					setPlayerTurn();
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			
		}
		
	}

}
