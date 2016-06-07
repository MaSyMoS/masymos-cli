package de.unirostock.sems.masymos.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

import de.unirostock.sems.masymos.analyzer.AnalyzerHandler;
import de.unirostock.sems.masymos.configuration.Property;

public class MainAnalyzer {
	
	private static String dumpPath = null;

	public static void main(String[] args) {

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-dumpPath")) {
				dumpPath = args[++i];
			}
		}

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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	

	
	

}
