package csplugins.ucsd.rmkelley.GeneticInteractions;


//java import statements
import java.util.List;
import java.util.Iterator;

//swing import statements
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

//giny import statements
import giny.view.GraphView;
import giny.model.Node;

//import cytoscape stuff
import cytoscape.view.CyWindow;


public class LethalCoPPFrame extends JFrame {
  List lethalCoPPs;
  CyWindow cyWindow;
  JTable table;
  public LethalCoPPFrame(List lethalCoPPs, CyWindow cyWindow){
    super();
    this.lethalCoPPs = lethalCoPPs;
    this.cyWindow = cyWindow;	
    Object [][] data = new Object [lethalCoPPs.size()][2];
    for(int i=0;i<lethalCoPPs.size();i++){
      data[i][0] =  lethalCoPPs.get(i);
      data[i][1] = new Double(((LethalCoPP)lethalCoPPs.get(i)).score);	
    }
    Object [] headers = new Object [2];
    headers[0] = "CoPP";
    headers[1] = "Score";
    table = new JTable(data,headers); //data has type Object[]
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ListSelectionModel rowSM = table.getSelectionModel();
    rowSM.addListSelectionListener(new PathListListener());

    JScrollPane tableScroller = new JScrollPane(table);
    getContentPane().add(tableScroller);
    this.setTitle("High Scoring CoPPs");
    this.setVisible(true);
    this.pack();
  } 

  private class PathListListener implements ListSelectionListener{
    GraphView myView = cyWindow.getView();
    public void valueChanged(ListSelectionEvent e) {
      //if(e.getValueIsAdjusting() == false){
      ListSelectionModel lsm = (ListSelectionModel)e.getSource();
      if (lsm.getMinSelectionIndex() != -1) {
	LethalCoPP selectedCoPP = (LethalCoPP)lethalCoPPs.get(lsm.getMinSelectionIndex());
	myView.getNodeView(selectedCoPP.lethal).setSelected(true);
	Iterator oneIt = selectedCoPP.getPathOne().iterator();
	while(oneIt.hasNext()){
	  myView.getNodeView((Node)oneIt.next()).setSelected(true);
	}
	Iterator twoIt = selectedCoPP.getPathTwo().iterator();
	while(twoIt.hasNext()){
	  myView.getNodeView((Node)twoIt.next()).setSelected(true);
	}

      }
    }
  }
}
