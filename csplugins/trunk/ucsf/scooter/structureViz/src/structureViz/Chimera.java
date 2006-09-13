/* vim: set ts=2: */
/**
 * Copyright (c) 2006 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package structureViz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.io.*;
import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.view.*;

import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraChain;
import structureViz.model.ChimeraResidue;
import structureViz.model.Structure;

/**
 * This class provides the main interface to UCSF Chimera
 * 
 * @author scooter
 *
 */
public class Chimera {
  /**
   * Static variables to keep track of the running
   * Chimera instance
   */
  // Chimera process
  static Process chimera;
	static private ArrayList replyLog;
	static private ArrayList models;
	static private HashMap modelHash;
	static private replyLogListener listener;
	static private CyNetworkView networkView;
    
  public Chimera(CyNetworkView networkView) {
  	/**
  	 * Null constructor, for now
  	 */
		replyLog = new ArrayList();
		models = new ArrayList();
		modelHash = new HashMap();
		this.networkView = networkView;
  }

	public ArrayList getChimeraModels () { return models; }

	public CyNetworkView getNetworkView () { return networkView; }

	public boolean isLaunched () {
		if (chimera != null) 
			return true;
		return false;
	}
    
  /**
   * Launch (start) an instance of Chimera
   * @param pdbList
   * @return
   * @throws IOException
 */
  public boolean launch() throws IOException {
  		// See if we already have a chimera instance running
  		if (chimera == null) {
  			// No, get one started
  			List <String> args = new ArrayList<String>();

				// Oops -- very platform specific, here!!
				// XXX FIXME XXX
  			args.add("chimera");
  			args.add("--start");
  			args.add("ReadStdin");

  			ProcessBuilder pb = new ProcessBuilder(args);
  			chimera = pb.start();
  		} 
			// Start up a listener
			listener = new replyLogListener(chimera);
			listener.start();

      return true;
  }
  
  /**
   * Open a Chimera model
   * @param pdb
   * @param model
   * @throws IOException
   */
  public void open(Structure structure) {
  	String cmd = "open "+structure.name();
  	this.command(cmd);

		// Now, figure out exactly what model # we got
		ChimeraModel newModel = getModelInfo(structure);
		if (newModel == null) return;

		// Get our properties (default color scheme, etc.)
		// Make the molecule look decent
		this.command("repr stick #"+newModel.getModelNumber());
		this.command("focus");

		// Create the information we need for the navigator
		getResidueInfo(newModel);

		// Add it to our list of models
		models.add(newModel);

		// Add it to the hash table
		modelHash.put(new Integer(newModel.getModelNumber()),newModel);

  	return;
  }

	/**
	 * Close a Chimera model
   * @param model
   * @throws IOException
	 */
	public void close(Structure structure) {
		int model = structure.modelNumber();
		String cmd = "close #"+model;
		this.command(cmd);
		
		ChimeraModel chimeraModel = (ChimeraModel)modelHash.get(new Integer(model));
		if (chimeraModel != null) {
			models.remove(chimeraModel);
			modelHash.remove(new Integer(model));
		}
		return;
	}

  
  /**
   * Send a string to the Chimera instance
   * @param text
   * @throws IOException
   */
  public void command(String text) {
  	if (chimera == null)
  		return;

		text = text.concat("\n");

		synchronized (replyLog) {
			try {
  			// send the command
  			chimera.getOutputStream().write(text.getBytes());
  			chimera.getOutputStream().flush();
			} catch (IOException e) {
				// popup error dialog
        JOptionPane.showMessageDialog(Cytoscape.getCurrentNetworkView().getComponent(),
        		"Unable to execute command "+text, "Unable to execute command "+text,
         		JOptionPane.ERROR_MESSAGE);
			}

			try {
				System.out.print("Waiting on replyLog for: "+text);
				replyLog.wait();
			} catch (InterruptedException e) {}
		}
		return;
  }
  
  /**
   * Terminate the running Chimera process
   * 
   */
  public void exit() {
  	if (chimera == null)
  		return;
  	this.command("stop really");
  	chimera.destroy();
  	chimera = null;
		models = null;
		modelHash = null;
  }

	/**
	 * Dump and refresh all of our model/chain/residue info
	 */
	public void refresh() {
		// Get a new model list
		HashMap newHash = new HashMap();

		// Get all of the open models
		List newModelList = getModelList();

		// Match them up -- assume that the model #'s haven't changed
		Iterator modelIter = newModelList.iterator();
		while (modelIter.hasNext()) {
			ChimeraModel model = (ChimeraModel)modelIter.next();
			Integer modelNumber = new Integer(model.getModelNumber());

			// If we already know about this model number, get the Structure,
			// which tells us about the associated CyNode
			if (modelHash.containsKey(modelNumber)) {
				ChimeraModel oldModel = (ChimeraModel)modelHash.get(modelNumber);
				model.setStructure(oldModel.getStructure());
			} else {
				// At some point, we should walk through all of the nodes in the
				// current network and see if we can match them up with this.  Until
				// then, set it to null
				model.setStructure((Structure)null);
			}

			newHash.put(modelNumber,model);

			// Get the residue information
			getResidueInfo(model);
		}

		// Replace the old model list
		models = (ArrayList)newModelList;
		modelHash = newHash;

		// Done
	}

	/**
	 * Reply listener thread
	 */
	static class replyLogListener extends Thread {
		private InputStream readChan = null;
		private BufferedReader lineReader = null;
		private Process chimera = null;

		replyLogListener(Process chimera) {
			this.chimera = chimera;
 		 	// Get a line-oriented reader
	  	readChan = chimera.getInputStream();
			lineReader = new BufferedReader(new InputStreamReader(readChan));
		}

		public void run() {
			System.out.println("replyLogListener running");
			while (true) {
				try {
					ArrayList reply = getReply();
					synchronized (replyLog) {
						if (reply.size() > 0) {
							replyLog.addAll(reply);
						}
						replyLog.notifyAll();
					}
				} catch (IOException e) {
					return;
				}
			}
		}

	  /**
 	  * Read input from Chimera
 	  */
 	  private ArrayList getReply() throws IOException {
 		 	if (chimera == null)
 		 		return null;

			// Generally -- looking for:
			// 	CMD command
			//   ........
			//	END
			// We return the text in between
			ArrayList reply = new ArrayList();
			String line = null;
			while ((line = lineReader.readLine()) != null) {
				// System.out.println("From Chimera: "+line);
				if (line.startsWith("END")) {
					break;
				}
				if (!line.startsWith("CMD")) {
					reply.add(line);
				}
			}
			return reply;
		}
	}

	private List getModelList() {
		ArrayList<ChimeraModel>modelList = new ArrayList<ChimeraModel>();
		replyLog.clear();
		this.command ("listm");
		Iterator modelIter = replyLog.iterator();
		while (modelIter.hasNext()) {
			String modelLine = (String)modelIter.next();
			String name = getModelName(modelLine);
			int model = getModelNumber(modelLine);
			ChimeraModel chimeraModel = new ChimeraModel(name, null);
			chimeraModel.setModelNumber(model);
			modelList.add(chimeraModel);
		}
		return modelList;
	}

	private ChimeraModel getModelInfo(Structure structure) {
		String name = structure.name();

		replyLog.clear();
		this.command ("listm");
		Iterator modelIter = replyLog.iterator();
		while (modelIter.hasNext()) {
			String modelLine = (String)modelIter.next();
			if (modelLine.contains(name)) {
				// got the right model, now get the model number
				int modelNumber = getModelNumber(modelLine);
				structure.setModelNumber(modelNumber);
				return new ChimeraModel(name, structure);
			}
		}
		return null;
	}

	private int getModelNumber(String inputLine) {
		int hash = inputLine.indexOf('#');
		int space = inputLine.indexOf(' ',hash);
		// model number is between hash+1 and space
		Integer modelInteger = new Integer(inputLine.substring(hash+1,space));
		return modelInteger.intValue();
	}

	private String getModelName(String inputLine) {
		int start = inputLine.indexOf("name ");
		return inputLine.substring(start+5);
	}

	private void getResidueInfo(ChimeraModel model) {
		int modelNumber = model.getModelNumber();
		replyLog.clear();

		// Get the list -- it will be in the reply log
		this.command ("listr spec #"+modelNumber);
		Iterator resIter = replyLog.iterator();
		while (resIter.hasNext()) {
			String inputLine = (String)resIter.next();
			ChimeraResidue r = new ChimeraResidue(inputLine);
			if (r.getModelNumber() == modelNumber) {
				model.addResidue(r);
			}
		}
		replyLog.clear();
	}
}
