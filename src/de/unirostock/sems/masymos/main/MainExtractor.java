package de.unirostock.sems.masymos.main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import de.binfalse.bfutils.FileRetriever;
import de.unirostock.sems.masymos.annotation.AnnotationResolverUtil;
import de.unirostock.sems.masymos.configuration.Config;
import de.unirostock.sems.masymos.configuration.NodeLabel;
import de.unirostock.sems.masymos.configuration.Property;
import de.unirostock.sems.masymos.database.IdFactory;
import de.unirostock.sems.masymos.database.Manager;
import de.unirostock.sems.masymos.extractor.Extractor;
import de.unirostock.sems.masymos.extractor.CellML.CellMLExtractorThread;
import de.unirostock.sems.masymos.extractor.Owl.Ontology;
import de.unirostock.sems.masymos.extractor.SBML.SBMLExtractor;
import de.unirostock.sems.masymos.extractor.SBML.SBMLExtractorThread;
import de.unirostock.sems.masymos.extractor.SedML.SEDMLExtractor;
import de.unirostock.sems.masymos.extractor.SedML.SEDMLExtractorThread;


public class MainExtractor {

	static boolean quiet = false;
	static boolean annotationOnly = false;
	static boolean noAnno = false;
	static boolean iscellml = false;
	static boolean issbml = true;
	static boolean isxml = false;
	static boolean issedml = false;
	static boolean isp2m = false;
	static boolean isowl = false;
	static boolean fileMode = false;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String directory = null;
		String ontologyName = null;
		//parse arguments
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-dbPath")) { 
				Config.instance().setDbPath(args[++i]);
			}
//			if (args[i].equals("-submitter")) { 
//				Config.submitter = args[++i];
//			}
			if (args[i].equals("-directory")) { 
				directory = args[++i];
			}
			if (args[i].equals("-ontology")) { 
				ontologyName = args[++i];
			}
			if (args[i].equals("-quiet")) { 
				quiet = true;
			}
			if (args[i].equals("-annoOnly")) { 
				annotationOnly = true;
			}
			if (args[i].equals("-noAnno")) { 
				noAnno = true;
			}
			if (args[i].equals("-type")) { 
				String type = args[++i];
				issbml =StringUtils.equalsIgnoreCase(type, "SBML");
				iscellml =StringUtils.equalsIgnoreCase(type, "CELLML");
				isxml =StringUtils.equalsIgnoreCase(type, "XML");
				issedml =StringUtils.equalsIgnoreCase(type, "SEDML");
				isowl =StringUtils.equalsIgnoreCase(type, "OWL");
				isp2m =StringUtils.equalsIgnoreCase(type, "P2M");
			}
			if (args[i].equals("-cache")) { 
				Config.instance().setCachePath(args[++i]);
			}
			if (args[i].equals("-fileMode")) { 
				fileMode = true;
			}
			
		}
		
//		PrintStream toFile = new PrintStream(new BufferedOutputStream(new FileOutputStream("E:/masymos-anno.txt")));
//		System.setOut(toFile);
		
		
		if (annotationOnly && noAnno) {
			System.out.print("Illegal argument combination: noAnno & annoOnly");
//			toFile.close();
			System.exit(0);
			return;
		}
		
		long start = System.currentTimeMillis();
		initializeDatabase();
		

		
		if (annotationOnly) {
			System.out.println("Starting annotation index...");
			AnnotationResolverUtil.instance().fillAnnotationFullTextIndex();
			System.out.println("all done at: "+ new Date() + " needed: " + (System.currentTimeMillis()-start)+ "ms");
			System.exit(0);
			return;
		}
		
	
		if (issbml) {
			if (fileMode) sbmlFileMode(directory); else sbmlMode(directory); 
		}
		else if (isp2m) {
			sbmlFileMode(directory);
		}
		else if (iscellml) {
			cellmlMode(directory);
		}
		else if (issedml){
			if (fileMode) sedmlFileMode(directory); else sedmlMode(directory);
		}
		else if (isowl){
			owlMode(directory, ontologyName);
		} else {
			System.out.println("nothing done...wrong parameter?");
			System.exit(0);
			return;
		}
		
		if (!noAnno &&(issbml || iscellml)) {
			System.out.println("Starting annotation index...");
			AnnotationResolverUtil.instance().fillAnnotationFullTextIndex();
		}
		System.out.println("all done at: "+ new Date() + " needed: " + (System.currentTimeMillis()-start)+ "ms");
		
		//call exit explicitly in case there a zombi threads
//		toFile.close();
		System.exit(0);
	}

	public static void owlMode(String directory, String ontologyName) {
		
		if (StringUtils.isNotBlank(ontologyName)) { 
			System.out.println("Processing: " + ontologyName);
			Ontology.extractOntology(new File(directory), ontologyName);
			return;
		}
		
		File dir = new File(directory);
		for (Iterator<File> fileIt = FileUtils.iterateFiles(dir, new String[]{"owl"}, false); fileIt.hasNext();) {
			File file = (File) fileIt.next();
			if (file.isDirectory()) continue;
			System.out.println("Processing: " + file.getName());
			Ontology.extractOntology(file, FilenameUtils.removeExtension(file.getName())); 
		}	
	}

	public static void initializeDatabase(){
		//create neo4j database

		System.out.println("Started at: " + new Date());
		System.out.print("Getting manager...");
		Manager.instance();
		System.out.println("done");

	}


	
	private static void cellmlMode(String modelDir) throws IOException {
		//cache cellml files
		File cellmlCacheDir = new File (Config.instance().getCachePath()+"cellml/");
		cellmlCacheDir.mkdirs();
		
		FileRetriever.setUpCache( cellmlCacheDir );
		//define stdOut and a dev0
		PrintStream stdOut = new PrintStream(System.out);
		PrintStream dev0 = new PrintStream(new OutputStream() {
		    public void write(int b) {
		    }
		});
		//parse and store a model
		File directory = new File(modelDir);
		List<String> urlList = new LinkedList<String>();
		createFileList(directory, urlList);
		
		//thread implementation of executors and future		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		//end thread
		int i = 0;		
		for (Iterator<String> urlIt = urlList.iterator(); urlIt.hasNext();) {
			long fileStart = System.currentTimeMillis();
			String url = (String) urlIt.next();
			i++;
			System.out.print("Processing file# " + i +": " + url + " ...");
			if (quiet) System.setOut(dev0);
			
			String vID = Long.valueOf(System.nanoTime()).toString();				
				
			CellMLExtractorThread cet = new CellMLExtractorThread(url, vID);
			Node documentNode;
			try {
				documentNode = executor.submit(cet).get();
			} catch (InterruptedException | ExecutionException e) {
				GraphDatabaseService graphDB = Manager.instance().getDatabase();
					
				try (Transaction tx = graphDB.beginTx()){
					documentNode = graphDB.createNode();				
					documentNode.addLabel(NodeLabel.Types.DOCUMENT);
					tx.success();
				}					
			}
				//Node documentNode = Extractor.extractStoreIndex(url,null,dID,Property.ModelType.CELLML); 
			Map<String, String> propertyMap = new HashMap<String, String>();
			propertyMap.put(Property.General.URI, url);
			String filename;
			filename = StringUtils.substringAfterLast(url, "/");
			if (StringUtils.isEmpty(filename)) filename = StringUtils.substringAfterLast(StringUtils.substringBeforeLast(url, "/"), "/");
			propertyMap.put(Property.General.FILENAME, filename);
			Extractor.setExternalDocumentInformation(documentNode, propertyMap);
			if (quiet) System.setOut(stdOut);
			System.out.println("done in " + (System.currentTimeMillis()-fileStart) + "ms");
		}
	}
	
	private static void sbmlMode(String modelDir) throws IOException {
		//cache sbml files
		File sbmlCacheDir = new File (Config.instance().getCachePath()+"sbml/");
		sbmlCacheDir.mkdirs();
		
		FileRetriever.setUpCache(sbmlCacheDir);
		//define stdOut and a dev0
		PrintStream stdOut = new PrintStream(System.out);
		PrintStream dev0 = new PrintStream(new OutputStream() {
		    public void write(int b) {
		    }
		});	
		
		//parse and store a model
		File directory = new File(modelDir);
		List<String> urlList = new LinkedList<String>();
		createFileList(directory, urlList);
		
		//thread implementation of executors and future		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		//end thread
		int i = 0;		
		for (Iterator<String> urlIt = urlList.iterator(); urlIt.hasNext();) {
			long fileStart = System.currentTimeMillis();
			String url = (String) urlIt.next();
			i++;
			System.out.print("Processing file# " + i +": " + url + " ...");
			if (quiet) System.setOut(dev0);
				String vID = Long.valueOf(System.nanoTime()).toString();				
				
				SBMLExtractorThread set = new SBMLExtractorThread(url, vID);
				Node documentNode;
				try {
					documentNode = executor.submit(set).get();
				} catch (InterruptedException | ExecutionException e) {
					GraphDatabaseService graphDB = Manager.instance().getDatabase();
					
					try (Transaction tx = graphDB.beginTx()){
						documentNode = graphDB.createNode();				
						documentNode.addLabel(NodeLabel.Types.DOCUMENT);
						tx.success();
					}					
				}
			 
				Map<String, String> propertyMap = new HashMap<String, String>();
			    propertyMap.put(Property.General.URI, url);
			    String filename;
			    filename = StringUtils.substringAfterLast(url, "=");
			    //if (StringUtils.isEmpty(filename)) filename = StringUtils.substringAfterLast(StringUtils.substringBeforeLast(url, "/"), "/");
			    propertyMap.put(Property.General.FILENAME, filename);
				Extractor.setExternalDocumentInformation(documentNode, propertyMap);
			if (quiet) System.setOut(stdOut);
			System.out.println("done in " + (System.currentTimeMillis()-fileStart) + "ms");
		}
	}
	
	private static void sedmlMode(String sedDir) {
		//define stdOut and a dev0
		PrintStream stdOut = new PrintStream(System.out);
		PrintStream dev0 = new PrintStream(new OutputStream() {
		    public void write(int b) {
		    }
		});
		//parse and store a model
		File directory = new File(sedDir);
		List<String> urlList = new LinkedList<String>();
		createFileList(directory, urlList);

		//thread implementation of executors and future		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		//end thread
		int i = 0;		
		for (Iterator<String> urlIt = urlList.iterator(); urlIt.hasNext();) {
			long fileStart = System.currentTimeMillis();
			String url = (String) urlIt.next();
			i++;
			System.out.print("Processing file# " + i +": " + url + " ...");
			if (quiet) System.setOut(dev0);
				String vID = Long.valueOf(System.nanoTime()).toString();				
				
				SEDMLExtractorThread set = new SEDMLExtractorThread(url, vID);
				Node documentNode;
				try {
					documentNode = executor.submit(set).get();
				} catch (InterruptedException | ExecutionException e) {
					GraphDatabaseService graphDB = Manager.instance().getDatabase();
					
					try (Transaction tx = graphDB.beginTx()){
						documentNode = graphDB.createNode();				
						documentNode.addLabel(NodeLabel.Types.DOCUMENT);
						tx.success();
					}					
				}
				//Node documentNode = Extractor.extractStoreIndex(url,null,dID,Property.ModelType.CELLML); 
				Map<String, String> propertyMap = new HashMap<String, String>();
			    propertyMap.put(Property.General.URI, url);
			    String filename;
			    filename = StringUtils.substringAfterLast(url, "/");
			    if (StringUtils.isEmpty(filename)) filename = StringUtils.substringAfterLast(StringUtils.substringBeforeLast(url, "/"), "/");
			    propertyMap.put(Property.General.FILENAME, filename);
				Extractor.setExternalDocumentInformation(documentNode, propertyMap);
			if (quiet) System.setOut(stdOut);
			System.out.println("done in " + (System.currentTimeMillis()-fileStart) + "ms");
		}

	}
	
	private static void sedmlFileMode(String sedDir) {
		//define stdOut and a dev0
		PrintStream stdOut = new PrintStream(System.out);
		PrintStream dev0 = new PrintStream(new OutputStream() {
		    public void write(int b) {
		    }
		});
		
		File directory = new File(sedDir);
		int i = 0;
		for (Iterator<File> fileIt = FileUtils.iterateFiles(directory, new String[]{"xml","sedml"}, true); fileIt.hasNext();) {
			File file = (File) fileIt.next();
			if (file.isDirectory()) continue;
			
			long fileStart = System.currentTimeMillis();
			i++;
			System.out.print("Processing file# " + i +": " + file.getName() + " ... ");
			if (quiet) System.setOut(dev0);
			try {
				   Long uID = IdFactory.instance().getID();	
				   String vID = Long.valueOf(System.nanoTime()).toString();
				   Node documentNode = SEDMLExtractor.extractStoreIndexSEDML(file, vID, uID);
                   Map<String, String> propertyMap = new HashMap<String, String>();
				
				propertyMap.put(Property.General.URI, file.getPath());
                propertyMap.put(Property.General.FILENAME, file.getName());
                Extractor.setExternalDocumentInformation(documentNode, propertyMap);
                Extractor.setDocumentUID(documentNode, uID);
			} catch (FileNotFoundException e) {
				System.out.println("File " + file.getName() + "not found!");
				e.printStackTrace();
			} catch (XMLStreamException e) {
				System.out.println("File " + file.getName() + "caused XMLStreamException");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("File " + file.getName() + "caused IOException!");
				e.printStackTrace();
			}
			if (quiet) System.setOut(stdOut);
			
			System.out.println("done in " + (System.currentTimeMillis()-fileStart) + "ms");
		}
		
	}

	private static void sbmlFileMode(String modelDir) {
		//define stdOut and a dev0
		PrintStream stdOut = new PrintStream(System.out);
		PrintStream dev0 = new PrintStream(new OutputStream() {
		    public void write(int b) {
		    }
		});
		//parse and store a model
		File directory = new File(modelDir);
		int i = 0;
		for (Iterator<File> fileIt = FileUtils.iterateFiles(directory, new String[]{"xml","sbml"}, true); fileIt.hasNext();) {
			File file = (File) fileIt.next();
			if (file.isDirectory()) continue;
			
			long fileStart = System.currentTimeMillis();
			i++;
			System.out.print("Processing file# " + i +": " + file.getName() + " ...");
			if (quiet) System.setOut(dev0);
			try {
				String vID = Long.valueOf(System.nanoTime()).toString();
				Long uID = IdFactory.instance().getID();	
				Node documentNode = SBMLExtractor.extractStoreIndexSBML(new FileInputStream(file), vID, uID);
				Map<String, String> propertyMap = new HashMap<String, String>();
			    propertyMap.put(Property.General.URI, file.getPath());
			    propertyMap.put(Property.General.FILENAME, file.getName());
				Extractor.setExternalDocumentInformation(documentNode, propertyMap);
				Extractor.setDocumentUID(documentNode, uID);
			} catch (FileNotFoundException e) {
				System.out.println("File " + file.getName() + "not found!");
				e.printStackTrace();
			} catch (XMLStreamException e) {
				System.out.println("File " + file.getName() + "caused XMLStreamException");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("File " + file.getName() + "caused IOException!");
				e.printStackTrace();
			}
			if (quiet) System.setOut(stdOut);
			System.out.println("done in " + (System.currentTimeMillis()-fileStart) + "ms");
		}
	}
	
	private static void createFileList(File directory, List<String> urlList) {
		for (Iterator<File> fileIt = FileUtils.iterateFiles(directory, new String[]{"list"}, true); fileIt.hasNext();) {
			File file = (File) fileIt.next();
			if (file.isDirectory()) continue;
			
			BufferedReader br = null;			
			try {	 
				String sCurrentLine;
				br = new BufferedReader(new FileReader(file));
				while ((sCurrentLine = br.readLine()) != null) {
					urlList.add(sCurrentLine);
				}	 
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}	 
		}
	}

}
