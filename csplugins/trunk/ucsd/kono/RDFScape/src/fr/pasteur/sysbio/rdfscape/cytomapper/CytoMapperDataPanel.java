/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Oct 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.cytomapper;

import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CytoMapperDataPanel extends JPanel {
	CytoMapper cytoMapper=null;
	JProgressBar dataCoverage=null;
	
	/**
	 * 
	 */
	public CytoMapperDataPanel(CytoMapper ct) {
		super();
		cytoMapper=ct;
	}

	
}
