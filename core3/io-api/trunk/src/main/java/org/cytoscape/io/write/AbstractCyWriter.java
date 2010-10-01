
package org.cytoscape.io.write;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;
import org.cytoscape.io.CyFileFilter;
import java.io.File;

import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;

/**
 */
public abstract class AbstractCyWriter<T extends CyWriterManager> extends AbstractTask 
	implements CyWriter {

	private File outputFile;

	public final void setOutputFile(File f) {
		if ( f != null )
			outputFile = f;
	}

	@Tunable(description="Select the output file name")
	public final File getOutputFile() {
		return outputFile;
	}

	@Tunable(description = "Select the export file format")
	public final ListSingleSelection<String> options;

	private final Map<String,CyFileFilter> descriptionFilterMap;

	protected final T writerManager;

	public AbstractCyWriter(T writerManager) {
		if ( writerManager == null )
			throw new NullPointerException("CyWriterManager is null");
		this.writerManager = writerManager;

		descriptionFilterMap = new TreeMap<String,CyFileFilter>();
		for ( CyFileFilter f : writerManager.getAvailableWriters() )
			descriptionFilterMap.put( f.getDescription(), f );
   
		options = new ListSingleSelection<String>( new ArrayList<String>( descriptionFilterMap.keySet() ) );
	}

	public final void run(TaskMonitor tm) throws Exception {
		if ( outputFile == null )
			throw new NullPointerException("Output file has not be specified!");

		String desc = options.getSelectedValue();
		if ( desc == null )
			throw new NullPointerException("No file type has been specified!");

		CyFileFilter filter = descriptionFilterMap.get(desc);
		if ( filter == null )
			throw new NullPointerException("No file filter found for specified file type!");
		
		CyWriter writer = getWriter(filter,outputFile); 
		if ( writer == null )
			throw new NullPointerException("No CyWriter found for specified file type!");

		insertTasksAfterCurrentTask( writer );
	}

	protected abstract CyWriter getWriter(CyFileFilter filter, File out) throws Exception;

}
