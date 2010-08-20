package org.cytoscape.work.internal.props;

import java.util.*;

import javax.swing.JPanel;

import org.cytoscape.work.spring.SpringTunableInterceptor;


public class LoadPropsInterceptor extends SpringTunableInterceptor<PropHandler> {
	private Properties inputProps;

	public LoadPropsInterceptor(final Properties inputProps){
		super(new PropHandlerFactory());
		this.inputProps = inputProps;
	}

	public JPanel getUI(Object... objs) {
		return null;
	}

	public boolean execUI(Object... pobjs) {
		Object[] objs = convertSpringProxyObjs(pobjs);
		for (final Object o : objs) {
			if ( !handlerMap.containsKey( o ) )
				throw new IllegalArgumentException("No Tunables exist for Object yet!");
			
			final Collection<PropHandler> handlers = handlerMap.get(o).values();
			
			for (final PropHandler p : handlers)
				p.setProps(inputProps);
		}
		return true;
	}
	
	public boolean handle(){ return false; }
	public void setParent(Object o) { }
}
