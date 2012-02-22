package org.cytoscape.cpathsquared.internal.view;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.text.Document;

import cpath.service.jaxb.*;

import org.apache.commons.lang.StringUtils;


public final class SelectEntity {

    public final void selectItem(
            SearchHit item, 
            Document summaryDocumentModel, 
            JTextPane textPane, 
            JComponent textPaneOwner) 
    {
    	
        if (item != null) {
            StringBuffer html = new StringBuffer();
            html.append("<html>");

            html.append ("<h2>" + item.getName() + "</h2>");
            html.append ("<h3>Class: " + item.getBiopaxClass() + "</h3>");
            html.append ("<h3>URI: " + item.getUri() + "</h3>");

            List<String> items = item.getOrganism();
            if (items != null && !items.isEmpty()) {
                html.append ("<H3>Organisms:<br/>" + StringUtils.join(items, "<br/>") + "</H3>");
            }

            items = item.getPathway();
            if (items != null && !items.isEmpty()) {
                html.append ("<H3>Pathway URIs:<br/>" + StringUtils.join(items, "<br/>") + "</H3>");
            }
            
            items = item.getDataSource();
            if (items != null && !items.isEmpty()) {
                html.append ("<H3>Data sources:<br/>" + StringUtils.join(items, "<br/>") + "</H3>");
            }
            
            String primeExcerpt = item.getExcerpt();
            if (primeExcerpt != null) {
                html.append("<H4>Matched in</H4>");
                html.append("<span class='excerpt'>" + primeExcerpt + "</span><BR>") ;
            }
            
            
            //TODO add more details here

            html.append ("</html>");
            textPane.setText(html.toString());
            textPane.setCaretPosition(0);

			textPaneOwner.repaint();
        }
    }


}