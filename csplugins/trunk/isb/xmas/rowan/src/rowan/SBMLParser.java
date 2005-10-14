package rowan;

import javax.xml.parsers.*;
import org.xml.sax.*;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.*;
import java.util.*;
import java.util.regex.*;
import cytoscape.*;
import cytoscape.data.*;

public class SBMLParser {

  
  // An array of names for DOM node-types
  // (Array indexes = nodeType() values.)
  static final String[] typeName = {
    "none",
    "Element",
    "Attr",
    "Text",
    "CDATA",
    "EntityRef",
    "Entity",
    "ProcInstr",
    "Comment",
    "Document",
    "DocType",
    "DocFragment",
    "Notation",
  };

  CyNetwork network;
  CytoscapeData nodeData = Cytoscape.getNodeNetworkData();
  CytoscapeData edgeData = Cytoscape.getEdgeNetworkData();


  public SBMLParser () {
  } // constructor

  public void parseSBML ( List files) {
 
    for ( Iterator it = files.iterator(); it.hasNext(); ) {
      String file = (String)it.next();
      System.out.println( "SBML File: "+file );

      network = Cytoscape.createNetwork( file, false );

      Document document;
      DocumentBuilderFactory factory =
        DocumentBuilderFactory.newInstance();
        
      // is this needed or not?
      //factory.setValidating(true);   
      //factory.setNamespaceAware(true);
      try {
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse( new File(file) );
        
        walkDOM( document );
        
      } catch (SAXException sxe) {
        // Error generated during parsing)
        Exception  x = sxe;
        if (sxe.getException() != null)
          x = sxe.getException();
        x.printStackTrace();
        
      } catch (ParserConfigurationException pce) {
        // Parser with specified options can't be built
        pce.printStackTrace();
        
      } catch (IOException ioe) {
        // I/O error
        ioe.printStackTrace();
      }
    } 
  }
   
  public void walkDOM ( Node node ) {

    // Display Node Information
    String name = node.getNodeName();
    String value = node.getNodeValue();


    //System.out.println( "Node: "+name+" value: "+value );

    // NamedNodeMap map = node.getAttributes();
//     if ( map != null ) {
      
//       for ( int i = 0; i < map.getLength(); i++ ) {
        
//         Node elem = map.item( i );
//         String iname = elem.getNodeName();
//         String ivalue = elem.getNodeValue();
//         if ( value == null ) 
//           value = "Null";
//         //System.out.println( "\tNode: "+name+" value: "+value );
        
//       }
          
    //}

    if ( name.equals( "species" ) ) {
      parseSpecies( node );
    } else if ( name.equals( "reaction" ) ) {
      parseReaction( node );
    } else {
 
      NodeList children = node.getChildNodes();
      for ( int i = 0; i < children.getLength(); ++i ) {
        walkDOM( children.item( i ) );
        
      }
    }

  } // walkDOM

  private void parseReaction ( Node node ) {

    // Display Node Information
    String name = node.getNodeName();
    String value = node.getNodeValue();

    // make sure this is a species node
    if ( !name.equals( "reaction" ) )
      return;

    Map elements = new HashMap();
    CyNode cynode = null;
    NamedNodeMap map = node.getAttributes();
    if ( map != null ) {
      for ( int i = 0; i < map.getLength(); i++ ) {
        Node elem = map.item( i );
        String iname = elem.getNodeName();
        String ivalue = elem.getNodeValue();
        elements.put( iname, ivalue );
      }
      cynode = Cytoscape.getCyNode( (String)elements.get( "id" ), true );
      network.restoreNode( cynode );

      nodeData.setAttributeValue( cynode.getIdentifier(), "InSBML", "true" );
      nodeData.setAttributeValue( cynode.getIdentifier(), "SBML_type", "reaction" );

      Iterator atts = elements.keySet().iterator();
      while ( atts.hasNext() ) {
        String attribute = (String)atts.next();
        nodeData.setAttributeValue( cynode.getIdentifier(), attribute, elements.get(attribute) );
      }
    } else {
      return;
    }

    NodeList children = node.getChildNodes();
    for ( int j = 0; j < children.getLength(); ++j ) {
      Node child = children.item(j);
      String child_name = child.getNodeName();


      if ( child_name.equals( "listOfReactants" ) ) {
        // parse reactants
        NodeList ichildren = child.getChildNodes();
         for ( int i = 0; i < ichildren.getLength(); ++i ) {
           if ( ichildren.item(i).getNodeName().equals( "speciesReference" ) ) {
             String ichild = getNodeSpeciesName( ichildren.item(i) );
             CyNode inode = Cytoscape.getCyNode( ichild );
             CyEdge edge =Cytoscape.getCyEdge( inode, cynode, Semantics.INTERACTION, "reactant-reaction", true );
             network.restoreEdge( edge );
           }
         }
      }
    
    if ( child_name.equals( "listOfProducts" ) ) {
        // parse reactants
        NodeList ichildren = child.getChildNodes();
         for ( int i = 0; i < ichildren.getLength(); ++i ) {
           if ( ichildren.item(i).getNodeName().equals( "speciesReference" ) ) {
             String ichild = getNodeSpeciesName( ichildren.item(i) );
             CyNode inode = Cytoscape.getCyNode( ichild );
             CyEdge edge = Cytoscape.getCyEdge( cynode, inode, Semantics.INTERACTION, "reaction-product", true );
             network.restoreEdge( edge ); 
           }
         }
      }

    if ( child_name.equals( "listOfModifiers" ) ) {
        // parse reactants
        NodeList ichildren = child.getChildNodes();
         for ( int i = 0; i < ichildren.getLength(); ++i ) {
           if ( ichildren.item(i).getNodeName().equals( "modifierSpeciesReference" ) ) {
             String ichild = getNodeSpeciesName( ichildren.item(i) );
             CyNode inode = Cytoscape.getCyNode( ichild );
             CyEdge edge = Cytoscape.getCyEdge( inode, cynode, Semantics.INTERACTION, "modifier-reaction", true );
             network.restoreEdge( edge );
           }
         }
      }

    }
  }


  private String getNodeSpeciesName ( Node node ) {
    
    NamedNodeMap map = node.getAttributes();
    if ( map != null ) {
      for ( int i = 0; i < map.getLength(); i++ ) {
        Node elem = map.item( i );
        String iname = elem.getNodeName();
        String ivalue = elem.getNodeValue();
      
        System.out.println( "Node: "+node+" name: "+iname+" value:" +ivalue );

        if ( iname.equals( "species") ) {
          return ivalue;
        }
      }
    }
    return null;
  }

  private void parseSpecies ( Node node ) {

    // Display Node Information
    String name = node.getNodeName();
    String value = node.getNodeValue();

    // make sure this is a species node
    if ( !name.equals( "species" ) )
      return;

   
    Map elements = new HashMap();
    boolean gene = false;
    CyNode cynode;

     NamedNodeMap map = node.getAttributes();
     if ( map != null ) {
      
       for ( int i = 0; i < map.getLength(); i++ ) {
         
         Node elem = map.item( i );
         String iname = elem.getNodeName();
         String ivalue = elem.getNodeValue();
                 
         if( iname.equals( "name" ) ) {
           char c1 = ivalue.charAt(0);
           char c2 = ivalue.charAt(1);
           if ( Character.isDigit( c1 ) && !Character.isLetter( c2 )){
             gene = true;
           } 
         } 
         
         elements.put( iname, ivalue );
         
       } // parse map for node attributes
       
       cynode = Cytoscape.getCyNode( (String)elements.get( "id" ), true );
       
       nodeData.setAttributeValue( cynode.getIdentifier(), "InSBML", "true" );
       if ( gene ) {
         nodeData.setAttributeValue( cynode.getIdentifier(), "SBML_type", "gene" );
         nodeData.setAttributeValue( cynode.getIdentifier(), "SBMLName", elements.get( "id") );
       } else {
         nodeData.setAttributeValue( cynode.getIdentifier(), "SBMLName", elements.get( "name") );
         nodeData.setAttributeValue( cynode.getIdentifier(), "SBML_type", "metabolite" );
       }
       
       // add node to network
       network.restoreNode( cynode );

       Iterator atts = elements.keySet().iterator();
       while ( atts.hasNext() ) {
         String attribute = (String)atts.next();
         nodeData.setAttributeValue( cynode.getIdentifier(), attribute, elements.get(attribute) );
       }
         

     }
  }


    

}
