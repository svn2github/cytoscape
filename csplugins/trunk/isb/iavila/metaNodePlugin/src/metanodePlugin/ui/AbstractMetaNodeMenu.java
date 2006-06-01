/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/
package metanodePlugin.ui;
import edu.umd.cs.piccolo.PNode;
import javax.swing.JMenu;

/**
 * Small class for Cytoscape's context menu that contains collapse/expand/destroy operations for meta-nodes.
 * 
 * @author Iliana Avila-Campillo iavila@systemsbiology.org, iliana.avila@gmail.com
 */
public class AbstractMetaNodeMenu {

	/**
	 * The MNcollapserDialog that contains the actions for this menu.
	 */
	protected static MNcollapserDialog dialog;
	
	/**
	 * @param dialog the MNcollapserDialog that contains the actions for the menu.
	 */
	public static void setCollapserDialog (MNcollapserDialog dialog){
		AbstractMetaNodeMenu.dialog = dialog;
	}//AbstractMetaNodeMenu
	
	public static JMenu getAbstractMetaNodeMenu (Object [] args){
		return AbstractMetaNodeMenu.dialog.getMenu(args);
	}//getAbstractMetaNodeMenu
	
}//AbstractMetaNodeMenu
