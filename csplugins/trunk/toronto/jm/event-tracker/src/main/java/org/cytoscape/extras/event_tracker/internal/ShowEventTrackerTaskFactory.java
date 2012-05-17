package org.cytoscape.extras.event_tracker.internal;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

public class ShowEventTrackerTaskFactory extends AbstractTaskFactory {
	private final CySwingApplication application;
	private final EventDataTableModel model;
	private final Set<String> trackedEvents;

	private JDialog dialog;
	private JTable table;
	
	public ShowEventTrackerTaskFactory(CySwingApplication application, EventTracker tracker) {
		this.application = application;
		trackedEvents = new HashSet<String>();
		
		model = new EventDataTableModel(); 
		tracker.addListener(new EventOccurredListener() {
			public void eventOccurred(EventData data) {
				synchronized (model) {
					String key = data.getFullName();
					if (trackedEvents.contains(key)) {
						model.update(data);
						return;
					}
					trackedEvents.add(key);
					model.addRow(data);
				}
				if (dialog != null) {
					dialog.invalidate();
					dialog.repaint();
				}
			}
		});
	}

	public TaskIterator createTaskIterator() {
		return new TaskIterator(new AbstractTask() {
			@Override
			public void run(TaskMonitor taskMonitor) throws Exception {
				showEventTracker();
			}
		});
	}

	public void showEventTracker() {
		synchronized (this) {
			if (dialog == null) {
				dialog = createDialog(model);
				int width = 500;
				int height = computeHeight(width);
				dialog.setSize(width, height);
			}
			dialog.setVisible(true);
		}
	}
	
	public void dispose() {
		synchronized (this) {
			if (dialog == null) {
				return;
			}
			dialog.dispose();
			dialog = null;
		}
	}
	private int computeHeight(int width) {
		// Use golden mean: (p+q)/p = p/q where p+q = width
		// Then:
		//   q = width-p
		//   width/p = p/(width-p)
		//   p^2 + (width)p - width^2 = 0
		// Now solve for quadratic equation where A=1, B=width, C=-width^2
		int discriminant = width * width - 4 * (-width * width);
		return (int) ((-width + Math.sqrt(discriminant)) / 2);
	}

	private JDialog createDialog(TableModel model) {
		JDialog dialog = new JDialog(application.getJFrame());
		dialog.setTitle("Event Tracker");
		dialog.setModal(false);
		Container contents = dialog.getContentPane();
		contents.setLayout(new BorderLayout());
		table = new JTable(model);
		table.setAutoCreateRowSorter(true);
		table.setShowGrid(true);
		contents.add(new JScrollPane(table), BorderLayout.CENTER);
		
		JButton resetButton = new JButton("Reset Counters");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				handleReset();
			}
		});
		
		final JCheckBox useFullNameCheckBox = new JCheckBox("Show fully-qualified class names");
		useFullNameCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleFullNameCheckBox(useFullNameCheckBox.isSelected());
			}
		});
		
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(useFullNameCheckBox);
		buttonPanel.add(resetButton);
		contents.add(buttonPanel, BorderLayout.PAGE_END);
		
		handleFullNameCheckBox(useFullNameCheckBox.isSelected());
		return dialog;
	}

	private void handleFullNameCheckBox(boolean selected) {
		model.setUseShortName(!selected);
	}

	void handleReset() {
		model.reset();
	}
}
