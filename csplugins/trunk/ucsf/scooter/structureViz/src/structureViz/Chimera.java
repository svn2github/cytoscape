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

import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraChain;
import structureViz.model.ChimeraResidue;

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
    
  public Chimera() {
  	/**
  	 * Null constructor, for now
  	 */
		replyLog = new ArrayList();
		models = new ArrayList();
		modelHash = new HashMap();
  }

	public ArrayList getChimeraModels () { return models; }
    
  /**
   * Launch (start) an instance of Chimera
   * @param pdbList
   * @return
   * @throws IOException
 */
  public boolean launch()
      throws IOException {
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
  public void open(String pdb, int model) throws IOException {
  	String cmd = "open "+model+" "+pdb;
  	this.command(cmd);

		// Get our properties (default color scheme, etc.)
		// Make the molecule look decent
		this.command("repr stick #"+model);
		this.command("focus");

		// Create our internal object
		ChimeraModel newModel = new ChimeraModel(pdb, model);

		// Create the information we need for the navigator
		getResidueInfo(newModel);

		// Add it to our list of models
		models.add(newModel);

		// Add it to the hash table
		modelHash.put(new Integer(model),newModel);

  	return;
  }

	/**
	 * Close a Chimera model
   * @param model
   * @throws IOException
	 */
	public void close(int model) throws IOException {
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
  public void command(String text) throws IOException {
  	if (chimera == null)
  		return;

		text = text.concat("\n");

		synchronized (replyLog) {
  		// send the command
  		chimera.getOutputStream().write(text.getBytes());
  		chimera.getOutputStream().flush();
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
  public void exit() throws IOException {
  	if (chimera == null)
  		return;
  	this.command("stop really");
  	chimera.destroy();
  	chimera = null;
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
			lineReader = new BufferedReader(new InputStreamReader(readChan), 1024*100);
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

		private String readLine(InputStream readChan) throws IOException {
 		 	int nbytes = readChan.available();
 		 	if (nbytes == 0)
 		 		return null;
 		 	byte buffer[] = {};
 		 	readChan.read(buffer, 0, nbytes);
 		 	return new String(buffer);
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

	private void getResidueInfo(ChimeraModel model) throws IOException {
		int modelNumber = model.getModelNumber();
		replyLog.clear();

		// Get the list -- it will be in the reply log
		this.command ("listr");
		Iterator resIter = replyLog.iterator();
		while (resIter.hasNext()) {
			ChimeraResidue r = new ChimeraResidue((String)resIter.next());
			if (r.getModelNumber() == modelNumber) {
				model.addResidue(r);
			}
		}
		replyLog.clear();
	}
}
