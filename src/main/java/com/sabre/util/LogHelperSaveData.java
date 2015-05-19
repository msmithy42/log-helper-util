package com.sabre.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class LogHelperSaveData {
		
	public static String[] load() throws IOException {
		String fileText = FileUtils.readFileToString(new File("save.dat"));
		String[] saveData = fileText.split(",");
		
		return saveData;
	}
	
	public static void save(String[] data) throws IOException {
		FileOutputStream out = new FileOutputStream("save.dat");
		
		String dataString = "";
		for (int i = 0; i < data.length; i++) {
			if (data[i] != null) {
				dataString += (data[i] + ",");
			}
		}
		
		out.write(dataString.getBytes());
		out.close();
	}

}
