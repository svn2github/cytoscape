/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.cytoscape.coreplugin.cpath.util;


/**
 * Common Simple HTML Writing Utilities.
 *
 * @author Ethan Cerami.
 */
public class HtmlUtil {

    /**
     * Starts a new HTML Table.
     *
     * @param html StringBuffer Object.
     */
    public static void startTable(StringBuffer html) {
        html.append("<TABLE WIDTH=100% "
                + "CELLSPACING=2 CELLPADDING=2 BORDER=0>");
    }

    /**
     * Ends an HTML Table.
     *
     * @param html StringBuffer Object.
     */
    public static void endTable(StringBuffer html) {
        html.append("</TABLE>");
    }


    /**
     * Adds Data.
     *
     * @param html  HTML StringBuffer.
     * @param name  Data Name.
     * @param value Data Value.
     */
    public static void addData
            (StringBuffer html, String name, String value) {
        addHeader(html, name, "CCCCCC");
        addDataRow(html, value);
    }

    /**
     * Adds Data.
     *
     * @param html  HTML StringBuffer.
     * @param name  Data Name.
     * @param value Data Value.
     * @param color String Color Value.T
     */
    public static void addData
            (StringBuffer html, String name, String value, String color) {
        addHeader(html, name, color);
        addDataRow(html, value);
    }

    /**
     * Adds TD Data.
     *
     * @param html HTML StringBuffer.
     * @param data Data to Add.
     */
    public static void addTD(StringBuffer html, String data) {
        html.append("<TD>" + data + "</TD>");
    }

    /**
     * Adds Header.
     *
     * @param html   HTML StringBuffer.
     * @param header Header Title
     */
    private static void addHeader(StringBuffer html, String header,
            String color) {
        html.append("<TR BGCOLOR='#" + color + "'><TD>"
                + header + "</TD></TR>");
    }

    /**
     * Add Data Field.
     *
     * @param html HTML StringBuffer.
     * @param data Data Contents.
     */
    private static void addDataRow
            (StringBuffer html, String data) {
        html.append("<TR VALIGN=TOP>");
        html.append("<TD>");
        html.append(data);
        html.append("<BR>");
        html.append("</TD>");
        html.append("</TR>");
    }
}
