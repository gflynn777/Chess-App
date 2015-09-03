package com.liflynn.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import com.liflynn.chess.Game;

/**
 * 
 * @author Bohan Li
 * @author Greg Flynn
 * 
 */
public class Util {
	/**
	 * 
	 * @param position
	 * @return null if position cannot be resolved
	 */
	public static byte[] convertPosition (String position)
	{
		if (position.length() != 2 || position.charAt(0) < 'a' || position.charAt(0) > 'h' ||
				position.charAt(1) < '1' || position.charAt(1) > '8')
			return null;
		return new byte[]{(byte)(position.charAt(0) - 'a'), (byte)(position.charAt(1) - '1')};
	}
	
	/**
	 * Converts 0-63 positions from gridview into row, col
	 */
	public static byte[] convertPosition (int position)
	{
		if (position < 0 || position > 63)
			return null;
		return new byte[]{(byte)(position%8), (byte)(7-position/8)};
	}
	
	/**
	 * Converts row, col into 0-63 positions
	 */
	public static int convertPosition (byte[] position)
	{
		if (!validPosition(position))
			return -1;
		return (7-position[1])*8 + position[0];
	}
	
	public static boolean validPosition (byte[] position)
	{
		if (position == null || position.length != 2 || !validPosition(position[0], position[1]))
			return false;
		return true;
	}
	
	public static boolean validPosition (int col, int row)
	{
		if (col < 0 || col > 7 || row < 0 || row > 7)
			return false;
		return true;
	}
	
	public static boolean posEquality (byte[] position1, byte[] position2)
	{
		if (!validPosition(position1) || !validPosition(position2))
			return false;//throw new IllegalArgumentException("Invalid position");
		return (position1[0] == position2[0] && position1[1] == position2[1]);
	}
	
	public static void importMoves(Game game){
		String line = null;
		BufferedReader reader;
		String[] input = null;
		boolean moveAccepted = false;
		
		File file = new File("./moves.txt");
		if (!file.exists()){
			System.out.println("File not exists");
			return;
		}
		try {
			reader = new BufferedReader(new FileReader(file));
	
			while((line = reader.readLine()) != null){
				input = line.split("\\s+");
				if (!Util.validPosition(Util.convertPosition(input[0])) || !Util.validPosition(Util.convertPosition(input[1])))
					System.out.println("Invalid arguments!");
				moveAccepted = game.move(Util.convertPosition(input[0]), Util.convertPosition(input[1]), true, false);
				if (!moveAccepted){
					System.out.println("Move Not Accepted: "+line);
				}
				if (moveAccepted){
					System.out.println("\nMove: "+line+"\n");
					//game.printGame();
				}
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int random(int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt(max);
	    return randomNum;
	}
}
