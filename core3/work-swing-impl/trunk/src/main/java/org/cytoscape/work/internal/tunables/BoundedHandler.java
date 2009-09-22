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
@SuppressWarnings("unchecked")
public class BoundedHandler<T extends AbstractBounded> extends AbstractGuiHandler {


	/**
	 * <code>Bounded</code> object that need to be put in this type of <code>Guihandler</code>
	 */
	private T bounded;
	
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
	 * Construction of the <code>Guihandler</code> for the <code>Bounded</code> type
	 * 
	 * If <code>useslider</code> is set to <code>true</code> : displays the bounded object in a <code>JSlider</code> by using its bounds
	 * else diplays it in a <code>JTextField</code> with informations about the bounds
	 * 
	 * The Swing representation is then added to the <code>JPanel</code> for GUI representation
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
		for ( Param s : t.flag())if(s.equals(Param.slider))useslider = true;		
		panel = new JPanel(new BorderLayout());
		
		try{
			if(useslider){
				JLabel label = new JLabel(title);
				label.setFont(new Font(null, Font.PLAIN,12));
				panel.add(label,BorderLayout.WEST);
				slider = new mySlider(title,(Number) bounded.getLowerBound(),(Number)bounded.getUpperBound(),(Number)bounded.getValue(),bounded.isLowerBoundStrict(),bounded.isUpperBoundStrict());
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
	 * To set the value (from the JSlider or the JTextField) to the <code>Bounded</code> object
	 * 
	 * The constraints of the bound values have to be respected : <code>lowerBound &lt; value &lt; upperBound</code> or <code>lowerBound &lti; value &lti; upperBound</code> ....
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
    

	/**
	 * To reset the current value of this <code>BoundedHandler</code>, and set it to the initial one
	 */
	@Override
	public void resetValue() {
		// TODO Auto-generated method stub
		
	}
}
