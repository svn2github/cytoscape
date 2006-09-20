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
package structureViz.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.io.*;
import javax.swing.JOptionPane;
import java.awt.Color;

import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.view.*;
import cytoscape.data.CyAttributes;

import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraChain;
import structureViz.model.ChimeraResidue;
import structureViz.model.Structure;
import structureViz.actions.CyChimera;
import structureViz.ui.ModelNavigatorDialog;
import structureViz.ui.AlignStructuresDialog;

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
	static private ListenerThreads listener;
	static private CyNetworkView networkView;
	static private ModelNavigatorDialog mnDialog = null;
	static private AlignStructuresDialog alDialog = null;
    
  public Chimera(CyNetworkView networkView) {
  	/**
  	 * Null constructor, for now
  	 */
		replyLog = new ArrayList();
		models = new ArrayList();
		modelHash = new HashMap();
		this.networkView = networkView;
  }

	public List getChimeraModels () { return models; }

	public CyNetworkView getNetworkView () { return networkView; }

	// We need this to be able to update selections
	public void setDialog(ModelNavigatorDialog dialog) { mnDialog = dialog; }

	public ModelNavigatorDialog getDialog() { return mnDialog; }

	public void setAlignDialog(AlignStructuresDialog dialog) { this.alDialog = dialog; }

	public AlignStructuresDialog getAlignDialog() { return this.alDialog; }

	public boolean containsModel(Integer modelNumber) {
		return modelHash.containsKey(modelNumber);
	}

	public boolean containsModel(int modelNumber) {
		Integer mn = new Integer(modelNumber);
		return modelHash.containsKey(mn);
	}

	public ChimeraModel getModel(int modelNumber) {
		Integer mn = new Integer(modelNumber);
		return (ChimeraModel)modelHash.get(mn);
	}

	public ChimeraModel getModel(String modelName) {
		Iterator modelIter = models.iterator();
		while (modelIter.hasNext()) {
			ChimeraModel model = (ChimeraModel)modelIter.next();
			if (model.getModelName().equals(modelName))
				return model;
		}
		return null;
	}

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

				// Get the path
				String path = CyChimera.getProperty("chimeraPath");
				if (path != null) {
					path = path+"chimera";
				} else {
					path = "chimera";
				}

				// Oops -- very platform specific, here!!
				// XXX FIXME XXX
  			args.add(path);
  			args.add("--start");
  			args.add("ReadStdin");

  			ProcessBuilder pb = new ProcessBuilder(args);
  			chimera = pb.start();
  		} 
			// Start up a listener
			listener = new ListenerThreads(chimera, replyLog, this);
			listener.start();

			// Ask Chimera to give us updates
			this.command("listen start models");
			this.command("listen start selection");

      return true;
  }
  
  /**
   * Open a Chimera model
   * @param pdb
   * @param model
   * @throws IOException
   */
  public void open(Structure structure) {
		this.command("listen stop models");
		this.command("listen stop selection");
  	this.command("open "+structure.name());

		// Now, figure out exactly what model # we got
		ChimeraModel newModel = getModelInfo(structure);
		if (newModel == null) return;

		// Get the color (for our navigator)
		newModel.setModelColor(getModelColor(newModel));

		// Get our properties (default color scheme, etc.)
		// Make the molecule look decent
		this.command("repr stick #"+newModel.getModelNumber());
		this.command("focus");

		// Create the information we need for the navigator
		getResidueInfo(newModel);

		// Add it to our list of models
		models.add(newModel);
		//System.out.println("Added "+newModel.toString()+" to list");

		// Add it to the hash table
		modelHash.put(new Integer(newModel.getModelNumber()),newModel);
		this.command("listen start models");
		this.command("listen start selection");

		// Update the structure model #
		structure.setModelNumber(newModel.getModelNumber());

  	return;
  }

	/**
	 * Close a Chimera model
   * @param model
   * @throws IOException
	 */
	public void close(Structure structure) {
		this.command("listen stop models");
		int model = structure.modelNumber();
		this.command("close #"+model);
		
		ChimeraModel chimeraModel = (ChimeraModel)modelHash.get(new Integer(model));
		if (chimeraModel != null) {
			models.remove(chimeraModel);
			modelHash.remove(new Integer(model));
		}
		this.command("listen start models");
		return;
	}

	/**
	 * Select something in Chimera
	 */
	public void select(String command) {
		this.command("listen stop select");
		this.command(command);
		this.command("listen start select");
	}

	public Iterator commandReply(String text) {
		replyLog.clear();
		this.command(text);
		return replyLog.iterator();
	}

  /**
   * Send a string to the Chimera instance
   * @param text
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
				//System.out.print("Waiting on replyLog for: "+text);
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
				model.setStructure(CyChimera.findStructureForModel(networkView, model.getModelName()));
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

	public void modelChanged() {
		mnDialog.modelChanged();
	}

	public void updateSelection(List selectionList) {
		mnDialog.updateSelection(selectionList);
	}

	private List getModelList() {
		ArrayList<ChimeraModel>modelList = new ArrayList<ChimeraModel>();
		Iterator modelIter = this.commandReply ("listm");
		while (modelIter.hasNext()) {
			String modelLine = (String)modelIter.next();
			ChimeraModel chimeraModel = new ChimeraModel(modelLine);
			modelList.add(chimeraModel);
		}
		return modelList;
	}

	private ChimeraModel getModelInfo(Structure structure) {
		String name = structure.name();

		Iterator modelIter = this.commandReply ("listm");
		while (modelIter.hasNext()) {
			String modelLine = (String)modelIter.next();
			if (modelLine.contains(name)) {
				// got the right model, now get the model number
				ChimeraModel chimeraModel = new ChimeraModel(structure, modelLine);
				structure.setModelNumber(chimeraModel.getModelNumber());
				return chimeraModel;
			}
		}
		return null;
	}

	private Color getModelColor(ChimeraModel model) {
		replyLog.clear();
		this.command ("listm attr color spec "+model.toSpec());
		String inputLine = (String)replyLog.get(0);
		int colorStart = inputLine.indexOf("color ");
		String colorString = inputLine.substring(colorStart+6);
		String[] rgbStrings = colorString.split(",");
		float[] rgbValues = new float[4];
		for (int i = 0; i < rgbStrings.length; i++) {
			Float f = new Float(rgbStrings[i]);
			rgbValues[i] = f.floatValue();
		}
		if (rgbStrings.length == 4) {
			return new Color(rgbValues[0], rgbValues[1], rgbValues[2], rgbValues[3]);
		} else {
			return new Color(rgbValues[0], rgbValues[1], rgbValues[2]);
		}
	}

	private void getResidueInfo(ChimeraModel model) {
		int modelNumber = model.getModelNumber();

		// Get the list -- it will be in the reply log
		Iterator resIter = this.commandReply ("listr spec #"+modelNumber);
		while (resIter.hasNext()) {
			String inputLine = (String)resIter.next();
			ChimeraResidue r = new ChimeraResidue(inputLine);
			if (r.getModelNumber() == modelNumber) {
				model.addResidue(r);
			}
		}
	}
}
