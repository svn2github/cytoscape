package csplugins.sbml;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.plugin.*;
import cytoscape.plugin.jar.*;

import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.beans.*;
import java.io.*;
import java.util.List;
import javax.swing.*;

import cern.colt.list.*;

import phoebe.*;
import giny.view.*;

import edu.umd.cs.piccolo.PNode;

import org.sbml.libsbml.*;

import java.io.IOException; 
import javax.xml.parsers.*;
import org.w3c.dom.*;

public abstract class LibSBML {


  public static void loadSBML ( String sbml_file ) {
    System.loadLibrary("sbmlj");
    SBMLReader reader = new SBMLReader();
    SBMLDocument document = reader.readSBML( sbml_file );
    Model model = document.getModel();
    CyNetwork parent_network = Cytoscape.createNetwork( sbml_file );

    // get all compartments, make each a seperate Network.
    ListOf listOf_compartments = model.getListOfCompartments();
    Map cID2network = new HashMap();
    for (int i = 0; i < model.getNumCompartments(); i++) {
      Compartment comp = ( Compartment )listOf_compartments.get(i);
      CyNetwork network = Cytoscape.createNetwork( new int[] {}, new int[] {},comp.getName(), parent_network );
      //Cytoscape.createNetworkView( network );
      cID2network.put( comp.getName(), network );
      System.out.println( "LibSBML-- Network Created for Compartment: "+comp.getName() );
    }


    ListOf listOfSpecies = model.getListOfSpecies();
    for (int i = 0; i < model.getNumSpecies(); i++) {
      Species species = (Species)listOfSpecies.get(i);
      CyNode node = Cytoscape.getCyNode( species.getId(), true );
      Cytoscape.setNodeAttributeValue( node, "sbml name", species.getName() );
      Cytoscape.setNodeAttributeValue( node, "sbml type", "species" );
      Cytoscape.setNodeAttributeValue( node, "sbml id", species.getId() );
      Cytoscape.setNodeAttributeValue( node, "sbml initial concentration", new Double( species.getInitialConcentration() ) );
      Cytoscape.setNodeAttributeValue( node, "sbml initial amount", new Double( species.getInitialAmount() ) );
      Cytoscape.setNodeAttributeValue( node, "sbml compartment", species.getCompartment() );
      Cytoscape.setNodeAttributeValue( node, "sbml charge", new Integer( species.getCharge() ) );


      CyNetwork network = ( CyNetwork )cID2network.get( species.getCompartment() );
      network.restoreNode( node );
      //Cytoscape.getNetworkView( network.getIdentifier() ).getNodeView( node ).setUnselectedPaint( java.awt.Color.red );

      System.out.println( "LibSBML-- species created: "+species.getName()+" "+node.getRootGraphIndex() );

    }


    



    ListOf listOf_reactions = model.getListOfReactions();
    for (int j = 0; j < model.getNumReactions(); j++) {
      Reaction reaction = ( Reaction )listOf_reactions.get(j);
      boolean is_reversable = reaction.getReversible();
      CyNode node = Cytoscape.getCyNode( reaction.getName(), true );
      csplugins.metabolic.Reaction reaction_node = new csplugins.metabolic.Reaction( node.getRootGraphIndex(),
                                                                                     Cytoscape.getRootGraph() );
      Cytoscape.getRootGraph().replaceNode( node.getRootGraphIndex(), reaction_node );
      Cytoscape.setNodeAttributeValue( reaction_node, "sbml type", "reaction" );
      Cytoscape.setNodeAttributeValue( reaction_node, "sbml id", reaction.getId() );
            
      ListOf modifiers = reaction.getListOfModifiers();
      ListOf products = reaction.getListOfProducts();
      ListOf reactants = reaction.getListOfReactants();

      String sbml_compartment = null;

   
      System.out.println( "LibSBML-- reaction created: "+reaction_node.getIdentifier() );

      for ( int i = 0; i < products.getNumItems(); ++i ) {
        SpeciesReference species = (SpeciesReference)products.get(i);
        System.out.println( "LibSBML-- product: "+ species.getSpecies() );
        CyNode product = Cytoscape.getCyNode( species.getSpecies(), false );
        CyEdge edge = Cytoscape.getCyEdge( reaction_node, product, Semantics.INTERACTION, "reaction-product", true );

        if ( sbml_compartment == null ) 
          sbml_compartment =  ( String )Cytoscape.getNodeAttributeValue( product, "sbml compartment" );
        ( ( CyNetwork )cID2network.get( sbml_compartment ) ).restoreNode( node );
        ( ( CyNetwork )cID2network.get( sbml_compartment ) ).restoreEdge( edge );
       
        if ( is_reversable ) {
          Cytoscape.setEdgeAttributeValue( edge, "sbml reaction type", "reversable reaction-product" );
        } else {
          Cytoscape.setEdgeAttributeValue( edge, "sbml reaction type", "non-reversable reaction-product" );
        }

        reaction_node.addProduct( product.getRootGraphIndex() );
       

      }

      for ( int i = 0; i < reactants.getNumItems(); ++i ) {
        SpeciesReference species = (SpeciesReference)reactants.get(i);
        System.out.println( "LibSBML-- reactant: "+ species.getSpecies() );
        CyNode reactant = Cytoscape.getCyNode( species.getSpecies(), false );
        CyEdge edge = Cytoscape.getCyEdge( reaction_node, reactant, Semantics.INTERACTION, "reaction-reactant", true );
      
        if ( sbml_compartment == null ) 
          sbml_compartment =  ( String )Cytoscape.getNodeAttributeValue( reactant, "sbml compartment" );
        ( ( CyNetwork )cID2network.get( sbml_compartment ) ).restoreNode( node );
        ( ( CyNetwork )cID2network.get( sbml_compartment ) ).restoreEdge( edge );


        if ( is_reversable ) {
          Cytoscape.setEdgeAttributeValue( edge, "sbml reaction type", "reversable reaction-reactant" );
        } else {
          Cytoscape.setEdgeAttributeValue( edge, "sbml reaction type", "non-reversable reaction-reactant" );
        }
        reaction_node.addReactant( reactant.getRootGraphIndex() );
      
      }


      for ( int i = 0; i < modifiers.getNumItems(); ++i ) {
        ModifierSpeciesReference species = (ModifierSpeciesReference)modifiers.get(i);
        System.out.println( "LibSBML-- modifier: "+ species.getSpecies() );
        CyNode modifier = Cytoscape.getCyNode( species.getSpecies(), true );
        Cytoscape.setNodeAttributeValue( modifier, "sbml type", "modifier" );
        CyEdge edge = Cytoscape.getCyEdge( reaction_node, modifier, Semantics.INTERACTION, "reaction-modifier", true );

        ( ( CyNetwork )cID2network.get( sbml_compartment ) ).restoreNode( modifier );
        ( ( CyNetwork )cID2network.get( sbml_compartment ) ).restoreEdge( edge );

        reaction_node.addModifier( modifier.getRootGraphIndex() );
      
      }
     
    }
  }

}
