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
}
