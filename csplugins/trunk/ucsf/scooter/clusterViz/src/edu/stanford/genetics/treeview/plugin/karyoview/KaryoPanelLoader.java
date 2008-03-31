/*
 * Created on Feb 2, 2007
 *
 * Copyright Alok Saldnaha, all rights reserved.
 */
package edu.stanford.genetics.treeview.plugin.karyoview;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.*;

import edu.stanford.genetics.treeview.*;
import edu.stanford.genetics.treeview.model.RectData;

/**
 * This is a class that performs the transformation of information from 
 * what's appropriate to a TVModel into what's required for karyoscope display.
 * @author aloksaldanha
 *
 */
class KaryoPanelLoader implements ProgressTrackable {
	private String [] phases = new String [] {"Loading Cdt",
			"Parsing Cdt", "Loading ATR", "Parsing ATR", "Loading GTR", 
			"Parsing GTR", "Loading Document Config", "Finished"};
	/*
	 * first some variables to support a fancy progress bar 
	 */
	private LoadProgress2 loadProgress;
	public int getLength() {return loadProgress.getLength();}
	public int getValue() {return loadProgress.getValue();}
	public void incrValue(int i) {loadProgress.incrValue(i);}
	public void setLength(int i) {loadProgress.setLength(i);}
	public void setValue(int i) {loadProgress.setValue(i);}
	public boolean getCanceled() {return loadProgress.getCanceled();}

	/**
	 * source to load from
	 */
	private ViewFrame sourceFrame;
	
	/**
	 * Target to load into
	 */
	private KaryoPanel targetPanel;

	public void loadInto() throws LoadException {
		loadProgress = new LoadProgress2("Creating Karyoscope View", sourceFrame);
		loadProgress.setPhases(phases);
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				run();
				return null;
			}
		};
		// start up the worker thread
		worker.start();
		// show a modal dialog, should block until loading done...
		loadProgress.setIndeterminate(true);
		loadProgress.pack();
		loadProgress.setVisible(true);
		// System.out.println("loadNew 6, ex: " + fileLoader.getException());
		if (loadProgress.getException() != null) {
			throw loadProgress.getException();
		}
	}
	
	protected void run() {
		/*
		try {
			loadProgress.setPhaseLength(phases.length);
			loadProgress.setPhase(0);
			println("loading " + fileSet.getCdt() + " ... ");
			try {
				parser.setParseQuotedStrings(fileSet.getParseQuotedStrings());
				parser.setResource(fileSet.getCdt());
				parser.setProgressTrackable(this);
				RectData tempTable = parser.loadIntoTable();
				
				if (loadProgress.getCanceled()) return;
				setPhase(1);
				parseCDT(tempTable);
			} catch (LoadException e) {
				throw e;
			} catch (Exception e) {
				// this should never happen!
				LogBuffer.println("TVModel.ResourceLoader.run() : while parsing cdt got error " + e.getMessage());
				e.printStackTrace();
				throw new LoadException("Error Parsing CDT: " + e, LoadException.CDTPARSE);
			}
			if (loadProgress.getCanceled()) return;
			
			setPhase(2);
			if (targetModel.getArrayHeaderInfo().getIndex("AID") != -1) {
				println("parsing atr");
				try {
					parser.setResource(fileSet.getAtr());
					parser.setProgressTrackable(this);
					RectData tempTable = parser.loadIntoTable();
					setPhase(3);
					parseATR(tempTable);
					targetModel.hashAIDs();
					targetModel.hashATRs();
					targetModel.aidFound(true);
				} catch (Exception e) {
					println("error parsing ATR: " + e.getMessage());
					e.printStackTrace();
					println("ignoring array tree.");
					setHadProblem(true);
					targetModel.aidFound(false);
				}
			} else {
				targetModel.aidFound(false);
			}
			
			if (loadProgress.getCanceled()) return;
			setPhase(4);
			if (targetModel.getGeneHeaderInfo().getIndex("GID") != -1) {
				println("parsing gtr");
				try {
					parser.setResource(fileSet.getGtr());
					parser.setProgressTrackable(this);
					RectData tempTable = parser.loadIntoTable();
					if (loadProgress.getCanceled()) return;
					setPhase(5);
					parseGTR(tempTable);
					targetModel.hashGIDs();
					targetModel.hashGTRs();
					targetModel.gidFound(true);
				} catch (Exception e) {
					e.printStackTrace();
					println("error parsing GTR: " + e.getMessage());
					println("ignoring gene tree.");
					setHadProblem(true);
					targetModel.gidFound(false);
				}
			} else {
				targetModel.gidFound(false);
			}
			if (loadProgress.getCanceled()) return;
			setPhase(6);

			try {
				println("parsing jtv config file");
				String xmlFile = targetModel.getFileSet().getJtv();
				
				XmlConfig documentConfig;
				if (xmlFile.startsWith("http:")) {
					documentConfig = new XmlConfig(new URL(xmlFile), "DocumentConfig");
				} else {
					documentConfig = new XmlConfig(xmlFile, "DocumentConfig");
				}
				targetModel.setDocumentConfig(documentConfig);
			} catch (Exception e) {
				targetModel.setDocumentConfig(null);
				println("Got exception " + e);
				setHadProblem(true);
			}
			if (loadProgress.getCanceled()) return;
			setPhase(7);
			if (getException() == null) {
				/*	
				if (!fileLoader.getCompleted()) {
					throw new LoadException("Parse not Completed", LoadException.INTPARSE);
				}
				//System.out.println("f had no exceptoin set");
				*/
			/*
			} else {
				throw getException();
			}
			targetModel.setLoaded(true);
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
		*/
	}

}
