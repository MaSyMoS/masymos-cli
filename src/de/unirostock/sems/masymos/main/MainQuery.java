package de.unirostock.sems.masymos.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import de.unirostock.sems.masymos.query.structure.StructureQuery;
import de.unirostock.sems.masymos.query.types.AnnotationQuery;
import de.unirostock.sems.masymos.query.types.CellMLModelQuery;
import de.unirostock.sems.masymos.query.types.PersonQuery;
import de.unirostock.sems.masymos.query.types.PublicationQuery;
import de.unirostock.sems.masymos.query.types.SBMLModelQuery;
import de.unirostock.sems.masymos.query.types.SedmlQuery;
import de.unirostock.sems.masymos.util.ModelResultSetWriter;
import de.unirostock.sems.masymos.util.ResultSetUtil;
import de.unirostock.sems.masymos.util.SBGN2Cypher;

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
							+ "(9) all, (10) cypher, (11) sedml, (12) sbgn: ");
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
					allInterfaceQuery(); break;	
				case 10:
					structureQuery(); break;
				case 11:
					sedmlNativeQuery(); break;	
				case 12:
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
			List<ModelResultSet> results = null;
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
			printModelResults(results);
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
			List<ModelResultSet> results = null;
			try {
				PersonQuery pq = new PersonQuery();
				pq.addQueryClause(PersonFieldEnumerator.NONE, s);
				List<IQueryInterface> qL = new LinkedList<IQueryInterface>();
				qL.add(pq);
				results = QueryAdapter.executeMultipleQueriesForModels(qL);
			} catch (Exception e) {
				e.printStackTrace();
			}
			printModelResults(results);
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
			List<ModelResultSet> results = null;
			try {
				SBMLModelQuery pq = new SBMLModelQuery();
				pq.addQueryClause(SBMLModelFieldEnumerator.NONE, s);
				List<IQueryInterface> qL = new LinkedList<IQueryInterface>();
				qL.add(pq);
				results = QueryAdapter.executeMultipleQueriesForModels(qL);
			} catch (Exception e) {
				e.printStackTrace();
			}
			printModelResults(results);
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
			List<ModelResultSet> results = null;
			try {
				CellMLModelQuery pq = new CellMLModelQuery();
				pq.addQueryClause(CellMLModelFieldEnumerator.NONE, s);
				List<IQueryInterface> qL = new LinkedList<IQueryInterface>();
				qL.add(pq);
				results = QueryAdapter.executeMultipleQueriesForModels(qL);
			} catch (Exception e) {
				e.printStackTrace();
			}
			printModelResults(results);
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
			List<ModelResultSet> results = null;
			try {
				PublicationQuery pq = new PublicationQuery();
				pq.addQueryClause(PublicationFieldEnumerator.NONE, s);
				List<IQueryInterface> qL = new LinkedList<IQueryInterface>();
				qL.add(pq);
				results = QueryAdapter.executeMultipleQueriesForModels(qL);
			} catch (Exception e) {
				e.printStackTrace();
			}
			printModelResults(results);
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
	
	
	private static void allInterfaceQuery() {
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
			List<ModelResultSet> results = null;
			try {
				AnnotationQuery aq = new AnnotationQuery();
				aq.setBestN(20);
				//aq.setThreshold(0.01f);
				aq.addQueryClause(AnnotationFieldEnumerator.RESOURCETEXT, s);
				
				PersonQuery ppq = new PersonQuery();
				ppq.addQueryClause(PersonFieldEnumerator.NONE, s);
				
				SBMLModelQuery sq = new SBMLModelQuery();
				sq.addQueryClause(SBMLModelFieldEnumerator.NONE, s);
				
				CellMLModelQuery cq = new CellMLModelQuery();
				cq.addQueryClause(CellMLModelFieldEnumerator.NONE, s);

				
				PublicationQuery pq = new PublicationQuery();
				pq.addQueryClause(PublicationFieldEnumerator.NONE, s);
				List<IQueryInterface> qL = new LinkedList<IQueryInterface>();
				
				qL.add(pq);
				qL.add(aq);
				qL.add(ppq);
				qL.add(sq);
				qL.add(cq);
					
				
				results = QueryAdapter.executeMultipleQueriesForModels(qL);
				if (!StringUtils.isEmpty(dumpPath)) ModelResultSetWriter.writeModelResults(results, qL, dumpPath);
				results = ResultSetUtil.collateModelResultSetByModelId(results);
				//results = ResultSetUtil.sortModelResultSetByScore(results);
				
				//change
				results = RankAggregation.aggregate(null, null, RankAggregationType.Types.ADJACENT_PAIRS);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			printModelResults(results);
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

	
	private static void printModelResults(List<ModelResultSet> results){
		if ((results != null) && (results.size() > 0)) {

			System.out.println("Found " + results.size() + " results");
			for (Iterator<ModelResultSet> iterator = results.iterator(); iterator
					.hasNext();) {
				ModelResultSet resultSet = (ModelResultSet) iterator.next();
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
