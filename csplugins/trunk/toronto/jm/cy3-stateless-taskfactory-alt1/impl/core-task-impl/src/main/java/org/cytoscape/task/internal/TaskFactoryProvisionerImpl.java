package org.cytoscape.task.internal;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.task.NetworkCollectionTaskFactory;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.task.NetworkViewCollectionTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.TableTaskFactory;
import org.cytoscape.task.TaskFactoryProvisioner;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class TaskFactoryProvisionerImpl implements TaskFactoryProvisioner {

	private CyApplicationManager applicationManager;

	public TaskFactoryProvisionerImpl(CyApplicationManager applicationManager) {
		this.applicationManager = applicationManager;
	}
	
	@Override
	public <T> TaskFactory<T> createFor(final NetworkTaskFactory<T> factory) {
		return new ProvisioningTaskFactory<T>() {
			@Override
			public TaskIterator createTaskIterator(T tunableContext) {
				return factory.createTaskIterator(tunableContext, applicationManager.getCurrentNetwork());
			}
			
			@Override
			public boolean isReady(T tunableContext) {
				return factory.isReady(tunableContext, applicationManager.getCurrentNetwork());
			}
			
			@Override
			public T createTunableContext() {
				return factory.createTunableContext();
			}
		};
	}

	@Override
	public <T> TaskFactory<T> createFor(final NetworkViewTaskFactory<T> factory) {
		return new ProvisioningTaskFactory<T>() {
			@Override
			public TaskIterator createTaskIterator(T tunableContext) {
				return factory.createTaskIterator(tunableContext, applicationManager.getCurrentNetworkView());
			}
			
			@Override
			public boolean isReady(T tunableContext) {
				return factory.isReady(tunableContext, applicationManager.getCurrentNetworkView());
			}
			
			@Override
			public T createTunableContext() {
				return factory.createTunableContext();
			}
		};
	}

	@Override
	public <T> TaskFactory<T> createFor(final NetworkCollectionTaskFactory<T> factory) {
		return new ProvisioningTaskFactory<T>() {
			@Override
			public TaskIterator createTaskIterator(T tunableContext) {
				return factory.createTaskIterator(tunableContext, applicationManager.getSelectedNetworks());
			}
			
			@Override
			public boolean isReady(T tunableContext) {
				return factory.isReady(tunableContext, applicationManager.getSelectedNetworks());
			}
			
			@Override
			public T createTunableContext() {
				return factory.createTunableContext();
			}
		};
	}

	@Override
	public <T> TaskFactory<T> createFor(final NetworkViewCollectionTaskFactory<T> factory) {
		return new ProvisioningTaskFactory<T>() {
			@Override
			public TaskIterator createTaskIterator(T tunableContext) {
				return factory.createTaskIterator(tunableContext, applicationManager.getSelectedNetworkViews());
			}
			
			@Override
			public boolean isReady(T tunableContext) {
				return factory.isReady(tunableContext, applicationManager.getSelectedNetworkViews());
			}

			@Override
			public T createTunableContext() {
				return factory.createTunableContext();
			}
		};
	}

	@Override
	public <T> TaskFactory<T> createFor(final TableTaskFactory<T> factory) {
		return new ProvisioningTaskFactory<T>() {
			public TaskIterator createTaskIterator(T tunableContext) {
				return factory.createTaskIterator(tunableContext, applicationManager.getCurrentTable());
			}
			
			@Override
			public boolean isReady(T tunableContext) {
				return factory.isReady(tunableContext, applicationManager.getCurrentTable());
			}
			
			@Override
			public T createTunableContext() {
				return factory.createTunableContext();
			}
		};
	}

	private abstract class ProvisioningTaskFactory<T> implements TaskFactory<T> {
		@Override
		public TaskIterator createTaskIterator() {
			return createTaskIterator(createTunableContext());
		}
		
		@Override
		public boolean isReady() {
			return isReady(createTunableContext());
		}
	}
}
