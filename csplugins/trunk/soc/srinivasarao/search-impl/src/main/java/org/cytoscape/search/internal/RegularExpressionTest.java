package org.cytoscape.search.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpressionTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String input = "node.canonicalName:900 AND node.attr:val";
		Pattern s = Pattern.compile("node.[\\S]*:");
		Matcher m = s.matcher(input);
		while (m.find()) {
			//System.out.println(m.pattern());
			System.out.println(m.group());
			String str = m.group();
			String[] ss = str.split("\\.");
			System.out.println(ss[1]);
		}
	}

}
