package org.example.tunable.internal.props;

import java.lang.reflect.Field;
import java.util.Properties;

import org.example.tunable.Tunable;
import org.example.tunable.util.AbstractFlexiblyBounded;


public class FlexiblyBoundedPropHandler<T extends AbstractFlexiblyBounded<?>> extends AbstractPropHandler {
	
	public FlexiblyBoundedPropHandler(Field f, Object o, Tunable t){
		super(f,o,t);
	}

	public Properties getProps() {
		Properties p = new Properties();
		try {
			T bo = (T)f.get(o);
			p.put(propKey, new String(bo.getLowerBound()+","+bo.getValue()+","+bo.getUpperBound()+","+bo.isLowerBoundStrict()+","+bo.isUpperBoundStrict()));
        }catch(IllegalAccessException iae) {iae.printStackTrace();}
		return p;
	}

	public void setProps(Properties p) {
		try{
			if(p.containsKey(propKey)){
				T bo = (T)f.get(o);
				String[] vals = p.getProperty(propKey).split(",");
				if(vals != null){
					bo.setLowerBound(vals[0]);
					bo.setValue(vals[1]);
					bo.setUpperBound(vals[2]);
					bo.setLowerBoundStrict(Boolean.getBoolean(vals[3]));
					bo.setUpperBoundStrict(Boolean.getBoolean(vals[4]));
				}
				if(bo != null) f.set(o, bo);
			}
		}catch(Exception e){e.printStackTrace();}
	}	
}