package cytoscape.render.stateful;

/**
 * Readers: please ignore this for now; this is a reminder to myself.
 */
public interface NodeLabelAreaCallback
{

  public void nodeLabelRendered(int node,
                                float xMinLabelArea, float yMinLabelArea,
                                float xMaxLabelArea, float yMaxLabelArea);

}
