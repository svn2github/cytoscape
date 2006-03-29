
//============================================================================
// 
//  file: SIFParser.java
// 
//  Copyright (c) 2006, University of California San Diego 
// 
//  This program is free software; you can redistribute it and/or modify it 
//  under the terms of the GNU General Public License as published by the 
//  Free Software Foundation; either version 2 of the License, or (at your 
//  option) any later version.
//  
//  This program is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
//  for more details.
//  
//  You should have received a copy of the GNU General Public License along 
//  with this program; if not, write to the Free Software Foundation, Inc., 
//  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
//============================================================================



package nct.parsers;

import java.lang.*;
import java.util.*;
import java.util.regex.*;
import java.io.*;

/** 
 * A simple class that provides a method of parse a SIF file.
 */
public class SIFParser {

	/**
	 * A simple method that parses a SIF formatted file.
	 * @param fileName The SIF file to be parsed.
	 * @return A list of arrays of Strings where each element in the array
	 * represents one column in the SIF file. This parser expects the SIF file
	 * to be in "nodeA value nodeB" format where value can be anything as long
	 * as it doesn't contain any spaces.
	 */
        public static List<String[]> parse(String fileName) {

                List<String[]> rows = new ArrayList<String[]>();

		try { 
                	BufferedReader input = new BufferedReader(new FileReader(new File(fileName)));
                	String line;
                	Pattern p = Pattern.compile("^(\\S+)\\s+(\\S+)\\s+(\\S+)\\s*$");
                	Matcher m = p.matcher("");
                	while ((line = input.readLine()) != null) {
                       		m.reset(line);
                        	if ( m.matches() ) {
                                	String[] s = {m.group(1), m.group(2), m.group(3)};
                                	rows.add( s );
                        	}
                	}
                	input.close();
		} catch ( IOException ioe ) { ioe.printStackTrace(); }

                return rows;
        }
}
