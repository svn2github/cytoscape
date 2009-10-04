package de.layclust.layout.acc;

public class SimpleAnt implements IAnt {
	
	protected Playground playground;
	protected int[] position;
	protected int item;
	protected int view;
	protected double basic_kp;
	protected double basic_kd;
	
	protected double kp;
	protected double kd;
	protected double alpha;
	//counter for the number of moves an ant has performed
	protected int numberOfMoves;
	//counter for the number of failed dropping operations:
	protected int failedDropOperations;
	//number of moves for the update of alpha:
	protected int numberActiveMoves;
	//the number of drop-operations that have to fail for an increase of alpha
	protected int numberOfFailedDropOperationsToIncreaseAlpha;
	
	protected boolean spreadMode;

	public SimpleAnt(Playground playground) {
		this.playground = playground;
		position = new int[playground.getDimension()];
		for (int i = 0; i < position.length; i++) {
			position[i] = (int)Math.round(Math.random() * (playground.getSize()-1));
			//System.out.print(position[i]+" // ");
		}
		//no item carried:
		this.item = 0;
		//how far the ant can look to decide whether to drop or to pick up an item:
		this.view = 1;
		basic_kd = playground.getKd();
		basic_kp = playground.getKp();
		this.kd = basic_kd;
		this.kp = basic_kp;
		this.alpha = playground.getAlpha();
		this.numberOfFailedDropOperationsToIncreaseAlpha = 99;
		this.numberActiveMoves = 100;
		this.numberOfMoves = 0;
		this.failedDropOperations = 0;
		spreadMode = false;
	}
	
	protected void performMovement(){
		//compute random movement:
		int[] mov = new int[playground.getDimension()];
		for (int i = 0; i < mov.length; i++) {
			//move -1, 0 or 1 in one dimension
			//TODO: test if all values == 0 ?
			mov[i] = (int)Math.round(Math.random() * 2) -1 ;
		}
		//perform movement:
		for (int i = 0; i < mov.length; i++) {
			position[i] = (position[i] + mov[i]);
			if(position[i] < 0) {
				position[i] = playground.getSize() + position[i];
			} else {
				position[i] = position[i] % (playground.getSize());
			}
		}
	}
	

	public void pickUpItem(int item) {
		
	}
	
	public void makeStep(){
		performMovement();
		//when ant carries no item, check for new item to pick up:
		if(item == 0) {
			if(playground.getLocation(position) != 0) {
				double prob = computePickUpProbability();
				if(Math.random() < prob) {
					//System.out.println("Picked up "+playground.getLocation(position));
					item = playground.getLocation(position);
					playground.setLocation(0, position);
				}
			}
		//otherwise compute probability to unload the item
		} else {
			if(playground.getLocation(position) == 0) {
				double prob = computeDropProbability();
				if(Math.random() < prob) {
					//System.out.println("Dropped "+item);
					playground.setLocation(item, position);
					playground.setItemPosition(item, position);
					item = 0;
					updateAlpha(true);
				} else {
					updateAlpha(false);
				}
			}
		}
	}
	
	protected double computePickUpProbability(){
		double pPick = Math.pow((kp / (kp + computeImprovedNeighbourhoodFunction(playground.getLocation(position)))), 2); 
//		int noNeighbours = getNoOfNeighbouringItems();
//		double prob;
//		if(noNeighbours == 0) {
//			prob =  1;
//		} else {
//			prob = 1.0 / (noNeighbours+1);
//		}
		//System.out.println("PUProb: "+prob);
		//System.out.println("PU-Prob: "+pPick);
		return pPick;
	}
	
	protected double computeDropProbability(){
		double fi = computeImprovedNeighbourhoodFunction(item);
		double prob = Math.pow((fi / (kd + fi)), 2);
//		int noNeighbours = getNoOfNeighbouringItems();
//		double prob;
//		if(noNeighbours == 0) {
//			prob = 0;
//		} else {
//			prob =1 - Math.pow((1.0 / (noNeighbours+1)),2);
//		} 
//		System.out.println("DropProb: "+prob);
		return prob;
//		if (pDrop < kd) {
//			return pDrop; 
//		} else {
//			return 1;
//		}
	}
	
	protected int getNoOfNeighbouringItems(){
		int[] items = playground.getItemsInViewSizeWithoutPeriodicBoundaries(position, view);
		return items.length;
	}
	
	protected double computeNeighbourhoodFunction(int it){
		double f = 0;
		int[] items = playground.getItemsInViewSizeWithoutPeriodicBoundaries(position, view);
		//System.out.println("Items in the neighbourhood of "+item+":");
		for (int i = 0; i < items.length; i++) {
			//System.out.println(items[i]+", ");
			double fi;
			//System.out.println("Similarity: ("+(it-1)+","+ (items[i]-1)+")"+playground.getSimilarity(it-1, items[i]-1));
			fi = (1.0 - (1.0-playground.getSimilarity(it-1, items[i]-1)) / alpha);
			//System.out.println("F(i,"+items[i]+"): "+fi);
			if(fi > 0) f += fi;
		}
		f *= 1 / Math.pow(view*2+1,playground.getDimension());
		//System.out.println("F(i): "+f);
		return f;
	}
	
	protected double computeImprovedNeighbourhoodFunction(int it){
		double f = 0;
		int[] items = playground.getItemsInViewSizeWithoutPeriodicBoundaries(position, view);
		//System.out.println("No of items in the neighbourhood of "+item+": "+items.length);
		//System.out.println("Items in the neighbourhood of "+item+":");
		int no = 0;
		for (int i = 0; i < items.length; i++) {
			if(items[i] == 0) {
				continue;
			} else {
				no++;
			}
			//System.out.println(items[i]+", ");
			double fi;
			//System.out.println("Similarity: ("+(it-1)+","+ (items[i]-1)+")"+playground.getSimilarity(it-1, items[i]-1));
			fi = (1.0 - (1.0-playground.getSimilarity(it-1, items[i]-1)) / alpha);
			//System.out.println("F(i,"+items[i]+"): "+fi);
			//if(fi > 0) {
				f += fi;
			//} else {
				//return 0;
			//}
		}
		if(!isSpreadMode()) {
			f *= 1 / Math.pow(view*2+1,playground.getDimension());
		} else {
			if(items.length > 0)  {
				f *= 1 / no;
			}
		}
		//System.out.println("F(i): "+f);
		//System.out.println(f);
		if(f < 0) f = 0;
		return f;
	}
	
	

	protected void updateAlpha(boolean dropped) {
//		if(dropped) {
//			numberOfMoves++;
//		} else {
//			numberOfMoves++;
//			failedDropOperations++;
//		}
//		if(numberOfMoves == numberActiveMoves) {
//			if(failedDropOperations >= numberOfFailedDropOperationsToIncreaseAlpha) {
//				alpha += 0.01;
//				
//			} else {
//				alpha -= 0.01;
//			}
//			if(alpha > 1) alpha = 1;
//			if(alpha <= 0) alpha = 0.01;
//			numberOfMoves = 0;
//			failedDropOperations = 0;
//			System.out.println("Alpha: "+alpha);
//		}
	}

	public void drop() {
		if (item == 0) return;
		playground.setLocation(item, position);
		System.out.println("Final drop "+item);
		item = 0;
	}

	public boolean isSpreadMode() {
		return spreadMode;
	}

	public void setSpreadMode(boolean spreadMode) {
		this.spreadMode = spreadMode;
	}

	public int[] getPosition() {
		return position;
	}

	public void setPosition(int[] position) {
		this.position = position;
	}
}
