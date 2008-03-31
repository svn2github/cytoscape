package clusterMaker.algorithms.FORCE;




import java.io.FileInputStream;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

import de.layclust.layout.acc.ACCConfig;
import de.layclust.layout.forcend.FORCEnDLayoutConfig;
import de.layclust.layout.geometric_clustering.GeometricClusteringConfig;
import de.layclust.taskmanaging.InvalidInputFileException;
import de.layclust.taskmanaging.InvalidTypeException;
import de.layclust.taskmanaging.TaskConfig;
import de.layclust.taskmanaging.gui.ConfigurationsGUI;
import de.layclust.taskmanaging.io.ArgsParseException;
import de.layclust.taskmanaging.io.ArgsUtility;
import de.layclust.taskmanaging.io.Console;
import de.layclust.taskmanaging.io.InfoFile;
import de.layclust.taskmanaging.io.Outfile;

/**
 * This class is the main entry point to the program. It is the class that is started
 * from the console. It is possible to enter the parameters -gui to start the {@link ConfigurationsGUI},
 * or -help for usage information or normally with necessary parameters.
 * 
 * @author Sita Lange
 *
 */
public class FORCEnD_ACC {

	/**
	 * The main method for the FORCEnD_ACC program. Starts the program
	 * appropriately.
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		
		/* check if no input is given */
		if(args.length ==0){
			System.out.println("ERROR: Please define at least an input file/directory and an " +
					"output file for the results. Use -help for more details or see the " +
					"respective documentation.\n\n");
			System.out.println(ArgsUtility.createUsage().toString());
			System.exit(-1);
		}
		
		/* print usage */
		if ((args.length == 1) && ((args[0].trim().equalsIgnoreCase("-help")) || 
				(args[0].trim().equalsIgnoreCase("--help")))) {
			System.out.println(ArgsUtility.createUsage().toString());
			System.exit(-1);
		}		
		
		/* start gui if wanted */
		if ((args.length == 1) && (args[0].trim().equalsIgnoreCase("-gui"))) {
			new ConfigurationsGUI();
//			new SimpleTree();
		}
		
		/* start with parameters from console */
		else{
			try {
				new Console(args);
			} catch (InvalidInputFileException e) {
				System.out.println("ERROR: An invalid file/path name was given.");
				e.printStackTrace();
				System.exit(-1);
			} catch (ArgsParseException e) {
				System.out.println("ERROR: please see usage details!");
				System.out.println(e.getMessage());
				System.out.println(ArgsUtility.createUsage().toString());
			}
		}
		
		/* create info file */
		if(TaskConfig.info){
			InfoFile info = new InfoFile();
			info.instantiateFile(TaskConfig.infoPath);
			InfoFile.appendHeaderToProjectDetails("test");
			InfoFile.apppendLnToProjectDetails("the first information");
			info.createAndCloseInfoFile();
		}

		
	}
}
