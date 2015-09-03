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

public class Knight extends GamePiece {

	public Knight(Game game, Space space, byte color)
	{
		if (color != BLACK && color != WHITE)
			throw new IllegalArgumentException();
		this.game = game;
		this.space = space;
		this.color = color;
	}
	
	@Override
	public boolean isLegalMoveIgnoreDest(byte[] position) {
		byte[] pos = space.getPosition();
		if (Util.posEquality(pos, position))
			return false;
		int c = pos[0], r = pos[1];
		int[][] legalMoves = new int[][]{{c+1, r+2}, {c+2, r+1}, {c+2, r-1},
				{c+1, r-2}, {c-1, r-2}, {c-2, r-1}, {c-2, r+1}, {c-1, r+2}};
		for (int i = 0; i < legalMoves.length; i++)
			if (legalMoves[i][0] == position[0] && legalMoves[i][1] == position[1])
				return true;
		return false;
	}

	@Override
	public String toString()
	{
		return (color == GamePiece.BLACK) ? "bN" : "wN";
	}
}
