
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

package cytoscape.layout.manual.common;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import org.cytoscape.view.GraphView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public class SliderStateTracker implements  PropertyChangeListener {

	String preFocusedViewId;
	Map<String,Integer> layoutStateMap;
	PolymorphicSlider slider;

	public SliderStateTracker(PolymorphicSlider s) {
		Cytoscape.getDesktop().getNetworkViewManager().getSwingPropertyChangeSupport()
		         .addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUSED,this);
		layoutStateMap = new HashMap<String,Integer>();
		preFocusedViewId = "none";
		slider = s;
	}

	/** 
	 * Keep track of the state for each view focused.
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUSED) {
			GraphView curr = Cytoscape.getCurrentNetworkView();

			if ( curr == null || curr == Cytoscape.getNullNetworkView() )
				return;

			String curFocusedViewId = curr.getIdentifier();

			// detect duplicate NETWORK_VIEW_FOCUSED event 
			if (preFocusedViewId.equals(curFocusedViewId)) {
				return;
			}

			//save layout state for the previous network view
			layoutStateMap.put(preFocusedViewId, slider.getSliderValue());

			//Restore layout state for the current network view, if any
			Integer stateValue = layoutStateMap.get(curFocusedViewId);

			if (stateValue == null) {
				slider.updateSlider(0);
			} else {
				slider.updateSlider(stateValue.intValue());
			}

			preFocusedViewId = curFocusedViewId;
		}
	}
}
