package de.unirostock.sems.masymos.grouping.algorithm;


import java.util.Iterator;
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

public class DynamicGrouping {

	protected static GraphDatabaseService graphDB;
	static String SCORING = Property.GroupCalc.EP;


	public static void main(String[] args) {

		int features = 5;
//		double balance = 0.6;
		String rootTerm = "SBO_0000000"; 
		//String rootTerm = "owl:GOOntology"; 
		//String rootTerm = "owl:ChebiOntology"; 
		String method="4";
		
				
				for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-dbPath")) {
				Config.instance().setDbPath(args[++i]);
			}

			if (args[i].equals("-features")) {
				features = Integer.parseInt(args[++i]);
			}
			
//			if (args[i].equals("-balance")) {
//				balance = Integer.parseInt(args[++i]);
//			}
			
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
			
			if (args[i].equals("-method")) {
				 method = (args[++i]);
			}
			
		}
				
				graphDB = Manager.instance().getDatabase();

				
				switch(rootTerm){ 
		        case "SBO_0000000": 
		        	UGroupingUtil.label="SBOOntology";

		    		try (Transaction tx = graphDB.beginTx()){
			        	UGroupingUtil.getNodebyTermID("owl:SBOOntology").setProperty(Property.GroupCalc.Trissl, "0");
		    			tx.success();
		    		} 
		            break; 
		        case "owl:GOOntology": 
	        		UGroupingUtil.label="GOOntology";
		            break; 
		        case "owl:ChebiOntology": 
	        		UGroupingUtil.label="ChebiOntology";
		            break; 
		      
		        default: 
		        	
		        } 

		try(Transaction tx = graphDB.beginTx()) {
			
			 StaticGrouping.setProbabilities(rootTerm);
			 initDynamicGrouping(rootTerm);
			
			 switch (Integer.valueOf(method)) {
				case 1:
					;
					break;
				case 2:
					initTopDownGrouping(rootTerm, features);
					;
					break;
				case 3:
					StaticGrouping.initStaticGrouping(rootTerm, features,UGroupingUtil.balanceValue);
					DynamicGrouping.initBottomUpGrouping(rootTerm, features);
					;
					break;
				case 4:
					initTrisslGrouping(rootTerm, features);
					;
					break;

				default:
					;
				}
			 
			 
			 //initTopDownGrouping(rootTerm, features);
			 //initBottomUpGrouping(rootTerm, features);
			 //initTrisslGrouping(rootTerm, features);
			 
			tx.success();
		}
		
		 System.exit(0);
	}

	public static void initDynamicGrouping(String rootTerm) {
		System.out.println("Computing probabilities...");
		setDynamicProperties(rootTerm);
	}

	public static void initTopDownGrouping(String rootTerm, int features) {
		GroupList resultGrouping = new GroupList(null);
		//resultGrouping = initListOfChildren(getOnlyChild(UGroupingUtil.getNodebyTermID(rootTerm)));
		resultGrouping = initListOfChildren(UGroupingUtil.getNodebyTermID(rootTerm));
		
		System.out
				.println("Beginning algorithm for dynamic top-down feature selection...");

		topDownGrouping(resultGrouping, features).printGroups();
	}

	private static GroupList topDownGrouping(GroupList list, int features) {
		list.makeDisjunct(); 
		list.getNMaxByScore(features);
		
	if ((list.getGroups().size()==features) && (UGroupingUtil.isBalanced(list))) {  //
			return list;
		} else {
			Group max = list.getMaxByScore();
			LinkedList<Group> childrenOfMax = initListOfChildren(
					UGroupingUtil.getNodebyTermID(max.getTerm())).getGroups();
			list.getGroups().remove(max);
			list.getGroups().addAll(childrenOfMax);
			return topDownGrouping(list, features);
		}
	}

	public static void initBottomUpGrouping(String rootTerm, int features) {
		GroupList resultGrouping = new GroupList(null);
		resultGrouping = initListOfReferencedNodes(
				UGroupingUtil.getNodebyTermID(rootTerm), SCORING);
		System.out
				.println("Beginning algorithm for dynamic bottom-up feature selection...");
	
		bottomUpGrouping(resultGrouping, features, resultGrouping.getMaxDepth()).printGroups();

		// call exit explicitly in case there a zombi threads
		System.exit(0);
	}

	private static GroupList bottomUpGrouping(GroupList list, int features, int depth) {
				
		if (list.getGroups().size()==features) {
			list.makeDisjunct();
			return topDownGrouping(list,features);
		}
		if (list.getGroups().size()<features) {
			list.makeDisjunct();
			return topDownGrouping(list,features);
		}
		if (depth==1){
			 list.getNMaxByScore(features);
			list.makeDisjunct();
			return topDownGrouping(list,features);
		}
		
		//get all Nodes of depth
		GroupList nodesOfDepth = list.getNodesOfDepth(depth);
		//merge to parent
		for (Group gr : nodesOfDepth.getGroups()){
			if (list.getGroups().size()>features){
				mergeToParent(list,UGroupingUtil.getNodebyTermID(gr.getTerm()));
			}
		}
		return bottomUpGrouping(list,features,depth-1);
	}

	private static void mergeToParent(GroupList list, Node parent) {

		double parentScore = Double.parseDouble(parent.getProperty(SCORING).toString());
		//remove children from list
		for (Path child : UGroupingUtil.getChildren(parent)){
			double childScore = Double.parseDouble(child.endNode().getProperty(SCORING).toString());
			
			if (childScore==0||childScore<parentScore){
				for (Path descendent : UGroupingUtil.getDescendents(parent))
				{
					Group childGr = new Group (descendent.endNode().getProperty(Property.Ontology.TermID).toString(), Double.parseDouble(descendent.endNode().getProperty(SCORING).toString()));
					list.getGroups().remove(childGr);
				}
			}
			else if (childScore==parentScore){
				Group parentGr = new Group(parent.getProperty(Property.Ontology.TermID).toString(),Double.parseDouble(parent.getProperty(SCORING).toString()));
				list.getGroups().remove(parentGr);
			}
		}
		
	}
	
	public static void initTrisslGrouping(String rootTerm, int features) {
		GroupList resultGrouping = new GroupList(null);

		// init GroupList of Nodes
		resultGrouping = initListOfReferencedNodes(
				UGroupingUtil.getNodebyTermID(rootTerm),
				Property.GroupCalc.Trissl);
		
		System.out
				.println("Beginning algorithm for dynamic feature selection inspired by Trissl ...");

		
		// For all nodes the ancestor with highest trissl score is chosen
		for (Iterator<Group> it = resultGrouping.getGroups().iterator(); it
				.hasNext();) {
			Group gr = it.next();
			Traverser tv = UGroupingUtil.getAncestors(UGroupingUtil
					.getNodebyTermID(gr.getTerm()));
			Group max = UGroupingUtil.getGroupOfMaxScore(tv,
					Property.GroupCalc.Trissl);
			gr.setScore(max.getScore());
			gr.setTerm(max.getTerm());
		}

		// sort and eliminate duplicates
		resultGrouping.eliminateDuplicates();
		resultGrouping.eliminateNonused();

		trisslGrouping(resultGrouping, features).printGroups();

		// call exit explicitly in case there a zombi threads
		System.exit(0);
	}

	/*private static GroupList DEPRECATEDtrisslGrouping(GroupList list, int features) {
		list.sortGroupsByScore();
		list.printGroups();
		GroupList all = new GroupList((LinkedList<Group>) list.getGroups()
				.clone());
		list.getNMaxByScore(features);

		int disjunct = list.makeDisjunct();
		if (disjunct == 0) {
			return list;
		} else {
			for (int i = 0; i < list.getGroups().size() - disjunct; i++) {
				all.getGroups().remove();
			}

			all.getGroups().addAll(list.getGroups());
			all.sortGroupsByScore();
			System.out.println("Neue Basis, sortiert");
			all.printGroups();
			return trisslGrouping(all, features);
		}
	}*/

	private static GroupList trisslGrouping(GroupList list, int features) {
		GroupList results = new GroupList (new LinkedList<Group>());

		do{
			results.getGroups().add(list.getMaxByScore());
			list.getGroups().remove(list.getMaxByScore());
			results.makeDisjunct();
		}while ((results.getGroups().size()<features) && list.getGroups().size()>0);
		return results;
	}

	
/*
	private static Node getOnlyChild(Node parent) {
		LinkedList<Group> list = new LinkedList<Group>();
		Node realRoot = null;
		
		for (Path path : UGroupingUtil.getChildren(parent)){
			realRoot = path.endNode();
		}
		return realRoot;
	}
*/
	
	private static GroupList initListOfChildren(Node parent) {
		LinkedList<Group> list = new LinkedList<Group>();
		
		
		for (Path path : UGroupingUtil.getChildren(parent)) {
			if (Double.parseDouble(path.endNode()
							.getProperty(SCORING).toString())>0){
				Group gr = new Group(path.endNode()
						.getProperty(Property.Ontology.TermID).toString(),
						Double.parseDouble(path.endNode()
								.getProperty(SCORING).toString()));
				list.add(gr);
			}
		}
		return new GroupList(list);
	}
/*
	private static GroupList initListOfLeafnodes(Node rootNode, String scoreProperty) {

		LinkedList<Group> list = new LinkedList<Group>();
		for (Node node : UGroupingUtil.getAllLeafnodesOfOntology(rootNode)) {

			Group gr = new Group(node.getProperty(Property.Ontology.TermID)
					.toString(), Double.parseDouble(node.getProperty(
					scoreProperty).toString()));
			list.add(gr);
		}
		return new GroupList(list);
	}
*/
	private static GroupList initListOfReferencedNodes(Node rootNode, String scoreProperty) {

		LinkedList<Group> list = new LinkedList<Group>();
		for (Path nodePath : UGroupingUtil.getAllNodesOfOntology(rootNode)) {

			Double score = Double.parseDouble(nodePath.endNode().getProperty(
					scoreProperty).toString());
			
			if (score>0) {
				Group gr = new Group(nodePath.endNode().getProperty(Property.Ontology.TermID)
						.toString(), Double.parseDouble(nodePath.endNode().getProperty(
						scoreProperty).toString()));
				list.add(gr);
			}
		}
		return new GroupList(list);
	}
	
	public static void setDynamicProperties(String rootTerm) {
		Node rootNode = UGroupingUtil.getNodebyTermID(rootTerm);
		
		//UGroupingUtil.aggregateByProperty(rootNode, Property.GroupCalc.df, Property.GroupCalc.DF, false);
		//UGroupingUtil.normalizeByProperty(rootNode, Property.GroupCalc.DF, Property.GroupCalc.DP);

		UGroupingUtil.aggregateByProperty(rootNode, Property.GroupCalc.ef, Property.GroupCalc.EF, true);
		UGroupingUtil.normalizeByProperty(rootNode, Property.GroupCalc.EF, Property.GroupCalc.EP);


	}
}
