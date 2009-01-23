package org.example.tunable.internal.props;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
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

	protected void process(java.util.List<PropHandler> lh) {
		for ( PropHandler p : lh ) {
			p.setProps( inputProps );
		}
	}
}
