package org.genmapp.subgeneviewer.readers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.genmapp.subgeneviewer.splice.view.SpliceNetworkView;
import org.genmapp.subgeneviewer.text.Example_Exon_Structure_GenMAPP_CS;


public class FileParser {

	private static final String PARSE_SPLICE = "splice";
	
	SpliceNetworkView view;
	String id;
	
	private List<LineParser> _lineParsers = new ArrayList<LineParser>();

	BufferedReader _in;

	String _fileName = "c:/java/code/SubgeneViewer/data/example-exon-structure-GenMAPP-CS.txt";
	
	public FileParser(SpliceNetworkView v, String nodeId, String type) {
		
		view = v;
		id = nodeId;
		
		if (type.equals(PARSE_SPLICE)){
			LineParser lp = new ParseSplice(v, nodeId, this);
		}
		else{}
		
		}
	
	public void addLineParser(LineParser lp) {
		_lineParsers.add(lp);
	}

	public void removeLineTermConverter(LineParser lp) {
		_lineParsers.remove(lp);
	}

	private void processLineTerms(String[] terms) {
		for (int i = 0; i < _lineParsers.size(); i++) {
			_lineParsers.get(i).processLineTerms(terms);
		}
	}

	public void doit() {

		String INPUT = null;
		Pattern pattern;

		try {
			_in = new BufferedReader(new FileReader(_fileName));
		} catch (FileNotFoundException fnfe) {
			System.out.println("Cannot locate input file! " + fnfe.getMessage());
			fnfe.printStackTrace();
		}

		try {
			INPUT = _in.readLine();
		} catch (IOException ioe) {
		}

		while (INPUT != null) {
			pattern = Pattern.compile("\t");

			processLineTerms(pattern.split(INPUT));
			
			try {
				INPUT = _in.readLine();
			} catch (IOException ioe) {
			}
		}
		try {
			_in.close();

		} catch (IOException ioe) {

		}

	}

}
