
import cytoscape.data.Interaction;
import cytoscape.data.readers.TextFileReader;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.servers.BioDataServer;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.StringTokenizer;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.FileOutputStream;

import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntDoubleHashMap;
import cern.colt.map.OpenIntObjectHashMap;


/**
 * Factory class for create an interaction graph.
 *
 */
public class InteractionGraphFactory
{

    private static String _defaultBioDataDir = "/cellar/users/cmak/cytoscape/testData/annotation/manifest";

        // pattern for matching directed edges.
    private static Pattern directedPattern =
        Pattern.compile("ypd|mms|pd", Pattern.CASE_INSENSITIVE);

    /**
     * @return true if an edge of type "type" is directed, false otherwise
     */
    static boolean isDirected(String type)
    {
        return directedPattern.matcher(type).matches();
    }

    
    /**
     * Factory method to create and InteractionGraph from a .sif file
     * Note: does not throw FileNotFoundException if filename is not found
     * @param filename The name of the file. 
     */
    public static InteractionGraph createFromSif(String filename)
        throws Exception
    {
        List interactions = loadInteractions(filename);

        InteractionGraph g = createFromInteractions(interactions, false);

        return g;
        
    }


    private static String canonicalizeName(String name)
    {
        return name;
    }

    
    /**
     * Edges labelled "ypd" or "mms" are directed
     * Edges labeled "pp" are undirected
     */
    private static InteractionGraph createFromInteractions(List interactions, 
                                                           boolean canonicalize)
    {
        //figure out how many nodes and edges we need before we create
        // the graph;
        //this improves performance for large graphs

        int edgeCount = 0;

        int selfEdges = 0;
        int numDirected = 0;
        int numUndirected = 0;
        
        Set nodeNameSet = new HashSet();
        
        // find all unique node names
        // count number of nodes
        // count number of edges
        for (int i=0; i<interactions.size(); i++) 
        {
            Interaction interaction = (Interaction) interactions.get(i);
            String sourceName = interaction.getSource();
            
            if(canonicalize) sourceName = canonicalizeName (sourceName);
            
            nodeNameSet.add(sourceName); //does nothing if already there

            String [] targets = interaction.getTargets ();
            for (int t=0; t < targets.length; t++) 
            {
                String targetNodeName = targets[t];
                if(canonicalize) targetNodeName = canonicalizeName (targetNodeName);
                nodeNameSet.add(targetNodeName); //does nothing if already there
                edgeCount++;

                String type = interaction.getType();
                // ypd and mms are directed edges from ChIP-CHIP experiments
                if(sourceName.equals(targetNodeName))
                {
                    selfEdges++;
                }
                else if( isDirected(type) )
                {
                    numDirected++;
                }
                else
                {
                    numUndirected++;
                }


            }
        }
        
        // Create the InteractionGraph
        int nodeCount = nodeNameSet.size();

        InteractionGraph g = new InteractionGraph(nodeCount, edgeCount);
        
        // Create the nodes
        int[] nIndices = g.createNodes(nodeCount);

        int i=0;
        for(Iterator si = nodeNameSet.iterator(); si.hasNext();)
        {
            String nodeName = (String)si.next();

            g.addNodeName(nIndices[i], nodeName);
            i++;
        }
        
        /* 
         * for efficiency, create all of the edges at once
         * after source and target node index arrays
         * have been created
         *
         * loop over the interactions
         * record edge source and target node indices
         *
         */
        
        String targetNodeName = null;
        String sourceName = null;

        int sourceIndex = 0;
        int targetIndex = 0;

        Interaction interaction = null;

        /*
        System.out.println("interactions=" + interactions.size());
        System.out.println("edges=" + edgeCount);
        System.out.println("selfEdges=" + selfEdges);
        */

        int[] Dsrc = new int[numDirected];
        int[] Dtgt = new int[numDirected];

        int[] Usrc = new int[numUndirected + selfEdges];
        int[] Utgt = new int[numUndirected + selfEdges];
        
        int[] self = new int[selfEdges];
        
        String[] directedTypes = new String[numDirected];
        String[] selfTypes = new String[selfEdges];
        String[] undirectedTypes = new String[numUndirected + selfEdges];

        int selfCt = 0;
        int undirCt = 0;
        int dirCt = 0;
        int x=0;
        for (x=0; x < interactions.size(); x++) 
        {
            interaction = (Interaction) interactions.get(x);

            if(canonicalize) 
            {
                sourceName = canonicalizeName (interaction.getSource ());
            }
            else
            {
                sourceName = interaction.getSource();
            }

            sourceIndex = g.name2Node(sourceName);

            String [] targets = interaction.getTargets ();
            for (int t=0; t < targets.length; t++) 
            {
                if(canonicalize)
                    targetNodeName = canonicalizeName (targets [t]);
                else
                    targetNodeName = targets[t];

                targetIndex = g.name2Node(targetNodeName);

                String type = interaction.getType();
                // record edge endpoints
                if(sourceIndex == targetIndex)
                {
                    undirectedTypes[undirCt] = type;
                    Usrc[undirCt] = sourceIndex;
                    Utgt[undirCt] = targetIndex;
                    undirCt++;
                    
                    /*selfTypes[selfCt] = interaction.getType();
                    self[selfCt]  = sourceIndex;
                    selfCt++;
                    */
                }
                else if( isDirected(type) )
                {
                    directedTypes[dirCt] = type;
                    Dsrc[dirCt] = sourceIndex;
                    Dtgt[dirCt] = targetIndex;
                    dirCt++;
                }
                else
                {
                    undirectedTypes[undirCt] = type;
                    Usrc[undirCt] = sourceIndex;
                    Utgt[undirCt] = targetIndex;
                    undirCt++;
                }
            }
        }

        /*
        System.out.println("x=" + x);
        System.out.println("selfCt=" + selfCt);
        System.out.println("undirCt=" + undirCt);
        */

        //g.createEdges(self, self, selfTypes, false);
        g.createEdges(Usrc, Utgt, undirectedTypes, false);
        g.createEdges(Dsrc, Dtgt, directedTypes, true);

        return g;
    } // createRootGraphFromInteractionData

    
    private static List loadInteractions(String filename)
    {
        String rawText = "";
        //BioDataServer bds = new BioDataServer(_defaultBioDataDir);
        List interactions = new ArrayList();

        TextFileReader reader = new TextFileReader (filename);
        
        int len = reader.read ();
        
        if(len > 0)
        {
            rawText = reader.getText ();
        }

        String delimiter = " ";
        if (rawText.indexOf ("\t") >= 0)
        {
            delimiter = "\t";
        }
        StringTokenizer strtok = new StringTokenizer (rawText, "\n");
        
        while (strtok.hasMoreElements ()) {
            String newLine = (String) strtok.nextElement ();
            interactions.add (new Interaction(newLine, delimiter));
        }

        return interactions;
    }

    public static InteractionGraph createFromSif(String sifFile,
                                                 String candidateGenes)
        throws Exception
    {
        List interactions = loadInteractions(sifFile);
        
        try
        {
            int i = interactions.size();
            Set candidates = loadCandidateGenes(candidateGenes);
            System.out.println("Parsed " + candidates.size()
                               + " candidate gene names.");
            
            int x = filterInteractions(interactions, candidates);
            System.out.println("Removed " + x + " of " + i + " interactions."
                               + (i - x) + " remain.");
        }
        catch(IOException e)
        {
            System.err.println("Error reading candidate genes file: "
                               + candidateGenes);
            e.printStackTrace();
        }
        
        InteractionGraph g = createFromInteractions(interactions, false);

        return g;

    }

    
    private static Set loadCandidateGenes(String filename)
        throws IOException
    {
        String rawText = "";
        List interactions = new ArrayList();

        TextFileReader reader = new TextFileReader (filename);
        
        int len = reader.read ();
        
        if(len > 0)
        {
            rawText = reader.getText ();
        }
        else
        {
            throw new IOException("loadCandidateGenes: TextFileReader error");
        }

        StringTokenizer strtok = new StringTokenizer (rawText, "\n");

        Set genes = new HashSet();
        
        while(strtok.hasMoreTokens())
        {
            String[] g = strtok.nextToken().trim().split("\\s+");
            for(int x=0; x < g.length; x++)
            {
                //System.out.println("c = " + g[x]);
                genes.add(g[x]);
            }
        }

        return genes;
    }

    private static int filterInteractions(List interactions, Set candidates)
    {
        int n=0;
        
        for(ListIterator it = interactions.listIterator(); it.hasNext();)
        {
            Interaction i = (Interaction) it.next();
            boolean remove = false;

            // remove an interaction if neither its source or any of its
            // targets are in the set of candidate genes.

            if(!candidates.contains(i.getSource()))
            {
                remove = true;
                
            }
            else
            {
                String[] targets = i.getTargets();
                for(int x=0; x < targets.length; x++)
                {
                    if(!candidates.contains(targets[x]))
                    {
                        remove = true;
                        break;
                    }
                }
            }

            if(remove)
            {
                it.remove();
                n++;
            }
                
        }

        return n;
    }


    /**
     * Load an edge attributes file and store the data in
     * a data structure that maps edge index to a p value.
     *
     * @param g the interaction graph
     * @param filename the file containing "EdgeProbs" edge attributes
     */
    public static void loadEdgeData(InteractionGraph g, String filename)
        throws FileNotFoundException, IllegalArgumentException,
               NumberFormatException
    {
          GraphObjAttributes edgeData = new GraphObjAttributes();
          edgeData.readAttributesFromFile( filename );
          Map m = edgeData.getAttribute("EdgeProbs");

          OpenIntDoubleHashMap pvMap = g.getEdgePvalMap();
          pvMap.ensureCapacity(m.size());

          ObjectIntMap name2edge = new ObjectIntMap( g.numEdges() );          

          int[] edges = g.edges();

          // create a mapping of edges names to edge indicies
          for(int x=0; x < edges.length; x++)
          {
              name2edge.put(g.edgeName(edges[x]), edges[x]);
          }

          // now map edge indicies to a pvalue/probability
          for(Iterator it = m.entrySet().iterator(); it.hasNext();)
          {
              Map.Entry e = (Map.Entry) it.next();

              //System.out.print("edge key=" + e.getKey() + " val=" +  e.getValue());
              if(name2edge.containsKey(e.getKey()))
              {
                  int i = name2edge.get(e.getKey());
                  
                  //System.out.println(" mapped to index: " + i);
                  
                  pvMap.put(i, ((Double) e.getValue()).doubleValue());
              }
          }
          
    }

    
}
