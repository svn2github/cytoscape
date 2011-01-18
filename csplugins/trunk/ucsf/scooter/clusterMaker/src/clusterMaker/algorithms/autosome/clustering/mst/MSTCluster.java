package clusterMaker.algorithms.autosome.clustering.mst;

/*
 * DEC.java
 * 
 * Created on Feb 26, 2008, 6:02:10 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */




import clusterMaker.algorithms.autosome.cluststruct.*;
import java.util.*;
//import view.view3d.*;
import java.io.*;
import clusterMaker.algorithms.autosome.launch.Settings;


/**
 *
 * @author a_newman
 */
public class MSTCluster {
    
    private int nodeNum = 0;
    private double alpha = 0.1;
    private int MCitors = 1;
    private boolean gaussian = false;
    public int[][] rgb;
    private int[] ids;
    public float[][] DEC;
    private double[] BMU;
    private javax.swing.JProgressBar jpb;
    private double[][] randEdges, edges;
    private int jpbItor = 1;
    private boolean file = false;
    private clusterRun cr;
    private boolean ensemble = false;
    private Settings s;
    private static double threshold=0;

    
    public MSTCluster(boolean ensemble) {this.ensemble = ensemble;}
    
    public void run(javax.swing.JProgressBar jprog, float[][] polygons, ArrayList ids, double alp, int it,  boolean gauss, Settings s){
       
        alpha = alp;
        MCitors = it;
        jpb = jprog;
        gaussian = gauss;
        this.file = file;
        this.s = s;
        float[][] coors = readPolygons(polygons);
        int[][] data = readIDs(ids);
        runMST(coors, data);
       
    }
    
    private float[][] readPolygons(float[][] poly){
        float[][] coors = new float[1][1];
        float[][] polyCoors = new float[1][1];
        int points = poly.length;
        int polygons = 4;
        int dimensions = 3;
        coors = new float[points][dimensions];
        polyCoors = new float[polygons][dimensions];
        DEC = poly;
        int scale = 36;
        
        float[][] shorterDEC = new float[DEC.length][3];
        
        //shorterDEC = new float[DEC.length][3];//unscaled SOM
        
        for(int h = 0; h < shorterDEC.length; h++) shorterDEC[h] = DEC[h];
        DEC = shorterDEC;

        int regionItor = 0;

            for(int q = 0, itor = 0; q < points; q++, itor++){               
                   if(itor == 4){
                        coors[regionItor++] = getCentroid(polyCoors);
                        polyCoors = new float[polygons][dimensions];
                        itor = 0;
                   }
                   for(int p = 0; p < dimensions; p++)
                        polyCoors[itor][p] = poly[q][p];                   
   
            }
            coors[regionItor++] = getCentroid(polyCoors);
          //  System.out.println(regionItor+" "+poly.length);
            nodeNum = regionItor - 1;

      
        
        return coors;
    }
    
    
    private float[] getCentroid(float[][] pts){
        float[] centroid = new float[pts[0].length];
      
        
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        
        for(int j = 0; j < pts.length; j++){
            if(pts[j][0] > maxX) maxX = pts[j][0];
            if(pts[j][1] > maxY) maxY = pts[j][1];
            if(pts[j][2] > maxZ) maxZ = pts[j][2];
            if(pts[j][0] < minX) minX = pts[j][0];
            if(pts[j][1] < minY) minY = pts[j][1];
            if(pts[j][2] < minZ) minZ = pts[j][2];
        }       
        
        centroid[0] = (maxX + minX) / 2f;
        centroid[1] = (maxY + minY) / 2f;
        centroid[2] = (maxZ + minZ) / 2f;
         
        return centroid;
    }
    
    
    private int[][] readIDs(ArrayList ids){
        int[][] data = new int[ids.size()][2];
        for(int i = 0; i < data.length; i++){
            data[i] = (int[]) ids.get(i);
        }
        return data;
      /* // System.out.println(nodeNum);
        int itor = 0;
        
        try{
            int lineItor = 0;
            BufferedReader bf = new BufferedReader(new StringReader(file));
            String line = new String();
            while((line = bf.readLine()) != null){
                if(line.length() != 0){
                   // System.out.println(line);
                    if(lineItor == 0) {
                        trs = new String[Integer.valueOf(line)][2];
                    //    rgb = new int[trs.length][3];
                        ids = new int[trs.length];
                    //    BMU = new double[trs.length];
                        //System.out.println(trs.length);
                        lineItor++;
                    }else{
                    StringTokenizer st = new StringTokenizer(line);
                    trs[itor][0] = st.nextToken();
                    trs[itor][1] = st.nextToken();
                   // rgb[itor][0] = Integer.valueOf(st.nextToken());
                   // rgb[itor][1] = Integer.valueOf(st.nextToken());
                   // rgb[itor][2] = Integer.valueOf(st.nextToken());
                    ids[itor] = Integer.valueOf(st.nextToken());                   
                  //  BMU[itor++] = Double.valueOf(st.nextToken());
                    }
                }
            }
            
        }catch(IOException err){};
        
        return trs;*/
    }
    
    
    
    private void runMST(float[][] coors, int[][] data){

      //  int[][] RGB = new int[coors.length][3];
        ArrayList[] IDs = new ArrayList[coors.length];
       // ArrayList[] bmus = new ArrayList[coors.length];
        int numNodes = 0;
        for(int j = 0; j < data.length; j++){
           // System.out.println(data[j][0]+" "+data[j][1]);
            if(IDs[data[j][0]] == null) {

                IDs[data[j][0]] = new ArrayList();
               // bmus[Integer.valueOf(trs[j][1])] = new ArrayList();
                numNodes++;
            }

            IDs[data[j][0]].add(data[j][1]);
           // bmus[Integer.valueOf(trs[j][1])].add(BMU[j]);
        }

        ArrayList[] idsreduced = new ArrayList[numNodes];
       // ArrayList[] BMUreduced = new ArrayList[numNodes];

        
        for(int j = 0, i = 0; j < IDs.length; j++){
            if(IDs[j] != null){

                idsreduced[i] = IDs[j];
                //System.out.println(i+" "+IDs[j].size());
                i++;
            }
        }
        
        
        
   
        
        Point[] p = new Point[numNodes];
        for(int i = 0, j = 0; i < coors.length; i++) {
            if(IDs[i] != null){
                p[j++] = new Point(coors[i]);
            }
        }

        /*for(int i = 0; i < p.length; i++) {
            for(int k = 0; k < TRidsreduced[i].size(); k++){
              System.out.println(TRidsreduced[i].get(k).toString()+" "+p[i].getPoint()[0]+" "+p[i].getPoint()[1]) ;
            }
        }*/

       runMSTclustering(p, idsreduced);
    }
    
    
    public void runMSTclustering(Point[] p,  ArrayList[] idsreduced){

        edges = MCST(p);

        //Random r = new Random();

        double thresh = 0;

        //if(r.nextDouble()>0.5){

            MonteCarlo(edges, p);
        
            thresh = threshold = getPvalue();
       // } thresh = threshold;

        cr = new clusterRun(p,edges,idsreduced,DEC,thresh,s.input.length);
        
    }

    
    public static void main(String[] args){
        Point[] coors = new Point[5000];
        Random r = new Random();
        for(int i = 0; i < coors.length; i++){
            double[] f = new double[3];
            f[0] = r.nextDouble();
            f[1] = r.nextDouble();
            f[2] = r.nextDouble();   
            coors[i] = new Point(f);
        }
        new MSTCluster(false).MCST(coors);
    }
    
    public double[][] MCST(Point[] coors){
        double[][] edges = new double[coors.length-1][3];

        float[] e = new float[edges.length+(edges.length*edges.length)/2];
        short[][] n = new short[e.length][2];
        int itor = 0;
        
        for(int i = 0; i < coors.length-1; i++){
            for(int j = i+1; j < coors.length; j++){
                n[itor][0] = (short)i;
                n[itor][1] = (short)j;
                e[itor++] = (float)Euc(coors[i].getPoint(), coors[j].getPoint());
                

            }
        }

        Kruskal k = new Kruskal();
        k.input_graph(e, n);

        for(int i = 0, j = 0; i < k.e.length; i++){
            if(k.e[i].select == 2){
                edges[j][0] = k.e[i].rndd_plus;
                edges[j][1] = k.e[i].rndd_minus;
                edges[j++][2] = k.e[i].len;
            }
            
        }
        
        return edges;
    }
    
    
    public float Euc(float[] a, float[] b){
        float sum = 0;
        
        for(int i = 0; i < a.length; i++) 
            sum += Math.pow(a[i] - b[i], 2);
        
        return (float)Math.sqrt(sum);
    }
    
    
    private void MonteCarlo(double[][] edges, Point[] coors){
        double pVal = 0, maxX = 0, maxY = 0, maxZ = 0, aveZ = 0, minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
        
        randEdges = new double[MCitors][Math.min(1000,coors.length-1)];
        
        for(int i = 0; i < coors.length; i++){
            if(coors[i].getPoint()[0] > maxX) maxX = coors[i].getPoint()[0];
            if(coors[i].getPoint()[1] > maxY) maxY = coors[i].getPoint()[1];
            if(coors[i].getPoint()[2] > maxZ) maxZ = coors[i].getPoint()[2];
            if(coors[i].getPoint()[0] < minX) minX = coors[i].getPoint()[0];
            if(coors[i].getPoint()[1] < minY) minY = coors[i].getPoint()[1];
            if(coors[i].getPoint()[2] < minZ) minZ = coors[i].getPoint()[2];
            aveZ += coors[i].getPoint()[2];
        }
       
        aveZ /= coors.length;
       // System.out.println(maxZ+" "+minZ+" "+aveZ);
        jpb.setStringPainted(true);
        Thread[] t = new Thread[1];//Runtime.getRuntime().availableProcessors()];
        for(int k = 0; k < t.length; k++){
            t[k] = new Thread(new runMonteCarlo(k*MCitors/t.length, (k+1)*MCitors/t.length, coors, maxX, maxY, maxZ, minX, minY, minZ, aveZ));
            t[k].start();
        }
        try{
            for(int k = 0; k < t.length; k++){
                t[k].join();
            }
        }catch(Exception err){};
        
    }
    
    
    public class runMonteCarlo implements Runnable{
        int iMin, iMax;
        Point[] coors;
        Random r = new Random();
        double maxX, minX, maxY, minY, maxZ, minZ, aveZ;
        public runMonteCarlo(int iMin, int iMax, Point[] coors, double maxX, double maxY, double maxZ, double minX, double minY, double minZ, double aveZ){
            this.iMin = iMin;
            this.iMax = iMax;
            this.coors = coors;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.aveZ = aveZ;
        }
        public void run(){
          //  long t = System.currentTimeMillis();
            double Area = (maxX-minX)*(maxY-minY);
            double density = (double)coors.length/Area;
            double diff = (maxX-minX)/(maxY-minY);
            double newArea = 1000 / density;
            double newX = Math.sqrt(newArea/(1/diff));
            double newY = newArea/newX;
            if(coors.length>=1000){
                //System.out.println(Area+" "+(maxX-minX)+" "+(maxY-minY)+" "+density+" "+diff+" "+newArea+" "+newX+" "+newY);
            }
            for(int i = iMin; i < iMax; i++){
             jpb.setValue((int)(100*((double)jpbItor++)/(double)MCitors));
          //  System.out.println((int)((double)(i+1)/MCitors));
            
            
            Point[] rand = new Point[Math.min(1000,coors.length)]; //maximum of 1000 point simulation
     
  
            for(int j = 0; j < rand.length; j++){
                double[] coors = new double[3];
                
                if(gaussian){
                    coors[0] = r.nextGaussian() * ((coors.length<1000) ? (maxX - minX) : newX);
                    coors[1] = r.nextGaussian() * ((coors.length<1000) ? (maxY - minY) : newY);
                    coors[2] = 0;//r.nextDouble() * (aveZ - minZ);//r.nextGaussian() * (aveZ - minZ);//(maxZ - minZ);
                }else{
                    coors[0] = r.nextDouble() * (maxX - minX);
                    coors[1] = r.nextDouble() * (maxY - minY);
                    coors[2] = 0;//r.nextDouble() * (aveZ - minZ);
                }
                
                rand[j] = new Point(coors);
                
               /* if(Math.sqrt(Math.pow(rand[j].coors[0] - Math.max(maxX-minX, maxY-minY)/2,2) + 
                        Math.pow(rand[j].coors[0] - Math.max(maxX-minX, maxY-minY),2)/2) > Math.max(maxX-minX, maxY-minY)/2)
                {j--; continue;}*/
                        
            }
            double[][] Edges = MCST(rand);
           
            //for(int p = 0; p < Edges.length; p++) System.out.println(Edges[p][2]);
           /* if(i<0){
            
            Immersive_SOM_Core ism = new Immersive_SOM_Core(rand, TRsreduced, TRidsreduced, Edges, 1, xSize, ySize, zSize, DEC, rgb);
        
            Immersive_SOM_Viewer view = new Immersive_SOM_Viewer(ism);
            view.setVisible(true);}*/
            //System.out.println(i);
            double[] sortedEdges = sortEdges(Edges);
            
            for(int k = 0; k < Edges.length; k++) {
                randEdges[i][k] = sortedEdges[k];
             }
           } //System.out.println(System.currentTimeMillis()-t);
        }
    }
    
    public double[] sortEdges(double[][] edges){
        
        double[] sortedEdges = new double[edges.length];
        for(int i = 0; i < edges.length; i++) sortedEdges[i] = edges[i][2];
        Arrays.sort(sortedEdges);
        
        return sortedEdges;
    }
    
    
     private double getPvalue(){
        double longestDist = 0;
        
        double[] sortedEdges = sortEdges(edges);
        
        for(int j = 0; j < sortedEdges.length; j++){
            int count = 0;
           // System.out.println(sortedEdges[j]);
            for(int a = 0; a < randEdges.length; a++){
                for(int b = 0; b < randEdges[a].length; b++){
                    if(randEdges[a][b] <= sortedEdges[j]) count++;
                }
            }
            double frac = (double)count / (double)(randEdges.length * randEdges[0].length);
            //System.out.println(count+" "+randEdges.length+" "+randEdges[0].length+" "+frac+ " "+sortedEdges[sortedEdges.length-1]);
            if(frac > alpha) return longestDist/sortedEdges[sortedEdges.length-1];
            
            longestDist = sortedEdges[j];
                      
        }
        
        return longestDist/sortedEdges[sortedEdges.length-1];
    }
    

    /*
    private static double getPvalue(double[][] edges, double[][] randEdges){
        double longestDist = 0;
        
        double[] sortedEdges = sortEdges(edges);
        double realEdgeSum = 0;
        double min = Double.MAX_VALUE;
        for(int k = 0; k < sortedEdges.length; k++) realEdgeSum += sortedEdges[k];
        
        for(int j = sortedEdges.length-1; j >= 0; j--){
            int count = 0;
          
            //System.out.println(sortedEdges[j]);
            for(int a = 0; a < randEdges.length; a++){
                double localSum = 0;
                for(int b = 0; b <= j; b++){                    
                    localSum += randEdges[a][b];
                    //if(a==0 && j == 0) System.out.println(randEdges[a][b]);                           
                }
                if(localSum < min) min = localSum;
                if(localSum <= realEdgeSum) count++;
            }
            double frac = count / (double)(randEdges.length);
            System.out.println(frac+" "+randEdges.length+" "+count+ " "+longestDist+" "+realEdgeSum+" "+min);
            longestDist = sortedEdges[j];
            if(frac > alpha) return longestDist/sortedEdges[sortedEdges.length-1];
            
            realEdgeSum -= sortedEdges[j];
                      
        }
        
        return longestDist/sortedEdges[sortedEdges.length-1];
    }
    */
    
    public clusterRun getClusterRun() {return cr;}
    
    
    private void printCoors(float[][] coors){
        for(int i = 0; i < coors.length; i++){
            System.out.println(coors[i][0]+" "+coors[i][1]);
        }
    }

}
