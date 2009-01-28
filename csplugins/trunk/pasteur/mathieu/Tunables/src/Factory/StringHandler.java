
package Factory;

import java.lang.reflect.*;
import javax.swing.*;
import GuiInterception.AbstractGuiHandler;
import Tunable.*;

public class StringHandler extends AbstractGuiHandler {

	JTextField jtf;

	public StringHandler(Field f, Object o, Tunable t) {
		super(f,o,t);

		panel = new JPanel();
		try {
			panel.add( new JLabel( t.description() ) );
			jtf = new JTextField( (String)f.get(o), 20);
			jtf.setHorizontalAlignment(JTextField.RIGHT);
			jtf.addActionListener( this );
			panel.add( jtf );
		} catch (Exception e) { e.printStackTrace(); }
			
	}

	public void handle() {
		String s = jtf.getText();
		try {
		if ( s != null )
			f.set(o,s);
		} catch (Exception e) { e.printStackTrace(); }
	}

	public String getState() {
		String s;
		try {
			s = (String)f.get(o);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			s = "";
		}
		return s;
	}
}
