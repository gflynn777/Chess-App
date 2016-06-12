package com.liflynn.chess;

import java.util.ArrayList;
import java.util.Arrays;
import android.util.Log;
import com.liflynn.chessgame.ChessInterface;
import com.liflynn.chessgame.R;
import com.liflynn.piece.Bishop;
import com.liflynn.piece.GamePiece;
import com.liflynn.piece.King;
import com.liflynn.piece.Knight;
import com.liflynn.piece.Pawn;
import com.liflynn.piece.Queen;
import com.liflynn.piece.Rook;
import com.liflynn.util.Util;

/**
 * @author Greg Flynn
 * @author Bohan Li
 *
 */
public class Game {
	// First dimension is column, second dimension is row
	// ie board[2][0] is column 2, row 0, which is init to white bishop
	// Row starts from bottom, ie row 0 is the bottom-most row
	//	07 17 27 37 47 57 67 77
	//	06 16 26 36 46 56 66 76
	//	05 15 25 35 45 55 65 75
	//	04 14 24 34 44 54 64 74
	//	03 13 23 33 43 53 63 73
	//	02 12 22 32 42 52 62 72
	//	01 11 21 31 41 51 61 71
	//	00 10 20 30 40 50 60 70
	public Space[][] board;
	public GamePiece[][] previousBoard;
	public static final int STALEMATE = -2;
	public static final int DRAW = -1;
	public static final String PROMO = "promo";
	private byte whoseTurn;
	private King blackKing, whiteKing;
	private boolean blackInCheck = false, whiteInCheck = false;
	private Space previousMove = null;
	private ArrayList<GamePiece> gamePieces, piecesHaveCheck;
	private byte[] previousFrom;
	private Space pawnToPromote;
	private ChessInterface gui;
	public Moves moves;
	public boolean gameOver = false;
	
	public Game(ChessInterface chess)
	{
		board = new Space[8][8];
		previousBoard = null;
		gamePieces = new ArrayList<GamePiece>(32);
		piecesHaveCheck = new ArrayList<GamePiece>();
		initBoard();
		whoseTurn = GamePiece.WHITE;
		gui = chess;
		moves = new Moves();
	}
	
	private void initBoard()
	{
		// Init spaces
		for (byte i = 0; i < board.length; i++)
			for (byte j = 0; j < board[0].length; j++)
				board[i][j] = new Space(new byte[]{i,j}, (i+j)%2 == 0 ? true : false);
			
		// Pawns
		for (byte i = 0; i < board.length; i++)
		{
			board[i][1].setPiece(new Pawn(this, board[i][1], GamePiece.WHITE));
			board[i][6].setPiece(new Pawn(this, board[i][6], GamePiece.BLACK));
		}
		
		// Kings
		board[4][0].setPiece((whiteKing = new King(this, board[4][0], GamePiece.WHITE)));
		board[4][7].setPiece((blackKing = new King(this, board[4][7], GamePiece.BLACK)));
		
		// Queens
		board[3][0].setPiece(new Queen(this, board[3][0], GamePiece.WHITE));
		board[3][7].setPiece(new Queen(this, board[3][7], GamePiece.BLACK));
		
		// Bishops
		board[2][0].setPiece(new Bishop(this, board[2][0], GamePiece.WHITE));
		board[2][7].setPiece(new Bishop(this, board[2][7], GamePiece.BLACK));
		board[5][0].setPiece(new Bishop(this, board[5][0], GamePiece.WHITE));
		board[5][7].setPiece(new Bishop(this, board[5][7], GamePiece.BLACK));
		
		// Knights
		board[1][0].setPiece(new Knight(this, board[1][0], GamePiece.WHITE));
		board[1][7].setPiece(new Knight(this, board[1][7], GamePiece.BLACK));
		board[6][0].setPiece(new Knight(this, board[6][0], GamePiece.WHITE));
		board[6][7].setPiece(new Knight(this, board[6][7], GamePiece.BLACK));
		
		// Rooks
		board[0][0].setPiece(new Rook(this, board[0][0], GamePiece.WHITE));
		board[0][7].setPiece(new Rook(this, board[0][7], GamePiece.BLACK));
		board[7][0].setPiece(new Rook(this, board[7][0], GamePiece.WHITE));
		board[7][7].setPiece(new Rook(this, board[7][7], GamePiece.BLACK));
		
		//Put all GamePieces in an arraylist
		for (int i=0; i<board.length; i++)
			for (int j=0; j<board.length; j++)
				if (!board[i][j].isEmpty()){
					board[i][j].getPiece().setSpace(board[i][j]);
					gamePieces.add(board[i][j].getPiece());
				}
	}
	
	public void endGame(int winner, boolean resign){
		String preface = null;
		if (resign)
			preface = "Player Resigned - ";
		else
			preface = "Checkmate - ";
		if (winner == GamePiece.WHITE)
			gui.askForSave(preface+"White Wins", R.drawable.wking);
		else if (winner == GamePiece.BLACK)
			gui.askForSave(preface+"Black Wins", R.drawable.bking);
		else if (winner == STALEMATE)
			gui.askForSave("Stalemate", R.drawable.wking);
		else
			gui.askForSave("Draw", R.drawable.wking);
		
		gameOver = true;
	}
	
	public boolean isGameOver(){
		return gameOver;
	}
	
	public int getWhoseTurn(){
		return whoseTurn;
	}
	
	public byte notWhoseTurn(){
		if (whoseTurn == GamePiece.WHITE)
			return GamePiece.BLACK;
		return GamePiece.WHITE;
	}
	
	public Space getSpaceAtPosition(int col, int row)
	{
		if (!Util.validPosition(col, row))
			throw new IllegalArgumentException("Invalid position");
		return board[col][row];
	}
	
	public Space getSpaceAtPosition(byte[] position)
	{
		if (!Util.validPosition(position))
			return null;//throw new IllegalArgumentException("Invalid position");
		return board[position[0]][position[1]];
	}
	
	public void switchColors(){
		whoseTurn = notWhoseTurn();
	}
	
	public Space getKing(String side){
		if (side.equals("enemy"))
			return (whoseTurn == GamePiece.BLACK) ? whiteKing.getSpace() : blackKing.getSpace();
		else{
			return (whoseTurn == GamePiece.BLACK) ? blackKing.getSpace() : whiteKing.getSpace();
		}
	}
	
	public void removePiece(GamePiece piece, boolean permanent){
		if (piece.getSpace() != null)
		{
			piece.getSpace().setPiece(null);
			if (permanent)
				gui.remove(Util.convertPosition(piece.getSpace().getPosition()));
		}
		if (permanent)
			piece.setSpace(null);
		//Remove from gamepiece array
		gamePieces.remove(piece);
	}
	
	public void putKingInCheck(){
		if (whoseTurn == GamePiece.BLACK){
			whiteInCheck = true;
			Log.d("Check", "White is in check!");
		}
		else{
			blackInCheck = true;
			Log.d("Check", "Black is in check!");
		}
	}
	
	public boolean isKingInCheck(){
		return (whoseTurn == GamePiece.BLACK) ? blackInCheck : whiteInCheck;
	}
	
	public boolean putOwnKingInCheck(byte[] from, byte[] to, GamePiece pieceToMove){
		GamePiece temp = getSpaceAtPosition(to).getPiece();//Save the original piece at to position
		boolean returnValue = false;
		//Temporarily set the piece
		setMove(from, to, false);
		
		for (GamePiece enemyPiece : gamePieces ){
			if (enemyPiece.getSpace() == null || enemyPiece.getColor() == whoseTurn)
				continue;
			enemyPiece.resetHasKingInCheck();
			if(enemyPiece.putSpaceInCheck(getKing("ally")))
				returnValue =  true;
		}
		setMove(to, from, false);
		getSpaceAtPosition(to).setPiece(temp);
		if (temp != null)
			gamePieces.add(temp);
		return returnValue;
	}
	
	public boolean performEnPassant(byte[] from, byte[] to, GamePiece pawn){
		if (!previousMove.equals(getSpaceAtPosition(new byte[]{to[0], from[1]})))
			return false;
		if (whoseTurn == GamePiece.WHITE && (from[1] != 4 || previousFrom[1] != previousMove.getRow()+2))//White can only do enPassant from row 4
			return false;
		else if (whoseTurn == GamePiece.BLACK && (from[1] != 3 || previousFrom[1] != previousMove.getRow()-2))//Black can only do enPassant from row 3
			return false;
		
		//Take the pawn
		removePiece(previousMove.getPiece(), true);
		return true;
	}
	
	//King is set by move method only have to change the rook's position
	public boolean performCastle(byte[] from, byte[] to, Space rookSpace){
		GamePiece king = this.getSpaceAtPosition(from).getPiece();
		GamePiece rook = rookSpace.getPiece();
		byte row = to[1];
		byte rookCol = rook.getSpace().getColumn();
		if (rook.hasMoved() || king.hasMoved())
			return false;
		//Check spaces in between
		if (king.getSpace().getColumn() > rookCol){//King to the right of rook
			for (int i=from[0]-1; i > rookCol; i--)
				if (!board[i][row].isEmpty())
					return false;
			if (whoseTurn == GamePiece.WHITE)
				setMove(rookSpace.getPosition(), new byte[]{3,0}, true);
			else
				setMove(rookSpace.getPosition(), new byte[]{3,7}, true);
		}
		else if (king.getSpace().getColumn() < rookCol){//King is to the left
			for (int i=from[0]+1; i < rookCol; i++)
				if (!board[i][row].isEmpty()){
					//System.out.println("board["+i+"]["+row+"] is not empty!");
					return false;
				}
			if (whoseTurn == GamePiece.WHITE)
				setMove(rookSpace.getPosition(), new byte[]{5,0}, true);
			else
				setMove(rookSpace.getPosition(), new byte[]{5,7}, true);
		}
		return true;
	}
	
	public GamePiece performPromotion(String chosenPromo){
		//The pawn has already been moved and needs to be changed out for another piece 	
		GamePiece newPiece;
		switchColors();
		removePiece(pawnToPromote.getPiece(), true);
		if(chosenPromo.equals("Rook"))
			pawnToPromote.setPiece(newPiece = new Rook(this, pawnToPromote, whoseTurn));
		else if (chosenPromo.equals("Bishop"))
			pawnToPromote.setPiece(newPiece = new Bishop(this, pawnToPromote, whoseTurn));
		else if (chosenPromo.equals("Knight"))
			pawnToPromote.setPiece(newPiece = new Knight(this, pawnToPromote, whoseTurn));
		else
			pawnToPromote.setPiece(newPiece = new Queen(this, pawnToPromote, whoseTurn));
	
		newPiece.hasMoved();
		newPiece.setSpace(pawnToPromote);
		gamePieces.add(newPiece);
		gui.syncChess();
		move(newPiece.getSpace().getPosition(), newPiece.getSpace().getPosition(), true, true);
		moves.setPromoLastMove(chosenPromo);
		return newPiece;
	}
	
	//Method used to actually place the piece
	public void setMove(byte[] from, byte[] to, boolean permanent){
		Space dest = this.getSpaceAtPosition(to);
		Space origin = this.getSpaceAtPosition(from);
		//System.out.println("Setting Move: from: "+ origin.getInf() +" to: "+dest.getInf());
		if (dest.getPiece() != null)
			removePiece(dest.getPiece(), permanent);

		dest.setPiece(origin.getPiece());
		origin.setPiece(null);
		dest.getPiece().setSpace(dest);
		if (permanent)
		{
			dest.getPiece().setHasMoved();
			gui.move(Util.convertPosition(from), Util.convertPosition(to));
		}
	}
	
	public boolean validFrom(byte[] from)
	{
		GamePiece piece = this.getSpaceAtPosition(from).getPiece();
		if (piece == null || piece.getColor() != whoseTurn)
			return false;
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean checkForMoves(){
		ArrayList<GamePiece> temp = (ArrayList<GamePiece>)gamePieces.clone();
		boolean returnValue = false;
		switchColors();//Temporarily switch colors
		for (GamePiece piece : temp){
			if (piece.getColor() != whoseTurn)
				continue;
			for (Space move : piece.getAllMoves(board)){
				if (move(piece.getSpace().getPosition(), move.getPosition(), false, false)){
					returnValue = true;
				}
			}
		}
		switchColors();//Switch colors back
		return returnValue;
	}
	
	//Methods that checks all aspects of a move
	public boolean move(byte[] from, byte[] to, boolean permanent, boolean doubleCheck)
	{
		boolean enPassant = false;
		boolean castle = false;
		GamePiece piece = this.getSpaceAtPosition(from).getPiece();
		piecesHaveCheck.clear();
		//Log.d("Move", "From: "+from[0]+", "+from[1] +" to "+to[0]+", "+to[1]);
		
		//Check to make sure its the right color
		if (piece == null || piece.getColor() != whoseTurn){
			return false;
		}
			
		//Pawn situations
		if (piece instanceof Pawn && from[0] != to[0] && ((to[1] == from[1]-1 && whoseTurn == GamePiece.BLACK) || 
														  (to[1] == from[1]+1 && whoseTurn == GamePiece.WHITE))){
			//Check for EnPassant
			if (getSpaceAtPosition(to).isEmpty() && previousMove != null && previousMove.getPiece() instanceof Pawn)
				enPassant = performEnPassant(from, to, piece);
			//There is no enemy piece at the to position or there is an ally piece
			else if (getSpaceAtPosition(to).isEmpty() || getSpaceAtPosition(to).getPiece().getColor() == whoseTurn){
				return false;
			}
		}
		
		//Castle
		if (piece instanceof King && permanent){
			if (Util.posEquality(to, new byte[]{6,0}) && this.getSpaceAtPosition(new byte[]{7,0}).getPiece() instanceof Rook)
				castle = performCastle(from, to, this.getSpaceAtPosition(new byte[]{7,0}));
			
			else if (Util.posEquality(to, new byte[]{2,0}) && this.getSpaceAtPosition(new byte[]{0,0}).getPiece() instanceof Rook)
				castle = performCastle(from, to, this.getSpaceAtPosition(new byte[]{0,0}));
			
			else if (Util.posEquality(to, new byte[]{2,7}) && this.getSpaceAtPosition(new byte[]{0,7}).getPiece() instanceof Rook)
				castle = performCastle(from, to, this.getSpaceAtPosition(new byte[]{0,7}));
			
			else if (Util.posEquality(to, new byte[]{6,7}) && this.getSpaceAtPosition(new byte[]{7,7}).getPiece() instanceof Rook)
				castle = performCastle(from, to, this.getSpaceAtPosition(new byte[]{7,7}));
		}
		
		
		//Check if legal move
		if (!enPassant && !castle && !doubleCheck && !piece.isLegalMove(to)){//isLegalMove will not allow for enpassant, castling, etc
			return false;
		}
				
		//Check if moving the piece will put/leave player's own king in check
		if (!doubleCheck && putOwnKingInCheck(from, to, piece))
			return false;

		//If not permanent, return here, the move is legal
		if (!permanent)
			return true;
		
		//Save the board for later
		GamePiece[][] tmpPrevBoard = new GamePiece[8][8];
		for (int i=0; i<board.length; i++)
			for (int j=0; j<board.length; j++)
			{
				GamePiece p = board[i][j].getPiece();
				tmpPrevBoard[i][j] = p == null ? null : (GamePiece)p.clone();
			}
		
		//Actually perform the move
		if (doubleCheck == false)
			setMove(from, to, true);
		
		//Check for promotion
		if (piece instanceof Pawn && (Util.posEquality(to, new byte[]{to[0], 7}) || Util.posEquality(to, new byte[]{to[0], 0}))){
			pawnToPromote = getSpaceAtPosition(to);
			gui.startPromoActivity();
		}	
			
		//Check if move puts enemy king in check
		for (GamePiece allyPiece : gamePieces ){
			if (allyPiece.getSpace() == null || allyPiece.getColor() != whoseTurn)
				continue;
			allyPiece.resetHasKingInCheck();
			if(allyPiece.putSpaceInCheck(getKing("enemy"))){
				Log.d("Check", allyPiece+" has king in check!");
				allyPiece.hasKingInCheck();
				piecesHaveCheck.add(allyPiece);
				putKingInCheck();
			}
		}		
		previousMove = getSpaceAtPosition(to);
		previousFrom = from;
		
		boolean movesLeft = checkForMoves();
		//TODO: End the game
		if ((whiteInCheck || blackInCheck) && !movesLeft){
			gui.toast("Checkmate");
			endGame(whoseTurn, false);
		}
		
		else if (!whiteInCheck && !blackInCheck && !movesLeft)
			endGame(STALEMATE, false);
		
		else if (whiteInCheck || blackInCheck)
			gui.toast("Check");
		
		switchColors();
		blackInCheck = false;
		whiteInCheck = false;
		gui.setUndo(true);
		if (!doubleCheck)
		{
			previousBoard = tmpPrevBoard;
			moves.addMove(from, to);
		}
		return true;
	}
	
	/**
	 * 
	 * @return false if there is no previous board, true if board has been reverted
	 */
	public boolean undo(){
		GamePiece piece;
		if (previousBoard == null)
			return false;

		gamePieces.clear();
		for (int i=0; i<board.length; i++)
			for (int j=0; j<board.length; j++){
				board[i][j].setPiece(previousBoard[i][j]);
				if ((piece = board[i][j].getPiece()) != null){
					piece.setSpace(board[i][j]);
					gamePieces.add(piece);
					if (piece instanceof King && piece.getColor() == GamePiece.WHITE)
						whiteKing = (King) piece;
					if (piece instanceof King && piece.getColor() == GamePiece.BLACK)
						blackKing = (King) piece;
					if (piece.getColor() != whoseTurn)//At this point it is still the opposite player's turn
						board[i][j].getPiece().resetHasKingInCheck();
				}					
			}
		previousBoard = null;
		switchColors();
		moves.removeLast();
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void performAiMove(){
		//Pick a random gamePiece
		ArrayList<GamePiece> pieces = (ArrayList<GamePiece>)gamePieces.clone();
		while(pieces.size() > 0){
			int rand = Util.random(pieces.size());
			GamePiece piece = pieces.get(rand);
			if (piece.getColor() == whoseTurn){
				//Pick a random move
				ArrayList<Space> moves = new ArrayList<Space>(Arrays.asList(piece.getAllMoves(board)));
				while (moves.size() > 0){
					//If the random move is valid, play it. Else remove it and try again.
					int random = Util.random(moves.size());
					if (move(piece.getSpace().getPosition(), moves.get(random).getPosition(), true, false))
							return;
					else
						moves.remove(random);
				}
			}
			pieces.remove(rand);
		}
	}
}
