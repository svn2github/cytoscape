//JarLoaderCommandLineParser
//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.jarLoader;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import cytoscape.*;

public class JarLoaderCommandLineParser {
    
    private boolean activePathParametersPresent = false;
    JarPluginDirectoryAction jpda;
    CytoscapeWindow cw;
    
    public JarLoaderCommandLineParser(String[] args, CytoscapeWindow cw) {
	this.cw=cw;
	jpda = new JarPluginDirectoryAction(cw);
        this.parseArgs(args);
    }
    
    public void usage() {
        System.err.println("jarLoader command line arguments:");
        System.err.println("    --JLD  <string>   directory for .jar files");
        System.err.println("    --JLH             prints this help");
    }

    private void parseArgs(String[] args) {
        if (args == null || args.length == 0) {return;}
                
        LongOpt [] longOpts = new LongOpt [2];
        longOpts[0] = new LongOpt ("JLD",  LongOpt.REQUIRED_ARGUMENT, null, 0);
        longOpts[1] = new LongOpt ("JLH",  LongOpt.NO_ARGUMENT, null, 1);

        Getopt g = new Getopt ("jarLoader", args, "", longOpts);
        g.setOpterr (false); // We'll do our own error handling
        String tmp;
        boolean helpRequested = false;
        
        int c;
        while ((c = g.getopt ()) != -1) {
            switch (c) {
                case 0:
                    tmp = g.getOptarg();
		    // try to load the jars.
		    System.out.println("Command line to load jars from "+tmp);
		    jpda.setDir(tmp);
		    jpda.tryDirectory();
                    break;
                case 1:
                    tmp = g.getOptarg();
                    helpRequested = true;
                    break;
                default:
                    break;
            }
        }
        
        if (helpRequested) {usage();}
    }
}

