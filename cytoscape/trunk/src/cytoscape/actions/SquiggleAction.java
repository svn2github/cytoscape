package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import phoebe.PGraphView;
import cytoscape.view.NetworkView;
import cytoscape.dialogs.GraphObjectSelection;

public class SquiggleAction extends JMenu {

  public SquiggleAction ( NetworkView networkview ) {
    super("Squiggle");
    

    final NetworkView networkView = networkview;
    //final PGraphView view = (PGraphView)networkView.getView();

    add( new JMenuItem( new AbstractAction( "Squiggle ON" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
		PGraphView view = (PGraphView)networkView.getView();
                  view.getSquiggleHandler().beginSquiggling();
                } } ); } } ) );

    add( new JMenuItem( new AbstractAction( "Squiggle OFF" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
		PGraphView view = (PGraphView)networkView.getView();
                  view.getSquiggleHandler().stopSquiggling();
                } } ); } } ) );

    add( new JMenuItem( new AbstractAction( "Clear Squiggle" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                 PGraphView view = (PGraphView)networkView.getView();
                 view.getSquiggleHandler().clearSquiggles();
                } } ); } } ) );
    

  }

}
