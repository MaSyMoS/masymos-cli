package de.unirostock.sems.masymos.main;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.ReadableIndex;

import de.unirostock.sems.masymos.configuration.Config;
import de.unirostock.sems.masymos.database.Manager;


public class MainIndex {

	static boolean deleteIndex = false;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		//parse arguments
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-dbPath")) { 
				Config.instance().setDbPath(args[++i]);
			}
			if (args[i].equals("-deleteIndex")) { 
				deleteIndex = Boolean.getBoolean(args[++i].toLowerCase());
			}			
		}
		
		if (deleteIndex) {
			deleteAllIndexes();			
			return;
		} else {
			System.out.println("-deleteIndex option was " + deleteIndex + " >> terminating");
		}
		System.exit(0);
	}
		
		

		


	public static void initializeDatabase(){
		//create neo4j database

		System.out.println("Started at: " + new Date());
		System.out.print("Getting manager...");
		Manager.instance();
		System.out.println("done");

	}


	
	private static void deleteAllIndexes() {
		long start = System.currentTimeMillis();
		initializeDatabase();
		Map<String, Index<Node>> indexMap = Manager.instance().getNodeIndexMap();
		try (Transaction tx = Manager.instance().getDatabase().beginTx())
		{
			for (Iterator<Index<Node>> iterator = indexMap.values().iterator(); iterator.hasNext();) {
				Index<?> index = (Index<?>) iterator.next();			
				index.delete();		
			}
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
		long end = System.currentTimeMillis();
		System.out.print(end-start);
		System.out.println("ms for deleting indexes.");
	}

}
