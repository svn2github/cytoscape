package Props;

import Tunable.Tunable;
import java.lang.reflect.Field;
import java.util.Properties;
import Utils.BoundedLong;


public class BoundedLongPropHandler implements PropHandler {
	Field f;
	Object o;
	Tunable t;
	String propKey;
	BoundedLong bounded;



	public BoundedLongPropHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t=t;
		try{
			this.bounded=(BoundedLong) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		propKey = f.getName();
	}

	
	public Properties getProps() {
		Properties p = new Properties();
		p.put(propKey, bounded.getValue());
		return p;
	}

	
	public void add(Properties p) {
		bounded.setValue(bounded.getValue().longValue()); //Need to initialize the value
		try{
			p.put(propKey,bounded.getValue());
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