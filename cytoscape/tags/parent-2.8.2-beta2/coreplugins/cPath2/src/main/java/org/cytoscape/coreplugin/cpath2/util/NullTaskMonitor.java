package org.cytoscape.coreplugin.cpath2.util;

import cytoscape.task.TaskMonitor;

public class NullTaskMonitor implements TaskMonitor {

    public void setPercentCompleted(int i) throws IllegalArgumentException {
    }

    public void setEstimatedTimeRemaining(long l) throws IllegalThreadStateException {
    }

    public void setException(Throwable throwable, String string)
            throws IllegalThreadStateException {
    }

    public void setException(Throwable throwable, String string, String string1)
            throws IllegalThreadStateException {
    }

    public void setStatus(String string) throws IllegalThreadStateException, NullPointerException {
    }
}