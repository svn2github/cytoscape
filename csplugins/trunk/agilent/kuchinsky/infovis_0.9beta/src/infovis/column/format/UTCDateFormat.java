/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column.format;

import java.io.ObjectStreamException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Format for Unix Time Dates.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class UTCDateFormat extends SimpleDateFormat {
	private static UTCDateFormat instance;

    /**
     * Returns the instance of that format.
     * @return the instance of that format
     */
	public static UTCDateFormat getSharedInstance() {
		if (instance == null) {
			instance = new UTCDateFormat();
		}
		return instance;
	}

    /**
     * Sets the instance of that format.
     * @param format the format
     */
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
	 * @param pattern the string pattern for that format.
	 */
	public UTCDateFormat(String pattern) {
		super(pattern);
	}

	/**
	 * Constructor for UTCDateFormat.
	 * @param pattern the string pattern
	 * @param locale the locale
	 */
	public UTCDateFormat(String pattern, Locale locale) {
		super(pattern, locale);
	}

	/**
	 * Constructor for UTCDateFormat.
	 * @param pattern the string pattern
	 * @param formatSymbols the format symbols
	 */
	public UTCDateFormat(String pattern, DateFormatSymbols formatSymbols) {
		super(pattern, formatSymbols);
	}

	/**
     * {@inheritDoc}
	 */
	public Object parseObject(String source) throws ParseException {
		return new Long(parse(source).getTime());
    }
    
    private Object readResolve() throws ObjectStreamException {
        return getSharedInstance();
    }
}
