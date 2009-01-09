package Props;

import Tunable.*;
import java.lang.reflect.*;
import java.util.*;


public class LongPropHandler implements PropHandler {
	Field f;
	Object o;
	String propKey;

	
	public LongPropHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		propKey = f.getName();
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
		try{
			p.put(propKey,f.get(o));
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	public void setProps(Properties p) {
		try {
			if (p.containsKey(propKey)) {
				String val = p.get(propKey).toString();
				if (val != null)
					f.set(o, Long.valueOf(val));
			}
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		}
	}
}
