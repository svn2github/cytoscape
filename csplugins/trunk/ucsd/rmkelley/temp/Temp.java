package ucsd.rmkelley.Temp;
import java.util.*;
import edu.umd.cs.piccolo.activities.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import giny.view.NodeView;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import phoebe.PNodeView;
import phoebe.PGraphView;
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
	    Thread t = new GunThread();
	    t.run();
	}

    }
}

class GunThread extends Thread implements PActivity.PActivityDelegate {
    public static double CELL_WIDTH = 50;
    public static double CELL_HEIGHT = 50;
    int index = 0;
    int count = 0;
    int total = Cytoscape.getCurrentNetworkView().getNodeViewCount();
    boolean zooming = false;;

    String [] displays = {"CYTOSCAPE","WILL","CRUSH","THE","CORPORATE","COMPETITION","WE","HAVE","THE","GUN","SHOW"};
    protected HashMap fontMap;
    public void run(){
	setUpFontMap();
	displayString(displays[0]);
    }
    
    public void activityFinished(PActivity activity){
	if(zooming){
	    zooming = false;
	    count = 0;
	    try{
		Thread.sleep(2000);
	    }catch(Exception e){
		e.printStackTrace();
	    }
	    if(index < displays.length){
		displayString(displays[++index]);
	    }
	}
	else{
	    count++;
	    if(count == total){
		zooming = true;
		PGraphView graphView = (PGraphView)Cytoscape.getCurrentNetworkView();
		PTransformActivity zoomactivity = graphView.getCanvas().getCamera().animateViewToCenterBounds( graphView.getCanvas().getLayer().getFullBounds(), true, 500l );
		zoomactivity.setDelegate(this);
	    }
	}
    }
    public void activityStarted(PActivity activity){}
    public void activityStepped(PActivity activity){}


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
	
	cSet = new HashSet();
	cSet.addAll(createHLine(0,0,4));
	cSet.addAll(createVLine(2,0,4));
	fontMap.put("T",new Vector(cSet));

	cSet = new HashSet();
	cSet.addAll(createHLine(0,0,4));
	cSet.addAll(createHLine(2,0,4));
	cSet.addAll(createHLine(4,0,4));
	cSet.add(new Coordinate(0,1));
	cSet.add(new Coordinate(4,3));
	fontMap.put("S",new Vector(cSet));

	cSet = new HashSet();
	cSet.addAll(createVLine(0,0,4));
	cSet.addAll(createHLine(0,0,3));
	cSet.addAll(createHLine(2,0,3));
	cSet.add(new Coordinate(3,1));
	fontMap.put("P",new Vector(cSet));
	
	cSet = new HashSet();
	cSet.addAll(createHLine(0,0,4));
	cSet.addAll(createHLine(4,0,4));
	cSet.addAll(createVLine(0,0,4));
	cSet.addAll(createHLine(2,0,2));
	fontMap.put("E",new Vector(cSet));

	cSet = new HashSet();
	cSet.addAll(createVLine(2,2,4));
	cSet.add(new Coordinate(0,0));
	cSet.add(new Coordinate(1,1));
	cSet.add(new Coordinate(4,0));
	cSet.add(new Coordinate(3,1));
	fontMap.put("Y",new Vector(cSet));
	
	cSet = new HashSet();
	cSet.addAll(createVLine(0,0,4));
	cSet.addAll(createVLine(2,2,4));
	cSet.addAll(createVLine(4,0,4));
	cSet.addAll(createHLine(4,0,4));
	fontMap.put("W",new Vector(cSet));

	cSet = new HashSet();
	cSet.addAll(createVLine(0,0,4));
	cSet.addAll(createVLine(4,0,4));
	cSet.addAll(createHLine(2,0,4));
	fontMap.put("H",new Vector(cSet));

	cSet = new HashSet();
	cSet.addAll(createVLine(0,0,2));
	cSet.addAll(createVLine(4,0,2));
	cSet.add(new Coordinate(1,3));
	cSet.add(new Coordinate(3,3));
	cSet.add(new Coordinate(2,4));
	fontMap.put("V",new Vector(cSet));

	cSet = new HashSet();
	cSet.addAll(createHLine(0,0,4));
	cSet.addAll(createVLine(0,0,4));
	cSet.addAll(createHLine(4,0,4));
	cSet.addAll(createHLine(2,2,4));
	cSet.add(new Coordinate(4,3));
	fontMap.put("G",new Vector(cSet));
	
	cSet = new HashSet();
	cSet.addAll(createVLine(0,0,4));
	cSet.addAll(createVLine(4,0,4));
	cSet.addAll(createHLine(4,0,4));
	fontMap.put("U",new Vector(cSet));

	cSet = new HashSet();
	cSet.addAll(createVLine(0,0,4));
	cSet.addAll(createVLine(4,0,4));
	cSet.add(new Coordinate(1,1));
	cSet.add(new Coordinate(2,2));
	cSet.add(new Coordinate(3,3));
	fontMap.put("N",new Vector(cSet));
	
	cSet = new HashSet();
	cSet.addAll(createVLine(2,0,4));
	cSet.addAll(createHLine(0,0,4));
	cSet.addAll(createHLine(4,0,4));
	fontMap.put("I",new Vector(cSet));

	cSet = new HashSet();
	cSet.addAll(createVLine(0,0,4));
	cSet.addAll(createHLine(4,0,4));
	fontMap.put("L",new Vector(cSet));
	
	cSet = new HashSet();
	cSet.addAll(createVLine(0,0,4));
	cSet.addAll(createHLine(0,0,3));
	cSet.addAll(createHLine(2,0,3));
	cSet.add(new Coordinate(3,1));
	cSet.add(new Coordinate(2,3));
	cSet.add(new Coordinate(3,4));
	fontMap.put("R",new Vector(cSet));

	cSet = new HashSet();
	cSet.addAll(createHLine(0,0,4));
	cSet.addAll(createVLine(0,0,4));
	cSet.addAll(createVLine(4,0,4));
	cSet.addAll(createVLine(2,0,2));
	fontMap.put("M",new Vector(cSet));

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
	    nodeView.setXPosition(coordinate.x*CELL_WIDTH,false);
	    nodeView.setYPosition(coordinate.y*CELL_WIDTH,false);
	    coordinateIndex++;
	    coordinateIndex = coordinateIndex % coordinates.size();
	}

	for(Iterator viewIt = view.getNodeViewsIterator();viewIt.hasNext();){
	    PNodeView nodeView = (PNodeView)viewIt.next();
	    PTransformActivity activity = nodeView.animateToPositionScaleRotation(nodeView.getXPosition(),nodeView.getYPosition(),1,0,2000);
	    activity.setDelegate(this);
	}
	PGraphView graphView = (PGraphView)view;

	
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

