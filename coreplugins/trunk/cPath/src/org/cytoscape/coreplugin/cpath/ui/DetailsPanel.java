/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.cytoscape.coreplugin.cpath.ui;

import org.cytoscape.coreplugin.cpath.model.UserSelection;
import org.cytoscape.coreplugin.cpath.util.HtmlUtil;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.ExpressionData;
import cytoscape.data.mRNAMeasurement;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * Interactor / Interaction Details Panel.
 *
 * @author Ethan Cerami.
 */
public class DetailsPanel extends JPanel implements Observer {
    private UserSelection userSelection;
    private TitledBorder border;
    private JEditorPane htmlPane;
    private JFrame parent;

    /**
     * Constructor.
     *
     * @param userSelection Current User Selection
     * @param parent        JFrame Parent Object.
     */
    public DetailsPanel(UserSelection userSelection, JFrame parent) {
        this.userSelection = userSelection;
        this.parent = parent;
        if (userSelection != null) {
            this.userSelection.addObserver(this);
        }
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(border);

        htmlPane = new JEditorPane();
        htmlPane.setContentType("text/html");
        htmlPane.setEditable(false);

        htmlPane.setBackground((Color) UIManager.get("Label.background"));
        htmlPane.setForeground((Color) UIManager.get("Label.foreground"));
        htmlPane.setFont((Font) UIManager.get("Label.font"));

        JScrollPane scrollPane = new JScrollPane(htmlPane);
        scrollPane.setHorizontalScrollBarPolicy
                (JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);

        HTMLDocument doc = (HTMLDocument) htmlPane.getDocument();
        StyleSheet styleSheet = doc.getStyleSheet();
        styleSheet.addRule("body { font-family: Verdana, Helvetica, Arial, "
                + "sans-serif;}");

        htmlPane.setText("Directions:  "
                + "Select a node or edge in the main Cytoscape "
                + "window to view details.");
    }

    /**
     * User has Selected a New Node/Edge in the main Cytoscape Window.
     *
     * @param o   The Observed Object.
     * @param arg Observation Arguments.
     */
    public void update(Observable o, Object arg) {
        // Bring the parent window forward;  fixes bug #0000512.
        parent.setVisible(true);
        if (arg.equals(UserSelection.INTERACTOR_CHANGED)) {
//            updateInteractor();
        } else {
//            updateInteraction();
        }
    }
//
//    /**
//     * Updates Panel with Interactor Details.
//     */
//    private void updateInteractor() {
//        Interactor interactor = userSelection.getSelectedInteractor();
//        String nodeId = userSelection.getSelectedNodeId();
//        border = new TitledBorder("Node Details");
//        setBorder(border);
//        if (interactor != null) {
//            StringBuffer html = new StringBuffer();
//            HtmlUtil.startTable(html);
//            HtmlUtil.addData(html, "Short Name:  ",
//                    interactor.getName().trim());
//            String fullName = (String) interactor.getAttribute
//                    (InteractorVocab.FULL_NAME);
//            if (fullName != null && fullName.length() > 0) {
//                HtmlUtil.addData(html, "Full Name:  ", fullName.trim());
//            }
//
//            String description = interactor.getDescription();
//            if (description != null && description.length() > 0) {
//                HtmlUtil.addData(html, "Description:  ", description.trim());
//            }
//
//            String species = (String) interactor.getAttribute
//                    (InteractorVocab.ORGANISM_SPECIES_NAME);
//            if (species != null && species.length() > 0) {
//                HtmlUtil.addData(html, "Species:  ", species.trim());
//            }
//
//            ExternalReference refs[] = interactor.getExternalRefs();
//            outputExternalRefs(refs, html);
//            outputExpressionData(html, nodeId, refs);
//            outputAdditionalAttributes(html, nodeId);
//            HtmlUtil.endTable(html);
//            htmlPane.setText(html.toString());
//        } else {
//            htmlPane.setText("cPath Data is not "
//                    + "available for the selected node.");
//        }
//        htmlPane.setCaretPosition(0);
//        Dimension before = this.getSize();
//        System.out.println("Preferred Size:  " + this.getPreferredSize());
//        System.out.println("Before:  " + before.toString());
//        this.validate();
//        Dimension after = this.getSize();
//        System.out.println("After:  " + after.toString());
//    }
//
//    /**
//     * Preferred Size is use by the Border Layout.
//     * BorderLayout will only use the width when placed in the EAST.
//     *
//     * @return Dimension Object.
//     */
//    public Dimension getPreferredSize() {
//        return new Dimension(180, 50);
//    }
//
//    private void outputExternalRefs(ExternalReference[] refs,
//            StringBuffer html) {
//        if (refs != null && refs.length > 0) {
//            StringBuffer temp = new StringBuffer();
//            for (int i = 0; i < refs.length; i++) {
//                String db = refs[i].getDatabase();
//                String id = refs[i].getId();
//                temp.append("- " + db + ":  " + id + "<BR>");
//            }
//            HtmlUtil.addData(html, "External References:", temp.toString());
//        }
//    }
//
//    /**
//     * Outputs all Expression Data Associated with this Node.
//     */
//    private void outputExpressionData(StringBuffer html,
//            String nodeId, ExternalReference[] refs) {
//        StringBuffer temp = new StringBuffer();
//        temp.append("<TABLE WIDTH=100% "
//                + "CELLSPACING=2 CELLPADDING=2 BORDER=1>");
//        temp.append("<TR BGCOLOR='#FFFFCC'>");
//        HtmlUtil.addTD(temp, "ID");
//        HtmlUtil.addTD(temp, "Condition");
//        HtmlUtil.addTD(temp, "Ratio");
//        temp.append("</TR>");
//        ExpressionData expData = Cytoscape.getExpressionData();
//
//        int counter = 0;
//        if (expData != null) {
//            //  First try to locate expression data based on node id.
//            counter += extractMeasurement(expData, nodeId, temp);
//
//            //  Then, try to locate expression data based on affymetrix id.
//            if (refs != null) {
//                for (int i = 0; i < refs.length; i++) {
//                    ExternalReference ref = refs[i];
//                    if (ref.getDatabase().equalsIgnoreCase("Affymetrix")) {
//                        counter += extractMeasurement(expData,
//                                ref.getId(), temp);
//                    }
//                }
//            }
//        }
//        if (counter == 0) {
//            temp.append("<TR><TD COLSPAN=3>"
//                    + "No expression data."
//                    + "</TD></TR>");
//        }
//        HtmlUtil.endTable(temp);
//        HtmlUtil.addData(html, "Expression data:", temp.toString());
//    }
//
//    private int extractMeasurement(ExpressionData expData, String id,
//            StringBuffer temp) {
//        int counter = 0;
//        Vector measurements = expData.getMeasurements(id);
//        if (measurements != null) {
//            NumberFormat formatter = new DecimalFormat("#,###,###.###");
//            String conditions[] = expData.getConditionNames();
//            for (int i = 0; i < conditions.length; i++) {
//                mRNAMeasurement mrna =
//                        expData.getMeasurement(id, conditions[i]);
//                if (mrna != null) {
//                    temp.append("<TR>");
//                    HtmlUtil.addTD(temp, id);
//                    HtmlUtil.addTD(temp, conditions[i]);
//                    HtmlUtil.addTD(temp, formatter.format(mrna.getRatio()));
//                    temp.append("</TR>");
//                    counter++;
//                }
//            }
//        }
//        return counter;
//    }
//
//    /**
//     * Outputs all other Numerical Attributes Associated with this Node.
//     */
//    private void outputAdditionalAttributes(StringBuffer html,
//            String nodeId) {
//        StringBuffer temp = new StringBuffer();
//        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
//        NumberFormat formatter = new DecimalFormat("#,###,###.###");
//
//        if (nodeAttributes != null) {
//            Map attributes = CyAttributesUtils.getAttributes(nodeId,nodeAttributes);
//            if (attributes != null) {
//                Set keys = attributes.keySet();
//                if (keys != null) {
//                    Iterator iterator = keys.iterator();
//                    while (iterator.hasNext()) {
//                        Object key = iterator.next();
//                        Object value = attributes.get(key);
//                        if (value instanceof Number) {
//                            temp.append("- " + key + ": "
//                                    + formatter.format(value) + "<BR>");
//                        }
//                    }
//                }
//            }
//        }
//
//        if (temp.length() == 0) {
//            temp.append("None");
//        }
//        HtmlUtil.addData(html, "Additional attributes:", temp.toString());
//    }
//
//    /**
//     * Updates Panel with Interaction Details.
//     */
//    private void updateInteraction() {
//        Interaction interaction = userSelection.getSelectedInteraction();
//        border = new TitledBorder("Edge Details");
//        setBorder(border);
//
//        if (interaction != null) {
//            StringBuffer html = new StringBuffer();
//            HtmlUtil.startTable(html);
//
//            String shortName = (String) interaction.getAttribute
//                    (InteractionVocab.INTERACTION_SHORT_NAME);
//
//            if (shortName != null) {
//                HtmlUtil.addData(html, "Interaction Short Name",
//                        shortName);
//            }
//
//            String fullName = (String) interaction.getAttribute
//                    (InteractionVocab.INTERACTION_FULL_NAME);
//
//            if (fullName != null) {
//                HtmlUtil.addData(html, "Interaction Full Name",
//                        fullName);
//            }
//
//            StringBuffer temp = new StringBuffer();
//            ArrayList interactors = interaction.getInteractors();
//            for (int i = 0; i < interactors.size(); i++) {
//                Interactor interactor = (Interactor) interactors.get(i);
//                temp.append("- " + interactor.getName());
//                if (i < interactors.size() - 1) {
//                    temp.append("<BR>");
//                }
//            }
//            HtmlUtil.addData(html, "Interactors", temp.toString());
//
//            String experimentalEvidence = (String)
//                    interaction.getAttribute
//                            (InteractionVocab.EXPERIMENTAL_SYSTEM_NAME);
//
//            String pmid = (String) interaction.getAttribute
//                    (InteractionVocab.PUB_MED_ID);
//
//            if (experimentalEvidence != null
//                    && experimentalEvidence.length() > 0) {
//                HtmlUtil.addData(html, "Experimental Evidence",
//                        experimentalEvidence);
//                if (pmid != null && pmid.length() > 0) {
//                    HtmlUtil.addData(html, "PMID", pmid);
//                }
//            }
//            outputExternalRefs(interaction.getExternalRefs(), html);
//            HtmlUtil.endTable(html);
//            htmlPane.setText(html.toString());
//        } else {
//            htmlPane.setText("cPath Data is not "
//                    + "available for the selected node.");
//        }
//        htmlPane.setCaretPosition(0);
//    }
}
