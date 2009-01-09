package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import giny.view.GraphView;
import cytoscape.view.NetworkView;


public class SelectionModeAction extends JMenu {

  public SelectionModeAction ( NetworkView networkview ) {
    super("Mouse Drag Selects");
    

    final NetworkView networkView = networkview;
    //final PGraphView view = (PGraphView)networkView.getView();

    ButtonGroup modeGroup = new ButtonGroup();
    JCheckBoxMenuItem nodes = new JCheckBoxMenuItem( new AbstractAction( "Nodes" ) {
	public void actionPerformed ( ActionEvent e ) {
	  // Do this in the GUI Event Dispatch thread...
	  SwingUtilities.invokeLater( new Runnable() {
	      public void run() {
		GraphView view = networkView.getView();
		view.disableNodeSelection();
		view.disableEdgeSelection();
		view.enableNodeSelection();
	      } } ); } } ) ;
    JCheckBoxMenuItem edges = new JCheckBoxMenuItem( new AbstractAction( "Edges" ) {
	public void actionPerformed ( ActionEvent e ) {
	  // Do this in the GUI Event Dispatch thread...
	  SwingUtilities.invokeLater( new Runnable() {
	      public void run() {
		GraphView view = networkView.getView();
		view.disableNodeSelection();
		view.disableEdgeSelection();
		view.enableEdgeSelection();
	      } } ); } } ) ;

    JCheckBoxMenuItem nodesAndEdges = new JCheckBoxMenuItem( new AbstractAction( "Nodes and Edges" ) {
	public void actionPerformed ( ActionEvent e ) {
	  // Do this in the GUI Event Dispatch thread...
	  SwingUtilities.invokeLater( new Runnable() {
	      public void run() {
		GraphView view = networkView.getView();
		view.disableNodeSelection();
		view.disableEdgeSelection();
		view.enableNodeSelection();
		view.enableEdgeSelection();
	      } } ); } } ) ;

    modeGroup.add(nodes);
    modeGroup.add(edges);
    modeGroup.add(nodesAndEdges);
    
    add(nodes);
    add(edges);
    add(nodesAndEdges);
    nodes.setSelected(true);
    GraphView view = networkView.getView();
    view.enableNodeSelection();
    view.disableEdgeSelection();
  }

}
