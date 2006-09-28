
/*
  File: CalculatorFactory.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;
//----------------------------------------------------------------------------
import java.util.Properties;
import java.lang.reflect.Constructor;
import cytoscape.visual.ui.VizMapUI;
import cytoscape.visual.mappings.ObjectMapping;
//----------------------------------------------------------------------------
/**
 * This class provides a static factory method for constructing an instance
 * of Calculator as specified by a Properties object and other arguments.
 * It searches for a key-value pair identifying the name of the class to
 * create, verifies that that class exists, implements the desired interface,
 * and has an appropriate constructor, and calls that constructor with the
 * appropriate arguments.
 */
public class CalculatorFactory {
    
    /**
     * Attempt to construct an instance of Calculator as defined by
     * the supplied arguments.
     */
    public static Calculator newCalculator(String name, Properties calcProps, String baseKey) {
        //String to use in case of errors
        String errString = "CalculatorFactory: error processing baseKey " + baseKey;

        //get the class object for the real implementation object specified by
        //these properties
        String className = calcProps.getProperty(baseKey + ".class");
        if (className == null) {
            System.err.println(errString);
            String s = "    expected property key '"
                       + baseKey + ".class' identifying class to construct";
            System.err.println(s);
            return null;
	}

        Class realClass = null;
        try {
            realClass = Class.forName(className);
        } catch (Exception e) {
            System.err.println(errString);
            String s = "    class not found: " + className;
            System.err.println(s);
            return null;
        }
	
        //get the class object representing the top-level interface Calculator
        Class calcClass = Calculator.class;
        if (!calcClass.isAssignableFrom(realClass)) {
            System.err.println(errString);
            String s = "    requested class " + className
                       + " does not implement the Calculator interface";
            System.err.println(s);
            return null;
        }
	
        //look for a constructor in this class that takes the right arguments
        Constructor constructor = null;
        try {
            Class[] parameterTypes = {String.class, Properties.class, String.class};
            constructor = realClass.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException nsme) {
            System.err.println(errString);
            String s = "    no suitable constructor found in class "
                       + className;
            System.err.println(s);
            return null;
        } catch (SecurityException se) {//highly unlikely
            System.err.println(errString);
            String s = "    could not access constructors for class "
                       + className;
            System.err.println(s);
            return null;
        }
        //assert(constructor != null);  //should be impossible
        
        //try constructing a calculator by calling the found constructor
        Calculator calculator = null;
        try {
            Object[] params = {name, calcProps, baseKey};
            calculator = (Calculator) (constructor.newInstance(params));
        } catch (Exception e) {
            System.err.println(errString);
            String s = "    unable to construct an instance"
                       + " of class " + className;
            System.err.println(s);
            System.err.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
           
        return calculator;
    }

    public static Calculator newDefaultCalculator(byte type, String calcName, ObjectMapping mapper) {

	Calculator calc = null;
	if (type == VizMapUI.NODE_COLOR) {
		calc = new GenericNodeFillColorCalculator(calcName, mapper);
	} else if (type == VizMapUI.NODE_BORDER_COLOR) {
       		calc = new GenericNodeBorderColorCalculator(calcName, mapper);
	} else if (type == VizMapUI.NODE_LINETYPE) {
       		calc = new GenericNodeLineTypeCalculator(calcName, mapper);
	} else if (type == VizMapUI.NODE_SHAPE) {
       		calc = new GenericNodeShapeCalculator(calcName, mapper);
	} else if (type == VizMapUI.NODE_HEIGHT) {
       		calc = new GenericNodeHeightCalculator(calcName, mapper);
	} else if (type == VizMapUI.NODE_WIDTH) {
       		calc = new GenericNodeWidthCalculator(calcName, mapper);
	} else if (type == VizMapUI.NODE_SIZE) {
       		calc = new GenericNodeUniformSizeCalculator(calcName, mapper);
	} else if (type == VizMapUI.NODE_LABEL) {
       		calc = new GenericNodeLabelCalculator(calcName, mapper);
	} else if (type == VizMapUI.NODE_LABEL_COLOR) {
       		calc = new GenericNodeLabelColorCalculator(calcName, mapper);
	} else if (type == VizMapUI.NODE_TOOLTIP) {
       		calc = new GenericNodeToolTipCalculator(calcName, mapper);
	} else if (type == VizMapUI.EDGE_COLOR) {
       		calc = new GenericEdgeColorCalculator(calcName, mapper);
	} else if (type == VizMapUI.EDGE_LINETYPE) {
       		calc = new GenericEdgeLineTypeCalculator(calcName, mapper);
	} else if (type == VizMapUI.EDGE_SRCARROW) {
       		calc = new GenericEdgeSourceArrowCalculator(calcName, mapper);
	} else if (type == VizMapUI.EDGE_TGTARROW) {
       		calc = new GenericEdgeTargetArrowCalculator(calcName, mapper);
	} else if (type == VizMapUI.EDGE_LABEL) {
       		calc = new GenericEdgeLabelCalculator(calcName, mapper);
	} else if (type == VizMapUI.EDGE_TOOLTIP) {
       		calc = new GenericEdgeToolTipCalculator(calcName, mapper);
	} else if (type == VizMapUI.EDGE_FONT_FACE) {
       		calc = new GenericEdgeFontFaceCalculator(calcName, mapper);
	} else if (type == VizMapUI.EDGE_FONT_SIZE) {
       		calc = new GenericEdgeFontSizeCalculator(calcName, mapper);
	} else if (type == VizMapUI.NODE_FONT_FACE) {
       		calc = new GenericNodeFontFaceCalculator(calcName, mapper);
	} else if (type == VizMapUI.NODE_FONT_SIZE) {
       		calc = new GenericNodeFontSizeCalculator(calcName, mapper);
	} else if (type == VizMapUI.NODE_LABEL_POSITION) {
       		calc = new GenericNodeLabelPositionCalculator(calcName, mapper);
	}
	return calc;
    }

    /**
     * Get a properties description of the caclulator argument. This
     * method calls the getProperties method of the calculator and
     * then adds a property giving the calculator class name as recognized
     * by the newCalculator method.
     */
    public static Properties getProperties(Calculator c, String baseKey) {
        if (c == null || baseKey == null) {return null;}
        Properties newProps = c.getProperties(baseKey);
        String classKey = baseKey + ".class";
        String className = c.getClass().getName();
        newProps.setProperty(classKey, className);
        return newProps;
    }
}

