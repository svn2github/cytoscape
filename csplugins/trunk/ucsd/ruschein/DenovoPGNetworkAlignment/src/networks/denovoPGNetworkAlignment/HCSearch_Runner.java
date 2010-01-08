package networks.denovoPGNetworkAlignment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import networks.SFNetwork;
import networks.SNodeModule;
import networks.hashNetworks.FloatHashNetwork;
import networks.linkedNetworks.TypedLinkEdge;
import networks.linkedNetworks.TypedLinkNetwork;
import networks.linkedNetworks.TypedLinkNode;
import networks.linkedNetworks.TypedLinkNodeModule;
import data.StringTable;

public class HCSearch_Runner {

	public static void main (String args[]) 
	{
		double time = System.currentTimeMillis();
		
		if (args.length != 6 && args.length!=7)
		{
			System.out.println("Error: Incorrect number of arguments.");
			System.out.println("Searches for enriched modules and their connections.");
			System.out.println("p1: Physical network");
			System.out.println("p2: Genetic network");
			System.out.println("p3: Output folder");
			System.out.println("p4: Randomize?");
			System.out.println("p5: Alpha");
			System.out.println("p6: Alpha Multiplier");
			System.out.println("p7: Physical network filter degree (optional)");
			System.exit(0);
		}
		
		String fpnet = args[0];
		String fgnet = args[1];
		String fout = args[2]+"/";
		boolean randomize = Boolean.valueOf(args[3]);
		float alpha = Float.valueOf(args[4]);
		float alpham = Float.valueOf(args[5]);
		
		int filter = -1;
		if (args.length==7) filter = Integer.valueOf(args[6]);
		
		System.out.println("Make sure networks are unique and not self!");
		
		//Read in networks and convert to type <String,Double>
		System.out.println("Reading networks.");
		//FloatMatrixNetwork pnet = new FloatMatrixNetwork(fpnet,false,false,0,1,2);
		//FloatMatrixNetwork gnet = new FloatMatrixNetwork(fgnet,false,false,0,1,2);
		
		SFNetwork pnet = new FloatHashNetwork(fpnet,false,true,0,1,2);
		SFNetwork gnet = new FloatHashNetwork(fgnet,false,false,0,1,2);
		
		//Trim all the nodes in the genetic network to ones found in the physical network.
		System.out.println("Trimming genetic network.");
		Set<String> pnodes = pnet.getNodes();
		Set<String> gnodes = gnet.getNodes(); 
		pnodes.retainAll(gnodes);
		gnet = gnet.subNetwork(pnodes);
		
		//Optionally filter physical nodes to ones near genetic nodes
		if (filter!=-1)
		{
			System.out.println("Trimming physical network. Filter degree "+filter);
			TypedLinkNetwork<String,Float> ptlnet = pnet.asTypedLinkNetwork();
			
			pnet = new FloatHashNetwork(ptlnet.subNetwork(pnodes, filter));
		}
		
		//System.out.println("Number of nodes remaining in the physical network: "+pnet.numNodes());
		System.out.println("Number of edges remaining in the physical network: "+pnet.numEdges());
		//System.out.println("Number of nodes remaining in the genetic network: "+gnet.numNodes());
		System.out.println("Number of edges remaining in the genetic network: "+gnet.numEdges());
		
		System.gc();
		utilities.MemoryReporter.reportMemoryUsage();
		
		//Randomize mapping
		
		if (randomize)
		{
			System.out.println("Randomizing the network mapping.");
			
			gnet = gnet.shuffleNodes();
			
			//Check which output to perform
			int taskID = 1;
			try	{
				taskID = Integer.valueOf(System.getenv("SGE_TASK_ID")).intValue();
			}catch (Exception e) {System.out.println("System variable SGE_TASK_ID has not been set. Defaulting to "+taskID+".");}
			
			fout+=taskID;
		}
		
		fout+="/";
		
		//Load a scoring function
		HCScoringFunction sfunc = new SouravScore(pnet,gnet,alpha,alpham);
		System.out.println("Initializing scoring function.");
		sfunc.Initialize(pnet,gnet);
				
		//Perform network search
		System.out.println("Performing network search.");
		TypedLinkNetwork<TypedLinkNodeModule<String,BFEdge>,BFEdge> results = HCSearch2.search(pnet, gnet, sfunc);
		
		System.out.println("Time Elapsed: "+(System.currentTimeMillis()-time)/1000);
		
		//Print a simple summary of the results
		HCSearch2.report(results);
		
		//Create the output directory
		File f = new File(fout);
		if (!f.exists()) f.mkdir();
		
		
		
		//Build a table of complex properties and save it and the complexes themselves. 
		System.out.println("Calculating complex properties.");
		Set<String> testedgenes = pnet.getNodes();
				
		Map<String,SNodeModule> genecomplexes = new HashMap<String,SNodeModule>(testedgenes.size());
				
		StringTable cprops = new StringTable(results.numNodes(), 4,"");
		List<String> colnames = new ArrayList<String>(5);
		colnames.add("CID");colnames.add("Size");colnames.add("Radius");colnames.add("Score");
		cprops.setColNames(colnames);
		
		StringTable gprops = new StringTable(testedgenes.size(),2, "");
		colnames = new ArrayList<String>(3);
		colnames.add("ORF");colnames.add("CID");
		gprops.setColNames(colnames);
		
		List<SNodeModule> clist = new ArrayList<SNodeModule>(results.numNodes());
		Map<TypedLinkNode<TypedLinkNodeModule<String,BFEdge>,BFEdge>,SNodeModule> cmap = new HashMap<TypedLinkNode<TypedLinkNodeModule<String,BFEdge>,BFEdge>,SNodeModule>();
		
		int i=0;
		for (TypedLinkNode<TypedLinkNodeModule<String,BFEdge>,BFEdge> mod : results.nodeIterator())
		{
			String id = String.valueOf(i);
			Set<String> genes = mod.value().getMemberValues();
			SNodeModule newc = new SNodeModule(id,genes);
			clist.add(newc);
			cmap.put(mod, newc);
			
			for (String gene : genes)
				genecomplexes.put(gene, newc);
							
			cprops.set(i, 0, id);
			cprops.set(i, 1, String.valueOf(genes.size()));
			cprops.set(i, 2, String.valueOf(Math.sqrt(genes.size()/Math.PI)));
			
			//if (!randomize && hm.size()>2) ONetwork.saveModuleToSif(fout+"complex_"+ni+".sif", hm, pnet, gnet);
			cprops.set(i, 3, mod.value().score());
			i++;
		}
		SNodeModule.saveComplexes(fout+"complexes.txt",clist);
		
		cprops.SortRowsAsDouble(1);
		cprops.save(fout+"cprops.txt");
		
		
		//Save the complex network (note, this needs to be done AFTER the complex properties have completed)
		//results.saveToFile(fout+"cnet.txt");
		
		//Save gene properties
		System.out.println("Saving properties for "+testedgenes.size()+" genes.");
		i=0;
		for (String gene : testedgenes)
		{
			gprops.set(i, 0, gene);
			SNodeModule parent = genecomplexes.get(gene);
			gprops.set(i, 1, parent.getID());
			i++;
		}
		
		gprops.save(fout+"gprops.txt");
		
		
		//Save each edge and build the edge properties file
		System.out.println("Calculating edge properties.");
		TypedLinkNetwork<String,Float> pn = pnet.asTypedLinkNetwork();
		TypedLinkNetwork<String,Float> gn = gnet.asTypedLinkNetwork();
		
		if (results.numEdges()>0)
		{
			StringTable eprops = new StringTable(results.numEdges(), 7,"");
			colnames = new ArrayList<String>(7);
			colnames.add("CID1");colnames.add("CID2");colnames.add("Score");colnames.add("#Genetic");colnames.add("#Physical");colnames.add("C1Size");colnames.add("C2Size");colnames.add("Density");
			eprops.setColNames(colnames);
			
			i=0;
			for (TypedLinkEdge<TypedLinkNodeModule<String,BFEdge>,BFEdge> edge : results.edgeIterator())
			{
				TypedLinkNodeModule<String,BFEdge> hm1 = edge.source().value();
				TypedLinkNodeModule<String,BFEdge> hm2 = edge.target().value();
				
				//The modules are not built from the gn and pn networks, so their member nodes have no connectivity information!
				int gconnectedness = gn.getConnectedness(hm1.asStringSet(),hm2.asStringSet());
				int pconnectedness = pn.getConnectedness(hm1.asStringSet(),hm2.asStringSet());
				
				eprops.set(i, 0, cmap.get(edge.source()).getID());
				eprops.set(i, 1, cmap.get(edge.target()).getID());
				eprops.set(i, 2, String.valueOf(edge.value().link()));
				eprops.set(i, 3, String.valueOf(gconnectedness));
				eprops.set(i, 4, String.valueOf(pconnectedness));
				eprops.set(i, 5, String.valueOf(hm1.size()));
				eprops.set(i, 6, String.valueOf(hm2.size()));
				eprops.set(i, 7, String.valueOf(edge.value().link()/hm1.size()/hm2.size()));
				
				//if (!randomize && edge.value().link()>0 && hm1.size()>2 && hm2.size()>2 && gconnectedness>2) ONetwork.saveModuleToSif(fout+"edge_"+n1+","+n2+"_"+gconnectedness+".sif", HyperModule.union(hm1, hm2), pnet, gnet);
				i++;
			}
			
			eprops.SortRowsAsDouble(2);
			
			eprops.save(fout+"eprops.txt");
		}

		System.out.println("Time Elapsed: "+(System.currentTimeMillis()-time)/1000);
	}
}