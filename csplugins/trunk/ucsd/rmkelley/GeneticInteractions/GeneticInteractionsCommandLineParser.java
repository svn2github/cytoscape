//ActivePathsCommandLineParser
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$xx
//------------------------------------------------------------------------------
package csplugins.ucsd.rmkelley.GeneticInteractions;
//------------------------------------------------------------------------------
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
//------------------------------------------------------------------------------
public class GeneticInteractionsCommandLineParser {
	//configuration options
	private int max = 4;
	private int min = 3;
	private int thread_count = 1;
	private boolean debug = false;
	private int results = 50;
	private boolean run = false;

	//------------------------------------------------------------------------------
	public GeneticInteractionsCommandLineParser(String[] args) {
		this.parseArgs(args);
	}
	//------------------------------------------------------------------------------

	public int getThreadCount(){
		return thread_count;
	}

	public int getResults(){
		return results;
	}

	public int getMax(){
		return max;
	}

	public boolean getDebug(){
		return debug;
	}

	public int getMin(){
		return min;
	}

	public boolean getRun(){
		return run;
	}

	//------------------------------------------------------------------------------
	public void usage() {
		System.err.println("Dual Layout Command Line Arguments:");
		System.err.println("    --GIthreads <int>	number of threads (default is 1)");
		System.err.println("    --GImax <int>		max depth of lethal path (default is 4)");
		System.err.println("    --GImin <int>		min depth of lethal path (default is 3)");
		System.err.println("	--GIresults <int>	number of results to display (default is 50)"); 
		System.err.println("	--GIdebug		display debugging information"); 
		System.err.println("    --GIhelp		Display this usage information");

	}

	private void parseArgs(String[] args) {
		if (args == null || args.length == 0) {return;}

		LongOpt [] longOpts = new LongOpt [6];
		longOpts[0] = new LongOpt ("GIthreads",  LongOpt.REQUIRED_ARGUMENT, null, 0); // initial temp
		longOpts[1] = new LongOpt ("GImax",  LongOpt.REQUIRED_ARGUMENT, null, 1); // final temp
		longOpts[2] = new LongOpt ("GImin",  LongOpt.REQUIRED_ARGUMENT, null, 2); // final temp
		longOpts[3] = new LongOpt ("GIresults",  LongOpt.REQUIRED_ARGUMENT, null, 3); // final temp
		longOpts[4] = new LongOpt ("GIdebug",  LongOpt.NO_ARGUMENT, null, 4); // hub adj
		longOpts[5] = new LongOpt ("GIhelp",  LongOpt.NO_ARGUMENT, null, 5); // help

		Getopt g = new Getopt ("GeneticInteractions", args, "", longOpts);
		g.setOpterr (false); // We'll do our own error handling
		String tmp;
		int c;
		while ((c = g.getopt ()) != -1) {
			switch (c) {
				case 0:
					try{
						thread_count = (new Integer(g.getOptarg())).intValue();
						run = true;
					}catch(Exception e){
						System.err.println("Problem with thread argument");
						usage();
						System.exit(-1);
					}
					break;
				case 1:
					try{
						max = (new Integer(g.getOptarg())).intValue();
						run = true;
					}catch(Exception e){
						System.err.println("Problem with max argument");
						usage();
						System.exit(-1);
					}
					break;
				case 2:
					try{
						min = (new Integer(g.getOptarg())).intValue();
						run = true;
					}catch(Exception e){
						System.err.println("Problem with min argument");
						usage();
						System.exit(-1);
					}
					break;
				case 3:
					try{
						results = (new Integer(g.getOptarg())).intValue();
						run = true;
					}catch(Exception e){
						System.err.println("Problem with results argument");
						usage();
						System.exit(-1);
					}
					break;
				case 4:
					debug = true;
					run = true;
					break;
				case 5:
					usage();
					System.exit(0);
					break;
				default:
					break;
			}
		}
	}
}

