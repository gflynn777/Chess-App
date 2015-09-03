package com.liflynn.piece;

import java.util.ArrayList;

import com.liflynn.chess.Game;
import com.liflynn.chess.Space;
import com.liflynn.util.Util;

/**
 * 
 * @author Bohan Li
 * @author Greg Flynn
 * 
 */
public abstract class GamePiece implements Cloneable {
	public static final byte WHITE = 1;
	public static final byte BLACK = 2;
	protected boolean hasKingInCheck;//Init to false
	protected boolean hasMoved;//For castling and pawn
	protected Game game;
	protected Space space;
	protected Space formerSpace;
	protected byte color;
	
	// Every piece will have this method called with s being the enemy king after a move.
	// Can also be used on non king spaces
	// If s is an enemy king, hasKingInCheck is set
	public boolean putSpaceInCheck(Space s)
	{
		if (s == null || s.getPiece() == null)
			return false;
		if (s.getPiece() == null)
			System.out.println("Piece on space passed to putSpaceInCheck is null");
		boolean ret = isLegalMove(s.getPosition());
		if (ret && s.getPiece().getClass() == King.class && s.getPiece().getColor() != color)
			hasKingInCheck = true;
		return ret;
	}
	
	public Space[] getAllMoves(Space[][] board){
		ArrayList<Space> moves = new ArrayList<Space>();
		for (int i=0; i<board.length; i++)
			for (int j=0; j<board.length; j++)
				if (isLegalMove(board[i][j].getPosition()))
					moves.add(board[i][j]);
		return moves.toArray(new Space[moves.size()]);
	}
	
	
	/**
	 * Given a position, checks if this piece can legally move to position. If position has
	 * a piece that is our color, it is not legal.
	 * Will not check if moving such piece will put ally king in check, that is the game's role
	 * Will check if given position is same as current position, if true it is not a legal move
	 * @param position
	 * @return
	 */
	public boolean isLegalMove(byte[] position) {
		return isLegalMoveIgnoreDest(position) && legalDestination(game.getSpaceAtPosition(position));
	}
	
	/**
	 * Given a position, checks if this piece can legally move to position. This method ignores
	 * whether or not destination has a piece that we can or cannot take, used for checkmate detection
	 * Will not check if moving such piece will put ally king in check, that is the game's role
	 * Will check if given position is same as current position, if true it is not a legal move
	 * @param position
	 * @return
	 */
	public abstract boolean isLegalMoveIgnoreDest(byte[] position);
	
	/**
	 * Checks if the given space is empty or is an enemy piece, so that our piece can land on it
	 * @param s
	 * @return false if the destination has our piece on it, true otherwise
	 */
	public boolean legalDestination(Space s)
	{
		if (s == null)
			return false;
		
		if (s.isEmpty())
			return true;
		return s.getPiece().getColor() != color;
	}

	public int getColor() {
		return color;
	}

	public boolean hasKingInCheck() {
		return hasKingInCheck;
	}
	
	public void resetHasKingInCheck() {
		hasKingInCheck = false;
	}

	public boolean hasMoved() {
		return hasMoved;
	}
	
	public void setHasMoved() {
		hasMoved = true;
	}
	
	public void setMoved(boolean hasMoved){
		this.hasMoved = hasMoved;
	}
	
	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}

	public boolean isInStraightPath(Space s)
	{
		return isInStraightPath(s.getPosition());
	}
	
	public boolean isInDiagonalPath(Space s)
	{
		return isInDiagonalPath(s.getPosition());
	}
	
	
	public boolean isInStraightPath(byte[] position)
	{
		if (!Util.validPosition(position))
			throw new IllegalArgumentException("Invalid position");
		return isInStraightPath(position[0], position[1]);
	}
	
	public boolean isInDiagonalPath(byte[] position)
	{
		if (!Util.validPosition(position))
			throw new IllegalArgumentException("Invalid position");
		return isInDiagonalPath(position[0], position[1]);
	}
	
	/**
	 * Calculates if the given position is in a straight path to the current piece
	 * @param position
	 * @return
	 */
	public boolean isInStraightPath(int col, int row)
	{
		if (!Util.validPosition(col, row))
			throw new IllegalArgumentException("Invalid position");
		return col == space.getPosition()[0] || row == space.getPosition()[1];
	}
	
	/**
	 * Calculates if the given position is in a diagonal path to the current piece
	 * @param position
	 * @return
	 */
	public boolean isInDiagonalPath(int col, int row)
	{
		if (!Util.validPosition(col, row))
			throw new IllegalArgumentException("Invalid position");
		return col + row == space.getPosition()[0] + space.getPosition()[1] ||
				col - row == space.getPosition()[0] - space.getPosition()[1];
	}
	
	public boolean hasClearStraightPath(byte[] position)
	{
		if (!Util.validPosition(position))
			throw new IllegalArgumentException("Invalid position");
		return hasClearStraightPath(position[0], position[1]);
	}
	
	public boolean hasClearDiagonalPath(byte[] position)
	{
		if (!Util.validPosition(position))
			throw new IllegalArgumentException("Invalid position");
		return hasClearDiagonalPath(position[0], position[1]);
	}
	
	/**
	 * Calculates if the straight path to destination is clear, excluding the destination
	 * ie all spaces between us and destination has no game piece on it.
	 * @param position
	 * @return false if the destination is not in a straight path or has a piece blocking it to
	 * the given position, true if we can move the piece there or if we are already on the space
	 */
	public boolean hasClearStraightPath(int col, int row)
	{
		if (!isInStraightPath(col, row))
			return false;
		byte[] pos = space.getPosition();
		if (pos[0] == col)
		{ //same column
			if (pos[1] > row)
			{ //position is below us
				for (int i = pos[1] - 1; i > row; i--)
					if (!game.getSpaceAtPosition(pos[0], i).isEmpty())
						return false;
			} else
			{ //position is above us
				for (int i = pos[1] + 1; i < row; i++)
					if (!game.getSpaceAtPosition(pos[0], i).isEmpty())
						return false;
			}
		} else
		{ //same row
			if (pos[0] > col)
			{ //position is to the left of us
				for (int i = pos[0] - 1; i > col; i--)
					if (!game.getSpaceAtPosition(i, pos[1]).isEmpty())
						return false;
			} else
			{ //position is to the right of us
				for (int i = pos[0] + 1; i < col; i++)
					if (!game.getSpaceAtPosition(i, pos[1]).isEmpty())
						return false;
			}
		}
		return true;
	}
	
	/**
	 * Calculates if the diagonal path to destination is clear, excluding the destination.
	 * ie all spaces between us and the destination has no game piece on it.
	 * @param position
	 * @return false if the destination is not in a diagonal path or has a piece blocking it to
	 * the given position, true if we can move the piece there or if we are already on the space
	 */
	public boolean hasClearDiagonalPath(int col, int row)
	{
		if (!isInDiagonalPath(col, row))
			return false;
		byte[] pos = space.getPosition();
		if (pos[0] + pos[1] == col + row)
		{ //negative slope
			int sum = pos[0] + pos[1];
			if (col > pos[0])
			{ //position is further right bottom to us
				for (int i = pos[0] + 1; i < col; i++)
					if (!game.getSpaceAtPosition(i, sum - i).isEmpty())
						return false;
			}else
			{ //position is further left top to us
				for (int i = pos[0] - 1; i > col; i--)
					if (!game.getSpaceAtPosition(i, sum - i).isEmpty())
						return false;
			}
		} else
		{ //positive slope
			int diff = pos[1] - pos[0];
			if (col > pos[0])
			{ //position is further right top to us
				for (int i = pos[0] + 1; i < col; i++)
					if (!game.getSpaceAtPosition(i, diff + i).isEmpty())
						return false;
			}else
			{ //position is further left bottom to us
				for (int i = pos[0] - 1; i > col; i--)
					if (!game.getSpaceAtPosition(i, diff + i).isEmpty())
						return false;
			}
		}
		return true;
	}


	public Space getFormerSpace() {
		return formerSpace;
	}


	public void setFormerSpace(Space formerSpace) {
		this.formerSpace = formerSpace;
	}
	
	public Object clone(){  
	    try{  
	        return super.clone();  
	    }catch(Exception e){
	    	e.printStackTrace();
	        return null; 
	    }
	}
}
