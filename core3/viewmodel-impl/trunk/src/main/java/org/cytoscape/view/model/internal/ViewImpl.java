package org.cytoscape.view.model.internal;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.ViewChangeListener;
import org.cytoscape.view.model.VisualProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is an implementation of row-oriented ViewModel.
 * 
 * @author kono
 *
 * @param <M>
 */
public class ViewImpl<M> implements View<M> {

	private static final Logger logger = LoggerFactory.getLogger(ViewImpl.class);
	
	protected final M model;
	protected final long suid;
	
	protected final CyEventHelper cyEventHelper;
	
	//TODO: Thread safety?
	private final Map<VisualProperty<?>, Object> visualProperties;
	private final Map<VisualProperty<?>, Object> visualPropertyLocks;
	
	
	public ViewImpl(final M model, final CyEventHelper cyEventHelper) {
		if(model == null)
			throw new IllegalArgumentException("Data model cannot be null.");
		if(cyEventHelper == null)
			throw new IllegalArgumentException("CyEventHelper is null.");
		
		this.suid = SUIDFactory.getNextSUID();
		this.model = model;
		this.cyEventHelper = cyEventHelper;
		
		this.visualProperties = new HashMap<VisualProperty<?>, Object>();
		this.visualPropertyLocks = new HashMap<VisualProperty<?>, Object>();
		
		logger.info("Graph Object View Created.  SUID = " + suid);
	}

	
	@Override
	public M getModel() {
		return model;
	}

	
	@Override
	public long getSUID() {
		return suid;
	}
	

	@Override
	public <T, V extends T> void setVisualProperty(
			VisualProperty<? extends T> vp, V value) {
		
		if(value == null)
			this.visualProperties.remove(vp);
		else
			this.visualProperties.put(vp, value);
		
		final ViewChangeListener vcl = cyEventHelper.getMicroListener(ViewChangeListener.class, this);
		if(vcl != null)
			vcl.visualPropertySet(vp, value);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getVisualProperty(VisualProperty<T> vp) {
		
		if(visualPropertyLocks.get(vp) == null) {
			if(visualProperties.get(vp) == null)
				return vp.getDefault();
			else
				return (T) visualProperties.get(vp);
			
		} else
			return (T) this.visualPropertyLocks.get(vp);
	}

	
	// TODO: should I fire event?
	@Override
	public <T, V extends T> void setLockedValue(VisualProperty<? extends T> vp,
			V value) {
		this.visualPropertyLocks.put(vp, value);
	}
	

	@Override
	public boolean isValueLocked(VisualProperty<?> vp) {
		if(visualPropertyLocks.get(vp) == null)
			return false;
		else 
			return true;
	}

	@Override
	public void clearValueLock(VisualProperty<?> vp) {
		this.visualPropertyLocks.remove(vp);
	}

}
