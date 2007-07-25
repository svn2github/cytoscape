/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.color;

import java.awt.*;
import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.Icon;

import org.apache.log4j.Logger;

/**
 * Class ColorScheme
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */

public class ColorScheme implements Icon {
    private static Logger logger = Logger.getLogger(ColorScheme.class);
    protected String name;
    protected Color [] ramp;
    
    public ColorScheme(String name, Color start, Color end) {
        this.name = name;
        ramp = new Color[2];
        ramp[0] = start;
        ramp[1] = end;
    }

    public ColorScheme(String name, Color[] ramp) {
        this.name = name;
        this.ramp = (Color[])ramp.clone();
    }

    public static Color parseColor(String spec) {
        String[] fields = spec.split(",");
        if (fields.length != 3) {
            logger.error("Expected 3 integer fields for color, received "+spec);
            return null;
        }
        try {
            int r = Integer.parseInt(fields[0].trim());
            int g = Integer.parseInt(fields[1].trim());
            int b = Integer.parseInt(fields[2].trim());
            return new Color(r, g, b);
        }
        catch(Exception e) {
            logger.error("Error parsing color spec "+spec);
        }
        return null;
    }
    
    public static Color[] parseColorRamp(String spec) {
        String[] colors = spec.split(" ");
        if (colors.length < 2) {
            logger.error("Expected at most 2 colors, received "
                    +colors.length+" in "+spec);
            return null;
        }
        Color[] ramp = new Color[colors.length];
        for (int i = 0; i < ramp.length; i++) {
            Color c = parseColor(colors[i]);
            if (c == null) return null;
            ramp[i] = c;
        }
        return ramp;
    }
    
    public static Properties loadProperties(String colorName) {
        String resourceName = "resources/"+colorName+".properties";
        InputStream in = 
            ClassLoader.getSystemResourceAsStream(resourceName);
        if (in != null)
            try {
            Properties props = new Properties();
            props.load(in);
            return props;
        }
        catch(FileNotFoundException e) {
            logger.error("Cannot find color scheme file "+resourceName, e);
        }
        catch(IOException e) {
            logger.error("Exception reading color scheme file "+resourceName, e);
        }                
        return null;
    }
    
    public static ColorScheme[] loadColorSchemes(String colorName) {
        Properties props = loadProperties(colorName);
        if (props == null) {
            logger.error("Cannot read color schemes from "+colorName);
            throw new RuntimeException("Cannot read color schemes from "+colorName);
        }
        ArrayList colorSchemes = new ArrayList();
        for (int i = 0; i < 1000; i++) {
            String suffix = "." + i;
            String name = props.getProperty("name" + suffix);
            if (name == null) {
                break;
            }
            String rampSpec = props.getProperty("colors" + suffix);

            colorSchemes.add(new ColorScheme(
                    name.trim(), 
                    parseColorRamp(rampSpec)));
        }
        ColorScheme[] ret = new ColorScheme[colorSchemes.size()];
        colorSchemes.toArray(ret);
        return ret;
    }
    
    public Color getEnd() {
        return ramp[ramp.length-1];
    }
    public String getName() {
        return name;
    }
    public Color getStart() {
        return ramp[0];
    }
    
    public Color[] getRamp() {
        return ramp;
    }
    
    public int getIconHeight() {
        return 32;
    }
    
    public int getIconWidth() {
        return 64;
    }
    public void paintIcon(Component c, Graphics g, int x, int y) {
        float dx = 64 / ramp.length;
        for (int i = 0; i < ramp.length; i++) {
            g.setColor(ramp[i]);
            g.fillRect(x, y, (int)dx, 32);
            x += (int)dx;
        }
        //g.setColor(end);
        //g.fillRect(x+32, y, 32, 32);
    }
    
    public String toString() {
        return getName();
    }
}

