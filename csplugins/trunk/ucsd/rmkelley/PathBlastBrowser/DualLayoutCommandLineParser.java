//ActivePathsCommandLineParser
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$xx
//------------------------------------------------------------------------------
package csplugins.ucsd.rmkelley.PathBlastBrowser.Layout;
//------------------------------------------------------------------------------
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
//------------------------------------------------------------------------------
public class DualLayoutCommandLineParser {
    //configuration options
    private boolean color = false;
    private String filename;
    private boolean run = false;
    private boolean edges = false;
    private boolean exit = false;
    private boolean save = false;
    
//------------------------------------------------------------------------------
    public DualLayoutCommandLineParser(String[] args) {
        this.parseArgs(args);
    }
//------------------------------------------------------------------------------
   
	public boolean applyColor(){
   		return color;
   	}
	
	public boolean run(){
		return run;
	}

	public boolean exit(){
		return exit;
	}

	public boolean addEdges(){
		return edges;
	}

	public String getGMLname(){
		return filename;
	}

	public boolean save(){
		return save;
	}
    
//------------------------------------------------------------------------------
    public void usage() {
        System.err.println("Dual Layout Command Line Arguments:");
        System.err.println("    --DLcolor		apply color to nodes");
        System.err.println("    --DLgml <string>	specify filename to save graph");
	System.err.println("	--DLrun			Run after loading"); 
	System.err.println("	--DLedges		Add edges between homologies");
	System.err.println("	--DLexit		Exit after running");
	System.err.println("    --DLhelp		Display this usage information");
  
    }

    private void parseArgs(String[] args) {
        if (args == null || args.length == 0) {return;}
                
        LongOpt [] longOpts = new LongOpt [6];
        longOpts[0] = new LongOpt ("DLcolor",  LongOpt.NO_ARGUMENT, null, 0); // initial temp
        longOpts[1] = new LongOpt ("DLgml",  LongOpt.REQUIRED_ARGUMENT, null, 1); // final temp
        longOpts[2] = new LongOpt ("DLrun",  LongOpt.NO_ARGUMENT, null, 2); // hub adj
        longOpts[3] = new LongOpt ("DLedges", LongOpt.NO_ARGUMENT, null,3);
	longOpts[4] = new LongOpt ("DLexit", LongOpt.NO_ARGUMENT,null,4);
	longOpts[5] = new LongOpt ("DLhelp",  LongOpt.NO_ARGUMENT, null, 5); // help

        Getopt g = new Getopt ("DualLayout", args, "", longOpts);
        g.setOpterr (false); // We'll do our own error handling
        String tmp;
	int c;
        while ((c = g.getopt ()) != -1) {
            switch (c) {
	    case 0:
		color = true;
		run = true;
		break;
	    case 1:
		filename = g.getOptarg();
		save = true;
		break;
	    case 2:
		run = true;
		break;
	    case 3:
		edges = true;
		break;
	    case 4:
	    	exit = true;
		break;
	    case 5:
	    	usage();
	    default:
		break;
            }
        }
    }
}

