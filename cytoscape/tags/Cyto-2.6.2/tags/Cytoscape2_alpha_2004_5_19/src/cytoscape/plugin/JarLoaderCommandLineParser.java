//JarLoaderCommandLineParser
//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.plugin;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import cytoscape.CytoscapeObj;
import cytoscape.actions.LoadPluginDirectoryAction;
import cytoscape.actions.LoadPluginListAction;
//----------------------------------------------------------------------------
/**
 * This class searches the command-line arguments for arguments specifying
 * directories to search for plugins. The option "--JLD <string>" is used
 * to specify a plugin directory. The option "--JLD <URL> is used to specify
 * the URL of a plugin jar, while the option "--JLL <URL>" is used to specify
 * a list of plugin jar URLs. Finally, the option "--JLH" prints a usage
 * statement for these options.
 *
 * If a directory is found, it is passed to an instance of LoadPLuginDirectoryAction.
 * If a plugin list is found, it is passed to an instance of LoadPluginListAction.
 */
public class JarLoaderCommandLineParser {
    protected CytoscapeObj cyObj;
    protected StringBuffer messageBuffer;

    /**
     * creates an instance and links it to the shared plugin registry.
     * @param cyObj
     */
    public JarLoaderCommandLineParser(CytoscapeObj cyObj) {
        this.cyObj = cyObj;
        this.messageBuffer = new StringBuffer();
    }

    /**
     * prints a usage message.
     */
    public void usage() {
        System.err.println("jarLoader command line arguments:");
        System.err.println("    --JLD  <string>   local directory containing .jar files");
        System.err.println("    --JLW  <URL>      URL of a .jar file to load");
        System.err.println("    --JLL  <URL>      URL of a list of .jar files to load");
        System.err.println("    --JLH             prints this help");
    }

    /**
     * examines the arguments for --JLD or --JLH. If found, they are
     * processed immediately.
     * @param args
     */
    public void parseArgs(String[] args) {
        if (args == null || args.length == 0) {return;}

        LongOpt [] longOpts = new LongOpt [4];
        longOpts[0] = new LongOpt ("JLD",  LongOpt.REQUIRED_ARGUMENT, null, 0);
        longOpts[1] = new LongOpt ("JLW",  LongOpt.REQUIRED_ARGUMENT, null, 1);
        longOpts[2] = new LongOpt ("JLL",  LongOpt.REQUIRED_ARGUMENT, null, 2);        
        longOpts[3] = new LongOpt ("JLH",  LongOpt.NO_ARGUMENT, null, 3);

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
                    messageBuffer.append("Command line to load jars from directory: "+tmp);
                    LoadPluginDirectoryAction lpda = new LoadPluginDirectoryAction(cyObj);;
                    lpda.setDir(tmp);
                    lpda.tryDirectory();
                    break;
                case 1:
                    tmp = g.getOptarg();
                    JarClassLoader jcl;
                    try {
                        jcl = new JarClassLoader(tmp, cyObj);
                        jcl.loadRelevantClasses();
                    } catch (Exception e) {
                        System.err.println("Error loading jar: " + e.getMessage());
                    }
                    break;
                case 2:
                    tmp = g.getOptarg();
                    LoadPluginListAction lpla = new LoadPluginListAction(cyObj);
                    lpla.parsePluginList(tmp);
                    break;
                case 3:
                    tmp = g.getOptarg();
                    helpRequested = true;
                    break;
                default:
                    break;
            }
        }

        if (helpRequested) {usage();}
    }

    /**
     * returns the message log.
     * @return
     */
    public String getMessages ()
    {
        return messageBuffer.toString ();
    }
}