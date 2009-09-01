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
import cytoscape.data.CyAttributes;
import cytoscape.logger.CyLogger;
import cytoscape.view.*;

import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraChain;
import structureViz.model.ChimeraResidue;
import structureViz.model.ChimeraStructuralObject;
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
	static private ArrayList<String> replyLog;
	static private ArrayList<ChimeraModel> models;
	static private HashMap<Float,ChimeraModel> modelHash;
	static private ListenerThreads listener;
	static private CyNetworkView networkView;
	static private ModelNavigatorDialog mnDialog = null;
	static private AlignStructuresDialog alDialog = null;
    
	/**
	 * Create the Chimera object
	 *
	 * @param networkView the current CyNetworkView 
	 */
  public Chimera(CyNetworkView networkView) {
  	/**
  	 * Null constructor, for now
  	 */
		replyLog = new ArrayList();
		models = new ArrayList();
		modelHash = new HashMap();
		this.networkView = networkView;
  }

	/**
	 * Return the list of all open models in this instance of Chimera
	 *
	 * @return list of ChimeraModels
	 */
	public List<ChimeraModel> getChimeraModels () { return models; }

	/**
	 * Return our network view
	 *
	 * @return the network view we were created with
	 */
	public CyNetworkView getNetworkView () { return networkView; }

	/**
	 * Provide a handle to the ModelNavigatorDialog that is currently
	 * up.  We need this so that we can update the selections in the
	 * dialog when the selections change in Chimera.
	 *
	 * @param dialog the ModelNavigatorDialog that provides our interface
	 */
	public void setDialog(ModelNavigatorDialog dialog) { mnDialog = dialog; }

	/**
	 * Return the ModelNavigatorDialog that is providing our interface
	 *
	 * @return our ModelNavigatorDialog
	 */
	public ModelNavigatorDialog getDialog() { return mnDialog; }

	/**
	 * Provide a handle to the AlignStructuresDialog that is currently
	 * up.  This does not necessarily need to be in the Chimera object,
	 * but it has to be somewhere, and this seemed like a good place.
	 *
	 * @param dialog the AlignStructuresDialog that provides our interface
	 */
	public void setAlignDialog(AlignStructuresDialog dialog) { this.alDialog = dialog; }

	/**
	 * Return the AlignStructuresDialog that provides the interface to our align structures
	 * functionality.
	 *
	 * @return our AlignStructuresDialog
	 */
	public AlignStructuresDialog getAlignDialog() { return this.alDialog; }

	/**
	 * Test to see if we currently have a particular model open
	 *
	 * @param modelNumber the model number expressed as an Float object
	 */
	public boolean containsModel(Float modelNumber) {
		return modelHash.containsKey(modelNumber);
	}

	/**
	 * Test to see if we currently have a particular model open
	 *
	 * @param modelNumber the model number expressed as a float
	 */
	public boolean containsModel(float modelNumber) {
		Float mn = new Float(modelNumber);
		return modelHash.containsKey(mn);
	}

	/**
	 * Return the ChimeraModel associated with the requested 
	 * model number
	 *
	 * @param modelNumber the model number expressed as a float
	 * @return the ChimeraModel with a model number of modelNumber
	 */
	public ChimeraModel getModel(float modelNumber) {
		Float mn = new Float(modelNumber);
		return (ChimeraModel)modelHash.get(mn);
	}

	/**
	 * Return the ChimeraModel associated with the requested 
	 * model name
	 *
	 * @param modelName the model name expressed as a string
	 * @return the ChimeraModel with a model name of modelName
	 */
	public ChimeraModel getModel(String modelName) {
		for (ChimeraModel model: models) {
			if (model.getModelName().equals(modelName))
				return model;
		}
		return null;
	}

	/**
	 * Test to see if we currently have a running instance of Chimera
	 *
	 * @return <b>true</b> if Chimera is already running
	 */
	public boolean isLaunched () {
		if (chimera != null) 
			return true;
		return false;
	}
    
  /**
   * Launch (start) an instance of Chimera
   * @return "true" if the launch was successful
   * @throws IOException
 */
  public boolean launch() throws IOException {
  		// See if we already have a chimera instance running
  		if (chimera == null) {
  			// No, get one started

				// Figure out our fallback
				String osPath = "";
				String os = System.getProperty("os.name");
				if (os.startsWith("Linux")) {
					osPath = "/usr/local/chimera/bin/";
				} else if (os.startsWith("Windows")) {
					osPath = "\\Program Files\\Chimera\\bin\\";
				} else if (os.startsWith("Mac")) {
					osPath = "/Applications/Chimera.app/Contents/MacOS/";
				}

				// Get the path
				String path = CyChimera.getProperty("chimeraPath");
				if (path != null) {
					path = path+"chimera";
				} else {
					path = "chimera";
				}
				CyLogger.getLogger(Chimera.class).info("Path: "+path);

				try {
  				List <String> args = new ArrayList<String>();
  				args.add(path);
  				args.add("--start");
  				args.add("ReadStdin");
  				ProcessBuilder pb = new ProcessBuilder(args);
  				chimera = pb.start();
				} catch (Exception e) {
  				List <String> args = new ArrayList<String>();
  				args.add(osPath+"chimera");
  				args.add("--start");
  				args.add("ReadStdin");
  				ProcessBuilder pb = new ProcessBuilder(args);
  				chimera = pb.start();
				}
  		} 
			// Start up a listener
			listener = new ListenerThreads(chimera, replyLog, this);
			listener.start();

			// Ask Chimera to give us updates
			this.command("listen start models; listen start selection");

      return true;
  }
  
  /**
   * Open a Chimera model
	 *
   * @param structure the Structure to open
   */
  public void open(Structure structure) {
		structure.setModelNumber(Structure.getNextModel());
		if (structure.getType() == Structure.StructureType.MODBASE_MODEL)
			this.command("listen stop models; listen stop selection; open "+structure.modelNumber()+" modbase:"+structure.name());
		else if (structure.getType() == Structure.StructureType.SMILES)
			this.command("listen stop models; listen stop selection; open "+structure.modelNumber()+" smiles:"+structure.name());
		else
			this.command("listen stop models; listen stop selection; open "+structure.modelNumber()+" "+structure.name());

		// Now, figure out exactly what model # we got
		List<ChimeraModel> modelList = getModelInfoList(structure);
		if (modelList == null) return;
		for (ChimeraModel newModel: modelList) {

			// Get the color (for our navigator)
			newModel.setModelColor(getModelColor(newModel));

			// Get our properties (default color scheme, etc.)
			// Make the molecule look decent
			this.command("repr stick #"+newModel.getModelNumber());

			if (structure.getType() != Structure.StructureType.SMILES) {
				// Create the information we need for the navigator
				getResidueInfo(newModel);
			}

			// Add it to our list of models
			models.add(newModel);
			//System.out.println("Added "+newModel.toString()+" to list");

			// Add it to the hash table
			modelHash.put(new Float(newModel.getModelNumber()),newModel);
		}

		this.command("focus");
		this.command("listen start models; listen start selection");

  	return;
  }

	/**
	 * Close a Chimera model
	 *
   * @param structure the Structure to close
	 */
	public void close(Structure structure) {
		float model = structure.modelNumber();
		this.command("listen stop models; listen stop select; close #"+model);
		
		ChimeraModel chimeraModel = (ChimeraModel)modelHash.get(new Float(model));
		if (chimeraModel != null) {
			models.remove(chimeraModel);
			modelHash.remove(new Float(model));
		}
		this.command("listen start models; listen start select");
		return;
	}

	/**
	 * Select something in Chimera
	 *
	 * @param command the selection command to pass to Chimera
	 */
	public void select(String command) {
		this.command("listen stop select; "+command+"; listen start select");
	}

	public Iterator commandReply(String text) {
		synchronized (replyLog) {
			replyLog.clear();
		}
		this.command(text);
		return replyLog.iterator();
	}

  /**
   * Send a string to the Chimera instance
	 *
   * @param text the text to pass to Chimera
   */
  public void command(String text) {
  	if (chimera == null)
  		return;

		// System.out.println("To Chimera --> "+text);
		text = text.concat("\n");

		synchronized (replyLog) {
			try {
  			// send the command
  			chimera.getOutputStream().write(text.getBytes());
  			chimera.getOutputStream().flush();
			} catch (IOException e) {
				CyLogger.getLogger(Chimera.class).warning("Unable to execute command: "+text);
			}

			try {
				// System.out.print("Waiting on replyLog for: "+text);
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

		// Stop all of our listeners while we try to handle this
		this.command("listen stop select; listen stop models");

		// Get all of the open models
		List<ChimeraModel> newModelList = getModelList();

		// Match them up -- assume that the model #'s haven't changed
		for (ChimeraModel model: newModelList) {
			Float modelNumber = new Float(model.getModelNumber());
			// Get the color (for our navigator)
			model.setModelColor(getModelColor(model));

			// If we already know about this model number, get the Structure,
			// which tells us about the associated CyNode
			if (modelHash.containsKey(modelNumber)) {
				ChimeraModel oldModel = (ChimeraModel)modelHash.get(modelNumber);
				Structure s = oldModel.getStructure();
				if (s.getType() == Structure.StructureType.SMILES)
					model.setModelName(s.name());
				model.setStructure(s);
			} else {
				// This will return a new Structure if we don't know about it
				Structure s = CyChimera.findStructureForModel(networkView, model.getModelName());
				s.setModelNumber(modelNumber);
				model.setStructure(s);
			}

			newHash.put(modelNumber,model);

			if (model.getStructure() != null && model.getStructure().getType() != Structure.StructureType.SMILES) {
				// Get the residue information
				getResidueInfo(model);
			}
		}

		// Replace the old model list
		models = (ArrayList)newModelList;
		modelHash = newHash;

		// Restart all of our listeners
		this.command("listen start models; listen start select");

		// Done
	}

	/**
	 * Inform our interface that the model has changed
	 */
	public void modelChanged() {
		mnDialog.modelChanged();
	}

	/**
	 * Inform our interface that the selection has changed
	 */
	public void updateSelection(List selectionList) {
		mnDialog.updateSelection(selectionList);
	}

	/**
	 * This is called by the selectionListener to let us know that
	 * the user has changed their selection in Chimera.  We need
	 * to go back to Chimera to find out what is currently selected
	 * and update our list.
	 */
	public void updateSelection() {
		HashMap<Float,ChimeraModel>modelSelHash = new HashMap();
		ArrayList<ChimeraStructuralObject>selectionList = new ArrayList();
		// System.out.println("updateSelection()");

		// Execute the command to get the list of models with selections
		Iterator lineIter = commandReply("lists level molecule");
		while (lineIter.hasNext()) {
			String modelLine = (String)lineIter.next();
			ChimeraModel chimeraModel = new ChimeraModel(modelLine);
			modelSelHash.put(new Float(chimeraModel.getModelNumber()), chimeraModel);
		}

		// Now get the residue-level data
		lineIter = commandReply("lists level residue");
		while (lineIter.hasNext()) {
			String inputLine = (String)lineIter.next();
			ChimeraResidue r = new ChimeraResidue(inputLine);
			Float modelNumber = new Float(r.getModelNumber());
			if (modelSelHash.containsKey(modelNumber)) {
				ChimeraModel model = (ChimeraModel)modelSelHash.get(modelNumber);
				model.addResidue(r);
			}
		}

		// Get the selected objects
		for (ChimeraModel selectedModel: modelSelHash.values()) {
			float modelNumber = selectedModel.getModelNumber();
			// Get the corresponding "real" model
			if (containsModel(modelNumber)) {
				ChimeraModel dataModel = getModel(modelNumber);
				if (dataModel.getResidueCount() == selectedModel.getResidueCount() ||
				    dataModel.getStructure().getType() == Structure.StructureType.SMILES) {
					// Select the entire model
					selectionList.add(dataModel);
				} else {
					for (ChimeraChain selectedChain: selectedModel.getChains()) {
						ChimeraChain dataChain = dataModel.getChain(selectedChain.getChainId());
						if (selectedChain.getResidueCount() == dataChain.getResidueCount()) {
							selectionList.add(dataChain);
						} else {
							// Need to select individual residues
							for (ChimeraResidue res: selectedChain.getResidues()) {
								String residueIndex = res.getIndex();
								ChimeraResidue residue = dataChain.getResidue(residueIndex);
								selectionList.add(residue);
							} // resIter.hasNext
						}
					} // chainIter.hasNext()
				}
			}
		} // modelIter.hasNext()

		// Finally, update the navigator panel
		updateSelection(selectionList);
	}

	/**
	 * Return the list of depiction presets available from within Chimera.
	 * Chimera will return the list as a series of lines with the format:
	 *	Preset type number "description"
	 *
	 * @return list of presets
	 */
	public List getPresets() {
		ArrayList<String>presetList = new ArrayList<String>();
		Iterator<String> presetIter = this.commandReply ("preset list");
		while (presetIter.hasNext()) {
			String preset = presetIter.next();
			preset = preset.substring(7); // Skip over the "Preset"
			preset = preset.replaceFirst("\"", "(");
			preset = preset.replaceFirst("\"", ")");
			// string now looks like: type number (description)
			presetList.add(preset);
		}
		return presetList;
	}

	/**
	 * Return the list of ChimeraModels currently open
	 *
	 * @return List of ChimeraModel's
	 */
	private List getModelList() {
		ArrayList<ChimeraModel>modelList = new ArrayList<ChimeraModel>();
		Iterator modelIter = this.commandReply ("listm type molecule");
		synchronized (replyLog) {
			while (modelIter.hasNext()) {
				String modelLine = (String)modelIter.next();
				ChimeraModel chimeraModel = new ChimeraModel(modelLine);
				modelList.add(chimeraModel);
			}
		}
		return modelList;
	}

	/**
	 * Return the ChimeraModel associated with this structure.  This
	 * involves asking Chimera to provide us with the information.
	 *
	 * @param structure the Structure we want to get the ChimeraModel for
	 * @return the ChimeraModel for this structure
	 */
	private List<ChimeraModel> getModelInfoList(Structure structure) {
		String name = structure.name();
		int modelNumber = structure.modelNumber();
		List<ChimeraModel>infoList = new ArrayList();

		Iterator modelIter = this.commandReply ("listm type molecule spec #"+modelNumber);
		while (modelIter.hasNext()) {
			String modelLine = (String)modelIter.next();
			// System.out.println("ModelList: "+modelLine);
			if (modelLine.contains("id #"+modelNumber)) {
				// System.out.println("Found: "+name);
				// got the right model, now get the model number
				ChimeraModel chimeraModel = new ChimeraModel(structure, modelLine);
				structure.setModelNumber(chimeraModel.getModelNumber());
				// System.out.println("Identified model as "+chimeraModel);
				infoList.add(chimeraModel);
			}
		}
		if (infoList.size() > 0)
			return infoList;
		return null;
	}

	/**
	 * Determine the color that Chimera is using for this model.
	 *
	 * @param model the ChimeraModel we want to get the Color for
	 * @return the default model Color for this model in Chimera
	 */
	private Color getModelColor(ChimeraModel model) {
		replyLog.clear();
		this.command ("listm type molecule attr color spec "+model.toSpec());
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

	/**
	 * Get information about the residues associated with a model.  This
	 * uses the Chimera listr command.  We don't return the resulting
	 * residues, but we add the residues to the model.
	 *
	 * @param model the ChimeraModel to get residue information for
	 * 
	 */
	private void getResidueInfo(ChimeraModel model) {
		float modelNumber = model.getModelNumber();

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
