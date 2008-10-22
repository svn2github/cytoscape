// vim: set ts=2: */
package org.cytoscape.tunable.impl;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.tunable.Tunable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Map;


/**
 * The Tunable class provides a convenient way to encapsulate
 * CyLayoutAlgorithm property and settings values.  Each Tunable
 * has a name, which corresponds to the property name, a description,
 * which is used as the label in the settings dialog, a type, a
 * value, and information about the value, such as a list of options
 * or the lower and upper bounds for the value.  These are meant
 * to be used as part of the LayoutSettingsDialog (see getPanel).
 */
public class TunableImpl implements Tunable, FocusListener, ChangeListener {
	private String name;
	private String desc;
	private int type = STRING;
	private int flag = 0;
	private Object value;
	private Object lowerBound;
	private Object upperBound;
	private JComponent inputField;
	private JSlider slider;
	private boolean valueChanged = true;
	private String savedValue = null;
	private boolean usingSlider = false;
	
	private boolean immutable = false;

	private CyDataTable attrs = null;


	/**
	 * Constructor to create a Tunable with bounds
	 * information as well as a flag.
	 *
	 * @param name The name of the Tunable
	 * @param desc The description of the Tunable
	 * @param type Integer value that represents the type of
	 *	     the Tunable.  The type not only impact the
	 *	     way that the value is interpreted, but also
	 *	     the component used for the LayoutSettingsDialog
	 * @param value The initial (default) value of the Tunable.  This
	 *	     is a String in the case of an EDGEATTRIBUTE or
	 *	     NODEATTRIBUTE tunable, it is an Integer index
	 *	     a LIST tunable.
	 * @param lowerBound An Object that either represents the lower
	 *	     bounds of a numeric Tunable or an array of values
	 *	     for an attribute (or other type of) list.
	 * @param upperBound An Object that represents the upper bounds
	 *	     of a numeric Tunable.
	 * @param flag The initial value of the flag.  This can be
	 *	     used to indicate that this tunable is not user
	 *	     changeable (e.g. debug), or to indicate if there
	 *	     is a specific type for the attributes.
	 */
	public TunableImpl(String name, String desc, int type, Object value, Object lowerBound,
	    Object upperBound, int flag, boolean immutable, CyDataTable attrs) {
		this.name = name;
		this.desc = desc;
		this.type = type;
		this.value = value;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
		this.flag = flag;
		this.immutable = immutable;
		this.attrs = attrs;
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
	 * This method is used to set the value for this Tunable.  If
	 * this is an INTEGER, DOUBLE, or BOOLEAN Tunable, then value
	 * is assumed to be a String.  This also sets the "changed" state
	 * of the value to "true".
	 *
	 * @param value Object (usually String) containing the value to be set
	 */
	public void setValue(Object value) {
		// Did the user hand us a string representation?
		if (value.getClass() == String.class) {
			switch (type) {
				case INTEGER:
					this.value = Integer.valueOf((String) value);
					break;

				case DOUBLE:
					this.value = new Double((String) value);
					break;

				case BOOLEAN:
					this.value = new Boolean((String) value);
					break;

				case LIST:
					if ((flag & MULTISELECT) != 0) {
						// Multiselect LIST -- value is a List of Integers, or String values
						this.value = value;
					} else {
						this.value = Integer.valueOf((String) value);
					}
					return;

				case GROUP:
					// System.out.println("Setting Group tunable "+desc+" value to "+value);
					this.value = Integer.valueOf((String) value);
					return;

				default:
					this.value = value;
					break;
			}
		} else {
			this.value = value;
		}

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

		JPanel tunablePanel = new JPanel(new BorderLayout(10, 2));
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
			  if (upperBound != null || lowerBound != null) {
			  	field.addFocusListener(this);
			  }
			  inputField = field;
			}
		} else if (type == BOOLEAN) {
			JCheckBox box = new JCheckBox();
			box.setSelected(((Boolean) value).booleanValue());
			inputField = box;
			tunablePanel.add(inputField, BorderLayout.LINE_END);
		} else if (type == NODEATTRIBUTE || type == EDGEATTRIBUTE) {
			inputField = getAttributePanel(attrs);
			tunablePanel.add(inputField, BorderLayout.LINE_END);
		} else if (type == LIST) {
			inputField = getListPanel((Object[]) lowerBound);
			tunablePanel.add(inputField, BorderLayout.LINE_END);
		} else if (type == STRING) {
			JTextField field = new JTextField(value.toString(), 20);
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
	 @SuppressWarnings("unchecked")  // TODO REVIEW THIS CODE!
	private JComponent getAttributePanel(CyDataTable attributes) {

		final List<String> list = new ArrayList<String>();

		// See if we have any initial attributes (mapped into lowerBound)
		if (lowerBound != null) {
			list.addAll((List) lowerBound);
		}

		if (attributes != null) {
			Map<String,Class<?>> typeMap = attributes.getColumnTypeMap();
			Set<String> keys = typeMap.keySet();

			for (String key : keys) {
				
				Class<?> type = typeMap.get(key); 

				if (((flag & NUMERICATTRIBUTE) == 0)
				    || ((type == Integer.class) 
				    || (type == Double.class))) {
					list.add(key);
				}
			}
		}

		if ((flag & MULTISELECT) != 0) {
			// Set our current value as selected
			JList jList = new JList(list.toArray());
			jList.setSelectedIndices(getSelectedValues(list, decodeArray((String)value)));
			return jList;
		} else {
			// Set our current value as selected
			JComboBox box = new JComboBox(list.toArray());
			box.setSelectedItem((String) value);
			return box;
		}
	}

	/**
	 * This method is used by getPanel to construct a JComboBox that
	 * contains a list of values the user can choose from.
	 *
	 * @param list Array of Objects containing the list
	 * @return a JComponent with an entry for each item on the list
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
			return box;
		}
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
				newValue = Integer.valueOf(((JSlider) inputField).getValue());
			} else {
				newValue = Integer.valueOf(((JTextField) inputField).getText());
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
				newValue = Integer.valueOf(((JComboBox) inputField).getSelectedIndex());
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
				newValue = Integer.valueOf(((JTextField) inputField).getText());
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
		} else {
			// Ooops -- shouldn't be here!
			return;
		}

		if ((flag & USESLIDER) != 0) {
			// Update the slider with this new value
			slider.setValue(sliderScale(value));
		}
	}

	public void focusGained(FocusEvent ev) {
		// Save the current value
		savedValue = ((JTextField) inputField).getText();
	}

	public void stateChanged(ChangeEvent e) {
		// Get the widget
		JSlider slider = (JSlider) e.getSource();

		// Get the value
		int value = slider.getValue();

		// Update the text box
		((JTextField) inputField).setText(sliderScale(value).toString());
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
		Hashtable<Integer,JComponent>table = new Hashtable<Integer,JComponent>();
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
}
