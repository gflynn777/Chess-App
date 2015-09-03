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

public class King extends GamePiece {

	public King(Game game, Space space, byte color)
	{
		if (color != BLACK && color != WHITE)
			throw new IllegalArgumentException();
		this.game = game;
		this.space = space;
		this.color = color;
	}
	
	@Override
	public boolean isLegalMoveIgnoreDest(byte[] position)
	{
		byte[] pos = space.getPosition();
		if (Util.posEquality(pos, position))
			return false;
		if (pos[0] + 1 >= position[0] && pos[0] - 1 <= position[0] &&
				pos[1] + 1 >= position[1] && pos[1] - 1 <= position[1])
			return true;
		return false;
	}

	@Override
	public String toString()
	{
		return (color == GamePiece.BLACK) ? "bK" : "wK";
	}
}
