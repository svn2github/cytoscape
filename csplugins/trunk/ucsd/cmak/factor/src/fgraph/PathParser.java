package fgraph;

import java.io.*;

import java.util.*;

import java.util.logging.Logger;
import java.util.logging.Level;

import fgraph.util.Target2PathMap;

public class PathParser
{
    private static Logger logger = Logger.getLogger(PathParser.class.getName());
    InteractionGraph ig;
    
    /**
     * Create a PathResult object from output from the C program
     *
     * @param
     * @return
     * @throws
     */
    public static PathResult parse(InteractionGraph ig, String file)
        throws IOException
    {
        PathParser pp = new PathParser(ig);
        return pp.parse(file);
    }

        
    private PathParser(InteractionGraph ig)
    {
        this.ig = ig;
    }


    private PathResult parse(String file)
        throws IOException
    {
        List paths = new ArrayList();
        PathResult result = readFile(paths, file);

        addPaths(result, paths);

        return result;
    }


    private void addPaths(PathResult result, List paths)
    {
        int pathCount = 0;

        try
        {
        processPath: for(int x=0; x < paths.size(); x++)
        {
            String[] path = (String[]) paths.get(x);

            logPath(path);
            int inGraph = allNodesInGraph(path);
            logger.fine("  nodes not in graph " + inGraph);
            if(inGraph < 0)
            {
                int ko = ig.name2Node(path[0]);
                int tmp = ko;

                logger.fine("  ko " + ko);
                
                for(int y=1; y < path.length; y++)
                {
                    int node = ig.name2Node(path[y]);
                    logger.fine("  node " + node);
                    
                    int[] ea = ig.getEdgeIndicesArray(tmp, node);

                    if(ea != null && ea.length == 1)
                    {
                        PathResult.Interval i = result.addInterval(ea[0]);
                        i.setStart(pathCount);
                        i.setEnd(pathCount+1);
                        i.setDir(getDir(tmp, node));

                        logger.fine("added interval " + i);
                    }
                    else
                    {
                        logger.warning((ea==null ? 0 : ea.length)
                                       + " edges exist from "
                                       + ig.node2Name(tmp)
                                       + " to " + ig.node2Name(node)
                                       + ". skipping path " + x);

                        continue processPath;
                    }
                    
                    tmp = node;
                }

                logger.fine("adding path " + pathCount + " from "
                            + path[0] + " to " + path[path.length-1]);

                Target2PathMap map = result.addKO(ko);
                map.addPath(tmp, pathCount);
                pathCount++;
            }
            else
            {
                logger.warning("  node " + path[inGraph]
                               + " is not in graph. skipping path " + x);
            }


        }
        } catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    private void logPath(String[] path)
    {
        StringBuffer b = new StringBuffer();
        
        for(int y=0; y < path.length; y++)
        {
            b.append(path[y]);
            b.append(" ");
          }
        
        logger.fine(b.toString());
        
    }
    
    /**
     * This method returns the directionality of an edge in a path.
     *
     * @param ig the interaction graph
     * @param edge the id of the edge in the interaction graph
     * @param pathSource the source node of the edge in the path
     * @return State.PLUS if pathSource is equal to the source of "edge"
     *         that is stored in the interaction graph.
     *         State.MINUS if pathSource is the target of "edge"
     */
    private State getDir(int edge, int pathSource)
    {
        int source = ig.getBioGraph().getEdgeSourceIndex(edge);
        int target = ig.getBioGraph().getEdgeTargetIndex(edge);

        if(source == pathSource)
        {
            return State.PLUS;
        }
        else
        {
            return State.MINUS;
        }
    }

    /**
     * Check that all of the nodes along a path are in the 
     * interaction graph.
     * 
     * @param path an array of ORF's
     * @return -1 if all node are in the graph, or the index of the
     *         first node that is not in the graph.
     */
    private int allNodesInGraph(String[] path)
    {
        for(int y=0; y < path.length; y++)
        {
            if(!ig.containsNode(path[y]))
            {
                return y;
            }
        }

        return -1;
    }

    private PathResult readFile(List paths, String file)
        throws IOException
    {
        BufferedReader in = new BufferedReader(new FileReader(file));

        String line;
        String[] tmp;
        int max = 0;
        int n = 0;
        while((line = in.readLine()) != null)
        {
            tmp = line.split("\\s");
            paths.add(tmp);

            n += 2*tmp.length;

            if(tmp.length > max)
            {
                max = tmp.length;
            }
        }

        in.close();

        logger.info("parsed " + paths.size() + " paths");
                
        PathResult result = new PathResult(max, n, n);
        result.setPathCount(paths.size());

        return result;
    }
    
}
