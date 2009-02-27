package org.cytoscape.work.internal.props;

import java.util.*;

import org.cytoscape.work.AbstractTunableInterceptor;


public class StorePropsInterceptor extends AbstractTunableInterceptor<PropHandler> {
	private Properties inputProps;

	public StorePropsInterceptor(Properties inputProps) {
		super(new PropHandlerFactory<PropHandler>());
		this.inputProps = inputProps;
	}

	public int createUI(Object... objs) {
		java.util.List<PropHandler> lh = new ArrayList<PropHandler>();
		for ( Object o : objs ) {
			if ( !handlerMap.containsKey( o ) )
				throw new IllegalArgumentException("No Tunables exist for Object yet!");
			lh.addAll( handlerMap.get(o).values() );
		}
		for (PropHandler p : lh) {
			inputProps.putAll(p.getProps());
		}
		return 0;
	}

}
