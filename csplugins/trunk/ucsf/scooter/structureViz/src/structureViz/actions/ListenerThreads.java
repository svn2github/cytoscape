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
import java.util.List;
import java.io.*;

import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraChain;
import structureViz.model.ChimeraResidue;

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

	/**
	 * Create a new listener thread to read the responses from Chimera
	 *
	 * @param chimera a handle to the Chimera Process
	 * @param log a handle to a List to post the responses to
	 * @param chimeraObject a handle to the Chimera Object
	 */
	ListenerThreads(Process chimera, List log, Chimera chimeraObject) {
		this.chimera = chimera;
		this.replyLog = log;
		this.chimeraObject = chimeraObject;
 	 	// Get a line-oriented reader
  	readChan = chimera.getInputStream();
		lineReader = new BufferedReader(new InputStreamReader(readChan));
	}

	/**
	 * Start the thread running
	 */
	public void run() {
		// System.out.println("ReplyLogListener running");
		while (true) {
			try {
				List reply = getReply();
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
	 *
	 * @return a List containing the replies from Chimera
   */
   private List getReply() throws IOException {
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
			if (line.startsWith("ModelChanged: ")) {
				(new ModelUpdater()).start();
			} else if (line.startsWith("SelectionChanged: ")) {
				// Start up the updater on a separate thread
				(new SelectionUpdater()).start();
			} else if (line.length() == 0) {
				continue;
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
				// (new SelectionUpdater()).start();
			}
	}

	/**
	 * Selection updater thread
	 */
	class SelectionUpdater extends Thread {

		public SelectionUpdater() { }

		public void run() {
			try {
				// System.out.println("Calling updateSelection");
				chimeraObject.updateSelection();
			} catch (Exception e) {}
		}
	}
}
