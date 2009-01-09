package Props;

import Tunable.*;
import java.lang.reflect.*;
import java.util.*;


public class BooleanPropHandler implements PropHandler {
	Field f;
	Object o;
	String propKey;
	Boolean bool;
	
	public BooleanPropHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		propKey = f.getName();
		try{
			bool=(Boolean) f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}

	
	public Properties getProps() {
		Properties p = new Properties();
		try {
			p.put(propKey,f.get(o).toString());
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		}
		return p;
	}

	
	public void add(Properties p) {
			p.put(propKey,bool);
	}
	
	
	public void setProps(Properties p) {
		try {
			if (p.containsKey(propKey)) {
				String val = p.get(propKey).toString();
				if (val != null)
					f.set(o, Boolean.valueOf(Boolean.parseBoolean(val)));
			}
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
			}
	}

}
