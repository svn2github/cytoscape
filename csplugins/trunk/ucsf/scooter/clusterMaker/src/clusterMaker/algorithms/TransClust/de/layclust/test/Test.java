package de.layclust.test;

public class Test {
	
	public static void main (String[] args){
		int[][] testarray = new int[100][500000];
		
		for(int i=0;i<100;i++){
			for(int j = 0; j<500000;j++){
				testarray[i][j] = 2;
			}
		}
		
		for(int j=0;j<500000;j++){
			testarray[1][j] = 8;
		}
		
		long time = System.currentTimeMillis();
		System.out.println(Test.isRowEqual2(testarray[0], testarray[1]));
		time = System.currentTimeMillis() - time;
		System.out.println(time);
		
		time = System.currentTimeMillis();
		System.out.println(Test.isRowEqual2(testarray[0], testarray[3]));
		time = System.currentTimeMillis() - time;
		System.out.println(time);
		
		time = System.currentTimeMillis();
		int[] array = {1,1,1,1,1};
		int[] array2 = {1,1,1,1,1};
		System.out.println(Test.isRowEqual2(array, array2));
		time = System.currentTimeMillis() - time;
		System.out.println(time);
		
		time = System.currentTimeMillis();
		boolean isequal = true;
		for (int j=0; j<500000;j++){
			if(!(testarray[0][j]==testarray[1][j])){
				isequal = false;
			}
		}
		
		System.out.println(isequal);
		time = System.currentTimeMillis() - time;
		System.out.println(time);
	}
	
	public static boolean isRowEqual(int[] a, int[] b){
		 if(a.equals(b)){
			 return true;
		 }	else return false;	

	}
	
	public static boolean isRowEqual2(int[] a, int[] b){
		for(int i=0;i<a.length;i++){
			if(!(a[i]==b[i])){
				return false;
			}
		}
		return true;

	}

}


