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
package VizmapInit;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import javax.swing.JMenuItem;
/**
 * A plugin that sets a visual style for online tutorials.
 *
 * @author Melissa Cline, cline@pasteur.fr
 * @version %I%, %G%
 * @since 2.3
 */
public class VizmapInitPlugin extends CytoscapePlugin{

  /**
   * Constructor.
   */
  public VizmapInitPlugin (){
       (new VizmapInit()).prepareVizmapper();
    }//VizmapInitPlugin
  
  /**
   * @return a description of this plug-in
   */
  public String describe (){
      return "A Plug-In that sets the visual style for online tutorials";
  }//describe
  

    public void activate () {
	(new VizmapInit()).setVisualStyle();
    }
}//class VizmapInitPlugin
