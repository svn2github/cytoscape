package csplugins.layout.algorithms;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;

public class GroupAttributesLayoutMenu extends JMenu implements MenuListener
{
  /**
   * Constructor for the class.
   * The menu's title is "Attribute Circle Layout."
   */
  public GroupAttributesLayoutMenu()
  {
    // call the super constructor
    super("Group Attributes Layout");

    // add this class as a listener. See method menuSelected()
    this.addMenuListener(this);
  }

  /**
    * Creates a new menu item that has the title "No Attributes Loaded."
    * The item is disabled. This method is called by menuSelected() when the
    * node attributes CyNetwork could not be loaded.
    */
  private JMenuItem getNewNoAttributesMenuItem()
  {
    JMenuItem item = new JMenuItem("No Attributes Available");
    item.setEnabled(false);
    return item;
  }

  /**
    * Creates a new menu item that has the title of _attributeName.
    * When this menu item is selected, it will perform the
    * AttributeCircleLayout algorithm based on the attribute specified.
    * This method is called for each attribute in the current CyNetwork
    * by menuSelected.
    */
  private JMenuItem getNewAttributesMenuItem(final String _attributeName)
  {
    return new GroupAttributesLayout(_attributeName);
  } // end getNewAttributesMenuItem()

  // empty methods to fulfill the MenuListener interface
  public void menuDeselected(MenuEvent _e) {}
  public void menuCanceled(MenuEvent _e) {}

  /**
    * This is the heart of the class. Whenever this menu is selected,
    * menuSelected() is called. This method builds a submenu of a list of
    * attributes.
    */
  public void menuSelected(MenuEvent _e)
  {
    // remove all the previosly created menu items
    this.removeAll();

    // get the attributes
    CyAttributes attributes = Cytoscape.getNodeAttributes();

    // if there aren't any attributes, call getNewNoAttributesMenuItem()
    if (attributes == null)
    {
      this.add(getNewNoAttributesMenuItem());
      return;
    }

    // get the list of attributes in a String array. If the array
    // is null or empty, call getNewNoAttributesMenuItem()
    String attributeNames[] = attributes.getAttributeNames();
    if (attributeNames == null || attributeNames.length == 0)
    {
      this.add(getNewNoAttributesMenuItem());
      return;
    }

    // iterate over the attributeNames[] array, calling
    // getNewAttributesMenuItem() for each attribute

    for (int i = 0; i < attributeNames.length; i++)
    {
      this.add(getNewAttributesMenuItem(attributeNames[i]));
    }

  } // end menuSelected()

} // end class AttributeLayoutMenu
