package mTree;

import giny.model.Node;

import java.util.ArrayList;
import java.util.Random;

public class MTree {
	
	public static MTree root;
	
	private MTreeNode parent;
	
	private boolean isLeaf = true;
	
	private boolean isRoot = false;
	
	private ArrayList<MTreeNode> entries;
	
	private final int maxSize = 30;
	
	public MTree(){
		entries = new ArrayList<MTreeNode>();
	}
	
	public void insert(MTreeNode n){
		if(!isLeaf){
			double min = Double.MAX_VALUE;
			MTreeNode best = null;
			double distance;
			for(MTreeNode c : entries){
				distance = n.distance(c);  
				if(distance < min){
					best = c;
					min = distance;
				}
			}
			if(min > best.getCoveringRadius()){
					best.setCoveringRadius(min);
			}
			best.getSubtree().insert(n);
		}
		else{
			//System.out.println("entryjä " + entries.size());
			//is leaf
			if(entries.size() < maxSize){
				//not full
				entries.add(n);
				n.setContainer(this);
				if(!isRoot){
					n.setParentDistance(n.distance(parent));
				}
			}
			else{
				//full
				split(n);
			}
		}
	}
	
	private void split(MTreeNode n){
		ArrayList<MTreeNode> overfull = getEntries();
		overfull.add(n);
		
		//we need a new subtree
		MTree newTree = new MTree();
		if(!isLeaf) newTree.setLeaf(false);
		
		//we partition nodes in overfull into these lists
		ArrayList<MTreeNode> n1 = new ArrayList<MTreeNode>();
		ArrayList<MTreeNode> n2 = new ArrayList<MTreeNode>();
		
		//promote two random nodes
		Random rand = new Random();
		MTreeNode op1 = overfull.get(rand.nextInt(overfull.size()));
		MTreeNode op2 = null;
		while(op2 == null){
			MTreeNode temp = overfull.get(rand.nextInt(overfull.size()));
			if(op1 != temp){
				op2 = temp;
			}
		}
		op1 = new MTreeNode(op1.getGraphNode());
		op2 = new MTreeNode(op2.getGraphNode());
		
//		//alternative method 1: promote one random node and another with maximum distance from the first 
//		Random rand = new Random();
//		int prom1;
//		prom1 = rand.nextInt(overfull.size());
//		
//		MTreeNode op1 = new MTreeNode(overfull.get(prom1).getGraphNode());
//		MTreeNode op2 = null;
//		double max = Double.MIN_VALUE;
//		double dist;
//		for(MTreeNode m : overfull){
//			if(m!=op1){
//				dist = m.distance(op1); 
//				if(dist > max){
//					op2 = m;
//					max = dist;
//				}
//			}
//		}
//		op2 = new MTreeNode(op2.getGraphNode());
		
//		//alternative method 2: promote two nodes from a sample set with minimized sum of covering radii
//		ArrayList<MTreeNode> cands = new ArrayList<MTreeNode>();
//		Random rand = new Random();
//		while(cands.size() < 5){
//			MTreeNode temp = overfull.get(rand.nextInt(overfull.size()));
//			if(!cands.contains(temp)){
//				cands.add(temp);
//			}
//		}
//		MTreeNode op1 = null; 
//		MTreeNode op2 = null;
//		double minCovSum = Double.MAX_VALUE;
//		for(MTreeNode u : cands){
//			for(MTreeNode v : cands){
//				if(u != v){
//					double covU = 0;
//					double covV = 0;
//					for(MTreeNode t : overfull){
//						double dis1 = t.distance(u);
//						double dis2 = t.distance(v);
//						if(dis1 < dis2){
//							if(dis1 > covU){
//								covU = dis1;
//							}
//						}
//						else{
//							if(dis2 > covV){
//								covV = dis1;
//							}
//						}
//					}
//					if(covU + covV < minCovSum){
//						minCovSum = covU + covV;
//						op1 = u;
//						op2 = v;
//					}
//				}
//			}
//		}
//		op1 = new MTreeNode(op1.getGraphNode());
//		op2 = new MTreeNode(op2.getGraphNode());
		
		//partition
		for(MTreeNode m : overfull){
			double dis1 = m.distance(op1);
			double dis2 = m.distance(op2);
			if(dis1 < dis2){
				if(dis1 > op1.getCoveringRadius()){
					op1.setCoveringRadius(dis1);
				}
				n1.add(m);
				m.setContainer(this);
				m.setParentDistance(dis1);
			}
			else{
				if(dis2 > op2.getCoveringRadius()){
					op2.setCoveringRadius(dis2);
				}
				n2.add(m);
				m.setContainer(newTree);
				m.setParentDistance(dis2);
			}
		}
		
		//store nodes
		setEntries(n1);
		newTree.setEntries(n2);
		
		//update subtrees
		op1.setSubtree(this);
		op2.setSubtree(newTree);
		
		if(isRoot){
			//root splitted, need a new root
			MTree newRoot = new MTree();
			MTree.root = newRoot;
			newRoot.setLeaf(false);
			newRoot.setRoot(true);
			newRoot.addEntry(op1);
			newRoot.addEntry(op2);
			op1.setContainer(newRoot);
			op2.setContainer(newRoot);
		}
		else{
			//System.out.println("non-root splitted");
			//non-root node splitted
			MTree parCont = parent.getContainer();
			parCont.replaceEntry(parent, op1);
			op1.setContainer(parCont);
			if(!parCont.isRoot){
				op1.setParentDistance(op1.distance(parCont.parent));
			}
			if(parent.getContainer().getEntries().size() >= maxSize){
				//split propagates
				parent.getContainer().split(op2);
			}
			else{
				parCont.addEntry(op2);
				op2.setContainer(parCont);
				if(!parCont.isRoot()){
					op2.setParentDistance(op2.distance(parCont.parent));
				}
			}
		}
		parent = op1;
		isRoot = false;
		newTree.setParent(op2);
	}
	
	public void getRange(MTreeNode q, double radius, ArrayList<Node> result, double pqDist) {
		if (!isLeaf){
			for(MTreeNode n : entries){
				if(Math.abs(pqDist - n.getParentDistance()) <= radius + n.getCoveringRadius()){
					double dist = n.distance(q);
					if(dist <= radius + n.getCoveringRadius()){
						n.getSubtree().getRange(q, radius, result, dist);
					}
				}
			}
		}
		else {
			for(MTreeNode n: entries){
				if(Math.abs(pqDist - n.getParentDistance()) <= radius){
					double dist = n.distance(q);
					if(dist <= radius){
						result.add(n.getGraphNode());
					}
				}
			}
		}
	}
	
	public ArrayList<MTreeNode> getEntries(){
		return entries;
	}
	
	public MTreeNode getParent(){
		return parent;
	}
	
	public boolean isRoot(){
		return isRoot;
	}
	
	
	public void setEntries(ArrayList<MTreeNode> l){
		entries = l;
	}
	
	public void setRoot(boolean b){
		isRoot = b;
	}
	
	public void setLeaf(boolean b){
		isLeaf = b;
	}
	
	public void setParent(MTreeNode m){
		parent = m;
	}
	
	public void addEntry(MTreeNode n){
		entries.add(n);
	}
	
	//used in split
	public void replaceEntry(MTreeNode oldOne, MTreeNode newOne){
		entries.remove(oldOne);
		entries.add(newOne);
	}
	
}
