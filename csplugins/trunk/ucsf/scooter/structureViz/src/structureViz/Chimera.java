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
import java.util.Iterator;
import java.util.List;
import java.io.*;

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
    
  public Chimera() {
  	/**
  	 * Null constructor, for now
  	 */
		replyLog = new ArrayList();
  }
    
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
			replyLogListener listener = new replyLogListener(chimera);
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
  	String cmd = "open "+model+" "+pdb+"\n";
  	this.command(cmd);

		// Get our properties (default color scheme, etc.)
		// Make the molecule look decent
		this.command("repr stick #"+model+"\n");
  	return;
  }

	/**
	 * Close a Chimera model
   * @param model
   * @throws IOException
	 */
	public void close(int model) throws IOException {
		String cmd = "close #"+model+"\n";
		this.command(cmd);
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
  	// send the command
  	chimera.getOutputStream().write(text.getBytes());
  	chimera.getOutputStream().flush();
		return;
  }
  
  /**
   * Terminate the running Chimera process
   * 
   */
  public void exit() throws IOException {
  	if (chimera == null)
  		return;
  	this.command("stop really\n");
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
			lineReader = new BufferedReader(new InputStreamReader(readChan));
		}

		public void run() {
			System.out.println("replyLogListener running");
			while (true) {
				try {
					String str = getReply();
					if (str != null && str.length() > 0) {
						replyLog.add(str);
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
 	 private String getReply() throws IOException {
 		 	if (chimera == null)
 		 		return null;

			// Generally -- looking for:
			// 	CMD command
			//   ........
			//	END
			// We return the text in between
			String reply = new String();
			String line = null;
			while ((line = lineReader.readLine()) != null) {
				System.out.println("From Chimera: "+line);
				if (line.startsWith("END")) {
					break;
				}
				if (!line.startsWith("CMD")) {
					reply = reply.concat(line);
				}
			}
			System.out.println(reply);
			return reply;
		}
	}
}
