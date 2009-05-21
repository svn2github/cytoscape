//ActivePathsCommandLineParser
//------------------------------------------------------------------------------
// $Revision: 3668 $
// $Date: 2006-03-06 21:00:03 -0800 (Mon, 06 Mar 2006) $
// $Author: rmkelley $xx
//------------------------------------------------------------------------------
package csplugins.jActiveModules;
//------------------------------------------------------------------------------
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import csplugins.jActiveModules.data.ActivePathFinderParameters;
//------------------------------------------------------------------------------
public class ActivePathsCommandLineParser {

	private ActivePathFinderParameters activePathParameters = new ActivePathFinderParameters();
	private boolean inputsError = false;
	private String tableFileName = null;

	// ------------------------------------------------------------------------------
	public ActivePathsCommandLineParser(String[] args) {
		this.parseArgs(args);
	}
	// ------------------------------------------------------------------------------

//	public boolean activePathParametersPresent() {
//		return activePathParametersPresent;
//	}

	public boolean haveInputsError() {
		return inputsError;
	}

//	public boolean shouldRunActivePaths() {
//		return (run && activePathParametersPresent() && !haveInputsError());
//	}
//
//	public boolean shouldSaveAndExit() {
//		return saveAndExit;
//	}

	public String getTableFileName() {
		return tableFileName;
	}

	public ActivePathFinderParameters getActivePathFinderParameters() {
		return activePathParameters;
	}

	// ------------------------------------------------------------------------------
	public void usage() {
		System.err.println("activePaths command line arguments:");
		System.err.println("    --APt0 <double>         initial temperature");
		System.err.println("    --APtf <double>         final temperature");
		System.err.println("    --APha <double>         hub adjustment");
		System.err.println("    --APni <integer>        number of iterations");
		System.err.println("    --APnp <integer>        number of paths");
		System.err.println("    --APdi <integer>        display interval");
		System.err.println("    --APhs <integer>        minimum hub size");
		System.err.println("    --APrs <integer>        random seed");
		System.err.println("    --APsd <integer>        search depth");
		System.err.println("    --APmd <integer>        max depth");
		System.err.println("    --APqu <true|false>     apply quenching");
		System.err.println("    --APed <true|false>     edges not nodes");
		System.err
				.println("    --APmcb <true|false>    use mc correction at all");
		System.err.println("    --APmc <string>         monte carlo file name");
		System.err
				.println("    --APsig <double>        significance threshold");
		System.err
				.println("    --APreg <true|false>    regional scoring true/false");
		System.err
				.println("    --APexit                save and exit after run");
		System.err.println("    --APhelp                prints this help");
		System.err.println("    --APrun                 run active modules");
		System.err.println("    --APtable <string>      table file name");
		System.err
				.println("    --APmt <int>            maximum number of threads");
		System.err.println("    --APanneal               use simulated annealing");
	}

	// to those who would modify the parseArgs() code below:
	// ---------------------------------------------------------------
	// LongOpt.REQUIRED_ARGUMENT means that there will be an argument
	// after the command line flag. In the example below,
	// --APt0 2.0
	// 2.0 is the argument. If you want getOptarg() to work, you have
	// to specify LongOpt.REQUIRED_ARGUMENT. If, on the other hand,
	// the flag takes no additional argument, use LongOpt.NO_ARGUMENT.
	//
	private void parseArgs(String[] args) {
		if (args == null || args.length == 0) {
			return;
		}

		LongOpt[] longOpts = new LongOpt[21];
		longOpts[0] = new LongOpt("APt0", LongOpt.REQUIRED_ARGUMENT, null, 0); // initial
																				// temp
		longOpts[1] = new LongOpt("APtf", LongOpt.REQUIRED_ARGUMENT, null, 1); // final
																				// temp
		longOpts[2] = new LongOpt("APha", LongOpt.REQUIRED_ARGUMENT, null, 2); // hub
																				// adj
		longOpts[3] = new LongOpt("APni", LongOpt.REQUIRED_ARGUMENT, null, 3); // iterations
		longOpts[4] = new LongOpt("APnp", LongOpt.REQUIRED_ARGUMENT, null, 4); // number
																				// of
																				// paths
		longOpts[5] = new LongOpt("APdi", LongOpt.REQUIRED_ARGUMENT, null, 5); // display
																				// interval
		longOpts[6] = new LongOpt("APhs", LongOpt.REQUIRED_ARGUMENT, null, 6); // minHubSize
		longOpts[7] = new LongOpt("APrs", LongOpt.REQUIRED_ARGUMENT, null, 7); // random
																				// seed
		longOpts[8] = new LongOpt("APsd", LongOpt.REQUIRED_ARGUMENT, null, 8); // search
																				// depth
		longOpts[9] = new LongOpt("APmd", LongOpt.REQUIRED_ARGUMENT, null, 9); // max
																				// depth
		longOpts[10] = new LongOpt("APqu", LongOpt.REQUIRED_ARGUMENT, null, 10); // quenching
		longOpts[11] = new LongOpt("APed", LongOpt.REQUIRED_ARGUMENT, null, 11); // edge
																					// annealing
		longOpts[12] = new LongOpt("APmcb", LongOpt.REQUIRED_ARGUMENT, null, 12); // monte
																					// carlo
																					// (y/n)
		longOpts[13] = new LongOpt("APmc", LongOpt.REQUIRED_ARGUMENT, null, 13); // monte
																					// carlo
																					// file
		longOpts[14] = new LongOpt("APreg", LongOpt.REQUIRED_ARGUMENT, null, 14); // monte
																					// carlo
																					// file
		longOpts[15] = new LongOpt("APhelp", LongOpt.NO_ARGUMENT, null, 15); // help
		longOpts[16] = new LongOpt("APexit", LongOpt.NO_ARGUMENT, null, 16); // save
																				// and
																				// exit
		longOpts[17] = new LongOpt("APrun", LongOpt.NO_ARGUMENT, null, 17); // don't
																			// run
		longOpts[18] = new LongOpt("APtable", LongOpt.REQUIRED_ARGUMENT, null,
				18); // file for table
		longOpts[19] = new LongOpt("APmt", LongOpt.REQUIRED_ARGUMENT, null, 19);
		longOpts[20] = new LongOpt("APanneal",LongOpt.NO_ARGUMENT,null,20);

		Getopt g = new Getopt("activeModules", args, "", longOpts);
		g.setOpterr(false); // We'll do our own error handling
		String tmp;
		boolean helpRequested = false;

		int c;
		while ((c = g.getopt()) != -1) {
			switch (c) {
				case 0 :
					tmp = g.getOptarg();
					try {
						activePathParameters.setInitialTemperature(Double
								.parseDouble(tmp));
					} catch (Exception e) {
						inputsError = true;
						System.err.println("illegal value for --APt0: " + tmp);
					}
					break;
				case 1 :
					tmp = g.getOptarg();
					try {
						activePathParameters.setFinalTemperature(Double
								.parseDouble(tmp));
					} catch (Exception e) {
						inputsError = true;
						System.err.println("illegal value for --APtf: " + tmp);
					}
					break;
				case 2 :
					tmp = g.getOptarg();
					try {
						activePathParameters.setHubAdjustment(Double
								.parseDouble(tmp));
						;
					} catch (Exception e) {
						inputsError = true;
						System.err.println("illegal value for --APha: " + tmp);
					}
					break;
				case 3 :
					tmp = g.getOptarg();
					try {
						activePathParameters.setTotalIterations(Integer
								.parseInt(tmp));
					} catch (Exception e) {
						inputsError = true;
						System.err.println("illegal value for --APni: " + tmp);
					}
					break;
				case 4 :
					tmp = g.getOptarg();
					try {
						activePathParameters.setNumberOfPaths(Integer
								.parseInt(tmp));
					} catch (NumberFormatException e) {
						inputsError = true;
						System.err.println("illegal value for --APnp: " + tmp);
					}
					break;
				case 5 :
					tmp = g.getOptarg();
					try {
						activePathParameters.setDisplayInterval(Integer
								.parseInt(tmp));
					} catch (Exception e) {
						inputsError = true;
						System.err.println("illegal value for --APdi: " + tmp);
					}
					break;
				case 6 :
					tmp = g.getOptarg();
					try {
						activePathParameters.setMinHubSize(Integer
								.parseInt(tmp));
					} catch (NumberFormatException e) {
						inputsError = true;
						System.err.println("illegal value for --APhs: " + tmp);
					}
					break;
				case 7 :
					tmp = g.getOptarg();
					try {
						activePathParameters.setRandomSeed(Integer
								.parseInt(tmp));
					} catch (Exception e) {
						inputsError = true;
						System.err.println("illegal value for --APrs: " + tmp);
					}
					break;
				case 8 :
					tmp = g.getOptarg();
					try {
						activePathParameters.setSearchDepth(Integer
								.parseInt(tmp));
					} catch (NumberFormatException e) {
						inputsError = true;
						System.err.println("illegal value for --APsd: " + tmp);
					}
					break;
				case 9 :
					tmp = g.getOptarg();
					try {
						activePathParameters.setMaxDepth(Integer.parseInt(tmp));
					} catch (Exception e) {
						inputsError = true;
						System.err.println("illegal value for --APmd: " + tmp);
					}
					break;
				case 10 :
					tmp = g.getOptarg();
					if (tmp.equals("true")) {
						activePathParameters.setToQuench(true);
					} else if (tmp.equals("false")) {
						activePathParameters.setToQuench(false);
					} else {
						inputsError = true;
						System.err.println("illegal value for --APqu: " + tmp);
					}
					break;
				case 11 :
					tmp = g.getOptarg();
					if (tmp.equals("true")) {
						// activePathParameters.setEdgesNotNodes(true);
						// activePathParametersPresent = true;
					} else if (tmp.equals("false")) {
						// activePathParameters.setEdgesNotNodes(false);
						// activePathParametersPresent = true;
					} else {
						inputsError = true;
						System.err.println("illegal value for --APed: " + tmp);
					}
					break;
				case 12 :
					tmp = g.getOptarg();
					if (tmp.equals("true")) {
						activePathParameters.setMCboolean(true);
					} else if (tmp.equals("false")) {
						activePathParameters.setMCboolean(false);
					} else {
						inputsError = true;
						System.err.println("illegal value for --APmcb: " + tmp);
					}
					break;
				case 13 :
					tmp = g.getOptarg();
					activePathParameters.setToUseMCFile(true);
					activePathParameters.setMcFileName(tmp);
					break;
				case 14 :
					tmp = g.getOptarg();
					if (tmp.equals("true")) {
						activePathParameters.setRegionalBoolean(true);
					} else if (tmp.equals("false")) {
						activePathParameters.setRegionalBoolean(false);
					} else {
						inputsError = true;
						System.err.println("illegal value for --APreg: " + tmp);
					}
					break;
				case 15 :
					helpRequested = true;
					break;
				case 16 :
					//saveAndExit = true;
					activePathParameters.setExit(true);
					break;
				case 17 :
					activePathParameters.setRun(true);
					break;
				case 18 :
					tableFileName = g.getOptarg();
					break;
				case 19 :
					tmp = g.getOptarg();
					try {
						activePathParameters.setMaxThreads(Integer
								.parseInt(tmp));
					} catch (Exception e) {
						inputsError = true;
						System.err.println("illegal value for --APmt: " + tmp);
					}
					break;
				case 20 :
					activePathParameters.setGreedySearch(false);
					break;
				default :
					break;
			}
		}

		if (helpRequested || inputsError) {
			usage();
		}
	}
}
