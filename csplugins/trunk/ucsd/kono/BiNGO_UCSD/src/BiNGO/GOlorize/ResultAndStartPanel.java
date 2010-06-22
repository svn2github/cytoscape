/*
 * ResultAndStartPanel.java
 *
 * Created on August 5, 2006, 6:37 PM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * The software and documentation provided hereunder is on an "as is" basis,
 * and the Pasteur Institut
 * has no obligations to provide maintenance, support,
 * updates, enhancements or modifications.  In no event shall the
 * Pasteur Institut
 * be liable to any party for direct, indirect, special,
 * incidental or consequential damages, including lost profits, arising
 * out of the use of this software and its documentation, even if
 * the Pasteur Institut
 * has been advised of the possibility of such damage. See the
 * GNU General Public License for more details: 
 *                http://www.gnu.org/licenses/gpl.txt.
 *
 * Authors: Olivier Garcia
 */

package BiNGO.GOlorize;

/**
 *
 * @author ogarcia
 */
public interface ResultAndStartPanel {
    
    public cytoscape.data.annotation.Ontology getOntology();
    public cytoscape.data.annotation.Annotation getAnnotation();
    public cytoscape.data.annotation.Ontology getOntology(String term);
    public cytoscape.data.annotation.Annotation getAnnotation(String term);
    public int getSelectColumn();
    public int getGoTermColumn();
    public int getDescriptionColumn();
    public javax.swing.JTable getJTable();
    public GoBin getGoBin();
    public boolean isSelected(String term);
    public boolean select(String term);
    public boolean unselect(String term);
    public cytoscape.view.CyNetworkView getNetworkView();
    public java.util.HashMap getAlias() ;
}
