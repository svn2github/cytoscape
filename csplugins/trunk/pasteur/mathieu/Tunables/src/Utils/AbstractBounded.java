package Utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * A bounded number object.
 *
 * @param <N>  DOCUMENT ME!
 */
public class AbstractBounded<N extends Comparable<N>> extends JTextField {

	protected N value;

	protected N initValue;
	final protected N lower;
	final protected N upper;
	final protected boolean upperStrict;
	final protected boolean lowerStrict;
	
	Number val;
    String newline=System.getProperty("line.separator");
    
	/**
	 * Creates a new Bounded object.
	 *
	 * @param lower  DOCUMENT ME!
	 * @param upper  DOCUMENT ME!
	 * @param lowerStrict  DOCUMENT ME!
	 * @param upperStrict  DOCUMENT ME!
	 */
	AbstractBounded(final N lower, final N initValue, final N upper, boolean lowerStrict, boolean upperStrict) {
		super(initValue.toString(),11);
		if (lower == null)
			throw new NullPointerException("lower bound is null!");

		if (upper == null)
			throw new NullPointerException("upper bound is null!");

		if (lower.compareTo(upper) >= 0)
			throw new IllegalArgumentException("lower value is greater than or equal to upper value");
		this.lower = lower;
		this.upper = upper;
		this.lowerStrict = lowerStrict;
		this.upperStrict = upperStrict;
		this.initValue = initValue;
		setValue(initValue);


		setHorizontalAlignment(JTextField.RIGHT);
		addActionListener(new myActionListener());
	}


	public class myActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
				try{
					if(initValue.getClass().equals(Integer.class))  val = Integer.parseInt(getText());
					else if(initValue.getClass().equals(Double.class)) val = Double.parseDouble(getText());
					else if(initValue.getClass().equals(Float.class)) val = Float.parseFloat(getText());
					else if(initValue.getClass().equals(Long.class)) val = Long.parseLong(getText());
					setValue((N)val);
					setText(value.toString());
					setBackground(Color.white);
				}catch(NumberFormatException nfe){
					setBackground(Color.red);
					JOptionPane.showMessageDialog(null, "An "+initValue.getClass().getSimpleName() +" is Expected" , "Error",JOptionPane.ERROR_MESSAGE);
					setText(initValue.toString());
				}
		}	
	}
	
	
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public N getUpperBound() {
		return upper;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public N getLowerBound() {
		return lower;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isUpperBoundStrict() {
		return upperStrict;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isLowerBoundStrict() {
		return lowerStrict;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public N getValue() {
		return value;
	}

	public void updateValue(){
		try{
			if(initValue.getClass().equals(Integer.class))  val = Integer.parseInt(getText());
			else if(initValue.getClass().equals(Double.class)) val = Double.parseDouble(getText());
			else if(initValue.getClass().equals(Float.class)) val = Float.parseFloat(getText());
			else if(initValue.getClass().equals(Long.class)) val = Long.parseLong(getText());
			setValue((N)val);
		}catch(NumberFormatException nfe){
			setBackground(Color.red);
			JOptionPane.showMessageDialog(null, "An "+initValue.getClass().getSimpleName() +" is Expected" , "Error",JOptionPane.ERROR_MESSAGE);
			setText(initValue.toString());
		}

		//setBackground(Color.white);
	}
	
	
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @param v DOCUMENT ME!
	 */
	public void setValue(N v) {
		
		if (v == null){
			JOptionPane.showMessageDialog(null, "Value is missing", "Alert",JOptionPane.ERROR_MESSAGE);
			//throw new NullPointerException("value is null!");
			value = initValue;
		}

		int up = v.compareTo(upper);

		value = v;
		
		if (upperStrict) {
			if (up > 0){
				setBackground(Color.red);
				JOptionPane.showMessageDialog(null, value+" is much than upper value ("+upper+")"+newline+"Value will be set to default = "+initValue, "Error",JOptionPane.ERROR_MESSAGE);
				value = initValue;
			}
			if(up == 0){
				setBackground(Color.red);
				JOptionPane.showMessageDialog(null, value+" can not be equal to upper value ("+upper+")"+newline+"Value will be set to default = "+initValue, "Error",JOptionPane.ERROR_MESSAGE);
				value = initValue;
				
			}
		} else {
			if (up > 0){
				setBackground(Color.red);
				JOptionPane.showMessageDialog(null, value+" is much than upper value ("+upper+")"+newline+"Value will be set to default = "+initValue, "Error",JOptionPane.ERROR_MESSAGE);
				value = initValue;
			}
		}

		int low = v.compareTo(lower);

		if (lowerStrict) {
			if (low < 0){	
				JOptionPane.showMessageDialog(null, value+" is less than lower value ("+lower+")"+newline+"Value will be set to default = "+initValue, "Error",JOptionPane.ERROR_MESSAGE);
				value = initValue;
			}
			if (low == 0){	
				JOptionPane.showMessageDialog(null, value+" can not be equal to lower value ("+lower+")"+newline+"Value will be set to default = "+initValue, "Error",JOptionPane.ERROR_MESSAGE);
				///////////throw new IllegalArgumentException("value is less than or equal to lower limit");
				//value = lower;
				value = initValue;
			}
		} else {
			if (low < 0){
				JOptionPane.showMessageDialog(null, value+" is less than lower value ("+lower+")"+newline+"Value will be set to default = "+initValue, "Error",JOptionPane.ERROR_MESSAGE);
				//////////throw new IllegalArgumentException("value is less than lower limit");
				//value = lower;
				value = initValue;
			}
		}		
	}
}
