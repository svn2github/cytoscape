/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
 //----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;
//----------------------------------------------------------------------------
import java.io.Serializable;
import giny.view.EdgeView;
//----------------------------------------------------------------------------
/**
 * This class is a replacement for the yFiles Arrow class.
 */
public class Arrow implements Serializable {
    
    public static final Arrow NONE = new Arrow("NONE");
    public static final Arrow STANDARD = new Arrow("STANDARD");
    public static final Arrow DELTA = new Arrow("DELTA");
    public static final Arrow DIAMOND = new Arrow("DIAMOND");
    public static final Arrow SHORT = new Arrow("SHORT");
    public static final Arrow WHITE_DELTA = new Arrow("WHITE_DELTA");
    public static final Arrow WHITE_DIAMOND = new Arrow("WHITE_DIAMOND");
    public static final Arrow SCALABLE = new Arrow("SCALABLE");
    
    String name;
    
    public Arrow(String name) {this.name = name;}
    
    public int getGinyArrow() {
        if (name.equals("DIAMOND") || name.equals("WHITE_DIAMOND")) {
            return EdgeView.DIAMOND_END;
        } else if (name.equalsIgnoreCase("short")) {
            return EdgeView.T_END;
        } else if (name.equalsIgnoreCase("none")) {
            //should be none, but Giny doesn't have a none yet
            return EdgeView.T_END;
        } else {
            return EdgeView.ARROW_END;
        }
    }
    
    public String getName() {return name;}
    public String toString() {return getName();}
    
    public static Arrow parseArrowText(String text) {
        String arrowtext = text.trim();
        
        if(arrowtext.equalsIgnoreCase("delta")) {
            return Arrow.DELTA;
        } else if(arrowtext.equalsIgnoreCase("standard")) {
            return Arrow.STANDARD;
        } else if(arrowtext.equalsIgnoreCase("arrow")) {
            return Arrow.STANDARD;
        }  else if(arrowtext.equalsIgnoreCase("diamond")) {
            return Arrow.DIAMOND;
        } else if(arrowtext.equalsIgnoreCase("short")) {
            return Arrow.SHORT;
        } else if(arrowtext.equalsIgnoreCase("white_delta")) {
            return Arrow.WHITE_DELTA;
        } else if(arrowtext.equalsIgnoreCase("whitedelta")) {
            return Arrow.WHITE_DELTA;
        } else if(arrowtext.equalsIgnoreCase("white_diamond")) {
            return Arrow.WHITE_DIAMOND;
        } else if(arrowtext.equalsIgnoreCase("whitediamond")) {
            return Arrow.WHITE_DIAMOND;
        } else if(arrowtext.equalsIgnoreCase("none")) {
            return Arrow.NONE;
        } else if(arrowtext.startsWith("scalableArrow")) {
            return Arrow.SCALABLE;
        }
        else {
            return Arrow.NONE;
        }
    } // parseArrowText
}

