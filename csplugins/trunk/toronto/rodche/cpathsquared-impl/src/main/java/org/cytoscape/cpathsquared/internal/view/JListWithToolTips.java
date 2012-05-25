package org.cytoscape.cpathsquared.internal.view;

import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.ListModel;

import cpath.service.jaxb.SearchHit;


/**
 * Extension to JList to Support Tool Tips.
 * <p/>
 * Based on sample code from:
 * http://www.roseindia.net/java/example/java/swing/TooltipTextOfList.shtml
 *
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
    @Override
	public String getToolTipText(MouseEvent mouseEvent) {
		int index = locationToIndex(mouseEvent.getPoint());
		if (-1 < index) {
			SearchHit record = (SearchHit) getModel().getElementAt(index);
			StringBuilder html = new StringBuilder();
			html.append("<html><table cellpadding=10><tr><td>");
			html.append("<B>").append(record.getBiopaxClass());
			if(record.getName() != null)
				html.append("&nbsp;").append(record.getName());
			html.append("</B>&nbsp;");
			html.append("</td></tr></table></html>");
			return html.toString();
		} else {
			return null;
		}
	}

}
