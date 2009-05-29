package org.example.tunable.internal.props;

import java.lang.reflect.Field;
import java.util.Properties;

import org.example.tunable.Tunable;
import org.example.tunable.util.AbstractBounded;


public class BoundedPropHandler<T extends AbstractBounded<?>> extends AbstractPropHandler {
	
	public BoundedPropHandler(Field f, Object o, Tunable t){
		super(f,o,t);
	}

	public Properties getProps() {
		Properties p = new Properties();
		try {
			p.setProperty(propKey, ((T)f.get(o)).getValue().toString());
        }catch(IllegalAccessException iae) {iae.printStackTrace();}
		return p;
	}

	public void setProps(Properties p) {
		try{
			if(p.containsKey(propKey)){
				T bo = (T) f.get(o);
				String val = p.getProperty(propKey).toString();
				if(val != null) {
					bo.setValue(val);
					f.set(o, bo);
				}
			}
		}catch(Exception e){e.printStackTrace();}
	}	
}