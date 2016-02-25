package de.unirostock.sems.masymos.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.core.IsInstanceOf;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.ReadableIndex;

import de.binfalse.bfutils.FileRetriever;
import de.unirostock.sems.masymos.annotation.AnnotationResolverUtil;
import de.unirostock.sems.masymos.configuration.Config;
import de.unirostock.sems.masymos.configuration.NodeLabel;
import de.unirostock.sems.masymos.configuration.Property;
import de.unirostock.sems.masymos.database.Manager;
import de.unirostock.sems.masymos.extractor.Extractor;
import de.unirostock.sems.masymos.extractor.CellML.CellMLExtractorThread;
import de.unirostock.sems.masymos.extractor.Owl.Ontology;
import de.unirostock.sems.masymos.extractor.SBML.SBMLExtractor;
import de.unirostock.sems.masymos.extractor.SBML.SBMLExtractorThread;
import de.unirostock.sems.masymos.extractor.SedML.SEDMLExtractor;
import de.unirostock.sems.masymos.extractor.SedML.SEDMLExtractorThread;


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
		Map<String, ReadableIndex<?>> indexMap = Manager.instance().getIndexMap();
		try (Transaction tx = Manager.instance().createNewTransaction())
		{
			for (Iterator<ReadableIndex<?>> iterator = indexMap.values().iterator(); iterator.hasNext();) {
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
