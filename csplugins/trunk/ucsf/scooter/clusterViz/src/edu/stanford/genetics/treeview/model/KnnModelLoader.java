/* BEGIN_HEADER                                              Java TreeView
*
* $Author: alokito $
* $RCSfile: KnnModelLoader.java,v $
* $Revision: 1.11 $
* $Date: 2007/02/03 04:58:37 $
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

import java.io.IOException;
import java.util.Vector;

import javax.swing.*;

import edu.stanford.genetics.treeview.*;

/**
 * @author aloksaldanha
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class KnnModelLoader extends TVModelLoader2 {
	
	/**
	 * @param model
	 */
	public KnnModelLoader(KnnModel model) {
		super(model);
		loadProgress.setPhases(new String [] {"Starting",
				"Loading CDT", "Parsing CDT", 
				"Parsing KGG", "Parsing KAG", 
				"Loading Document Config", "Finished"});
	}
	

	private KnnModel getKnnModel() {
		// localize the cast
		return (KnnModel) targetModel;
	}

	
	/**
	 * This run() completely overrides the run() of TVModelLoader2.
	 * 
	 * @see edu.stanford.genetics.treeview.model.TVModelLoader2#run()
	 */
	protected void run() {
		try{
			KnnModel model = getKnnModel();
			FileSet fileSet = targetModel.getFileSet();
			setPhase(0);
			model.gidFound(false);
			model.aidFound(false);
			
			setPhase(1);
			println("loading " + fileSet.getCdt() + " ... ");
			try {
				parser.setResource(fileSet.getCdt());
				parser.setProgressTrackable(this);
				RectData tempTable = parser.loadIntoTable();
				setPhase(2);
				parseCDT(tempTable);
			} catch (LoadException e) {
				throw e;
			} catch (Exception e) {
				// this should never happen!
				LogBuffer.println("TVModelLoader2.run() : while parsing cdt got error " + e.getMessage());
				LogBuffer.println("TVModel instance " + targetModel.getType());
				e.printStackTrace();
				throw new LoadException("Error Parsing CDT: " + e, LoadException.CDTPARSE);
			}

			String kggfilename = fileSet.getKgg();
			if (kggfilename != "") {
				println ("parsing kgg");
				try {
					parser.setResource(fileSet.getKgg());
					parser.setProgressTrackable(this);
					setPhase(3);
					RectData tempTable = parser.loadIntoTable();
					model.setGClusters(tempTable, LoadException.KGGPARSE);
				} catch (Exception e) {
					e.printStackTrace();
					println("ignoring gene k-means clusters.");
					setHadProblem(true);
				}
			}
			
			String kagfilename = fileSet.getKag();
			if (kagfilename != "") {
				println ("parsing kag");
				try {
					parser.setResource(fileSet.getKag());
					parser.setProgressTrackable(this);
					setPhase(4);
					RectData tempTable = parser.loadIntoTable();
					model.setAClusters(tempTable, LoadException.KAGPARSE);
				} catch (Exception e) {
					println("error parsing KAG: " + e.getMessage());
					e.printStackTrace();
					println("ignoring array k-means clusters.");
					setHadProblem(true);
				}
			}

			setPhase(5);
			try {
				println("parsing jtv config file");
				XmlConfig documentConfig = new XmlConfig(targetModel.getFileSet().getJtv(),
				"DocumentConfig");
				targetModel.setDocumentConfig(documentConfig);
			} catch (Exception e) {
				targetModel.setDocumentConfig(null);
				println("Got exception " + e);
				setHadProblem(true);
			}

			setPhase(6);
			if (getException() == null) {
				/*	
				 if (!fileLoader.getCompleted()) {
				 throw new LoadException("Parse not Completed", LoadException.INTPARSE);
				 }
				 //System.out.println("f had no exceptoin set");
				  */
			} else {
				throw getException();
			}
			//	ActionEvent(this, 0, "none",0);
		} catch (java.lang.OutOfMemoryError ex) {
			JPanel temp = new JPanel();
			temp. add(new JLabel("Out of memory, allocate more RAM"));
			temp. add(new JLabel("see Chapter 3 of Help->Documentation... for Out of Memory"));
			JOptionPane.showMessageDialog(parent,  temp);
		} catch (LoadException e) {
			setException(e);
			println("error parsing File: " + e.getMessage());
			println("parse cannot succeed. please fix.");
			setHadProblem(true);
		}
		setFinished(true);
		
	}
	
}
