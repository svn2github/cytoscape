package de.layclust.layout.acc;

import java.util.Iterator;
import java.util.Vector;

import de.layclust.layout.IParameters;
import de.layclust.taskmanaging.TaskConfig;
import de.layclust.datastructure.ConnectedComponent;
import de.layclust.datastructure.ICCEdges;

public class StackPlayground implements IPlayground{
	private Vector [][] playground2D;
	private Vector [][][] playground3D;
	private Object playgroundnD;
	private ConnectedComponent cc;
	private ACCParameters parameters;
	private int dimension;
	private int noNodes;
	private int size;
	private Vector ants;
	private ICCEdges ccEdges;
	private float iterationquotient;
	
	//value for the slope of the e-function that is used to compute wakeupprobability
	//higher value means that it is very strict (highly similar objects stay together, dissimilar 
	//objects are seperated). 
	private double m;
	//y-axis-intercept of the e-function that is used to compute wakeupprobability. lower 
	//values mean a higher basic-wakeupprobability.
	private double n;
	
	private double normthresh;
	
	//TODO:maybe add these parameters to ACCParameters?
	private int gridmultiplicator = 1;
	private int noOfIterations;
	private int mergeOps = 40;
	
	public StackPlayground(ConnectedComponent cc, IParameters parameters) {
		this.cc = cc;
		this.ccEdges = cc.getCCEdges();
		ccEdges.normaliseWithThreshold(((ACCParameters)parameters).getNormaliseThreshold());
		//ccEdges.normalise();
		this.parameters = (ACCParameters) parameters;
		this.dimension = TaskConfig.dimension;
		this.noNodes = cc.getNodeNumber();
		this.size = (int) Math.ceil(Math.pow(gridmultiplicator*noNodes, 1.0/dimension));
		if(dimension > 3) {
			System.err.println("Dimension larger then 3 not implemented for SleepingAnts.");
			System.exit(1);
		}
		this.noOfIterations = ((ACCParameters) parameters).getSa_iterations();
		this.m = ((ACCParameters) parameters).getSa_m();
		this.n = ((ACCParameters) parameters).getSa_n();
		initialisePlayground();
		initialiseAnts();
	}
	
	private void initialisePlayground(){
		if(dimension == 2) {
			playground2D = new Vector[size][size];
			for(int i = 0; i < playground2D.length; i++) {
				for(int j = 0; j < playground2D[i].length; j++) {
					playground2D[i][j] = new Vector();
				}
			}
		} else if (dimension == 3) {
			playground3D = new Vector[size][size][size];
			for(int i = 0; i < playground3D.length; i++) {
				for(int j = 0; j < playground3D[i].length; j++) {
					for(int k = 0; k < playground3D[i][j].length; k++) {
						playground3D[i][j][k] = new Vector();
					}
				}
			}
		}
	}
	
	private void initialiseAnts(){
		ants = new Vector();
		int[] position = new int[dimension];
		for(int i = 0; i < position.length; i++) {
			position[i] = 0;
		}
		for(int i = 0; i < noNodes; i++) {
			int[] pos = new int[dimension];
			for (int j = 0; j < pos.length; j++) {
				pos[j] = position[j];
			}
			ISAnt ant = new SleepingAnt(pos, i, this);
			setAntToPosition(ant, pos);
			ants.add(ant);
			position[dimension-1]++;
			for(int j = dimension-1; j > 0; j--) {
				if(position[j] >= size) {
					position[j-1]++;
					position[j] = 0;
				}
			}
		}
	}

	public Vector getPosition(int[] position) {
		if(dimension == 2) {
			return playground2D[position[0]][position[1]];
		} else {
			return playground3D[position[0]][position[1]][position[2]];
		}
	}
	
	public boolean removeAnt(ISAnt ant, int[] position) {

		boolean ret;
		if(dimension == 2) {
			ret = playground2D[position[0]][position[1]].remove(ant);
			
		} else {
			ret = playground3D[position[0]][position[1]][position[2]].remove(ant);
		}
		return ret;
	}
	
	public void setAntToPosition(ISAnt ant, int[] position) {
		if(dimension == 2) {
			playground2D[position[0]][position[1]].add(ant);
		} else {
			playground3D[position[0]][position[1]][position[2]].add(ant);
		}
	}
	
	public void run() {
		int iterationsForMerge = (int) ((1.0/2)* (((float)noOfIterations) / mergeOps));
		int halfIterations = noOfIterations / 2;
		//System.out.println(halfIterations+" / "+iterationsForMerge);
		for(int j = 0; j < noOfIterations; j++) {
			this.iterationquotient =(float) j / noOfIterations;
			if(j > halfIterations && j % iterationsForMerge == 0) {
				//System.out.println(j);
				Vector removed = new Vector();
				Vector add = new Vector();
				for (Iterator iterator = ants.iterator(); iterator.hasNext();) {
					ISAnt ant = (ISAnt) iterator.next();
					if(ant instanceof GroupOfAnts) {
						Vector demerged = tryToDemerge((GroupOfAnts)ant);
						if(demerged != null) {
							removed.add(ant);
							for (Iterator iterator2 = demerged.iterator(); iterator2.hasNext();) {
								ISAnt object = (ISAnt) iterator2.next();
								add.add(object);
							}
						}
					}
				}
				int removedAntz = 0;
				int addedAntz = add.size();
				for (Iterator iterator = removed.iterator(); iterator.hasNext();) {
					ISAnt object = (ISAnt) iterator.next();
					ants.remove(object);
				}
				for (Iterator iterator = add.iterator(); iterator.hasNext();) {
					ISAnt object = (ISAnt) iterator.next();
					ants.add(object);
					//System.out.println("Nachher: "+ants.size());
				}
				if(dimension == 2) {
					for(int a = 0; a < playground2D.length; a++) {
						for(int b = 0; b < playground2D[a].length; b++) {
							int[]p = {a,b};
							tryToMerge(p);
						}
					}
				}	else  {
					for(int a = 0; a < playground2D.length; a++) {
						for(int b = 0; b < playground2D[a].length; b++) {
							for(int c = 0; c < playground2D[b].length; c++) {
								int[]p = {a,b,c};
								tryToMerge(p);
							}
						}
					}
				}

			}
			for(int i = 0; i < ants.size(); i++) {
				((ISAnt)ants.get(i)).makeStep();

			}
		}
		deMergeAllGroups();
		cc.getCCEdges().denormaliseWithThreshold();
	}

	public int getDimension() {
		return dimension;
	}

	public int getSize() {
		return size;
	}
	
	public float getSimilarity(ISAnt a, ISAnt b) {
		if(a instanceof GroupOfAnts && b instanceof GroupOfAnts) {
			return ((GroupOfAnts) a).computeSimilarity((GroupOfAnts)b);
		}else if(a instanceof GroupOfAnts) {
			return ((GroupOfAnts) a).computeSimilarity((SleepingAnt)b);
		} else if (b instanceof GroupOfAnts) {
			return ((GroupOfAnts) b).computeSimilarity((SleepingAnt)a);
		} else {
			return this.ccEdges.getEdgeCost(((SleepingAnt)a).getId(), ((SleepingAnt)b).getId());
		}
	}
	
	private void tryToMerge(int[] pos) {
		Vector items = getPosition(pos);
		if(items.size() <= 1) return;
		float simsum = 0;
		for (int i = 0; i < items.size(); i++) {
			for(int j = i+1; j < items.size(); j++) {
				simsum += getSimilarity((ISAnt)items.get(i), (ISAnt)items.get(j));
			}
		}
		double x = 1.0/2 * items.size() * (items.size()-1);
		simsum = (float) (simsum / x);
		if(Math.random() < simsum) {
			GroupOfAnts group = new GroupOfAnts((Vector)items.clone(),pos,this);
			for(int i = 0; i < items.size(); i++) {
				ants.remove(items.get(i));
			}
			ants.add(group);
			items.removeAllElements();
			items.add(group);
		}
	}
	
	private Vector tryToDemerge(GroupOfAnts group) {
		Vector items = group.getAnts();
		float simsum = 0;
		for (int i = 0; i < items.size(); i++) {
			for(int j = i+1; j < items.size(); j++) {
				simsum += getSimilarity((ISAnt)items.get(i), (ISAnt)items.get(j));
			}
		}
		double x = 1.0/2 * items.size() * (items.size()-1);
		simsum = (float) (simsum / x);
		simsum = 1 - simsum;
		Vector addnew = null;
		if(Math.random() < simsum) {
			addnew = new Vector();
			int[] pos = group.getPosition();
			Vector vector = getPosition(pos);
			boolean wech = vector.remove(group);
//			if (!wech) {
//				//System.out.println("Exception!!!!!!!!!! "+(group.getPosition())[0]+"/"+(group.getPosition())[1]);
//			//	removeGroup(group);
//			}
			for (Iterator iterator = items.iterator(); iterator.hasNext();) {
				ISAnt object = (ISAnt) iterator.next();
				object.setPosition(pos);
				vector.add(object);
				addnew.add(object);
//				if(object instanceof SleepingAnt) {
//					int it = (int) (noOfIterations * iterationquotient);
//					System.out.println("Demerged ant no "+((SleepingAnt)object).getId()+"///"+it);
//				} else {
//					System.out.println("Demerged a group");
//				}
			}
		}

		return addnew;
	}
	
	private boolean checkConsis() {
		Vector allAnts = new Vector();
		boolean posAlarm = false;
		boolean doubleAlarm = false;
		for(int i = 0; i < playground2D.length; i++) {
			for(int j = 0; j < playground2D[i].length; j++) {
				int[] pos = {i,j};
				Vector antz = getPosition(pos);
				for (Iterator iterator = antz.iterator(); iterator.hasNext();) {
					ISAnt object = (ISAnt) iterator.next();
					int[] pos2 = object.getPosition();
					if(pos[0] != pos2[0] || pos[1] != pos2[1]) {
						if(object instanceof SleepingAnt) {
							int it = (int) (noOfIterations * iterationquotient);
							//System.out.println("Wrong ant no "+((SleepingAnt)object).getId() +"///"+ it);
							object.setPosition(pos);
						} else {
							//System.out.println("Wrong group");
						}
						
						posAlarm = true;
					}
					allAnts.add(object);
				}
			}
		}
		return posAlarm;
	}
	
	private void deMergeAllGroups() {
		boolean groupFound = true; 
		while(groupFound) {
			groupFound = false;
			for(int i = 0; i < ants.size(); i++) {
				if(ants.get(i) instanceof GroupOfAnts) {
					groupFound = true;
					GroupOfAnts ant = (GroupOfAnts)(ants.get(i));
					Vector antz = ant.getAnts();
					int[] pos = ant.getPosition();
					for (Iterator iterator = antz.iterator(); iterator.hasNext();) {
						ISAnt sa = (ISAnt) iterator.next();
						sa.setPosition(pos);
						ants.add(sa);
					}
					ants.remove(ant);
				}
			}
		}
	}
	
	private void removeGroup(GroupOfAnts group) {
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				int[] pos = {i,j};
				Vector antz = getPosition(pos);
				antz.remove(group);
			}
		}
	}
	
	private void setCCPositionsInConnectedComponent() {
		double[][] itemPos = new double[noNodes][dimension];
		for (int i = 0; i < itemPos.length; i++) {
			//System.out.print(i+": ");
			for (int j = 0; j < itemPos[i].length; j++) { 
				itemPos[i][j] = ((SleepingAnt) ants.get(i)).getPosition()[j] + (Math.random() * 0.001);
				//System.out.print(itemPos[i][j] + "/");
			}
			//System.out.println();
		}
		this.cc.setCCPositions(itemPos);
	}
	
	public ConnectedComponent getCC(){
		setCCPositionsInConnectedComponent();
		return cc;
	}

	public float getIterationquotient() {
		return iterationquotient;
	}
	
	public boolean findAndRemoveAnt(ISAnt ant) {
		boolean success = false;
		for(int i = 0; i < size; i++) {
			if(success) break;
			for (int j = 0; j < size; j++) {
				int[] pos = {i,j};
				Vector antz = getPosition(pos);
				boolean bla = antz.remove(ant);
				if(bla) {
					success = true;
					break;
				}
			}
		}
		return success;
	}

	

	public double getM() {
		return m;
	}

	public double getN() {
		return n;
	}
	
	
}
