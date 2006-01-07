package nct.networkblast;

import org.apache.commons.cli.*; // for CLI
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.io.*;
import java.text.DecimalFormat;

import nct.networkblast.search.*;
import nct.networkblast.graph.*;
import nct.networkblast.score.*;
import nct.graph.*;
import nct.graph.basic.*;
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
	public static boolean SERIALIZE = false;
	public static boolean VERBOSE = false;
	public static boolean QUIET = false;
	public static double truthFactorDefault = 2.5;
	public static double modelTruthDefault = 0.8;
	public static String outputPrefixDefault = "out";
	public static double expectationDefault = 1e-10;
	public static double backgroundProbDefault = 1e-10;
	public static Level logLevelDefault = Level.WARNING;
	
	public static int complexMinSeedSize = 4;
	public static int complexMaxSize = 15;
	public static int pathSize = 4;
	public static Level logLevel = null;

	protected String outputPrefix;
	protected double expectation;
	protected double backgroundProb;
	protected double truthFactor;
	protected double modelTruth;
	protected Random randomNG;
	protected String compatFile;
	protected String intGraph1;
	protected String intGraph2;

	private static Logger log = Logger.getLogger("networkblast");
	private static ConsoleHandler logConsole = null;
	private static FileHandler logFile = null;

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
			log.info("this is a log message");

			if (!QUIET)
				System.out.println("# read in homology and interaction data");
			List<SequenceGraph<String,Double>> inputSpecies = new ArrayList<SequenceGraph<String,Double>>();

			inputSpecies.add( new InteractionGraph(intGraph1) );
			if (!QUIET)
				System.out.println("# read in interaction data for species 1");
			inputSpecies.add( new InteractionGraph(intGraph2) );
			if (!QUIET)
				System.out.println("# read in interaction data for species 2");

			ScoreModel logScore = new LogLikelihoodScoreModel(truthFactor, modelTruth, backgroundProb);
			SIFHomologyReader sr = new SIFHomologyReader(compatFile);
			HomologyGraph homologyGraph = new HomologyGraph(sr);
			for ( SequenceGraph<String,Double> spec : inputSpecies )
				homologyGraph.addGraph(spec);

			if (!QUIET)
				System.out.println("# begin creating compatibility graph");
			CompatibilityGraph compatGraph = new CompatibilityGraph(homologyGraph,
					inputSpecies, expectation, logScore);

			if (!QUIET)
				System.out.println("# begin path search");
			List<Graph<String,Double>> results_paths;
			SearchGraph colorCoding = new ColorCodingPathSearch(pathSize);
			results_paths = colorCoding.searchGraph(compatGraph, logScore);

			if (!QUIET) {
				DecimalFormat myFormatter = new DecimalFormat("#0.00");
				System.out.println("# path results");
				for (int i = 0; i < results_paths.size(); i++) {
					System.out.println("path " + i + ": "
							+ results_paths.get(i).getNodes() + " : "
							+ results_paths.get(i).getScore());
				}
			}

			if (!QUIET)
				System.out.println("# begin complexes search");
			SearchGraph greedyComplexes = new GreedyComplexSearch(
					results_paths, complexMinSeedSize, complexMaxSize);
			List<Graph<String,Double>> results_complexes;
			results_complexes = greedyComplexes.searchGraph(compatGraph, logScore);

			if (!QUIET) {
				System.out.println("# complexes results: " );
				for (int i = 0; i < results_complexes.size(); i++) {
					System.out.println("complex " + i + ": "
							+ results_complexes.get(i).getNodes() + " : "
							+ results_complexes.get(i).getScore());

				}
			}

			if (SERIALIZE) {

				if (!QUIET)
					System.out.println("# serializing path results");
				ZIPSIFWriter zipper = new ZIPSIFWriter<String,Double>(
						"testpaths");
				zipper.write(results_paths);

				if (!QUIET)
					System.out.println("# serializing complexes results");
				zipper = new ZIPSIFWriter("testcomplexes");
				zipper.write(results_complexes);

				if (!QUIET)
					System.out.println("# serializing compatibility graph");
				zipper = new ZIPSIFWriter("compatibility");
				List<Graph<String,Double>> arraySol = new ArrayList<Graph<String,Double>>();
				arraySol.add(compatGraph);
				zipper.write(arraySol);
			}

			if (!QUIET) {
				System.out.println("# found " + results_paths.size()
						+ " unfiltered paths");
				System.out.println("# found " + results_complexes.size()
						+ " unfiltered complexes");
			}

			Filter dupeFilter = new DuplicateThresholdFilter(1.0);
			results_paths = dupeFilter.filter(results_paths);
			results_complexes = dupeFilter.filter(results_complexes);

			if (!QUIET) {
				System.out.println("# found " + results_paths.size()
						+ " filtered paths");
				System.out.println("# found " + results_complexes.size()
						+ " filtered complexes");
			}

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

		options.addOption("h", "help", false, "print this message");

		options.addOption("V", "version", false,
				"prints the version number and exits");
		options.addOption("v", "verbose", false, "give extra info");
		options.addOption("S", "serialize", SERIALIZE,
				"serialize result data (" + SERIALIZE + ")");
		options.addOption("q", "quiet", QUIET,
				"don't print any OUTPUT (exclusive of -S) (" + QUIET + ")");

		options.addOption(OptionBuilder.withLongOpt("output").withDescription(
				"output prefix").withValueSeparator('=').withArgName(
				"outputPrefix").hasArg().create("o"));

		options.addOption(OptionBuilder.withLongOpt("randomSeed")
				.withDescription(
						"seed the random generator (Java default - varies)")
				.withValueSeparator('=').withArgName("randomSeed").hasArg()
				.create("r"));

		options.addOption(OptionBuilder.withLongOpt("expectation")
				.withDescription(
						"expectation threshold level (" + expectationDefault
								+ ")").withValueSeparator('=').withArgName(
						"expectation").hasArg().create("e"));

		options.addOption(OptionBuilder.withLongOpt("background")
				.withDescription(
						"background probability (" + backgroundProbDefault
								+ ")").withValueSeparator('=').withArgName(
						"background").hasArg().create("b"));

		options.addOption(OptionBuilder.withLongOpt("truth_factor")
				.withDescription(
						"log likelihood score truth factor ("
								+ truthFactorDefault + ")").withValueSeparator(
						'=').withArgName("truth_factor").hasArg().create("t"));

		options.addOption(OptionBuilder.withLongOpt("model_truth")
				.withDescription(
						"model truth (beta value) (" + modelTruthDefault + ")")
				.withValueSeparator('=').withArgName("model_truth").hasArg()
				.create("m"));

		options.addOption(OptionBuilder.withLongOpt("log_level")
				.withDescription(
				"logging level (" + logLevelDefault.toString() + ") (standard Java log levels allowed)")
				.withValueSeparator('=').withArgName("log_level").hasArg()
				.create("l"));

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

		if (line.hasOption("v")) {
			VERBOSE = true;
		}

		if (line.hasOption("S")) {
			SERIALIZE = true;
		}

		if (line.hasOption("q")) {
			QUIET = true;
		}

		// optional args
		if (line.hasOption("o")) {
			outputPrefix = line.getOptionValue("o");
		} else {
			outputPrefix = outputPrefixDefault;
		}

		if (line.hasOption("r")) {
			randomNG = new Random(Long.parseLong(line.getOptionValue("r")));
		} else {
			randomNG = new Random();
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
		formatter.printHelp( "java -jar networkblast.jar [OPTIONS] <homology file> <interaction file1> [<interaction file2>... ]", options);
		System.exit(1);
	}

	/**
	 * Initializes the logger object according to user's wishes.
	 */
	public static void setUpLogging(Level xlogLevel) {
		// Create a new handler to write to the console
		logConsole = new ConsoleHandler();

		// create a new handler to write to a named logFile
		try {
			logFile = new FileHandler("logs/networkblast.log");
			logFile.setFormatter(new SimpleFormatter());
		} catch(IOException ioe) {
			log.warning("Could not create a log file...");
		}
		
		// Add the handlers to the logger
		log.addHandler(logConsole);
		log.addHandler(logFile);

		log.setLevel(xlogLevel);
	}

	/**
	 * A stupid method to determine the log level based on a string.
	 */
	private static Level determineLogLevel( String levelString ) {

		levelString.toLowerCase();

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
}
