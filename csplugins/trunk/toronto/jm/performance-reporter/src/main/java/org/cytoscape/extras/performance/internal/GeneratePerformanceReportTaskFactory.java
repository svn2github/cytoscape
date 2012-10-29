package org.cytoscape.extras.performance.internal;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.cytoscape.application.CyVersion;
import org.cytoscape.diagnostics.PerformanceDetails;
import org.cytoscape.diagnostics.SystemDetails;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class GeneratePerformanceReportTaskFactory implements TaskFactory {
	private static final String REPORT_HOST = "code.cytoscape.org";
	private BundleContext context;
	boolean reportSent;

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
	
	JDialog createDialog(final String report) {
		final JDialog dialog = new JDialog();
		dialog.setTitle("Performance Report");
		
		JTextArea textArea = new JTextArea(60, 25);
		textArea.setText(report);
		
		JLabel preambleLabel = new JLabel("<html>This app creates an anonymized report about Cytoscape's start-up performance on your system.  You can opt to send the details below to the Cytoscape development team so they can improve the quality and responsiveness of this product.");
		
		final JButton sendButton = new JButton("Submit Report");
		sendButton.setEnabled(!reportSent);
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (sendReport(report)) {
					JOptionPane.showMessageDialog(dialog, "Thank you for sending us your report!  Your feedback will help us improve future versions of Cytoscape.", "Submit Report", JOptionPane.INFORMATION_MESSAGE);
					reportSent = true;
					sendButton.setEnabled(false);
				} else {
					JOptionPane.showMessageDialog(dialog, "We are currently experiencing problems submitting your report.  Please try again later.", "Submit Report", JOptionPane.ERROR_MESSAGE);
				}
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

	protected boolean sendReport(String report) {
		String version = getVersion();
		try {
			return sendReport(REPORT_HOST, version, report);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private String getVersion() {
		ServiceReference reference = context.getServiceReference(CyVersion.class.getName());
		if (reference == null) {
			return null;
		}
		CyVersion version = (CyVersion) context.getService(reference);
		try {
			return version.getVersion();
		} finally {
			context.ungetService(reference);
		}
	}

	boolean sendReport(String host, String version, String report) throws IOException {
		URL url = new URL(String.format("http://%s/log-performance/%s", host, version));
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("PUT");
		PrintWriter writer = new PrintWriter(connection.getOutputStream());
		try {
			writer.write(report);
		} finally {
			writer.close();
		}		
		connection.connect();
		return connection.getResponseCode() == 200;
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
