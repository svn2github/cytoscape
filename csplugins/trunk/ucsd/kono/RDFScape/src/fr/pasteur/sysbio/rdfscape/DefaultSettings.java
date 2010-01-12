/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Dec 7, 2005
 *
 */
package fr.pasteur.sysbio.rdfscape;

import java.awt.Color;
import java.util.Hashtable;

/**
 * @author andrea@pasetur.fr
 *	
 * This class manages defaults. Ideally it should read them from a configuration file, but they are hardcoded now.
 */
public class DefaultSettings {
	public static boolean limitedMode=true;
	/**
	 * 
	 */
	public static String contextsDirectory="rdfscapecontexts";
	public static String defaultKnowledgeEngine="Jena";
	public static Color defaultColor=Color.RED;
	/*
	public static Hashtable getDefaultKnowledgeEngineOptions() {
		Hashtable table=new Hashtable();
		table.put("Language","RDFS");
		table.put("Level","Low");
		return table;
	};
	*/
	public DefaultSettings() {
		super();
		
	}

	/**
	 * @param colorString color string
	 * @return color object
	 */
	public static Color translateString2Color(String colorString) {
		if(colorString.equalsIgnoreCase("black")) return Color.BLACK;
		if(colorString.equalsIgnoreCase("blue")) return Color.BLUE;
		if(colorString.equalsIgnoreCase("cyan")) return Color.CYAN;
		if(colorString.equalsIgnoreCase("dark_gray")) return Color.DARK_GRAY;
		if(colorString.equalsIgnoreCase("gray")) return Color.GRAY;
		if(colorString.equalsIgnoreCase("green")) return Color.GREEN;
		if(colorString.equalsIgnoreCase("light_gray")) return Color.LIGHT_GRAY;
		if(colorString.equalsIgnoreCase("magenta")) return Color.MAGENTA;
		if(colorString.equalsIgnoreCase("orange")) return Color.ORANGE;
		if(colorString.equalsIgnoreCase("pink")) return Color.PINK;
		if(colorString.equalsIgnoreCase("red")) return Color.RED;
		if(colorString.equalsIgnoreCase("white")) return Color.WHITE;
		if(colorString.equalsIgnoreCase("yellow")) return Color.YELLOW;
		return Color.BLACK;
	}
	/**
	 * @param tempNamespace
	 * @return
	 */
	public static String translateColor2String(Color myColor) {
		if(myColor==null) return "BLACK";
		if(myColor==Color.BLACK) return "BLACK";
		if(myColor==Color.BLUE) return "BLUE";
		if(myColor==Color.CYAN) return "CYAN";
		if(myColor==Color.DARK_GRAY) return "DARK_GRAY";
		if(myColor==Color.GRAY) return "GRAY";
		if(myColor==Color.GREEN) return "GREEN";
		if(myColor==Color.LIGHT_GRAY) return "LIGHT_GRAY";
		if(myColor==Color.MAGENTA) return "MAGENTA";
		if(myColor==Color.ORANGE) return "ORANGE";
		if(myColor==Color.PINK) return "PINK";
		if(myColor==Color.RED) return "RED";
		if(myColor==Color.WHITE) return "WHITE";
		if(myColor==Color.YELLOW) return "YELLOW";
		return null;
	}
	
	public static Color[] getPossibleNameSpaceColors() {
		Color[] myColors=new Color[13];
		myColors[0]=Color.BLACK;
		myColors[1]=Color.BLUE;
		myColors[2]=Color.CYAN;
		myColors[3]=Color.DARK_GRAY;
		myColors[4]=Color.GRAY;
		myColors[5]=Color.GREEN;
		myColors[6]=Color.LIGHT_GRAY;
		myColors[7]=Color.MAGENTA;
		myColors[8]=Color.ORANGE;
		myColors[9]=Color.PINK;
		myColors[10]=Color.RED;
		myColors[11]=Color.WHITE;
		myColors[12]=Color.YELLOW;
		return myColors;
	
	}

	
}
