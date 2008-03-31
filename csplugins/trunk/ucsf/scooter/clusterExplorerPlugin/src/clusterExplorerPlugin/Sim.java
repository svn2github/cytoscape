package clusterExplorerPlugin;


public class Sim {
	
	private float[][] sim;
	private int size;
	private Mapping map;
	
	public Sim(Mapping map) {
		this(map.size(), map);
	}
	
	public Sim(Mapping map, float initValue) {
		this(map.size(), map, initValue);
	}
	
	public Sim(int size, Mapping map) {
		this.map = map;
		initArray(size);
	}
	
	public Sim(int size, Mapping map, float initValue) {
		this.map = map;
		initArray(size, initValue);
	}
	
	
	public void resetArray(int size) {
		initArray(size);
	}
	
	public void resetArray(int size, float initValue) {
		initArray(size, initValue);
	}
	
	public void initArray(int size) {
		this.size = size;
		this.sim = new float[size][size];
	}
	
	public void initArray(int size, float initValue) {
		initArray(size);
		for (int i = 0; i < sim.length; i++) {
			for (int j = 0; j < sim.length; j++) {
				if (i!=j) {
					this.sim[i][j] = initValue;
				}
			}
			
		}
	}
	
	public float set(int i, int j, float value) {
		float r = get(i,j);
		this.sim[i][j] = value;
		this.sim[j][i] = value;
		return r;
	}
	
	public float set(String iID, String jID, float value) {
		
		int i = map.getNumber(iID);
		int j = map.getNumber(jID);
		
		float r = get(i,j);
		
		set(i,j,value);
		
		return r;
	}

	public float get(int i, int j) {
		return this.sim[i][j];
	}
	
	public float get(String iID, String jID) {
		
		int i = map.getNumber(iID);
		int j = map.getNumber(jID);
		
		return get(i,j);
	}
	
	public Mapping getMapping() {
		return map;
	}
	
	public int size() {
		return this.size;
	}
	
	
	
	
	
	public void printMatrix() {
		
		for (int i = 0; i < this.size; i++) {
			if (i==0) {
				System.out.print("\t");
				System.out.print(i + "\t");
			} else {
				System.out.print(i + "\t");
			}
		}
		System.out.println();
		for (int i = 0; i < this.size; i++) {
			System.out.print(i + "\t");
			for (int j = 0; j < this.size; j++) {
				System.out.print(this.sim[i][j] + "\t");
			}
			System.out.println();
		}
	}
	
	
	
	
}

















