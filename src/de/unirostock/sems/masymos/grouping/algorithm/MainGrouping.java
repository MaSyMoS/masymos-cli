package de.unirostock.sems.masymos.grouping.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.neo4j.graphdb.GraphDatabaseService;

import de.unirostock.sems.masymos.configuration.Config;
import de.unirostock.sems.masymos.database.Manager;
import de.unirostock.sems.masymos.grouping.UGroupingUtil;

public class MainGrouping {

	protected static GraphDatabaseService graphDB;

	public static void main(String[] args) {

		int features = 4;
		String rootTerm = "SBO_0000000"; 
		//String rootTerm = "owl:GOOntology"; 
		//String rootTerm = "owl:ChebiOntology"; 

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-dbPath")) {
				Config.instance().setDbPath(args[++i]);
			}

			if (args[i].equals("-features")) {
				features = Integer.parseInt(args[++i]);
			}

			if (args[i].equals("-balance")) {
				UGroupingUtil.balanceValue = Double.parseDouble(args[++i]);
			}
			
			if (args[i].equals("-onto")) {
				String o = (args[++i]);
				if (o.equalsIgnoreCase("sbo")){
					rootTerm = "SBO_0000000";
				}
				else if (o.equalsIgnoreCase("go")){
					rootTerm = "owl:GOOntology";
				}
				else if (o.equalsIgnoreCase("chebi")){
					rootTerm = "owl:ChebiOntology";
				}
			}
		}

		UGroupingUtil.balanceValue = 0.6;

		graphDB = Manager.instance().getDatabase();

		String s = "";
		while (!s.equals("exit")) {
			System.out
					.println("GroupingType: (1) static, (2) dynamic top-down, (3) dynamic bottom-up, (4) dynamic bottom-up with Similarity Based Scoring: ");
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

			switch (Integer.valueOf(s)) {
			case 1:
				StaticGrouping.graphDB = graphDB;
				StaticGrouping.initStaticGrouping(rootTerm, features,
						UGroupingUtil.balanceValue);
				
				;
				break;
			case 2:
				DynamicGrouping.graphDB = graphDB;

				StaticGrouping.initStaticGrouping(rootTerm, features,UGroupingUtil.balanceValue);
				DynamicGrouping.initDynamicGrouping(rootTerm);
				DynamicGrouping.initTopDownGrouping(rootTerm, features);

				;
				break;
			case 3:
				DynamicGrouping.graphDB = graphDB;
				StaticGrouping.initStaticGrouping(rootTerm, features,UGroupingUtil.balanceValue);
				DynamicGrouping.initDynamicGrouping(rootTerm);
				DynamicGrouping.initBottomUpGrouping(rootTerm, features);
				;
				break;
			case 4:
				DynamicGrouping.graphDB = graphDB;
				StaticGrouping.initStaticGrouping(rootTerm, features,UGroupingUtil.balanceValue);
				DynamicGrouping.initDynamicGrouping(rootTerm);
				DynamicGrouping.initTrisslGrouping(rootTerm, features);
				;
				break;

			default:
				continue;
			}
		}
		System.exit(0);
	}

}
