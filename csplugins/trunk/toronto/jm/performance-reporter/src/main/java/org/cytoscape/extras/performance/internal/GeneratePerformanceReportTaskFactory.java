package org.cytoscape.extras.performance.internal;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.cytoscape.diagnostics.PerformanceDetails;
import org.cytoscape.diagnostics.SystemDetails;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class GeneratePerformanceReportTaskFactory implements TaskFactory {
	private BundleContext context;

	public GeneratePerformanceReportTaskFactory(BundleContext context) {
		this.context = context;
	}

	public TaskIterator createTaskIterator() {
		return new TaskIterator(new AbstractTask() {
			@Override
			public void run(TaskMonitor taskMonitor) throws Exception {
				ServicePool pool = new ServicePool(context);
				try {
					PerformanceDetails performanceDetails = pool.getService(PerformanceDetails.class);
					SystemDetails systemDetails = pool.getService(SystemDetails.class);
					String report = buildReport(systemDetails, performanceDetails);
					
					JDialog dialog = createDialog(report);
					dialog.setLocationByPlatform(true);
					dialog.setSize(800, 600);
					dialog.setVisible(true);
				} finally {
					pool.releaseAll();
				}
			}

		});
	}
	
	JDialog createDialog(String report) {
		final JDialog dialog = new JDialog();
		dialog.setTitle("Performance Report");
		
		JTextArea textArea = new JTextArea(60, 25);
		textArea.setText(report);
		
		JLabel preambleLabel = new JLabel("<html>This app creates an anonymized report about Cytoscape's start-up performance on your system.  You can opt to send the details below to the Cytoscape development team so they can improve the quality and responsiveness of this product.");
		
		JButton sendButton = new JButton("Submit Report");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// TODO: Implement this
			}
		});

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				dialog.setVisible(false);
			}
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(sendButton);
		buttonPanel.add(closeButton);
		
		Container contents = dialog.getContentPane();
		contents.setLayout(new GridBagLayout());
		
		int row = 0;
		contents.add(preambleLabel, new GridBagConstraints(0, row++, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(16, 16, 0, 16), 0, 0));
		contents.add(new JScrollPane(textArea), new GridBagConstraints(0, row++, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(16, 16, 0, 16), 0, 0));
		contents.add(buttonPanel, new GridBagConstraints(0, row++, 1, 1, 0, 0, GridBagConstraints.LAST_LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		return dialog;
	}

	String buildReport(SystemDetails systemDetails, PerformanceDetails performanceDetails) {
		StringBuilder builder = new StringBuilder();
		
		builder.append("OS Name\t" + systemDetails.getOSName());
		builder.append("\n");
		builder.append("Processor Name\t" + systemDetails.getProcessorName());
		builder.append("\n");
		builder.append("Total Memory\t" + systemDetails.getTotalMemory());
		builder.append("\n");
		builder.append("Free Memory\t" + systemDetails.getFreeMemory());
		builder.append("\n");
		builder.append("Total Swap\t" + systemDetails.getTotalSwap());
		builder.append("\n");
		builder.append("Free Swap\t" + systemDetails.getFreeSwap());
		builder.append("\n");
		
		for (String key : new String[] {
			"java.version",
			"java.vm.vendor",
			"java.vm.version",
			"os.arch",
			"os.name",
			"os.version",
		}) {
			builder.append(key + "\t" + System.getProperty(key));
			builder.append("\n");
		}
		
		builder.append("Total Launch Duration\t" + performanceDetails.getTotalLaunchDuration());
		builder.append("\n");
		builder.append("Framework Launch Duration\t" + performanceDetails.getFrameworkLaunchDuration());
		builder.append("\n\n");
		
		List<ReportRow> rows = new ArrayList<ReportRow>();
		for (long id : performanceDetails.getObservedBundleIds()) {
			String description = performanceDetails.getBundleDescription(id);
			long latency = performanceDetails.getBundleLaunchLatency(id);
			long duration = performanceDetails.getBundleLaunchDuration(id);
			if (latency < 0 || duration < 0) {
				continue;
			}
			rows.add(new ReportRow(description, latency, duration));
		}
		if (rows.size() > 0) {
			Collections.sort(rows, new Comparator<ReportRow>() {
				public int compare(ReportRow row1, ReportRow row2) {
					long result = row2.duration - row1.duration;
					if (result != 0) {
						return (int) result;
					}
					return (int) (row1.latency - row2.latency);
				}
			});
			builder.append("Bundle\tLatency\tActivation Duration");
			builder.append("\n");
			for (ReportRow row : rows) {
				builder.append(String.format("%s\t%d\t%d\n", row.name, row.latency, row.duration));
			}
		}
		return builder.toString();
	}

	static class ReportRow {
		public String name;
		public long latency;
		public long duration;
		
		public ReportRow(String name, long latency, long duration) {
			this.name = name;
			this.latency = latency;
			this.duration = duration;
		}
	}
	
	public boolean isReady() {
		return true;
	}
	
	static class ServicePool {
		List<ServiceReference> serviceReferences;
		private BundleContext context;
		
		public ServicePool(BundleContext context) {
			this.context = context;
			serviceReferences = new ArrayList<ServiceReference>();
		}
		
		@SuppressWarnings("unchecked")
		<T> T getService(Class<T> type) {
			ServiceReference reference = context.getServiceReference(type.getName());
			return (T) context.getService(reference);
		}
		
		void releaseAll() {
			Iterator<ServiceReference> iterator = serviceReferences.iterator();
			while (iterator.hasNext()) {
				ServiceReference reference = iterator.next();
				context.ungetService(reference);
				iterator.remove();
			}
		}
	}
}
