package Props;

import Tunable.Tunable;
import java.lang.reflect.Field;
import java.util.Properties;
import Utils.BoundedDouble;


public class BoundedDoublePropHandler implements PropHandler {
	Field f;
	Object o;
	Tunable t;
	String propKey;
	BoundedDouble bounded;

	
	public BoundedDoublePropHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t=t;
		try{
			this.bounded=(BoundedDouble) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		propKey = f.getName();
	}

	
	public Properties getProps() {
		Properties p = new Properties();
		p.put(propKey, bounded.getValue());
		return p;
	}

	
	public void add(Properties p) {
		bounded.setValue(bounded.getValue()); //Need to initialize the value
		try{
			p.put(propKey,bounded.getValue());
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