package org.cytoscape.browser.internal;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import org.cytoscape.model.CyColumn;
import org.cytoscape.task.TableCellTaskFactory;
import org.cytoscape.task.TableColumnTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class StaticTaskFactoryProvisioner {
	public <T> TaskFactory<T> createFor(final TableCellTaskFactory<T> factory, final CyColumn column, final Object primaryKeyValue) {
		final Reference<CyColumn> columnReference = new WeakReference<CyColumn>(column);
		final Reference<Object> keyReference = new WeakReference<Object>(primaryKeyValue);
		return new ProvisioningTaskFactory<T>() {
			public TaskIterator createTaskIterator(T tunableContext) {
				return factory.createTaskIterator(tunableContext, columnReference.get(), keyReference.get());
			}
			
			public boolean isReady(T tunableContext) {
				return factory.isReady(tunableContext, columnReference.get(), keyReference.get());
			}
			
			@Override
			public T createTunableContext() {
				return factory.createTunableContext();
			}
		};
	}
	
	public <T> TaskFactory<T> createFor(final TableColumnTaskFactory<T> factory, final CyColumn column) {
		final Reference<CyColumn> columnReference = new WeakReference<CyColumn>(column);
		return new ProvisioningTaskFactory<T>() {
			public TaskIterator createTaskIterator(T tunableContext) {
				return factory.createTaskIterator(tunableContext, columnReference.get());
			}
			
			public boolean isReady(T tunableContext) {
				return factory.isReady(tunableContext, columnReference.get());
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
