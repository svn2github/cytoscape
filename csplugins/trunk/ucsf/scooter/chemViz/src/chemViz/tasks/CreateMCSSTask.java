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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import giny.model.GraphObject;
import giny.view.EdgeView;
import giny.view.NodeView;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.logger.CyLogger;
import cytoscape.task.util.TaskManager;

import chemViz.commands.ValueUtils;
import chemViz.model.Compound;
import chemViz.model.Compound.AttriType;
import chemViz.ui.ChemInfoSettingsDialog;
import chemViz.ui.CompoundPopup;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
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
	boolean createGroup = false;
	boolean calculationComplete = false;
	private static CyLogger logger = CyLogger.getLogger(CreateMCSSTask.class);

	/**
 	 * Creates the task.
 	 *
 	 */
  public CreateMCSSTask(List<GraphObject> gObjList, CyAttributes attributes, 
	                      ChemInfoSettingsDialog dialog, boolean showResult, boolean createGroup) {
		this.objectList = gObjList;
		
		if (gObjList.get(0) instanceof CyNode)
			type = "node";
		else
			type = "edge";
		this.dialog = dialog;
		this.canceled = false;
		this.attributes = attributes;
		this.showResult = showResult;
		this.createGroup = createGroup;
	}

	public String getTitle() {
		return "Calculating MCSS";
	}

	public boolean isDone() {
		return calculationComplete;
	}

	public String getMCSSSmiles() {
		SmilesGenerator g = new SmilesGenerator();
		g.setUseAromaticityFlag(true);
		return g.createSMILES(mcss);
	}

	/**
 	 * Runs the task -- this will get all of the compounds, fetching the images (if necessary) and creates the popup.
 	 */
	public void run() {
		int maxThreads = dialog.getMaxThreads();
		compoundList = getCompounds(objectList, attributes,
                                dialog.getCompoundAttributes(type,AttriType.smiles),
                                dialog.getCompoundAttributes(type,AttriType.inchi), maxThreads);

		int nThreads = Runtime.getRuntime().availableProcessors()-1;
		if (maxThreads > 0) nThreads = maxThreads;

		List<IAtomContainer> mcssList = Collections.synchronizedList(new ArrayList<IAtomContainer>(compoundList.size()));
		for (Compound c: compoundList) {
			mcssList.add(c.getIMolecule());
		}

		int pass = 0;
		while (mcssList.size() > 1) {
			mcssList = calculateMCSS(mcssList, nThreads);
			pass++;
		}
		mcss = (IMolecule)mcssList.get(0);

		calculationComplete = true;	
		if (showResult) {
			String mcssSmiles = getMCSSSmiles();
			String label = mcssSmiles;
			// List<Compound> mcssList = ValueUtils.getCompounds(null, mcssSmiles, AttriType.smiles, null, null);
			Compound c = new Compound(null, null, mcssSmiles, mcss, AttriType.smiles);
			List<Compound> mcssList2 = new ArrayList<Compound>();
			mcssList2.add(c);
			CreatePopupTask loader = new CreatePopupTask(mcssList2, null, dialog, label, 1);
			loader.setDialogTitle("Maximum Common SubStructure");
			TaskManager.executeTask(loader, loader.getDefaultTaskConfig());
		}

		if (createGroup) {
			List<CyNode> nodeList = new ArrayList<CyNode>();
			HashSet<CyGroup> groupSet = null;
			String groupName = "";
			boolean newGroup = false;

			// Only create a new group if the nodes aren't already in a group
			for (GraphObject obj: objectList) {
				if (!(obj instanceof CyNode)) {
					// System.out.println("obj "+obj.toString()+" is not a node");
					return;
				}

				CyNode node = (CyNode) obj;
				nodeList.add(node);
				groupName += ","+node.getIdentifier();

				if (newGroup)
					continue;

				List<CyGroup> myGroups = node.getGroups();
				if (myGroups != null && myGroups.size() > 0) {
					if (groupSet == null) {
						groupSet = new HashSet<CyGroup>(myGroups);
					} else {
						groupSet.retainAll(myGroups);
						if (groupSet.size() == 0)
							newGroup = true;
					}
				} else {
					newGroup = true;
				}
			}

			// System.out.println("newGroup = "+newGroup+" nodeList.size() = "+nodeList.size());

			CyGroup group = null;
			String smilesString = getMCSSSmiles();

			if (!newGroup) {
				// All nodes already are part of at least one group
				group = groupSet.iterator().next(); // Get the first group
			} else {
				if (groupName.length() > 16)
					groupName = groupName.substring(0,16)+"...";
				group = CyGroupManager.createGroup("MCSS of ["+groupName.substring(1)+"]", nodeList, "metaNode", Cytoscape.getCurrentNetwork());
				CyGroupManager.notifyCreateGroup(group);
			}
			group.setState(2); // Collapse it?

			CyNode node = group.getGroupNode();
			String attribute = compoundList.get(0).getAttribute(); // get the attribute
			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			nodeAttributes.setAttribute(node.getIdentifier(), attribute, getMCSSSmiles());
		}
	}

	public List<Compound>getCompoundList() { return compoundList; }


	private List<IAtomContainer> calculateMCSS(List<IAtomContainer>mcssList, int nThreads) {
		List<IAtomContainer> newMCSSList = Collections.synchronizedList(new ArrayList<IAtomContainer>(nThreads));
		List<GetMCSSTask> taskList = new ArrayList<GetMCSSTask>();

		if (nThreads == 1) {
			GetMCSSTask task = new GetMCSSTask(mcssList, newMCSSList);
			task.call();
		} else {
			int step = (int)Math.ceil((double)mcssList.size()/(double)nThreads);
			if (step < 2) step = 2; // Can't have a step size of less than 2
			for (int i = 0; i < mcssList.size(); i=i+step) {
				int endPoint = i+step;
				if (endPoint > mcssList.size())
					endPoint = mcssList.size();
				taskList.add(new GetMCSSTask(mcssList.subList(i, endPoint), newMCSSList));
			}

			ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);
			try {
				threadPool.invokeAll(taskList);
			} catch (Exception e) {
				logger.warning("Execution exception: "+e);
			}
		}
		return newMCSSList;
	}

	private class GetMCSSTask implements Callable <IAtomContainer> {
		List<IAtomContainer> mcssList;
		List<IAtomContainer> resultsList;
		IAtomContainer mcss = null;

		public GetMCSSTask(List<IAtomContainer>mcssList, List<IAtomContainer>resultsList) {
			this.mcssList = mcssList;
			this.resultsList = resultsList;
		}

		public IAtomContainer call() {
			mcss = mcssList.get(0);
			try {
				for (int index = 1; index < mcssList.size(); index++) {
					List<IAtomContainer> overlap = UniversalIsomorphismTester.getOverlaps(mcss, mcssList.get(index));
					mcss = maximumStructure(overlap);
					if (mcss == null) break;
				}
			} catch (CDKException e) {
				System.out.println("CDKException: "+e);
			}
			resultsList.add(mcss);
			return mcss;
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

		public IAtomContainer get() { 
			if (mcss == null) 
				return call();
			else
				return mcss; 
		}
	}
}
