package de.unirostock.sems.masymos.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unirostock.sems.masymos.analyzer.AnalyzerHandler;
import de.unirostock.sems.masymos.configuration.Property;

/**
*
* Copyright 2016 Ron Henkel (GPL v3)
* @author ronhenkel
*/
public class MainAnalyzer {
	
	final static Logger logger = LoggerFactory.getLogger(MainAnalyzer.class);

	public static void main(String[] args) {
/*
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-dumpPath")) {
				dumpPath = args[++i];
			}
		}
*/
		logger.debug(MainAnalyzer.class.getName() + " started");
		String s = "";
		while (!s.equals("exit")) {
			System.out
					.println("AnalyzerType: (1) all, (2) specify manually");
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
					allAnalyzers(); break;
				case 2:
					specifiedAnalyzer(); break;			
			default:
				continue;
			}
		}
		logger.debug(MainAnalyzer.class.getName() + " finished");
		System.out.println("done");
		System.exit(0);
	}


	private static void specifiedAnalyzer() {
		// TODO Auto-generated method stub
		
	}


	private static void allAnalyzers() {
		String s = "";
		while (!s.equals("exit")) {
			System.out.println("Term to analyze: ");
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
				Analyzer a;
				QueryParser qp;
				Query q;

				a = AnalyzerHandler.getAnnotationindexanalyzer();
				qp = new QueryParser(Property.General.URI, a);
				q = qp.parse(s);
				System.out.println();
				System.out.println(a.getClass().getName() + " & " + qp.getField() + ": " + q.toString());
				System.out.println();
				qp = new QueryParser("none", a);
				q = qp.parse(s);
				System.out.println();
				System.out.println(a.getClass().getName() + " & " + qp.getField() + ": " + q.toString());
				a = new KeywordAnalyzer();
				qp = new QueryParser("none", a);
				q = qp.parse(s);
				System.out.println();
				System.out.println(a.getClass().getName() + " & " + qp.getField() + ": " + q.toString());
				a = new WhitespaceAnalyzer();
				qp = new QueryParser("none", a);
				q = qp.parse(s);
				System.out.println();
				System.out.println(a.getClass().getName() + " & " + qp.getField() + ": " + q.toString());
				a = new StandardAnalyzer();
				qp = new QueryParser("none", a);
				q = qp.parse(s);
				System.out.println();
				System.out.println(a.getClass().getName() + " & " + qp.getField() + ": " + q.toString());
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	

	
	

}
