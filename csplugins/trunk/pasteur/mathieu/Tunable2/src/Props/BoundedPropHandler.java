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
	Double doub;
	Integer inte;
	
	@SuppressWarnings("unchecked")
	public BoundedPropHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t=t;
		try{
			if(t.type() == Double.class || t.type() == double.class) this.doub = (Double) f.get(o);
			else if(t.type() == Integer.class || t.type() == int.class) this.inte = (Integer)f.get(o);
			else this.boundedobject=(Bounded) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		propKey = f.getName();
	}

	
	public Properties getProps() {
		Properties p = new Properties();
		if(t.type() == Double.class || t.type() == double.class || t.type() == Integer.class || t.type() == int.class){
			try {
				p.put(propKey,f.get(o).toString());
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();
				}
		}
		else p.put(propKey, boundedobject.getValue());
		return p;
	}

	
	public void add(Properties p) {
		if(t.type() == Double.class || t.type() == double.class || t.type() == Integer.class || t.type() == int.class){
			try{
				p.put(propKey,f.get(o));
			}catch(Exception e){e.printStackTrace();}
		}
		else{
			boundedobject.setValue(""); //Need to initialize the value
			try{
				p.put(propKey,boundedobject.getValue());
			}catch(Exception e){e.printStackTrace();}
		}
	}
	
	
	public void setProps(Properties p) {
		
		if(t.type() == Double.class || t.type() == double.class){
			try {
				if (p.containsKey(propKey)) {
					String val = p.get(propKey).toString();
					if (val != null)
						f.set(o, Double.valueOf(val));
				}
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();
				}
		}
		else if(t.type() == Integer.class || t.type() == int.class){
			try {
				if (p.containsKey(propKey)) {
					String val = p.get(propKey).toString();
					if (val != null)
						f.set(o, Integer.valueOf(val));
				}
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();
				}
		}
		else{
			try {
				if (p.containsKey(propKey)) {
					String val = p.get(propKey).toString();
					boundedobject.setValue(val);
					if (val != null)
						f.set(o, boundedobject);
				}
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();
				}
		}
	}
}
