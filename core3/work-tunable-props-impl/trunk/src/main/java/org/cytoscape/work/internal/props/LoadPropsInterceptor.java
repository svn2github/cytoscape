package org.cytoscape.work.internal.props;

import java.util.*;

import org.cytoscape.work.spring.SpringTunableInterceptor;


public class LoadPropsInterceptor extends SpringTunableInterceptor<PropHandler> {
	private Properties inputProps;

	public LoadPropsInterceptor(final Properties inputProps){
		super(new PropHandlerFactory());
		this.inputProps = inputProps;
	}

	public boolean createUI(Object... pobjs) {
		Object[] objs = convertSpringProxyObjs(pobjs);

		java.util.List<PropHandler> lh = new ArrayList<PropHandler>();
		for ( Object o : objs ) {
			if ( !handlerMap.containsKey( o ) )
				throw new IllegalArgumentException("No Tunables exist for Object yet!");
			
			lh.addAll( handlerMap.get(o).values() );
		}
		for (PropHandler p : lh) {
			p.add(inputProps);
		}
		return true;
	}
	
	public void handle(){}
	public void setParent(Object o) {};
}
