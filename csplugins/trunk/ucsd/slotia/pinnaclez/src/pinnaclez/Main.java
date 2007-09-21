package pinnaclez;

import java.util.Map;
import java.util.List;

import oiler.Graph;
import oiler.LinkedListGraph;
import oiler.TypeConverter;
import oiler.util.IntIterator;

import modlab.Search;
import modlab.Score;
import modlab.Randomize;
import modlab.Filter;
import modlab.util.SearchExecutor;

import pinnaclez.io.ParsingException;
import pinnaclez.io.ClassReader;
import pinnaclez.io.ExpressionMatrixReader;
import pinnaclez.io.NetworkReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.IOException;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;

public class Main
{
	static enum ScoreModel { MI, T }

	static ScoreModel scoreModel = ScoreModel.MI;
	static int maxModuleSize = 20;
	static int maxRadius = 2;
	static int maxNodeDegree = 300;
	static double minImprovement = 0.05;
	static int numOfThreads = Runtime.getRuntime().availableProcessors();
	static double ST1PValCutoff = 0.05;
	static double ST2PValCutoff = 0.05;
	static double ST3PValCutoff = 0.00005;
	static int numOfTrials = 100;
	static int numOfST3Randomizations = 20000;
	static String classFilePath, matrixFilePath, networkFilePath;
	static PrintStream output = System.out;
	static boolean verbose = false;
	
	public static void main(String[] args)
	{
		parseCommandLine(args);

		//
		// Stage 1.A: Read class file
		//

		if (verbose) System.err.print("Reading class file..."); System.err.flush();
		Map<String,Integer> classMap = null;
		try
		{
			classMap = ClassReader.read(classFilePath);
		}
		catch (ParsingException e)
		{
			System.err.println("Failed to read file: " + classFilePath + "\n" + e.getMessage());
			System.exit(1);
		}
		if (verbose) System.err.println(" done.");

		//
		// Stage 1.B: Read expression matrix file
		//

		if (verbose) System.err.print("Reading expression matrix file..."); System.err.flush();
		ExpressionMatrix matrix = null;
		try
		{
			matrix = ExpressionMatrixReader.read(classMap, matrixFilePath);
		}
		catch (ParsingException e)
		{
			System.err.println("Failed to read file: " + matrixFilePath + "\n" + e.getMessage());
			System.exit(1);
		}
		if (verbose) System.err.println(" done.");

		//
		// Stage 1.C: Read network file
		//

		if (verbose) System.err.print("Reading network file..."); System.err.flush();
		Graph<Activity,String> network = null;
		try
		{
			network = NetworkReader.read(matrix, networkFilePath);
		}
		catch (ParsingException e)
		{
			System.err.println("Failed to read file: " + networkFilePath + "\n" + e.getMessage());
			System.exit(1);
		}
		if (verbose) System.err.println(" done.");

		//
		// Stage 2.A: Setup search algorithm
		//

		Search<Activity,String> search = new GreedySearch<Activity,String>(maxModuleSize, maxRadius, maxNodeDegree, minImprovement);

		//
		// Stage 2.B: Setup score algorithm
		//

		AbstractActivityScore score = null;
		switch (scoreModel)
		{
			case MI: score = new MIScore(matrix); break;
			case T: score = new TScore(matrix); break;
		}

		//
		// Stage 2.C: Setup randomizing algorithm
		//

		Randomize<Activity,String> randomize = new ActivityRandomize();

		//
		// Stage 2.D: Setup SearchExecutor
		//

		SearchExecutor.Duplicate<Activity,String> duplicate = new SearchExecutor.Duplicate<Activity,String>()
		{
			public Graph<Activity,String> duplicate(Graph<Activity,String> network)
			{
				TypeConverter<Activity,String,Activity,String> converter = new TypeConverter<Activity,String,Activity,String>()
				{
					public Activity convertNodeObject(Activity nodeObject)
					{
						Activity newActivity = new Activity();
						newActivity.name = nodeObject.name;
						newActivity.matrixIndex = nodeObject.matrixIndex;
						return newActivity;
					}

					public String convertEdgeObject(String edgeObject)
					{
						return edgeObject;
					}
				};

				return new LinkedListGraph<Activity,String>(network, converter);
			}
		};

		SearchExecutor.ProgressMonitor monitor = null;
		if (verbose)
		{
			monitor = new SearchExecutor.ProgressMonitor()
			{
				final int totalNumOfBars = 25;
				int numOfBarsWritten = 0;
				public void setPercentCompleted(double percent)
				{
					int numOfBars = (int) (totalNumOfBars * percent);
					while (numOfBarsWritten < numOfBars)
					{
						System.err.print(">"); System.err.flush();
						numOfBarsWritten++;
					}
				}
			};
		}

		//
		// Stage 2.E: Run SearchExecutor
		//

		if (verbose)
		{
			System.err.println("Starting search in " + numOfThreads + " threads...");
			System.err.println("  |-------------------------|");
			System.err.print("   "); System.err.flush();
		}
		List<List<Graph<Activity,String>>> searchTrials = SearchExecutor.execute(network, search, score, randomize, duplicate, monitor, numOfTrials, numOfThreads);
		if (verbose) System.err.println();
		if (verbose) System.err.println("Search complete.");

		//
		// Stage 2.F: Collect random trials
		//
		
		List<Graph<Activity,String>> modules = searchTrials.remove(0);
		Trials trials = new Trials(searchTrials);
		searchTrials = null;

		//
		// Stage 3.A: Run ST1
		//

		if (verbose) System.err.print("Running ST1..."); System.err.flush();
		ST1Filter<Activity,String> st1Filter = new ST1Filter<Activity,String>(trials, ST1PValCutoff);
		modules = st1Filter.filter(modules);
		if (verbose) System.err.println(" done. Number of modules passed: " + modules.size());

		//
		// Stage 3.B: Run ST2
		//

		if (verbose) System.err.print("Running ST2..."); System.err.flush();
		ST2Filter<Activity,String> st2Filter = null;
		switch(scoreModel)
		{
			case MI: st2Filter = new ST2Filter<Activity,String>(trials, ST2Filter.Distribution.GAMMA, ST2PValCutoff); break;
			case T: st2Filter = new ST2Filter<Activity,String>(trials, ST2Filter.Distribution.NORMAL, ST2PValCutoff); break;
		}
		modules = st2Filter.filter(modules);
		if (verbose) System.err.println(" done. Number of modules passed: " + modules.size());

		//
		// Stage 3.C: Run ST3
		//

		if (verbose) System.err.print("Running ST3..."); System.err.flush();
		ST3Filter st3Filter = new ST3Filter(network, matrix, score, numOfST3Randomizations, ST3PValCutoff);
		modules = st3Filter.filter(modules);
		if (verbose) System.err.println(" done. Number of modules passed: " + modules.size());

		//
		// Stage 4: Output
		//

		output.println("Start\tModule Score\tST1 PValue\tST2 PValue\tST3 PValue\tGenes");
		for(Graph<Activity,String> module : modules)
		{
			final IntIterator iterator = module.nodes();
			final int startNode = iterator.next();
			output.format("%s\t%7f\t%7f\t%7f\t%7f\t", module.nodeObject(startNode).name, module.score(), st1Filter.pValue(module), st2Filter.pValue(module), st3Filter.pValue(module));
			output.print(module.nodeObject(startNode).name + ' ');
			while (iterator.hasNext())
			{
				final int member = iterator.next();
				output.print(module.nodeObject(member).name);
				output.print(' ');
			}
			output.println();
		}
		output.close();
	}
	
	private static void parseCommandLine(String[] args)
	{
		CommandLineParser parser = new PosixParser();
		CommandLine line = null;
		Options options = createCommandLineOptions();

		try
		{
			line = parser.parse(options, args);
		}
		catch (ParseException e)
		{
			printHelpAndExit("Could not parse command line: " + e.getMessage(), options);
		}

		if (line.hasOption("h"))
			printHelpAndExit(null, options);

		if (line.hasOption("s"))
		{
			if (line.getOptionValue("s").equals("MI"))
				scoreModel = ScoreModel.MI;
			else if (line.getOptionValue("s").equals("T"))
				scoreModel = ScoreModel.T;
			else
				printHelpAndExit("Invalid score model specified: " + line.getOptionValue("s"), options);
		}

		if (line.hasOption("1"))
		{
			try
			{
				ST1PValCutoff = Double.parseDouble(line.getOptionValue("1"));
			}
			catch (NumberFormatException e)
			{
				printHelpAndExit("Invalid ST1 p-value cutoff specified: " + line.getOptionValue("1"), options);
			}
		}

		if (line.hasOption("2"))
		{
			try
			{
				ST2PValCutoff = Double.parseDouble(line.getOptionValue("2"));
			}
			catch (NumberFormatException e)
			{
				printHelpAndExit("Invalid ST2 p-value cutoff specified: " + line.getOptionValue("2"), options);
			}
		}

		if (line.hasOption("3"))
		{
			try
			{
				ST3PValCutoff = Double.parseDouble(line.getOptionValue("3"));
			}
			catch (NumberFormatException e)
			{
				printHelpAndExit("Invalid ST3 p-value cutoff specified: " + line.getOptionValue("3"), options);
			}
		}

		if (line.hasOption("t"))
		{
			try
			{
				numOfTrials = Integer.parseInt(line.getOptionValue("t"));
			}
			catch (NumberFormatException e)
			{
				printHelpAndExit("Invalid number of random trials specified: " + line.getOptionValue("t"), options);
			}
		}

		if (line.hasOption("q"))
		{
			try
			{
				numOfST3Randomizations = Integer.parseInt(line.getOptionValue("q"));
			}
			catch (NumberFormatException e)
			{
				printHelpAndExit("Invalid number of random trials specified: " + line.getOptionValue("q"), options);
			}
		}
		if (line.hasOption("m"))
		{
			try
			{
				maxModuleSize = Integer.parseInt(line.getOptionValue("m"));
			}
			catch (NumberFormatException e)
			{
				printHelpAndExit("Invalid max module size specified: " + line.getOptionValue("m"), options);
			}
		}

		if (line.hasOption("r"))
		{
			try
			{
				maxRadius = Integer.parseInt(line.getOptionValue("r"));
			}
			catch (NumberFormatException e)
			{
				printHelpAndExit("Invalid max radius specified: " + line.getOptionValue("r"), options);
			}

			if (maxRadius < 1)
				maxRadius = Integer.MAX_VALUE;
		}

		if (line.hasOption("d"))
		{
			try
			{
				maxNodeDegree = Integer.parseInt(line.getOptionValue("d"));
			}
			catch (NumberFormatException e)
			{
				printHelpAndExit("Invalid max node degree specified: " + line.getOptionValue("d"), options);
			}
		}
		
		if (line.hasOption("i"))
		{
			try
			{
				minImprovement = Double.parseDouble(line.getOptionValue("i"));
			}
			catch (NumberFormatException e)
			{
				printHelpAndExit("Invalid min improvement specified: " + line.getOptionValue("i"), options);
			}
		}
		
		if (line.hasOption("o"))
		{
			if (!line.getOptionValue("o").equals("-"))
			{
				try
				{
					output = new PrintStream(new File(line.getOptionValue("o")));
				}
				catch (FileNotFoundException e)
				{
					System.err.println("Could not create output file: " + line.getOptionValue("o"));
					System.exit(1);
				}
			}
		}

		if (line.hasOption("v"))
			verbose = true;
		
		String[] files = line.getArgs();
		if (files.length < 2)
			printHelpAndExit("Class, matrix, or network files were not specified", options);

		classFilePath = files[0];
		matrixFilePath = files[1];
		networkFilePath = files[2];
	}
	
	private static Options createCommandLineOptions()
	{
		Options options = new Options();
		options.addOption("h", "help", false, "Print this command line options help");
		options.addOption(OptionBuilder
					.withLongOpt("score")
					.withDescription("the score model to use; default: MI")
					.withValueSeparator(' ')
					.withArgName("\"MI\" or \"T\"")
					.hasArg()
					.create("s"));
		options.addOption(OptionBuilder
					.withLongOpt("st1cutoff")
					.withDescription("p-value cutoff for ST1 (must be between 0.0 and 1.0); default: " + ST1PValCutoff)
					.withValueSeparator(' ')
					.withArgName("double")
					.hasArg()
					.create("1"));
		options.addOption(OptionBuilder
					.withLongOpt("st2cutoff")
					.withDescription("p-value cutoff for ST2 (must be between 0.0 and 1.0); default: " + ST2PValCutoff)
					.withValueSeparator(' ')
					.withArgName("double")
					.hasArg()
					.create("2"));
		options.addOption(OptionBuilder
					.withLongOpt("st3cutoff")
					.withDescription("p-value cutoff for ST3 (must be between 0.0 and 1.0); default: " + ST3PValCutoff)
					.withValueSeparator(' ')
					.withArgName("double")
					.hasArg()
					.create("3"));
		options.addOption(OptionBuilder
					.withLongOpt("st3trials")
					.withDescription("number of randomizations for ST3 (must be a positive integer); default: " + numOfST3Randomizations)
					.withValueSeparator(' ')
					.withArgName("integer")
					.hasArg()
					.create("3t"));
		options.addOption(OptionBuilder
					.withLongOpt("trials")
					.withDescription("number of random trials (must be a positive integer); default: " + numOfTrials)
					.withValueSeparator(' ')
					.withArgName("integer")
					.hasArg()
					.create("t"));
		options.addOption(OptionBuilder
					.withLongOpt("maxModuleSize")
					.withDescription("max number of nodes in a module (must be a positive integer); default: " + maxModuleSize)
					.withValueSeparator(' ')
					.withArgName("integer")
					.hasArg()
					.create("m"));
		options.addOption(OptionBuilder
					.withLongOpt("maxRadius")
					.withDescription("max distance from start node to any node in the module, or pass any integer less than 1 for infinite radius (must be an integer); default: " + maxRadius)
					.withValueSeparator(' ')
					.withArgName("integer")
					.hasArg()
					.create("r"));
		options.addOption(OptionBuilder
					.withLongOpt("maxNodeDegree")
					.withDescription("max degree of all nodes in a module (must be a positive integer); default: " + maxNodeDegree)
					.withValueSeparator(' ')
					.withArgName("integer")
					.hasArg()
					.create("d"));
		options.addOption(OptionBuilder
					.withLongOpt("minImprovement")
					.withDescription("min percentage of score improvement when adding a node to a module (must be between 0.0 and 1.0); default: " + minImprovement)
					.withValueSeparator(' ')
					.withArgName("double")
					.hasArg()
					.create("i"));
		options.addOption(OptionBuilder
					.withLongOpt("output")
					.withDescription("filepath to write results to; default: stdout")
					.withValueSeparator(' ')
					.withArgName("filepath")
					.hasArg()
					.create("o"));
		options.addOption(OptionBuilder
					.withLongOpt("verbose")
					.withDescription("write detailed progress to stderr; default: " + verbose)
					.create("v"));
		return options;
	}

	private static void printHelpAndExit(String message, Options options)
	{
		if (message != null)
			System.err.println(message);
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("pinnaclez [OPTIONS] class_file matrix_file network_file", options);
		System.exit(1);
	}
}
