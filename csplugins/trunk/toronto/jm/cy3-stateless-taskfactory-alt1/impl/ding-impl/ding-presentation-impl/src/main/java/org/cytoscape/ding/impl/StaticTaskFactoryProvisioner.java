package org.cytoscape.ding.impl;

import java.awt.datatransfer.Transferable;
import java.awt.geom.Point2D;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import org.cytoscape.dnd.DropNetworkViewTaskFactory;
import org.cytoscape.dnd.DropNodeViewTaskFactory;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class StaticTaskFactoryProvisioner {
	public <T> TaskFactory<T> createFor(final NetworkViewTaskFactory<T> factory, CyNetworkView networkView) {
		final Reference<CyNetworkView> reference = new WeakReference<CyNetworkView>(networkView);
		return new ProvisioningTaskFactory<T>() {
			public TaskIterator createTaskIterator(T tunableContext) {
				return factory.createTaskIterator(tunableContext, reference.get());
			}
			
			public boolean isReady(T tunableContext) {
				return factory.isReady(tunableContext, reference.get());
			}
			
			@Override
			public T createTunableContext() {
				return factory.createTunableContext();
			}
		};
	}
	
	public <T> TaskFactory<T> createFor(final DropNetworkViewTaskFactory<T> factory, CyNetworkView networkView, final Transferable transferable, final Point2D point, final Point2D transformedPoint) {
		final Reference<CyNetworkView> reference = new WeakReference<CyNetworkView>(networkView);
		return new ProvisioningTaskFactory<T>() {
			public TaskIterator createTaskIterator(T tunableContext) {
				return factory.createTaskIterator(tunableContext, reference.get(), transferable, point, transformedPoint);
			}
			
			public boolean isReady(T tunableContext) {
				return factory.isReady(tunableContext, reference.get(), transferable, point, transformedPoint);
			}
			
			@Override
			public T createTunableContext() {
				return factory.createTunableContext();
			}
		};
	}

	public <T> TaskFactory<T> createFor(final NodeViewTaskFactory<T> factory, View<CyNode> nodeView, CyNetworkView networkView) {
		final Reference<View<CyNode>> nodeReference = new WeakReference<View<CyNode>>(nodeView);
		final Reference<CyNetworkView> networkReference = new WeakReference<CyNetworkView>(networkView);
		return new ProvisioningTaskFactory<T>() {
			public TaskIterator createTaskIterator(T tunableContext) {
				return factory.createTaskIterator(tunableContext, nodeReference.get(), networkReference.get());
			}
			
			public boolean isReady(T tunableContext) {
				return factory.isReady(tunableContext, nodeReference.get(), networkReference.get());
			}
			
			@Override
			public T createTunableContext() {
				return factory.createTunableContext();
			}
		};
	}

	public <T> TaskFactory<T> createFor(final EdgeViewTaskFactory<T> factory, View<CyEdge> edgeView, CyNetworkView networkView) {
		final Reference<View<CyEdge>> edgeReference = new WeakReference<View<CyEdge>>(edgeView);
		final Reference<CyNetworkView> networkReference = new WeakReference<CyNetworkView>(networkView);
		return new ProvisioningTaskFactory<T>() {
			public TaskIterator createTaskIterator(T tunableContext) {
				return factory.createTaskIterator(tunableContext, edgeReference.get(), networkReference.get());
			}
			
			public boolean isReady(T tunableContext) {
				return factory.isReady(tunableContext, edgeReference.get(), networkReference.get());
			}
			
			@Override
			public T createTunableContext() {
				return factory.createTunableContext();
			}
		};
	}

	public <T> TaskFactory<T> createFor(final DropNodeViewTaskFactory<T> factory, View<CyNode> nodeView, CyNetworkView networkView, final Transferable transferable, final Point2D point, final Point2D transformedPoint) {
		final Reference<View<CyNode>> nodeReference = new WeakReference<View<CyNode>>(nodeView);
		final Reference<CyNetworkView> networkReference = new WeakReference<CyNetworkView>(networkView);
		return new ProvisioningTaskFactory<T>() {
			public TaskIterator createTaskIterator(T tunableContext) {
				return factory.createTaskIterator(tunableContext, nodeReference.get(), networkReference.get(), transferable, point, transformedPoint);
			}
			
			public boolean isReady(T tunableContext) {
				return factory.isReady(tunableContext, nodeReference.get(), networkReference.get(), transferable, point, transformedPoint);
			}
			
			@Override
			public T createTunableContext() {
				return factory.createTunableContext();
			}
		};
	}

	static abstract class ProvisioningTaskFactory<T> implements TaskFactory<T> {
		@Override
		public TaskIterator createTaskIterator() {
			return createTaskIterator(null);
		}
		
		@Override
		public boolean isReady() {
			return isReady(null);
		}
	}
}
