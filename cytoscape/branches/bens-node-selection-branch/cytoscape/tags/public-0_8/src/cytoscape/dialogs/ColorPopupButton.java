// ColorPopupButton.java
//---------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package cytoscape.dialogs;
//---------------------------------------------------------------------------------------
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.text.*;
//---------------------------------------------------------------------------------------
/**
 * Displays a button and a color swatch; the button popups up a standard Swing ColorChooser.
 * <p>
 * <b> todo: </b> <i> (pshannon, 2002/02/26) </i> add an (optional) button to the right
 * of the color swatch which fires a callback to a method which can  apply the 
 * current color choice to the target.  This is motivated by the need to set <b><i>only</i></b>
 * the background color of the CytoscapeWindow, leaving node, node border, and edge colors
 * unchanged.
 */
public class ColorPopupButton extends JPanel implements ActionListener{
    Color currentColor;
    JColorChooser colorChoice;
    final JLabel color = new JLabel(" ");
    final JLabel identifier = new JLabel("   Current Color:   ");
    JFrame mainFrame;
    JPanel mainPanel;
    JLabel colorSelectionForPanel;
    JButton colorButton;
    String name;
    String title;
    public ColorPopupButton (String title, Color startColor){
	this.title = title;
	currentColor = startColor;
	setup();
    }
    public ColorPopupButton (String attributeName,String title,Color startColor){
	this.title = title;
	currentColor = startColor;
	name = attributeName;
	setup();
    }

    private void setup(){
	colorButton = new JButton(title);	
	colorButton.addActionListener(this);
	mainPanel = new JPanel();
	colorSelectionForPanel = new JLabel("     ");
	colorSelectionForPanel.setOpaque(true);
	colorSelectionForPanel.setBackground(currentColor);
	add(colorButton);
	add(colorSelectionForPanel);
    }
    public void actionPerformed(ActionEvent e){
	mainFrame = new JFrame("Color Chooser");
	mainFrame.setLocation(colorButton.getLocationOnScreen());
	color.setBackground(currentColor);
	color.setOpaque(true);
	JPanel subPanel = new JPanel();
	subPanel.setLayout(new BorderLayout());
	subPanel.add(identifier, BorderLayout.WEST);
	subPanel.add(color, BorderLayout.CENTER);
	
	JPanel displayPanel = new JPanel();
	displayPanel.setLayout(new BorderLayout());
	colorChoice = new JColorChooser(currentColor);
	colorChoice.getSelectionModel().addChangeListener(
       	      new ChangeListener(){
		      public void stateChanged(ChangeEvent t){
			  Color newColor = colorChoice.getColor();
			  color.setBackground(newColor);
		      }});
	colorChoice.setPreviewPanel(new JPanel()); //remove preview panel
	JScrollPane scrollPane = new JScrollPane(colorChoice);
	
	displayPanel.add(subPanel, BorderLayout.NORTH);
	displayPanel.add(scrollPane, BorderLayout.CENTER);
	 

	JPanel buttonPanel    = new JPanel();
	JButton applyButton   = new JButton("Set Color");
	JButton dismissButton = new JButton("Dismiss");
	buttonPanel.setLayout(new GridLayout(0,2));
	applyButton.addActionListener   (new ApplyColorAction());
	dismissButton.addActionListener (new DismissColorAction()); 
	buttonPanel.add(applyButton);
	buttonPanel.add(dismissButton);

	displayPanel.add(buttonPanel, BorderLayout.SOUTH);
	mainFrame.getContentPane().add(displayPanel);
	mainFrame.pack();
	mainFrame.setVisible(true);
    }
    public class ApplyColorAction extends AbstractAction{   
	public void actionPerformed(ActionEvent e){
            // System.out.println ("ColorPopupButton.actionPerformed: " + e);
	    currentColor = colorChoice.getColor();
	    String test = title;
	    setColor(currentColor);
	    mainFrame.dispose();
	}
    }
    public class DismissColorAction extends AbstractAction{
	DismissColorAction(){super ("");}
	public void actionPerformed (ActionEvent e){
	    mainFrame.dispose();
	}
    }
     public void setColor(Color c){
	 colorSelectionForPanel.setBackground(c);
    }
    public Color getColor(){
	return currentColor;
    } 
}//class ColorPopupButton
