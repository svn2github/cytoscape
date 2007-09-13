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
package dynamicXpr;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
/**
 * A plugin that allows the user to see the changes in gene expression
 * by coloring nodes according to their expression values in different 
 * conditions. It colors the nodes sequentially through each condition
 * at a user set delay between conditions.
 *
 * @author Iliana Avila-Campillo iavila@systemsbiology.net
 * @version %I%, %G%
 * @since 1.1
 */
public class DynamicXprPlugIn extends CytoscapePlugin{

  /**
   * Constructor.
   */
  public DynamicXprPlugIn (){
    Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(new DynamicExpression());
  }//DynamicXprPlugIn
  
  /**
   * @return a description of this plug-in
   */
  public String describe (){
    return "A Plug-In that colors nodes according to their expression across many conditions, as in a movie";
  }//describe
  
}//class DynamicXprPlugIn
