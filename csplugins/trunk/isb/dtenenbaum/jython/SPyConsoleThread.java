// SPyConsoleThread
//----------------------------------------------------------------------------------------
package csplugins.isb.dtenenbaum.jython;
//----------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------------------
import spyconsole.SPyConsole;
/**
 * A subclass of SPyConsole that implements the Runnable interace so that it can be used as a thread.
 * SPyconsole was released by Thomas Maxwell (http://www.uvm.edu/giee/SME3/ftp/JConsole/).
 *
 * @author Olivier Dameron  (dameron@smi.stanford.edu)
 */
public class SPyConsoleThread extends SPyConsole implements Runnable {


    /**
     * Default constructor. 
     * Just calls the SPyConsole constructor.
     */
    public SPyConsoleThread() {
	super();
    }

    /**
     * Required by the Runnable interface.
     * Calls the processArgs() and runShell() methods.
     */
    public void run() {
	String[] args = new String[] {};
	processArgs(args);
	runShell();
    }

}
