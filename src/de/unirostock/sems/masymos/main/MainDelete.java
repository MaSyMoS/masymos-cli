package de.unirostock.sems.masymos.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

import de.unirostock.sems.masymos.configuration.Config;
import de.unirostock.sems.masymos.configuration.NodeLabel;
import de.unirostock.sems.masymos.configuration.Property;
import de.unirostock.sems.masymos.database.Manager;
import de.unirostock.sems.masymos.database.ModelDeleter;


public class MainDelete {

	private static final String queryString = "MATCH (id:GlobalUniqueId) RETURN id.count AS latest_id";
	
	
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
			Long uid = Long.MIN_VALUE; 
			System.out.println("Type Document URI to be deleted: ");
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
			if (s.equals("random")) {
				Long rnd = random();
				System.out.println("Deleting Doc# : " + rnd);				
				ModelDeleter.deleteDocument(uid);
				continue;
			}
			if (s.equals("all")) {
				all();
				break;
			}
			uid = Long.valueOf(s);
			System.out.println("Deleting Doc# : " + uid);
			
			System.out.println(ModelDeleter.deleteDocument(uid));
		}
		System.out.println("Done...");
		System.exit(0);
	}

	public static Long getLastID(){
		Long uuID = Long.MIN_VALUE; 
		
		GraphDatabaseService graphDb = Manager.instance().getDatabase();
		
		try ( Transaction tx = graphDb.beginTx() )
		{		
		    ResourceIterator<Long> resultIterator = graphDb.execute( queryString).columnAs( "latest_id" );
		    uuID = resultIterator.next();
		    resultIterator.close();
		    tx.success();
		}
		return uuID;
	}
	
	private static Long  random(){
		long x = 1L;
		long y = getLastID()+1;
		Random r = new Random();
		return x+((long)(r.nextDouble()*(y-x)));
	}
	
	private static void all(){
		GraphDatabaseService graphDb = Manager.instance().getDatabase();
		List<Long> uids = new LinkedList<Long>();
		try (Transaction tx = graphDb.beginTx()){
			for (Iterator<Node> iterator = graphDb.findNodes(NodeLabel.Types.DOCUMENT); iterator.hasNext();) {
				Node doc = (Node) iterator.next();
				uids.add((Long) doc.getProperty(Property.General.UID));			
			}
			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Collections.shuffle(uids);
		for (Iterator<Long> iterator = uids.iterator(); iterator.hasNext();) {
			Long uid = (Long) iterator.next();
			System.out.println("Deleting Doc# : " + uid);
			System.out.println(ModelDeleter.deleteDocument(uid));
		}

	}

}
