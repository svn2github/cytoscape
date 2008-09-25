/* Copyright 2008 - The Cytoscape Consortium (www.cytoscape_merge.org)
 *
 * The Cytoscape Consortium is:
 * - Institute for Systems Biology
 * - University of California San Diego
 * - Memorial Sloan-Kettering Cancer Center
 * - Institut Pasteur
 * - Agilent Technologies
 *
 * Authors: B. Arman Aksoy, Thomas Kelder, Emek Demir
 *
 * This file is part of SimilarityBasedMergePlugin.
 *
 *  SimilarityBasedMergePlugin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PaxtoolsPlugin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.biyoenformatik.cytoscape_merge.task;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import org.biyoenformatik.cytoscape_merge.util.MatchScore;
import org.biyoenformatik.cytoscape_merge.util.Criteria;
import org.biyoenformatik.cytoscape_merge.util.ComponentType;
import org.biyoenformatik.cytoscape_merge.ui.SimilarityBasedMergeDialog;

import java.util.*;

public class ScoreSimilarComponentsTask implements Task {
    private TaskMonitor taskMonitor;
    private CyNetwork network1, network2;
    private SimilarityBasedMergeDialog sbmDialog;
    private ArrayList<MatchScore> scoreList;
    private ArrayList<Criteria> criteriaList;

    public ScoreSimilarComponentsTask(CyNetwork network1, CyNetwork network2,
                                      ArrayList<Criteria> criteriaList, ArrayList<MatchScore> scoreList,
                                      SimilarityBasedMergeDialog sbmDialog) {
        this.network1 = network1;
        this.network2 = network2;
        this.criteriaList = criteriaList;
        scoreList.clear();
        this.scoreList = scoreList;
        this.sbmDialog = sbmDialog;
    }

    public void run() {
        taskMonitor.setPercentCompleted(0);

        String nodeScores = "Scoring nodes";
        int toBeScored = network1.getNodeIndicesArray().length * network2.getNodeIndicesArray().length;
        int scoreCount = 0;

        for(int i1: network1.getNodeIndicesArray()) {
            for(int i2: network2.getNodeIndicesArray()) {
                String node1 = network1.getNode(i1).getIdentifier(),
                       node2 = network2.getNode(i2).getIdentifier();
                taskMonitor.setStatus(nodeScores + " (" + scoreCount + "/" + toBeScored + ")");

                scoreList.add(scoreComponents(ComponentType.NODE, criteriaList, node1, node2));
                scoreCount++;
            }
        }

        String edgeScores = "Scoring edges";
        toBeScored = network1.getEdgeIndicesArray().length * network2.getEdgeIndicesArray().length;
        scoreCount = 0;

        for(int i1: network1.getEdgeIndicesArray()) {
            for(int i2: network2.getEdgeIndicesArray()) {
                String edge1 = network1.getEdge(i1).getIdentifier(),
                       edge2 = network2.getEdge(i2).getIdentifier();
                taskMonitor.setStatus(edgeScores + " (" + scoreCount + "/" + toBeScored + ")");

                scoreList.add(scoreComponents(ComponentType.EDGE, criteriaList, edge1, edge2));
                scoreCount++;
            }
        }

        taskMonitor.setStatus("Sorting scores...");
        Collections.sort(scoreList);
        Collections.reverse(scoreList);

        taskMonitor.setStatus("Updating preview table...");
        sbmDialog.updateMatchesTable();

        taskMonitor.setStatus("Completed.");
        taskMonitor.setPercentCompleted(100);
    }

    private MatchScore scoreComponents(ComponentType type, ArrayList<Criteria> criteriaList,
                                       String id1, String id2) {
        CyAttributes attrs = type.getAttributes();
        double cumulativeScore = 1;

        Map<String, String> matchedAttributes = new HashMap<String, String>();
        for(Criteria criteria: criteriaList) {
            boolean isMatch = false;

            if(!criteria.type.equals(type) || !attrs.hasAttribute(id1, criteria.attribute1)
                                           || !attrs.hasAttribute(id2, criteria.attribute2))
                continue;

            Object value1 = attrs.getAttribute(id1, criteria.attribute1),
                   value2 = attrs.getAttribute(id2, criteria.attribute2);

            if( value1.equals(value2) ) {
                isMatch = true;
            } else if(value1 instanceof List && value2 instanceof List) {
                List intersection = new ArrayList();
                intersection.addAll((List) value1);
                intersection.retainAll((List) value2);
                if( !intersection.isEmpty() )
                    isMatch = true;
            } else if(value1 instanceof List) {
                if( ((List) value1).contains(value2) )
                    isMatch = true;
            } else if(value2 instanceof List) {
                if( ((List) value2).contains(value1) )
                    isMatch = true;
            } else {
                if( (value1.toString().length() > 0 && value2.toString().length() > 0) &&
                        value1.toString().equalsIgnoreCase(value2.toString()) )
                    isMatch = true;
            }

            if(isMatch) {
                cumulativeScore *= (1.0 - criteria.importance);
                matchedAttributes.put(criteria.attribute1, criteria.attribute2);
            }
        }

        return new MatchScore(type, 1.0 - cumulativeScore, id1, id2, matchedAttributes);
    }

    public void halt() {
        // No halt support
    }

    public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    public String getTitle() {
        return "Calculating Similarity Scores";
    }

}
