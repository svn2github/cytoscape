/*
  Copyright (c) 2006, 2007, 2008 The Cytoscape Consortium (www.cytoscape.org)

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

package chemViz.tasks;

import java.util.ArrayList;
import java.util.List;

import giny.model.GraphObject;
import giny.view.EdgeView;
import giny.view.NodeView;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.task.util.TaskManager;

import chemViz.commands.ValueUtils;
import chemViz.model.Compound;
import chemViz.model.Compound.AttriType;
import chemViz.ui.ChemInfoSettingsDialog;
import chemViz.ui.CompoundPopup;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.smiles.SmilesGenerator;


/**
 * The CreateCompoundsTask fetches all of the compounds defined by the
 * object passed in its constructor and provides some methods to allow
 * the caller to fetch the compounds when the task is complete.
 */
public class CreateMCSSTask extends AbstractCompoundTask {
	List<GraphObject> objectList;
	ChemInfoSettingsDialog dialog;
	String type;
	CyAttributes attributes;
	List<Compound> compoundList;
	IMolecule mcss = null;
	boolean showResult = false;
	boolean calculationComplete = false;

	/**
 	 * Creates the task.
 	 *
 	 */
  public CreateMCSSTask(List<GraphObject> gObjList, CyAttributes attributes, 
	                      ChemInfoSettingsDialog dialog, boolean showResult) {
		this.objectList = gObjList;
		
		if (gObjList.get(0) instanceof CyNode)
			type = "node";
		else
			type = "edge";
		this.dialog = dialog;
		this.canceled = false;
		this.attributes = attributes;
		this.showResult = showResult;
	}

	public String getTitle() {
		return "Calculating MCSS";
	}

	public boolean isDone() {
		return calculationComplete;
	}

	public String getMCSSSmiles() {
		SmilesGenerator g = new SmilesGenerator();
		return g.createSMILES(mcss);
	}

	/**
 	 * Runs the task -- this will get all of the compounds, fetching the images (if necessary) and creates the popup.
 	 */
	public void run() {
		compoundList = getCompounds(objectList, attributes,
                                dialog.getCompoundAttributes(type,AttriType.smiles),
                                dialog.getCompoundAttributes(type,AttriType.inchi));

		mcss = compoundList.get(0).getIMolecule();
		try {
			for (int index = 1; index < compoundList.size(); index++) {
				List<IAtomContainer> overlap = UniversalIsomorphismTester.getOverlaps(mcss, compoundList.get(index).getIMolecule());
				mcss = maximumStructure(overlap);
				if (mcss == null) break;
			}
		} catch (CDKException e) {}
		calculationComplete = true;	
		if (showResult) {
			String mcssSmiles = getMCSSSmiles();
			String label = "MCSS = "+mcssSmiles;
			List<Compound> mcssList = ValueUtils.getCompounds(null, mcssSmiles, AttriType.smiles, null, null);
			CreatePopupTask loader = new CreatePopupTask(mcssList, null, dialog, label, 1);
			TaskManager.executeTask(loader, loader.getDefaultTaskConfig());
		}
	}

	private IMolecule maximumStructure(List<IAtomContainer> mcsslist) {
		int maxmcss = -99999999;
		IAtomContainer maxac = null;
		if (mcsslist == null || mcsslist.size() == 0) return null;
		for (IAtomContainer a: mcsslist) {
			if (a.getAtomCount() > maxmcss) {
				maxmcss = a.getAtomCount();
				maxac = a;
			}
		}
		return new Molecule(maxac);
	}

	public List<Compound>getCompoundList() { return compoundList; }
}
