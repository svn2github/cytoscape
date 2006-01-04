package ding.view;

import cytoscape.geom.spacial.MutableSpacialIndex2D;
import giny.model.GraphPerspective;
import giny.model.Edge;
import giny.model.Node;
import giny.model.RootGraph;
import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.GraphViewChangeListener;
import giny.view.NodeView;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Font;
import java.awt.Paint;
import java.util.Iterator;
import java.util.List;

// Package visible class.
class DGraphView implements GraphView
{

  final Object m_lock = new Object();
  final float[] m_extentsBuff = new float[4];
  MutableSpacialIndex2D m_spacial;
  DNodeDetails m_nodeDetails;

  private static class InnerCanvas extends Canvas
  {
  }

  DGraphView()
  {
  }

  public GraphPerspective getGraphPerspective()
  {
    return null;
  }

  public boolean nodeSelectionEnabled()
  {
    return false;
  }

  public boolean edgeSelectionEnabled()
  {
    return false;
  }

  public void enableNodeSelection()
  {
  }

  public void disableNodeSelection()
  {
  }

  public void enableEdgeSelection()
  {
  }

  public void disableEdgeSelection()
  {
  }

  public int[] getSelectedNodeIndices()
  {
    return null;
  }

  public List getSelectedNodes()
  {
    return null;
  }

  public int[] getSelectedEdgeIndices()
  {
    return null;
  }

  public List getSelectedEdges()
  {
    return null;
  }

  public void addGraphViewChangeListener(GraphViewChangeListener l)
  {
  }

  public void removeGraphViewChangeListener(GraphViewChangeListener l)
  {
  }

  public void setBackgroundPaint(Paint paint)
  {
  }

  public Paint getBackgroundPaint()
  {
    return null;
  }

  public Component getComponent()
  {
    return null;
  }

  public NodeView addNodeView(int nodeInx)
  {
    return null;
  }

  public EdgeView addEdgeView(int edgeInx)
  {
    return null;
  }

  public EdgeView addEdgeView(String className, int edgeInx)
  {
    return null;
  }

  public NodeView addNodeView(String className, int nodeInx)
  {
    return null;
  }

  public NodeView addNodeView(int nodeInx, NodeView replacement)
  {
    return null;
  }

  public NodeView removeNodeView(NodeView nodeView)
  {
    return null;
  }

  public NodeView removeNodeView(Node node)
  {
    return null;
  }

  public NodeView removeNodeView(int nodeInx)
  {
    return null;
  }

  public EdgeView removeEdgeView(EdgeView edgeView)
  {
    return null;
  }

  public EdgeView removeEdgeView(Edge edge)
  {
    return null;
  }

  public EdgeView removeEdgeView(int edgeInx)
  {
    return null;
  }

  public String getIdentifier()
  {
    return null;
  }

  public void setIdentifier(String id)
  {
  }

  public double getZoom()
  {
    return 0.0d;
  }

  public void setZoom(double zoom)
  {
  }

  public void fitContent()
  {
  }

  public void updateView()
  {
  }

  public RootGraph getRootGraph()
  {
    return null;
  }

  public Iterator getNodeViewsIterator()
  {
    return null;
  }

  public int getNodeViewCount()
  {
    return 0;
  }

  public int getEdgeViewCount()
  {
    return 0;
  }

  public NodeView getNodeView(Node node)
  {
    return null;
  }

  public NodeView getNodeView(int nodeInx)
  {
    return null;
  }

  public List getEdgeViewsList()
  {
    return null;
  }

  public List getEdgeViewsList(Node oneNode, Node otherNode)
  {
    return null;
  }

  public List getEdgeViewsList(int oneNodeInx, int otherNodeInx,
                               boolean includeUndirected)
  {
    return null;
  }

  public EdgeView getEdgeView(int edgeInx)
  {
    return null;
  }

  public Iterator getEdgeViewsIterator()
  {
    return null;
  }

  public EdgeView getEdgeView(Edge edge)
  {
    return null;
  }

  public int edgeCount()
  {
    return 0;
  }

  public int nodeCount()
  {
    return 0;
  }

  public boolean hideGraphObject(Object obj)
  {
    return false;
  }

  public boolean showGraphObject(Object obj)
  {
    return false;
  }

  public boolean hideGraphObjects(List objects)
  {
    return false;
  }

  public boolean showGraphObjects(List objects)
  {
    return false;
  }

  public Object[] getContextMethods(String className, boolean plusSuperclass)
  {
    return null;
  }

  public Object[] getContextMethods(String className, Object[] methods)
  {
    return null;
  }

  public boolean addContextMethod(String className, String methodClassName,
                                  String methodName, Object[] args,
                                  ClassLoader loader)
  {
    return false;
  }

  public void setAllNodePropertyData(int nodeInx, Object[] data)
  {
  }

  public Object[] getAllNodePropertyData(int nodeInx)
  {
    return null;
  }

  public void setAllEdgePropertyData(int edgeInx, Object[] data)
  {
  }

  public Object[] getAllEdgePropertyData(int edgeInx)
  {
    return null;
  }

  public Object getNodeObjectProperty(int nodeInx, int property)
  {
    return null;
  }

  public boolean setNodeObjectProperty(int nodeInx, int property, Object value)
  {
    return false;
  }

  public Object getEdgeObjectProperty(int edgeInx, int property)
  {
    return null;
  }

  public boolean setEdgeObjectProperty(int edgeInx, int property, Object value)
  {
    return false;
  }

  public double getNodeDoubleProperty(int nodeInx, int property)
  {
    return 0.0d;
  }

  public boolean setNodeDoubleProperty(int nodeInx, int property, double val)
  {
    return false;
  }

  public double getEdgeDoubleProperty(int edgeInx, int property)
  {
    return 0.0d;
  }

  public boolean setEdgeDoubleProperty(int edgeInx, int property, double val)
  {
    return false;
  }

  public float getNodeFloatProperty(int nodeInx, int property)
  {
    return 0.0f;
  }

  public boolean setNodeFloatProperty(int nodeInx, int property, float value)
  {
    return false;
  }

  public float getEdgeFloatProperty(int edgeInx, int property)
  {
    return 0.0f;
  }

  public boolean setEdgeFloatProperty(int edgeInx, int property, float value)
  {
    return false;
  }

  public boolean getNodeBooleanProperty(int nodeInx, int property)
  {
    return false;
  }

  public boolean setNodeBooleanProperty(int nodeInx, int property, boolean val)
  {
    return false;
  }

  public boolean getEdgeBooleanProperty(int edgeInx, int property)
  {
    return false;
  }

  public boolean setEdgeBooleanProperty(int edgeInx, int property, boolean val)
  {
    return false;
  }

  public int getNodeIntProperty(int nodeInx, int property)
  {
    return 0;
  }

  public boolean setNodeIntProperty(int nodeInx, int property, int value)
  {
    return false;
  }

  public int getEdgeIntProperty(int edgeInx, int property)
  {
    return 0;
  }

  public boolean setEdgeIntProperty(int edgeInx, int property, int value)
  {
    return false;
  }

}
