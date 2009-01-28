
package Factory;

import java.lang.reflect.*;
import javax.swing.*;
import GuiInterception.AbstractGuiHandler;
import Tunable.*;

public class BooleanHandler extends AbstractGuiHandler {

	JCheckBox jcb;
	Boolean myBoolean;
	
	public BooleanHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	
		try{
			this.myBoolean = (Boolean) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		
		panel = new JPanel();
		try {
		jcb = new JCheckBox( t.description(), myBoolean.booleanValue());
		jcb.addActionListener( this );
		panel.add( jcb );
		} catch (Exception e) { e.printStackTrace(); }
	}

	public void handle() {
		try {
		f.set(o,jcb.isSelected());
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	
	public String getState() {
		String s;
		try {
			s = f.get(o).toString();
		//s = Boolean.toString(f.getBoolean(o));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			s = "";
		}
		return s;
	}
}
