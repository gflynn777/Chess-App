package com.liflynn.piece;

import com.liflynn.util.Util;
import com.liflynn.chess.Space;
import com.liflynn.chess.Game;

/**
 * 
 * @author Bohan Li
 * @author Greg Flynn
 * 
 */
public class Bishop extends GamePiece {

	public Bishop(Game game, Space space, byte color)
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
		return hasClearDiagonalPath(position);
	}
	
	@Override
	public String toString()
	{
		return (color == GamePiece.BLACK) ? "bB" : "wB";
	}
}
