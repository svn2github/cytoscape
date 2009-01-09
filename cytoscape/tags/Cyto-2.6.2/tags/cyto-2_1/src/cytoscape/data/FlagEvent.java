//---------------------------------------------------------------------------
//  $Revision$ 
//  $Date$
//  $Author$
//---------------------------------------------------------------------------
package cytoscape.data;
//---------------------------------------------------------------------------
import java.util.Set;

import giny.model.Node;
import giny.model.Edge;
//---------------------------------------------------------------------------
/**
 * Events that are fired when the flagged state of a Node or Edge, or a group
 * of Nodes or Edges, is changed.
 */
public class FlagEvent {

    /**
     * Static constant indicating a change to a single Node.
     */
    public static final int SINGLE_NODE = 0;
    /**
     * Static constant indicating a change to a single Edge.
     */
    public static final int SINGLE_EDGE = 1;
    /**
     * Static constant indicating a change to a group of Nodes.
     */
    public static final int NODE_SET = 2;
    /**
     * Static constant indicating a change to a group of Edges.
     */
    public static final int EDGE_SET = 3;
    
    private FlagFilter source;
    private Object target;
    private int targetType;
    private boolean selectOn = true;
    
    /**
     * Standard constructor.<P>
     *
     * The first argument is the object that fired this event.<P>
     *
     * The second argument decribes what objects were affected; it should be of
     * type Node, Edge, a Set of Nodes, or a Set of Edges. If the argument is a
     * Set, it should contain at least one element.
     *
     * The third argument is a boolean indicating the type of event. It should be true
     * if the change is setting the flag on for the target objects, or false
     * if the change is removing the flag.<P>
     *
     * @throws IllegalArgumentException if the target is null or an invalid type,
     */
    public FlagEvent(FlagFilter source, Object target, boolean selectOn) {
        this.source = source;
        this.target = target;
        this.selectOn = selectOn;
        if (target == null) {
            throw new IllegalArgumentException("Unexpected null target");
        } else if (target instanceof Node) {
            this.targetType = this.SINGLE_NODE;
        } else if (target instanceof Edge) {
            this.targetType = this.SINGLE_EDGE;
        } else if (target instanceof Set) {
            Set targetSet = (Set)target;
            if (targetSet.size() == 0) {
                throw new IllegalArgumentException("Unexpected empty target set");
            }
            Object first = targetSet.iterator().next();
            if (first instanceof Node) {
                this.targetType = this.NODE_SET;
            } else if (first instanceof Edge) {
                this.targetType = this.EDGE_SET;
            } else {//unknown object type
                throw new IllegalArgumentException("Unknown object type in target set");
            }
        } else {
            throw new IllegalArgumentException("Unexpected target type");
        }
    }
    
    /**
     * Returns the source of this event.
     */
    public FlagFilter getSource() {return source;}
    
    /**
     * Returns an object references the target that was changed. This should
     * be a Node, an Edge, a Set of Nodes, or a Set of Edges. The return value
     * of getTargetType determines which of the four cases applies.
     */
    public Object getTarget() {return target;}

    /**
     * Returns a static constant identifying the type of object; either SINGLE_NODE
     * for a Node, SINGLE_EDGE for an Edge, NODE_SET for a Set of Nodes, or
     * EDGE_SET for a Set of Edges.
     */
    public int getTargetType() {return targetType;}
    
    /**
     * Returns a boolean identifying the type of event, true if the change is
     * setting the flag for these objects, or false if the change is removing the flag.
     */
    public boolean getEventType() {return selectOn;}
    
    /**
     * Returns a String representation of this object's data.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String lineSep = System.getProperty("line.separator");
        sb.append("FlagEvent:" + lineSep);
        sb.append("    target = " + getTarget() + lineSep);
        sb.append("    target type = ");
        switch (getTargetType()) {
            case SINGLE_NODE:
                sb.append("SINGLE_NODE");
                break;
            case SINGLE_EDGE:
                sb.append("SINGLE_EDGE");
                break;
            case NODE_SET:
                sb.append("NODE_SET");
                break;
            case EDGE_SET:
                sb.append("EDGE_SET");
                break;
            default: //should never happen
                sb.append(getTargetType());
                break;
        }
        sb.append(lineSep);
        sb.append("    event type = ");
        if (getEventType()) {sb.append("ON");} else {sb.append("OFF");}
        sb.append(lineSep);
        return sb.toString();
    }
}

