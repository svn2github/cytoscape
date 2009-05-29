package org.example.tunable.internal.props;

import java.util.Collection;
import java.util.Properties;

import org.example.tunable.*;

/**
 * This would presumably be service. 
 */
public class LoadPropsInterceptor extends AbstractTunableInterceptor<PropHandler> {

	private Properties inputProps;

	public LoadPropsInterceptor(final Properties inputProps) {
		super( new PropHandlerFactory() );
		this.inputProps = inputProps; 
	}

	public void createUI(Object ... objs) {

		for ( Object o : objs ) {
			if ( !handlerMap.containsKey(o) )
				throw new IllegalArgumentException("Interceptor does not yet know about this object");

			Collection<PropHandler> lh = handlerMap.get(o).values();
			
			for ( PropHandler p : lh ) {
				p.setProps( inputProps );
			}
		}
	}
}
