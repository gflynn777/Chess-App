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

public class Rook extends GamePiece {

	public Rook(Game game, Space space, byte color)
	{
		if (color != BLACK && color != WHITE)
			throw new IllegalArgumentException();
		this.game = game;
		this.space = space;
		this.color = color;
	}

	@Override
	public boolean isLegalMoveIgnoreDest(byte[] position) {
		if (Util.posEquality(space.getPosition(), position))
			return false;
		return hasClearStraightPath(position);
	}

	@Override
	public String toString()
	{
		return (color == BLACK) ? "bR" : "wR";
	}
}
