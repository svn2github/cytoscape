//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;
//----------------------------------------------------------------------------
import java.util.Map;
import java.util.Properties;
import java.awt.Color;
import java.awt.Font;

import y.base.Node;
import y.view.LineType;
import y.view.Arrow;
import y.view.ShapeNodeRealizer;

import cytoscape.visual.calculators.*;
import cytoscape.visual.parsers.*;
//----------------------------------------------------------------------------
/**
 * This class calculates global visual attributes such as the background
 * color of the graph window. Currently dynamic calculators for these
 * values are not supported, only default values.
 */
public class GlobalAppearanceCalculator implements Cloneable {
    
    Color defaultBackgroundColor = Color.WHITE;
    Color defaultSloppySelectionColor = Color.GRAY;

    /**
     * Make shallow copy of this object
     */
    public Object clone() throws CloneNotSupportedException {
	Object copy = null;
	copy = super.clone();
	return copy;
    }

    public GlobalAppearanceCalculator() {}
    /**
     * Copy constructor. Returns a default object if the argument is null.
     */
    public GlobalAppearanceCalculator(GlobalAppearanceCalculator toCopy) {
        if (toCopy == null) {return;}
        
        setDefaultBackgroundColor( toCopy.getDefaultBackgroundColor() );
        setDefaultSloppySelectionColor( toCopy.getDefaultSloppySelectionColor() );
    }
    /**
     * Creates a new GlobalAppearanceCalculator and immediately customizes it
     * by calling applyProperties with the supplied arguments.
     */
    public GlobalAppearanceCalculator(String name, Properties gProps,
                                      String baseKey, CalculatorCatalog catalog) {
        applyProperties(name, gProps, baseKey, catalog);
    }
    
    /**
     * Constructs a new GlobalAppearance object containing the values for
     * the known global visual attributes.
     */
    public GlobalAppearance calculateGlobalAppearance(Network network) {
        GlobalAppearance appr = new GlobalAppearance();
        calculateGlobalAppearance(appr, network);
        return appr;
    }
    
    /**
     * The supplied GlobalAppearance object will be changed to hold new
     * values for the known global visual attributes.
     */
    public void calculateGlobalAppearance(GlobalAppearance appr, Network network) {
        appr.setBackgroundColor( calculateBackgroundColor(network) );
        appr.setSloppySelectionColor( calculateSloppySelectionColor(network) );
    }

    
    public Color getDefaultBackgroundColor() {return defaultBackgroundColor;}
    public void setDefaultBackgroundColor(Color c) {
        if (c != null) {defaultBackgroundColor = c;}
    }
    /**
     * Currently no calculators are supported for global visual attributes,
     * so this method simply returns the default background color.
     */
    public Color calculateBackgroundColor(Network network) {
        return defaultBackgroundColor;
    }
        
    public Color getDefaultSloppySelectionColor() {return defaultSloppySelectionColor;}
    public void setDefaultSloppySelectionColor(Color c) {
        if (c != null) {defaultSloppySelectionColor = c;}
    }
    /**
     * Currently no calculators are supported for global visual attributes,
     * so this method simply returns the default sloppy selection color.
     */
    public Color calculateSloppySelectionColor(Network network) {
        return defaultSloppySelectionColor;
    }

    /**
     * Returns a text description of this object's current state.
     */
    public String getDescription() {
        String lineSep = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        sb.append("GlobalAppearanceCalculator:" + lineSep);
        sb.append("defaultBackgroundColor = ");
        sb.append(defaultBackgroundColor).append(lineSep);
        sb.append("defaultSloppySelectionColor = ");
        sb.append(defaultSloppySelectionColor).append(lineSep);
        return sb.toString();
    }

    /**
     * This method customizes this object by searching the supplied properties
     * object for keys identifying default values and calculators. Recognized
     * keys are of the form "globalAppearanceCalculator." + name + ident, where
     * name is a supplied argument and ident is a String indicating a default
     * value for a specific visual attribute. Since calculators are not
     * supported for global visual attributes, the catalog argument is
     * currently ignored.
     */
    public void applyProperties(String name, Properties nacProps, String baseKey,
                                CalculatorCatalog catalog) {
        String value = null;
        
        //look for default values
        value = nacProps.getProperty(baseKey + ".defaultBackgroundColor");
        if (value != null) {
            Color c = (new ColorParser()).parseColor(value);
            if (c != null) {setDefaultBackgroundColor(c);}
        }
        value = nacProps.getProperty(baseKey + ".defaultSloppySelectionColor");
        if (value != null) {
            Color c = (new ColorParser()).parseColor(value);
            if (c != null) {setDefaultSloppySelectionColor(c);}
        }
    }
    
    /**
     * Returns a Properties description of this object, suitable for customization
     * by the applyProperties method.
     */
    public Properties getProperties(String baseKey) {
        String key = null;
        String value = null;
        Properties newProps = new Properties();
        
        //save default values
        key = baseKey + ".defaultBackgroundColor";
        value = ObjectToString.getStringValue( getDefaultBackgroundColor() );
        newProps.setProperty(key, value);
        key = baseKey + ".defaultSloppySelectionColor";
        value = ObjectToString.getStringValue( getDefaultSloppySelectionColor() );
        newProps.setProperty(key, value);

        return newProps;
    }
}

