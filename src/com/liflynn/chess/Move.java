package com.liflynn.chess;

import java.io.Serializable;

public class Move implements Serializable
{
	public byte from, to;
	public String promo = null;
	public Move(int from, int to)
	{
		this.from = (byte)from;
		this.to = (byte)to;
	}
	public boolean hasPromo(){
		return promo != null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + from;
		result = prime * result + to;
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
		Move other = (Move) obj;
		if (from != other.from)
			return false;
		if (to != other.to)
			return false;
		return true;
	}
}