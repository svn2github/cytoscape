/*
 * Created on Mar 7, 2005
 *
 * Copyright Alok Saldnaha, all rights reserved.
 */
package edu.stanford.genetics.treeview.model;

import java.io.File;
import java.io.FileWriter;
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
	public void writeAtr(String atr) {
		writeTree(dataModel.getAtrHeaderInfo(), atr);
	}

	/**
	 * write out gtr to file
	 * @param gtr complete path of file to write to
	 */
	public void writeGtr(String gtr) {
		writeTree(dataModel.getGtrHeaderInfo(), gtr);
	}

	/**
	 * write out the data array to file
	 * @param cdt path of file to write to
	 */
	public void writeCdt(String cdt) {
		FileWriter out = null;
		try {
			out = new FileWriter(cdt);

			// Get the Gene header info
			HeaderInfo geneHeaderInfo = dataModel.getGeneHeaderInfo();
			// Get the Array header info
			HeaderInfo arrayHeaderInfo = dataModel.getArrayHeaderInfo();
	
			// Get the data
			DataMatrix dataMatrix = dataModel.getDataMatrix();
	
			// Get the number of Genes
			int nGenes = dataMatrix.getNumRow();
			// Get the number of experimental values
			int nExpr = dataMatrix.getNumCol();
	
			// Write out the header data
			writeGeneHeader(out, geneHeaderInfo, arrayHeaderInfo);
	
			// Write out the AID line
			writeArrayHeader(out, "AID", geneHeaderInfo.getNames().length, arrayHeaderInfo);
	
			// Write out the EWEIGHT
			writeArrayHeader(out, "EWEIGHT", geneHeaderInfo.getNames().length, arrayHeaderInfo);
	
			for (int row = 0; row < nGenes; row++) {
				writeDataRow(out, geneHeaderInfo, dataMatrix, row);
			}

			out.flush();
			out.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,"Error writing " + cdt +" " + e, "Save Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void writeGeneHeader(FileWriter out, HeaderInfo geneHeaderInfo, HeaderInfo arrayHeaderInfo) throws IOException {
		// Write out the Gene header names, followed by the AID's
		String[] geneNames = geneHeaderInfo.getNames();
		for (int i = 0; i < geneNames.length; i++) {
			out.write(geneNames[i]+"\t");
		}

		writeArrayHeader(out, "AID", -1, arrayHeaderInfo);
	}

	private void writeArrayHeader(FileWriter out, String header, int spacers, HeaderInfo arrayHeaderInfo) throws IOException {
		if (spacers >= 0) {
			out.write(header);
			for (int i = 0; i < spacers; i++) out.write("\t");
		}

		for (int i = 0; i < arrayHeaderInfo.getNumHeaders()-1; i++) {
			out.write(arrayHeaderInfo.getHeader(i, "AID")+"\t");
		}
		out.write(arrayHeaderInfo.getHeader(arrayHeaderInfo.getNumHeaders()-1, "AID")+"\n");
	}

	private void writeDataRow(FileWriter out, HeaderInfo geneHeaderInfo, DataMatrix matrix, int row) throws IOException {
		// Output the headers
		String[] geneNames = geneHeaderInfo.getNames();
		for (int i = 0; i < geneNames.length; i++) {
			out.write(geneHeaderInfo.getHeader(row,geneNames[i])+"\t");
		}

		// Now, output the data
		for (int col = 0; col < matrix.getNumCol()-1; col++) {
			out.write(matrix.getValue(col, row)+"\t");
		}
		out.write(matrix.getValue(matrix.getNumCol()-1, row)+"\n");
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
			writer.write(file);
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
