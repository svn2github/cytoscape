package cytoscape.editor.impl;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;

import cytoscape.Cytoscape;
import cytoscape.editor.event.HackLoadFileClass;

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

    public boolean importData(JComponent c, Transferable t) {
        if (canImport(c, t.getTransferDataFlavors())) {
            try {
                String str = (String)t.getTransferData(DataFlavor.stringFlavor);
        		DataFlavor[] dfl = t.getTransferDataFlavors();

        		for (int i = 0; i < dfl.length; i++) {
        			DataFlavor d = dfl[i];
        			System.out
        					.println("Item dropped of MIME type = " + d.getMimeType());
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
