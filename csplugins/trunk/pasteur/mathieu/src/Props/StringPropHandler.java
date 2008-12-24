package Props;


import Tunable.*;
import java.lang.reflect.*;
import java.util.*;


public class StringPropHandler implements PropHandler {
	Field f;
	Object o;
	String propKey;

	public StringPropHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		propKey = f.getName();
	}


	public Properties getProps() {
		Properties p = new Properties();
		try {
			p.put(propKey,f.get(o));
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		}

		return p;
	}

	public void setProps(Properties p) {
		try {
			if (p.containsKey(propKey)) {
				String val = p.getProperty(propKey);
				if (val != null)
					f.set(o, val);
			}
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		}
	}

	public void add(Properties p) {
		try{
			p.put(propKey,f.get(o));
		}catch(Exception e){e.printStackTrace();}
	}
}
