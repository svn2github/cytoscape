package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;

import cytoscape.view.NetworkView;
import cytoscape.dialogs.GraphObjectSelection;

public class SquiggleAction extends JMenu {

  public SquiggleAction ( NetworkView networkview ) {
    super("Squiggle");
    

    final NetworkView networkView = networkview;

    add( new JMenuItem( new AbstractAction( "Squiggle ON" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  networkView.getView().getSquiggleHandler().beginSquiggling();
                } } ); } } ) );

    add( new JMenuItem( new AbstractAction( "Squiggle OFF" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  networkView.getView().getSquiggleHandler().stopSquiggling();
                } } ); } } ) );

    add( new JMenuItem( new AbstractAction( "Clear Squiggle" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                 
                  networkView.getView().getSquiggleHandler().clearSquiggles();
                } } ); } } ) );
    

  }

}
