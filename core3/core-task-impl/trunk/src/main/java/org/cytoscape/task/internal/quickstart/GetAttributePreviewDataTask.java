package org.cytoscape.task.internal.quickstart;

import java.util.List;
import org.cytoscape.io.read.CyTableReader;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyColumn;

///////
public class GetAttributePreviewDataTask extends AbstractTask {
	private String[] previewKey;
	private String[][] previewData;
	private CyTableReader reader;
	
	public GetAttributePreviewDataTask(CyTableReader reader, String[] previewKey, String[][] previewData){
		this.reader = reader;
		this.previewKey = previewKey;
		this.previewData = previewData;
	}
	
	@Override
	public void run(TaskMonitor monitor) throws Exception {
	
		CyTable[] tbls = reader.getCyTables();
		
		CyColumn keyCol = tbls[0].getPrimaryKey();
		
		this.previewKey[0] = keyCol.getName();
		
		List values = keyCol.getValues(keyCol.getType());
		
		int colCount = this.previewData.length;
		if (values.size()< colCount){
			colCount = values.size();
		}
		
		for (int i=0; i< colCount; i++){
			this.previewData[i][0] = values.get(i).toString();
		}		
	}
}