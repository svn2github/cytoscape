package org.cytoscape.work.internal.props;

import java.util.*;

import org.cytoscape.work.spring.SpringTunableInterceptor;



public class StorePropsInterceptor extends SpringTunableInterceptor<PropHandler> {
	private Properties inputProps;

	public StorePropsInterceptor(Properties inputProps) {
		super(new PropHandlerFactory());
		this.inputProps = inputProps;
	}

	public boolean createUI(Object... pobjs) {
		Object[] objs = convertSpringProxyObjs(pobjs);
		for ( Object o : objs ) {
			if ( !handlerMap.containsKey( o ) )
				throw new IllegalArgumentException("No Tunables exist for Object yet!");

			Collection<PropHandler> lh = handlerMap.get(o).values();
			
			for ( PropHandler p : lh ) {
				inputProps.putAll( p.getProps() );
			}
//			lh.addAll( handlerMap.get(o).values() );
		}
//		for (PropHandler p : lh) {
//			inputProps.putAll(p.getProps());
//		}
		return true;
	}
	
	public void handle(){};
	public void setParent(Object o){};
}
