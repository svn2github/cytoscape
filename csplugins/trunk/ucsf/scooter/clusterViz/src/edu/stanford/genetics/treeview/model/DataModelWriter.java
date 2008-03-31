/*
 * Created on Mar 7, 2005
 *
 * Copyright Alok Saldnaha, all rights reserved.
 */
package edu.stanford.genetics.treeview.model;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import edu.stanford.genetics.treeview.*;

/**
 * 
 *The purpose of this class is to write a DataModel out to flat file format.
 *
 */
public class DataModelWriter {
	DataModel dataModel;
	public DataModelWriter(DataModel source) {
		dataModel= source;
	}
	/**
	 * Write all parts of Datamodel out to disk
	 * 
	 * @param fileSet fileset to write to
	 */
	public void writeAll(FileSet fileSet) {
		writeAtr(fileSet.getAtr());
		writeGtr(fileSet.getGtr());
//		writeCdt(fileSet.getCdt());
	}
	public void writeIncremental(FileSet fileSet) {
		if (dataModel.aidFound() && 
				dataModel.getAtrHeaderInfo().getModified()) {
			writeAtr(fileSet.getAtr());
		}
		if (dataModel.gidFound() && 
				dataModel.getGtrHeaderInfo().getModified()) {
			writeGtr(fileSet.getGtr());
		}
		// cdt is not mutable (yet)
	}
	/**
	 * write out atr to file
	 * @param atr complete path of file to write to
	 */
	private void writeAtr(String atr) {
		writeTree(dataModel.getAtrHeaderInfo(), atr);
	}

	/**
	 * write out gtr to file
	 * @param gtr complete path of file to write to
	 */
	private void writeGtr(String gtr) {
		writeTree(dataModel.getGtrHeaderInfo(), gtr);
	}
	/**
	 * write out HeaderInfo of tree to file
	 * @param info HeaderInfo to write out
	 * @param filePath complete path of file to write to
	 */
	private void writeTree(HeaderInfo info, String file) {
		HeaderInfoWriter writer = new HeaderInfoWriter(info);
		try {
			String spool = file + ".spool";
			writer.write(spool);
			File f = new File(spool);
			if (f.renameTo(new File(file))) {
				info.setModified(false);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,"Error writing " + file +" " + e, "Save Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}	
}
