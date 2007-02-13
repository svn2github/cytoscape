
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.editor.impl;

import cytoscape.Cytoscape;

import cytoscape.editor.event.HackLoadFileClass;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.File;
import java.io.IOException;

import java.util.List;

import javax.swing.JComponent;


/**
 *
 */
public class FileListTransferHandler extends StringTransferHandler {
	//Bundle up the selected items in the list
	//as a single string, for export.
	protected String exportString(JComponent c) {
		return null;
	}

	protected void cleanup(JComponent c, boolean remove) {
	}

	protected void importString(JComponent c, String str) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 * @param t DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean importData(JComponent c, Transferable t) {
		if (canImport(c, t.getTransferDataFlavors())) {
			try {
				String str = (String) t.getTransferData(DataFlavor.stringFlavor);
				DataFlavor[] dfl = t.getTransferDataFlavors();

				for (int i = 0; i < dfl.length; i++) {
					DataFlavor d = dfl[i];
					System.out.println("Item dropped of MIME type = " + d.getMimeType());

					// if (d.isMimeTypeEqual("application/x-java-url"))
					// {
					// handleDroppedURL(t, d, location);
					// }

					// AJK: 11/15/06 BEGIN
					// load file
					if (d.isFlavorJavaFileListType()) {
						try {
							List files = (List) t.getTransferData(dfl[i]);

							// for now, just assume a list of one file				
							HackLoadFileClass hlf = new HackLoadFileClass();
							hlf.loadFile((File) files.get(0), false, false);
						} catch (UnsupportedFlavorException exc) {
							exc.printStackTrace();

							return false;
						} catch (IOException exc) {
							exc.printStackTrace();

							return false;
						}
					}
				}

				return true;
			} catch (UnsupportedFlavorException ufe) {
			} catch (IOException ioe) {
			}
		}

		return true;
	}
}
