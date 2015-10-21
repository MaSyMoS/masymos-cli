package de.unirostock.sems.masymos.grouping.algorithm;


import java.util.LinkedList;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Traverser;

import de.unirostock.sems.masymos.configuration.Config;
import de.unirostock.sems.masymos.configuration.Property;
import de.unirostock.sems.masymos.database.Manager;
import de.unirostock.sems.masymos.grouping.Group;
import de.unirostock.sems.masymos.grouping.GroupList;
import de.unirostock.sems.masymos.grouping.UGroupingUtil;

public class StaticGrouping {

	protected static GraphDatabaseService graphDB;

	public static void main(String[] args) {

		int features = 5; 
		double balance = 0.6;
		
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
				balance = Double.parseDouble(args[++i]);
			}
			
			if (args[i].equals("-ontoRoot")) {
				rootTerm = (args[++i]);
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
		
		switch(rootTerm){ 
        case "SBO_0000000": 
        	UGroupingUtil.label="SBOOntology";
            break; 
        case "owl:GOOntology": 
    		UGroupingUtil.label="GOOntology";
            break; 
        case "owl:ChebiOntology": 
    		UGroupingUtil.label="ChebiOntology";
            break; 
      
        default: 
        	
        } 


		initStaticGrouping(rootTerm, features, balance);
	}

	public static void initStaticGrouping(String rootTerm, int features, double balance) {
		graphDB = Manager.instance().getDatabase();

		
		

		try(Transaction tx = graphDB.beginTx();) {
			
		if (!UGroupingUtil.getNodebyTermID(rootTerm).hasProperty(
				Property.GroupCalc.COUNTofCONCEPTS)) {
			System.out.println("Computing probabilities...");
			setProbabilities(rootTerm);
			//System.out.println("Probabilities for all nodes computed.");
		} 
		//else {System.out.println("Probabilities checked.");}

		GroupList resultGrouping = new GroupList();
		resultGrouping = getChildrenList(UGroupingUtil
				.getNodebyTermID(rootTerm));
		//System.out.println("Beginning algorithm for static feature selection...");

		//System.out.println("Basis: ");
		//resultGrouping.printGroups();

		staticGrouping(resultGrouping, features, balance).printGroups();
		
		// call exit explicitly in case there a zombi threads
		
		tx.success();
		}
		
		System.exit(0);

	}

	private static GroupList staticGrouping(GroupList list, int features, double balance) {
		list.getNMaxByScore(features);

		if (list.getGroups().size()==features && UGroupingUtil.isBalanced(list,balance)) {
			return list;
		} else {
			Group max = list.getMaxByScore();
			LinkedList<Group> childrenOfMax = getChildrenList(
					UGroupingUtil.getNodebyTermID(max.getTerm())).getGroups();
			list.getGroups().remove(max);
			list.getGroups().addAll(childrenOfMax);
			return staticGrouping(list, features, balance);
		}
	}

	private static GroupList getChildrenList(Node parent) {
		LinkedList<Group> list = new LinkedList<Group>();
		for (Path path : UGroupingUtil.getChildren(parent)) {
			Group gr = new Group(path.endNode()
					.getProperty(Property.Ontology.TermID).toString(),
					Double.parseDouble(path.endNode()
							.getProperty(Property.GroupCalc.P).toString()));
			list.add(gr);
		}
		return new GroupList(list);
	}


	static void setProbabilities(String rootTerm) {
		graphDB = Manager.instance().getDatabase();

		
		Node rootNode = UGroupingUtil.getNodebyTermID(rootTerm);
		setCountOfConcepts(rootNode);
		//System.out.println("Concepts counted for all nodes.");

		double total = Double.parseDouble(rootNode.getProperty(
				Property.GroupCalc.COUNTofCONCEPTS).toString());
		//System.out.println("Total number of concepts: " + total);

		for (Path path : UGroupingUtil.getAllNodesOfOntology(UGroupingUtil.getNodebyTermID(rootTerm))) {
			if (path.endNode().hasProperty(Property.GroupCalc.COUNTofCONCEPTS)) {
				double count = Double.parseDouble(path.endNode().getProperty(
						Property.GroupCalc.COUNTofCONCEPTS, 0).toString());
				double probability = count / total;
				double IC = Math.abs(Math.log(probability));

				try(Transaction tx = graphDB.beginTx();) {
					path.endNode().setProperty(Property.GroupCalc.P, probability);
					path.endNode().setProperty(Property.GroupCalc.IC, IC);
					tx.success();
				}

			}
		}
	}

	private static void setCountOfConcepts(Node parentNode) {
		Traverser tv = UGroupingUtil.getChildren(parentNode);

		if (!tv.iterator().hasNext()) {
			

			try(Transaction tx = graphDB.beginTx();) {
				parentNode.setProperty(Property.GroupCalc.COUNTofCONCEPTS, 1);
				parentNode.setProperty(Property.Ontology.isLeaf, true);
				tx.success();
			}

		} else {
			for (Path nodePath : tv) {
				setCountOfConcepts(nodePath.endNode());
			}

			

			try(Transaction tx = graphDB.beginTx();) {
				int count = 0; //getDecendents contains also the parentNode
			
				for (@SuppressWarnings("unused") Path nodePath : UGroupingUtil.getDescendents(parentNode)) {
					count = count
						+ 1;
			}

				parentNode.setProperty(Property.GroupCalc.COUNTofCONCEPTS,
						count);
				tx.success();
			}
		}
	}
}
