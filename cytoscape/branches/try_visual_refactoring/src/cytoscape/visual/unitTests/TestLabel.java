
package cytoscape.visual.unitTests; 

import java.awt.Font;
import java.awt.Paint;
import java.awt.Color;
import giny.view.Label;

public class TestLabel implements Label {

	int position = 0;
	Paint textPaint = Color.RED;
	double greekThreshold = 0.0;
	String text = "";
	Font font = new Font("plain", Font.PLAIN, 10);

	public TestLabel() {}

	public void setPositionHint ( int p ) { position = p; }

	public Paint getTextPaint() { return textPaint; }

	public void setTextPaint(Paint tp) { textPaint = tp ;}
	
	public double getGreekThreshold() {return greekThreshold;}

	public void setGreekThreshold(double t) {greekThreshold = t;}
		
	public String getText() { return text; }

	public void setText(String t) { text = t; }
	
	public Font getFont() { return font; } 
	
	public void setFont(Font f) { font = f ;}
}
