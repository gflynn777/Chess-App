package com.liflynn.chess;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import com.liflynn.util.Util;

public class Moves implements Serializable
{
	public static final Comparator<Moves> SortByDate = new Comparator<Moves>() {
		@Override
		public int compare(Moves m1, Moves m2) {
			if (m1 == null || m2 == null)
				return 0;
			return m1.time.compareTo(m2.time);
		}
	};
	public static final Comparator<Moves> SortByName = new Comparator<Moves>() {
		@Override
		public int compare(Moves m1, Moves m2) {
			if (m1 == null || m2 == null)
				return 0;
			return m1.name.toLowerCase(Locale.US).compareTo(m2.name.toLowerCase(Locale.US));
		}
	};
	
	private LinkedList<Move> moves;
	public Calendar time;
	public String name;
	
	public Moves()
	{
		moves = new LinkedList<Move>();
		time = Calendar.getInstance();
	}
	
	public void addMove(int from, int to)
	{
		moves.add(new Move(from, to));
	}
	
	public void addMove(byte[] from, byte[] to)
	{
		moves.add(new Move(Util.convertPosition(from), Util.convertPosition(to)));
	}
	
	public Move removeLast()
	{
		return moves.removeLast();
	}
	
	public List<Move> getList()
	{
		return Collections.unmodifiableList(moves);
	}
	
	public Calendar getTime()
	{
		return time;
	}
	
	public void setPromoLastMove(String promo)
	{
		moves.getLast().promo = promo;
	}

	@Override
	public String toString() {
		return name;
	}
}