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
package cytoscape.visual.calculators;

import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.ObjectMapping;

import java.lang.reflect.Constructor;

import java.util.Properties;


/**
 * This class provides static factory methods for constructing instances of
 * Calculators as specified by arguments and static methods for getting names
 * and labels based on calculator type.
 */
public class CalculatorFactory {
    /**
     * @deprecated Use othe newCalculator - we don't need the base interface
     *             class name any more. Will be removed 10/2007.
     */
	@Deprecated
    public static Calculator newCalculator(String name, Properties calcProps,
        String baseKey, String intClassName) {
        return newCalculator(name, calcProps, baseKey);
    }

	/**
	 * Creates a caclculator defined by the type identified by the ".visualPropertyType" 
	 * property in the calcProps argument.  If that isn't found, it looks for a
	 * ".class" property and then tries the old-school approach using reflection to create
	 * a calculator.
	 */
    public static Calculator newCalculator(String name, Properties calcProps, String baseKey) {
        final String typeName = calcProps.getProperty(baseKey + ".visualPropertyType");

		if ( typeName == null )
			return oldSchoolNewCalculator(name, calcProps, baseKey);

		try { 
			VisualPropertyType t = VisualPropertyType.valueOf(typeName);
			return new BasicCalculator(name,calcProps,baseKey,t);
		} catch (IllegalArgumentException e) { 
			System.out.println("Couldn't parse visual property type: " + typeName);
			return null;	
		}
	}


    /**
     * Attempt to construct an instance of Calculator as defined by the supplied
     * arguments. It searches for a key-value pair identifying the name of the
     * class to create, verifies that that class exists, implements the desired
     * interface, and has an appropriate constructor, and calls that constructor
     * with the appropriate arguments.
     */
    private static Calculator oldSchoolNewCalculator(String name, Properties calcProps,
        String baseKey) {
        /*
         * Get the class object for the real implementation object specified by
         * these properties
         */
        final String className = calcProps.getProperty(baseKey + ".class");

        if (className == null)
            return null; // this is normal, so don't shout about it

        final String errString = "CalculatorFactory: error processing baseKey " +
            baseKey;

        Class realClass = null;

        try {
            realClass = Class.forName(className);
        } catch (Exception e) {
            System.err.println(errString + " class not found: " + className);

            return null;
        }

        // get the class object representing the top-level interface Calculator
        Class calcClass = Calculator.class;

        if (!calcClass.isAssignableFrom(realClass)) {
            System.err.println(errString + " requested class " + className +
                " does not implement the Calculator interface");

            return null;
        }

        // create the constructor for the specified class
        Class[] parameterTypes = { String.class, Properties.class, String.class };
        Constructor constructor = getConstructor(realClass, parameterTypes,
                className);

        if (constructor == null) {
            System.err.println(errString + " requested constructor for " +
                className + " could not be created");

            return null;
        }

        // try constructing a calculator by calling the found constructor
        Object[] params = { name, calcProps, baseKey };
        Calculator calculator = getCalculator(constructor, params, className);

        if (calculator == null)
            System.err.println(errString + " requested calculator for " +
                className + " could not be created");

        return calculator;
    }

    /**
     * Creates a new default Calculator based on type.
	 * @deprecated Use new BasicCalculator(name,mapper,type) instead. Gone 5/2008.
     */
    @Deprecated
    public static Calculator newDefaultCalculator(byte t, String calcName, ObjectMapping mapper) {
		VisualPropertyType type = VisualPropertyType.getVisualPorpertyType(t);
		return new BasicCalculator(calcName,mapper,type);
    }

    /**
     * Returns the type name for calculators of a given type.
	 * @deprecated Use VisualPropertyType.getName() instead. Gone 5/2008.
     */
    @Deprecated
    public static String getTypeName(byte type) {
        return VisualPropertyType.getVisualPorpertyType(type).getName();
    }

    /**
     * Returns the property label for calculators of a given type.
	 * @deprecated Use VisualPropertyType.getPropertyLabel(). Will be removed 4/2008
     */
    @Deprecated
    public static String getPropertyLabel(byte type) {
        return VisualPropertyType.getVisualPorpertyType(type).getPropertyLabel();
    }

    // utility method to create a constructor based on the params
    private static Constructor getConstructor(Class realClass,
        Class[] parameterTypes, String className) {
        // look for a constructor in this class that takes the right arguments
        Constructor constructor = null;

        try {
            constructor = realClass.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException nsme) {
            String s = "no suitable constructor found in class " + className;
            System.err.println(s);
            nsme.printStackTrace();

            return null;
        } catch (SecurityException se) { // highly unlikely

            String s = "could not access constructors for class " + className;
            System.err.println(s);
            se.printStackTrace();

            return null;
        }

        return constructor;
    }

    // utility method to create a calculator based on the params
    private static Calculator getCalculator(Constructor constructor,
        Object[] params, String className) {
        Calculator calculator = null;

        try {
            calculator = (Calculator) (constructor.newInstance(params));
        } catch (Exception e) {
            String s = "unable to construct an instance of class " + className;
            for(Object p: params) {
            	System.out.println("-- Parameter = " + p);
            }
            System.err.println(s);
            e.printStackTrace();

            return null;
        }

        return calculator;
    }
}
