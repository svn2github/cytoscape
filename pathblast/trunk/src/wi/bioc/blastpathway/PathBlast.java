package wi.bioc.blastpathway;

/**
 * <p>Title: pathblast</p>
 * <p>Description: pathblast</p>
 * <p>Copyright: Copyright (c) 2002 -- 2006 </p>
 * <p>Company: Whitehead Institute</p>
 * <p>Company: University of California, San Diego</p>
 * @author Bingbing Yuan
 * @author Michael Smoot 
 * @version 1.2
 */

import java.io.*;
import java.util.*;
import java.text.DecimalFormat;

import nct.networkblast.search.*;
import nct.networkblast.graph.*;
import nct.networkblast.score.*;
import nct.networkblast.filter.*;
import nct.graph.*;
import nct.graph.basic.*;
import nct.filter.*;
import nct.output.*;
import nct.networkblast.graph.*;
import nct.networkblast.graph.compatibility.*;
import nct.service.interactions.*;
import nct.service.homology.*;
import nct.service.homology.blast.*;
import nct.visualization.cytoscape.dual.*;

import org.biojava.bio.seq.*;
import org.biojava.bio.seq.db.*;
import org.biojava.bio.symbol.*;

/**
 * This class handles a single request of path blast. Every request
 * is handled by a seperated thread. Current implementation will run
 * the request by invoking a system call
 */

public class PathBlast implements Runnable {
	private BlastManager m_manager;
	private Process m_process;
	private long m_start_time;
	private boolean m_done = false;

	private String m_outputdir;
	private String m_uid;
	private Protein[] proteins; 
	private double e_value; 
	private String t_org;
	private boolean useZero;
	private boolean blastAllDip;

	protected PathBlast(BlastManager manager,
				String outputdir,
				String uid, 
				Protein[] proteins, 
				double e_value, 
				String t_org,
				boolean useZero,
				boolean blastAllDip) {
		m_manager = manager;
		m_outputdir = outputdir;
		m_uid = uid;
		this.proteins = proteins;
		this.e_value = e_value;
		this.t_org = t_org;
		this.useZero = useZero;
		this.blastAllDip = blastAllDip;
	}

	public void run() {

		m_start_time = System.currentTimeMillis();

		try {
			Thread.sleep(1000);

			List<SequenceGraph<String,Double>> seqGraphs = createSequenceGraphs(); 

			HomologyGraph homologyGraph = runBlast(seqGraphs); 

			List<Graph<String,Double>> resultPaths = searchPaths(seqGraphs, homologyGraph);

			writeOutput( resultPaths, seqGraphs );

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("generic exception : " + e.getMessage());
		}

		m_done = true;
	}

	public long getStartTime() {
		return m_start_time;
	}
	public long execTimeSoFar() {
		return System.currentTimeMillis() - m_start_time;
	}
	public boolean isDone() {
		return m_done;
	}
	protected void stop() {
		m_process.destroy();
	}

	/**
	 * Madness.
	 */
	protected List<String> getAlignment(Graph<String,Double> g) {
		List<String> nodes = new ArrayList<String>();
		List<Edge<String,Double>> edges = new ArrayList<Edge<String,Double>>(g.getEdges());

		for (Edge<String,Double> edge : edges) 
			System.out.println("edge: " + edge.getSourceNode() + " " + edge.getTargetNode());
		nodes.add(edges.get(0).getSourceNode());
		nodes.add(edges.get(0).getTargetNode());
		edges.remove(0);

	
		// work from the end
		while ( true ) {
			Edge<String,Double> remove = null; 
			for (Edge<String,Double> edge : edges) {
				String curr = nodes.get(nodes.size()-1); // end
				String source = edge.getSourceNode();
				String target = edge.getTargetNode();

				if ( source.equals( curr ) ) {
					nodes.add(target); // add to end
					remove = edge;
					break;
				}
				if ( target.equals( curr ) ) {
					nodes.add(source); // add to end
					remove = edge;
					break;
				}
			}
			if ( remove != null )
				edges.remove(remove);
			else
				break;
		}

		// work from the front
		while ( true ) {
			Edge<String,Double> remove = null; 
			for (Edge<String,Double> edge : edges) {
				String curr = nodes.get(0); // front
				String source = edge.getSourceNode();
				String target = edge.getTargetNode();

				if ( source.equals( curr ) ) {
					nodes.add(0,target); // add to front
					remove = edge;
					break;
				}
				if ( target.equals( curr ) ) {
					nodes.add(0,source); // add to front
					remove = edge;
					break;
				}
			}
			if ( remove != null )
				edges.remove(remove);
			else
				break;
		}

		for (String s : nodes )
			System.out.println("node: " + s);

		return nodes;	
	}

	private String getDIPURL(String name) {
		String uid = Config.getSynonymMapper().getSynonym(name,"uid");
		String[] parts = uid.split("\\:");
		String val = parts[1].substring(0,parts[1].length()-1);
		String url = "<a href=\"http://dip.doe-mbi.ucla.edu/dip/DIPview.cgi?PK=" + val + "\" target=\"_blank\">"+name+"</a>";
		return url;
	}

	List<SequenceGraph<String,Double>> createSequenceGraphs() { 

		try {
			// create interaction graph based on path input 

		// create temp file that will contain the fasta and blast files 
		File outDir = new File(m_outputdir);
		if ( !outDir.isDirectory() )
			outDir.mkdir();
		File tmpFasta = File.createTempFile("path",".fa", outDir); 

		// create a seqdb for writing the fasta file
		SequenceDB seqdb = new HashSequenceDB();
		for ( int i = 0; i < proteins.length; i++ ) {
			Protein node = proteins[i];
			Sequence prot = ProteinTools.createProteinSequence(node.getSeq(), node.getProteinId()); 
			seqdb.addSequence(prot);
		}

		// create the fasta graph with the temp file and seqdb and then add the nodes/edges.
		FastaGraph<String,Double> ig1 = new FastaGraph<String,Double>(seqdb,tmpFasta.getName(),m_outputdir);
		for ( int i = 0; i < proteins.length-1; i++ ) {
			Protein node1 = proteins[i];
			Protein node2 = proteins[i+1];
			
			System.out.println("adding " + node1.getProteinId() + " and " + node2.getProteinId());
			ig1.addNode(node1.getProteinId());
			ig1.addNode(node2.getProteinId());
			ig1.addEdge(node1.getProteinId(),node2.getProteinId(),1.0);
		}
		System.out.println("query path: " + ig1.toString());

		List<SequenceGraph<String,Double>> seqGraphs = new ArrayList<SequenceGraph<String,Double>>();
		seqGraphs.add(ig1);
		SequenceGraph<String,Double> ig2 = Config.getSpeciesGraph(t_org);
		if ( blastAllDip ) {
			ig2.setDBName("All_DIP_Species.fa");
			System.out.println("blasting against all");
		}
		
		seqGraphs.add(ig2);
		System.out.println("species graph: " + ig2.toString());
		return seqGraphs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	HomologyGraph runBlast( List<SequenceGraph<String,Double>> seqGraphs) {
		try {
		// create the homology graph based on the interaction graphs just created and a
		// homology model
		HomologyModel blastHomology = new LocalBlast(Config.getProperties(), 
		                                             Config.getSynonymMapper(),  
													 m_outputdir + 
													 System.getProperty("file.separator") + 
													 "blastout.xml", e_value);
		return new HomologyGraph(blastHomology, e_value, seqGraphs);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	List<Graph<String,Double>> searchPaths(List<SequenceGraph<String,Double>> seqGraphs, 
	                                       HomologyGraph homologyGraph) {

		// create compat graph based on the interaction graphs and the homology graph 
		ScoreModel<String,Double> logScore = new LogLikelihoodScoreModel<String>(1.0, 0.8, 1e-10);
		CompatibilityCalculator compatCalc = new AdditiveCompatibilityCalculator(0.01,logScore, useZero);
		CompatibilityGraph compatGraph = new CompatibilityGraph(homologyGraph, seqGraphs, compatCalc);
		System.out.println("compatGraph: " + compatGraph.toString());
		// run path analysis
		SearchGraph<String,Double> colorCoding = new ColorCodingPathSearch<String>(proteins.length);
		ScoreModel<String,Double> edgeScore = new SimpleEdgeScoreModel<String>();
		List<Graph<String,Double>> resultPaths = colorCoding.searchGraph(compatGraph, edgeScore);

		System.out.println("begin filtering: " + resultPaths.size());
		if ( !useZero ) {
			Filter<String,Double> noZeros = new UniqueCompatNodeFilter();
			resultPaths = noZeros.filter(resultPaths);
		}
		Filter<String,Double> noDupes = new DuplicateThresholdFilter<String,Double>(1.0);
		resultPaths = noDupes.filter( resultPaths );
		System.out.println("end filtering: " + resultPaths.size());

		Collections.reverse(resultPaths);

		return resultPaths;
		
	}

	void writeOutput( List<Graph<String,Double>> resultPaths, 
	                  List<SequenceGraph<String,Double>> seqGraphs) {
		try {
		//System.out.println("writing results to html");
		// print results to html.
		BufferedWriter fw = new BufferedWriter( new FileWriter( m_outputdir + "/index.html" ) );
		fw.write("<html><head>\n");
		fw.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"../../../docs/pathblast.css\"/>");
		fw.write("</head><body>\n");
		fw.write("<h2>PathBlast results</h2>\n");

		fw.write("<div id=\"legend\">\n");
		fw.write("<a href=\"../../../images/legend.jpg\">\n");
		fw.write("<img src=\"../../../images/small_legend.jpg\" width=\"400\" height=\"215\"/>\n");
		fw.write("</a>\n");
		fw.write("</div>\n");

		fw.write("<ul>\n");
		fw.write("<li>Target Network</li>\n");
		fw.write("<ul>\n");
		fw.write("<li>Species: " + t_org + " </li>\n");
		fw.write("<li>Number of proteins: " + seqGraphs.get(1).numberOfNodes() + "</li>\n");
		fw.write("<li>Number of interactions: " + seqGraphs.get(1).numberOfEdges() + "</li>\n");
		fw.write("</ul>\n");

		fw.write("<li>Query Network</li>\n");
		List<String> nodeL = getAlignment(seqGraphs.get(0));
		fw.write("<ul>\n");

		for (String node: nodeL)
			fw.write("<li>" + node + "</li>\n");
		fw.write("</ul>\n");
		fw.write("<li>Alignment Parameters</li>\n");
		fw.write("<ul>\n");
		fw.write("<li>BLAST E-value: " + e_value + "</li>\n");
		fw.write("<li>BLAST Query Database: " + seqGraphs.get(1).getDBName() + "</li>\n");
		fw.write("<li>Allow Duplicate Protein Networks: " + useZero + "</li>\n");
		fw.write("</ul>\n");
		fw.write("<li><a href=\"../../../docs/faq.html\">FAQ</a></li>\n");
		fw.write("<li><a href=\"../../../docs/publications.html\">References</a></li>\n");
		fw.write("<li><a href=\"http://www.pathblast.org\">PathBLAST Home</a></li>\n");
		fw.write("</ul>\n");
		DecimalFormat decimal = new DecimalFormat("#0.00");
		int count = 0;
		if ( resultPaths.size() == 0 )
			fw.write("No paths found for the specified query path and species.");

		for ( Graph<String,Double> g: resultPaths ) {
			fw.write("<table  class=result cellspacing=0 cellpadding=5>\n");
			fw.write("<tr valign=\"top\"><th class=result>Network "+Integer.toString(count++) +"</th><th class=result>Score</th><th class=result>Query</th> <th class=result>Match</th><th class=result>Match Description</th></tr>\n");
			List<String> nodeList = getAlignment(g);
			int numRows = nodeList.size()  + nodeList.size() - 1;
			String imgFile = "path." + count + ".png";
			String imgThumbFile = "path." + count + ".thumb.png";
			fw.write("<tr valign=\"top\">\n");
			//fw.write("<td rowspan="+ numRows+">" + Integer.toString(count++) + "</td>\n");

			// network image
			fw.write("<td rowspan="+ numRows+">");	
			try {

				DualLayout.create(g,"DualLayout2",m_outputdir + "/" + imgFile, m_outputdir + "/" + imgThumbFile, Config.getProperties().getProperty("cytoscape.vizmap.props"));
				fw.write("<a href=\"" + imgFile + "\"><img src=\"" + imgThumbFile + "\"/></a>");
			} catch (Exception e) { 
				e.printStackTrace(); 
				System.out.println("Couldn't create DualLayout Image");
				fw.write("<p class=error>ERROR creating network image.</p>");
			}
			fw.write("</td>\n");	

			// score
			fw.write("<td rowspan="+ numRows+">" + decimal.format(g.getScore()) + "</td>\n");
			
			// alignment and description
			for ( int q = 0; q < nodeList.size(); q++ ) {
				String n = nodeList.get(q);
				if ( q > 0 )
					fw.write("<tr valign=\"top\">\n");
				String[] nodes = n.split("\\|");
				fw.write("<td>" + nodes[0] + "</td>\n");
				fw.write("<td>" + getDIPURL(nodes[1]) + "</td>\n");
				String desc = Config.getSynonymMapper().getSynonym(nodes[1],"description");
				fw.write("<td>" +desc + "</td>\n");
				fw.write("</tr>");

				if ( q < nodeList.size() - 1 ) {
					fw.write("<tr valign=\"top\">\n");
					fw.write("<td align=\"center\">|</td>\n");
					fw.write("<td align=\"center\">|</td>\n");
					fw.write("<td> &nbsp; </td>\n");
					fw.write("</tr>");
				}
			}
			fw.write("</table></br>\n");

		}

		fw.write("</body></html>\n");
		fw.close();

		//System.out.println("finished writing results to html");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("generic exception : " + e.getMessage());
		}
	}
}
