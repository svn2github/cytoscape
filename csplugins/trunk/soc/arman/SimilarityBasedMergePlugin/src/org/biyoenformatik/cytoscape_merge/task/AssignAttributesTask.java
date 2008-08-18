package org.biyoenformatik.cytoscape_merge.task;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.data.CyAttributes;
import org.biyoenformatik.cytoscape_merge.util.MatchScore;
import org.biyoenformatik.cytoscape_merge.util.ComponentType;
import org.biyoenformatik.cytoscape_merge.SimilarityBasedMergePlugin;

import java.util.*;

public class AssignAttributesTask implements Task {
    private TaskMonitor taskMonitor;

    private ArrayList<MatchScore> scoreList;

    public AssignAttributesTask(ArrayList<MatchScore> scoreList) {
        this.scoreList = scoreList;
    }

    public void run() {
        taskMonitor.setPercentCompleted(0);
        taskMonitor.setStatus("Grouping scores (" + scoreList.size() + ")...");

        // First run - Group similar ones
        Map<String, Set<String>> similarityGroups = new HashMap<String, Set<String>>();
        Map<String, ComponentType> idTypes = new HashMap<String, ComponentType>();

        for(MatchScore ms: scoreList) {
            boolean has1 = similarityGroups.containsKey(ms.id1),
                    has2 = similarityGroups.containsKey(ms.id2);

            if(!has1 && !has2) {
                Set<String> similarSet = new HashSet<String>();
                similarSet.add(ms.id1);
                similarSet.add(ms.id2);
                similarityGroups.put(ms.id1, similarSet);
                similarityGroups.put(ms.id2, similarSet);
            } else if(has1 && has2) {
                Set<String> similarSet1 = similarityGroups.get(ms.id1),
                            similarSet2 = similarityGroups.get(ms.id2);

                if( similarSet1 != similarSet2 )
                    similarSet1.addAll(similarSet2);

                similarityGroups.put(ms.id2, similarSet1);
            } else if(has1) {
                Set<String> similarSet = similarityGroups.get(ms.id1);
                similarSet.add(ms.id2);

                similarityGroups.put(ms.id2, similarSet);
            } else {
                Set<String> similarSet = similarityGroups.get(ms.id2);
                similarSet.add(ms.id1);

                similarityGroups.put(ms.id1, similarSet);
            }

            idTypes.put(ms.id1, ms.type);
            idTypes.put(ms.id2, ms.type);
        }

        taskMonitor.setStatus("Assigning attributes to similar components.");

        for(Set<String> aSet: similarityGroups.values()) {
            assert aSet != null;
            CyAttributes attrs = idTypes.get(aSet.iterator().next()).getAttributes();
            String setCode = "" + aSet.hashCode() + Math.random();

            for(String id: aSet)
                attrs.setAttribute(id, SimilarityBasedMergePlugin.SIMILARITY_CODE, setCode);
        }

        taskMonitor.setStatus("Attribute '" + SimilarityBasedMergePlugin.SIMILARITY_CODE + "' assigned.");
        taskMonitor.setPercentCompleted(100);
    }

    public void halt() {
        // No halt support
    }

    public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    public String getTitle() {
        return "Assign attributes according to similarity scores";
    }

}
