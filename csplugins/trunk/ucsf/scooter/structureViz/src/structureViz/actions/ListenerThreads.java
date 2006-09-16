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
import java.io.*;

import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraChain;
import structureViz.model.ChimeraResidue;
import structureViz.ui.ModelNavigatorDialog;

/***************************************************
*                 Thread Classes                  *
**************************************************/

/**
 * Reply listener thread
 */
class ListenerThreads extends Thread 
{
	private InputStream readChan = null;
	private BufferedReader lineReader = null;
	private Chimera chimeraObject = null;
	private Process chimera = null;
	private List replyLog = null;

	ListenerThreads(Process chimera, List log, Chimera chimeraObject) {
		this.chimera = chimera;
		this.replyLog = log;
		this.chimeraObject = chimeraObject;
 	 	// Get a line-oriented reader
  	readChan = chimera.getInputStream();
		lineReader = new BufferedReader(new InputStreamReader(readChan));
	}

	public void run() {
		System.out.println("ReplyLogListener running");
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
			System.out.println("From Chimera: "+line);
			if (line.startsWith("END")) {
				break;
			}
			if (line.startsWith("ModelChanged: ")) {
				(new ModelUpdater()).start();
			} else if (line.startsWith("SelectionChanged: ")) {
				// Start up the updater on a separate thread
				(new SelectionUpdater()).start();
			} else if (!line.startsWith("CMD")) {
				reply.add(line);
			}
		}
		return reply;
	}

	/**
	 * Model updater thread
	 */
	class ModelUpdater extends Thread {

			public ModelUpdater() {}

			public void run() {
				chimeraObject.refresh();
				chimeraObject.modelChanged();
				// Now update our selection from Chimera
				(new SelectionUpdater()).start();
			}
	}

	/**
	 * Selection updater thread
	 */
	class SelectionUpdater extends Thread {

		public SelectionUpdater() { }

		public void run() {
			HashMap modelSelHash = new HashMap();
			ArrayList selectionList = new ArrayList();
			Iterator lineIter;
			// Clear the reply log
			// Execute the command to get the list of models with selections
			lineIter = chimeraObject.commandReply("lists level molecule");
			while (lineIter.hasNext()) {
				String modelLine = (String)lineIter.next();
				ChimeraModel chimeraModel = new ChimeraModel(modelLine);
				modelSelHash.put(new Integer(chimeraModel.getModelNumber()), chimeraModel);
			}

			// Now get the residue-level data
			lineIter = chimeraObject.commandReply("lists level residue");
			while (lineIter.hasNext()) {
				String inputLine = (String)lineIter.next();
				ChimeraResidue r = new ChimeraResidue(inputLine);
				Integer modelNumber = new Integer(r.getModelNumber());
				if (modelSelHash.containsKey(modelNumber)) {
					ChimeraModel model = (ChimeraModel)modelSelHash.get(modelNumber);
					model.addResidue(r);
				}
			}

			// Get the selected objects
			Iterator modelIter = modelSelHash.values().iterator();
			while (modelIter.hasNext()) {
				// Get the model
				ChimeraModel selectedModel = (ChimeraModel)modelIter.next();
				int modelNumber = selectedModel.getModelNumber();
				// Get the corresponding "real" model
				if (chimeraObject.containsModel(modelNumber)) {
					ChimeraModel dataModel = chimeraObject.getModel(modelNumber);
					if (dataModel.getResidueCount() == selectedModel.getResidueCount()) {
						// Select the entire model
						selectionList.add(dataModel);
					} else {
						Iterator chainIter = selectedModel.getChains().iterator();
						while (chainIter.hasNext()) {
							ChimeraChain selectedChain = (ChimeraChain)chainIter.next();
							ChimeraChain dataChain = dataModel.getChain(selectedChain.getChainId());
							if (selectedChain.getResidueCount() == dataChain.getResidueCount()) {
								selectionList.add(dataChain);
							} else {
								// Need to select individual residues
								Iterator resIter = selectedChain.getResidueList().iterator();
								while (resIter.hasNext()) {
									String residueIndex = ((ChimeraResidue)resIter.next()).getIndex();
									ChimeraResidue residue = dataChain.getResidue(residueIndex);
									selectionList.add(residue);
								} // resIter.hasNext
							}
						} // chainIter.hasNext()
					}
				}
			} // modelIter.hasNext()

			// Finally, update the navigator panel
			chimeraObject.updateSelection(selectionList);
		}
	}
}
