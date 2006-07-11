
//============================================================================
// 
//  file: NetworkBlast.java
// 
//  Copyright (c) 2006, University of California San Diego 
// 
//  This program is free software; you can redistribute it and/or modify it 
//  under the terms of the GNU General Public License as published by the 
//  Free Software Foundation; either version 2 of the License, or (at your 
//  option) any later version.
//  
//  This program is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
//  for more details.
//  
//  You should have received a copy of the GNU General Public License along 
//  with this program; if not, write to the Free Software Foundation, Inc., 
//  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
//============================================================================



package nct.networkblast;

import org.apache.commons.cli.*; // for CLI
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.StreamHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.io.*;
import java.lang.*;
import java.text.DecimalFormat;

import nct.networkblast.search.*;
import nct.networkblast.graph.*;
import nct.networkblast.graph.compatibility.*;
import nct.networkblast.score.*;
import nct.networkblast.filter.*;
import nct.graph.*;
import nct.graph.basic.*;
import nct.graph.util.*;
import nct.filter.*;
import nct.output.*;
import nct.service.homology.HomologyModel;
import nct.service.homology.sif.SIFHomologyReader;

/**
 * The main program (NetworkBlast.java) will create all the objects needed to
 * run the NetworkBlast algorithm.
 */
public class NetworkBlast {

	protected static Options options;

	// default variables
	public static String VERSION = "0.1";
	public static boolean FILEOUTPUT = false;
	public static boolean SILENT = false;
	public static boolean useZero = false;
	public static boolean filterDuplicateComplexNodes = false;
	public static boolean filterDuplicatePathNodes = false;
	public static double truthFactorDefault = 2.5;
	public static double modelTruthDefault = 0.8;
	public static double expectationDefault = 1e-10;
	public static double backgroundProbDefault = 1e-10;
	public static double dupeThresholdDefault = 1.0;
	public static String outFileDefault = "network_blast_results";
	public static Level logLevelDefault = Level.WARNING;
	public static int simulationsDefault = 0;
	
	public static int complexMinSeedSize = 4;
	public static int complexMaxSize = 15;
	public static int pathSize = 4;
	public static Level logLevel = null;

	protected double expectation;
	protected double backgroundProb;
	protected double truthFactor;
	protected double modelTruth;
	protected double dupeThreshold;
	protected int numSimulations;

	protected Random randomNG;
	protected String compatFile;
	protected String intGraph1;
	protected String intGraph2;
	protected String outFile;

	protected static StringBuffer networkBlastRecord;
	protected static String lineSep = System.getProperty("line.separator");

	private static Logger log = Logger.getLogger("networkblast");
	private static Handler logConsole = null;
	private static Handler logFile = null;

	/**
	 * Main. Duh.
	 */
	public static void main(String[] args) {
		NetworkBlast nb = new NetworkBlast(args);
	}

	/**
	 * This is the main class that initializes and executes everything.
	 * @param args Command line args.
	 */
	public NetworkBlast(String[] args) {
	
		parseCommandLine(args);
		setUpLogging(logLevel);


		try { 
			// create the interaction graphs
			print("begin reading homology and interaction data");
			List<SequenceGraph<String,Double>> inputSpecies = new ArrayList<SequenceGraph<String,Double>>();

			inputSpecies.add( new InteractionGraph(intGraph1) );
			print("read in interaction graph " + inputSpecies.get(0).getId());
			inputSpecies.add( new InteractionGraph(intGraph2) );
			print("read in interaction graph " + inputSpecies.get(1).getId());

			for ( Graph<String,Double> spec : inputSpecies ) {
				print("num nodes in interaction graph " + spec.getId() + ": " + spec.numberOfNodes() );
				print("num edges in interaction graph " + spec.getId() + ": " + spec.numberOfEdges() );
			}


			// get the homology data
			SIFHomologyReader sr = new SIFHomologyReader(compatFile);
			HomologyGraph homologyGraph = new HomologyGraph(sr, expectation, inputSpecies);
			print("num nodes in homology graph: " + homologyGraph.numberOfNodes() );
			print("num edges in homology graph: " + homologyGraph.numberOfEdges() );
			// create classes for compat graph 
			ScoreModel<String,Double> logScore = new LogLikelihoodScoreModel<String>(truthFactor, modelTruth, backgroundProb);
			CompatibilityCalculator compatCalc = new AdditiveCompatibilityCalculator(0.01,logScore,useZero);

			// initialize the search classes
			List<Graph<String,Double>> resultPaths;
			SearchGraph<String,Double> colorCoding = new ColorCodingPathSearch<String>(pathSize);

			List<Graph<String,Double>> resultComplexes;
			NewComplexSearch<String> greedyComplexes = new NewComplexSearch<String>( complexMinSeedSize, complexMaxSize);
			
			// initialize filter
			Filter<String,Double> dupeFilter = new DuplicateThresholdFilter<String,Double>(dupeThreshold);
			Filter<String,Double> dupeNodeFilter = new UniqueCompatNodeFilter();
			Filter<String,Double> sortFilter = new SortFilter<String,Double>(true);

			// initialize the randomization classes
			GraphRandomizer<String,Double> homologyShuffle = new EdgeWeightShuffle<String,Double>(randomNG);
			GraphRandomizer<String,Double> edgeShuffle = new ThresholdRandomizer(randomNG,0.2);

			// define the scoring model for the search algorithms
			ScoreModel<String,Double> edgeScore = new SimpleEdgeScoreModel<String>();

			if ( numSimulations > 0 ) 
				print("run 0 is NOT randomized - it is run on the unaltered input data"); 
			int count = 0;

			do {
				print("use zero edges: " + useZero);
				print("filter duplicate complex nodes: " + filterDuplicateComplexNodes); 
				print("filter duplicate path nodes: " + filterDuplicatePathNodes); 
				print("expectation: " + expectation); 
				print("background prob: " + backgroundProb);
				print("truth factor: " + truthFactor);
				print("model truth: " + modelTruth);
				print("duplicate threshold: " + dupeThreshold);
				print("num simulations: " + numSimulations);
				print("complex seed size: " + complexMinSeedSize); 
				print("max complex size: " + complexMaxSize);
				print("path size: " + pathSize); 


				print("begin creating compatibility graph");
				CompatibilityGraph compatGraph = new CompatibilityGraph(homologyGraph, inputSpecies, compatCalc, null );
				print("num nodes compatibility graph: " + compatGraph.numberOfNodes() );
				print("num edges compatibility graph: " + compatGraph.numberOfEdges() );

				print("begin path search");
				resultPaths = colorCoding.searchGraph(compatGraph, edgeScore);

				print("begin complexes search");
				greedyComplexes.setSeeds( resultPaths );
				resultComplexes = greedyComplexes.searchGraph(compatGraph, edgeScore);

				print("found " + resultPaths.size() + " unfiltered paths");
				print("found " + resultComplexes.size() + " unfiltered complexes");

				resultPaths = dupeFilter.filter(resultPaths);
				resultComplexes = dupeFilter.filter(resultComplexes);

				if ( filterDuplicatePathNodes ) 
					resultPaths = dupeNodeFilter.filter(resultPaths);

				if ( filterDuplicateComplexNodes ) 
					resultComplexes = dupeNodeFilter.filter(resultComplexes);
				resultPaths = sortFilter.filter(resultPaths);
				resultComplexes = sortFilter.filter(resultComplexes);

				print("found " + resultPaths.size() + " filtered paths");
				print("found " + resultComplexes.size() + " filtered complexes");

				print("path results");
				for (int i = 0; i < resultPaths.size(); i++) 
					print("path " + i + ": " + resultPaths.get(i).toString());

				print("complexes results: " );
				for (int i = 0; i < resultComplexes.size(); i++) 
					print("complex " + i + ": " + resultComplexes.get(i).toString());
				if (FILEOUTPUT) {
					
					String zname = outFile; 
					if ( numSimulations > 0 )
						zname = zname + "_" + count;
	
					print("writing results to file: " + zname + ".zip" );
					ZIPSIFWriter<String,Double> zipper = new ZIPSIFWriter<String,Double>(zname);
					int ct = 0;		
					for ( Graph<String,Double> p : resultPaths )
						zipper.add(p, "path_" + ct++);

					ct = 0;		
					for ( Graph<String,Double> p : resultComplexes )
						zipper.add(p, "complex_" + ct++);

					zipper.add(compatGraph,"compat_graph");
					zipper.add(homologyGraph,"homology_graph");

					ct = 0;
					for ( Graph<String,Double> spec : inputSpecies )
						zipper.add( spec, "interaction_graph_" + ct++ );

					zipper.add(networkBlastRecord.toString(),"networkblast_record.txt");
					networkBlastRecord.delete(0,networkBlastRecord.length()-1);


					zipper.write();
				}

				count++;	
				if ( numSimulations > 0 && count <= numSimulations ) {
					print("begin simulation " + count);
					print("randomizing input graphs");
					for ( Graph<String,Double> spec : inputSpecies ) {
						edgeShuffle.randomize( spec );
						print("num nodes in randomized interaction graph " + spec.getId() + ": " + spec.numberOfNodes() );
						print("num edges in randomized interaction graph " + spec.getId() + ": " + spec.numberOfEdges() );
					}

					homologyShuffle.randomize( homologyGraph );
					print("num nodes in randomized homology graph: " + homologyGraph.numberOfNodes() );
					print("num edges in randomized homology graph: " + homologyGraph.numberOfEdges() );
				}

			} while ( count <= numSimulations );

		} catch (IOException e1) {
			log.severe("Error reading file: " + e1.getMessage());
		}
	}

	/**
	 * Ummmmm.
	 */
	private void parseCommandLine(String[] args) {

		// Define and add options
		options = new Options();

		options.addOption("h", "help", false, "Print this message.");
		options.addOption("z", "zero_edges", false, "Allow zero edges.");
		options.addOption("V", "version", false, "Prints the version number and exits.");
		options.addOption("S", "silent", false, "Don't print any output to the screen.");
		options.addOption(OptionBuilder
				.withLongOpt("random_seed")
				.withDescription( "Seed the random generator (Java default - varies).")
				.withValueSeparator(' ')
				.withArgName("integer seed")
				.hasArg()
				.create("r"));
		options.addOption(OptionBuilder
				.withLongOpt("filter")
				.withDescription( "Filter the results. Possible filters inlude:\nDUPE_PATH_PROTEINS\nDUPE_COMPLEX_PROTEINS\n(no filter).")
				.withValueSeparator(' ')
				.withArgName("filter name")
				.hasArg()
				.create("f"));


		options.addOption(OptionBuilder
				.withLongOpt("expectation")
				.withDescription( "Expectation threshold level (" + expectationDefault + ").")
				.withValueSeparator(' ')
				.withArgName( "value")
				.hasArg()
				.create("e"));

		options.addOption(OptionBuilder
				.withLongOpt("background")
				.withDescription( "Background probability (" + backgroundProbDefault + ").")
				.withValueSeparator(' ')
				.withArgName( "value")
				.hasArg()
				.create("b"));

		options.addOption(OptionBuilder
				.withLongOpt("truth_factor")
				.withDescription( "Log likelihood score truth factor (" + truthFactorDefault + ").")
				.withValueSeparator(' ')
				.withArgName("value")
				.hasArg()
				.create("t"));

		options.addOption(OptionBuilder
				.withLongOpt("model_truth")
				.withDescription( "Model truth (beta value) (" + modelTruthDefault + ").") 
				.withValueSeparator(' ')
				.withArgName("value")
				.hasArg()
				.create("m"));

		options.addOption(OptionBuilder
				.withLongOpt("log_level")
				.withDescription( "Logging level. Standard Java logging levels allowed:\nOFF, SEVERE, WARNING,\nINFO, CONFIG, FINE,\nFINER, FINEST, ALL\n(" + logLevelDefault.toString() + ").")
				.withValueSeparator(' ')
				.withArgName("level name")
				.hasArg()
				.create("l"));

		options.addOption(OptionBuilder
				.withLongOpt("simulations")
				.withDescription( "Number of additional simulations to run (" + simulationsDefault + ").")
				.withValueSeparator(' ')
				.withArgName("number")
				.hasArg()
				.create("s"));

		options.addOption(OptionBuilder
				.withLongOpt("duplicate_threshold")
				.withDescription( "Percent duplicate nodes allowed in a result graph.(" + dupeThresholdDefault + ").")
				.withValueSeparator(' ')
				.withArgName("threshold")
				.hasArg()
				.create("d"));

		options.addOption(OptionBuilder
				.withLongOpt("output")
				.withDescription( "Output file label. The file will be a ZIP file with '.zip'\n appended to the specified name.")
				.withValueSeparator(' ')
				.withArgName("file")
				.hasArg()
				.create("o"));

		// try to parse the cmd line
		// try to parse the cmd line
		CommandLineParser parser = new PosixParser();
		CommandLine line = null;

		try {
			line = parser.parse(options, args);
			if (args.length == 0) {
				helpAndDie("ERROR: missing arguments");
			}
		} catch (ParseException e) {
			System.err.println("Parse failed: " + e.getMessage());
			System.exit(0);
		}

		// boolean args
		if (line.hasOption("V")) {
			System.out.println("NetworkBlast " + VERSION);
			System.exit(0);
		}

		if (line.hasOption("h")) {
			helpAndDie(null);
		}

		if (line.hasOption("z")) {
			useZero = true;
		}

		if (line.hasOption("S")) {
			SILENT = true;
		}

		if (line.hasOption("f")) {
			String[] filters = line.getOptionValues("f");
			for ( int i = 0; i < filters.length; i++ ) {
				if ( filters[i].equals("DUPE_PATH_PROTEINS") )
					filterDuplicatePathNodes = true;
				else if ( filters[i].equals("DUPE_COMPLEX_PROTEINS") )
					filterDuplicateComplexNodes = true;
				else
					log.warning("Invalid filter specified: " + filters[i]);
			}
		}

		// optional args

		if (line.hasOption("r")) {
			randomNG = new Random(Long.parseLong(line.getOptionValue("r")));
		} else {
			randomNG = new Random();
		}
		
		if (line.hasOption("o")) {
			outFile = line.getOptionValue("o");
			FILEOUTPUT = true;
		} else {
			outFile = outFileDefault;
			FILEOUTPUT = false;
		}

		if (line.hasOption("e")) {
			expectation = Double.parseDouble(line.getOptionValue("e"));
		} else {
			expectation = expectationDefault;
		}

		if (line.hasOption("b")) {
			backgroundProb = Double.parseDouble(line.getOptionValue("b"));
		} else {
			backgroundProb = backgroundProbDefault;
		}

		if (line.hasOption("t")) {
			truthFactor = Double.parseDouble(line.getOptionValue("t"));
		} else {
			truthFactor = truthFactorDefault;
		}

		if (line.hasOption("m")) {
			modelTruth = Double.parseDouble(line.getOptionValue("m"));
		} else {
			modelTruth = modelTruthDefault;
		}

		if (line.hasOption("l")) {
			logLevel = determineLogLevel( line.getOptionValue("l") );
		} else {
			logLevel = logLevelDefault;
		}

		if (line.hasOption("s")) {
			numSimulations = Integer.parseInt(line.getOptionValue("s")); 
		} else {
			numSimulations = simulationsDefault;
		}

		if (line.hasOption("d")) {
			dupeThreshold = Double.parseDouble(line.getOptionValue("d")); 
		} else {
			dupeThreshold = dupeThresholdDefault;
		}

		// required unlabeled args
		String[] remains = line.getArgs();

		if (remains.length < 2) {
			helpAndDie("ERROR: missing compatibility and/or interaction graphs");
		}

		compatFile = remains[0];
		intGraph1 = remains[1];
		intGraph2 = remains[2];
	}

	/**
	 * Simple helper method that prints and error message and exits with a status of 1.
	 */
	private static void helpAndDie(String message) {
		HelpFormatter formatter = new HelpFormatter();
		if (message != null) 
			log.severe(message);
		formatter.printHelp( "java -jar nct.jar [OPTIONS] <homology file> <interaction file1> <interaction file2>", options);
		System.exit(1);
	}

	/**
	 * Initializes the logger object according to user's wishes.
	 */
	public static void setUpLogging(Level xlogLevel) {
		networkBlastRecord = new StringBuffer();

		// Create a new handler to write to the console
		//logConsole = new ConsoleHandler();
		logConsole = new StreamHandler(System.out,new SimpleFormatter());

	/*
		// create a new handler to write to a named logFile
		try {
			logFile = new FileHandler("logs/networkblast.log");
			logFile.setFormatter(new SimpleFormatter());
		} catch(IOException ioe) {
			log.warning("Could not create a log file...");
		}
		*/
		
		// Add the handlers to the logger
		log.addHandler(logConsole);
		//log.addHandler(logFile);

		log.setLevel(xlogLevel);
	}

	/**
	 * A stupid method to determine the log level based on a string.
	 */
	private static Level determineLogLevel( String levelString ) {

		levelString = levelString.toLowerCase();

		if ( Pattern.matches("severe", levelString) )
			return Level.SEVERE;
		if ( Pattern.matches("warning", levelString) )
			return Level.WARNING;
		if ( Pattern.matches("info", levelString) )
			return Level.INFO;
		if ( Pattern.matches("config", levelString) )
			return Level.CONFIG;
		if ( Pattern.matches("fine", levelString) )
			return Level.FINE;
		if ( Pattern.matches("finer", levelString) )
			return Level.FINER;
		if ( Pattern.matches("finest", levelString) )
			return Level.FINEST;
		if ( Pattern.matches("off", levelString) )
			return Level.OFF;
		if ( Pattern.matches("all", levelString) )
			return Level.ALL;

		log.warning("Unrecognized log level: " + levelString);
		log.warning("Only standard java log levels allowed.");
		log.warning("Using default log level: " + logLevelDefault.toString());

		return logLevelDefault; 
	}

	private void print(String s) {
		if ( !SILENT )
			System.out.println("# " + s);

		if ( FILEOUTPUT ) {
			networkBlastRecord.append(s);
			networkBlastRecord.append(lineSep);
		}

		log.info(s);
	}
}
