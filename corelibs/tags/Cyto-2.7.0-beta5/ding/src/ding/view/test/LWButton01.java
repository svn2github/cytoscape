
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

package ding.view.test;


/* File LWButton01.java Copyright 1997, R.G.Baldwin

This class is used to instantiate a 3D lightweight button
object that behaves quite a bit like a heavyweight Button
object but is much more responsive than a heavyweight
button under JDK 1.1.3 and Win95 on a 133 mhz Pentium
processor.

The color of the button is based on the background color
of its container, but is one shade brighter than the color
of the background.

Normally, it appears to protrude slightly out of the
screen with highlights on the left and top edges and
shadows on the bottom and right edges.  Note that the
highlighting only works if the background color does not
contain components with values of 255.

When you click the button with the mouse, it appears to
retreat into the screen and then pops back out.  As with
a heavyweight button, this causes it to gain the focus.

When it appears to retreat into the screen, its color
changes to match that of the background with heavy shadows
on the left and top and a faint outline on the bottom and
right.

The visual indication of focus is that the text on the
button is rendered in bold italics.

When you click the button, it generates an action event.

When the button has the focus and you press the space
bar, it generates an action event.

This class was tested using JDK 1.1.3 under Win95.
*/

//=======================================================//
import java.awt.*;
import java.awt.event.*;


//=======================================================//
class LWButton01 extends Component {
	//Save the raw label provided by the user here to make
	// it available to the getLabel() method.
	String rawLabel;

	//The following instance variable contains the raw
	// label with two spaces appended to each end to make
	// it easier to use for sizing the LWButton.
	String label;

	// The following instance variable is set to true if 
	// the LWButton is pressed and not released.
	boolean pressed = false;

	//The following instance variable is set to true when 
	// the LWButton has focus
	boolean gotFocus = false;

	//The following instance variable refers to a list of 
	// registered ActionListener objects.
	ActionListener actionListener;

	//-----------------------------------------------------//
	/**
	 * Creates a new LWButton01 object.
	 */
	public LWButton01() {
		//Invoke the parameterized constructor with an 
		// empty string
		this("");
	} //end constructor

	//Constructor for an LWButton with a label.
	/**
	 * Creates a new LWButton01 object.
	 *
	 * @param rawLabel  DOCUMENT ME!
	 */
	public LWButton01(String rawLabel) {
		this.rawLabel = rawLabel;

		//Add spaces on either end and save it that way
		this.label = "  " + rawLabel + "  ";

		//Invoke the enableEvents() method so that the
		// processMouseEvent(), processFocusEvent(), and
		// processKeyEvent() methods will be automatically
		// invoked whenever an event of the corresponding
		// type occurs on the LWButton.  Note that this is
		// an alternative approach to the source/listener
		// event model.
		enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK
		             | AWTEvent.KEY_EVENT_MASK);
	} //end constructor

	//-----------------------------------------------------//
	/**
	 *  DOCUMENT ME!
	 *
	 * @param listener DOCUMENT ME!
	 */
	public void addActionListener(ActionListener listener) {
		actionListener = AWTEventMulticaster.add(actionListener, listener);
	} //end addActionListener()

	//-----------------------------------------------------//
	/**
	 *  DOCUMENT ME!
	 *
	 * @param listener DOCUMENT ME!
	 */
	public void removeActionListener(ActionListener listener) {
		actionListener = AWTEventMulticaster.remove(actionListener, listener);
	} //end removeActionListener

	/*-----------------------------------------------------//
	This method is used to cause the LWButton to behave as
	if a mouse event occurred on it whenever it has the
	focus and the space bar is pressed and then released.
	Holding the space bar down generates repetitive
	events due to the repeat feature of the keyboard (this
	would need to be disabled in a real program).

	This method is automatically called whenever a key
	  event occurs on the LWButton and the method
	  enableEvents(AWTEvent.KEY_EVENT_MASK) has been
	  previously invoked on the LWButton.  */
	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void processKeyEvent(KeyEvent e) {
		//Generate mousePressed() event when the space bar 
		// is pressed by invoking the processMouseEvent()
		// method and passing an event object that 
		// impersonates a mouse pressed event.      
		if ((e.getID() == KeyEvent.KEY_PRESSED) && (e.getKeyChar() == ' '))
			processMouseEvent(new MouseEvent(this, MouseEvent.MOUSE_PRESSED, 0, 0, 0, 0, 0, false));

		//Generate mouseReleased() event when the space bar 
		// is released by invoking the processMouseEvent()
		// method and passing an event object that 
		// impersonates a mouse released event.                
		if ((e.getID() == KeyEvent.KEY_RELEASED) && (e.getKeyChar() == ' '))
			processMouseEvent(new MouseEvent(this, MouseEvent.MOUSE_RELEASED, 0, 0, 0, 0, 0, false));

		//The following statement is always needed when an
		// overridden version of processKeyEvent() is used.
		super.processKeyEvent(e);
	} //end processKeyEvent()  

	/*-----------------------------------------------------//
	This method in invoked when a focus event occurs on the
	  LWButton.  This happens when the requestFocus() method
	  is called inside the mouseReleased() event handler for
	  the LWButton.  This sets or clears the gotFocus flag
	  that is used to cause the text renderer to modify the
	  text to indicate that the LWButton has the focus.
	  When the LWButton has the focus, the text is rendered
	  in bold italics.

	This method is automatically called whenever a focus
	  event occurs on the LWButton and the method
	  enableEvents(AWTEvent.FOCUS_EVENT_MASK) has been
	  previously invoked on the LWButton.  */
	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void processFocusEvent(FocusEvent e) {
		if (e.getID() == FocusEvent.FOCUS_GAINED)
			gotFocus = true; //set the gotFocus flag

		if (e.getID() == FocusEvent.FOCUS_LOST)
			gotFocus = false; //clear the gotFocus flag

		this.invalidate();
		this.repaint();

		//The following statement is always needed when an
		// overridden version of processFocusEvent() is used.
		super.processFocusEvent(e);
	} //end processFocusEvent()

	/*-----------------------------------------------------//
	The  purpose of this method is twofold:
	 1.  Modify the appearance of the LWButton object when
	     the user clicks on it.
	 2.  Invoke the actionPerformed() method in the Listener
	     object that is registered to listen to this
	     LWButton object.

	This method is automatically called whenever a mouse
	  event occurs on the LWButton and the method
	  enableEvents(AWTEvent.MOUSE_EVENT_MASK) has been
	  previously invoked on the LWButton.            */
	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void processMouseEvent(MouseEvent e) {
		switch (e.getID()) { //what kind of mouse event?
			case MouseEvent.MOUSE_PRESSED:
				//When the mouse is pressed on the LWButton object,
				// set the "pressed" state of the object to true 
				// and force it to be repainted to change its 
				// appearance. When pressed is true, the button is
				// rendered as though it has been pressed into the
				// screen.
				pressed = true;
				this.invalidate();
				this.repaint();

				break;

			case MouseEvent.MOUSE_RELEASED:

				//When the mouse is released on the LWButton object:
				// 1. Invoke the actionPerformed() method in the 
				//    Listener objects that are registered to 
				//    listen to the LWButton object.
				// 2. Confirm that the "pressed" state is true and 
				//    if so, set it to false and force the object 
				//    to be repainted to change its appearance.
				//    When pressed is false, the button is rendered
				//    so as to appear to protrude out of the 
				//    screen.
				// 3. Request the focus for the LWButton object.
				//if an ActionListener is registered
				if (actionListener != null) {
					//Invoke the actionPerformed() method on the list
					// of listener objects registered on the 
					// LWButton object.  Instantiate and pass an
					// ActionEvent object as a parameter.
					actionListener.actionPerformed(new ActionEvent(this,
					                                               ActionEvent.ACTION_PERFORMED,
					                                               label));
				} //end if on actionListener

				if (pressed == true) {
					pressed = false;
					this.requestFocus();
					this.invalidate();
					this.repaint();
				} //end if on pressed

				break;
		} //end switch

		//The following statement is always needed when an
		// overridden version of processMouseEvent() is used.
		super.processMouseEvent(e);
	} //end processMouseEvent()

	//-----------------------------------------------------//
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Dimension getPreferredSize() {
		if (getFont() != null) {
			FontMetrics fm = getFontMetrics(getFont());

			return new Dimension(fm.stringWidth(label), fm.getHeight() + 10);
		} else

			return new Dimension(10, 10); //no font
	} //end getPreferredSize()

	//Override the getMinimumSize() method and specify
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Dimension getMinimumSize() {
		return new Dimension(10, 10);
	} //end getMinimumSize()

	//-----------------------------------------------------//
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getLabel() { //gets the label

		return rawLabel;
	} //end getLabel()

	/**
	 * DOCUMENT ME!
	 *
	 * @param rawLabel DOCUMENT ME!
	 */
	public void setLabel(String rawLabel) { //sets the label
		this.rawLabel = rawLabel; //save the raw label

		//Add spaces to each end of the rawLabel to make it
		// easier to center the label in the LWButton.
		this.label = "  " + rawLabel + "  ";
		this.invalidate();
		this.repaint();
	} //end setLabel()

	//-----------------------------------------------------//
	/**
	 *  DOCUMENT ME!
	 *
	 * @param g DOCUMENT ME!
	 */
	public void paint(Graphics g) { //paints the LWButton

		//If LWButton has the focus, display the text in
		// bold italics.  Otherwise display plain.
		if (gotFocus)
			g.setFont(new Font(getFont().getName(), Font.BOLD | Font.ITALIC, getFont().getSize()));
		else
			g.setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()));

		if (pressed) { //if the pressed flag is true
			g.setColor(getBackground());
			g.fillRect( //fill rectangle with background color
			0, 0, this.getSize().width, this.getSize().height);

			//Draw shadows three shades darker than background
			g.setColor(getBackground().darker().darker().darker());

			//Note that three offset rectangles are drawn to
			// produce a shadow effect on the left and top of
			// the rectangle.               
			g.drawRect( //
			0, 0, this.getSize().width, this.getSize().height);
			g.drawRect(1, 1, this.getSize().width, this.getSize().height);
			g.drawRect(2, 2, this.getSize().width, this.getSize().height);

			//Now draw a faint outline on the bottom and right of
			// the rectangle.
			g.setColor(getBackground().darker());
			g.drawRect(-1, -1, this.getSize().width, this.getSize().height);

			//Now center the text in the LWButton object
			FontMetrics fm = getFontMetrics(getFont());
			g.setColor(getForeground());
			g.drawString(label, (getSize().width / 2) - (fm.stringWidth(label) / 2),
			             (getSize().height / 2) + (fm.getAscent() / 2));
		} //end if(pressed)

		else { //not pressed
			//Make the protruding LWButton object one shade
			// brighter than the background.
			g.setColor(getBackground().brighter());
			g.fillRect( //and fill a rectangle
			0, 0, this.getSize().width, this.getSize().height);

			//Set the color for the shadows three shades darker
			// than the background.
			g.setColor(getBackground().darker().darker().darker());

			//Draw two offset rectangles to create shadows on 
			// the right and bottom.               
			g.drawRect(-1, -1, this.getSize().width, this.getSize().height);
			g.drawRect(-2, -2, this.getSize().width, this.getSize().height);

			//Highlight the left and top two shades brighter 
			// than the background, one shade brighter than the
			// color of the LWButton itself which is one shade
			// brighter than the background.
			g.setColor(getBackground().brighter().brighter());
			g.drawRect( //
			0, 0, this.getSize().width, this.getSize().height);

			//Now place the text in the LWButton object shifted
			// by two pixels up and to the left.           
			FontMetrics fm = getFontMetrics(getFont());
			g.setColor(getForeground());
			g.drawString(label, (getSize().width / 2) - (fm.stringWidth(label) / 2) - 2,
			             ((getSize().height / 2) + (fm.getAscent() / 2)) - 2);
		} //end else
	} //end overridden paint() method

	//-----------------------------------------------------//
} //end class LWButton01
//=======================================================//
