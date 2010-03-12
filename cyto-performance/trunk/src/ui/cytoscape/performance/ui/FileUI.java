
package cytoscape.performance.ui;

import cytoscape.*;
import cytoscape.performance.*;
import cytoscape.performance.track.*;
import java.util.*;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.beans.*;
import java.io.*;

public class FileUI {

	List<TrackedEvent> results;
	String inputDir;
	String outFile;

	public FileUI(List<TrackedEvent> results, String inputDir,String vers) {
		this.results = results;
		this.inputDir = inputDir; 	
		outFile = vers + ".perf";
	}

	public void dumpResults() {
		FileWriter fw; 
		try {
			fw = new FileWriter(outFile);
			String newline = System.getProperty("line.separator","\n");
			for (TrackedEvent t : results) {
				fw.write(t.toParsable());
				fw.write(newline);
			}
			fw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			fw = null;
		}
	}
}


