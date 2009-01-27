package Utils;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class AbstractBounded<N extends Comparable<N>> extends JTextField {

	protected N value;

	protected N initValue;
	final protected N lower;
	final protected N upper;
	final protected boolean upperStrict;
	final protected boolean lowerStrict;
	
	Number val;
    String newline = System.getProperty("line.separator");
    
    
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
		@SuppressWarnings("unchecked")
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
					JOptionPane.showMessageDialog(null, "An "+initValue.getClass().getSimpleName() +" is Expected"+newline+"Value will be set to default = "+initValue , "Error",JOptionPane.ERROR_MESSAGE);
					setText(initValue.toString());
				}
		}	
	}
	

	public N getUpperBound() {
		return upper;
	}

	public N getLowerBound() {
		return lower;
	}

	public boolean isUpperBoundStrict() {
		return upperStrict;
	}

	public boolean isLowerBoundStrict() {
		return lowerStrict;
	}


	public N getValue() {
		return value;
	}

	@SuppressWarnings("unchecked")
	public void updateValue(){
		try{
			if(initValue.getClass().equals(Integer.class))  val = Integer.parseInt(getText());
			else if(initValue.getClass().equals(Double.class)) val = Double.parseDouble(getText());
			else if(initValue.getClass().equals(Float.class)) val = Float.parseFloat(getText());
			else if(initValue.getClass().equals(Long.class)) val = Long.parseLong(getText());
			setValue((N)val);			
		}catch(NumberFormatException nfe){
			setBackground(Color.red);
			JOptionPane.showMessageDialog(null, "An "+initValue.getClass().getSimpleName() +" is Expected" +newline+"Value will be set to default = "+initValue, "Error",JOptionPane.ERROR_MESSAGE);
			setText(initValue.toString());
		}
		//setBackground(Color.white);
	}
	
	

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
				setBackground(Color.red);
				JOptionPane.showMessageDialog(null, value+" is less than lower value ("+lower+")"+newline+"Value will be set to default = "+initValue, "Error",JOptionPane.ERROR_MESSAGE);
				value = initValue;
			}
			if (low == 0){
				setBackground(Color.red);
				JOptionPane.showMessageDialog(null, value+" can not be equal to lower value ("+lower+")"+newline+"Value will be set to default = "+initValue, "Error",JOptionPane.ERROR_MESSAGE);
				value = initValue;
			}
		} else {
			if (low < 0){
				setBackground(Color.red);
				JOptionPane.showMessageDialog(null, value+" is less than lower value ("+lower+")"+newline+"Value will be set to default = "+initValue, "Error",JOptionPane.ERROR_MESSAGE);
				value = initValue;
			}
		}		
	}
}
