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
//----------------------------------------------------------------------------
/**
 * Objects of this class hold data describing global appearance attributes
 * of the graph window.
 */
 public class GlobalAppearance {
    
    Color backgroundColor;
    Color sloppySelectionColor;

    public GlobalAppearance() {}
    
    public Color getBackgroundColor() {return backgroundColor;}
    public void setBackgroundColor(Color c) {backgroundColor = c;}
    
    public Color getSloppySelectionColor() {return sloppySelectionColor;}
    public void setSloppySelectionColor(Color c) {sloppySelectionColor = c;}
}
