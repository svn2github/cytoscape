package de.layclust.layout.acc;


import java.awt.BorderLayout;
import java.lang.reflect.Array;
//import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Vector;
//import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import javax.swing.JPanel;

import de.layclust.datastructure.ConnectedComponent;
import de.layclust.datastructure.ICCEdges;
import de.layclust.geometric_clustering.SingleLinkageClusterer;
import de.layclust.layout.IParameters;
import de.layclust.postprocessing.PP_RearrangeAndMergeBest;
import de.layclust.taskmanaging.TaskConfig;

public class Playground implements IPlayground{
	private int[][] itemPositions;
	private IAnt[] ants;
	private int dimension;
	private int noNodes;
	private ICCEdges ccEdges;
	private Object playground; 
	private int size;
	private ConnectedComponent cc;
	private LinkedList<Object> uncarriedItems;
	private int noOfIterations;
	private boolean isRunning;
	private AntPanel antPlot;
	private String antType = "MemoryAnt";
	private TreeMap<Double, Integer> itemsByPickUpProbability;
	private boolean spreadMode;
	private int spreadIterations;
	private int maxStepSizeForJumpingAnts;
	private int maxViewSize;
	private double kp;
	private double kd;
	private double alpha;
	private int sizeOfMemory;
	private int[][] playground2D;
	private int[][][] playground3D;
	public ACCParameters parameters; 

	

	
	public Playground(ConnectedComponent cc, IParameters parameters) {
		this.cc = cc;
		//this.parameters = (ACCParameters) parameters;
		this.spreadMode = false;
		this.dimension = TaskConfig.dimension;
		this.noNodes = cc.getNodeNumber();
		int noAnts = ((ACCParameters)parameters).getNoAnts();
		this.noOfIterations = ((ACCParameters)parameters).getMultiplicatorForIterations() * noNodes;
		//System.out.println("Number of Iterations: "+noOfIterations);
		this.ccEdges = cc.getCCEdges();
		ccEdges.normaliseWithThreshold(((ACCParameters)parameters).getNormaliseThreshold());
		this.ants = new IAnt[noAnts];
		this.antType = ((ACCParameters)parameters).getAntType();
		this.size = (int) Math.ceil(Math.pow(((ACCParameters)parameters).getMultiplicatorForGridSize()*noNodes, 1.0/dimension));
		this.itemPositions = new int[noNodes][dimension];
		this.maxStepSizeForJumpingAnts = (int) Math.pow(((ACCParameters)parameters).getMultiplicatorForMaxStepsize() * noNodes,1.0/dimension );
		this.maxViewSize = ((ACCParameters)parameters).getMaxViewSize();
		this.alpha = ((ACCParameters)parameters).getAlpha();
		this.kp = ((ACCParameters)parameters).getKp();
		this.kd = ((ACCParameters)parameters).getKd();
		this.sizeOfMemory = ((ACCParameters)parameters).getMemorySize();
		//System.out.println("Size: "+size);
		//System.out.println("MaxStepSize: "+maxStepSizeForJumpingAnts);
		createPlayground();
		uncarriedItems = new LinkedList<Object>();
		//placeItems();
		initialiseGridFromCCPositions();
		//cc.printPositions();
		//setCCPositionsInConnectedComponent();
		initializeAnts();
	}
	
	
	public Playground(ConnectedComponent cc) {
		System.out.println("CALLED OLD CONSTRUCTOR!?");
		this.cc = cc;
		this.spreadMode = false;
		this.dimension = 2;
		this.noNodes = cc.getNodeNumber();
		//int noAnts = (int) Math.round(noNodes * ACCConfig.ANT_FACTOR);
		int noAnts = 1;
		this.noOfIterations = 500000;
		System.out.println("Number of Iterations: "+noOfIterations);
		this.ccEdges = cc.getCCEdges();
		ccEdges.normaliseWithThreshold(1.0);
		this.ants = new IAnt[noAnts];
		this.size = (int) Math.round(Math.pow(ACCConfig.multiplicatorForGridSize*noNodes, 1.0/dimension));
		this.itemPositions = new int[noNodes][dimension];
		this.maxStepSizeForJumpingAnts = (int) Math.pow(ACCConfig.multiplicatorForMaxStepsize * noNodes,1.0/dimension );
		System.out.println("Size: "+size);
		System.out.println("MaxStepSize: "+maxStepSizeForJumpingAnts);
		createPlayground();
		uncarriedItems = new LinkedList<Object>();
		placeItems();
		//setCCPositionsInConnectedComponent();
		initializeAnts();
	}
	
	public void reset() {
		cc.getCCEdges().normaliseWithThreshold(1.0);
		this.itemPositions = new int[noNodes][dimension];
		createPlayground();
		uncarriedItems = new LinkedList<Object>();
		placeItems();
		initializeAnts();
	}
	
	private void initialiseGridFromCCPositions(){
		double[][] ccPositions = cc.getCCPositions();
		double min = ccPositions[0][0];
		double max = ccPositions[0][0];
		for (int i = 0; i < ccPositions.length; i++) {
			for (int j = 0; j < ccPositions[i].length; j++) {
				if(ccPositions[i][j] < min) min = ccPositions[i][j];
				if(ccPositions[i][j] >max ) max = ccPositions[i][j];
			}
		}
		if(dimension == 2) {
			for (int i = 0; i < ccPositions.length; i++) {
				//System.out.println("Item No. "+i+": "+ccPositions[i][0]+" / "+ccPositions[i][1]);
				int x = (int)( (ccPositions[i][0] -min) / (max-min) * (size-1));
				int y = (int)( (ccPositions[i][1] -min) / (max-min) * (size-1));
				//System.out.println("X/Y: "+x+" / "+y);
				//place item if position is free:
				if(this.getLocation(new int[] {x,y}) == 0) {
					ccPositions[i][0] = x;
					ccPositions[i][1] = y;
					this.setLocation(i+1, new int[] {x,y});
					addUncarriedItem(i+1);
					//System.out.println("Position now: "+x+" / " +y);
				//search a new position otherwise
				} else {
					int r = 1;
					boolean found = false;
					int[] xMods = new int[] {0,-1,1};
					int[] yMods = new int[] {0,-1,1};
					while(!found) {
						for(int j = 0; j < 3; j++) {
							if(found) break;
							for(int k = 0; k < 3; k++) {
								int x1 = x + r * xMods[j];
								int y1 = y + r * yMods[k];
								if(x1 < 0 || y1 < 0 || x1 > (size-1) || y1 > (size-1)) continue;
								if(this.getLocation(new int[] {x1,y1}) == 0) {
									found = true;
									x =x1;
									y = y1;
									break;
								}
							}
						}
						r++;
					}
					ccPositions[i][0] = x;
					ccPositions[i][1] = y;
					this.setLocation(i+1, new int[] {x,y});
					addUncarriedItem(i+1);
					//System.out.println("Position now: "+x+" / " +y);
				}
			}
		} else if (dimension == 3) {
			for (int i = 0; i < ccPositions.length; i++) {
				int x = (int)( (ccPositions[i][0] -min) / (max-min) * (size-1));
				int y = (int)( (ccPositions[i][1] -min) / (max-min) * (size-1)); 
				int z = (int)( (ccPositions[i][2] -min) / (max-min) * (size-1));
				//System.out.println("X: "+x+" Y: "+y+" Z: "+z);
				if(this.getLocation(new int[] {x,y,z}) == 0) {
					ccPositions[i][0] = x;
					ccPositions[i][1] = y;
					ccPositions[i][2] = z;
					this.setLocation(i+1, new int[] {x,y,z});
					addUncarriedItem(i+1);
					//System.out.println("Position now: "+x+" / " +y);
				//search a new position otherwise
				} else {
					int r = 1;
					boolean found = false;
					int[] xMods = new int[] {0,-1,1};
					int[] yMods = new int[] {0,-1,1};
					int[] zMods = new int[] {0,-1,1};
					while(!found) {
						for(int j = 0; j < 3; j++) {
							if(found) break;
							for(int k = 0; k < 3; k++) {
								if(found) break;
								for(int l = 0; l < 3; l++) {
									int x1 = x + r * xMods[j];
									int y1 = y + r * yMods[k];
									int z1 = z + r * zMods[l];
									if(x1 < 0 || y1 < 0 || x1 > (size-1) || y1 > (size-1) || z1 < 0 || z1 > (size-1)) continue;
									if(this.getLocation(new int[] {x1,y1,z1}) == 0) {
										found = true;
										x =x1;
										y = y1;
										z = z1;
										break;
									}
								}
							}
						}
						r++;
					}
					ccPositions[i][0] = x;
					ccPositions[i][1] = y;
					ccPositions[i][2] = z;
					this.setLocation(i+1, new int[] {x,y,z});
					addUncarriedItem(i+1);
					//System.out.println("Position now: "+x+" / " +y);
				}
			}
		} else {
			for(int i = 0; i < ccPositions.length; i++) {
				int[] pos = new int[dimension];
				for(int j = 0; j < ccPositions[i].length; j++) {
					pos[j] = (int)( (ccPositions[i][j] -min) / (max-min) * (size-1));
				}
				if(this.getLocation(pos) == 0) {
					for(int j = 0; j < ccPositions[i].length; j++) {
						ccPositions[i][j] = pos[j];
					}
					this.setLocation(i+1, pos);
					addUncarriedItem(i+1);
					//search a new position otherwise
				} else { 
					int r = 1;
					boolean found = false;
					int[] mods = new int[] {0,-1,1};
					while(!found) {
						int[] actPos = new int[dimension];
						int[] thisPos = new int[dimension];
						for(int j = 0; j < Math.pow(dimension, mods.length); j++) {
							if(found) break;
							for(int k = 0; k < thisPos.length; k++) {
								thisPos[k] = pos[k] + r * mods[actPos[k]];
							}
							boolean outOfBounds = false;
							for(int k = 0; k < thisPos.length; k++) {
								if(thisPos[k] < 0 || thisPos[k] > (size-1)) outOfBounds = true;
							}
							for(int k = dimension-1; k >= 0; k--) {
								if(actPos[k] == mods.length-1) {
									actPos[k] = 0;
									continue;
								} else {
									actPos[k]++;
									break;
								}
							}
							if(outOfBounds) continue;
							if(this.getLocation(thisPos) == 0) {
								found = true;
								for (int k = 0; k < thisPos.length; k++) {
									pos[k] = thisPos[k];
								}
								break;
							}
						}
						r++;
					}
					for (int j = 0; j < pos.length; j++) {
						ccPositions[i][j] = pos[j];
					}
					this.setLocation(i+1, pos);
					addUncarriedItem(i+1);
				}
			}
		}
		//initialise itemPositions:
		for(int i = 0; i < itemPositions.length; i++) {
			for (int j = 0; j < itemPositions[i].length; j++) {
				itemPositions[i][j] = (int) ccPositions[i][j];
			}
		}
	}
	
	/**
	 * old function to place the items on the grid.
	 *
	 */
	private void placeItems() {
		for(int i = 1; i <= itemPositions.length; i++) {
			for(int j = 0; j < itemPositions[i-1].length; j++) {
				int position = (int)Math.round(Math.random() * (size-1));
				itemPositions[i-1][j] = position;
			}
			int[] pos = itemPositions[i-1];
			if(getLocation(pos) != 0) {
				i--;
			} else {
				setLocation(i, pos);
				addUncarriedItem(i);
			}
		}
	}
	
	private void initializeAnts(){
		for (int i = 0; i < ants.length; i++) {
			if(antType.equals("JumpingAnt")) {
				ants[i] = new JumpingAnt(this);
			} else if (antType.equals("JumpingAntWithIncreasingViewField")) {
				ants[i] = new JumpingAntWithIncreasingViewField(this);
			} else if (antType.equals("MemoryAnt")) {
				ants[i] = new MemoryAnt(this);
			} else {
				ants[i] = new SimpleAnt(this);
			}
		}
	}
	
	public void addUncarriedItem(int item) {
		//System.out.println("Added "+item);
		uncarriedItems.add(item);
	}
	
	public int getUncarriedItem(){
		int item = ((Integer) uncarriedItems.poll()).intValue();
		//System.out.println("Removed "+item);
		//System.out.println("Size of uncarriedItems: "+uncarriedItems.size());
		return item;
	}
	
	public float getSimilarity(int a, int b) {
		return this.ccEdges.getEdgeCost(a, b);
	}
	
	private void createPlayground(){
		
		if(dimension == 2) {
			playground2D = new int[size][size];
		} else if (dimension == 3) {
			playground3D = new int[size][size][size];
		}else {
			int [] dim = new int[dimension];
			for (int i = 0; i < dim.length; i++) {
				dim[i] = size;
			}
			playground =  Array.newInstance(int.class, dim);
		}
	}
	
	public void setLocation(int item, int[] pos) {
		//System.out.println("Set Location of "+item+" to "+pos[0]+"/"+pos[1]);
		if(dimension == 2) {
			playground2D[pos[0]][pos[1]] = item;
		} else if(dimension == 3) {
			playground3D[pos[0]][pos[1]][pos[2]] = item;
		} else {
			Object arr = playground;
			for(int i = 0; i < dimension; i++) {
				if(i == dimension-1) {
					((int[]) arr)[pos[i]] = item;
				} else {
					arr = Array.get(arr, pos[i]);
				}
			}
		}
	}
	 
	
	public int getLocation(int[] pos) {
		if(dimension == 2) {
			return playground2D[pos[0]][pos[1]];
		} else if(dimension == 3) {
			return playground3D[pos[0]][pos[1]][pos[2]];
		} else {
			Object arr = playground;
			for(int i = 0; i < dimension; i++) {
				//System.out.println(pos[i]+" / "+size);
				arr = Array.get(arr, pos[i]);
			}
			return ((Integer) arr).intValue();
		}
	}
	
	public int[] getItemsInViewSizeWithoutPeriodicBoundaries(int[] pos, int view) {
//		System.out.print("Position: ");
//		for (int i = 0; i < pos.length; i++) {
//			System.out.print(pos[i]+" / ");
//		}
//		System.out.println();
		int[]items;
		int itemHere;
		if (dimension == 2) {
			items = new int[(2*view+1)*(2*view+1)];
			itemHere = playground2D[pos[0]][pos[1]];
			int count = 0;
			for(int i = (pos[0]-view >= 0) ? (pos[0]-view) : 0; i <= ((pos[0]+view <= size-1) ? (pos[0]+view) : (size-1)); i++) {
				for(int j = (pos[1]-view >= 0) ? (pos[1]-view) : 0; j <= ((pos[1]+view <= size-1) ? (pos[1]+view) : (size-1)); j++) { 
					int it = playground2D[i][j];
					if(it != itemHere) {
						items[count] = playground2D[i][j];
					} else {
						items[count] = 0;
					}
					count++;
				}
			}
		} else if(dimension==3){
			items = new int[(2*view+1)*(2*view+1)*(2*view+1)];
			itemHere = playground3D[pos[0]][pos[1]][pos[2]];
			int count = 0;
			for(int i = (pos[0]-view >= 0) ? (pos[0]-view) : 0; i <= ((pos[0]+view <= size-1) ? (pos[0]+view) : (size-1)); i++) {
				for(int j = (pos[1]-view >= 0) ? (pos[1]-view) : 0; j <= ((pos[1]+view <= size-1) ? (pos[1]+view) : (size-1)); j++) { 
					for(int k = (pos[2]-view >= 0) ? (pos[2]-view) : 0; k <= ((pos[2]+view <= size-1) ? (pos[2]+view) : (size-1)); k++) {
						int it = playground3D[i][j][k];
						if(it != itemHere) {
							items[count] = playground3D[i][j][k];
						} else {
							items[count] = 0;
						}
						count++;
					}
				}
			}
		} else {
			items = new int[1];
			System.out.println("Dimensions larger than 3 turned off due to performance reasons!");
			System.exit(1);
//			Object[] area = new Object[dimension];
//			int noOfPlaces = 1;
//			for (int i = 0; i < area.length; i++) {
//				int a = (pos[i] - view > 0) ? (pos[i] - view) : 0;
//				int b = (pos[i]+view < size) ? (pos[i] + view) : (size-1);
//				int[] area2 = new int[b-a+1];
//				noOfPlaces *= b-a+1;
//				for (int x = 0; x <= (b-a); x++) {
//					area2[x] =x+a;
//				}
//				area[i] = area2;
//			}
//			int[] actPos = new int[dimension];
//			for (int i = 0; i < noOfPlaces; i++) {
//				int[] thisPos = new int[dimension];
//				for (int j = 0; j < thisPos.length; j++) {
//					thisPos[j] = ((int[])area[j])[actPos[j]];
//				}
//				int item = getLocation(thisPos);
//
//				if (item != 0) items.add(item);
//				for(int j = area.length-1; j >= 0; j--) {
//					if(actPos[j] == ((int[])area[j]).length-1) {
//						actPos[j] = 0;
//						continue;
//					} else {
//						actPos[j]++;
//						break;
//					}
//				}
//			}
//		}
//			System.out.print("Items: ");
//			for (int i = 0; i < ret.length; i++) {
//			System.out.print(ret[i]+" / ");
			}
//			System.out.println();
			return items;
	}
	
	public int[] getItemsInViewSize(int[] pos, int view) {
		int[][] area = new int[dimension][1+view*2];
		for (int i = 0; i < area.length; i++) {
			int a = (pos[i] - view >= 0) ? (pos[i] - view) : (size+pos[i]-view);
			int b = (pos[i] + view) % (size);
			if(a < b) {
				for (int x = 0; x <= (b-a); x++) {
					area[i][x] = x+a;
				}
			} else {
				int j = 0;
				for (int x = 0; x < (size-a); x++) {
					area[i][j] = x+a;
					j++;
				}
				for(int x = 0; x <= b; x++) {
					area[i][j] = x;
					j++;
				}
			}
		}
		Vector<Object> items = new Vector<Object>();
		int[] actPos = new int[dimension];
		for (int i = 0; i < Math.pow(area[0].length, area.length); i++) {

			int[] thisPos = new int[dimension];
			for (int j = 0; j < thisPos.length; j++) {
				thisPos[j] = area[j][actPos[j]];
			}
			int item = getLocation(thisPos);
			
			if (item != 0) items.add(item);
			for(int j = area.length-1; j >= 0; j--) {
				if(actPos[j] == area[0].length-1) {
					actPos[j] = 0;
					continue;
				} else {
					actPos[j]++;
					break;
				}
			}
		}
		//remove item at this position:
		int itemAtThisPosition = getLocation(pos);
		int posToRemove = -1;
		for(int x = 0; x < items.size(); x++) {
			if((((Integer)items.get(x)).intValue()) == itemAtThisPosition) posToRemove = x;
		}
		if(posToRemove != -1) items.remove(posToRemove);
		int[] ret = new int[items.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = ((Integer)items.get(i)).intValue();
		}

		return ret;
	}
	
	public int[] getItemLocation(int item){
		return itemPositions[item-1];
	}
	
	 public void run() {
		 //printGrid();
		 //System.out.println(parameters.toString());
		 for(int i = 0; i < noOfIterations; i++) {
//			 if(i % 1000 == 0) {
//				 System.out.println(i);
//			 }
			 for(int j = 0; j < ants.length; j++) {
				 ants[j].makeStep();
			 }
		 }
		 for (int i = 0; i < ants.length; i++) {
			ants[i].drop();
		}
		 //printGrid();
		 cc.getCCEdges().denormaliseWithThreshold();
		 //printItemPositions(cc);
	 }
	 
	 public void runInPanel(JPanel panel, int time, GUI gui) {
		 final JPanel panel2 = panel;
		 final int time2 = time;
		 final Playground dieser = this;
		 final ConnectedComponent cc2 = cc;
		 final Semaphore s = new Semaphore(1);
		 final GUI gui2 = gui;
		 this.setRunning(true); 
		 if(this.getAntPlot() != null) {
			 panel2.remove(this.getAntPlot());
		 }
		Thread t = new Thread("Gui-Update") {
			public void run() {
				AntPanel plot = new AntPanel(cc2, dieser, false);
				panel2.add(plot, BorderLayout.CENTER);
				panel2.revalidate();
				panel2.repaint();
				while(dieser.isRunning) {
					try {
						Thread.sleep(time2);
					} catch (Exception e) {
						System.out.println(e.getStackTrace());
					}
					panel2.remove(plot);
					plot = new AntPanel(cc2, dieser, false);
					panel2.add(plot, BorderLayout.CENTER);
					panel2.revalidate();
					panel2.repaint();
				}
				ConnectedComponent cc = dieser.getCC();
				cc.getCCEdges().denormaliseWithThreshold();
				SingleLinkageClusterer slc = new SingleLinkageClusterer();
				slc.initGeometricClusterer(cc);
				slc.run();
				PP_RearrangeAndMergeBest pp = new PP_RearrangeAndMergeBest();
				pp.initPostProcessing(cc);
				pp.run();
				System.out.println("BestDistance SLC: "+slc.getBestDistance());
				//System.out.println("Clustering-Score: "+cc.getClusteringScore());
				panel2.remove(plot);
				plot = new AntPanel(cc, dieser, true);
				panel2.add(plot, BorderLayout.CENTER);
				panel2.revalidate();
				panel2.repaint();
				dieser.setAntPlot(plot);

				String res = dieser.computeClusteringResultText();
				//dieser.setClusteringResult(res.toString());
				gui2.setResultText(res);
				//printSimilarityMatrix(cc);
				int[] posi = dieser.getItemLocation(1);
				System.out.println("Position of First Item: "+posi[0]+"/"+posi[1]);
			}
		};
		//final int noOfIterations2 = noOfIterations;
		//final IAnt[] ants2 = ants;
		
		Thread t2 = new Thread("Ant-Movement") {
			public void run() {
				long time =System.currentTimeMillis(); 
				dieser.setRunning(true);
				try {
					s.acquire();
				} catch (Exception e) {
					e.printStackTrace();
				}
				for(int i = 0; i < noOfIterations; i++) { 
					for(int b = 0; b < 500; b++) {
						Vector blubb = new Vector();
						blubb.add(1);
					}
					if(i == noOfIterations / 2 && dieser.isSpreadMode()) {
						for(int j = 0; j < ants.length; j++) {
							ants[j].setSpreadMode(true);
						}
						for(int k = 0; k < spreadIterations; k++) {
							for(int j = 0; j < ants.length; j++) {
								ants[j].makeStep();
							}
						}
						for(int j = 0; j < ants.length; j++) {
							ants[j].setSpreadMode(false);
						}
					}
					for(int j = 0; j < ants.length; j++) {
						ants[j].makeStep();
					}
				}
				for (int i = 0; i < ants.length; i++) {
					ants[i].drop();
				}
				dieser.setRunning(false);
				s.release();
				time = System.currentTimeMillis() - time;
				System.out.println("Time: "+time);
			}
		};
		t2.start();
		t.start();
//		try {
//			Thread.sleep(1000);
//			s.acquire();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		System.out.println("DA! :(");
//		s.release();
	 }
	 
	 public void setItemPosition(int item, int[] pos) {
		 //System.out.println("Set position of "+item+" to "+pos[0]+"/"+pos[1]);
		 if(itemPositions[item-1].length != pos.length) return;
		 for (int i = 0; i < pos.length; i++) {
			 this.itemPositions[item-1][i] = pos[i];
		} 
	 }
	

	public int getSize() {
		return size;
	}

	public int getDimension() {
		return dimension;
	}
	
	private void setCCPositionsInConnectedComponent() {
		double[][] itemPos = new double[noNodes][dimension];
		for (int i = 0; i < itemPos.length; i++) {
			for (int j = 0; j < itemPos[i].length; j++) {
				itemPos[i][j] = this.itemPositions[i][j];
			}
		}
		this.cc.setCCPositions(itemPos);
	}
	
	public ConnectedComponent getCC(){
		setCCPositionsInConnectedComponent();
		return cc;
	}
	
	public void printGrid(){
		for(int i = 0; i < playground2D.length; i++) {
			for(int j = 0; j < playground2D[i].length; j++) {
				System.out.print(" "+playground2D[i][j]+" ");
			}
			System.out.println();
		}
	}
	
	public static void printItemPositions(ConnectedComponent cc){
		double[][] itemPositions = cc.getCCPositions();
		for (int i = 0; i < itemPositions.length; i++) {
			System.out.print(i+": ");
			for (int j = 0; j < itemPositions[i].length; j++) {
				System.out.print(itemPositions[i][j]+", ");
			}
			System.out.println();
		}
	}
	
//	private static void printSimilarityMatrix(ConnectedComponent cc) {
//		ICCEdges ccEdges = cc.getCCEdges();
//		for (int i = 0; i < cc.getNodeNumber(); i++) {
//			for (int j = 0; j < cc.getNodeNumber(); j++) {
//				System.out.print(ccEdges.getEdgeCost(i, j) + ", ");
//			}
//			System.out.println();
//		}
//	}
//	
	public int getNoOfIterations() {
		return noOfIterations;
	}

	public void setNoOfIterations(int noOfIterations) {
		this.noOfIterations = noOfIterations;
		for (int i = 0; i < ants.length; i++) {
			if(ants[i] instanceof JumpingAntWithIncreasingViewField) {
				((JumpingAntWithIncreasingViewField) ants[i]).setMaxSteps(noOfIterations);
			}
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public AntPanel getAntPlot() {
		return antPlot;
	}

	public void setAntPlot(AntPanel antPlot) {
		this.antPlot = antPlot;
	}

	public String getAntType() {
		return antType;
	}

	public void setAntType(String antType) {
		this.antType = antType;
		for (int i = 0; i < ants.length; i++) {
			ants[i].drop();
		}
		initializeAnts();
	}

	public String computeClusteringResultText() {
		StringBuffer res = new StringBuffer();
		res.append("No of Clusters: "+cc.getNumberOfClusters()+"\n");
		res.append("Clustering-Score: "+cc.getClusteringScore()+"\n");
		int[] clusterDistribution = cc.getClusterInfo();
		res.append("Sizes of the Clusters: \n");
		System.out.println(res);
		for (int i = 0; i < clusterDistribution.length; i++) {
			res.append(clusterDistribution[i]+"\n");
		}
		int[] clusters = cc.getClusters();
		StringBuffer[] proteinsByCluster = new StringBuffer[cc.getNumberOfClusters()];
		int[] lengths = new int[cc.getNumberOfClusters()];
		for (int i = 0; i < proteinsByCluster.length; i++) {
			proteinsByCluster[i] = new StringBuffer("Cluster No."+(i+1)+" :\t");
		}
		for (int i = 0; i < clusters.length; i++) {
			lengths[clusters[i]] += (cc.getObjectID(i)).length()+1;
			if(lengths[clusters[i]] > 70) {
				proteinsByCluster[clusters[i]].append("\n\t");
				lengths[clusters[i]] -= 70;
			}
			proteinsByCluster[clusters[i]].append(cc.getObjectID(i)+"\t");
		}
		for (int i = 0; i < proteinsByCluster.length; i++) {
			proteinsByCluster[i].append("\n");
			res.append(proteinsByCluster[i]);
		}
		return res.toString();
	}
	 
	public void initialiseItemsByPickUpProbability() {
		SimpleAnt ant = new SimpleAnt(this);
		for(int i = 1; i <= noNodes; i++ ) {
			//int[] pos = getItemLocation(i);
			double puProb = ant.computePickUpProbability();
			itemsByPickUpProbability.put(puProb, i);
		}
	}
	
//	public int moveWorstPlacedItem() {
//		if (itemsByPickUpProbability == null) return 0 ;
//		Entry<Double,Integer> entry = itemsByPickUpProbability.lastEntry();
//		int item = (Integer) entry.getValue();
//		JumpingAnt ant = new JumpingAnt(this);
//		ant.setPosition(getItemLocation(item));
//		ant.drop();
//		return item;
//	}
	
//	public void updateItemsByPickUpProbability() {
//		itemsByPickUpProbability.clear();
//		SimpleAnt ant = new SimpleAnt(this);
//		for(int i = 1; i <= noNodes; i++ ) {
//			int[] pos = getItemLocation(i);
//			double puProb = ant.computePickUpProbability();
//			itemsByPickUpProbability.put(puProb, i);
//		}
//	}
	
//	private void initialiseItemsByPickUpProbabilityTreeMap(){
//		itemsByPickUpProbability = new TreeMap<Double,Integer>(new Comparator<Object>() {
//			public int compare(Object o1, Object o2) {
//					if((Double) o1 <= (Double) o2) {
//						return -1;
//					} else {
//						return 1;
//					}
//			}
//		});
//	}


	public boolean isSpreadMode() {
		return spreadMode;
	}


	public void setSpreadMode(boolean spreadMode) {
		this.spreadMode = spreadMode;
	}


	public int getSpreadIterations() {
		return spreadIterations;
	}


	public void setSpreadIterations(int spreadIterations) {
		this.spreadIterations = spreadIterations;
	}


	public int getMaxStepSizeForJumpingAnts() {
		return maxStepSizeForJumpingAnts;
	}


	public void setMaxStepSizeForJumpingAnts(int maxStepSizeForJumpingAnts) {
		this.maxStepSizeForJumpingAnts = maxStepSizeForJumpingAnts;
	}


	public int getMaxViewSize() {
		return maxViewSize;
	}


	public void setMaxViewSize(int maxViewSize) {
		this.maxViewSize = maxViewSize;
	}


	public double getKd() {
		return kd;
	}


	public double getKp() {
		return kp;
	}


	public double getAlpha() {
		return alpha;
	}


	public int getSizeOfMemory() {
		return sizeOfMemory;
	}
	
	
}
