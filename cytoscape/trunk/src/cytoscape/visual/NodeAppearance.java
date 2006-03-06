
/*
  File: NodeAppearance.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
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

//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;
//----------------------------------------------------------------------------
import java.awt.Color;
import java.awt.Font;
import cytoscape.visual.LineType;
//----------------------------------------------------------------------------
/**
 * Objects of this class hold data describing the appearance of a Node.
 */
 public class NodeAppearance {
    
    Color fillColor;
    Color borderColor;
    LineType borderLineType;
    byte shape;
    double width;
    double height;
    String label;
    String toolTip;
    Font font;
    Color fontColor;
    
    public NodeAppearance() {}
    
    public Color getFillColor() {return fillColor;}
    public void setFillColor(Color c) {fillColor = c;}
    
    public Color getBorderColor() {return borderColor;}
    public void setBorderColor(Color c) {borderColor = c;}
    
    public LineType getBorderLineType() {return borderLineType;}
    public void setBorderLineType(LineType lt) {borderLineType = lt;}
    
    public byte getShape() {return shape;}
    public void setShape(byte s) {shape = s;}
    
    public double getWidth() {return width;}
    public void setWidth(double d) {width = d;}
    
    public double getHeight() {return height;}
    public void setHeight(double d) {height = d;}
    
    public String getLabel() {return label;}
    public void setLabel(String s) {label = s;}
    
    public String getToolTip() {return toolTip;}
    public void setToolTip(String s) {toolTip = s;}

    public Font getFont() {return font;}
    public void setFont(Font f) {font = f;}

    public Color getLabelColor() {return fontColor;}
    public void setLabelColor(Color c) {fontColor = c;}
}
