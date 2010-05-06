package org.idekerlab.PanGIAPlugin.utilities.text;

import java.text.NumberFormat;

public class DataFormatter {
	public static String printRatio(Number n1, Number n2) {
		String frac = String.valueOf(n1.doubleValue() / n2.doubleValue() * 100);
		return NumberFormat.getInstance().format(n1) + " / "
				+ NumberFormat.getInstance().format(n2) + "  ("
				+ frac.substring(0, frac.indexOf(".")) + "%)";
	}

}
