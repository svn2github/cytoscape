package org.cytoscape.view.model.internal;

import java.util.Collection;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;

/**
 * Null object for CyNetworkView.
 * 
 * Network readers may return this null object.
 * 
 */
public final class NullCyNetworkView implements CyNetworkView {

    private final long suid;
    private final CyNetwork model;

    public NullCyNetworkView(final CyNetwork model) {
	this.model = model;
	this.suid = SUIDFactory.getNextSUID();
    }

    @Override
    public CyNetwork getModel() {
	return model;
    }

    @Override
    public long getSUID() {
	return suid;
    }

    // All of the methods below are dummy.

    @Override
    public <T, V extends T> void setVisualProperty(VisualProperty<? extends T> vp, V value) {
	throw new UnsupportedOperationException("This is a null object and does not support this method.");
    }

    @Override
    public <T> T getVisualProperty(VisualProperty<T> vp) {
	throw new UnsupportedOperationException("This is a null object and does not support this method.");
    }

    @Override
    public <T, V extends T> void setLockedValue(VisualProperty<? extends T> vp, V value) {
	throw new UnsupportedOperationException("This is a null object and does not support this method.");
    }

    @Override
    public boolean isValueLocked(VisualProperty<?> vp) {
	throw new UnsupportedOperationException("This is a null object and does not support this method.");
    }

    @Override
    public void clearValueLock(VisualProperty<?> vp) {
	throw new UnsupportedOperationException("This is a null object and does not support this method.");
    }

    @Override
    public View<CyNode> getNodeView(CyNode node) {
	throw new UnsupportedOperationException("This is a null object and does not support this method.");
    }

    @Override
    public Collection<View<CyNode>> getNodeViews() {
	throw new UnsupportedOperationException("This is a null object and does not support this method.");
    }

    @Override
    public View<CyEdge> getEdgeView(CyEdge edge) {
	throw new UnsupportedOperationException("This is a null object and does not support this method.");
    }

    @Override
    public Collection<View<CyEdge>> getEdgeViews() {
	throw new UnsupportedOperationException("This is a null object and does not support this method.");
    }

    @Override
    public Collection<View<? extends CyTableEntry>> getAllViews() {
	throw new UnsupportedOperationException("This is a null object and does not support this method.");
    }

    @Override
    public void fitContent() {
	throw new UnsupportedOperationException("This is a null object and does not support this method.");
    }

    @Override
    public void fitSelected() {
	throw new UnsupportedOperationException("This is a null object and does not support this method.");
    }

    @Override
    public void updateView() {
	throw new UnsupportedOperationException("This is a null object and does not support this method.");
    }

    @Override
    public Boolean isEmptyView() {
	return true;
    }

}
