package org.cytoscape.cpathsquared.internal.view;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.text.Document;

import cpath.service.jaxb.*;

import org.apache.commons.lang.StringUtils;


public final class SelectMemberEntity {

    public final void selectItem(
    		int selectedIndex,
            RecordList results, 
            Document summaryDocumentModel, 
            JTextPane textPane, 
            JComponent textPaneOwner) 
    {
    	
        if (results != null) {
            List<SearchHit> searchHits = results.getHits();
            SearchHit searchHit = searchHits.get(selectedIndex);

            StringBuffer html = new StringBuffer();
            html.append("<html>");

            html.append ("<h2>" + searchHit.getName() + "</h2>");
            html.append ("<h3>Class: " + searchHit.getBiopaxClass() + "</h3>");
            html.append ("<h3>URI: " + searchHit.getUri() + "</h3>");

            List<String> items = searchHit.getOrganism();
            if (items != null && !items.isEmpty()) {
                html.append ("<H3>Organisms:<br/>" + StringUtils.join(items, "<br/>") + "</H3>");
            }

            items = searchHit.getPathway();
            if (items != null && !items.isEmpty()) {
                html.append ("<H3>Pathway URIs:<br/>" + StringUtils.join(items, "<br/>") + "</H3>");
            }
            
            items = searchHit.getDataSource();
            if (items != null && !items.isEmpty()) {
                html.append ("<H3>Data sources:<br/>" + StringUtils.join(items, "<br/>") + "</H3>");
            }
            
            String primeExcerpt = searchHit.getExcerpt();
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