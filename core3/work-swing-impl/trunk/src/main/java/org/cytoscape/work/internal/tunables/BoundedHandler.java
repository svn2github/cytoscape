package org.cytoscape.work.internal.tunables;

import java.awt.BorderLayout;
import java.awt.Font;
import java.lang.reflect.Field;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;
import org.cytoscape.work.internal.tunables.utils.myBoundedSwing;
import org.cytoscape.work.internal.tunables.utils.mySlider;
import org.cytoscape.work.util.AbstractBounded;


/**
 * Handler for the type <i>Bounded</i> of <code>Tunable</code>
 * 
 * @author pasteur
 *
 * @param <T> type of <code>AbstractBounded</code>
 */
public class BoundedHandler<T extends AbstractBounded> extends AbstractGuiHandler {


	/**
	 * <code>Bounded</code> object that need to be put in this type of <code>Guihandler</code>
	 */
	T bounded;
	
	/**
	 * description of the <code>Bounded</code> object that will be displayed in the JPanel of this <code>Guihandler</code>
	 */
	private String title;
	
	/**
	 * Representation of the <code>Bounded</code> in a <code>JSlider</code>
	 */
	private boolean useslider = false;
	
	/**
	 * 1st representation of this <code>Bounded</code> object in its <code>Guihandler</code>'s JPanel : a <code>JSlider</code>
	 */
	private mySlider slider;
	
	/**
	 * 2nd representation of this <code>Bounded</code> object : a <code>JTextField</code> that will display to the user all the informations about the bounds
	 */
	private myBoundedSwing boundedField;
	
	
	/**
	 * Constructs the <code>Guihandler</code> for the <code>Bounded</code> type
	 * 
	 * @param f field that has been annotated
	 * @param o object contained in <code>f</code>
	 * @param t tunable associated to <code>f</code>
	 */
	public BoundedHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
			this.bounded = (T)f.get(o);
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();	
		}

		this.title = t.description();
		for ( Param s : t.flag())if(s.equals(Param.slider))useslider=true;		
		panel = new JPanel(new BorderLayout());
		
		try{
			if(useslider){
				JLabel label = new JLabel(title);
				label.setFont(new Font(null, Font.PLAIN,12));
				panel.add(label,BorderLayout.WEST);
				slider = new mySlider(title,(Number) bounded.getLowerBound(),(Number)bounded.getUpperBound(),(Number)bounded.getValue(),bounded.isLowerBoundStrict(),bounded.isUpperBoundStrict());
				// Add ChangeListener????????
				slider.addChangeListener(this);
				panel.add(slider,BorderLayout.EAST);
			}
	
			else{
				JLabel label = new JLabel( title + " (max: " + bounded.getLowerBound().toString() + "  min: " + bounded.getUpperBound().toString() + ")" );
				label.setFont(new Font(null, Font.PLAIN,12));
				boundedField = new myBoundedSwing((Number)bounded.getValue(),(Number)bounded.getLowerBound(),(Number)bounded.getUpperBound(),bounded.isLowerBoundStrict(),bounded.isUpperBoundStrict());
				panel.add(label,BorderLayout.WEST);
				panel.add(boundedField,BorderLayout.EAST);
			}
		}catch (Exception e) { e.printStackTrace(); }
	}

	/**
	 * To set the current value represented in the <code>Guihandler</code> to the value of the <code>Bounded</code> object, with the constraints of the bound values that have to be respected : <code>lowerBound < value < upperBound</code>
	 */
	public void handle() {
		try{
			if(useslider==true){
				bounded.setValue(slider.getValue().doubleValue());
			}
			else{
				bounded.setValue(boundedField.getFieldValue().doubleValue());
			}
		}catch (Exception e){e.printStackTrace();}
	}
	
	/**
	 * To get the current value of the <code>Bounded</code> object
	 * @return the value of the <code>Bounded</code> object
	 */
    public String getState() {
        return bounded.getValue().toString();
    }

	@Override
	public void resetValue() {
		// TODO Auto-generated method stub
		
	}
}
