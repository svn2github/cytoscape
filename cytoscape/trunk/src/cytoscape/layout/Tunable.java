// vim: set ts=2: */
package cytoscape.layout;

import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;

import javax.swing.BoxLayout;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;


/**
 * The Tunable class provides a convenient way to encapsulate
 * CyLayoutAlgorithm property and settings values.  Each Tunable
 * has a name, which corresponds to the property name, a description,
 * which is used as the label in the settings dialog, a type, a
 * value, and information about the value, such as a list of options
 * or the lower and upper bounds for the value.  These are meant
 * to be used as part of the LayoutSettingsDialog (see getPanel).
 */
public class Tunable implements FocusListener,ChangeListener,ActionListener,ItemListener {
	private String name;
	private String desc;
	private int type = STRING;
	private int flag = 0;
	private Object value;
	private Object lowerBound;
	private Object upperBound;
	private JComponent inputField = null;
	private JSlider slider = null;
	private boolean valueChanged = true;
	private String savedValue = null;
	private boolean usingSlider = false;
	private List<String>attributeList = null;
	private List<TunableListener>listenerList = null;
	
	private boolean immutable = false;

	/**
	 * Types
	 */
	final public static int INTEGER = 0;
	final public static int DOUBLE = 1;
	final public static int BOOLEAN = 2;
	final public static int STRING = 3;
	final public static int NODEATTRIBUTE = 4;
	final public static int EDGEATTRIBUTE = 5;
	final public static int LIST = 6;
	final public static int GROUP = 7;
	final public static int BUTTON = 8;

	/**
	 * Flags
	 */
	final public static int NOINPUT = 0x1;

	/**
	 * For attributes, indicate that the list should be restricted to integer
	 * or float attributes.
	 */
	final public static int NUMERICATTRIBUTE = 0x2;

	/**
	 * For LIST, NODEATTRIBUTE, or EDGEATTRIBUTE types, use a list widget that
	 * supports multiselect rather than a combo box.
	 */
	final public static int MULTISELECT = 0x4;

	/**
 	 * For INTEGER or DOUBLE tunables, preferentially use a slider widget.  This
 	 * will *only* take effect if the upper and lower bounds are provided.
 	 */
	final public static int USESLIDER = 0x8;

	/**
	 * Constructor to create a Tunable with no bounds
	 * information, and no flag.
	 *
	 * @param name The name of the Tunable
	 * @param desc The description of the Tunable
	 * @param type Integer value that represents the type of
	 *             the Tunable.  The type not only impact the
	 *             way that the value is interpreted, but also
	 *             the component used for the LayoutSettingsDialog
	 * @param value The initial (default) value of the Tunable
	 */
	public Tunable(String name, String desc, int type, Object value) {
		this(name, desc, type, value, null, null, 0);
	}

	/**
	 * Constructor to create a Tunable with no bounds
	 * information, but with a flag.
	 *
	 * @param name The name of the Tunable
	 * @param desc The description of the Tunable
	 * @param type Integer value that represents the type of
	 *             the Tunable.  The type not only impact the
	 *             way that the value is interpreted, but also
	 *             the component used for the LayoutSettingsDialog
	 * @param value The initial (default) value of the Tunable
	 * @param flag The initial value of the flag.  This can be
	 *             used to indicate that this tunable is not user
	 *             changeable (e.g. debug), or to indicate if there
	 *             is a specific type for the attributes.
	 */
	public Tunable(String name, String desc, int type, Object value, int flag) {
		this(name, desc, type, value, null, null, flag);
	}

	/**
	 * Constructor to create a Tunable with bounds
	 * information as well as a flag.
	 *
	 * @param name The name of the Tunable
	 * @param desc The description of the Tunable
	 * @param type Integer value that represents the type of
	 *             the Tunable.  The type not only impact the
	 *             way that the value is interpreted, but also
	 *             the component used for the LayoutSettingsDialog
	 * @param value The initial (default) value of the Tunable.  This
	 *             is a String in the case of an EDGEATTRIBUTE or
	 *             NODEATTRIBUTE tunable, it is an Integer index
	 *             a LIST tunable.
	 * @param lowerBound An Object that either represents the lower
	 *             bounds of a numeric Tunable or an array of values
	 *             for an attribute (or other type of) list.
	 * @param upperBound An Object that represents the upper bounds
	 *             of a numeric Tunable.
	 * @param flag The initial value of the flag.  This can be
	 *             used to indicate that this tunable is not user
	 *             changeable (e.g. debug), or to indicate if there
	 *             is a specific type for the attributes.
	 */
	public Tunable(String name, String desc, int type, Object value, Object lowerBound,
	               Object upperBound, int flag) {
		this(name, desc, type, value, lowerBound, upperBound, flag, false);
	}
	
	public Tunable(String name, String desc, int type, Object value, Object lowerBound,
            Object upperBound, int flag, boolean immutable) {
		this.name = name;
		this.desc = desc;
		this.type = type;
		this.value = value;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
		this.flag = flag;
		this.immutable = immutable;
		// CyLogger.getLogger().info("Tunable "+desc+" has value "+value);
	}

	/**
	 * This method can be used to set a flag for this Tunable
	 *
	 * @param flag integer value the contains the flag to set.
	 */
	public void setFlag(int flag) {
		this.flag |= flag;
	}

	/**
	 * This method can be used to clear a flag for this Tunable
	 *
	 * @param flag integer value the contains the flag to be cleared.
	 */
	public void clearFlag(int flag) {
		this.flag &= ~flag;
	}

	/**
 	 * This method can be used to set the "immutable" boolean,
 	 * which essentially get's mapped to the appropriate mechanism
 	 * for allowing a value to be editted.
 	 *
 	 * @param immutable 'true' if this is an immutable value
 	 */
	public void setImmutable(boolean immutable) {
		this.immutable = immutable;
		if (inputField != null)
			inputField.setEnabled(!immutable);
	}

	/**
	 * This method returns the value of the immutable boolean.
	 *
	 * @return 'true' if this Tunable is immutable
	 */
	public boolean getImmutable() {
		return this.immutable;
	}

	/**
	 * This method is used to set the value for this Tunable.  If
	 * this is an INTEGER, DOUBLE, or BOOLEAN Tunable, then value
	 * is assumed to be a String.  This also sets the "changed" state
	 * of the value to "true".
	 *
	 * @param value Object (usually String) containing the value to be set
	 */
	public void setValue(Object value) {

		switch (type) {
			case INTEGER:
				// CyLogger.getLogger().info("Setting Integer tunable "+desc+" value to "+value);
				if (value.getClass() == String.class)
					this.value = new Integer((String) value);
				else
					this.value = value;

				if ((slider != null) && ((flag & USESLIDER) != 0))
					slider.setValue(sliderScale(this.value));
				else if (inputField != null) {
					((JTextField)inputField).setText(this.value.toString());
				}
				break;

			case DOUBLE:
					// CyLogger.getLogger().info("Setting Double tunable "+desc+" value to "+value);
				if (value.getClass() == String.class)
					this.value = new Double((String) value);
				else
					this.value = value;
				if ((slider != null) && ((flag & USESLIDER) != 0))
					slider.setValue(sliderScale(value));
				else if (inputField != null) {
					((JTextField)inputField).setText((String)value);
				}
				break;

			case BOOLEAN:
				// CyLogger.getLogger().info("Setting Boolean tunable "+desc+" value to "+value);
				if (value.getClass() == String.class)
					this.value = new Boolean((String) value);
				else
					this.value = value;
				if (inputField != null)
					((JCheckBox)inputField).setSelected(((Boolean)this.value).booleanValue());
				break;

			case LIST:
				// CyLogger.getLogger().info("Setting List tunable "+desc+" value to "+value);
				if ((flag & MULTISELECT) != 0) {
					// Multiselect LIST -- value is a List of Integers, or String values
					this.value = value;
					if (inputField != null) {
						((JList)inputField).setSelectedIndices(decodeIntegerArray((String)value));
					}
				} else {
					if (value.getClass() == String.class)
						this.value = new Integer((String) value);
					else
						this.value = value;

					if (inputField != null)
						((JComboBox)inputField).setSelectedIndex(((Integer)this.value).intValue());
				}
				break;

			case NODEATTRIBUTE:
			case EDGEATTRIBUTE:
				// CyLogger.getLogger().info("Setting List tunable "+desc+" value to "+value);
				if ((flag & MULTISELECT) != 0) {
					// Multiselect LIST -- value is a List of Integers, or String values
					this.value = value;
					if (inputField != null) {
						((JList)inputField).setSelectedIndices(getSelectedValues(attributeList, decodeArray((String)value)));
					}
				} else {
					this.value = value;

					if (inputField != null)
						((JComboBox)inputField).setSelectedItem(this.value);
				}
				break;

			case GROUP:
				// CyLogger.getLogger().info("Setting Group tunable "+desc+" value to "+value);
				if (value.getClass() == String.class)
					this.value = new Integer((String) value);
				else
					this.value = value;
				return;

			case STRING:
				// CyLogger.getLogger().info("Setting String tunable "+desc+" value to "+value);
				this.value = value;
				if (inputField != null)
					((JTextField)inputField).setText((String)value);
				break;

			case BUTTON:
				// CyLogger.getLogger().info("Setting String tunable "+desc+" value to "+value);
				this.value = value;
				if (inputField != null)
					((JButton)inputField).setText((String)value);
				break;
		}

		inputField.validate();
		valueChanged = true;
	}

	/**
	 * This method returns the current value.  This method
	 * also resets the state of the value to indicate that
	 * it has not been changed since the last "get".
	 *
	 * @return Object that contains the value for this Tunable
	 */
	public Object getValue() {
		valueChanged = false;

		return value;
	}

	/**
	 * Returns the changed state of the value.  If true,
	 * the value has been changed since it was last retrieved.
	 *
	 * @return boolean value of the changed state.
	 */
	public boolean valueChanged() {
		return valueChanged;
	}

	/**
	 * Method to set the lowerBound for this Tunable.  This might be used to change a Tunable
	 * based on changes in the plugin environment.
	 *
	 * @param lowerBound the new lowerBound for the tunable
	 */
	public void setLowerBound(Object lowerBound) {
		this.lowerBound = lowerBound;
		if (inputField == null)
			return;

		// If we're a slider or a list, this might require us to reset things...
		if (type == LIST) {
			Object[] listData = (Object[])lowerBound;
			if ((flag & MULTISELECT) != 0) {
				JList list = (JList)inputField;
				list.setListData(listData);
			} else {
				JComboBox cbox = (JComboBox)inputField;
				cbox.removeAllItems();
				for (int i = 0; i < listData.length; i++)
					cbox.addItem(listData[i]);
			}
		} else if (type == NODEATTRIBUTE) {
		} else if (type == EDGEATTRIBUTE) {
		} else if ((flag & USESLIDER) != 0) {
			slider.setMinimum(sliderScale(lowerBound));
			slider.setLabelTable(createLabels(slider));
		}
	}

	/**
	 * Method to get the lowerBound for this Tunable.
	 *
	 * @return the lowerBound the tunable
	 */
	public Object getLowerBound() {
		return this.lowerBound;
	}

	/**
	 * Method to set the upperBound for this Tunable.  This might be used to change a Tunable
	 * based on changes in the plugin environment.
	 *
	 * @param upperBound the new upperBound for the tunable
	 */
	public void setUpperBound(Object upperBound) {
		this.upperBound = upperBound;
		if (inputField == null)
			return;

		// If we're a slider, this might require us to reset things
		if ((flag & USESLIDER) != 0) {
			slider.setMaximum(sliderScale(upperBound));
			slider.setLabelTable(createLabels(slider));
		}
	}

	/**
	 * Method to get the upperBound for this Tunable.
	 *
	 * @return the upperBound the tunable
	 */
	public Object getUpperBound() {
		return this.upperBound;
	}

	/**
	 * Method to return a string representation of this Tunable,
	 * which is essentially its name.
	 *
	 * @return String value of the name of the Tunable.
	 */
	public String toString() {
		return name;
	}

	/**
	 * Method to return a string representation of this Tunable,
	 * which is essentially its name.
	 *
	 * @return String value of the name of the Tunable.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Method to return the type of this Tunable.
	 *
	 * @return Tunable type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Method to return the description for this Tunable.
	 *
	 * @return Tunable description
	 */
	public String getDescription() {
		return desc;
	}


	/**
 	 * Method to add a value listener to this Tunable.  A value
 	 * listener is called whenever a tunable value is updated
 	 * by the user (as opposed to programmatically).  This can
 	 * be used to change the UI based on certain inputs.
 	 *
 	 * @param listener the TunableListener to add
 	 */
	public void addTunableValueListener(TunableListener listener) {
		if (listener == null)
			return;

		if (listenerList == null)
			listenerList = new ArrayList();

		listenerList.add(listener);
	}

	/**
 	 * Method to remove a value listener from this Tunable.  A value
 	 * listener is called whenever a tunable value is updated
 	 * by the user (as opposed to programmatically).  This can
 	 * be used to change the UI based on certain inputs.
 	 *
 	 * @param listener the TunableListener to remove
 	 */
	public void removeTunableValueListener(TunableListener listener) {
		if (listener == null || listenerList == null)
			return;

		listenerList.remove(listener);
	}

	/**
 	 * Method to call all of the value listeners.
 	 */
	public void updateValueListeners() {
		if (listenerList == null)
			return;

		updateValue();

		for (TunableListener listener: listenerList)
			listener.tunableChanged(this);
	}

	/**
	 * This method returns a JPanel suitable for inclusion in the
	 * LayoutSettingsDialog to represent this Tunable.  Note that
	 * while the type of the widgets used to represent the Tunable
	 * are customized to represent the type, no ActionListeners are
	 * included.  The dialog must call updateSettings to set the
	 * value of the Tunable from the user input data.
	 *
	 * @return JPanel that can be used to enter values for this Tunable
	 */
	public JPanel getPanel() {
		if ((flag & NOINPUT) != 0)
			return null;

		if (type == GROUP) {
			JPanel tunablesPanel = new JPanel();
			BoxLayout box = new BoxLayout(tunablesPanel, BoxLayout.Y_AXIS);
			tunablesPanel.setLayout(box);

			// Special case for groups
			Border refBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			TitledBorder titleBorder = BorderFactory.createTitledBorder(refBorder, this.desc);
			titleBorder.setTitlePosition(TitledBorder.LEFT);
			titleBorder.setTitlePosition(TitledBorder.TOP);
			tunablesPanel.setBorder(titleBorder);

			return tunablesPanel;
		}

		JPanel tunablePanel = new JPanel(new BorderLayout(0, 2));
		JLabel tunableLabel = new JLabel(desc);
		String labelLocation = BorderLayout.LINE_START;
		String fieldLocation = BorderLayout.LINE_END;

		if ((type == DOUBLE) || (type == INTEGER)) {
			if ( ((flag & USESLIDER) != 0) && (lowerBound != null) && (upperBound != null)) {
				// We're going to use a slider,  We need to be somewhat intelligent about the bounds and
				// labels.  It would also be nice to provide feedback, which we do by providing a text field
				// in addition to the slider.  The text field can also be used to enter the desired value
				// directly.

				slider = new JSlider(JSlider.HORIZONTAL, 
				                            sliderScale(lowerBound), 
				                            sliderScale(upperBound), 
				                            sliderScale(value));

				slider.setLabelTable(createLabels(slider));
				slider.setPaintLabels(true);
				slider.addChangeListener(this);
				tunablePanel.add(tunableLabel, BorderLayout.NORTH);
				tunablePanel.add(slider, BorderLayout.CENTER);

				JTextField textField = new JTextField(value.toString(), 4);
				textField.addFocusListener(this);
				inputField = textField;
				tunablePanel.add(textField, BorderLayout.EAST);
				textField.setBackground(Color.white);
				return tunablePanel;

			} else {
				// We can't use a slider, so turn off the flag
				clearFlag(USESLIDER);
				JTextField field = new JTextField(value.toString(), 8);
				field.setHorizontalAlignment(JTextField.RIGHT);
				// If we have an upper and/or lower bounds, we want to "listen" for changes
				field.addFocusListener(this);
				inputField = field;
			}
		} else if (type == BOOLEAN) {
			JCheckBox box = new JCheckBox();
			box.setSelected(((Boolean) value).booleanValue());
			box.addItemListener(this);
			inputField = box;
		} else if (type == NODEATTRIBUTE) {
			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			inputField = getAttributePanel(nodeAttributes);
		} else if (type == EDGEATTRIBUTE) {
			CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
			inputField = getAttributePanel(edgeAttributes);
		} else if (type == LIST) {
			inputField = getListPanel((Object[]) lowerBound);
		} else if (type == STRING) {
			JTextField field = new JTextField(value.toString(), 20);
			field.addFocusListener(this);
			field.setHorizontalAlignment(JTextField.RIGHT);
			inputField = field;
		} else if (type == BUTTON) {
			JButton button = new JButton((String)value);
			button.addActionListener((ActionListener)lowerBound);
			button.setActionCommand(name);
			inputField = button;
		}

		// Added by kono
		// This allow immutable value.
		if(immutable) {
			inputField.setEnabled(false);
		}
		inputField.setBackground(Color.white);

		tunablePanel.add(tunableLabel, labelLocation);

		// Special case for MULTISELECT lists
		if ((type == LIST || type == NODEATTRIBUTE || type == EDGEATTRIBUTE) 
			  && (flag & MULTISELECT) != 0) {
			JScrollPane listScroller = new JScrollPane(inputField);
			listScroller.setPreferredSize(new Dimension(200,100));
			tunablePanel.add(listScroller, fieldLocation);
		} else {
			tunablePanel.add(inputField, fieldLocation);
		}
		return tunablePanel;
	}

	/**
	 * This method is used by getPanel to construct a JComboBox that
	 * contains a list of node or edge attributes that the user can
	 * choose from for doing attribute-dependent layouts.
	 *
	 * @param attributes CyAttributes of the appropriate (edge or node) type
	 * @return a JComponent with an entry for each attribute
	 */
	private JComponent getAttributePanel(CyAttributes attributes) {
		final String[] attList = attributes.getAttributeNames();
		final List<String> list = new ArrayList<String>();

		// See if we have any initial attributes (mapped into lowerBound)
		if (lowerBound != null) {
			list.addAll((List) lowerBound);
		}

		for (int i = 0; i < attList.length; i++) {
			// Is this attribute user visible?
			if (!attributes.getUserVisible(attList[i]))
				continue;

			byte type = attributes.getType(attList[i]);

			if (((flag & NUMERICATTRIBUTE) == 0)
			    || ((type == CyAttributes.TYPE_FLOATING) 
			    || (type == CyAttributes.TYPE_INTEGER))) {
				list.add(attList[i]);
			}
		}

		attributeList = list;

		if ((flag & MULTISELECT) != 0) {
			// Set our current value as selected
			JList jList = new JList(list.toArray());
			int [] indices = getSelectedValues(attributeList, decodeArray((String)value));
			if (indices != null && indices.length > 0)
				jList.setSelectedIndices(indices);
			return jList;
		} else {
			// Set our current value as selected
			JComboBox box = new JComboBox(attributeList.toArray());
			box.setSelectedItem((String) value);
			box.addActionListener(this);
			return box;
		}
	}

	/**
	 * This method is used by getPanel to construct a JComboBox that
	 * contains a list of values the user can choose from.
	 *
	 * @param list Array of Objects containing the list
	 * @return a JComboBox with an entry for each item on the list
	 */
	private JComponent getListPanel(Object[] list) {
		if ((flag & MULTISELECT) != 0) {
			JList jList =  new JList(list);
			if (value != null && ((String)value).length() > 0) {
				jList.setSelectedIndices(decodeIntegerArray((String)value));
			}
			return jList;
		} else {
			// Set our current value as selected
			JComboBox box = new JComboBox(list);
			box.setSelectedIndex(((Integer) value).intValue());
			box.addActionListener(this);
			return box;
		}
	}

	/**
 	 * Return an array of indices suitable for selection. The passed
 	 * String value is an encoded list of entries of the form [attr1,attr2,...]
 	 *
 	 * @param attrs the list of attributes to choose from
 	 * @param values the list of values
 	 * @return array of integers to use to select values
 	 */
	private int[] getSelectedValues(List<String>attrs, String[] values) {
		if (values == null) return null;
		int[] selVals = new int[values.length];
		for (int i = 0; i < values.length;  i++) {
			selVals[i] = attrs.indexOf(values[i]);
		}
		return selVals;
	}

	private int[] decodeIntegerArray(String value) {
		if(value == null || value.length() == 0) {
			return null;
		}
		String[] valArray = value.split(",");
		int[] intArray = new int[valArray.length];
		for (int i = 0; i < valArray.length; i++) {
			intArray[i] = Integer.valueOf(valArray[i]).intValue();
		}
		return intArray;
	}

	private String[] decodeArray(String value) {
		if(value == null || value.length() == 0) {
			return null;
		}
		return value.split(",");
	}

	/**
	 * This method is called to extract the user-entered data from the
	 * JPanel and store it as our value.
	 */
	public void updateValue() {
		Object newValue;

		if (inputField == null || type == GROUP || type == BUTTON)
			return;

		if (type == DOUBLE) {
			if (usingSlider) {
				newValue = new Double(((JSlider) inputField).getValue());
			} else {
				newValue = new Double(((JTextField) inputField).getText());
			}
		} else if (type == INTEGER) {
			if (usingSlider) {
				newValue = new Integer(((JSlider) inputField).getValue());
			} else {
				newValue = new Integer(((JTextField) inputField).getText());
			}
		} else if (type == BOOLEAN) {
			newValue = new Boolean(((JCheckBox) inputField).isSelected());
		} else if (type == LIST) {
			if ((flag & MULTISELECT) != 0) {
				int [] selVals = ((JList) inputField).getSelectedIndices();
				String newString = "";
				for (int i = 0; i < selVals.length; i++) {
					newString += Integer.toString(selVals[i]);
					if (i < selVals.length-1) newString+= ",";
				}
				newValue = (Object) newString;
			} else {
				newValue = new Integer(((JComboBox) inputField).getSelectedIndex());
			}
		} else if ((type == NODEATTRIBUTE) || (type == EDGEATTRIBUTE)) {
			if ((flag & MULTISELECT) != 0) {
				Object [] selVals = ((JList) inputField).getSelectedValues();
				String newString = "";
				for (int i = 0; i < selVals.length; i++) {
					newString += selVals[i];
					if (i < selVals.length) newString+= ",";
				}
				newValue = (Object) newString;
			} else {
				newValue = (String) ((JComboBox) inputField).getSelectedItem();
			}
		} else {
			newValue = ((JTextField) inputField).getText();
		}

		if (!value.equals(newValue)) {
			valueChanged = true;
		}

		value = newValue;
	}

	/**
 	 * Document listener routines to handle bounds checking
 	 */
	public void focusLost(FocusEvent ev) {
		Object value = null;
		// Check the bounds
		if (type == DOUBLE) {
			Double newValue = null;
			try {
				newValue = new Double(((JTextField) inputField).getText());
			} catch (NumberFormatException e) {
				displayBoundsError("a floating point");
				return;
			}
			if ((upperBound != null && newValue > (Double)upperBound) ||
			   (lowerBound != null && newValue < (Double)lowerBound)) {
				displayBoundsError("a floating point");
				return;
			}
			value = (Object)newValue;
		} else if (type == INTEGER) {
			Integer newValue = null;
			try {
				newValue = new Integer(((JTextField) inputField).getText());
			} catch (NumberFormatException e) {
				displayBoundsError("an integer");
				return;
			}
			if ((upperBound != null && newValue > (Integer)upperBound) ||
			   (lowerBound != null && newValue < (Integer)lowerBound)) {
				displayBoundsError("an integer");
				return;
			}
			value = (Object)newValue;
		}

		if ((flag & USESLIDER) != 0) {
			// Update the slider with this new value
			slider.setValue(sliderScale(value));
		} else {
			updateValueListeners();
		}
	}

	public void focusGained(FocusEvent ev) {
		// Save the current value
		savedValue = ((JTextField) inputField).getText();
	}

	public void stateChanged(ChangeEvent e) {
		if (((type == DOUBLE) || (type == INTEGER)) && ((flag & USESLIDER) != 0)) {
			// Get the widget
			JSlider slider = (JSlider) e.getSource();

			// Get the value
			int value = slider.getValue();

			// Update the text box
			((JTextField) inputField).setText(sliderScale(value).toString());
		} else {
			updateValueListeners();
		}
	}

	public void actionPerformed(ActionEvent e) {
		updateValueListeners();
	}

	public void itemStateChanged(ItemEvent e) {
		updateValueListeners();
	}

	private void displayBoundsError(String typeString) {
		if (lowerBound != null && upperBound != null) {
			JOptionPane.showMessageDialog(null,  "Value must be "+typeString+" between "+lowerBound+" and "+upperBound,
				"Bounds Error", JOptionPane.ERROR_MESSAGE);
		} else if (lowerBound != null) {
			JOptionPane.showMessageDialog(null, "Value must be "+typeString+" greater than "+lowerBound,
				"Bounds Error", JOptionPane.ERROR_MESSAGE);
		} else if (upperBound != null) {
			JOptionPane.showMessageDialog(null, "Value must be "+typeString+" less than "+upperBound,
				"Bounds Error", JOptionPane.ERROR_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, "Value must be "+typeString+" number",
				"Type Error", JOptionPane.ERROR_MESSAGE);
		}
		((JTextField) inputField).setText(savedValue);
	}

	private int getIntValue(Object v) {
		if (type == DOUBLE) {
			Double d = (Double)v;
			return d.intValue();
		} else if (type == INTEGER) {
			Integer d = (Integer)v;
			return d.intValue();
		}
		return 0;
	}

	private int sliderScale(Object value) {
		if (type == INTEGER) {
			// Don't mess with Integer values
			return ((Integer)value).intValue();
		}
		double minimum = ((Double)lowerBound).doubleValue();
		double maximum = ((Double)upperBound).doubleValue();
		double input = ((Double)value).doubleValue();
		double extent = maximum-minimum;

		// Use a scale from 0-100 with 0 = minimum and 100 = maximum
		return (int)(((input-minimum)/extent)*100.0);
	}

	private Object sliderScale(int value) {
		if (type == INTEGER) {
			// Don't mess with Integer values
			return Integer.valueOf(value);
		}
		double minimum = ((Double)lowerBound).doubleValue();
		double maximum = ((Double)upperBound).doubleValue();
		double extent = maximum-minimum;
		double dvalue = (double)value/100.0;
		double scaledValue = (dvalue*extent)+minimum;
		int places = 2;
		if (extent < 1.0)
			places = (int)Math.round(-Math.log10(extent)) + 1;
		return new Double(round(scaledValue, places));

	}

	private Hashtable createLabels(JSlider slider) {
		if (type == INTEGER) {
			return slider.createStandardLabels((getIntValue(upperBound)-getIntValue(lowerBound))/5);
		}
		Hashtable<Integer,JComponent>table = new Hashtable();
		// Create our table in 5 steps from lowerBound to upperBound
		// This could obviously be much fancier, but it's probably sufficient for now.
		for (int label = 0; label < 6; label++) {
			Double v = (Double)sliderScale(label*20);
			table.put(label*20, new JLabel(v.toString()));
		}
		return table;
	}

	private double round(double val, int places) {
		long factor = (long)Math.pow(10, places);
		val = val * factor;

		long tmp = Math.round(val);

		return (double)tmp / factor;
	}
}
