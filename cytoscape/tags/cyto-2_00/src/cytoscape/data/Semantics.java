/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------

package cytoscape.data;

import java.util.*;

import giny.model.Edge;

import cytoscape.*;
import cytoscape.CytoscapeObj;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.servers.BioDataServer;

/**
 * This class defines names for certain data attributes that are commonly used
 * within Cytoscape. The constants defined here are provided to enable different
 * modules to use the same name when referring to the same conceptual attribute.
 *
 * This class also defines some static methods for assigning these attributes
 * to a network, given the objects that serve as the source for this information.
 */
public class Semantics {

  public static final String IDENTIFIER = "identifier";
  public static final String CANONICAL_NAME = "canonicalName";
  public static final String COMMON_NAME = "commonName";
  public static final String SPECIES = "species";
  public static final String INTERACTION = "interaction";
  public static final String MOLECULE_TYPE = "molecule_type";
  public static final String PROTEIN = "protein";
  public static final String DNA = "DNA";
  public static final String RNA = "RNA";
  

  /**
   * This method should be called in the process of creating a new network,
   * or upon loading a new bioDataServer. This method will use the bioDataServer
   * to assign common names to the network using the synonym utilities of
   * the bioDataServer. In the process, it will assign a species attribute
   * for any name that does not currently have one.
   *
   * Currently, this method calls assignSpecies and assignCommonNames. At some point
   * it may be desirable to check the configuration to see what to do, or put
   * up a UI to prompt the user for what services they would like.
   */
  public static void applyNamingServices(  cytoscape.CyNetwork network, CytoscapeObj cytoscapeObj) {
    //assignSpecies(network, cytoscapeObj);
    assignCommonNames(network, cytoscapeObj.getBioDataServer());
  }
 
  /**
   * This method attempts to set a species attribute for every canonical name
   * defined in the node attributes member of the supplied network. The value
   * returned by getDefaultSpecies is used; if this return value is null, then
   * this method exits without doing anything, as there is no species to set.
   *
   * If a canonical name already has an entry for the SPECIES attribute, then
   * this method does not change that value. Otherwise, this method sets the
   * value of that attribute to that returned by getDefaultSpecies.
 
   * This method does nothing at all if either argument is null.
   */
  public static void assignSpecies( cytoscape.CyNetwork network, CytoscapeObj cytoscapeObj) {
    if (network == null || cytoscapeObj == null) {return;}
    
    String defaultSpecies = getDefaultSpecies(network, cytoscapeObj);
    if (defaultSpecies == null) {return;} //we have no value to set
    
    String callerID = "Semantics.assignSpecies";
    //    network.beginActivity(callerID);
    GraphObjAttributes nodeAttributes = Cytoscape.getNodeNetworkData();
    String[] canonicalNames = nodeAttributes.getObjectNames(CANONICAL_NAME);
    for (int i=0; i<canonicalNames.length; i++) {
      String canonicalName = canonicalNames[i];
      String species = nodeAttributes.getStringValue(SPECIES, canonicalName);
      if (species == null) { //only do something if no value exists
        nodeAttributes.set(SPECIES, canonicalName, defaultSpecies);
      }
    }
    // network.endActivity(callerID);
  }

  /**
   * This method attempts to identify a default species for the given network.
   * This method encapsulates the rules for storing this piece of information.
   * Currently, this method tries to get the value from the configuration
   * member of the CytoscapeObj argument, first by calling
   * config.getDefaultSpeciesName(), then by calling
   * config.getProperties.getProperty(SPECIES);
   *
   * The first non-null value found is returned; otherwise, null is returned
   * indicating that no value could be found.
   */
  public static String getDefaultSpecies( cytoscape.CyNetwork network, CytoscapeObj cytoscapeObj) {
    String defaultSpecies = cytoscapeObj.getConfiguration().getDefaultSpeciesName();
    if (defaultSpecies == null) {
      defaultSpecies =
        cytoscapeObj.getConfiguration().getProperties().getProperty(SPECIES);
    }
    return defaultSpecies;
  }
 
  /**
   * Returns every unique species defined in the supplied network. Searches the
   * species attribute in the node attributes of the supplied network and returns
   * a Set containing every unique value found.
   */
  public static Set getSpeciesInNetwork( cytoscape.CyNetwork network) {
    Set returnSet = new HashSet();
    if (network == null) {return returnSet;}
    GraphObjAttributes nodeAttributes = Cytoscape.getNodeNetworkData();
    if (nodeAttributes == null) {return returnSet;}
    //in the following map, keys are objects names and values are the species
    Map speciesAttribute = nodeAttributes.getAttribute(SPECIES);
    if (speciesAttribute == null) {return returnSet;}
    //we will return each unique value stored in this map, without worrying
    //about the type; thus, for example, if some node has several species
    //defined as an array of Strings, we'll quietly add the array to our set
    returnSet.addAll(speciesAttribute.values());
    return returnSet;
  }

  
  /**
   * Use the given BioDataServer to set all of the aliases for a node, given its
   * species
   * @param node the Node that will be assigned names
   * @param species the species of the Node ( NOTE: if null, there will be a check to see if 
   *                the node has a species set for attribute Semantics.SPECIES, if not,
   *                then the general Cytoscape defaultSpecies ( settable with -s ) will be
   *                used. )
   * @param bds  the given BioDataServer ( NOTE: if null, then the general Cytoscape 
   *             BioDataServer will be used ( settable with -b ) ).
   */
  public static void assignNodeAliases ( CyNode node, String species, BioDataServer bds ) {

    // can't have a null node
    if ( node == null )
      return;

    if ( species == null ) {
      if ( Cytoscape.getNodeAttributeValue( node, SPECIES ) != null ) {
        species = ( String )Cytoscape.getNodeAttributeValue( node, SPECIES );
      } else {
        species = Cytoscape.getCytoscapeObj().getConfiguration().getDefaultSpeciesName();
      }
    }
    Cytoscape.setNodeAttributeValue( node, SPECIES, species );

    if ( bds == null )
      bds = Cytoscape.getCytoscapeObj().getBioDataServer();

    // return if no deafult BioDataServer
    if ( bds == null )
      return;

    // now do the name assignment

    String name = node.getIdentifier().toUpperCase();
    String cname = bds.getCanonicalName( species, name );
    // name was not canonical name
    if ( name != cname )
      node.setIdentifier( cname );

    Cytoscape.setNodeAttributeValue( node, CANONICAL_NAME, cname );
      
    System.out.println( "Name: "+name+" cname: "+cname +" species: "+species);

    String[] synonyms = bds.getAllCommonNames(species, cname);
    StringBuffer concat = new StringBuffer();
    String common_name = null;
    for ( int j = 0; j < synonyms.length; ++j ) {
      concat.append( synonyms[j]+" " );
      if ( common_name == null )
        common_name = synonyms[j];
    }
    if ( common_name == null )
      common_name = cname;
    Cytoscape.setNodeAttributeValue( node, "ALIASES", concat.toString() );
    Cytoscape.setNodeAttributeValue( node, COMMON_NAME, common_name );
  }

 
  /**
   * This method takes every canonical name defines in the node attributes of
   * the given network, and attempts to assign a common name by getting a
   * list of synonyms for the canonical name from the bioDataServer and
   * using the first synonym as the common name.
   *
   * This operation requires the SPECIES attribute to be defined for the
   * canonical name, as input to the bioDataServer. Any canonicalName that
   * does not have this attribute defined is skipped.
   *
   * This method does nothing if either argument is null. Also, for any
   * canonical name, this method does nothing if no synonyms for that name
   * can be provided by the bioDataServer.
   */
  public static void assignCommonNames( cytoscape.CyNetwork network, BioDataServer bioDataServer) {
    if (network == null || bioDataServer == null) {return;}
    List nodes = network.nodesList();
    for ( Iterator i = nodes.iterator(); i.hasNext(); ) {

      CyNode node = ( CyNode )i.next();
      assignNodeAliases( node, null, bioDataServer );
    }
  }
  //-------------------------------------------------------------------------
  /**
   * This method determines whether the bioDataServer should be used to canonicalize
   * names of objects as they are read from file. Currently, this method delegates
   * to a member of CytoscapeConfig which defines this parameter.
   */
  public static boolean getCanonicalize(CytoscapeObj cytoscapeObj) {
    return cytoscapeObj.getConfiguration().getCanonicalize();
  }
  //-------------------------------------------------------------------------
  /**
   * Returns an array containing all of the unique interaction types present
   * in the network. Formally, gets from the edge attributes all of the unique
   * values for the "interaction" attribute.
   *
   * If the argument is null, returns an array of length 0.
   */
  public static String[] getInteractionTypes( cytoscape.CyNetwork network) {
    if (network == null) {return new String[0];}
    return Cytoscape.getEdgeNetworkData().getUniqueStringValues(Semantics.INTERACTION);
  }
  //-------------------------------------------------------------------------
  /**
   * Returns the interaction type of the given edge. Formally, gets from the
   * edge attributes the value for the "interaction" attribute".
   *
   * If either argument is null, returns null.
   */
  public static String getInteractionType( cytoscape.CyNetwork network, Edge e) {
    if (network == null || e == null) {
      return null;
    }
    String canonicalName = Cytoscape.getEdgeNetworkData().getCanonicalName(e);
    return Cytoscape.getEdgeNetworkData().getStringValue(Semantics.INTERACTION, canonicalName);
  }
  //-------------------------------------------------------------------------
  /**
   * This method is used to determine if two, potentially different names really
   * refer to the same thing; that is, the two names are synonyms. The rules
   * applied are as follows:
   *
   * 1) If either name is null, this method returns true if both are null, false otherwise.
   * 2) If the names themselves match, this method returns true
   * 3) The getAllSynonyms method is called for both names, to get all known synonyms.
   * each possible pair of synonyms is compared, and this method returns true if any
   * match is found, false otherwise.
   *
   * In all cases, comparisons are done with name1.equalsIgnoreCase(name2).
   *
   * The network and cytoscapeObj arguments may be null, which simply limits the
   * tests that can be done to find synonyms.
   */
  public static boolean areSynonyms(String firstName, String secondName,
                                    cytoscape.CyNetwork network, CytoscapeObj cytoscapeObj) {
    if (firstName == null || secondName == null) {
      return (firstName == null && secondName == null);
    }
    if ( firstName.equalsIgnoreCase(secondName) ) {return true;}
    List firstSynonyms = getAllSynonyms(firstName, network, cytoscapeObj);
    List secondSynonyms = getAllSynonyms(secondName, network, cytoscapeObj);
    for (Iterator firstI = firstSynonyms.iterator(); firstI.hasNext(); ) {
      String firstSyn = (String)firstI.next();
      for (Iterator secondI = secondSynonyms.iterator(); secondI.hasNext(); ) {
        String secondSyn = (String)secondI.next();
        if ( firstSyn.equalsIgnoreCase(secondSyn) ) {
          return true;
        }
      }
    }
    return false;
  }
  //-------------------------------------------------------------------------
  /**
   * This method returns a list of all names that are synonyms of the given name.
   * The returned list will include the name argument itself, and thus will always be
   * non-null and contain at least one member (unless the argument itself is null,
   * in which case a list of size 0 is returned). The search for other names
   * follows the following steps:
   *
   * First, if the network argument is non-null and the node attributes include
   * the name argument as a canonical name, then add any entry for the COMMON_NAME
   * attribute associated with the canonical name.
   * Next, if a BioDataServer is available, try to get a species for the given name
   * either from the SPECIES attribute associated with the canonicalName, or using
   * the return value of getDefaultSpecies if needed.
   * If a species can be determined, then use the BioDataServer to add all the
   * synonyms that are registered for the name argument.
   */
  public static List getAllSynonyms(String name,  cytoscape.CyNetwork network,
                                    CytoscapeObj cytoscapeObj) {
    List returnList = new ArrayList();
    if (name == null) {return returnList;}
    returnList.add(name);
    String species = null;
    if (network != null) {
      String callerID = "Semantics.getAllSynonyms";
      String commonName = Cytoscape.getNodeNetworkData().getStringValue(COMMON_NAME, name);
      if (commonName != null) {returnList.add(commonName);}
      species = Cytoscape.getNodeNetworkData().getStringValue(SPECIES, name);
    }
    if (cytoscapeObj != null && cytoscapeObj.getBioDataServer() != null) {
      BioDataServer bioDataServer = cytoscapeObj.getBioDataServer();
      if (species == null) {species = getDefaultSpecies(network, cytoscapeObj);}
      if (species != null) {
        String[] synonyms = bioDataServer.getAllCommonNames(species, name);
        returnList.addAll( Arrays.asList(synonyms) );
        //we assume that this list of synonyms from the bioDataServer includes
        //any canonical and common names registered with the node attributes,
        //so we don't have to get a canonical name from the bioDataServer
        //and go back to the node attributes to check those attributes
      }
    }
    return returnList;
  }
  //-------------------------------------------------------------------------
}

