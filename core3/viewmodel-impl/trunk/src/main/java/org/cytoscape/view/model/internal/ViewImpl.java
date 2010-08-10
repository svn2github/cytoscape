package org.cytoscape.view.model.internal;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.model.SUIDFactory;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.ViewChangeListener;
import org.cytoscape.view.model.VisualProperty;


/**
 * This is an implementation of row-oriented ViewModel.
 * 
 * @author kono
 *
 * @param <M>
 */
public class ViewImpl<M> implements View<M> {

	private final M model;
	private final long suid;
	
	//TODO: Thread safety?
	private final Map<VisualProperty<?>, Object> visualPorperties;
	private final Map<VisualProperty<?>, Object> visualPorpertyLocks;
	
	
	public ViewImpl(final M model) {
		this.suid = SUIDFactory.getNextSUID();
		this.model = model;
		
		this.visualPorperties = new HashMap<VisualProperty<?>, Object>();
		this.visualPorpertyLocks = new HashMap<VisualProperty<?>, Object>();
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
		this.visualPorperties.put(vp, value);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getVisualProperty(VisualProperty<T> vp) {
		return (T) this.visualPorperties.get(vp);
	}

	
	@Override
	public <T, V extends T> void setLockedValue(VisualProperty<? extends T> vp,
			V value) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isValueLocked(VisualProperty<?> vp) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clearValueLock(VisualProperty<?> vp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addViewChangeListener(ViewChangeListener vcl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeViewChangeListener(ViewChangeListener vcl) {
		// TODO Auto-generated method stub

	}

}
