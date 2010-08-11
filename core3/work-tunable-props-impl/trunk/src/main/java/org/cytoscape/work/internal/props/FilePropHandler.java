package org.cytoscape.work.internal.props;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;
import java.io.File;
import org.cytoscape.work.Tunable;


public class FilePropHandler extends AbstractPropHandler {
	public FilePropHandler(final Field field, final Object instance, final Tunable tunable) {
		super(field, instance, tunable);
	}

	public FilePropHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable) {
		super(getter, setter, instance, tunable);
	}

	public Properties getProps() {
		Properties p = new Properties();
		try{
			p.setProperty(propKey, ((File)getValue()).getPath().toString());
		} catch(final Exception e) {
			e.printStackTrace();
		}
		return p;
	}

	public void setProps(Properties p) {
		try {
			if (p.containsKey(propKey )) {
				String val = p.getProperty(propKey);
				if (val != null)
					setValue(new File(val.toString()));
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
