package com.liflynn.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ImportMoves {
	
	public static void importMoves(){
		String line = null;
		BufferedReader reader;
		
		File file = new File("./moves.txt");
		if (!file.exists()){
			System.out.println("File not exists");
			return;
		}
		try {
			reader = new BufferedReader(new FileReader(file));
	
			while((line = reader.readLine()) != null){
				System.out.println(line);
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
