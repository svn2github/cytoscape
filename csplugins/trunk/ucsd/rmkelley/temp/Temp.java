package ucsd.rmkelley.Temp;
import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import giny.view.NodeView;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;


/**
 * This is a sample Cytoscape plugin using Giny graph structures. For each
 * currently selected node in the graph view, the action method of this plugin
 * additionally selects the neighbors of that node if their canonical name ends
 * with the same letter. (For yeast genes, whose names are of the form 'YOR167C',
 * this selects genes that are on the same DNA strand). This operation was
 * chosen to be illustrative, not necessarily useful.
 *
 * Note that selection is a property of the view of the graph, while neighbors
 * are a property of the graph itself. Thus this plugin must access both the
 * graph and its view.
 */
public class Temp extends CytoscapePlugin{
    /**
     * This constructor saves the cyWindow argument (the window to which this
     * plugin is attached) and adds an item to the operations menu.
     */
    public Temp(){
	Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add( new TestAction() );
    }
    
   

    public class TestAction extends AbstractAction{
    
	public TestAction() {super("Test Action");}
    
	/**
	 * This method is called when the user selects the menu item.
	 */
	public void actionPerformed(ActionEvent ae) {
	    try{
		Thread t = new GunThread("AO");
		t.run();
		t.join();
		t = new GunThread("CA");
		t.run();
		t.join();
	    }catch(Exception e){
		e.printStackTrace();
	    }
		    
	}

    }
}

class GunThread extends Thread{
    public static double CELL_WIDTH = 50;
    public static double CELL_HEIGHT = 50;
    protected String disp;
    
    public GunThread(String disp){
	this.disp = disp;
    }

    protected HashMap fontMap;
    public void run(){
	setUpFontMap();
	displayString(disp);
	//displayString("CA");
    }

    public void setUpFontMap(){
	fontMap = new HashMap();
	HashSet cSet = new HashSet();
	cSet.addAll(createVLine(0,0,4));
	cSet.addAll(createVLine(4,0,4));
	cSet.addAll(createHLine(0,0,4));
	cSet.addAll(createHLine(2,0,4));
	fontMap.put("A",new Vector(cSet));
	cSet = new HashSet();
	cSet.addAll(createVLine(0,0,4));
	cSet.addAll(createVLine(4,0,4));
	cSet.addAll(createHLine(4,0,4));
	cSet.addAll(createHLine(0,0,4));
	fontMap.put("O",new Vector(cSet));
	cSet = new HashSet();
	cSet.addAll(createHLine(0,0,4));
	cSet.addAll(createHLine(4,0,4));
	cSet.addAll(createVLine(0,0,4));
	fontMap.put("C",new Vector(cSet));
	


    }

    public void displayString(String display){
	Vector coordinates = new Vector();
	for(int idx=0;idx < display.length();idx++){
	    String character = display.substring(idx,idx+1);
	    Vector tempCoordinates = (Vector)fontMap.get(character);
	    for(Iterator coordIt = tempCoordinates.iterator();coordIt.hasNext();){
		Coordinate coord = (Coordinate)coordIt.next();
		coordinates.add(new Coordinate((7*idx)+coord.x,coord.y));
	    }
	}
	
	CyNetworkView view = Cytoscape.getCurrentNetworkView();
		
	int coordinateIndex = 0;
	for(Iterator viewIt = view.getNodeViewsIterator();viewIt.hasNext();){
	    NodeView nodeView = (NodeView)viewIt.next();
	    Coordinate coordinate = (Coordinate)coordinates.get(coordinateIndex);
	    nodeView.setXPosition(coordinate.x*CELL_WIDTH,true);
	    nodeView.setYPosition(coordinate.y*CELL_WIDTH,true);
	    coordinateIndex++;
	    coordinateIndex = coordinateIndex % coordinates.size();
	}

	//for(Iterator viewIt = view.getNodeViewsIterator();viewIt.hasNext();){
	//    ((NodeView)viewIt.next()).setNodePosition(true);
	//}

    }

    public List createVLine(int column, int start, int end){
	Vector result = new Vector();
	for (int idx = start ; idx <= end ; idx++){
	    result.add(new Coordinate(column,idx));
	}
	return result;
    }

    public List createHLine(int row, int start, int end){
	Vector result = new Vector();
	for (int idx = start; idx <= end; idx++){
	    result.add(new Coordinate(idx,row));
	}
	return result;
    }
}

class Coordinate{
    public int x;
    public int y;
    public Coordinate(int x,int y){
	this.x = x;
	this.y = y;
    }
}

