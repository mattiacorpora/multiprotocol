package it.inaf.iasfpa.mpm.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ScriptManager {
	
	public void createFileScript(Script script) {
		
		Gson gs = new GsonBuilder().setPrettyPrinting().create();
		FileWriter fw;
		try {
			fw = new FileWriter("./tmp/current_script.json");
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

}
