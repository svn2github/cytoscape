package org.cytoscape.work.internal.props;

import java.lang.reflect.Field;
import java.util.Properties;

import org.cytoscape.work.util.BoundedLong;
import org.cytoscape.work.Tunable;


public class BoundedLongPropHandler extends AbstractPropHandler {

	BoundedLong bounded;
	
	public BoundedLongPropHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			this.bounded = (BoundedLong)f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public Properties getProps() {
		Properties p = new Properties();
		p.put(propKey, bounded.getValue());
		return p;
	}

	
	public void add(Properties p) {
		bounded.setValue(bounded.getValue().longValue()); //Need to initialize the value
		try{
			p.put(propKey,bounded.getValue().toString());
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	public void setProps(Properties p) {
		try {
			if (p.containsKey(propKey)) {
				String val = p.get(propKey).toString();
				bounded.setValue(Long.parseLong(val));
				if (val != null)
					f.set(o, bounded);
			}
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
			}
	}
}
