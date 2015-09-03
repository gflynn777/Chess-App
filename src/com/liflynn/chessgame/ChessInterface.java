package com.liflynn.chessgame;

public interface ChessInterface {
	public void move (int from, int to);
	public void remove (int pos);
	public void askForSave(String winner, int icon);
	public void syncChess();
	public void startPromoActivity();
	public void toast(String msg);
	public void setUndo(boolean value);
}
