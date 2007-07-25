/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column.format;

import java.text.*;
import java.util.Locale;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class UTCDateFormat extends SimpleDateFormat {

	private static UTCDateFormat instance;

	public static UTCDateFormat getSharedInstance() {
		if (instance == null) {
			instance = new UTCDateFormat();
		}
		return instance;
	}

	public static void setSharedInstance(UTCDateFormat format) {
		instance = format;
	}

	/**
	 * Constructor for UTCDateFormat.
	 */
	public UTCDateFormat() {
		super("dd MMM yyyy HH:mm:ss", Locale.US);
	}

	/**
	 * Constructor for UTCDateFormat.
	 * @param pattern
	 */
	public UTCDateFormat(String pattern) {
		super(pattern);
	}

	/**
	 * Constructor for UTCDateFormat.
	 * @param pattern
	 * @param locale
	 */
	public UTCDateFormat(String pattern, Locale locale) {
		super(pattern, locale);
	}

	/**
	 * Constructor for UTCDateFormat.
	 * @param pattern
	 * @param formatSymbols
	 */
	public UTCDateFormat(String pattern, DateFormatSymbols formatSymbols) {
		super(pattern, formatSymbols);
	}

	/**
	 * @see java.text.DateFormat#parseObject(String)
	 */
	public Object parseObject(String source) {
            try {
		return new Long(parse(source).getTime());
            }
            catch(ParseException e) {
            }
            return null;
	}

}
