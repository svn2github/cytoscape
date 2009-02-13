package org.cytoscape.work.internal.props;

import java.lang.reflect.Field;
import java.util.Properties;

import org.cytoscape.work.AbstractPropHandler;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.BoundedDouble;



public class BoundedDoublePropHandler extends AbstractPropHandler {
	
	BoundedDouble bounded;
	
	public BoundedDoublePropHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			this.bounded = (BoundedDouble)f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}

	
	public Properties getProps() {
		Properties p = new Properties();
		p.put(propKey, bounded.getValue());
		return p;
	}

	
	public void add(Properties p) {
		bounded.setValue(bounded.getValue().doubleValue()); //Need to initialize the value
		try{
			p.put(propKey,bounded.getValue().toString());
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	public void setProps(Properties p) {
		try {
			if (p.containsKey(propKey)) {
				String val = p.get(propKey).toString();
				bounded.setValue(Double.parseDouble(val));
				if (val != null)
					f.set(o, bounded);
			}
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
			}
	}
}
