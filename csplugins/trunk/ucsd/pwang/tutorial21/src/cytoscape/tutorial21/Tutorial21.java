package cytoscape.tutorial21;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import cytoscape.CytoscapeInit;

import cytoscape.plugin.CytoscapePlugin;


/**
 * 
 */
public class Tutorial21 extends CytoscapePlugin {

	/**
	 * 
	 */
	public Tutorial21() {
		restoreInitState();
	}

	/**
	 * Save global state to "tutorial21.props"
	 */
	public void onCytoscapeExit() {

		File propFile = CytoscapeInit.getConfigFile("tutorial21.props");

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(propFile));
			writer.write("Line 1");			
			writer.newLine();
			writer.write("Line 2");

			writer.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Restore plugin state from global property "tutorial21.props"
	 */
	public void restoreInitState() {
		File global_prop_file = CytoscapeInit.getConfigFile("tutorial21.props");

		try {
			BufferedReader in = new BufferedReader(new FileReader(global_prop_file));

			String firstLine = in.readLine();			
			String secondLine = in.readLine();
			
			System.out.println("\ttutorial21.props:  " + firstLine);
			System.out.println("\ttutorial21.props:  " + secondLine);

			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// override the following two methods to save state in session.
	/**
	 * DOCUMENT ME!
	 * 
	 * @param pStateFileList
	 *            DOCUMENT ME!
	 */
	public void restoreSessionState(List<File> pStateFileList) {
		
		if ((pStateFileList == null) || (pStateFileList.size() == 0)) {
			//No previous state to restore
			return;
		}
		
		try {
			File prop_file = pStateFileList.get(0);

			BufferedReader in = new BufferedReader(new FileReader(prop_file));
			System.out.println("\tsession:tutorial21.props: " + in.readLine());
			System.out.println("\tsession:tutorial21.props: " + in.readLine());

		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param pFileList
	 *            DOCUMENT ME!
	 */
	public void saveSessionStateFiles(List<File> pFileList) {
		// Create an empty file on system temp directory
		String tmpDir = System.getProperty("java.io.tmpdir");
		System.out.println("java.io.tmpdir: [" + tmpDir + "]");

		File session_prop_file = new File(tmpDir, "tutorial21.props");

		//
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(session_prop_file));

			writer.write("line A");			
			writer.newLine();
			writer.write("line B");
			
			writer.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		pFileList.add(session_prop_file);
	}
}
