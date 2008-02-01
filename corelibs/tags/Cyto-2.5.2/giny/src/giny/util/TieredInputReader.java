package giny.util;

import giny.model.RootGraph;
import giny.model.Node;
import giny.view.*;

import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/** @deprecated Yell loudly (cytoscape-discuss@googlegroups.com) if you want to keep this.
    If we do keep it, it will move somewhere else.  If no one yells, it'll be removed 10/2007 */
public class TieredInputReader
{
  private GraphView view = null;
  private HashMap fNodeNameMap = null;

  public TieredInputReader ( GraphView view )
  {
    this.view = view;
    fNodeNameMap = new HashMap();
    Iterator i = view.getNodeViewsIterator();
    while ( i.hasNext() ) {
      NodeView nv = ( NodeView )i.next();
      fNodeNameMap.put( nv.getNode().getIdentifier(), nv.getNode() );
    }
  }

  public ArrayList readGraphFromInteractionsFile(String filename)
  {
    ArrayList list = new ArrayList ();
    try
    {
      BufferedReader reader = new BufferedReader (new FileReader (filename));
      String line = null;
      StringTokenizer tokenizer = null;
      String nodeName = null;
      int currentTier = 0;
      ArrayList insertList = new ArrayList ();
      while (reader.ready ())
      {
        line = reader.readLine ();
        tokenizer = new StringTokenizer (line);  // whitespace is delimiter
        nodeName = tokenizer.nextToken ();
        int tier = Integer.parseInt (tokenizer.nextToken ());
        System.err.println ("nodeName = " + nodeName + "; tier = " + tier);
        System.err.println ("currentTier = " + currentTier);
        if (tier > currentTier)
        {
          // when we get to the next tier, insert our current tier and get a new one ready to go
          list.add (insertList);
          insertList = new ArrayList ();
          insertList.add (getNode (nodeName));
          currentTier++;
        }
        else if (tier == currentTier) // add to existing
        {
          insertList.add (getNode (nodeName));
        }
      }
      list.add (insertList); // after last time through
    }
    catch (IOException e)
    {
      System.err.println ("Problem reading tier file");
      e.printStackTrace ();
    }
    return list;
  }

  private Node getNode (String nodeName)
  {
    Node retVal = (Node) fNodeNameMap.get (nodeName);
    if (retVal == null)
    {
      System.err.println ("looked up a bad node in TieredInputReader");
    }
    else
    {
      //System.err.println ("Node looked up = " + retVal.getIdentifier());
    }
    return retVal;
  }

}
