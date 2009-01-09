//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;
//----------------------------------------------------------------------------
import java.awt.Color;
import java.awt.Font;
import y.view.LineType;
import y.view.Arrow;
//----------------------------------------------------------------------------
/**
 * Objects of this class hold data describing the appearance of an Edge.
 */
public class EdgeAppearance {
    
    Color color;
    LineType lineType;
    Arrow sourceArrow;
    Arrow targetArrow;
    String label;
    String toolTip;
    Font font;
    
    public EdgeAppearance() {}
    
    public Color getColor() {return color;}
    public void setColor(Color c) {color = c;}
    
    public LineType getLineType() {return lineType;}
    public void setLineType(LineType lt) {lineType = lt;}
    
    public Arrow getSourceArrow() {return sourceArrow;}
    public void setSourceArrow(Arrow a) {sourceArrow = a;}
    
    public Arrow getTargetArrow() {return targetArrow;}
    public void setTargetArrow(Arrow a) {targetArrow = a;}
    
    public String getLabel() {return label;}
    public void setLabel(String s) {label = s;}
    
    public String getToolTip() {return toolTip;}
    public void setToolTip(String s) {toolTip = s;}

    public Font getFont() {return font;}
    public void setFont(Font f) {font = f;}
}

