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
/**
 * A node size calculator for meta-nodes.
 *
 * @author Iliana Avila-Campillo iavila@systemsbiology.org, iliana.avila@gmail.com
 * @version %I%, %G%
 * @since 2.0
 */
package metaNodeViewer.view;

import java.util.Map;
import java.util.Properties;
import cytoscape.CyNetwork;
import cytoscape.visual.calculators.GenericNodeSizeCalculator;
import cytoscape.visual.mappings.ObjectMapping;
import giny.model.Node;

public class SpecificNodeSizeCalculator extends GenericNodeSizeCalculator {
  
  protected String specialAttrName;
  
  public SpecificNodeSizeCalculator (String name, ObjectMapping m){
    super(name, m);
  }//SpecificNodeSizeCalculator
  
  public SpecificNodeSizeCalculator (String name, Properties props, String baseKey){
    super(name, props, baseKey);
    //extra code to extract the name of the bypass data attribute
    String spKey = baseKey + ".specialAttrName";
    specialAttrName = props.getProperty(spKey);
  }//SpecificNodeSizeCalculator
    
  public String getSpecialAttrName () {return specialAttrName;}//getSpecialAttrName
  
  public void setSpecialAttrName (String spName) {
    specialAttrName = spName;
  }//setSpecialAttrName
  
  public double calculateNodeSize (Node node, CyNetwork network) {
    String canonicalName = network.getNodeAttributes().getCanonicalName(node);
    Map attrBundle = network.getNodeAttributes().getAttributes(canonicalName);
    if (attrBundle == null || specialAttrName == null) {
      //System.out.println("Node " + node + " does not have an attribute bundle");
      return super.calculateNodeSize(node, network);
    }
    Object attrValue = attrBundle.get(specialAttrName);
    if (attrValue == null || !(attrValue instanceof Double)) {
      //if(attrValue == null){
      //System.out.println("Node " + node + " has a null attrValue");
      //}else if(!(attrValue instanceof Double)){
      //System.out.println("Node " + node + " attrValue is not an instance of Double");
      //}
      return super.calculateNodeSize(node, network);
    }
    double d = ((Number)attrValue).doubleValue();
    return d;
  }//calculateNodeSize
  
  public Properties getProperties(String baseKey) {
    Properties newProps = super.getProperties(baseKey);
    //extra code to save the bypass data attribute name
    String spKey = baseKey + ".specialAttrName";
    newProps.setProperty(spKey, specialAttrName);
    return newProps;
  }//getProperties

}//class SpecificNodeSizeCalculator
