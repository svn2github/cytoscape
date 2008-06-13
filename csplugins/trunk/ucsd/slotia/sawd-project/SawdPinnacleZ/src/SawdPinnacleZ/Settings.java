package SawdPinnacleZ;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.PrintStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.*;
import pinnaclez.*;
import modlab.*;
import oiler.*;

class Settings
{
        public enum ScoreModel { MI, T }

        public ScoreModel scoreModel = ScoreModel.MI;
        public int maxModuleSize = 20;
        public int maxRadius = 2;
        public int maxNodeDegree = 300;
        public double minImprovement = 0.05;
        public double ST1PValCutoff = 0.05;
        public double ST2PValCutoff = 0.05;
        public double ST3PValCutoff = 0.00005;
        public int numOfTrials = 100;
        public int numOfST3Randomizations = 20000;
        public String server, port, classFilePath, matrixFilePath, networkFilePath;
        public PrintStream output = System.out;
        public boolean verbose = false;
        public boolean reset = false;

	public Map<String,Integer> classMap = null;
	public ExpressionMatrix matrix = null;
	public Graph<Activity,String> network = null;
	public Search<Activity,String> search = null;
	public AbstractActivityScore score = null;
	public Randomize<Activity,String> randomize = null;

	public Settings(String[] args) throws SettingsParseException
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
			throw new SettingsParseException("Could not parse command line: " + e.getMessage(), options);
		}

		if (line.hasOption("h"))
			throw new SettingsParseException(null, options);

		if (line.hasOption("s"))
		{
			if (line.getOptionValue("s").equals("MI"))
				scoreModel = ScoreModel.MI;
			else if (line.getOptionValue("s").equals("T"))
				scoreModel = ScoreModel.T;
			else
				throw new SettingsParseException("Invalid score model specified: " + line.getOptionValue("s"), options);
		}

		if (line.hasOption("1"))
		{
			try
			{
				ST1PValCutoff = Double.parseDouble(line.getOptionValue("1"));
			}
			catch (NumberFormatException e)
			{
				throw new SettingsParseException("Invalid ST1 p-value cutoff specified: " + line.getOptionValue("1"), options);
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
				throw new SettingsParseException("Invalid ST2 p-value cutoff specified: " + line.getOptionValue("2"), options);
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
				throw new SettingsParseException("Invalid ST3 p-value cutoff specified: " + line.getOptionValue("3"), options);
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
				throw new SettingsParseException("Invalid number of random trials specified: " + line.getOptionValue("t"), options);
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
				throw new SettingsParseException("Invalid number of random trials specified: " + line.getOptionValue("q"), options);
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
				throw new SettingsParseException("Invalid max module size specified: " + line.getOptionValue("m"), options);
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
				throw new SettingsParseException("Invalid max radius specified: " + line.getOptionValue("r"), options);
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
				throw new SettingsParseException("Invalid max node degree specified: " + line.getOptionValue("d"), options);
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
				throw new SettingsParseException("Invalid min improvement specified: " + line.getOptionValue("i"), options);
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
		if (line.hasOption("R"))
			reset = true;
		
		String[] files = line.getArgs();
		if (files.length < 4)
			throw new SettingsParseException("Not enough parameters were specified", options);

		server = files[0];
		port = files[1];
		classFilePath = files[2];
		matrixFilePath = files[3];
		networkFilePath = files[4];
	}

	private Options createCommandLineOptions()
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
		options.addOption(OptionBuilder
					.withLongOpt("reset")
					.withDescription("reset the server; default: " + reset)
					.create("R"));
		return options;
	}

	public class SettingsParseException extends Exception
	{
		Options options;

		public SettingsParseException(String message, Options options)
		{
			super(message);
			this.options = options;
		}

		public Options getOptions()
		{
			return options;
		}
	}
}
