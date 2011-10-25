package org.idekerlab.PanGIAPlugin.ModFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.idekerlab.PanGIAPlugin.SearchTask;
import org.idekerlab.PanGIAPlugin.data.DoubleVector;
import org.idekerlab.PanGIAPlugin.networks.SFEdge;
import org.idekerlab.PanGIAPlugin.networks.SFNetwork;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.TypedLinkEdge;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.TypedLinkNetwork;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.TypedLinkNode;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.TypedLinkNodeModule;
import org.idekerlab.PanGIAPlugin.utilities.MemoryReporter;
import org.idekerlab.PanGIAPlugin.utilities.NumberFormatter;
import org.idekerlab.PanGIAPlugin.utilities.ThreadPriorityFactory;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;


public class HCSearch2 {

	/**
	 *  @param percentAllocated up to what point to advance the task monitor progress bar
	 */
	public static TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge> search(
		SFNetwork pnet, SFNetwork gnet, HCScoringFunction sfunc, final TaskMonitor taskMonitor,
		final float percentAllocated, SearchTask parentTask)
	{
		// The scoring function needs to load several lookup matricies for the
		// network data.
		System.gc();
		MemoryReporter.reportMemoryUsage();

		// Need to construct the ONetwork<HyperModule<String>,BFEdge> object.
		taskMonitor.setStatus("1. Building merged network.");

		TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge> results = constructBaseNetwork(
				pnet, gnet);
		
		if (parentTask.needsToHalt()) return null;

		System.gc();
		MemoryReporter.reportMemoryUsage();

		// Get the first-pass scores
		taskMonitor.setStatus("2. Obtaining primary scores.");
		computePrimaryScores(results, sfunc);

		if (parentTask.needsToHalt()) return null;
		
		System.gc();
		MemoryReporter.reportMemoryUsage();

		// Merge best tree-pairs together
		taskMonitor.setStatus("3. Forming clusters...");

		// MemoryReporter.reportMemoryUsage();

		DoubleVector global_scores = new DoubleVector(3000);
		global_scores.add(0);
		float globalscore = 0;
		float max = 1; // Tracks the best improvement in globalscore.

		System.gc();

		int iter = 0;
		final float INITIAL_NODE_COUNT = results.numNodes();
		while (results.numEdges() > 0 && max > 0) {
			if (iter % 1000 == 0) {
				System.gc();
				MemoryReporter.reportMemoryUsage();
			}
			
			if (parentTask.needsToHalt()) return null;
			
			// Identify the best physical edge to merge
			Iterator<TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge>> edgei = results
					.edgeIterator().iterator();
			TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> best = edgei
					.next();

			while (edgei.hasNext()
					&& !best.value().isType(BFEdge.InteractionType.Physical))
				best = edgei.next();

			if (!best.value().isType(BFEdge.InteractionType.Physical))
				break;

			BFEdge first = best.value();
			max = first.global();

			TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> newEdge = best;
			while (edgei.hasNext()) {
				newEdge = edgei.next();
				BFEdge nextedge = newEdge.value();

				if (nextedge.isType(BFEdge.InteractionType.Physical)) {
					float newscore = nextedge.global();
					if (newscore > max) {
						best = newEdge;
						max = nextedge.global();
					}
				}
			}

			if (max <= 0)
				break;

			// Merge the best pair
			//System.out.println(best.source()+", "+best.target());
			
			TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge> mergedNode = mergeNodes(
					results, best.source(), best.target(), sfunc);
			mergedNode.value().setScore(best.value().complexMerge());

			//if (mergedNode.value().contains("AT2G25930"))
			//		System.out.println(mergedNode.value());
			
			// Check to see if the search should continue
			if (max > 0) {
				// Recalculate linkMerge scores

				ExecutorService exec = Executors.newCachedThreadPool(new ThreadPriorityFactory(
								Thread.MIN_PRIORITY));

				// for (TypedLinkEdge<TypedLinkNodeModule<String,BFEdge>,BFEdge>
				// e : mergedNode.edgeIterator())
				// if (e.value().isType(BFEdge.InteractionType.Physical))
				// assignMergeLinkScore(e);
				for (TypedLinkEdge<TypedLinkNodeModule<String,BFEdge>,BFEdge> e : mergedNode.edgeIterator())
					if (e.value().isType(BFEdge.InteractionType.Physical))
						for (TypedLinkEdge<TypedLinkNodeModule<String,BFEdge>,BFEdge> e2 : e.opposite(mergedNode).edgeIterator())
							exec.execute(new MergeLinkRunner(e2));
				
				try {
					exec.shutdown();
					exec.awaitTermination(30*86400, TimeUnit.SECONDS);
					if (!exec.isTerminated())
						System.out.println("Did not fully terminate!");
				} catch (InterruptedException e) {
					System.out.println(e);
					exec.shutdownNow();
				}
                
				//Re-calculate global scores
				for (TypedLinkEdge<TypedLinkNodeModule<String,BFEdge>,BFEdge> e : mergedNode.edgeIterator())
					for (TypedLinkEdge<TypedLinkNodeModule<String,BFEdge>,BFEdge> e2 : e.opposite(mergedNode).edgeIterator())
						computeGlobalScore(results,e2,sfunc);
		
				globalscore += max;
				global_scores.add(globalscore);

				DoubleVector csizes = new DoubleVector(results.numNodes());

				Set<TypedLinkNodeModule<String, BFEdge>> allc = results.getNodeValues();

				for (TypedLinkNodeModule<String, BFEdge> m : allc)
					csizes.add(m.size());
				final int percentCompleted = Math.round((INITIAL_NODE_COUNT - results.numNodes()) * percentAllocated / INITIAL_NODE_COUNT);
				taskMonitor.setPercentCompleted(percentCompleted);
				taskMonitor.setStatus("3. Forming clusters (# of clusters: "
				                      + results.numNodes() + ", largest cluster size: "
						      + csizes.max(false) + ")");
			}

			iter++;
		}

		taskMonitor.setPercentCompleted(Math.round(percentAllocated));
		System.out.println("Best score: " + global_scores.max(true));
		System.out.println("Best score index: " + global_scores.maxI());
		System.out.println("Number of edges before filtering: "+results.numEdges());

		return results;
	}

	/**
	 * Constructs the base network for performing the network search. This
	 * network contains both physical and genetic interactions.
	 * 
	 * @param pnet
	 *            Physical network
	 * @param gnet
	 *            Genetic network
	 */
	public static TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge> constructBaseNetwork(
			SFNetwork pnet, SFNetwork gnet) {
		// Set each node in the physical network as a module
		TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge> results = new TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge>(
				false, false);

		for (String pn : pnet.nodeIterator())
			results.addNode(new TypedLinkNodeModule<String, BFEdge>(
					new TypedLinkNode<String, BFEdge>(pn)));

		// Add each physical edge
		System.out.println("->Setting physical interactions.");

		for (SFEdge pe : pnet.edgeIterator()) {
			BFEdge anedge = new BFEdge(BFEdge.InteractionType.Physical);
			results.addEdgeWNodeUpdate(new TypedLinkNodeModule<String, BFEdge>(
					pe.getI1()), new TypedLinkNodeModule<String, BFEdge>(pe
					.getI2()), anedge);
		}

		// Add each genetic edge
		// CHANGE MAP TO WORK FOR ALL GENES, NOT JUST GENETIC OR PHYSICAL
		/*
		 * System.out.println("->->Building translation map.");
		 * Map<String,TypedLinkNodeModule<String,BFEdge>> gene_rnode = new
		 * HashMap<String,TypedLinkNodeModule<String,BFEdge>>(gnet.numNodes());
		 * 
		 * for (TypedLinkNode<String,Float> gn : gnet.nodes())
		 * gene_rnode.put(gn.value(), results.getNode(new
		 * TypedLinkNodeModule<String,BFEdge>(gn.value())).value());
		 */

		System.out.println("->Adding genetic interactions.");
		int counter = 0;

		for (SFEdge ge : gnet.edgeIterator()) {
			counter++;
			if (counter % 100000 == 0)
				System.out.println("->->" + counter + "/" + gnet.numEdges());

			TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge> source = results
					.getNode(new TypedLinkNodeModule<String, BFEdge>(ge.getI1()));
			TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge> target = results
					.getNode(new TypedLinkNodeModule<String, BFEdge>(ge.getI2()));

			if (source != null && target != null) {
				TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> existingEdge = results
						.getEdge(source.value(), target.value());

				if (existingEdge != null)
					existingEdge.value()
							.addType(BFEdge.InteractionType.Genetic);
				else {
					BFEdge anedge = new BFEdge(BFEdge.InteractionType.Genetic);
					results.addEdgeWNodeUpdate(source, target, anedge);
				}
			}
		}

		return results;
	}

	public static void computePrimaryScores(
			TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge> results,
			HCScoringFunction sfunc) {
		// Assign link scores
		for (TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> ed : results
				.edgeIterator())
			ed.value().setLink(
					sfunc.getBetweenScore(ed.source().value(), ed.target()
							.value())); // Put source and targets into modules?

		// Assign merge scores
		ExecutorService exec = Executors
				.newCachedThreadPool(new ThreadPriorityFactory(Thread.MIN_PRIORITY));
		int count = 0;
		for (TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> ed : results
				.edgeIterator())
			if (ed.value().isType(BFEdge.InteractionType.Physical)) {
				if (count % 100000 == 0) {
					System.gc();
					MemoryReporter.reportMemoryUsage();
				}

				setComplexMerge(sfunc, ed.source(), ed.target(), ed.value());
				exec.execute(new MergeLinkRunner(ed));
				count++;
			}

		try {
			exec.shutdown();
			exec.awaitTermination(3000000, TimeUnit.SECONDS);
			if (!exec.isTerminated())
				System.out.println("Did not fully terminate!");
		} catch (InterruptedException e) {
			System.out.println(e);
			exec.shutdownNow();
		}

		// Compute global merging values
		for (TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> ed : results
				.edgeIterator())
			computeGlobalScore(results, ed, sfunc);
	}

	public static void computeGlobalScore(
			TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge> results,
			TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> edge,
			HCScoringFunction sfunc) {
		// HyperModule<String> m1 =
		// results.getNodeValue(results.getEdgeSource(edgeIndex));
		// HyperModule<String> m2 =
		// results.getNodeValue(results.getEdgeTarget(edgeIndex));

		// BFEdge edge = results.getEdgeValue(edgeIndex);
		// edge.setGlobal(edge.complexMerge()+edge.linkMerge()+sfunc.complexReward(m1.size()+m2.size())-sfunc.complexReward(m1.size())-sfunc.complexReward(m2.size()));
		edge.value().setGlobal(
				edge.value().complexMerge() + edge.value().linkMerge());
	}

	private static TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge> mergeNodes(
			TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge> results,
			TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge> n1,
			TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge> n2,
			HCScoringFunction sfunc) {
		TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge> mergedNode = new TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge>(
				TypedLinkNodeModule.union(n1.value(), n2.value()));
		results.addNode(mergedNode);

		for (TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> oldEdge : n1
				.edgeIterator()) {
			if (oldEdge.target() != n1 && oldEdge.target() != n2)
				results.addEdgeWNodeUpdate(mergedNode, oldEdge.target(),
						oldEdge.value());
			else if (oldEdge.source() != n1 && oldEdge.source() != n2)
				results.addEdgeWNodeUpdate(mergedNode, oldEdge.source(),
						oldEdge.value());
		}

		List<TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge>> addEdges = new ArrayList<TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge>>();
		List<TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge>> removeEdges = new ArrayList<TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge>>();

		for (TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> oldEdge : n2
				.edgeIterator()) {
			TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> existingEdge = results
					.getEdge(mergedNode.value(), oldEdge.opposite(n2).value());

			BFEdge newEdge;

			if (existingEdge == null)
				newEdge = oldEdge.value();
			else {
				// Merge two edges
				newEdge = new BFEdge();
				newEdge.addType(existingEdge.value().getTypes());
				newEdge.addType(oldEdge.value().getTypes());

				newEdge.setLink(existingEdge.value().link()
						+ oldEdge.value().link());
				// newedge.setComplexMerge(oldedge1.complexMerge()+oldedge2.complexMerge());

				if (newEdge.isType(BFEdge.InteractionType.Physical))
					setComplexMerge(sfunc, existingEdge.source(), existingEdge
							.target(), newEdge);
			}

			if (oldEdge.target() != n1 && oldEdge.target() != n2) {
				if (existingEdge != null)
					removeEdges.add(results.getEdge(mergedNode.value(), oldEdge
							.target().value()));
				addEdges
						.add(new TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge>(
								mergedNode, oldEdge.target(), newEdge, false));
			} else if (oldEdge.source() != n1 && oldEdge.source() != n2) {
				if (existingEdge != null)
					removeEdges.add(results.getEdge(mergedNode.value(), oldEdge
							.source().value()));
				addEdges
						.add(new TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge>(
								mergedNode, oldEdge.source(), newEdge, false));
			}
		}

		results.removeAllEdges(removeEdges);
		results.addAllEdgesWNodeUpdate(addEdges);

		results.removeNode(n2);
		results.removeNode(n1);

		return mergedNode;
	}

	public static String report(
			TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge> results) {

		final StringBuilder builder = new StringBuilder();

		DoubleVector cscores = new DoubleVector(results.numNodes());
		DoubleVector csizes = new DoubleVector(results.numNodes());

		for (TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge> m : results
				.nodeIterator()) {
			cscores.add(m.value().score());
			csizes.add(m.value().size());
		}

		builder.append("Best cluster score: " + NumberFormatter.formatNumber(cscores.max(false),3) + "\n");
		builder.append("Worst cluster score: " + NumberFormatter.formatNumber(cscores.min(false),3) + "\n");
		builder.append("Largest cluster size: " + (int)csizes.max(false) + "\n");

		DoubleVector escores = new DoubleVector(results.numEdges());
		for (TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> ed : results
				.edges()) {
			float score = ed.value().link();
			if (score != -1)
				escores.add(score);
		}

		builder.append("Best edge score: " + NumberFormatter.formatNumber(escores.max(false),3) + "\n");

		// csizes.plothist(30);

		System.out.println(builder.toString());

		return builder.toString();
	}

	protected static void assignMergeLinkScore(
			TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> ed) {
		TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge> n1 = ed
				.source();
		TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge> n2 = ed
				.target();

		Map<TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge>, Float> linkScores = new HashMap<TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge>, Float>(
				n1.numNeighbors() + n2.numNeighbors(), 1);

		for (TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> oedge : ed
				.edgeNeighbors()) {
			TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge> onode;
			if (oedge.source() == n1 || oedge.source() == n2)
				onode = oedge.target();
			else
				onode = oedge.source();

			if (!linkScores.containsKey(onode))
				linkScores.put(onode, oedge.value().link());
			else
				linkScores.put(onode, linkScores.get(onode)
						+ oedge.value().link());
		}

		// ****Need to calculate together / apart (not! before/after). Old
		// information should be completely unused.
		// Together has already been done. = 4
		// Separate needs to be done: = 8 (4-8=-4)

		// Sum all links to node1
		float separateScore = 0;
		for (TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> n1ed : n1
				.edgeIterator())
			// separateScore += n1ed.value().link();
			separateScore += Math.max(0, n1ed.value().link());

		// Sum all links to node2 (which do not point at node1)
		for (TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> n2ed : n2
				.edgeIterator()) {
			if (n2ed.source() == n1)
				continue;
			if (n2ed.target() == n1)
				continue;

			// separateScore += n2ed.value().link();
			separateScore += Math.max(0, n2ed.value().link());
		}

		float sumPositive = 0;
		for (Float f : linkScores.values())
			if (f > 0)
				sumPositive += f;

		ed.value().setLinkMerge(sumPositive - separateScore);
		// ed.value().setLinkMerge(ed.value().sumLinkScores()-separateScore);
	}

	private static void setComplexMerge(HCScoringFunction sfunc,
			TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge> n1,
			TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge> n2,
			BFEdge edge) {
		double c1score = sfunc.getWithinScore(n1.value());
		double c2score = sfunc.getWithinScore(n2.value());

		double combinedScore = sfunc.getWithinScore(TypedLinkNodeModule.union(
				n1.value(), n2.value()));

		edge.setComplexMerge((float) (combinedScore - c1score - c2score));
	}
}
