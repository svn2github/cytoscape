
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

package cytoscape.filters.util;

import cytoscape.Cytoscape;
import cytoscape.filters.AtomicFilter;
import cytoscape.filters.CompositeFilter;
import cytoscape.filters.NumericFilter;
import cytoscape.filters.StringFilter;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;

import java.util.ArrayList;
import java.util.List;


class ApplyFilterThread extends Thread {
	CompositeFilter theFilter = null;

	/**
	 * Creates a new ApplyFilterThread object.
	 *
	 * @param pFilter  DOCUMENT ME!
	 */
	public ApplyFilterThread(CompositeFilter pFilter) {
		theFilter = pFilter;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void run() {
		Cytoscape.getCurrentNetwork().unselectAllNodes();
		Cytoscape.getCurrentNetwork().unselectAllEdges();
		
		testObjects(theFilter);
		Cytoscape.getCurrentNetworkView().updateView();
	}

	private boolean passAtomicFilter(Object pObject, AtomicFilter pAtomicFilter) {
		CyAttributes data = null;
		String name = "";

		if (pObject instanceof CyNode) {
			data = Cytoscape.getNodeAttributes();
			name = ((CyNode) pObject).getIdentifier();
		} else {
			data = Cytoscape.getEdgeAttributes();
			name = ((CyEdge) pObject).getIdentifier();
		}

		if (name == null) {
			return false;
		}

		if (pAtomicFilter instanceof StringFilter) {
			StringFilter theStringFilter = (StringFilter) pAtomicFilter;

			String value = data.getStringAttribute(name,
			                                       theStringFilter.getControllingAttribute().substring(5));

			if (value == null) {
				return false;
			}

			if (theStringFilter == null) {
				return false;
			}

			if (theStringFilter.getSearchStr() == null) {
				return false;
			}

			String[] pattern = theStringFilter.getSearchStr().split("\\s");
			value = value.toLowerCase();

			for (int p = 0; p < pattern.length; ++p) {
				if (!value.matches(pattern[p].toLowerCase())) {
					// this is an OR function
					return false;
				}
			}
		} else if (pAtomicFilter instanceof NumericFilter) {
			NumericFilter theNumericFilter = (NumericFilter) pAtomicFilter;

			Number value;

			if (data.getType(theNumericFilter.getControllingAttribute().substring(5)) == CyAttributes.TYPE_FLOATING)
				value = (Number) data.getDoubleAttribute(name,
				                                         theNumericFilter.getControllingAttribute()
				                                                         .substring(5));
			else
				value = (Number) data.getIntegerAttribute(name,
				                                          theNumericFilter.getControllingAttribute()
				                                                          .substring(5));

			if (value == null) {
				return false;
			}

			Double lowValue = (Double) theNumericFilter.getLowBound();
			Double highValue = (Double) theNumericFilter.getHighBound();

			//To correct the boundary values for lowValue and highValue
			if (lowValue.doubleValue()>0.0) {
				lowValue = lowValue*0.99999;
			}
			else {
				lowValue = lowValue*1.00001;
			}

			if (highValue.doubleValue()>0.0) {
				highValue = highValue*1.00001;
			}
			else {
				highValue = highValue*0.99999;
			}

			//if (!(value.doubleValue() >= lowValue.doubleValue() && value.doubleValue()<= highValue.doubleValue())) {
			if (!((Double.compare(value.doubleValue(), lowValue.doubleValue()) >= 0)
			    && (Double.compare(value.doubleValue(), highValue.doubleValue())) <= 0)) {
				return false;
			}
		}

		return true;
	}

	private boolean passesCompositeFilter(Object pObject, CompositeFilter pFilter) {
		/*
		Vector<AtomicFilter> atomicFilterVect = pFilter.getAtomicFilterVect();

		for (int i = 0; i < atomicFilterVect.size(); i++) {
			boolean passTheAtomicFilter = passAtomicFilter(pObject,
			                                               (AtomicFilter) atomicFilterVect.elementAt(i));

			if (pFilter.getAdvancedSetting().isANDSelected() && !passTheAtomicFilter) {
				return false;
			}

			if (pFilter.getAdvancedSetting().isORSelected() && passTheAtomicFilter) {
				return true;
			}
		}

		if (pFilter.getAdvancedSetting().isANDSelected()) {
			return true;
		} else { // pFilter.getAdvancedSetting().isORSelected()

			return false;
		}
		*/
		return false;
	}

	protected void testObjects(CompositeFilter pCompositeFilter) {
		final CyNetwork network = Cytoscape.getCurrentNetwork();

		final List<CyNode> nodes_list = network.nodesList();
		final List<CyEdge> edges_list = network.edgesList();

		if (pCompositeFilter == null)
			return;

		if (pCompositeFilter.getAdvancedSetting().isNodeChecked()) {
			final List<CyNode> passedNodes = new ArrayList<CyNode>();

			for (CyNode node : nodes_list) {
				try {
					if (passesCompositeFilter(node, pCompositeFilter)) {
						passedNodes.add(node);
					}
				} catch (StackOverflowError soe) {
					soe.printStackTrace();

					return;
				}
			}

			//System.out.println("\tpassedNodes.size() ="+passedNodes.size());
			Cytoscape.getCurrentNetwork().setSelectedNodeState(passedNodes, true);
		}

		if (pCompositeFilter.getAdvancedSetting().isEdgeChecked()) {
			final List<CyEdge> passedEdges = new ArrayList<CyEdge>();

			for (CyEdge edge : edges_list) {
				try {
					if (passesCompositeFilter(edge, pCompositeFilter)) {
						passedEdges.add(edge);
					}
				} catch (StackOverflowError soe) {
					soe.printStackTrace();

					return;
				}
			}

			//System.out.println("\tpassedEdges.size() ="+passedEdges.size());
			Cytoscape.getCurrentNetwork().setSelectedEdgeState(passedEdges, true);
		}
	} //testObjects
}
