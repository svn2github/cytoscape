/*
 *  Copyright (C) 2001, 2002 Robert MacGrogan
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 * $Archive: SourceJammer$
 * $FileName: StringUtil.java$
 * $FileID: 4337$
 *
 * Last change:
 * $AuthorName: Rob MacGrogan$
 * $Date$
 * $Comment: Replaced GPL header with LGPL header.$
 */
package browser.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Vector;
import java.util.regex.*;


/**
 * Title: $FileName: StringUtil.java$
 * @author $AuthorName: Rob MacGrogan$
 * @version $VerNum: 2$<br><br>
 *
 * $Description: $
 * $KeyWordsOff: $
 */
public class StringUtil {
	public static final class DateFormats {
		public static final int LONG_WITH_TIME = 1;
		public static final int SHORT_WITH_TIME = 2;
		public static final int MEDIUM = 3;
	}

	private static SimpleDateFormat moLongWithTimeFormat = new SimpleDateFormat("MMMM d, yyyy h:mm:ss a");
	private static DateFormat moShortWithTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT,
	                                                                                 DateFormat.SHORT);
	private static DateFormat medium = DateFormat.getDateInstance(DateFormat.MEDIUM);

	private StringUtil() {
	}

	/**
	 * Returns sTarget with all instances of sSubstring replaced with sReplaceWith.
	 */
	public static String replaceSubstring(String sTarget, String sSubstring, String sReplaceWith) {
		// If there is a taget and a substring...
		if ((sTarget != null) && (sSubstring != null)) {
			// If there is no replace with string, make it an empty string.
			if (sReplaceWith == null) {
				sReplaceWith = "";
			}

			// Get the lengths of all of the strings (so that the function is only called once).
			int len = sTarget.length();
			int searchlen = sSubstring.length();
			int replacelen = sReplaceWith.length();

			// Holds the result of replacing the string.
			StringBuffer result = null;

			// Keeps track of where the start of a match is.
			int pos = 0;

			// Keeps track of the last good position (used as a starting position).
			int lastPos = 0;

			// Search the substring for the text.
			while ((pos = sTarget.indexOf(sSubstring, lastPos)) > -1) {
				// If the buffer has not been created, create it.
				if (result == null) {
					result = new StringBuffer(len);
				}

				// Append the first part of the string on before the match.
				result.append(sTarget.substring(lastPos, pos));

				// If there is text to replace the matched text, add that.
				if (replacelen > 0) {
					result.append(sReplaceWith);
				}

				// Set the last position to one position beyond the matched text.
				lastPos = pos + searchlen;
			}

			// If there was at least one match, append the remainder of the string onto the buffer
			//  and return it.
			if (result != null) {
				// Add in what is left over.
				if (lastPos < len) {
					result.append(sTarget.substring(lastPos));
				}

				return result.toString();
			}
		}

		// Else, return the original string.
		return sTarget;
	} //end replaceSubstring

	/**
	 *  DOCUMENT ME!
	 *
	 * @param d DOCUMENT ME!
	 * @param format DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static String dateString(Date d, int format) {
		String sReturn = null;

		DateFormat oFormatter = null;

		if (format == DateFormats.LONG_WITH_TIME) {
			oFormatter = moLongWithTimeFormat;
		} else if (format == DateFormats.SHORT_WITH_TIME) {
			oFormatter = moShortWithTimeFormat;
		} else if (format == DateFormats.MEDIUM) {
			oFormatter = medium;
		}

		if (d != null) {
			sReturn = oFormatter.format(d);
		}

		return sReturn;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param source DOCUMENT ME!
	 * @param length DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static String fixedLength(String source, int length) {
		String sReturn = null;

		if (source.length() >= length) {
			sReturn = source.substring(0, length);
		} else {
			int iFillLength = length - source.length();
			char[] fill = new char[iFillLength];
			java.util.Arrays.fill(fill, ' ');
			sReturn = source + new String(fill);
		}

		return sReturn;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		try {
			String test = "testtesttesttest";
			String result = fixedLength(test, 8);
			System.out.println("before: |" + test + "|");
			System.out.println("after:  |" + result + "|" + "   length=" + result.length());
		} catch (Throwable thr) {
			thr.printStackTrace();
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param sOne DOCUMENT ME!
	 * @param sTwo DOCUMENT ME!
	 * @param invert DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static boolean firstStringSortsBeforeSecond(String sOne, String sTwo, boolean invert) {
		boolean bReturn = false;

		try {
			double dblOne = Double.parseDouble(sOne);
			double dblTwo = Double.parseDouble(sTwo);

			if (dblOne <= dblTwo) {
				bReturn = true;
			} else {
				bReturn = false;
			}
		} catch (NumberFormatException ex) {
			Pattern p = Pattern.compile("([a-zA-Z]+)([0-9]+)");
			Matcher m1 = p.matcher(sOne);
			Matcher m2 = p.matcher(sTwo);

			if (m1.matches() && m2.matches() && m1.group(1).equals(m2.group(1))) {
				double d1 = (new Double(m1.group(2))).doubleValue();
				double d2 = (new Double(m2.group(2))).doubleValue();

				if (d1 > d2) {
					bReturn = true;
				}
			} else {
				int iComp = sOne.compareToIgnoreCase(sTwo);

				if (iComp <= 0) {
					bReturn = true;
				} else {
					bReturn = false;
				}
			}
		}

		if (invert) {
			bReturn = !bReturn;
		}

		return bReturn;
	}

	/**
	 * Checks if the string is numeric.
	 */
	public static boolean isNumeric(String sCheck) {
		boolean bReturn = false;

		try {
			if (sCheck != null) {
				double dblCheck = Double.parseDouble(sCheck);
				bReturn = true;
			}
		} //end try
		catch (NumberFormatException ex) {
			//this means sCheck is not numeric
		} //end catch

		return bReturn;
	} //end isNumeric(String)
}
