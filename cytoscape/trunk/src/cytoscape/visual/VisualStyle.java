//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;
//----------------------------------------------------------------------------
/**
 * This class encapsulates a full set of visual mapping specifications for
 * Cytoscape. Currently this is implemented by holding a reference to three
 * appearance calculators, one for nodes, one for edges, and one for global
 * visual attributes.
 */
public class VisualStyle {
    
    String name = "default";
    NodeAppearanceCalculator nodeAC;
    EdgeAppearanceCalculator edgeAC;
    GlobalAppearanceCalculator globalAC;
    
    /**
     * Simple constructor, creates default node/edge/global appearance calculators.
     */
    public VisualStyle(String name) {
        setName(name);
        setNodeAppearanceCalculator( new NodeAppearanceCalculator() );
        setEdgeAppearanceCalculator( new EdgeAppearanceCalculator() );
        setGlobalAppearanceCalculator( new GlobalAppearanceCalculator() );
    }
    
    /**
     * Full constructor.
     */
    public VisualStyle(String name, NodeAppearanceCalculator nac,
    EdgeAppearanceCalculator eac, GlobalAppearanceCalculator gac) {
        setName(name);
        setNodeAppearanceCalculator(nac);
        setEdgeAppearanceCalculator(eac);
        setGlobalAppearanceCalculator(gac);
    }
    
    /**
     * Copy constructor. Creates a default object if the argument is null.
     * The name of this new object should be changed by calling setName
     * with a new, unique name before adding it to a CalculatorCatalog.
     */
    public VisualStyle(VisualStyle toCopy) {
        if (toCopy == null) {return;}
        setName( toCopy.getName() );
        setNodeAppearanceCalculator( toCopy.getNodeAppearanceCalculator() );
        setEdgeAppearanceCalculator( toCopy.getEdgeAppearanceCalculator() );
        setGlobalAppearanceCalculator( toCopy.getGlobalAppearanceCalculator() );
    }
        
    
    /**
     * Returns the name of this object.
     */
    public String getName() {return name;}
    /**
     * Set the name of this visual style. This should be a unique name, or
     * a collision will ocur when adding this to a CalcualtorCatalog.
     *
     * @param n  the new name
     * @return   the old name
     */
    public String setName(String n) {
        String tmp = name;
        name = n;
        return tmp;
    }
    
    /**
     * Get the NodeAppearanceCalculator for this visual style.
     */
    public NodeAppearanceCalculator getNodeAppearanceCalculator() {
        return nodeAC;
    }
    /**
     * Set the NodeAppearanceCalculator for this visual style. A default
     * NodeAppearanceCalculator will be created and used if the argument
     * is null.
     *
     * @param nac  the new NodeAppearanceCalculator
     * @return  the old NodeAppearanceCalculator
     */
    public NodeAppearanceCalculator setNodeAppearanceCalculator(NodeAppearanceCalculator nac) {
        NodeAppearanceCalculator tmp = nodeAC;
        nodeAC = (nac == null) ? new NodeAppearanceCalculator() : nac;
        return tmp;
    }
    
    /**
     * Get the EdgeAppearanceCalculator for this visual style.
     */
    public EdgeAppearanceCalculator getEdgeAppearanceCalculator() {
        return edgeAC;
    }
    /**
     * Set the EdgeAppearanceCalculator for this visual style. A default
     * EdgeAppearanceCalculator will be created and used if the argument
     * is null.
     *
     * @param nac  the new EdgeAppearanceCalculator
     * @return  the old EdgeAppearanceCalculator
     */
    public EdgeAppearanceCalculator setEdgeAppearanceCalculator(EdgeAppearanceCalculator eac) {
        EdgeAppearanceCalculator tmp = edgeAC;
        edgeAC = (eac == null) ? new EdgeAppearanceCalculator() : eac;
        return tmp;
    }
    
    /**
     * Get the GlobalAppearanceCalculator for this visual style.
     */
    public GlobalAppearanceCalculator getGlobalAppearanceCalculator() {
        return globalAC;
    }
    /**
     * Set the GlobalAppearanceCalculator for this visual style. A default
     * GlobalAppearanceCalculator will be created and used if the argument
     * is null.
     *
     * @param nac  the new GlobalAppearanceCalculator
     * @return  the old GlobalAppearanceCalculator
     */
    public GlobalAppearanceCalculator setGlobalAppearanceCalculator(GlobalAppearanceCalculator gac) {
        GlobalAppearanceCalculator tmp = globalAC;
        globalAC = (gac == null) ? new GlobalAppearanceCalculator() : gac;
        return tmp;
    }
}

