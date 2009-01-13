package Props;

import Tunable.Tunable;
import Utils.myButton;
import java.lang.reflect.Field;
import java.util.Properties;


public class ButtonPropHandler implements PropHandler {
	Field f;
	Object o;
	String propKey;
	myButton button;
	
	public ButtonPropHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		propKey = f.getName();
		try{
			button=(myButton) f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}

	
	public Properties getProps() {
		Properties p = new Properties();
		p.put(propKey,button.getselected());
		return p;
	}

	
	public void add(Properties p) {
			p.put(propKey,button.isSelected());
	}
	
	
	public void setProps(Properties p) {
		try {
			if (p.containsKey(propKey)) {
				String val = p.get(propKey).toString();
				button.setselected(Boolean.parseBoolean(val));
				if (val != null)
					f.set(o, button);
			}
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
			}
	}

}
