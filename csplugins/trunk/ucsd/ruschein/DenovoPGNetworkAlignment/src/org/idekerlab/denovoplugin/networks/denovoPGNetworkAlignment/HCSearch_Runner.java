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

	public static void main(String args[]) {
		double time = System.currentTimeMillis();

		if (args.length != 5 && args.length != 6) {
			System.out.println("Error: Incorrect number of arguments.");
			System.out.println("Searches for enriched modules and their connections.");
			System.out.println("p1: Physical network");
			System.out.println("p2: Genetic network");
			System.out.println("p3: Output folder");
			System.out.println("p4: Alpha");
			System.out.println("p5: Alpha Multiplier");
			System.out.println("p6: Physical network filter degree (optional)");
			System.exit(0);
		}

		String fpnet = args[0];
		String fgnet = args[1];
		String outputDirectory = args[2] + "/";
		float alpha = Float.valueOf(args[4]);
		float alpham = Float.valueOf(args[5]);

		int filter = -1;
		if (args.length == 6)
			filter = Integer.valueOf(args[6]);

		System.out.println("Make sure networks are unique and not self!");

		// Read in networks and convert to type <String, Double>
		System.out.println("Reading networks.");
		SFNetwork pnet = new FloatHashNetwork(fpnet, false, true, 0, 1, 2);
		SFNetwork gnet = new FloatHashNetwork(fgnet, false, false, 0, 1, 2);

		// Trim all the nodes in the genetic network to ones found in the
		// physical network.
		System.out.println("Trimming genetic network.");
		Set<String> pnodes = pnet.getNodes();
		Set<String> gnodes = gnet.getNodes();
		pnodes.retainAll(gnodes);
		gnet = gnet.subNetwork(pnodes);

		// Optionally filter physical nodes to ones near genetic nodes
		if (filter != -1) {
			System.out.println("Trimming physical network. Filter degree "
					+ filter);
			TypedLinkNetwork<String, Float> ptlnet = pnet.asTypedLinkNetwork();

			pnet = new FloatHashNetwork(ptlnet.subNetwork(pnodes, filter));
		}

		// System.out.println("Number of nodes remaining in the physical network: "+pnet.numNodes());
		System.out.println("Number of edges remaining in the physical network: " + pnet.numEdges());
		// System.out.println("Number of nodes remaining in the genetic network: "+gnet.numNodes());
		System.out.println("Number of edges remaining in the genetic network: " + gnet.numEdges());

		// Select a scoring function
		HCScoringFunction sfunc = new SouravScore(pnet, gnet, alpha, alpham);
		System.out.println("Initializing scoring function.");
		sfunc.Initialize(pnet, gnet);

		// Perform network search
		System.out.println("Performing network search.");
		TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge> results = HCSearch2.search(pnet, gnet, sfunc);

		System.out.println("Time Elapsed: " + (System.currentTimeMillis() - time) / 1000);

		// Print a simple summary of the results
		HCSearch2.report(results);

		// Create the output directory
		File f = new File(outputDirectory);
		if (!f.exists())
			f.mkdir();

		// Build a table of complex properties and save it and the complexes
		// themselves.
		System.out.println("Calculating complex properties.");
		Set<String> testedgenes = pnet.getNodes();

		Map<String, SNodeModule> genecomplexes = new HashMap<String, SNodeModule>(testedgenes.size());

		StringTable cprops = new StringTable(results.numNodes(), 4, "");
		List<String> colnames = new ArrayList<String>(5);
		colnames.add("CID");
		colnames.add("Size");
		colnames.add("Radius");
		colnames.add("Score");
		cprops.setColNames(colnames);

		StringTable gprops = new StringTable(testedgenes.size(), 2, "");
		colnames = new ArrayList<String>(3);
		colnames.add("ORF");
		colnames.add("CID");
		gprops.setColNames(colnames);

		List<SNodeModule> clist = new ArrayList<SNodeModule>(results.numNodes());
		Map<TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge>, SNodeModule> cmap = new HashMap<TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge>, SNodeModule>();

		int i = 0;
		for (TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge> mod : results.nodeIterator()) {
			String id = String.valueOf(i);
			Set<String> genes = mod.value().getMemberValues();
			SNodeModule newc = new SNodeModule(id, genes);
			clist.add(newc);
			cmap.put(mod, newc);

			for (String gene : genes)
				genecomplexes.put(gene, newc);

			cprops.set(i, 0, id);
			cprops.set(i, 1, String.valueOf(genes.size()));
			cprops.set(i, 2, String.valueOf(Math.sqrt(genes.size() / Math.PI)));
			cprops.set(i, 3, mod.value().score());

			i++;
		}
		SNodeModule.saveComplexes(outputDirectory + "complexes.txt", clist);

		cprops.SortRowsAsDouble(1);
		cprops.save(outputDirectory + "cprops.txt");

		// Save the complex network (note, this needs to be done AFTER the
		// complex properties have completed)
		// results.saveToFile(outputDirectory+"cnet.txt");

		// Save gene properties
		System.out.println("Saving properties for " + testedgenes.size() + " genes.");
		i = 0;
		for (String gene : testedgenes) {
			gprops.set(i, 0, gene);
			SNodeModule parent = genecomplexes.get(gene);
			gprops.set(i, 1, parent.getID());
			i++;
		}

		gprops.save(outputDirectory + "gprops.txt");

		// Save each edge and build the edge properties file
		System.out.println("Calculating edge properties.");
		TypedLinkNetwork<String, Float> pn = pnet.asTypedLinkNetwork();
		TypedLinkNetwork<String, Float> gn = gnet.asTypedLinkNetwork();

		if (results.numEdges() > 0) {
			StringTable eprops = new StringTable(results.numEdges(), 7, "");
			colnames = new ArrayList<String>(7);
			colnames.add("CID1");
			colnames.add("CID2");
			colnames.add("Score");
			colnames.add("#Genetic");
			colnames.add("#Physical");
			colnames.add("C1Size");
			colnames.add("C2Size");
			colnames.add("Density");
			eprops.setColNames(colnames);

			i = 0;
			for (TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> edge : results.edgeIterator()) {
				TypedLinkNodeModule<String, BFEdge> hm1 = edge.source().value();
				TypedLinkNodeModule<String, BFEdge> hm2 = edge.target().value();

				// The modules are not built from the gn and pn networks, so
				// their member nodes have no connectivity information!
				int gconnectedness = gn.getConnectedness(hm1.asStringSet(), hm2.asStringSet());
				int pconnectedness = pn.getConnectedness(hm1.asStringSet(), hm2.asStringSet());

				eprops.set(i, 0, cmap.get(edge.source()).getID());
				eprops.set(i, 1, cmap.get(edge.target()).getID());
				eprops.set(i, 2, String.valueOf(edge.value().link()));
				eprops.set(i, 3, String.valueOf(gconnectedness));
				eprops.set(i, 4, String.valueOf(pconnectedness));
				eprops.set(i, 5, String.valueOf(hm1.size()));
				eprops.set(i, 6, String.valueOf(hm2.size()));
				eprops.set(i, 7, String.valueOf(edge.value().link() / hm1.size() / hm2.size()));

				i++;
			}

			eprops.SortRowsAsDouble(2);

			eprops.save(outputDirectory + "eprops.txt");
		}

		System.out.println("Time Elapsed: " + (System.currentTimeMillis() - time) / 1000);
	}
}