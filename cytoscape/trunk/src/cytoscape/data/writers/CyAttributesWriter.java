/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.data.writers;

import cytoscape.data.CyAttributes;

import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.MultiHashMapDefinition;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * CyAttributeWriter extracted from AttributeSaverDialog.
 *
 */
public class CyAttributesWriter {
	/**
	 *
	 */
	public static String newline = System.getProperty("line.separator");
    public static final String ENCODE_PROPERTY = "cytoscape.encode.attributes";

    public static final String ENCODING_SCHEME = "UTF-8";
	private final CyAttributes cyAttributes;
	private final String attributeName;
	private Writer fileWriter;
    private boolean doEncoding;

	/**
	 * Creates a new CyAttributesWriter2 object.
	 *
	 * @param attributes  DOCUMENT ME!
	 * @param attributeName  DOCUMENT ME!
	 * @param fileWriter  DOCUMENT ME!
	 */
	public CyAttributesWriter(final CyAttributes attributes, final String attributeName,
	                           final Writer fileWriter) {
		this.cyAttributes = attributes;
		this.attributeName = attributeName;
		this.fileWriter = fileWriter;
        doEncoding = Boolean.valueOf(System.getProperty(ENCODE_PROPERTY, "true"));
    }

	/**
	 * Write out the state for the given attributes
	 *
	 * @param selectedRows
	 *
	 * @return number of files successfully saved, the better way to do this
	 *         would just be to throw the error and display a specific message
	 *         for each failure, but oh well.
	 * @throws IOException
	 *
	 */
	public void writeAttributes() throws IOException {
		final String className;
		final byte dataType = cyAttributes.getMultiHashMapDefinition()
		                                  .getAttributeValueType(attributeName);

		if ((dataType == CyAttributes.TYPE_COMPLEX) || (dataType == CyAttributes.TYPE_SIMPLE_MAP)
		    || (dataType == CyAttributes.TYPE_UNDEFINED))
			throw new IOException("Unsupported Datatype.");

		if (dataType == MultiHashMapDefinition.TYPE_BOOLEAN)
			className = "java.lang.Boolean";
		else if (dataType == MultiHashMapDefinition.TYPE_INTEGER)
			className = "java.lang.Integer";
		else if (dataType == MultiHashMapDefinition.TYPE_FLOATING_POINT)
			className = "java.lang.Double";
		else
			className = "java.lang.String";

		fileWriter.write(attributeName + " (class=" + className + ")" + newline);

		final MultiHashMap attributeMap = cyAttributes.getMultiHashMap();

		if (attributeMap != null) {
			final Iterator<String> keys = cyAttributes.getMultiHashMap().getObjectKeys(attributeName);

			String key;
			Object value;
			Iterator objIt;
            String vs;
			StringBuilder result = new StringBuilder();

			while (keys.hasNext()) {
				key = keys.next();

				if (cyAttributes.getType(attributeName) == CyAttributes.TYPE_SIMPLE_LIST)
					value = cyAttributes.getListAttribute(key, attributeName);
				else
					value = cyAttributes.getAttribute(key, attributeName);
					
                key = encodeString(key);

				if (value != null) {
					if (value instanceof List) {
						result.append(key + " = ");

						if (((Collection) value).size() > 0) {
                            Object o;

							objIt = ((Collection) value).iterator();
							result.append("(");
                            o = objIt.next();
                            vs = o.toString();
                            vs = slashEncodeString(vs);
                            vs = encodeString(vs);
							result.append(vs);

							while (objIt.hasNext()) {

                                o = objIt.next();
                                vs = o.toString();
                                vs = slashEncodeString(vs);
                                vs = encodeString(vs);
								result.append("::" + vs);
                            }
							result.append(")" + newline);
							fileWriter.write(result.toString());
							result = new StringBuilder();
						}
					} else {
                        vs = value.toString();
                        vs = slashEncodeString(vs);
                        vs = encodeString(vs);
						fileWriter.write(key + " = " + vs + newline);
                    }
				}
			}

			fileWriter.flush();
		}

		fileWriter.close();
		fileWriter = null;
	}

    private String encodeString(String in) throws UnsupportedEncodingException {
        if (doEncoding) {
            in = URLEncoder.encode(in, ENCODING_SCHEME);
        }

        return in;
    }

    private static String slashEncodeString(String in) {
        StringBuilder sb;

        sb = new StringBuilder(in.length());
        for (int i = 0; i < in.length(); i++) {
            char c;

            c = in.charAt(i);
            switch (c) {
                case '\n': {
                    sb.append("\\n");
                    break;
                }
                case '\t': {
                    sb.append("\\t");
                    break;
                }
                case '\b': {
                    sb.append("\\b");
                    break;
                }
                case '\r': {
                    sb.append("\\r");
                    break;
                }
                case '\f': {
                    sb.append("\\f");
                    break;
                }
                case '\\': {
                    sb.append("\\\\");
                    break;
                }
                default : {
                    sb.append(c);
                    break;
                }
            }
        }

        return sb.toString();
    }

    public boolean isDoEncoding() {
        return doEncoding;
    }

    public void setDoEncoding(boolean doEnc) {
        doEncoding = doEnc;
    }
}
