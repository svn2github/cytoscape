package org.idekerlab.PanGIAPlugin.utilities;

import java.text.NumberFormat;

public class NumberFormatter
{
	public static String formatNumber(Number n, int decimals)
	{
		String ns = NumberFormat.getInstance().format(n);
		return ns.substring(0,Math.min(ns.length()-1, ns.indexOf(".")+decimals+1));
	}
}
