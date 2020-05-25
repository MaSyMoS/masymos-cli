package de.unirostock.sems.masymos.main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.unirostock.sems.masymos.configuration.Config;
import de.unirostock.sems.masymos.configuration.RankAggregationType;
import de.unirostock.sems.masymos.data.PersonWrapper;
import de.unirostock.sems.masymos.database.Manager;
import de.unirostock.sems.masymos.query.IQueryInterface;
import de.unirostock.sems.masymos.query.QueryAdapter;
import de.unirostock.sems.masymos.query.aggregation.GroupVersions;
import de.unirostock.sems.masymos.query.aggregation.RankAggregation;
import de.unirostock.sems.masymos.query.enumerator.AnnotationFieldEnumerator;
import de.unirostock.sems.masymos.query.enumerator.CellMLModelFieldEnumerator;
import de.unirostock.sems.masymos.query.enumerator.PersonFieldEnumerator;
import de.unirostock.sems.masymos.query.enumerator.PublicationFieldEnumerator;
import de.unirostock.sems.masymos.query.enumerator.SBMLModelFieldEnumerator;
import de.unirostock.sems.masymos.query.enumerator.SedmlFieldEnumerator;
import de.unirostock.sems.masymos.query.results.AnnotationResultSet;
import de.unirostock.sems.masymos.query.results.ModelResultSet;
import de.unirostock.sems.masymos.query.results.PersonResultSet;
import de.unirostock.sems.masymos.query.results.PublicationResultSet;
import de.unirostock.sems.masymos.query.results.SedmlResultSet;
import de.unirostock.sems.masymos.query.results.VersionResultSet;
import de.unirostock.sems.masymos.query.structure.StructureQuery;
import de.unirostock.sems.masymos.query.types.AnnotationQuery;
import de.unirostock.sems.masymos.query.types.CellMLModelQuery;
import de.unirostock.sems.masymos.query.types.PersonQuery;
import de.unirostock.sems.masymos.query.types.PublicationQuery;
import de.unirostock.sems.masymos.query.types.SBMLModelQuery;
import de.unirostock.sems.masymos.query.types.SedmlQuery;
import de.unirostock.sems.masymos.util.ModelResultSetWriter;
import de.unirostock.sems.masymos.util.RankAggregationUtil;
import de.unirostock.sems.masymos.util.ResultSetUtil;
import de.unirostock.sems.masymos.util.SBGN2Cypher;

/**
*
* Copyright 2016 Ron Henkel (GPL v3)
* @author ronhenkel
*/
public class MainQuery {
	
	private static String dumpPath = null;

	public static void main(String[] args) {

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-dbPath")) {
				Config.instance().setDbPath(args[++i]);
			} 
			else if (args[i].equals("-dumpPath")) {
				dumpPath = args[++i];
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
			System.out
					.println("QueryType: (1) sbmlmodeliq, (2) cellmlmodeliq, (3) annoiq->model, (4) persiq->model, "
							+ "(5) pubiq->model, (6) annoiq, (7) persiq, (8) pubiq, "
							+ "(9) all, (10) all aggregated, (11) cypher, (12) sedml, (13) sbgn: ");
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
					sbmlModelInterfaceQuery(); break;
				case 2:
					cellmlModelInterfaceQuery(); break;	
				case 3: 
					annoToModelInterfaceQuery(); break;
				case 4:
					personToModelInterfaceQuery(); break;
				case 5:
					publicationToModelInterfaceQuery(); break;
				case 6: 
					annoNativeInterfaceQuery(); break;
				case 7:
					personNativeInterfaceQuery(); break;
				case 8:
					publicationNativeInterfaceQuery(); break;					
				case 9:
					allInterfaceQuery(RankAggregationType.Types.DEFAULT); break;	
				case 10:
					
					int n = 5;
					while (n != 0){
						System.out.println("Enter:");
						System.out.println("0 to exit");
						System.out.println("1 for Adjacent pairs");
						System.out.println("2 for CombMNZ");
						System.out.println("3 for Local Kemenization");
						System.out.println("4 for Supervised Local Kemenization");
						System.out.println("5 for Default");
						BufferedReader in = new BufferedReader(new InputStreamReader(
								System.in));
						try {
							in = new BufferedReader(new InputStreamReader(
									System.in));
							n = Integer.parseInt(in.readLine());
							if(n == 0)
								break;
							if(n == 1)
								allInterfaceQuery(RankAggregationType.Types.ADJACENT_PAIRS); 
							else if (n == 2)
								allInterfaceQuery(RankAggregationType.Types.COMB_MNZ);
							else if (n == 3)
								allInterfaceQuery(RankAggregationType.Types.LOCAL_KEMENIZATION);
							else if (n == 4)
								allInterfaceQuery(RankAggregationType.Types.SUPERVISED_LOCAL_KEMENIZATION);
							else if (n == 5)
								allInterfaceQuery(RankAggregationType.Types.DEFAULT);

						} catch (IOException ex) {
							System.out.println(ex.getMessage());
						}
					
					}
					break;
				case 11:
					structureQuery(); break;
				case 12:
					sedmlNativeQuery(); break;	
				case 13:
					sbgnQuery(); break;		
			default:
				continue;
			}
		}
		System.exit(0);
	}

	

	private static void sbgnQuery() {
		
		String s = "";
		while (!s.equals("exit")) {
			System.out.println("Path to SBGN file: ");
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
			try {
				System.out.println(SBGN2Cypher.toCypher(s));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}


	private static void annoToModelInterfaceQuery() {
		String s = "";
		while (!s.equals("exit")) {
			System.out.println("Query: ");
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
			System.out.println("Retrieving from anno interface...");
			List<VersionResultSet> results = null;
			try {
				AnnotationQuery aq = new AnnotationQuery();
				aq.setBestN(20);
				aq.setThreshold(0.01f);
				aq.addQueryClause(AnnotationFieldEnumerator.RESOURCETEXT, s);
				List<IQueryInterface> aqL = new LinkedList<IQueryInterface>();
				aqL.add(aq);
				results = QueryAdapter.executeMultipleQueriesForModels(aqL);
			} catch (Exception e) {
				e.printStackTrace();
			}
			printVersionResults(results);
			System.out.println("done");
		}

	}
	
	private static void annoNativeInterfaceQuery() {
		String s = "";
		while (!s.equals("exit")) {
			System.out.println("Query: ");
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
			System.out.println("Retrieving from anno interface...");
			List<AnnotationResultSet> results = null;
			try {
				AnnotationQuery aq = new AnnotationQuery();
				aq.setBestN(20);
				aq.setThreshold(0.01f);
				aq.addQueryClause(AnnotationFieldEnumerator.RESOURCETEXT, s);
				//List<IQueryInterface> aqL = new LinkedList<IQueryInterface>();
				//aqL.add(aq);
				results = QueryAdapter.executeAnnotationQuery(aq);
			} catch (Exception e) {
				e.printStackTrace();
			}
			printAnnotationResults(results);
			System.out.println("done");
		}

	}
	
	private static void personToModelInterfaceQuery() {
		String s = "";
		while (!s.equals("exit")) {
			System.out.println("Query: ");
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
			System.out.println("Retrieving from person interface...");
			List<VersionResultSet> results = null;
			try {
				PersonQuery pq = new PersonQuery();
				pq.addQueryClause(PersonFieldEnumerator.NONE, s);
				List<IQueryInterface> qL = new LinkedList<IQueryInterface>();
				qL.add(pq);
				results = QueryAdapter.executeMultipleQueriesForModels(qL);
			} catch (Exception e) {
				e.printStackTrace();
			}
			printVersionResults(results);
			System.out.println("done");
		}	
	}

	private static void personNativeInterfaceQuery() {
		String s = "";
		while (!s.equals("exit")) {
			System.out.println("Query: ");
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
			System.out.println("Retrieving from person interface...");
			List<PersonResultSet> results = null;
			try {
				PersonQuery pq = new PersonQuery();
				pq.addQueryClause(PersonFieldEnumerator.NONE, s);
				results = QueryAdapter.executePersonQuery(pq);
			} catch (Exception e) {
				e.printStackTrace();
			}
			printPersonResults(results);
			System.out.println("done");
		}	
	}

	
	private static void sbmlModelInterfaceQuery() {
		String s = "";
		while (!s.equals("exit")) {
			System.out.println("Query: ");
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
			System.out.println("Retrieving from SBML model interface...");
			List<VersionResultSet> results = null;
			try {
				SBMLModelQuery pq = new SBMLModelQuery();
				pq.addQueryClause(SBMLModelFieldEnumerator.NONE, s);
				List<IQueryInterface> qL = new LinkedList<IQueryInterface>();
				qL.add(pq);
				results = QueryAdapter.executeMultipleQueriesForModels(qL);
			} catch (Exception e) {
				e.printStackTrace();
			}
			printVersionResults(results);
			System.out.println("done");
		}

	}
	
	private static void cellmlModelInterfaceQuery() {
		String s = "";
		while (!s.equals("exit")) {
			System.out.println("Query: ");
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
			System.out.println("Retrieving from CellML model interface...");
			List<VersionResultSet> results = null;
			try {
				CellMLModelQuery pq = new CellMLModelQuery();
				pq.addQueryClause(CellMLModelFieldEnumerator.NONE, s);
				List<IQueryInterface> qL = new LinkedList<IQueryInterface>();
				qL.add(pq);
				results = QueryAdapter.executeMultipleQueriesForModels(qL);
			} catch (Exception e) {
				e.printStackTrace();
			}
			printVersionResults(results);
			System.out.println("done");
		}
		
		
	}
	
	private static void publicationToModelInterfaceQuery() {
		String s = "";
		while (!s.equals("exit")) {
			System.out.println("Query: ");
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
			System.out.println("Retrieving from publication interface...");
			List<VersionResultSet> results = null;
			try {
				PublicationQuery pq = new PublicationQuery();
				pq.addQueryClause(PublicationFieldEnumerator.NONE, s);
				List<IQueryInterface> qL = new LinkedList<IQueryInterface>();
				qL.add(pq);
				results = QueryAdapter.executeMultipleQueriesForModels(qL);
			} catch (Exception e) {
				e.printStackTrace();
			}
			printVersionResults(results);
			System.out.println("done");
		}

	}
	
	private static void publicationNativeInterfaceQuery() {
		String s = "";
		while (!s.equals("exit")) {
			System.out.println("Query: ");
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
			System.out.println("Retrieving from publication interface...");
			List<PublicationResultSet> results = null;
			try {
				PublicationQuery pq = new PublicationQuery();
				pq.addQueryClause(PublicationFieldEnumerator.NONE, s);
				results = QueryAdapter.executePublicationQuery(pq);
			} catch (Exception e) {
				e.printStackTrace();
			}
			printPublicationResults(results);
			System.out.println("done");
		}

	}
	
	
	private static void allInterfaceQuery(RankAggregationType.Types type) {
		String s = "";
		while (!s.equals("exit")) {
			System.out.println("Query: ");
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
			System.out.println("Retrieving from all interfaces...");
			List<VersionResultSet> results = null;
			List<ModelResultSet> groupedResults = new LinkedList<ModelResultSet>();
			
			try {
//				AnnotationQuery aq = new AnnotationQuery();
//				aq.setBestN(20);
//				//aq.setThreshold(0.01f);
//				aq.addQueryClause(AnnotationFieldEnumerator.RESOURCETEXT, s);
//				
//				PersonQuery ppq = new PersonQuery();
//				ppq.addQueryClause(PersonFieldEnumerator.NONE, s);
//				
//				SBMLModelQuery sq = new SBMLModelQuery();
//				sq.addQueryClause(SBMLModelFieldEnumerator.NONE, s);
//				
//				CellMLModelQuery cq = new CellMLModelQuery();
//				cq.addQueryClause(CellMLModelFieldEnumerator.NONE, s);
//
//				
//				PublicationQuery pq = new PublicationQuery();
//				pq.addQueryClause(PublicationFieldEnumerator.NONE, s);
//				List<IQueryInterface> qL = new LinkedList<IQueryInterface>();
//				
//				qL.add(pq);
//				qL.add(aq);
//				qL.add(ppq);
//				qL.add(sq);
//				qL.add(cq);
				
				
		    	List<VersionResultSet> initialAggregateRanker = null;
		    	
		       	CellMLModelQuery cq = new CellMLModelQuery();
		    	cq.addQueryClause(CellMLModelFieldEnumerator.NONE, s);
		    	SBMLModelQuery sq = new SBMLModelQuery();
		    	sq.addQueryClause(SBMLModelFieldEnumerator.NONE, s);
		    	PersonQuery persq = new PersonQuery();
		    	persq.addQueryClause(PersonFieldEnumerator.NONE, s);
		    	PublicationQuery pubq = new PublicationQuery();
		    	pubq.addQueryClause(PublicationFieldEnumerator.NONE, s);
		    	AnnotationQuery aq = new AnnotationQuery();
		    	//aq.setBestN(20);
		    	aq.addQueryClause(AnnotationFieldEnumerator.NONE, s);
		    	
		    	
		    	List<IQueryInterface> qL = new LinkedList<IQueryInterface>();
		    	qL.add(sq);
				qL.add(cq);
				qL.add(persq);
				qL.add(pubq);
				qL.add(aq);
				
				int rankersWeights = 0;
				
				results = QueryAdapter.executeMultipleQueriesForModels(qL);
				if (!StringUtils.isEmpty(dumpPath)) ModelResultSetWriter.writeModelResults(results, qL, dumpPath);
				initialAggregateRanker = ResultSetUtil.collateModelResultSetByModelId(results);
				List<List<VersionResultSet>> splitResults = RankAggregationUtil.splitModelResultSetByIndex(results);
				
				if(type == RankAggregationType.Types.SUPERVISED_LOCAL_KEMENIZATION){
					System.out.println("Model Ranker weight:");
					BufferedReader in = new BufferedReader(new InputStreamReader(
							System.in));
					int weight = Integer.parseInt(in.readLine());
					rankersWeights+= weight;
					
					System.out.println("Annotation Ranker weight:");
					in = new BufferedReader(new InputStreamReader(
							System.in));
					weight = Integer.parseInt(in.readLine());
					rankersWeights+= weight * 100;
					
					System.out.println("Person Ranker weight:");
					in = new BufferedReader(new InputStreamReader(
							System.in));
					weight = Integer.parseInt(in.readLine());
					rankersWeights+= weight * 10000;
					
					System.out.println("Publication Ranker weight:");
					in = new BufferedReader(new InputStreamReader(
							System.in));
					weight = Integer.parseInt(in.readLine());
					rankersWeights+= weight * 1000000;
				}
					
				results = RankAggregation.aggregate(splitResults, initialAggregateRanker, type, rankersWeights);
				
				//group results by version
				groupedResults = GroupVersions.groupVersions(results);

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//OUTPUT
			printModelResultsToFile(groupedResults, s, type.toString());
			printVersionResultsToFile(results, s, type.toString());
		}
			System.out.println("done");


	}
	
	private static void sedmlNativeQuery() {
		String s = "";
		while (!s.equals("exit")) {
			System.out.println("Query: ");
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
			System.out.println("Retrieving from sedml interface...");
			List<SedmlResultSet> results = null;
			try {
				SedmlQuery pq = new SedmlQuery();
				pq.addQueryClause(SedmlFieldEnumerator.NONE, s);
				List<IQueryInterface> qL = new LinkedList<IQueryInterface>();
				
				qL.add(pq);
				results = QueryAdapter.executeSedmlQuery(pq);
				//results = ResultSetUtil.collateModelResultSetByModelId(results);
				//results = ResultSetUtil.sortModelResultSetByScore(results);
			} catch (Exception e) {
				e.printStackTrace();
			}
			printSedmlResults(results);
		}
		
	}
	
	
	private static void structureQuery() {
		String s = "";
		while (!s.equals("exit")) {
			System.out.println("Query: ");
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
			System.out.println("Retrieving from all interfaces...");
			StructureQuery.runCypherQuery(s);
			System.out.println("===============================================");
		}
	}

	
	private static void printVersionResultsToFile(List<VersionResultSet> results, String query, String raType){
		try {
			StringBuilder sb = new StringBuilder(query);
			sb.append(raType);
			sb.append("versions.txt");
			String fileName = sb.toString();
			fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
			PrintWriter writer;
			writer = new PrintWriter(fileName, "UTF-8");
			if ((results != null) && (results.size() > 0)) {
				writer.println("Found " + results.size() + " results");
				for (Iterator<VersionResultSet> iterator = results.iterator(); iterator
						.hasNext();) {
					VersionResultSet resultSet = (VersionResultSet) iterator.next();
					writer
							.println("===============================================");
					writer.println(resultSet.getScore());
					writer.println(resultSet.getModelName());
					writer.println(resultSet.getModelId());
					writer.println(resultSet.getVersionId());
					writer.println(resultSet.getFilename());
					writer.println(resultSet.getDocumentURI());
				}
			} else
				System.out.print("No results!");
			System.out.println();
			System.out
					.println("||||||||||||||||||||||||||||||||||||||||||||||||");
			System.out.println();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
	private static void printVersionResults(List<VersionResultSet> results){
		if ((results != null) && (results.size() > 0)) {
			System.out.println("Found " + results.size() + " results");
			for (Iterator<VersionResultSet> iterator = results.iterator(); iterator
					.hasNext();) {
				VersionResultSet resultSet = (VersionResultSet) iterator.next();
				System.out
						.println("===============================================");
				System.out.println(resultSet.getScore());
				System.out.println(resultSet.getModelName());
				System.out.println(resultSet.getModelId());
				System.out.println(resultSet.getVersionId());
				System.out.println(resultSet.getFilename());
				System.out.println(resultSet.getDocumentURI());
			}
		} else
			System.out.print("No results!");
		System.out.println();
		System.out
				.println("||||||||||||||||||||||||||||||||||||||||||||||||");
		System.out.println();
	}
	
	private static void printModelResults(List<ModelResultSet> modelResults){
		if ((modelResults != null) && (modelResults.size() > 0)) {

			System.out.println("Found " + modelResults.size() + " results");
			for (Iterator<ModelResultSet> modelIterator = modelResults.iterator(); modelIterator
					.hasNext();) {
				ModelResultSet modelResultSet = (ModelResultSet) modelIterator.next();
				System.out
						.println("===============================================");
				System.out.println(modelResultSet.getScore());
				System.out.println(modelResultSet.getModelName());
				System.out.println(modelResultSet.getModelId());
				System.out.println(modelResultSet.getFilename());
				System.out.println(modelResultSet.getDocumentURI());
				List<VersionResultSet> versionsResults = modelResultSet.getVersions();
				System.out.println("Model versions:");
				for (Iterator<VersionResultSet> versionsIterator = versionsResults.iterator(); versionsIterator
						.hasNext();) {
					VersionResultSet versionsResultSet = (VersionResultSet) versionsIterator.next();
					System.out
							.println("---------------------------------------------");
					System.out.println(versionsResultSet.getScore());
					System.out.println(versionsResultSet.getModelName());
					System.out.println(versionsResultSet.getModelId());
					System.out.println(versionsResultSet.getVersionId());
					System.out.println(versionsResultSet.getFilename());
					System.out.println(versionsResultSet.getDocumentURI());
				}
				//printVersionResults(resultSet.getVersions()); 
			}

		} else
			System.out.print("No results!");
		System.out.println();
		System.out
				.println("||||||||||||||||||||||||||||||||||||||||||||||||");
		System.out.println();
	}
	
	
	private static void printModelResultsToFile(List<ModelResultSet> modelResults, String query, String raType){
		PrintWriter writer;
		try {
			StringBuilder sb = new StringBuilder(query);
			sb.append(raType);
			sb.append("models.txt");
			String fileName = sb.toString();
			fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");

			writer = new PrintWriter(fileName, "UTF-8");
			if ((modelResults != null) && (modelResults.size() > 0)) {

				writer.println("Found " + modelResults.size() + " results");
				for (Iterator<ModelResultSet> modelIterator = modelResults.iterator(); modelIterator
						.hasNext();) {
					ModelResultSet modelResultSet = (ModelResultSet) modelIterator.next();
					writer
							.println("===============================================");
					writer.println(modelResultSet.getScore());
					writer.println(modelResultSet.getModelName());
					writer.println(modelResultSet.getModelId());
					writer.println(modelResultSet.getFilename());
					writer.println(modelResultSet.getDocumentURI());
					List<VersionResultSet> versionsResults = modelResultSet.getVersions();
					writer.println("Model versions:");
					for (Iterator<VersionResultSet> versionsIterator = versionsResults.iterator(); versionsIterator
							.hasNext();) {
						VersionResultSet versionsResultSet = (VersionResultSet) versionsIterator.next();
						writer
								.println("---------------------------------------------");
						writer.println(versionsResultSet.getScore());
						writer.println(versionsResultSet.getModelName());
						writer.println(versionsResultSet.getModelId());
						writer.println(versionsResultSet.getVersionId());
						writer.println(versionsResultSet.getFilename());
						writer.println(versionsResultSet.getDocumentURI());
						writer.println(versionsResultSet.getFileId());
					}
					//printVersionResults(resultSet.getVersions()); 
				}

			} else
				writer.print("No results!");
			writer.println();
			writer
					.println("||||||||||||||||||||||||||||||||||||||||||||||||");
			writer.println();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private static void printSedmlResults(List<SedmlResultSet> results){
		if ((results != null) && (results.size() > 0)) {

			System.out.println("Found " + results.size() + " results");
			for (Iterator<SedmlResultSet> iterator = results.iterator(); iterator
					.hasNext();) {
				SedmlResultSet resultSet = (SedmlResultSet) iterator.next();
				System.out
						.println("===============================================");
				System.out.println(resultSet.getScore());
				System.out.println(resultSet.getVersionId());
				System.out.println(resultSet.getFilename());
				System.out.println(resultSet.getFilepath());
				List<String> lmr = resultSet.getModelreferences();
				System.out.println("Model references:");
				for (Iterator<String> iterator2 = lmr.iterator(); iterator2.hasNext();) {
					String mr = (String) iterator2.next();
					System.out.println(mr);
				}
				System.out.println();
			}

		} else
			System.out.print("No results!");
		System.out.println();
		System.out
				.println("||||||||||||||||||||||||||||||||||||||||||||||||");
		System.out.println();
	}
	
	private static void printAnnotationResults(List<AnnotationResultSet> results){
		if ((results != null) && (results.size() > 0)) {

			System.out.println("Found " + results.size() + " results");
			for (Iterator<AnnotationResultSet> iterator = results.iterator(); iterator
					.hasNext();) {
				AnnotationResultSet resultSet = (AnnotationResultSet) iterator.next();
				System.out
						.println("===============================================");
				System.out.println(resultSet.getScore());
				System.out.println(resultSet.getUri());
				System.out.print("Related models: ");
				for (Iterator<String> models = resultSet.getRelatedModelsURI().iterator(); models.hasNext();) {
					String model = (String) models.next();
					System.out.print(model + ", ");
				}
				System.out.println();				
			}

		} else
			System.out.print("No results!");
		System.out.println();
		System.out
				.println("||||||||||||||||||||||||||||||||||||||||||||||||");
		System.out.println();
	}
	
	private static void printPersonResults(List<PersonResultSet> results){
		if ((results != null) && (results.size() > 0)) {

			System.out.println("Found " + results.size() + " results");
			for (Iterator<PersonResultSet> iterator = results.iterator(); iterator
					.hasNext();) {
				PersonResultSet resultSet = (PersonResultSet) iterator.next();
				System.out
						.println("===============================================");
				System.out.println(resultSet.getScore());
				System.out.println(resultSet.getFirstName());
				System.out.println(resultSet.getLastName());
				System.out.println(resultSet.getEmail());
				System.out.print("Related models: ");
				for (Iterator<String> models = resultSet.getRelatedModelsURI().iterator(); models.hasNext();) {
					String model = (String) models.next();
					System.out.print(model + ", ");
				}
				System.out.println();				
			}

		} else
			System.out.print("No results!");
		System.out.println();
		System.out
				.println("||||||||||||||||||||||||||||||||||||||||||||||||");
		System.out.println();
	}
	
	private static void printPublicationResults(List<PublicationResultSet> results){
		if ((results != null) && (results.size() > 0)) {

			System.out.println("Found " + results.size() + " results");
			for (Iterator<PublicationResultSet> iterator = results.iterator(); iterator
					.hasNext();) {
				PublicationResultSet resultSet = (PublicationResultSet) iterator.next();
				System.out
						.println("===============================================");
				System.out.println(resultSet.getScore());
				System.out.println(resultSet.getTitle());
				System.out.println(resultSet.getJounral());
				System.out.println(resultSet.getYear());				
				System.out.println(resultSet.getAffiliation());
				System.out.print("Authors: ");
				for (Iterator<PersonWrapper> persons = resultSet.getAuthors().iterator(); persons.hasNext();) {
					PersonWrapper person = (PersonWrapper) persons.next();
					System.out.print(person.getFirstName() + " " + person.getLastName() + ", ");
				}
				System.out.println();
				System.out.print("Related models: ");
				for (Iterator<String> models = resultSet.getRelatedModelsURI().iterator(); models.hasNext();) {
					String model = (String) models.next();
					System.out.print(model + ", ");
				}
				System.out.println();				
			}

		} else
			System.out.print("No results!");
		System.out.println();
		System.out
				.println("||||||||||||||||||||||||||||||||||||||||||||||||");
		System.out.println();
	}
	
	

}
