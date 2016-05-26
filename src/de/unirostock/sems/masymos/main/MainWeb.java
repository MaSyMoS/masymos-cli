package de.unirostock.sems.masymos.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import de.unirostock.sems.masymos.configuration.Config;
import de.unirostock.sems.masymos.database.Manager;
import de.unirostock.sems.masymos.database.ModelInserter;
import de.unirostock.sems.masymos.util.ModelDataHolder;

public class MainWeb {

	public static void main(String[] args) {

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-dbPath")) {
				Config.instance().setDbPath(args[++i]);
			}
		}

		// create neo4j database
		long start = System.currentTimeMillis();
		System.out.println("Started at: " + new Date());
		System.out.print("Getting manager...");
		Manager.instance();
		System.out.println("done in " + (System.currentTimeMillis() - start)
				+ "ms");
		System.out.println();

		String s = "";
		while (!s.equals("exit")) {
			System.out.println("Provide JSON String: ");
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						System.in));
				s = in.readLine();

			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
			if (s.equals("exit")) {
				break;
			}

			insertByJSON();
		}
		System.exit(0);
	}
	
	private static void insertByJSON() {
		String s = "";
		while (!s.equals("exit")) {
			System.out.println("JSON: ");
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						System.in));
				s = in.readLine();

			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
			if (s.equals("exit")) {
				break;
			}
			System.out.println("Inserting...");
		
	    	Gson gson = new Gson();
	    	
	    	ModelDataHolder mdh = null; 
	    	java.lang.reflect.Type typeOfT = new TypeToken<ModelDataHolder>(){}.getType();
	    	try {
	    		mdh = gson.fromJson(s, typeOfT);	
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
				return;
			}
	    	
	    	try {
	    		ModelInserter.addModelVersion(mdh.getFileId(), mdh.getVersionId(), mdh.getParentMap(), new URL(mdh.getXmldoc()), gson.toJson(mdh.getMetaMap()), mdh.getModelType());
			} catch (Exception e) {
				e.printStackTrace();		
				return;
	           
			}
	   		
	    	System.out.println("looks like it worked...");

		
		}	

	}	
	

}
