package Props;

import java.lang.reflect.Field;
import java.util.Properties;

import Tunable.Tunable;

public class IntPropHandler extends AbstractPropHandler{
	
	public IntPropHandler(Field f, Object o, Tunable t){
		super(f,o,t);
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
					f.set(o, Integer.valueOf(val));
			}
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		}
	}
}
