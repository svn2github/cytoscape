/* BEGIN_HEADER                                              Java TreeView
*
* $Author: alokito $
* $RCSfile: FlatFileParser.java,v $
* $Revision: 1.5 $
* $Date: 2004/12/21 03:28:12 $
* $Name:  $
*
* This file is part of Java TreeView
* Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
*
* This software is provided under the GNU GPL Version 2. In particular, 
*
* 1) If you modify a source file, make a comment in it containing your name and the date.
* 2) If you distribute a modified version, you must do it under the GPL 2.
* 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
*
* A full copy of the license can be found in gpl.txt or online at
* http://www.gnu.org/licenses/gpl.txt
*
* END_HEADER 
*/
package edu.stanford.genetics.treeview.model;

import edu.stanford.genetics.treeview.*;


import java.io.*;
import java.util.*;
/**
* parses a tab-delimitted file into a vector of String []. Each String [] represents a row of the file.
*
* This object should be created and configured. None of the real action gets started until the
* loadIntoTable() routine is called. After loading, the object can be reconfigured and reused to load
* other files.
*/

public class FlatFileParser {
	private ProgressTrackable progressTrackable;
	public ProgressTrackable getProgressTrackable() {
		return progressTrackable;
	}
	public void setProgressTrackable(ProgressTrackable progressTrackable) {
		this.progressTrackable = progressTrackable;
	}
	
	private String resource;
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	
	/* resource types */
	public static final int FILE = 0;
	public static final int URL = 1;
	private int resourceType = 0;
	public int getResourceType() {
		return resourceType;
	}
	public void setResourceType(int resourceType) {
		this.resourceType = resourceType;
	}
	
	private boolean cancelled = false;
	public boolean getCancelled() {
		return cancelled;
	}
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public Vector loadIntoTable() throws LoadException, IOException {
		InputStream stream;
		if (getResource().startsWith("http://")) {
			try {
				setResourceType(URL);
				stream = openStream();
			} catch (Exception e) {
				setResourceType(FILE);
				stream = openStream();
			}
		} else {
			try {
				setResourceType(FILE);
				stream = openStream();
			} catch (Exception e) {
				setResourceType(URL);
				stream = openStream();
			}
		}
		return loadIntoTable(stream);
	}
	
	private void setLength(int i) {
		if (progressTrackable != null) {
			progressTrackable.setLength(i);
		}
	}
	private void setValue(int i) {
		if (progressTrackable != null) {
			progressTrackable.setValue(i);
		}
	}
	private int getValue() {
		if (progressTrackable != null) {
			return progressTrackable.getValue();
		} else {
			return 0;
		}
		
	}
	private void incrValue(int i) {
		if (progressTrackable != null) {
			progressTrackable.incrValue(i);
		}
	}
	
	/** returns a list of vectors of String [], representing data from file.*/
	private Vector loadIntoTable(InputStream inputStream) throws IOException, LoadException {
		Vector data = new Vector(100,100);
		MeteredStream ms = new MeteredStream(inputStream);
		Reader reader = new BufferedReader(new InputStreamReader(ms));
		
		FlatFileStreamTokenizer st;
		st = new FlatFileStreamTokenizer(reader);
		// ignore leading blank lines...
		while (st.nextToken() == FlatFileStreamTokenizer.TT_EOL) {}
		st.pushBack();
		Vector line = new Vector(10, 10);
		while (st.nextToken() != FlatFileStreamTokenizer.TT_EOF) {
			if (getCancelled() == true) break; // we're cancelled
			st.pushBack();
			loadLine(line,st);
			String tokens[] = new String[line.size()];
			Enumeration e = line.elements();
			for (int i = 0; i < tokens.length; i++) {
				tokens[i] = (String) e.nextElement();
			}
			data.addElement(tokens);
			line.removeAllElements();
		}
		return data;
	}
	
		private void loadLine(Vector line, FlatFileStreamTokenizer st) 
	throws LoadException, IOException {
		int tt = st.nextToken();
		while ((tt != FlatFileStreamTokenizer.TT_EOL) && (tt != FlatFileStreamTokenizer.TT_EOF)) {
			if (tt == FlatFileStreamTokenizer.TT_WORD) {
				line.addElement(st.sval);
			} else if (tt == FlatFileStreamTokenizer.TT_NULL) {
				line.addElement(null);		
			} else {
				String err = "In loadLine, Got token type " + tt + " token " + st.toString() +
				
				" expected TT_WORD (" + FlatFileStreamTokenizer.TT_WORD + ") at line " + st.lineno();
				throw new LoadException(err, LoadException.CDTPARSE);
			}
			tt = st.nextToken();
		}
	}

	/** opens a stream from the resource */
	private InputStream openStream() throws LoadException{
		InputStream is;
		String file = getResource();
		if (getResourceType() == FILE) {
			try{
				File fd = new File(file);
				is = new MeteredStream(new FileInputStream(fd));
				setLength((int) fd.length());
			} catch (Exception ioe) {
				throw new LoadException("File " + file + " could not be opened: " +
				ioe.getMessage(),
				LoadException.CDTPARSE);
			}
		} else {
			try {
				java.net.URL url = new java.net.URL(file);
				java.net.URLConnection conn = url.openConnection();
				is = new MeteredStream(conn.getInputStream());
				setLength(conn.getContentLength());
			} catch (IOException ioe2) {
				throw new LoadException("Url " + file + " could not be opened: " +
				ioe2.getMessage(),
				LoadException.CDTPARSE);
			}
		}
		return is;
	}


	class MeteredStream extends FilterInputStream {
		MeteredStream(InputStream is) {
			super(is);
		}

		public int read() throws IOException {
			incrValue(1);
			return super.read();
		}
		
		// the following should be covered by the more general read...
		//public int read(byte [] b);
		
		public int read(byte [] b, int off, int len) throws IOException {
			int ret = super.read(b,off,len);
			if (ret != -1) {
				// for some reason, got factor of two error in sizes...
				incrValue(ret/2);
			}
			return ret;
		}
		
		public long skip(long n) throws IOException {
			long ret = super.skip(n);
			if (ret != -1) {
				// for some reason, got factor of two error in sizes...
				incrValue((int) ret/2);
			}
			return ret;
		}
		int markedValue = 0;
		public void mark(int readLimit) {
			super.mark(readLimit);
			markedValue = getValue();
		}
		public void reset() throws IOException {
			super.reset();
			setValue(markedValue);
		}
	}	
}
