/*
	
	StatisticsPlugin for Cytoscape (http://www.cytoscape.org/) 
	Copyright (C) 2007 Pekka Salmela

	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
	
 */

package statisticsPlugin;

import java.text.DecimalFormat;

import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;

/**
 * Class used to manage and control different 
 * modules used to calculate graph statistics, 
 * as well reporting the progress of the 
 * calculation.
 * 
 * @author Pekka Salmela
 *
 */
public class StatisticsManager {
	
	/**
	 * Task monitor used to monitor the 
	 * progress of the calculation.
	 */
	protected TaskMonitor taskMonitor;
	
	/**
	 * Network view the statistics are 
	 * calculated for.
	 */
	private CyNetworkView view;
	
	/**
	 * Flag to inform the calculation method 
	 * that it should be aborted.
	 */
	private boolean cancel;
	
	/**
	 * Class constructor.
	 * @param view Network view the statistics are 
	 * calculated for.
	 */
	public StatisticsManager(CyNetworkView view){
		this.view = view;
	}
	
	/**
	 * Sets the cancel flag to true.
	 */
	public void setCancel() {
		this.cancel = true;
	}
	
	/**
	 * Uses different statistics modules to calculate the 
	 * statistics and gets the string representation of the results 
	 * of the calculation. Also updates the task monitor 
	 * when the calculation progresses.
	 * @return Results of the calculation.
	 */
	public String reportStatistics(){
		String message = "";
        message += "Statistics about your network:\n";
        
		taskMonitor.setStatus("Calculating edge statistics...");
		taskMonitor.setPercentCompleted(20);
		
        EdgeLengthStatistics es = new EdgeLengthStatistics(view);
        es.generateEdgeStatistics();
        message += es.reportStatistics();
        
        if(cancel) return message;
        
        taskMonitor.setStatus("Calculating edge intersection statistics...");
		taskMonitor.setPercentCompleted(40);
        
        EdgeIntersectionStatistics eis = new EdgeIntersectionStatistics(view);
        eis.generateStatistics();
        message += eis.reportStatistics(); 
        
        if(cancel) return message;
        
        taskMonitor.setStatus("Calculating edge angle statistics...");
		taskMonitor.setPercentCompleted(50);
        
        EdgeAngleStatistics as = new EdgeAngleStatistics(view);
        as.generateStatistics();
        message += as.reportStatistics(); 
        
        if(cancel) return message;
        
        taskMonitor.setStatus("Calculating node deviation statistics...");
		taskMonitor.setPercentCompleted(60);
		
		NodeDeviationStatistics nds = new NodeDeviationStatistics(view);
		nds.generateStatistics();
		message += nds.reportStatistics();
        
		if(cancel) return message;
		
        taskMonitor.setStatus("Calculating node statistics...");
		taskMonitor.setPercentCompleted(80);
        
        NodeDegreeStatistics ns = new NodeDegreeStatistics(view.getNetwork());
        ns.generateStatistics();
        message += ns.reportStatistics();
        
        taskMonitor.setStatus("Calculating clustring coefficients...");
		taskMonitor.setPercentCompleted(90);
        
        ClusteringCoefficientStatistics ccs = new ClusteringCoefficientStatistics(view.getNetwork());
        ccs.generateStatistics();
        message += ccs.reportStatistics();
        
        taskMonitor.setStatus("Statistics calculation complete.");
		taskMonitor.setPercentCompleted(100);
		
        // These lines are used to print out the result for easier copying:
		DecimalFormat df = new DecimalFormat("0.##");
		System.out.println();
		System.out.println(view.getNetwork().getNodeCount());
		System.out.println(view.getNetwork().getEdgeCount());
		System.out.println(df.format((double)(view.getNetwork().getEdgeCount())/(double)(view.getNetwork().getNodeCount())));
		System.out.println(df.format(ns.getSum()/((double)ns.getCount())));
		System.out.println(ns.getDegreeOne());
		System.out.println(ns.getDegreeLessThanFive());
		System.out.println(ns.getDegreeLessThanTen());
		System.out.println(ns.getDegreeLessThanFifty());
		System.out.println((int)ns.getMax());
		System.out.println(df.format(ccs.getSum()/ccs.getCount()));
		System.out.println();
		System.out.println(df.format(nds.getXStandardDeviation()));
		System.out.println(df.format(nds.getYStandardDeviation()));
		System.out.println(eis.getIntersections() + "(" + eis.getCheckedEdges() + ")");
		System.out.println(df.format(as.getSum()/(double)as.getCheckedNodes()) + "(" + as.getCheckedNodes() + ")");
		System.out.println(df.format(es.getStandardDeviation()));
		
		
        return message;
	}
	
	/**
	 * Sets the task monitor to be used.
	 * @param t Task monitor to be used.
	 */
	public void setTaskMonitor(TaskMonitor t) {
			this.taskMonitor = t;
	}
}
