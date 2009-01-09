//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;
//----------------------------------------------------------------------------
import java.util.Properties;
import java.lang.reflect.Constructor;
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
    public static Calculator newCalculator(String name, Properties calcProps,
                                           String baseKey, String intClassName) {
        //String to use in case of errors
        String errString = "CalculatorFactory: error processing baseKey " + baseKey;
        //get the class object representing the top-level interface Calculator
        Class calcClass = Calculator.class;
        
        //get the class object representing the desired interface, and make sure
        //it is a subclass of Calculator
        Class intClass = null;
        try {
            intClass = Class.forName(intClassName);
        } catch (Exception e) {
            System.err.println(errString);
            String s = "    could not get Class object for class name: " + intClassName;
            System.err.println(s);
            return null;
        }
        assert(intClass != null);  //should be impossible
        if (!calcClass.isAssignableFrom(intClass)) {
            System.err.println(errString);
            String s = "    requested interface " + intClassName
                       + " is not a subinterface of Calculator";
            System.err.println(s);
            return null;
        }
        
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
        assert(realClass != null);  //should be impossible
        if (!intClass.isAssignableFrom(realClass)) {
            System.err.println(errString);
            String s = "    requested class " + className
                       + " does not implement interface " + intClassName;
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
        assert(constructor != null);  //should be impossible
        
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

    /**
     * Get a properties description of the caclulator argument. This
     * method calls the getProperties method of the calculator and
     * then adds a property giving the calculator class name as recognized
     * by the newCalculator method.
     */
    public static Properties getProperties(Calculator c, String baseKey) {
        if (c == null) {return null;}
        Properties newProps = c.getProperties(baseKey);
        String classKey = baseKey + ".class";
        String className = c.getClass().getName();
        newProps.setProperty(classKey, className);
        return newProps;
    }
}

