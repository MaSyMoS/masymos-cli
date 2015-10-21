package de.unirostock.sems.masymos.grouping;


import java.util.LinkedList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;

import de.unirostock.sems.masymos.configuration.Property;
import de.unirostock.sems.masymos.database.Manager;
import de.unirostock.sems.masymos.database.traverse.DBModelTraverser;

public class UGroupingUtil {

	public static double balanceValue=0.6;
	public static String label="SBOOntology"; //default 
	private static GraphDatabaseService graphDB;
	private static Index<Node> annoFull = Manager.instance().getAnnotationIndex();
	

	public static Node getNodebyTermID(String termID) {
		graphDB = Manager.instance().getDatabase();
	
		return graphDB.findNodes(DynamicLabel.label(label), Property.Ontology.TermID, termID).next();

	}
	
	public static Traverser getAllNodesOfOntology(Node rootNode) {
		TraversalDescription td = graphDB.traversalDescription().breadthFirst()
				.relationships(DynamicRelationshipType.withName("isA"), Direction.INCOMING)
				.evaluator(Evaluators.all());
		Traverser traverser = td.traverse(rootNode);
		return traverser;
	}

	public static LinkedList<Node> getAllLeafnodesOfOntology(Node rootNode) {
		LinkedList<Node> list = new LinkedList<Node>();
		for (Path path : getAllNodesOfOntology(rootNode)) {
			if (path.endNode().hasProperty(Property.Ontology.isLeaf)){
				list.add(path.endNode());
			}
		}
		return list;
	}
	
	public static Traverser getChildren(Node parent) {
		TraversalDescription td = graphDB.traversalDescription().breadthFirst()
				.relationships(DynamicRelationshipType.withName("isA"), Direction.INCOMING)
				.evaluator(Evaluators.atDepth(1));
		Traverser traverser = td.traverse(parent);
		return traverser;
	}
	
	public static LinkedList<Node> checkIfChildrenHaveOtherParents(Node parent) {
		LinkedList<Node> parentsList = new LinkedList<Node>();
		
		for(Path children : getChildren(parent)){
			
			for (Path parents : getParents(children.endNode())){
				if (!parents.endNode().equals(parent)){
					parentsList.add(parents.endNode());
				}
			}
		}
		return parentsList;
	}
	
	
	public static Traverser getParents(Node child) {
		TraversalDescription td = graphDB.traversalDescription().breadthFirst()
				.relationships(DynamicRelationshipType.withName("isA"), Direction.OUTGOING)
				.evaluator(Evaluators.atDepth(1));
		Traverser traverser = td.traverse(child);
		return traverser;
	}

	public static Traverser getAncestors(Node node) {
		TraversalDescription td = graphDB.traversalDescription().breadthFirst()
				.relationships(DynamicRelationshipType.withName("isA"), Direction.OUTGOING)
				.evaluator(Evaluators.all());
		Traverser traverser = td.traverse(node);
		return traverser;
	}
	
	public static Traverser getDescendents(Node node) {
		TraversalDescription td = graphDB.traversalDescription().breadthFirst()
				.relationships(DynamicRelationshipType.withName("isA"), Direction.INCOMING)
				.evaluator(Evaluators.all());
		Traverser traverser = td.traverse(node);
		return traverser;
	}
	
	public static Group getGroupOfMaxScore(Traverser tv, String property){
		double max = 0; 
		Group maxGr = new Group(null,0);
		for (Path nodePath : tv) {
			double score = Double.parseDouble(nodePath.endNode().getProperty(property).toString());
			if (max <= score){
				max = score;
				maxGr.setScore(max);
				maxGr.setTerm((String) nodePath.endNode().getProperty(Property.Ontology.TermID));
			}
		}
		return maxGr;
	}
	
	public static boolean isBalanced(GroupList groups){
		return isBalanced(groups,balanceValue);
	}
	
	public static boolean isBalanced(GroupList groups, double balanceValue) {
		double minScore = groups.getMinByScore().getScore();
		double maxScore = groups.getMaxByScore().getScore();
		
		if (balanceValue < 0 || balanceValue > 1) {
			System.out.println("Balance Value has to be between 0 and 1. Now set to 0.6");
			balanceValue = 0.6;
		}
		
		if (maxScore - minScore > balanceValue) {
			//double minus =maxScore - minScore ;
			return false;
		} else {
			System.out.println("true");

			return true;
		}
	}

	public static String termID2databaseTerm(String termID) {
		String databaseTerm = termID;
		if (databaseTerm.contains("_")) {
			
			databaseTerm = databaseTerm.replaceAll("SBO_","urn:miriam:biomodels.sbo:SBO:");
		}
		return databaseTerm;
	}

	public static void setDocumentProbability(Node rootNode) {
		setDocumentFrequencies(rootNode);
		System.out.println("Document Frequency counted for all nodes.");

		Traverser tv = getAllNodesOfOntology(rootNode);
		double totalDF = Integer.parseInt(rootNode.getProperty(
				Property.GroupCalc.DF).toString());
		for (Path nodePath : tv) {
			double DP = Double.parseDouble(nodePath.endNode()
					.getProperty(Property.GroupCalc.DF).toString())
					/ totalDF;
			

			try(Transaction tx = graphDB.beginTx();) {
				nodePath.endNode().setProperty(Property.GroupCalc.DP, DP);
				tx.success();
			}
		}
		System.out.println("Document Probabilitiy computed for all nodes.");

	}

	public static void setDocumentFrequencies(Node parentNode) {
		Traverser tvChildren = UGroupingUtil.getChildren(parentNode);

		if (!tvChildren.iterator().hasNext()) {

			try(Transaction tx = graphDB.beginTx()) {
				int df = UGroupingUtil.getDocumentFrequency(parentNode
						.getProperty(Property.Ontology.TermID).toString());
				parentNode.setProperty(Property.GroupCalc.df, df);
				parentNode.setProperty(Property.GroupCalc.DF, df);
				
				double trissl = Double.parseDouble(parentNode.getProperty(Property.GroupCalc.IC).toString()) * Double.parseDouble(parentNode.getProperty(Property.GroupCalc.DF).toString());
				parentNode.setProperty(Property.GroupCalc.Trissl, trissl);
				
				tx.success();
			}
		} else {
			for (Path nodePath : tvChildren) {
				setDocumentFrequencies(nodePath.endNode());
			}
		
		try(Transaction tx = graphDB.beginTx()) {
				int df = UGroupingUtil.getDocumentFrequency(parentNode
						.getProperty(Property.Ontology.TermID).toString());
				parentNode.setProperty(Property.GroupCalc.df, df);
				int DF = 0; //getDecendents contains also the parentNode
				for (Path nodePath : UGroupingUtil.getDescendents(parentNode)) {
					DF = DF
							+ Integer.parseInt(nodePath.endNode()
									.getProperty(Property.GroupCalc.df)
									.toString());
				}

				parentNode.setProperty(Property.GroupCalc.DF, DF);
				
				double trissl = Double.parseDouble(parentNode.getProperty(Property.GroupCalc.IC).toString()) * Double.parseDouble(parentNode.getProperty(Property.GroupCalc.DF).toString());
				parentNode.setProperty(Property.GroupCalc.Trissl, trissl);
				
				tx.success();
			}
		}
	}

	public static int getDocumentFrequency(String termID) {
		String searchTerm = termID2databaseTerm(termID);
		Node annoNode = annoFull.get(Property.General.URI, searchTerm)
				.getSingle();
		System.out.print(".");
		return DBModelTraverser.getModelsFromNode(annoNode).size();
	}
	
	public static boolean isAncestor(Node node, Node ancestorNode) {
		boolean isAnc = false;
		Traverser tv = UGroupingUtil.getAncestors(node);
		for (Path path : tv) {
			if (path.endNode().equals(ancestorNode)) {
				isAnc = true;
				System.out.println(ancestorNode.getProperty(Property.Ontology.TermID) +" is ancestor of "+ node.getProperty(Property.Ontology.TermID));
			} 
		}
		return isAnc;
	}
	
	public static void aggregateByProperty(Node parentNode, String byProperty, String aggProperty, boolean Trissl) {
		Traverser tv = UGroupingUtil.getChildren(parentNode);
		
		if(!parentNode.hasProperty(byProperty)){
			parentNode.setProperty(byProperty, 0);
			}
		
		if (!tv.iterator().hasNext()) {
			
			try(Transaction tx = graphDB.beginTx()) {
				parentNode.setProperty(aggProperty, parentNode.getProperty(byProperty));
				
				if(Trissl==true)
				{
					double trissl = Double.parseDouble(parentNode.getProperty(Property.GroupCalc.IC).toString()) * Double.parseDouble(parentNode.getProperty(aggProperty).toString());
					parentNode.setProperty(Property.GroupCalc.Trissl, trissl);

				}
				
				tx.success();
			}

		} else {
			for (Path nodePath : tv) {
				aggregateByProperty(nodePath.endNode(),byProperty,aggProperty, Trissl);
			}

			try(Transaction tx = graphDB.beginTx()) {
				int count = 0; //getDecendents contains also the parentNode
				for (Path nodePath : UGroupingUtil.getDescendents(parentNode)) {
					count = count
							+ Integer
									.parseInt(nodePath
											.endNode()
											.getProperty(byProperty)
											.toString());
				}

				parentNode.setProperty(aggProperty,
						count);
				
				double trissl = Double.parseDouble(parentNode.getProperty(Property.GroupCalc.IC).toString()) * Double.parseDouble(parentNode.getProperty(aggProperty).toString());
				parentNode.setProperty(Property.GroupCalc.Trissl, trissl);
				
				tx.success();
			}
		}
	}
	
	
	public static void normalizeByProperty(Node rootNode, String byProperty, String normProperty) {

		Traverser tv = getAllNodesOfOntology(rootNode);
		double total = Integer.parseInt(rootNode.getProperty(byProperty).toString());
		for (Path nodePath : tv) {
			double norm = Double.parseDouble(nodePath.endNode()
					.getProperty(byProperty).toString())
					/ total;

			try(Transaction tx = graphDB.beginTx()) {
				nodePath.endNode().setProperty(normProperty, norm);
				tx.success();
			}
		}
		System.out.println("Aggregated Property was normalized.");

	}

	
	
	
}
