package Props;

import Tunable.*;
import Utils.Bounded;
import java.lang.reflect.*;
import java.util.*;


public class BoundedPropHandler implements PropHandler {
	Field f;
	Object o;
	Tunable t;
	String propKey;
	Bounded<String> boundedobject;

	
	@SuppressWarnings("unchecked")
	public BoundedPropHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t=t;
		try{
			this.boundedobject=(Bounded) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		propKey = f.getName();
	}

	
	public Properties getProps() {
		Properties p = new Properties();
		p.put(propKey, boundedobject.getValue());
		return p;
	}

	
	public void add(Properties p) {
		boundedobject.setValue("null",null); //Need to initialize the value
		try{
			p.put(propKey,boundedobject.getValue());
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	public void setProps(Properties p) {
		try {
			if (p.containsKey(propKey)) {
				String val = p.get(propKey).toString();
				boundedobject.setValue(val,null);
				if (val != null)
					f.set(o, boundedobject);
			}
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
			}
	}
}