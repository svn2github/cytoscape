/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column.format;

import java.awt.Color;
import java.io.ObjectStreamException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.HashMap;

/**
 * Format for parsing color name.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class ColorFormat extends Format {
    private static ColorFormat instance;
    protected HashMap colorNames;

    /**
     * Returns the instance of that format.
     * @return the instance of that format.
     */
    public static ColorFormat getInstance() {
        if (instance == null) {
            instance = new ColorFormat();
        }
        return instance;
    }
    
    /**
     * Sets the instance of that format.
     * @param format the format
     */
    public static void setInstance(ColorFormat format) {
        instance = format;
    }
    
    /**
     * Constructor.
     */
    public ColorFormat() {
        colorNames = new HashMap();
        createDefaultColors();
    }
    
    protected void createDefaultColors() {
        addColor("white", Color.WHITE);
        addColor("lightGray", Color.LIGHT_GRAY);
        addColor("gray", Color.GRAY);
        addColor("darkGray", Color.DARK_GRAY);
        addColor("black", Color.BLACK);
        addColor("red", Color.RED);
        addColor("pink", Color.PINK);
        addColor("orange", Color.ORANGE);
        addColor("yellow", Color.YELLOW);
        addColor("green", Color.GREEN);
        addColor("magenta", Color.MAGENTA);
        addColor("cyan", Color.CYAN);
        addColor("blue", Color.BLUE);
    }

    /**
     * Adds a new named color name.
     * @param name the name
     * @param color the color
     */
    public void addColor(String name, Color color) {
        colorNames.put(name, color);
    }
    
    /**
     * Adds a new named color given its RGB values.
     * @param name the name
     * @param r the red value
     * @param g the green value
     * @param b the blue value
     */
    public void addColor(String name, int r, int g, int b) {
        addColor(name, new Color(r, g, b));
    }
    
    /**
     * Returns a color given its name.
     * @param name the name
     * @return the color or null
     */
    public Color getColor(String name) {
        return (Color)colorNames.get(name);
    }
    
    /**
     * {@inheritDoc}
     */
    public StringBuffer format(
        Object obj,
        StringBuffer toAppendTo,
        FieldPosition pos) {
        if (! (obj instanceof Integer)) {
            return null;
        }
        
        pos.setBeginIndex(toAppendTo.length());
        String formated = Integer.toString(((Integer)obj).intValue(), 16);
        while (formated.length() < 6) {
            formated = "0" + formated;
        }
        toAppendTo.append("0x"+formated);
        pos.setEndIndex(toAppendTo.length());
        return toAppendTo;
    }

    /**
     * {@inheritDoc}
     */
    public Object parseObject(String source, ParsePosition pos) {
        int index = pos.getIndex();
        if (index != 0) {
            source = source.substring(index);
        }
        Color c = getColor(source);
        if (c == null) 
            c = Color.decode(source);
        pos.setIndex(source.length());
        int value;
        if (c != null) {
            value = c.getRGB();
        }
        else {
            try {
                value = Integer.parseInt(source.substring(1));
            }
            catch(Exception e) {
                return null;
            }
        }
        return new Integer(value);
    }
    
    private Object readResolve() throws ObjectStreamException {
        return getInstance();
    }

}
