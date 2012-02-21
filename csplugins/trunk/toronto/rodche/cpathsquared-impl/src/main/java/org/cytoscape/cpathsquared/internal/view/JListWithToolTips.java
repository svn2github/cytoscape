package org.cytoscape.cpathsquared.internal.view;

import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JList;
import javax.swing.ListModel;

import cpath.service.jaxb.SearchHit;


/**
 * Extension to JList to Support Tool Tips.
 * <p/>
 * Based on sample code from:
 * http://www.roseindia.net/java/example/java/swing/TooltipTextOfList.shtml
 *
 * @author Ethan Cerami
 */
public class JListWithToolTips extends JList {

    public JListWithToolTips(ListModel listModel) {
        super(listModel);
    }

    /**
     * Impelement Tool Tip Functionality.
     *
     * @param mouseEvent Mouse Event.
     * @return Tool Tip.
     */
	public String getToolTipText(MouseEvent mouseEvent) {
		int index = locationToIndex(mouseEvent.getPoint());
		if (-1 < index) {
			SearchHit record = (SearchHit) getModel().getElementAt(index);
			StringBuffer html = new StringBuffer();
			html.append("<html>");
			html.append("<table cellpadding=10><tr><td>");
			html.append("<B>" + record.getName() + "</B>&nbsp;&nbsp;");

			List<String> organisms = record.getOrganism();
			if (!organisms.isEmpty()) {
				html.append(organisms.toString());
			}

			html.append("</td></tr></table>");
			html.append("</html>");
			return html.toString();
		} else {
			return null;
		}
	}


}
