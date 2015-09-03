package com.liflynn.piece;

/**
 * 
 * @author Bohan Li
 * @author Greg Flynn
 * 
 */
import com.liflynn.util.Util;
import com.liflynn.chess.Space;
import com.liflynn.chess.Game;

public class Pawn extends GamePiece {

	public Pawn(Game game, Space space, byte color)
	{
		if (color != BLACK && color != WHITE)
			throw new IllegalArgumentException();
		this.game = game;
		this.space = space;
		this.color = color;
	}
	
	/**
	 * helper method for isLegalMove for Pawn
	 * @param position
	 * @return true if destination has piece that we can take, false otherwise
	 */
	private boolean canTake(byte[] position) {
		Space s = game.getSpaceAtPosition(position);
		if (s == null)
			return false;
		return !s.isEmpty() && s.getPiece().getColor() != color;
	}
	
	@Override
	public boolean isLegalMove(byte[] position) {
		byte[] pos = space.getPosition();
		if (Util.posEquality(pos, position))
			return false;
		if (color == WHITE)
		{ // We are moving upwards, increasing row
			if (pos[0] == position[0] && pos[1] + 1 == position[1] &&
					game.getSpaceAtPosition(position).isEmpty())
				return true; // If we are moving forward and space is empty
			else if (pos[0] == position[0] && pos[1] + 2 == position[1] && !hasMoved &&
					hasClearStraightPath(position) &&
					game.getSpaceAtPosition(position).isEmpty())				
				return true; // If we haven't moved, and path is clear, we can move 2 spaces
			else if (pos[1] + 1 == position[1] && (pos[0] - 1 == position[0] || pos[0] + 1 == position[0]) &&
					canTake(position))
				return true; // If we are moving diagonally by 1, and destination can be taken, we can take it
		} else
		{ // We are moving downwards, decreasing row
			if (pos[0] == position[0] && pos[1] - 1 == position[1] &&
					game.getSpaceAtPosition(position).isEmpty())
				return true; // If we are moving forward and space is empty
			else if (pos[0] == position[0] && pos[1] - 2 == position[1] && !hasMoved &&
					hasClearStraightPath(position) &&
					game.getSpaceAtPosition(position).isEmpty())				
				return true; // If we haven't moved, and path is clear, we can move 2 spaces
			else if (pos[1] - 1 == position[1] && (pos[0] - 1 == position[0] || pos[0] + 1 == position[0]) &&
					canTake(position))
				return true; // If we are moving diagonally by 1, and destination can be taken, we can take it
		}
//		if (color != WHITE)
//			System.out.println("Color not white");
//		if ((pos[1] + 1) != position[1])
//			System.out.println("Algorithm no good! pos[1]+1: "+pos[1]+1+ " position[1]: "+position[1]);
//		if (!game.getSpaceAtPosition(position).isEmpty())
//			System.out.println("Space is not empty!");
		return false;
	}
	
	@Override
	public boolean isLegalMoveIgnoreDest(byte[] position)
	{
		byte[] pos = space.getPosition();
		if (Util.posEquality(pos, position))
			return false;
		if (color == WHITE)
		{ // We are moving upwards, increasing row
			if (pos[0] == position[0] && pos[1] + 1 == position[1])
				return true; // If we are moving forward and space is empty
			else if (pos[0] == position[0] && pos[1] + 2 == position[1] && !hasMoved &&
					hasClearStraightPath(position))				
				return true; // If we haven't moved, and path is clear, we can move 2 spaces
			else if (pos[1] + 1 == position[1] && (pos[0] - 1 == position[0] || pos[0] + 1 == position[0]) &&
					canTake(position))
				return true; // If we are moving diagonally by 1, assume destination has a piece and we can take it
		} else
		{ // We are moving downwards, decreasing row
			if (pos[0] == position[0] && pos[1] - 1 == position[1])
				return true; // If we are moving forward and space is empty
			else if (pos[0] == position[0] && pos[1] - 2 == position[1] && !hasMoved &&
					hasClearStraightPath(position))				
				return true; // If we haven't moved, and path is clear, we can move 2 spaces
			else if (pos[1] - 1 == position[1] && (pos[0] - 1 == position[0] || pos[0] + 1 == position[0]) &&
					canTake(position))
				return true; // If we are moving diagonally by 1, assume destination has a piece and we can take it
		}
		return false;
	}

	@Override
	public String toString()
	{
		return (color == BLACK) ? "bp" : "wp";
	}
}
