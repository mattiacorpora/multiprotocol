package it.inaf.iasfpa.mpm.utils;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class ScriptManager {
	
	public void createFileScript(Script script, File file) {
		
		Gson gs = new GsonBuilder().setPrettyPrinting().create();
		FileWriter fw;
		try {
			fw = new FileWriter(file.getAbsolutePath());
			BufferedWriter bw = new BufferedWriter(fw); 
			bw.write(gs.toJson(script));
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String scriptToJsonString(Script script) {
		Gson gs = new GsonBuilder().setPrettyPrinting().create();
		return gs.toJson(script);
	}
	
	public Script scriptFromJson(File jsonfile) {
		Gson gs = new GsonBuilder().setPrettyPrinting().create();
		JsonReader reader;
		try {
			reader = new JsonReader(new FileReader(jsonfile));
			return gs.fromJson(reader, Script.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		
		
		
		
		
		
	}

}
