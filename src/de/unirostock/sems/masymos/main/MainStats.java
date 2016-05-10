package de.unirostock.sems.masymos.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import de.unirostock.sems.masymos.configuration.Config;
import de.unirostock.sems.masymos.database.Manager;
import de.unirostock.sems.masymos.util.Assign;
import de.unirostock.sems.masymos.util.HungarianAlgorithm;
import de.unirostock.sems.masymos.util.OntologyUtil;

public class MainStats {

	static List<String> lsbo15M4F = new LinkedList<String>(Arrays.asList(
			"SBO_0000003", 
			"SBO_0000009", 
			"SBO_0000064", 
			"SBO_0000167", 
			"SBO_0000240"));
	static List<String> lsbo15M2F = new LinkedList<String>(Arrays.asList(
			"SBO_0000003",
			"SBO_0000064",
			"SBO_0000241",
			"SBO_0000245",
			"SBO_0000247",
			"SBO_0000253",
			"SBO_0000285",
			"SBO_0000290",
			"SBO_0000291",
			"SBO_0000374",
			"SBO_0000375",
			"SBO_0000405",
			"SBO_0000409",
			"SBO_0000412",
			"SBO_0000545"));
	
	static List<String> lgo15M2F = new LinkedList<String>(Arrays.asList(
			"GO_0003674",
			"GO_0005575",
			"GO_0008152",
			"GO_0009987",
			"GO_0032501",
			"GO_0032502",
			"GO_0040007",
			"GO_0044699",
			"GO_0048511",
			"GO_0050896",
			"GO_0051234",
			"GO_0051704",
			"GO_0065007",
			"GO_0071840"
			));
	
	static List<String> lgo15M4F = new LinkedList<String>(Arrays.asList(
			"GO_0003674",
			"GO_0005575",
			"GO_0009987",
			"GO_0043170",
			"GO_0071822"

			));
	
	
	static List<String> lchebi15M4F = new LinkedList<String>(Arrays.asList(
			"CHEBI_24835",
			"CHEBI_24870",
			"CHEBI_26082",
			"CHEBI_33241",
			"CHEBI_33259",
			"CHEBI_33636",
			"CHEBI_33695",
			"CHEBI_35155",
			"CHEBI_35569",
			"CHEBI_35701",
			"CHEBI_47019",
			"CHEBI_61120",
			"CHEBI_63161",
			"CHEBI_63367",
			"CHEBI_64709"
			));
			
	static List<String> lchebi15M2F = new LinkedList<String>(Arrays.asList(		
			"CHEBI_18059",
			"CHEBI_24835",
			"CHEBI_24870",
			"CHEBI_25367",
			"CHEBI_26082",
			"CHEBI_33241",
			"CHEBI_33259",
			"CHEBI_33285",
			"CHEBI_33304",
			"CHEBI_33674",
			"CHEBI_33839",
			"CHEBI_35701",
			"CHEBI_50906",
			"CHEBI_51143",
			"CHEBI_64709"
			));
	
	static List<String> lsbo15M4CC = new LinkedList<String>(Arrays.asList(	
			"SBO_0000009",
			"SBO_0000231",
			"SBO_0000252",
			"SBO_0000336"
			));
	
	static List<String> lgo15M4CC = new LinkedList<String>(Arrays.asList(	
			"GO_0000216",
			"GO_0004693",
			"GO_0005575",
			"GO_0022411",
			"GO_0030163",
			"GO_0032268",
			"GO_0045750",
			"GO_0051726",
			"GO_0065009",
			"GO_0071822"
			));
	
	static List<String> lchebi15M4CC = new LinkedList<String>(Arrays.asList(	
			"CHEBI_22563",
			"CHEBI_33608",
			"CHEBI_33694",
			"CHEBI_37096",
			"CHEBI_37787"

			));
	
	static List<String> lsbo15M2CC = new LinkedList<String>(Arrays.asList(	
			"SBO_0000009",
			"SBO_0000177",
			"SBO_0000179",
			"SBO_0000180",
			"SBO_0000181",
			"SBO_0000182",
			"SBO_0000205",
			"SBO_0000245",
			"SBO_0000253",
			"SBO_0000290",
			"SBO_0000291",
			"SBO_0000308",
			"SBO_0000342",
			"SBO_0000360",
			"SBO_0000374"
			));
	
	static List<String> lchebi15M2CC = new LinkedList<String>(Arrays.asList(	
			"CHEBI_16646",
			"CHEBI_24651",
			"CHEBI_25367",
			"CHEBI_25699",
			"CHEBI_25741",
			"CHEBI_26082",
			"CHEBI_33241",
			"CHEBI_33839",
			"CHEBI_35701",
			"CHEBI_36358",
			"CHEBI_36606",
			"CHEBI_51143",
			"CHEBI_63161",
			"CHEBI_63299",
			"CHEBI_64709"
			));
	
	static List<String> lgo15M2CC = new LinkedList<String>(Arrays.asList(	
			"GO_0003674",
			"GO_0005575",
			"GO_0006807",
			"GO_0009056",
			"GO_0009058",
			"GO_0040007",
			"GO_0044237",
			"GO_0044238",
			"GO_0044699",
			"GO_0050896",
			"GO_0051234",
			"GO_0065007",
			"GO_0071704",
			"GO_0071840"
			));
	
	static List<String> lsbo5M4F_ = new LinkedList<String>(Arrays.asList(
			"SBO_0000003", 
			"SBO_0000009", 
			"SBO_0000064", 
			"SBO_0000167", 
			"SBO_0000240"));
	
	static List<String> lsbo5M2F_ = new LinkedList<String>(Arrays.asList(
			"SBO_0000003",
			"SBO_0000064",
			"SBO_0000231",
			"SBO_0000236",
			"SBO_0000545"
			));
	
	static List<String> lgo5M2F_ = new LinkedList<String>(Arrays.asList(
			"GO_0003674",
			"GO_0008152",
			"GO_0009987",
			"GO_0044699",
			"GO_0065007"
			));
	
	static List<String> lgo5M4F_ = new LinkedList<String>(Arrays.asList(
			"GO_0003674",
			"GO_0005575",
			"GO_0009987",
			"GO_0043170",
			"GO_0071822"
			));
	
	
	static List<String> lchebi5M4F_ = new LinkedList<String>(Arrays.asList(
			"CHEBI_24870",
			"CHEBI_26082",
			"CHEBI_33241",
			"CHEBI_33695",
			"CHEBI_61120"
			));
			
	static List<String> lchebi5M2F_ = new LinkedList<String>(Arrays.asList(		
			"CHEBI_24870",
			"CHEBI_33302",
			"CHEBI_33304",
			"CHEBI_33582",
			"CHEBI_36357"
			));
	
	static List<String> lsbo5M4CC_ = new LinkedList<String>(Arrays.asList(	
			"SBO_0000009",
			"SBO_0000231",
			"SBO_0000252",
			"SBO_0000336"
			));
	
	static List<String> lgo5M4CC_ = new LinkedList<String>(Arrays.asList(	
			"GO_0022411",
			"GO_0030163",
			"GO_0051726",
			"GO_0065009",
			"GO_0071822"

			));
	
	static List<String> lchebi5M4CC_ = new LinkedList<String>(Arrays.asList(	
			"CHEBI_22563",
			"CHEBI_33608",
			"CHEBI_33694",
			"CHEBI_37096",
			"CHEBI_37787"
			));
	
	static List<String> lsbo5M2CC_ = new LinkedList<String>(Arrays.asList(	
			"SBO_0000003",
			"SBO_0000236",
			"SBO_0000374",
			"SBO_0000375",
			"SBO_0000545"
			));
	
	static List<String> lchebi5M2CC_ = new LinkedList<String>(Arrays.asList(	
			"CHEBI_33285",
			"CHEBI_33302",
			"CHEBI_33304",
			"CHEBI_35701",
			"CHEBI_36357"
			));
	
	static List<String> lgo5M2CC_ = new LinkedList<String>(Arrays.asList(	
			"GO_0008152",
			"GO_0009987",
			"GO_0044699",
			"GO_0065007",
			"GO_0071840"
			));	
	
	static List<String> lsbo15M4RS1 = new LinkedList<String>(Arrays.asList(
			"SBO_0000009",
			"SBO_0000064",
			"SBO_0000176",
			"SBO_0000252"
			));
	
	static List<String> lgo15M4RS1 = new LinkedList<String>(Arrays.asList(
			"GO_0003674",
			"GO_0005575",
			"GO_0006810",
			"GO_0009987",
			"GO_0016088",
			"GO_0043170",
			"GO_0045750"
			));
	static List<String> lchebi15M4RS1 = new LinkedList<String>(Arrays.asList(
			"CHEBI_22563",
			"CHEBI_24835",
			"CHEBI_25741",
			"CHEBI_26082",
			"CHEBI_33241",
			"CHEBI_33252",
			"CHEBI_33259",
			"CHEBI_33608",
			"CHEBI_33695",
			"CHEBI_35701",
			"CHEBI_61120",
			"CHEBI_63367",
			"CHEBI_64709"
			));
	
	static List<String> lsbo15M2RS1 = new LinkedList<String>(Arrays.asList(
			"SBO_0000064",
			"SBO_0000177",
			"SBO_0000179",
			"SBO_0000180",
			"SBO_0000182",
			"SBO_0000185",
			"SBO_0000205",
			"SBO_0000241",
			"SBO_0000247",
			"SBO_0000250",
			"SBO_0000253",
			"SBO_0000285",
			"SBO_0000290",
			"SBO_0000377",
			"SBO_0000545"

			));
	
	static List<String> lgo15M2RS1 = new LinkedList<String>(Arrays.asList(
			"GO_0003674",
			"GO_0005575",
			"GO_0006807",
			"GO_0009056",
			"GO_0009058",
			"GO_0044237",
			"GO_0044238",
			"GO_0044699",
			"GO_0044710",
			"GO_0048511",
			"GO_0050896",
			"GO_0051234",
			"GO_0065007",
			"GO_0071704",
			"GO_0071840"
			));
	
	static List<String> lchebi15M2RS1 = new LinkedList<String>(Arrays.asList(
			"CHEBI_18059",
			"CHEBI_24835",
			"CHEBI_24870",
			"CHEBI_25367",
			"CHEBI_25806",
			"CHEBI_26082",
			"CHEBI_26835",
			"CHEBI_33241",
			"CHEBI_33259",
			"CHEBI_33285",
			"CHEBI_33674",
			"CHEBI_33694",
			"CHEBI_35701",
			"CHEBI_51143",
			"CHEBI_64709"
			));	

	static List<String> lsbo5M4RS1_ = new LinkedList<String>(Arrays.asList(
			"SBO_0000009",
			"SBO_0000064",
			"SBO_0000176",
			"SBO_0000252"

			));
	
	static List<String> lgo5M4RS1_ = new LinkedList<String>(Arrays.asList(
			"GO_0003674",
			"GO_0005575",
			"GO_0006810",
			"GO_0009987",
			"GO_0043170"

			));
	static List<String> lchebi5M4RS1_ = new LinkedList<String>(Arrays.asList(
			"CHEBI_22563",
			"CHEBI_26082",
			"CHEBI_33241",
			"CHEBI_33695",
			"CHEBI_61120"

			));
	
	static List<String> lsbo5M2RS1_ = new LinkedList<String>(Arrays.asList(
			"SBO_0000545",
			"SBO_0000064",
			"SBO_0000231",
			"SBO_0000240",
			"SBO_0000241"


			));
	
	static List<String> lgo5M2RS1_ = new LinkedList<String>(Arrays.asList(
			"GO_0003674",
			"GO_0008152",
			"GO_0009987",
			"GO_0044699",
			"GO_0051234"

			));
	
	static List<String> lchebi5M2RS1_ = new LinkedList<String>(Arrays.asList(
			"CHEBI_24870",
			"CHEBI_33302",
			"CHEBI_33304",
			"CHEBI_33582",
			"CHEBI_36357"
			));	
	
	//daraus rs2 machen
	static List<String> lsbo15M4RS2 = new LinkedList<String>(Arrays.asList(
			"SBO_0000009",
			"SBO_0000167",
			"SBO_0000240",
			"SBO_0000289"
			));
	
	static List<String> lgo15M4RS2 = new LinkedList<String>(Arrays.asList(
			"GO_0003674",
			"GO_0003675",
			"GO_0005623",
			"GO_0022411",
			"GO_0043170",
			"GO_0044238",
			"GO_0071822"
			));
	static List<String> lchebi15M4RS2 = new LinkedList<String>(Arrays.asList(
			"CHEBI_26816",
			"CHEBI_30412",
			"CHEBI_33636",
			"CHEBI_33674",
			"CHEBI_33699",
			"CHEBI_37848",
			"CHEBI_60240",
			"CHEBI_63367",
			"CHEBI_64709"
			));
	
	static List<String> lsbo15M2RS2 = new LinkedList<String>(Arrays.asList(
			"SBO_0000003",
			"SBO_0000009",
			"SBO_0000241",
			"SBO_0000245",
			"SBO_0000247",
			"SBO_0000253",
			"SBO_0000257",
			"SBO_0000258",
			"SBO_0000259",
			"SBO_0000290",
			"SBO_0000291",
			"SBO_0000308",
			"SBO_0000374",
			"SBO_0000375",
			"SBO_0000380"
			));
	
	static List<String> lgo15M2RS2 = new LinkedList<String>(Arrays.asList(
			"GO_0003674",
			"GO_0005575",
			"GO_0008152",
			"GO_0009987",
			"GO_0032501",
			"GO_0032502",
			"GO_0040011",
			"GO_0044699",
			"GO_0048511",
			"GO_0050896",
			"GO_0051234",
			"GO_0051704",
			"GO_0065007",
			"GO_0071840"
			));
	
	static List<String> lchebi15M2RS2 = new LinkedList<String>(Arrays.asList(
			"CHEBI_24835",
			"CHEBI_24870",
			"CHEBI_25806",
			"CHEBI_26082",
			"CHEBI_26835",
			"CHEBI_33259",
			"CHEBI_33674",
			"CHEBI_33694",
			"CHEBI_35701",
			"CHEBI_36962",
			"CHEBI_37577",
			"CHEBI_50906",
			"CHEBI_51143",
			"CHEBI_64709",
			"CHEBI_72695"

			));	

	static List<String> lsbo5M4RS2_ = new LinkedList<String>(Arrays.asList(
			"SBO_0000009",
			"SBO_0000167",
			"SBO_0000240",
			"SBO_0000289"
			));
	
	static List<String> lgo5M4RS2_ = new LinkedList<String>(Arrays.asList(
			"GO_0005623",
			"GO_0022411",
			"GO_0043170",
			"GO_0044238",
			"GO_0071822"

			));
	static List<String> lchebi5M4RS2_ = new LinkedList<String>(Arrays.asList(
			"CHEBI_26816",
			"CHEBI_30412",
			"CHEBI_33699",
			"CHEBI_60240",
			"CHEBI_63367"
			));
	
	static List<String> lsbo5M2RS2_ = new LinkedList<String>(Arrays.asList(
			"SBO_0000003",
			"SBO_0000231",
			"SBO_0000240",
			"SBO_0000241",
			"SBO_0000545"


			));
	
	static List<String> lgo5M2RS2_ = new LinkedList<String>(Arrays.asList(
			"GO_0003674",
			"GO_0008152",
			"GO_0009987",
			"GO_0044699",
			"GO_0071840"
			));
	
	static List<String> lchebi5M2RS2_ = new LinkedList<String>(Arrays.asList(
			"CHEBI_33302",
			"CHEBI_33304",
			"CHEBI_33582",
			"CHEBI_33674",
			"CHEBI_36357"

			));	
	
	
	static List<String> lgo5M2Apop_ = new LinkedList<String>(Arrays.asList(
			"GO_0003674","GO_0005575","GO_0008152","GO_0009987","GO_0071840"
			));	
	
	static List<String> lgo15M2Apop = new LinkedList<String>(Arrays.asList(
			"GO_0003824","GO_0005488","GO_0005575","GO_0009056","GO_0009987","GO_0030234","GO_0032501","GO_0044238","GO_0044699","GO_0050896","GO_0051234","GO_0065007","GO_0071704","GO_0071840"
			));	
	
	static List<String> lgo5M4Apop_ = new LinkedList<String>(Arrays.asList(
			"GO_0005515","GO_0030693","GO_0044257","GO_0065003","GO_0071822"
			));	
	
	static List<String> lgo15M4Apop= new LinkedList<String>(Arrays.asList(
			"GO_0002090","GO_0005515","GO_0016265","GO_0030693","GO_0031264","GO_0043027","GO_0044257","GO_0065003","GO_0071822"
			));	
	
	static List<String> lgo5M2CaOz_ = new LinkedList<String>(Arrays.asList(
			"GO_0003674","GO_0009987","GO_0044699","GO_0051234","GO_0065007"
			));	
	
	static List<String> lgo15M2CaOz = new LinkedList<String>(Arrays.asList(
			"GO_0003824","GO_0004872","GO_0005215","GO_0005488","GO_0005575","GO_0007204",
			"GO_0022411","GO_0032469","GO_0044237","GO_0050789","GO_0051234","GO_0051481","GO_0051716","GO_0060089","GO_0065009"
			));	
	
	static List<String> lgo5M4CaOz_ = new LinkedList<String>(Arrays.asList(
			"GO_0005217","GO_0005829","GO_0006816","GO_0015085","GO_0051480"
			));	
	
	static List<String> lgo15M4CaOz = new LinkedList<String>(Arrays.asList(
			"GO_0005217","GO_0005783","GO_0005829","GO_0006816",
			"GO_0015085","GO_0017111","GO_0038023","GO_0051480"
			));	
	
	static List<String> lgo5M2NFKB_ = new LinkedList<String>(Arrays.asList(
			"GO_0003674","GO_0008152","GO_0009987",
			"GO_0044699","GO_0071840"
			));	
	
	static List<String> lgo15M2NFKB = new LinkedList<String>(Arrays.asList(
			"GO_0003674","GO_0005575","GO_0006807","GO_0009056","GO_0009058",
			"GO_0044237","GO_0044238","GO_0044699","GO_0044710","GO_0050896","GO_0051234","GO_0065007","GO_0071704","GO_0071840"
			));	
	
	static List<String> lgo5M4NFKB_ = new LinkedList<String>(Arrays.asList(
			"GO_0005515","GO_0006886","GO_0022607","GO_0044257","GO_0071822"
			));	
	
	static List<String> lgo15M4NFKB = new LinkedList<String>(Arrays.asList(
			"GO_0005515","GO_0005634","GO_0006886","GO_0016563","GO_0022607","GO_0044257","GO_0071822"
			));	
	
	
	
//	static List<String> lsbo5M2Apop_ = new LinkedList<String>(Arrays.asList(
//			
//			));	
//	
//	static List<String> lsbo15M2Apop = new LinkedList<String>(Arrays.asList(
//			
//			));	
//	
//	static List<String> lsbo5M4Apop_ = new LinkedList<String>(Arrays.asList(
//			
//			));	
//	
//	static List<String> lsbo15M4Apop= new LinkedList<String>(Arrays.asList(
//			
//			));	
//	
//	static List<String> lsbo5M2CaOz_ = new LinkedList<String>(Arrays.asList(
//			
//			));	
//	
//	static List<String> lsbo15M2CaOz = new LinkedList<String>(Arrays.asList(
//			
//			));	
//	
//	static List<String> lsbo5M4CaOz_ = new LinkedList<String>(Arrays.asList(
//			
//			));	
//	
//	static List<String> lsbo15M4CaOz = new LinkedList<String>(Arrays.asList(
//			
//			));	
//	
//	static List<String> lsbo5M2NFKB_ = new LinkedList<String>(Arrays.asList(
//			
//			));	
//	
//	static List<String> lsbo15M2NFKB = new LinkedList<String>(Arrays.asList(
//			
//			));	
//	
//	static List<String> lsbo5M4NFKB_ = new LinkedList<String>(Arrays.asList(
//			
//			));	
//	
//	static List<String> lsbo15M4NFKB = new LinkedList<String>(Arrays.asList(
//			
//			));	
	
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
		//start the engine

		System.out.println("done in " + (System.currentTimeMillis() - start)
				+ "ms");
		System.out.println();

		String s = "";
		while (!s.equals("exit")) {
			System.out
					.println("(1) DepthStats, (2) ConceptDepth F5, (3) ConceptDepth F15, (4) ConceptSim");
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
				case 1: {conceptDepthCounter(); break;}
				case 2: {
					//CC
					System.out.println("Chebi, F5, M2 CC");
					conceptDepthList("Chebi", lchebi5M2CC_); 
					System.out.println("---------------------");
					System.out.println("Chebi, F5, M4 CC");
					conceptDepthList("Chebi", lchebi5M4CC_); 
					System.out.println("---------------------");
					System.out.println("Go, F5, M2 CC");
					conceptDepthList("GO", lgo5M2CC_); 
					System.out.println("---------------------");
					System.out.println("Go, F5, M4 CC");
					conceptDepthList("GO", lgo5M4CC_); 
					System.out.println("---------------------");
					System.out.println("SBO, F5, M2 CC");
					conceptDepthList("SBO", lsbo5M2CC_); 
					System.out.println("---------------------");
					System.out.println("SBO, F5, M4 CC");
					conceptDepthList("SBO", lsbo5M4CC_); 
					System.out.println("---------------------");
					
					//BIODB
					System.out.println("Chebi, F5, M2 F");
					conceptDepthList("Chebi", lchebi5M2F_); 
					System.out.println("---------------------");
					System.out.println("Chebi, F5, M4 F");
					conceptDepthList("Chebi", lchebi5M4F_); 
					System.out.println("---------------------");
					System.out.println("Go, F5, M2 F");
					conceptDepthList("GO", lgo5M2F_); 
					System.out.println("---------------------");
					System.out.println("Go, F5, M4 F");
					conceptDepthList("GO", lgo5M4F_); 
					System.out.println("---------------------");
					System.out.println("SBO, F5, M2 F");
					conceptDepthList("SBO", lsbo5M2F_); 
					System.out.println("---------------------");
					System.out.println("SBO, F5, M4 F");
					conceptDepthList("SBO", lsbo5M4F_); 
					System.out.println("---------------------");
					
					//RS1
					System.out.println("Chebi, F5, M2 RS1");
					conceptDepthList("Chebi", lchebi5M2RS1_); 
					System.out.println("---------------------");
					System.out.println("Chebi, F5, M4 RS1");
					conceptDepthList("Chebi", lchebi5M4RS1_); 
					System.out.println("---------------------");
					System.out.println("Go, F5, M2 RS1");
					conceptDepthList("GO", lgo5M2RS1_); 
					System.out.println("---------------------");
					System.out.println("Go, F5, M4 RS1");
					conceptDepthList("GO", lgo5M4RS1_); 
					System.out.println("---------------------");
					System.out.println("SBO, F5, M2 RS1");
					conceptDepthList("SBO", lsbo5M2RS1_); 
					System.out.println("---------------------");
					System.out.println("SBO, F5, M4 RS1");
					conceptDepthList("SBO", lsbo5M4RS1_); 
					System.out.println("---------------------");
					
					//RS2
					System.out.println("Chebi, F5, M2 RS2");
					conceptDepthList("Chebi", lchebi5M2RS2_); 
					System.out.println("---------------------");
					System.out.println("Chebi, F5, M4 RS2");
					conceptDepthList("Chebi", lchebi5M4RS2_); 
					System.out.println("---------------------");
					System.out.println("Go, F5, M2 RS2");
					conceptDepthList("GO", lgo5M2RS2_); 
					System.out.println("---------------------");
					System.out.println("Go, F5, M4 RS2");
					conceptDepthList("GO", lgo5M4RS2_); 
					System.out.println("---------------------");
					System.out.println("SBO, F5, M2 RS2");
					conceptDepthList("SBO", lsbo5M2RS2_); 
					System.out.println("---------------------");
					System.out.println("SBO, F5, M4 RS2");
					conceptDepthList("SBO", lsbo5M4RS2_); 
					System.out.println("---------------------");
					
					
					//Apop
//					System.out.println("Chebi, F5, M2 Apop");
//					conceptDepthList("Chebi", lchebi5M2Apop_); 
//					System.out.println("---------------------");
//					System.out.println("Chebi, F5, M4 Apop");
//					conceptDepthList("Chebi", lchebi5M4Apop_); 
//					System.out.println("---------------------");
					System.out.println("Go, F5, M2 Apop");
					conceptDepthList("GO", lgo5M2Apop_); 
					System.out.println("---------------------");
					System.out.println("Go, F5, M4 Apop");
					conceptDepthList("GO", lgo5M4Apop_); 
					System.out.println("---------------------");
//					System.out.println("SBO, F5, M2 Apop");
//					conceptDepthList("SBO", lsbo5M2Apop_); 
//					System.out.println("---------------------");
//					System.out.println("SBO, F5, M4 Apop");
//					conceptDepthList("SBO", lsbo5M4Apop_); 
//					System.out.println("---------------------");
					
					//CaOz
//					System.out.println("Chebi, F5, M2 CaOz");
//					conceptDepthList("Chebi", lchebi5M2CaOz_); 
//					System.out.println("---------------------");
//					System.out.println("Chebi, F5, M4 CaOz");
//					conceptDepthList("Chebi", lchebi5M4CaOz_); 
//					System.out.println("---------------------");
					System.out.println("Go, F5, M2 CaOz");
					conceptDepthList("GO", lgo5M2CaOz_); 
					System.out.println("---------------------");
					System.out.println("Go, F5, M4 CaOz");
					conceptDepthList("GO", lgo5M4CaOz_); 
					System.out.println("---------------------");
//					System.out.println("SBO, F5, M2 CaOz");
//					conceptDepthList("SBO", lsbo5M2CaOz_); 
//					System.out.println("---------------------");
//					System.out.println("SBO, F5, M4 CaOz");
//					conceptDepthList("SBO", lsbo5M4CaOz_); 
//					System.out.println("---------------------");
					
					//NFKB
//					System.out.println("Chebi, F5, M2 NFKB");
//					conceptDepthList("Chebi", lchebi5M2NFKB_); 
//					System.out.println("---------------------");
//					System.out.println("Chebi, F5, M4 NFKB");
//					conceptDepthList("Chebi", lchebi5M4NFKB_); 
					System.out.println("---------------------");
					System.out.println("Go, F5, M2 NFKB");
					conceptDepthList("GO", lgo5M2NFKB_); 
					System.out.println("---------------------");
					System.out.println("Go, F5, M4 NFKB");
					conceptDepthList("GO", lgo5M4NFKB_); 
					System.out.println("---------------------");
					System.out.println("SBO, F5, M2 NFKB");
//					conceptDepthList("SBO", lsbo5M2NFKB_); 
//					System.out.println("---------------------");
//					System.out.println("SBO, F5, M4 NFKB");
//					conceptDepthList("SBO", lsbo5M4NFKB_); 
//					System.out.println("---------------------");
					break;
				}
				case 3: {
					//CC
					System.out.println("Chebi, F15, M2 CC");
					conceptDepthList("Chebi", lchebi15M2CC); 
					System.out.println("---------------------");
					System.out.println("Chebi, F15, M4 CC");
					conceptDepthList("Chebi", lchebi15M4CC); 
					System.out.println("---------------------");
					System.out.println("Go, F15, M2 CC");
					conceptDepthList("GO", lgo15M2CC); 
					System.out.println("---------------------");
					System.out.println("Go, F15, M4 CC");
					conceptDepthList("GO", lgo15M4CC); 
					System.out.println("---------------------");
					System.out.println("SBO, F15, M2 CC");
					conceptDepthList("SBO", lsbo15M2CC); 
					System.out.println("---------------------");
					System.out.println("SBO, F15, M4 CC");
					conceptDepthList("SBO", lsbo15M4CC); 
					System.out.println("---------------------");
					
					//BIODB
					System.out.println("Chebi, F15, M2 F");
					conceptDepthList("Chebi", lchebi15M2F); 
					System.out.println("---------------------");
					System.out.println("Chebi, F15, M4 F");
					conceptDepthList("Chebi", lchebi15M4F); 
					System.out.println("---------------------");
					System.out.println("Go, F15, M2 F");
					conceptDepthList("GO", lgo15M2F); 
					System.out.println("---------------------");
					System.out.println("Go, F15, M4 F");
					conceptDepthList("GO", lgo15M4F); 
					System.out.println("---------------------");
					System.out.println("SBO, F15, M2 F");
					conceptDepthList("SBO", lsbo15M2F); 
					System.out.println("---------------------");
					System.out.println("SBO, F15, M4 F");
					conceptDepthList("SBO", lsbo15M4F); 
					System.out.println("---------------------");
					
					//RS1
					System.out.println("Chebi, F15, M2 RS1");
					conceptDepthList("Chebi", lchebi15M2RS1); 
					System.out.println("---------------------");
					System.out.println("Chebi, F15, M4 RS1");
					conceptDepthList("Chebi", lchebi15M4RS1); 
					System.out.println("---------------------");
					System.out.println("Go, F15, M2 RS1");
					conceptDepthList("GO", lgo15M2RS1); 
					System.out.println("---------------------");
					System.out.println("Go, F15, M4 RS1");
					conceptDepthList("GO", lgo15M4RS1); 
					System.out.println("---------------------");
					System.out.println("SBO, F15, M2 RS1");
					conceptDepthList("SBO", lsbo15M2RS1); 
					System.out.println("---------------------");
					System.out.println("SBO, F15, M4 RS1");
					conceptDepthList("SBO", lsbo15M4RS1); 
					System.out.println("---------------------");
					
					//RS2
					System.out.println("Chebi, F15, M2 RS2");
					conceptDepthList("Chebi", lchebi15M2RS2); 
					System.out.println("---------------------");
					System.out.println("Chebi, F15, M4 RS2");
					conceptDepthList("Chebi", lchebi15M4RS2); 
					System.out.println("---------------------");
					System.out.println("Go, F15, M2 RS2");
					conceptDepthList("GO", lgo15M2RS2); 
					System.out.println("---------------------");
					System.out.println("Go, F15, M4 RS2");
					conceptDepthList("GO", lgo15M4RS2); 
					System.out.println("---------------------");
					System.out.println("SBO, F15, M2 RS2");
					conceptDepthList("SBO", lsbo15M2RS2); 
					System.out.println("---------------------");
					System.out.println("SBO, F15, M4 RS2");
					conceptDepthList("SBO", lsbo15M4RS2); 
					System.out.println("---------------------");
					
					//Apop
//					System.out.println("Chebi, F15, M2 Apop");
//					conceptDepthList("Chebi", lchebi15M2Apop); 
//					System.out.println("---------------------");
//					System.out.println("Chebi, F15, M4 Apop");
//					conceptDepthList("Chebi", lchebi15M4Apop); 
//					System.out.println("---------------------");
					System.out.println("Go, F15, M2 Apop");
					conceptDepthList("GO", lgo15M2Apop); 
					System.out.println("---------------------");
					System.out.println("Go, F15, M4 Apop");
					conceptDepthList("GO", lgo15M4Apop); 
					System.out.println("---------------------");
//					System.out.println("SBO, F15, M2 Apop");
//					conceptDepthList("SBO", lsbo15M2Apop); 
//					System.out.println("---------------------");
//					System.out.println("SBO, F15, M4 Apop");
//					conceptDepthList("SBO", lsbo15M4Apop); 
//					System.out.println("---------------------");
					
					//CaOz
//					System.out.println("Chebi, F15, M2 CaOz");
//					conceptDepthList("Chebi", lchebi15M2CaOz); 
//					System.out.println("---------------------");
//					System.out.println("Chebi, F15, M4 CaOz");
//					conceptDepthList("Chebi", lchebi15M4CaOz); 
//					System.out.println("---------------------");
					System.out.println("Go, F15, M2 CaOz");
					conceptDepthList("GO", lgo15M2CaOz); 
					System.out.println("---------------------");
					System.out.println("Go, F15, M4 CaOz");
					conceptDepthList("GO", lgo15M4CaOz); 
					System.out.println("---------------------");
//					System.out.println("SBO, F15, M2 CaOz");
//					conceptDepthList("SBO", lsbo15M2CaOz); 
//					System.out.println("---------------------");
//					System.out.println("SBO, F15, M4 CaOz");
//					conceptDepthList("SBO", lsbo15M4CaOz); 
//					System.out.println("---------------------");
//					
					//NFKB
//					System.out.println("Chebi, F15, M2 NFKB");
//					conceptDepthList("Chebi", lchebi15M2NFKB); 
//					System.out.println("---------------------");
//					System.out.println("Chebi, F15, M4 NFKB");
//					conceptDepthList("Chebi", lchebi15M4NFKB); 
					System.out.println("---------------------");
					System.out.println("Go, F15, M2 NFKB");
					conceptDepthList("GO", lgo15M2NFKB); 
					System.out.println("---------------------");
					System.out.println("Go, F15, M4 NFKB");
					conceptDepthList("GO", lgo15M4NFKB); 
					System.out.println("---------------------");
//					System.out.println("SBO, F15, M2 NFKB");
//					conceptDepthList("SBO", lsbo15M2NFKB); 
//					System.out.println("---------------------");
//					System.out.println("SBO, F15, M4 NFKB");
//					conceptDepthList("SBO", lsbo15M4NFKB); 
//					System.out.println("---------------------");
					break;
				}	
				case 4: {
					conceptSim();
					break;
				}
			default:
				continue;
			}
		}
		System.exit(0);
	}

	
	public static void conceptDepthCounter(){
		String s = "";
		while (!s.equals("exit")) {
			System.out.println("Ontology Prefix:");
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
			System.out.println("DepthStats for: " + s);
			Result result;
			Node root = OntologyUtil.getRoot(s);
			Map<Integer, Long> depthMap = new HashMap<Integer,Long>();
			try(Transaction tx = Manager.instance().getDatabase().beginTx()){
				GraphDatabaseService graphDB = Manager.instance().getDatabase();
				result = graphDB.execute("match (o:" + s + "Ontology)<-[:ENTITY_TO_" + s.toUpperCase()  + "]-()  return o;");
				for (ResourceIterator<Node> iterator = result.columnAs("o"); iterator
						.hasNext();) {
					Node node = (Node) iterator.next();
					Integer depth = OntologyUtil.getDepth(node, root);
					if (depthMap.containsKey(depth)){
						depthMap.put(depth, (Long)node.getProperty("entityfrequency", 0) + depthMap.get(depth));
					} else {
						depthMap.put(depth, (Long)node.getProperty("entityfrequency", 0));
					}
						
				}
				tx.success();
			}
		
			for (Iterator<Integer> iterator = depthMap.keySet().iterator(); iterator.hasNext();) {
				Integer d = (Integer) iterator.next();
				System.out.println(d + "," + depthMap.get(d));
				
			}
		}	
		
	}
	
	public static void conceptDepthList(String ontoPrefix, List<String> conceptList){
		//Map<String, Long> resMap = new HashMap<String, Long>();
		GraphDatabaseService graphDB = Manager.instance().getDatabase();
		Result result;
		Node root = OntologyUtil.getRoot(ontoPrefix);
		try(Transaction tx = Manager.instance().getDatabase().beginTx()){			
			for (Iterator<String> iterator = conceptList.iterator(); iterator.hasNext();) {
				String id = (String) iterator.next();
				result = graphDB.execute("match (o:" + ontoPrefix + "Ontology) where (o.id='"+id+"') return o");			
				ResourceIterator<Node> iRoot = result.columnAs("o");
				Node concept = iRoot.next();
				iRoot.close();	
				System.out.println(id + ", " + OntologyUtil.getDepth(concept, root));
			}
		}
	}

	public static void conceptSim(){
		String s = "";
		String dir = "D:/xxx/";
		while (!s.equals("exit")) {
			try {
				System.out.println("Ontology Prefix:");
				BufferedReader in = new BufferedReader(new InputStreamReader(
						System.in));
				s = in.readLine();
				System.out.println("Output Dir:");
				dir = in.readLine();
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
			if (s.equals("exit")) {
				break;
			}
		
			switch (s) {
			case "SBO": { //just for testing...only go has meaningfull results
				printAssignListToFile(calcAssign(s, lsbo15M2F, lsbo15M2CC), dir, "sbo15M2fcc"); 
				printAssignListToFile(calcAssign(s, lsbo15M4F, lsbo15M4CC), dir, "sbo15M4fcc");
				printAssignListToFile(calcAssign(s, lsbo5M2F_, lsbo5M2CC_), dir, "sbo5M2fcc"); 
				printAssignListToFile(calcAssign(s, lsbo5M4F_, lsbo5M4CC_), dir, "sbo5M4fcc");
				
				printAssignListToFile(calcAssign(s, lsbo15M2RS1, lsbo15M2CC), dir, "sbo15M2r1cc"); 
				printAssignListToFile(calcAssign(s, lsbo15M4RS1, lsbo15M4CC), dir, "sbo15M4r1cc");
				printAssignListToFile(calcAssign(s, lsbo5M2RS1_, lsbo5M2CC_), dir, "sbo5M2r1cc"); 
				printAssignListToFile(calcAssign(s, lsbo5M4RS1_, lsbo5M4CC_), dir, "sbo5M4r1cc");
				
				printAssignListToFile(calcAssign(s, lsbo15M2F, lsbo15M2RS1), dir, "sbo15M2fr1"); 
				printAssignListToFile(calcAssign(s, lsbo15M4F, lsbo15M4RS1), dir, "sbo15M4fr1");
				printAssignListToFile(calcAssign(s, lsbo5M2F_, lsbo5M2RS1_), dir, "sbo5M2fr1"); 
				printAssignListToFile(calcAssign(s, lsbo5M4F_, lsbo5M4RS1_), dir, "sbo5M4fr1");
				

				break;
				}
			case "GO": {
				
				System.out.println("GO F - CC");
				printAssignListToFile(calcAssign(s, lgo15M2F, lgo15M2CC), dir, "go15M2fcc"); 
				printAssignListToFile(calcAssign(s, lgo15M4F, lgo15M4CC), dir, "go15M4fcc"); 
				printAssignListToFile(calcAssign(s, lgo5M2F_, lgo5M2CC_), dir, "go5M2fcc"); 
				printAssignListToFile(calcAssign(s, lgo5M4F_, lgo5M4CC_), dir, "go5M4fcc");

				System.out.println("GO r1 - CC");
				printAssignListToFile(calcAssign(s, lgo15M2RS1, lgo15M2CC), dir, "go15M2r1cc"); 
				printAssignListToFile(calcAssign(s, lgo15M4RS1, lgo15M4CC), dir, "go15M4r1cc");
				printAssignListToFile(calcAssign(s, lgo5M2RS1_, lgo5M2CC_), dir, "go5M2r1cc"); 
				printAssignListToFile(calcAssign(s, lgo5M4RS1_, lgo5M4CC_), dir, "go5M4r1cc");

				System.out.println("GO F - r1");
				printAssignListToFile(calcAssign(s, lgo15M2F, lgo15M2RS1), dir, "go15M2fr1"); 
				printAssignListToFile(calcAssign(s, lgo15M4F, lgo15M4RS1), dir, "go15M4fr1");
				printAssignListToFile(calcAssign(s, lgo5M2F_, lgo5M2RS1_), dir, "go5M2fr1"); 
				printAssignListToFile(calcAssign(s, lgo5M4F_, lgo5M4RS1_), dir, "go5M4fr1");

				System.out.println("GO caoz - CC");
				printAssignListToFile(calcAssign(s, lgo15M2CaOz, lgo15M2CC), dir, "go15M2caozcc"); 
				printAssignListToFile(calcAssign(s, lgo15M4CaOz, lgo15M4CC), dir, "go15M4caozcc"); 
				printAssignListToFile(calcAssign(s, lgo5M2CaOz_, lgo5M2CC_), dir, "go5M2caozcc"); 
				printAssignListToFile(calcAssign(s, lgo5M4CaOz_, lgo5M4CC_), dir, "go5M4caozcc");

				System.out.println("GO F - caoz");
				printAssignListToFile(calcAssign(s, lgo15M2F, lgo15M2CaOz), dir, "go15M2fcaoz"); 
				printAssignListToFile(calcAssign(s, lgo15M4F, lgo15M4CaOz), dir, "go15M4fcaoz"); 
				printAssignListToFile(calcAssign(s, lgo5M2F_, lgo5M2CaOz_), dir, "go5M2fcaoz"); 
				printAssignListToFile(calcAssign(s, lgo5M4F_, lgo5M4CaOz_), dir, "go5M4fcaoz");

				System.out.println("GO caoz - r1");
				printAssignListToFile(calcAssign(s, lgo15M2CaOz, lgo15M2RS1), dir, "go15M2caozr1"); 
				printAssignListToFile(calcAssign(s, lgo15M4CaOz, lgo15M4RS1), dir, "go15M4caozr1"); 
				printAssignListToFile(calcAssign(s, lgo5M2CaOz_, lgo5M2RS1_), dir, "go5M2caozr1"); 
				printAssignListToFile(calcAssign(s, lgo5M4CaOz_, lgo5M4RS1_), dir, "go5M4caozr1");

				System.out.println("GO apop - caoz");
				printAssignListToFile(calcAssign(s, lgo15M2Apop, lgo15M2CaOz), dir, "go15M2apopcaoz"); 
				printAssignListToFile(calcAssign(s, lgo15M4Apop, lgo15M4CaOz), dir, "go15M4apopcaoz"); 
				printAssignListToFile(calcAssign(s, lgo5M2Apop_, lgo5M2CaOz_), dir, "go5M2apopcaoz"); 
				printAssignListToFile(calcAssign(s, lgo5M4Apop_, lgo5M4CaOz_), dir, "go5M4apopcaoz");

				System.out.println("GO nfkb - caoz");
				printAssignListToFile(calcAssign(s, lgo15M2NFKB, lgo15M2CaOz), dir, "go15M2nfkbcaoz"); 
				printAssignListToFile(calcAssign(s, lgo15M4NFKB, lgo15M4CaOz), dir, "go15M4nfkbcaoz"); 
				printAssignListToFile(calcAssign(s, lgo5M2NFKB_, lgo5M2CaOz_), dir, "go5M2nfkbcaoz"); 
				printAssignListToFile(calcAssign(s, lgo5M4NFKB_, lgo5M4CaOz_), dir, "go5M4nfkbcaoz");

				System.out.println("GO nfkb - apop");
				printAssignListToFile(calcAssign(s, lgo15M2NFKB, lgo15M2Apop), dir, "go15M2nfkbapop"); 
				printAssignListToFile(calcAssign(s, lgo15M4NFKB, lgo15M4Apop), dir, "go15M4nfkbapop"); 
				printAssignListToFile( calcAssign(s, lgo5M2NFKB_, lgo5M2Apop_), dir, "go5M2nfkbapop"); 
				printAssignListToFile(calcAssign(s, lgo5M4NFKB_, lgo5M4Apop_), dir, "go5M4nfkbapop");

				System.out.println("GO nfkb - CC");
				printAssignListToFile(calcAssign(s, lgo15M2NFKB, lgo15M2CC), dir, "go15M2nfkbcc"); 
				printAssignListToFile(calcAssign(s, lgo15M4NFKB, lgo15M4CC), dir, "go15M4nfkbcc"); 
				printAssignListToFile(calcAssign(s, lgo5M2NFKB_, lgo5M2CC_), dir, "go5M2nfkbcc"); 
				printAssignListToFile(calcAssign(s, lgo5M4NFKB_, lgo5M4CC_), dir, "go5M4nfkbcc");

				System.out.println("GO nfkb - f");
				printAssignListToFile(calcAssign(s, lgo15M2NFKB, lgo15M2F), dir, "go15M2nfkbf"); 
				printAssignListToFile(calcAssign(s, lgo15M4NFKB, lgo15M4F), dir, "go15M4nfkbf"); 
				printAssignListToFile(calcAssign(s, lgo5M2NFKB_, lgo5M2F_), dir, "go5M2nfkbf"); 
				printAssignListToFile(calcAssign(s, lgo5M4NFKB_, lgo5M4F_), dir, "go5M4nfkbf");

				System.out.println("GO nfkb - r1");
				printAssignListToFile(calcAssign(s, lgo15M2NFKB, lgo15M2RS1), dir, "go15M2nfkbr1"); 
				printAssignListToFile(calcAssign(s, lgo15M4NFKB, lgo15M4RS1), dir, "go15M4nfkbr1"); 
				printAssignListToFile(calcAssign(s, lgo5M2NFKB_, lgo5M2RS1_), dir, "go5M2nfkbr1"); 
				printAssignListToFile(calcAssign(s, lgo5M4NFKB_, lgo5M4RS1_), dir, "go5M4nfkbr1");

				System.out.println("GO apop - CC");
				printAssignListToFile(calcAssign(s, lgo15M2Apop, lgo15M2CC), dir, "go15M2apopcc"); 
				printAssignListToFile(calcAssign(s, lgo15M4Apop, lgo15M4CC), dir, "go15M4apopcc"); 
				printAssignListToFile( calcAssign(s, lgo5M2Apop_, lgo5M2CC_), dir, "go5M2apopcc"); 
				printAssignListToFile(calcAssign(s, lgo5M4Apop_, lgo5M4CC_), dir, "go5M4apopcc");

				System.out.println("GO apop - r1");
				printAssignListToFile(calcAssign(s, lgo15M2Apop, lgo15M2RS1), dir, "go15M2apopr1"); 
				printAssignListToFile(calcAssign(s, lgo15M4Apop, lgo15M4RS1), dir, "go15M4apopr1"); 
				printAssignListToFile(calcAssign(s, lgo5M2Apop_, lgo5M2RS1_), dir, "go5M2apopr1"); 
				printAssignListToFile(calcAssign(s, lgo5M4Apop_, lgo5M4RS1_), dir, "go5M4apopr1");

				System.out.println("GO apop - f");
				printAssignListToFile(calcAssign(s, lgo15M2Apop, lgo15M2F), dir, "go15M2apopf"); 
				printAssignListToFile(calcAssign(s, lgo15M4Apop, lgo15M4F), dir, "go15M4apopf"); 
				printAssignListToFile(calcAssign(s, lgo5M2Apop_, lgo5M2F_), dir, "go5M2apopf"); 
				printAssignListToFile(calcAssign(s, lgo5M4Apop_, lgo5M4F_), dir, "go5M4apopf");

				System.out.println("GO apop - r2");
				printAssignListToFile(calcAssign(s, lgo15M2Apop, lgo15M2RS2), dir, "go15M2apoprs2"); 
				printAssignListToFile(calcAssign(s, lgo15M4Apop, lgo15M4RS2), dir, "go15M4apoprs2"); 
				printAssignListToFile(calcAssign(s, lgo5M2Apop_, lgo5M2RS2_), dir, "go5M2apoprs2"); 
				printAssignListToFile(calcAssign(s, lgo5M4Apop_, lgo5M4RS2_), dir, "go5M4apoprs2");
				
				System.out.println("GO NFKB - r2");
				printAssignListToFile(calcAssign(s, lgo15M2NFKB, lgo15M2RS2), dir, "go15M2NFKBrs2"); 
				printAssignListToFile(calcAssign(s, lgo15M4NFKB, lgo15M4RS2), dir, "go15M4NFKBrs2"); 
				printAssignListToFile(calcAssign(s, lgo5M2NFKB_, lgo5M2RS2_), dir, "go5M2NFKBrs2"); 
				printAssignListToFile(calcAssign(s, lgo5M4NFKB_, lgo5M4RS2_), dir, "go5M4NFKBrs2");
				
				System.out.println("GO CaOz - r2");
				printAssignListToFile(calcAssign(s, lgo15M2CaOz, lgo15M2RS2), dir, "go15M2CaOzrs2"); 
				printAssignListToFile(calcAssign(s, lgo15M4CaOz, lgo15M4RS2), dir, "go15M4CaOzrs2"); 
				printAssignListToFile(calcAssign(s, lgo5M2CaOz_, lgo5M2RS2_), dir, "go5M2CaOzrs2"); 
				printAssignListToFile(calcAssign(s, lgo5M4CaOz_, lgo5M4RS2_), dir, "go5M4CaOzrs2");
				
				System.out.println("GO CC - r2");
				printAssignListToFile(calcAssign(s, lgo15M2CC, lgo15M2RS2), dir, "go15M2CCrs2"); 
				printAssignListToFile(calcAssign(s, lgo15M4CC, lgo15M4RS2), dir, "go15M4CCrs2"); 
				printAssignListToFile(calcAssign(s, lgo5M2CC_, lgo5M2RS2_), dir, "go5M2CCrs2"); 
				printAssignListToFile(calcAssign(s, lgo5M4CC_, lgo5M4RS2_), dir, "go5M4CCrs2");
				
				System.out.println("GO RS1 - r2");
				printAssignListToFile(calcAssign(s, lgo15M2RS1, lgo15M2RS2), dir, "go15M2rs1rs2"); 
				printAssignListToFile(calcAssign(s, lgo15M4RS1, lgo15M4RS2), dir, "go15M4rs1rs2"); 
				printAssignListToFile(calcAssign(s, lgo5M2RS1_, lgo5M2RS2_), dir, "go5M2rs1rs2"); 
				printAssignListToFile(calcAssign(s, lgo5M4RS1_, lgo5M4RS2_), dir, "go5M4rs1rs2");
				
				System.out.println("GO f - r2");
				printAssignListToFile(calcAssign(s, lgo15M2F, lgo15M2RS2), dir, "go15M2frs2"); 
				printAssignListToFile(calcAssign(s, lgo15M4F, lgo15M4RS2), dir, "go15M4frs2"); 
				printAssignListToFile(calcAssign(s, lgo5M2F_, lgo5M2RS2_), dir, "go5M2frs2"); 
				printAssignListToFile(calcAssign(s, lgo5M4F_, lgo5M4RS2_), dir, "go5M4frs2");

				
				
				break;}
			case "Chebi":{ //just for testing...only go has meaningfull results
				printAssignListToFile( calcAssign(s, lchebi15M2F, lchebi15M2CC), dir, "chebi15M2fcc"); 
				printAssignListToFile(calcAssign(s, lchebi15M4F, lchebi15M4CC), dir, "chebi15M4fcc"); 
				printAssignListToFile(calcAssign(s, lchebi5M2F_, lchebi5M2CC_), dir, "chebi5M2fcc"); 
				printAssignListToFile(calcAssign(s, lchebi5M4F_, lchebi5M4CC_), dir, "chebi5M4fcc");
				
				printAssignListToFile(calcAssign(s, lchebi15M2RS1, lchebi15M2CC), dir, "chebi15M2r1cc"); 
				printAssignListToFile(calcAssign(s, lchebi15M4RS1, lchebi15M4CC), dir, "chebi15M4r1cc");
				printAssignListToFile(calcAssign(s, lchebi5M2RS1_, lchebi5M2CC_), dir, "chebi5M2r1cc"); 
				printAssignListToFile(calcAssign(s, lchebi5M4RS1_, lchebi5M4CC_), dir, "chebi5M4r1cc");
				
				printAssignListToFile(calcAssign(s, lchebi15M2F, lchebi15M2RS1), dir, "chebi15M2fr1"); 
				printAssignListToFile(calcAssign(s, lchebi15M4F, lchebi15M4RS1), dir, "chebi15M4fr1");
				printAssignListToFile( calcAssign(s, lchebi5M2F_, lchebi5M2RS1_), dir, "chebi5M2fr1"); 
				printAssignListToFile(calcAssign(s, lchebi5M4F_, lchebi5M4RS1_), dir, "chebi5M4fr1");
				
				break;}
			default:
				continue;
			}	
		}
	}
			

	private static List<Assign> calcAssign(String ontologyPrefix, List<String> v1, List<String> v2){
	
		int maxSize = Math.max(v1.size(),v2.size());
		double[][] simMatrix = new double[maxSize][maxSize];
		String l1Item = null;
		String l2Item = null;
		for (int i = 0; i < maxSize; i++) {
			if (i < v1.size())  l1Item = v1.get(i);
			else l1Item = null;
			for (int j = 0; j < maxSize; j++) {
				if (j < v2.size())  l2Item = v2.get(j);
				else l2Item = null;
				if ((l1Item==null) || (l2Item==null)) {
					simMatrix[i][j] = 0d;
					continue;
				}
				if (StringUtils.equalsIgnoreCase(l1Item, l2Item)) {
					simMatrix[i][j] = 1d;
					continue;
				}
				simMatrix[i][j] = OntologyUtil.getSimilarity(ontologyPrefix, OntologyUtil.getNodeById(ontologyPrefix, l1Item), OntologyUtil.getNodeById(ontologyPrefix, l2Item) );
			}
		}
		double[][] distMatrix =  new double[maxSize][maxSize];
		for (int i = 0; i < maxSize; i++) {			
			for (int j = 0; j < maxSize; j++) {
			 distMatrix[i][j] = 1 - simMatrix[i][j];
			}
		}
		HungarianAlgorithm ha = new HungarianAlgorithm();
		int[][] assignment = ha.computeAssignments(distMatrix);
		
		List<Assign> asg = new LinkedList<Assign>();
		for (int i = 0; i < assignment.length; i++) {			
			int c1 = assignment[i][0];
			int c2 = assignment[i][1];
			if (c1 < v1.size())  l1Item = v1.get(c1);
			else l1Item = null;
			if (c2 < v2.size())  l2Item = v2.get(c2);
			else l2Item = null;
			asg.add(new Assign(l1Item, l2Item, simMatrix[c1][c2]));
		}
		
		return asg;
				
		
	}	
	
	private static void printAssignListToFile(List<Assign> l, String path, String name){
		  try {
              BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path+name), true));
              bw.write(name);
              bw.newLine();
              for (Iterator<Assign> iterator = l.iterator(); iterator.hasNext();) {
					Assign assign = (Assign) iterator.next();
					if (assign.getN1()!= null)  bw.write(assign.getN1());
					else bw.write("null");
					bw.write(";");
					if (assign.getN2()!= null)  bw.write(assign.getN2());
					else bw.write("null");
					bw.write(";");
					bw.write(String.valueOf(assign.getSim()));
					bw.newLine();
              }
              bw.close();
      } catch (Exception e) {
    	  e.printStackTrace();
      }
	}

	


}
