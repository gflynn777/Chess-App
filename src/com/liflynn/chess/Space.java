package com.liflynn.chess;

import java.util.Arrays;
import com.liflynn.piece.GamePiece;
import com.liflynn.util.Util;

/**
 * 
 * @author Greg Flynn
 * @author Bohan Li
 *
 */
public class Space {
	private final byte[] position;
	private GamePiece piece = null;
	public final boolean isBlackSpace;
	
	public Space(byte[] position, boolean isBlackSpace) {
		super();
		if (!Util.validPosition(position))
			throw new IllegalArgumentException("invalid position");
		this.position = new byte[]{position[0], position[1]};
		this.isBlackSpace = isBlackSpace;
	}

	public GamePiece getPiece() {
		return piece;
	}

	public void setPiece(GamePiece piece) {
		this.piece = piece;
	}
	
	public boolean isEmpty() {
		return piece == null;
	}
	
	/**
	 * 
	 * @return byte array of length 2, first element = column, second element = row
	 */
	public byte[] getPosition()
	{
		return new byte[]{position[0], position[1]};
	}
	
	public byte getColumn(){
		return position[0];
	}
	
	public byte getRow(){
		return position[1];
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(position);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Space other = (Space) obj;
		if (!Arrays.equals(position, other.position))
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		if (piece != null)
			return piece.toString();
		return isBlackSpace ? "##" : "  ";
	}
	
	public String info(){
		return ""+ position[0]+ ", "+ position[1];
	}
	
	public String spot()
	{
		if (piece != null)
			return piece.toString();
		return isBlackSpace ? "##"+position[0]+", "+position[1] : "  "+position[0]+", "+position[1];
	}
}

	
