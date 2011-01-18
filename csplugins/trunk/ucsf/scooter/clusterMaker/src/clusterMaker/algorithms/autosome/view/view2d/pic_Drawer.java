/*
 * TR2DText.java
 *
 * Created on April 4, 2007, 4:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package clusterMaker.algorithms.autosome.view.view2d;


/**
 *
 * @author a_newman
 */

import java.awt.image.*;
import java.awt.*;
import java.util.*;
import javax.imageio.*;
import java.io.*;
import clusterMaker.algorithms.autosome.cluststruct.*;
import clusterMaker.algorithms.autosome.launch.Settings;
//import motiflogo.motifLogo;
import java.text.*;
import java.math.*;
import java.text.*;

public class pic_Drawer{
    
    public Graphics2D g2d;
    public static BufferedImage rendImage;
    public static boolean sort = true;
    public ArrayList RGB;
    public boolean maxSize = false;
    int w = 0, h = 0;
    private Settings s;
//    private motifLogo ml = new motifLogo(70,150, false,0);
    public static double pixelX = 1;
    public static double pixelY = 0.5;
    public static boolean black = true;
    public static double contrast = 1;
    public static boolean useUserScale = false;
    public static int userScale = 1;
    public static boolean columnLabels = true;
    public static boolean rowLabels = true;
    public static boolean rainbow = false;
    public static boolean colorScaleBar=true;
    public static String[] storeColumnHeaders;
    public static String[][] storeMetaHeaders;
    public static boolean orderCol = false;
    public static int metaLevel=-1;
    public static boolean showClusterSeparators = true;
    public static boolean showClusterColumnSeparators = true;
    public static int extraHeight=0;
    public static int extraWidth=0;
    
    public void setmaxSize(int width, int height){
        this.w = width;
        this.h = height;
        maxSize = true;
    }

    public pic_Drawer(){};
    public pic_Drawer(Settings s) {this.s = s;}
    public pic_Drawer(BufferedImage bi) {rendImage = bi;}
    
    public void close(int itor){
      g2d.dispose();
    }

    
    public void createScatterPlot(dataItem[] d, int scale, boolean red, boolean scalebar, int barNum, int numWidth, boolean meanSig, boolean scaleWholeDataset){

        extraHeight=0;
        extraWidth=0;
        double res = ((useUserScale) ? userScale : scale);
        
        float min = s.inputMin;
        float max = s.inputMax;
        if(!scaleWholeDataset){
            min = Float.MAX_VALUE;
            max = Float.MIN_VALUE;
            for(int i = 0; i < d.length; i++){
            for(int j = 0; j < d[i].getValues().length; j++){
                if(min > d[i].getValues()[j]) min = d[i].getValues()[j];
                if(max < d[i].getValues()[j]) max = d[i].getValues()[j];
            }
        }
        }
                 
        int sbResLen = (int)Math.max(2, (.1*(double)d[0].getValues().length));
        
        int width = (int)((d[0].getValues().length+((scalebar) ? -1+sbResLen : -1))*res);
      //  int height = ((int)Math.min(1000,5*res2));
        double res2 = Math.max(120,scale*10*pixelY);
        int height = ((int)(res2));
        if(((double)height/width)>(.5)) {res2=height=(int)((double)width/2);}
        //System.out.println(height);
        int heightSpace = (int)((double).1*height);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gconf = gd.getDefaultConfiguration(); 
        rendImage = gconf.createCompatibleImage(width, height+heightSpace, BufferedImage.TYPE_INT_RGB);
        g2d = rendImage.createGraphics();
        g2d.setRenderingHint
          (RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON);
        //g2d.setColor(Color.RED);  
        if(!black){
            g2d.fillRect(0,0, rendImage.getWidth(), rendImage.getHeight());
        }

        if(scalebar) addScaleBar(black, max,min, (sbResLen*res)/2, heightSpace, barNum, numWidth);
        
       
        ArrayList sbytemp = new ArrayList();
        for(int i = 0; i < d.length; i++) {
            if(d[i].getValues().length==1 && d[i].getIdentity().equals("spacer")) continue;
            sbytemp.add(new sortByY(d[i].getValues()[0], d[i]));
        }
        sortByY[] sby = new sortByY[sbytemp.size()];
        for(int i = 0; i < sbytemp.size(); i++){
            sby[i]=(sortByY) sbytemp.get(i);
        }
        
        if(!red) Arrays.sort(sby);
        else g2d.setColor(Color.RED);
        
        int[] rgb = new int[3];
        
        rgb[0] = 255;
        rgb[1] = rgb[2] = 0;
        
        //display mean signal///////////////////////
        if(meanSig){
            float[] v = new float[sby[0].d.getValues().length];
            for(int p = 0; p < sby.length; p++){
                for(int q = 0; q < v.length; q++)
                    v[q] += sby[p].d.getValues()[q];
            }
            for(int q = 0; q < v.length; q++)
                v[q] /= sby.length;
            sby = new sortByY[1];
            dataItem meanSignal = new dataItem(v,new String());
            sby[0] = new sortByY(0, meanSignal);
            red = true;
            g2d.setColor(Color.RED);
            g2d.setStroke( new BasicStroke(Float.valueOf(String.valueOf(scale/4)+"f"),java.awt.BasicStroke.CAP_ROUND,java.awt.BasicStroke.JOIN_ROUND ));
        }
        //////////////////////////////////////
        
        for(int i = 0; i < sby.length; i++){
            dataItem data = sby[i].d;
            for(int j = 0; j < data.getValues().length-1; j++){
                int y1 = height-(int)(res2*((data.getValues()[j]-min)/(max-min)))+heightSpace/2;
                int y2 = height-(int)(res2*((data.getValues()[j+1]-min)/(max-min)))+heightSpace/2;
                if(j==0 && !red){
                    float[] f = new float[3];
           
                    int rgbIndex = (int)Math.floor(5.99 * ((double)y1/height))+1;
                    int rgbValue = (int)(255*((5.99*(double)y1/height)-Math.floor(5.99*((double)y1/height))));
                    if(rgbIndex%2==0) rgbValue = 255-rgbValue;
                    if(rgbIndex >= 3) {
                        rgbIndex -= 3;
                    } if(rgbIndex>2) rgbIndex = 0;
                    rgbIndex = 2-rgbIndex;
                    rgb[rgbIndex] = rgbValue;
                   // System.out.println(y1+" "+rgbIndex+" "+rgbValue);
                    f = Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], f);
                    Color c = Color.getHSBColor(f[0],f[1],f[2]);               
                    g2d.setColor(c);
                }
                
                g2d.drawLine((int)((j+((scalebar) ? sbResLen : 0))*res), y1, (int)((j+((scalebar) ? sbResLen+1 : 1))*res), y2);
            }
        }
       
    }
    
    
    //add scalebar to signal plot
    private void addScaleBar(boolean black, float max, float min, double res, int hSpace, int bars, int textWidth){
        int resSpace = 5;
        if(((int)res/resSpace)==0) return;
        if(textWidth>resSpace+1) textWidth = resSpace+1;
        if(black) g2d.setColor(Color.WHITE);
        else g2d.setColor(Color.BLACK);
 
        NumberFormat formatter = new DecimalFormat();
        
        g2d.drawLine(((int)((res/2)+res)), 0+hSpace/2, ((int)((res/2)+res)), rendImage.getHeight()-(hSpace/2));
        for(int i = 0; i < bars; i++){
            if(i==0 || i == bars-1) g2d.drawLine(((int)((res/3)+res)), (int)((i*((double)(rendImage.getHeight()-hSpace)/(bars-1))+(double)hSpace/2)), ((int)((2*(res/3))+res)), (int)(i*((double)(rendImage.getHeight()-hSpace)/(bars-1))+(double)hSpace/2));
            else g2d.drawLine(((int)(((2*res)/5)+res)), (int)((i*((double)(rendImage.getHeight()-hSpace)/(bars-1))+(double)hSpace/2)), ((int)((3*(res/5))+res)), (int)(i*((double)(rendImage.getHeight()-hSpace)/(bars-1))+(double)hSpace/2));            
        }
        for(int i = 0; i < bars ; i++){
            float val = ((max-min)*((float)i/(bars-1)))+min;
            String value = String.valueOf(val);
            if(val/Math.pow(10,textWidth)>1 || val/Math.pow(10,textWidth)<-1){
                
                formatter = new DecimalFormat("0.#E0");

                value = formatter.format(val);
            }else  if(value.length() >= textWidth) {
                formatter = new DecimalFormat("0.###");

                value = formatter.format(val);
                
            }
            if(value.length() >= textWidth) {
                formatter = new DecimalFormat("0.##");

                value = formatter.format(val);
                
            }
            if(value.length() >= textWidth) {
                formatter = new DecimalFormat("0.#");

                value = formatter.format(val);
                
            }
            if(value.length() >= textWidth){
                value = String.valueOf(Math.round(val));
            }
            

            int pxHeight = Math.max((int)(2*res/resSpace),((int)(.25*((double)(rendImage.getHeight()-hSpace)/(bars-1)))));
            if(pxHeight<=0) return;
            
            g2d.drawImage(scaleFontToPixels(value, (int)(res/5),pxHeight,false,0,black), null, (int)(res/resSpace), (int)(((bars-(i+1))*((double)(rendImage.getHeight()-hSpace)/(bars-1)))+((double)hSpace/2)-((double)pxHeight/2)));
        }
    }
    
    
    public void createHeatMap(dataItem[] d, int choice, int scale, boolean conf, boolean lab, boolean scaleWholeDataset, boolean orderColumns, String highlight, boolean manualContrast, float manualMin, float manualMax){
        
        int spacerHeight = 0;
        int currSpacerHeight=0;
        int heightSpacer=0;
        int spacerWidth = 0;
        int currSpacerWidth=0;
        int widthSpacer=0;

        extraHeight=0;
        extraWidth=0;

        ArrayList datatemp = new ArrayList();
        for(int j = 0; j < d.length; j++){
            dataItem datum = new dataItem(d[j].getValues(),d[j].getIdentity());
            if(d[j].getValues().length==1 && d[j].getIdentity().equals("spacer")) {
                    if(showClusterSeparators){
                        spacerHeight++;
                        datatemp.add(datum);
                    }
                }
                else{
                    int con = d[j].getConf();
                    // String[] metaRow = d[j].getMetaData();
                    datum.setConf(con);
                    datatemp.add(datum);
                }
            
           // data[j].setMetaData(metaRow);
        }

        if(showClusterColumnSeparators && s.columnClusters!=null){
            for(int i = 0; i < s.columnClusters.length-1; i++){
                if(s.columnClusters[i]!=s.columnClusters[i+1]) spacerWidth++;
            }
        }

        dataItem[] data = new dataItem[datatemp.size()];
        for(int j = 0; j < datatemp.size(); j++){
             data[j] = (dataItem) datatemp.get(j);
        }

        /*
        if(metaLevel>-1 && s.columnHeaders!=null && s.metaColumnLabels!=null){
                    if(metaLevel<s.metaColumnLabels.length){
                        for(int p=0; p < s.metaColumnLabels[0].length; p++){
                            s.columnHeaders[p+1] = storeMetaHeaders[metaLevel][p];
                            //s.columnHeaders[p+1] = storeColumnHeaders[p+1].concat((s.metaColumnLabels[metaLevel][p].length()>0) ? " ("+s.metaColumnLabels[metaLevel][p]+")" : "");
                        }
                    }
         }else if (metaLevel==-1 && s.columnHeaders!=null){
             for(int i = 0; i < storeColumnHeaders.length; i++)
                 s.columnHeaders[i]=storeColumnHeaders[i];
         }*/
     
        if(!s.readColumns) colorScaleBar=false;

        double res = ((useUserScale) ? userScale : scale);
        if(spacerHeight>0) heightSpacer =(int)Math.min(10,res*.5);
        if(spacerWidth>0) widthSpacer = (int)Math.min(5,res*.5);
 
        float min = s.inputMin;
        float max = s.inputMax;
        if(!manualContrast && (!scaleWholeDataset)){
            min = Float.MAX_VALUE;
            max = Float.MIN_VALUE;
            for(int i = 0; i < data.length; i++){
            for(int j = 0; j < data[i].getValues().length; j++){
                if(data[i].getValues().length==1 && data[i].getIdentity().equals("spacer")) continue;
                if(min > data[i].getValues()[j]) min = data[i].getValues()[j];
                if(max < data[i].getValues()[j]) max = data[i].getValues()[j];
            }
        }
        }



        if(manualContrast){ min = manualMin; max = manualMax;}

        int width = (int)(data[0].getValues().length*res*pixelX);
        int height = (int)((data.length)*res*pixelY);
        if(spacerHeight>0) height += (heightSpacer*spacerHeight);
        extraHeight+=(heightSpacer*spacerHeight);
        if(spacerWidth>0) width += (widthSpacer*spacerWidth);
        extraWidth+=(widthSpacer*spacerWidth);
 
        if(s.confidence && data[0].getConf() != -1 && conf) width += (res*pixelX);
        
        int heightBuffer = 0;

        //get label dimensions
         int maxIDLen = 0;
        if(lab){  

            if(rowLabels){
                for(int i = 0; i < data.length; i++){
                    if(data[i].getValues().length==1 && data[i].getIdentity().equals("spacer") && showClusterSeparators) continue;
                    StringTokenizer st = new StringTokenizer(data[i].getIdentity(),",");
                    String iden = (st.hasMoreTokens()) ? st.nextToken() : "";
                    if(iden.length() > maxIDLen) maxIDLen = iden.length();
                }
                width+=res*Math.ceil((double)maxIDLen/2.5);
                extraWidth=(int)(res*Math.ceil((double)maxIDLen/2.5));
            }

            if(columnLabels){
                int maxColLen = 0;
                if(s.columnHeaders != null){
                    for(int i = s.startData; i < s.columnHeaders.length; i++){
                        String col = s.columnHeaders[i];
                        if(col.length() > maxColLen) {
                            maxColLen = col.length();                          
                        }
                    }
                    if(maxColLen<10 && conf) maxColLen=10;
                    heightBuffer+=Math.ceil((double)maxColLen/3);
                    if(colorScaleBar) heightBuffer+=2;
                    height+=res*heightBuffer;
                }
            }else{
                if(colorScaleBar) {
                   // System.out.println("yeah");
                    heightBuffer+=3;
                    height+=res*heightBuffer;
                }
            }
                
        }
       // if(colorScaleBar) {height+=(int)(res); heightBuffer +=(int)(res);}
       // System.out.println(heightBuffer+" "+res);
        extraHeight+=heightBuffer*res;
      
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        rendImage = gconf.createCompatibleImage(width, height, BufferedImage.TYPE_INT_RGB);
        g2d = rendImage.createGraphics();

        if(!black){
            g2d.fillRect(0,0, rendImage.getWidth(), rendImage.getHeight());
        }


        if(colorScaleBar) addColorScaleBar((int)(res*pixelY), (int)(data[0].getValues().length*res), (int)res, choice, min, max, manualContrast);

        if(s.confidence && data[0].getConf() != -1 && conf) renderConfidence(data, res,heightBuffer, heightSpacer);


//        if(orderCol) data = orderColumns(data);


        if(lab) {
            if(highlight != null) {
                g2d.setColor(Color.YELLOW);
                int index = 0;
                for(; index < data.length; index++) if(data[index].getIdentity().equals(highlight)) break;
                g2d.fillRect((int)Math.ceil((data[0].getValues().length+((s.confidence && data[0].getConf() != -1 && conf) ? 1 : 0))*res*pixelX), (int)Math.ceil(heightBuffer*res+(index*res*pixelY)),  (int)(res*pixelX*Math.min(Math.ceil((double)maxIDLen/3), 10)),  (int)Math.ceil(res*pixelY));
                addLabelsRows(data,res,heightBuffer,conf, index, heightSpacer, widthSpacer*spacerWidth);
            }else if(rowLabels) addLabelsRows(data,res,heightBuffer,conf,-1, heightSpacer, widthSpacer*spacerWidth);
            
            if(heightBuffer>0 && columnLabels) {

                
                addLabelsColumns(res,heightBuffer,conf,widthSpacer);
            }
        }
         double scaleFactor = (contrast-1)*.5*(max-min);
         
         if(!manualContrast) {
             max+=scaleFactor;
             min-=scaleFactor;
         }else{

         }
        
         for(int i = 0; i < data.length; i++){
            for(int j = 0; j < data[i].getValues().length; j++){
                float[] f = new float[3];
                int[] rgb = new int[3];
                rgb[2] = 0;

              if(data[i].getValues().length>1 || !data[i].getIdentity().equals("spacer") && showClusterSeparators){

                double Scal = Math.min(100,(100*((data[i].getValues()[j]-(min))/((max)-(min)))));
                if(Scal<0) Scal=0;
               // if(i==0&&j==0) System.out.println(max+" "+min+" "+Scal+" "+scaleFactor+" "+contrast);
                if(Scal < 50) rgb[1] = (int)(255*((double)50-Scal)/50);
                else rgb[0] = (int)(255*((double)1-((double)(100-Scal)/50)));
                //System.out.println(rgb[0]+" "+rgb[1]+" "+scale);
                if(choice == 3) {
                    rgb[0] = rgb[1] = rgb[2] = (int)(255 * ((double)Scal/100));
                }
                if(choice == 4) {
                    rgb[0] = rgb[1] = 255-(int)((255-95) * ((double)Scal/100));
                    rgb[2] = 255;//(int)(255 * ((double)Scal/100));
                }
                if(rainbow){
                    if(Scal<33){
                        rgb[0] = 0;
                        rgb[1] =  (int)(255 * ((double)Scal/33));
                        rgb[2] = 255;
                    }else if(Scal<=50){
                        rgb[0] = 0;
                        rgb[1] = 255;
                        rgb[2] =  255-(int)(255 * ((double)(Scal-33)/17));
                    }else if(Scal<=67){
                        rgb[0] = (int)(255 * ((double)(Scal-50)/17));
                        rgb[1] = 255;
                        rgb[2] =  0;
                    }else if(Scal<=100){
                        rgb[0] = 255;
                        rgb[1] = 255-(int)(255 * ((double)(Scal-67)/33));
                        rgb[2] =  0;
                    }
                   // System.out.println(rgb[0]+" "+rgb[1]+" "+rgb[2]);
                
                }
                }

                f = Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], f);
                Color c = Color.getHSBColor(f[0],f[1],f[2]);               
                g2d.setColor(c);
                if(data[i].getValues().length==1 && data[i].getIdentity().equals("spacer") && showClusterSeparators) {
                    if(!black) g2d.setColor(Color.WHITE);
                    else g2d.setColor(Color.BLACK);
                    currSpacerHeight++;
                }
               
                g2d.fillRect((int)Math.ceil((j+((s.confidence && data[0].getConf() != -1 && conf) ? 1 : 0))*res*pixelX)+(widthSpacer*currSpacerWidth), (int)Math.ceil(heightBuffer*res+(i*res*pixelY)+(heightSpacer*currSpacerHeight)),  (int)Math.ceil(res*pixelX),  (int)Math.ceil(res*pixelY));
                if(widthSpacer>0 && j < s.columnHeaders.length-1){
                    if(s.columnClusters[j]!=s.columnClusters[j+1]){
                        currSpacerWidth++;
                       // if(!black) g2d.setColor(Color.WHITE);
                      //  else g2d.setColor(Color.BLACK);
                      //  g2d.fillRect((int)Math.ceil((j+((s.confidence && data[0].getConf() != -1 && conf) ? 1 : 0))*res*pixelX)+(widthSpacer*currSpacerWidth), (int)Math.ceil(heightBuffer*res+(i*res*pixelY)+(heightSpacer*currSpacerHeight)),  (int)Math.ceil(res*pixelX),  (int)Math.ceil(res*pixelY));
                    }
                }
            }
            currSpacerWidth=0;
        }
        
    }


    private void addColorScaleBar(int yHeight, int width, int res, int choice, float min, float max, boolean manualContrast){
  

        float[] f = new float[3];
         int[] rgb = new int[3];
         DecimalFormat Format = new DecimalFormat("#");
         Format.setMinimumFractionDigits(2);

         double scaleFactor = (contrast-1)*.5*(max-min);

         if(!manualContrast) {
             max+=scaleFactor;
             min-=scaleFactor;
         }



         for(int i = 0; i < Math.max(5*res,.15*width); i++){

         int Scal = (int)(100*(double)i/(Math.max(5*res,.15*width)));
        // System.out.println(Scal+" "+(int)Math.ceil(1/(double).1*width)+" "+.1*width+" "+width);
         if(Scal < 50) rgb[1] = (int)(255*((double)50-Scal)/50);
                else rgb[0] = (int)(255*((double)1-((double)(100-Scal)/50)));
                //System.out.println(rgb[0]+" "+rgb[1]+" "+scale);
                if(choice == 3) {
                    rgb[0] = rgb[1] = rgb[2] = (int)(255 * ((double)Scal/100));
                }
                if(choice == 4) {
                    rgb[0] = rgb[1] = 255-(int)((255-95) * ((double)Scal/100));
                    rgb[2] = 255;//(int)(255 * ((double)Scal/100));
                }
         if(rainbow){
                    if(Scal<33){
                        rgb[0] = 0;
                        rgb[1] =  (int)(255 * ((double)Scal/33));
                        rgb[2] = 255;
                    }else if(Scal<=50){
                        rgb[0] = 0;
                        rgb[1] = 255;
                        rgb[2] =  255-(int)(255 * ((double)(Scal-33)/17));
                    }else if(Scal<=67){
                        rgb[0] = (int)(255 * ((double)(Scal-50)/17));
                        rgb[1] = 255;
                        rgb[2] =  0;
                    }else if(Scal<=100){
                        rgb[0] = 255;
                        rgb[1] = 255-(int)(255 * ((double)(Scal-67)/33));
                        rgb[2] =  0;
                    }
                    //System.out.println(Scal+" "+rgb[0]+" "+rgb[1]+" "+rgb[2]);

                }

                f = Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], f);
                Color c = Color.getHSBColor(f[0],f[1],f[2]);
                g2d.setColor(c);
                g2d.fillRect((int)(i+(res*2)+(res*.5)),(int)(res), 1,  res);
         }
         String minL = Format.format(min);
         if(minL.equals(".00")) minL="0.00";
         String maxL = Format.format(max);
         BufferedImage label = scaleFontToPixels(minL, (int)(res/3), (int)(pixelX*res), false, (int)res ,black);
         g2d.drawImage(label,null, (int)(res*pixelX*.3)+((min<0) ? (int)(.2*res) : (int)(.5*res)),res);
         label = scaleFontToPixels(maxL, (int)(res/3), (int)(pixelX*res), false, (int)res ,black);
         g2d.drawImage(label,null, (int)(2.3*res)+(int)(pixelX*Math.max(5*res,.15*width))+(int)(.5*res),res);
    }

/*
    private dataItem[] orderColumns(dataItem[] d){

        if(s.columnHeaders!=null && metaLevel==-1){
            for(int i = 0;  i< s.columnHeaders.length; i++){
                    s.columnHeaders[i] = new String(storeColumnHeaders[i]);
            }
        }
        if(s.metaColumnLabels!=null){
            for(int i = 0; i < s.metaColumnLabels.length; i++)
                for(int j = 0; j < s.metaColumnLabels[i].length; j++)
                    s.metaColumnLabels[i][j] = new String(storeMetaHeaders[i][j]);
        }


        dataItem[] transpose = new dataItem[d[0].getValues().length];
        orderVariance[] ov = new orderVariance[transpose.length];
        for(int i = 0; i < transpose.length; i++){
            float[] f = new float[d.length];
            float mean = 0;
            for(int j = 0; j < f.length; j++){
                f[j] = d[j].getValues()[i];
                mean += f[j];
            }
            mean/=f.length;
            transpose[i] = new dataItem(f, String.valueOf(i));
            //System.out.println(s.columnHeaders[i+1] );
            String[] ml = new String[1];
            if(s.metaColumnLabels!=null){
                ml = new String[s.metaColumnLabels.length];
                for(int k = 0; k < ml.length; k++) ml[k]=s.metaColumnLabels[k][i];
            }
            ov[i] = new orderVariance(mean, transpose[i],(s.columnHeaders!=null) ? s.columnHeaders[i+1] : new String(), ml);
        }
        Arrays.sort(ov);

        dataItem[] ordered = new dataItem[d.length];
         for(int j = 0; j < d.length; j++){
            float[] f = new float[ov.length];
            for(int k = 0; k < ov.length; k++){

                f[k] = ov[k].d.getValues()[j];
                if(s.columnHeaders!=null) s.columnHeaders[k+1]=ov[k].colLabel;
                if(s.metaColumnLabels!=null){
                    String[] ml = ov[k].metaLabels;
                    for(int q = 0; q < ml.length; q++) s.metaColumnLabels[q][k] = ml[q];
                }
            }
            int conf = d[j].getConf();
            String[] metaRow = d[j].getMetaData();
            ordered[j] = new dataItem(f,d[j].getIdentity());
            ordered[j].setConf(conf);
            ordered[j].setMetaData(metaRow);

        }
        

        return ordered;
    }

    public class orderVariance implements Comparable{
        float index;
        dataItem d;
        String colLabel;
        String[] metaLabels;
        public orderVariance(float index, dataItem d, String cl, String[] ml){this.index=index; this.d=d;this.colLabel=cl; this.metaLabels=ml;};
        public int compareTo(Object o){
           double dist2 = ((orderVariance)o).index;
           return (index < dist2 ? -1 : (index == dist2 ? 0 : 1));
      }
    }*/
    
    private dataItem[] orderColumns2(dataItem[] d){
        dataItem[] transpose = new dataItem[d[0].getValues().length];
        for(int i = 0; i < transpose.length; i++){
            float[] f = new float[d.length];
            for(int j = 0; j < f.length; j++)
                f[j] = d[j].getValues()[i];
            transpose[i] = new dataItem(f, String.valueOf(i));
        }

       // cluster[] c = new clustering.agglomerative.agglomerative(transpose,1,3).run();
        cluster[] c = linearOrdering(transpose);
        dataItem[] transposeOrder = new dataItem[d.length];
        for(int i = 0; i < d.length; i++) {
            transposeOrder[i] = new dataItem(new float[d[i].getValues().length],d[i].getIdentity());
            transposeOrder[i].setConf(d[i].getConf());
        }
        int count = 0;
        for(int p = 0; p < c.length; p++){
        for(int i = 0; i < c[p].labels.size(); i++){
            StringTokenizer label = new StringTokenizer(c[p].labels.get(i).toString(), ",");
            int id = Integer.valueOf(label.nextToken());

            int j = 0;

            while(label.hasMoreTokens()){
                transposeOrder[j++].getValues()[count] = Float.valueOf(label.nextToken());
                //System.out.println(transposeOrder[j-1].getValues()[i] );
            }
            count++;
        }
        }
  
        return transposeOrder;
    }
    
    
    private cluster[] linearOrdering(dataItem[] t){
        cluster[] c = new cluster[t.length];
        
        ArrayList labels = new ArrayList();
        boolean[] used = new boolean[t.length];
        for(int i = 0; i < used.length; i++) used[i] = false;
        
         StringBuffer sb = new StringBuffer();
            for(int q = 0; q < t[0].getValues().length; q++)
                sb.append(","+t[0].getValues()[q]);

            labels.add(t[0].getIdentity()+sb.toString());
            
        used[0] = true;
        
        dataItem compare = t[0];
        
        for(int j = 0; j < t.length-1; j++){
            
            int bestIndex  = 0;
            float bestDist = Float.MAX_VALUE;
            for(int k = 0; k < t.length; k++){
                if(used[k] || j == k) continue;
                float Dist = 0;
                for(int l = 0; l < compare.getValues().length; l++){
                    Dist += Math.pow(compare.getValues()[l] - t[k].getValues()[l],2);
                }
                if(Dist < bestDist) {
                    bestDist = Dist;
                    bestIndex = k;
                }
            }
            used[bestIndex] = true;
            sb = new StringBuffer();
            for(int q = 0; q < t[bestIndex].getValues().length; q++)
                sb.append(","+t[bestIndex].getValues()[q]);
            //System.out.println(bestIndex);
            labels.add(t[bestIndex].getIdentity()+sb.toString());
            
            compare = t[bestIndex];
        }
        
        for(int i = 0; i < c.length; i++){
            c[i] = new cluster();
            ArrayList a = new ArrayList();
            a.add(labels.get(i));
            c[i].labels = a;
        }
        
        return c;
    }
    
    
    private void renderConfidence(dataItem[] d, double res, int heightBuffer, int heightSpacer){
        int spacerHeight=0;
        for(int i = 0; i < d.length; i++){
            float[] f = new float[3];
            int[] rgb = new int[3];
            double conf = Double.valueOf(d[i].getConf());
            int blue = (int)(255*(conf/100));
            int red = 255 - blue;
            rgb[0] = red;
            rgb[1] = 0;
            rgb[2] = blue;
            f = Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], f);
            Color c = Color.getHSBColor(f[0],f[1],f[2]);               
            g2d.setColor(c);
            if(d[i].getValues().length==1 && d[i].getIdentity().equals("spacer")) {
                if(!black) g2d.setColor(Color.WHITE);
                else g2d.setColor(Color.BLACK);
                spacerHeight++;
            }
            g2d.fillRect(0,(int)Math.ceil(heightBuffer*res+(i*res*pixelY)+(heightSpacer*spacerHeight)),  (int)Math.ceil(res*pixelX),  (int)(Math.ceil(res*pixelY)));
        }
    }
    
    private void addLabelsRows(dataItem[] d, double res, int heightBuffer, boolean conf, int index, int heightSpacer, int widthSpace){
        if((int)res/3==0)return;
        g2d.setColor(Color.WHITE);
        int spacerHeight=0;
        for(int i = 0; i < d.length; i++){
            if(d[i].getValues().length==1 && d[i].getIdentity().equals("spacer")){spacerHeight++; continue;}
            StringTokenizer st = new StringTokenizer(d[i].getIdentity(),",");
            String display = (st.hasMoreTokens()) ? st.nextToken() : "";
            if(display.length() == 0) continue;
            if(i==index) g2d.drawImage(scaleFontToPixels(display, (int)(pixelX*res/3), (int)(pixelY*res), false,0,false),null, (int)((d[0].getValues().length*res*pixelX)+((s.confidence && conf) ? res : 0)+(res/5))+widthSpace,(int)(heightBuffer*res+(i*res*pixelY)+spacerHeight*heightSpacer));
            else g2d.drawImage(scaleFontToPixels(display, (int)(pixelX*res/3), (int)(pixelY*res), false,0,black),null, (int)((d[0].getValues().length*res*pixelX)+((s.confidence && conf) ? res : 0)+(res/5))+widthSpace,(int)(heightBuffer*res+(i*res*pixelY)+spacerHeight*heightSpacer));
        }
    }
    
    private void addLabelsColumns(double res, int heightBuffer, boolean conf, int widthSpacer){
        if((int)res/3==0)return;
        g2d.setColor(Color.WHITE);
        int spacerWidth=0;
        int start = (conf) ? -1 : 0;
        for(int i = s.startData+start, j = start, k=0; i < s.columnHeaders.length; i++,j++,k++){
            String display = (i==s.startData-1) ? "Confidence" : s.columnHeaders[i];
            BufferedImage label = scaleFontToPixels(display, (int)(res/3), (int)(pixelX*res), true, heightBuffer,black);
            g2d.drawImage(label,null, (int)((j*res*pixelX)+((s.confidence && conf) ? res*pixelX : 0))+(widthSpacer*spacerWidth),0);
            if(widthSpacer>0 && k < s.columnHeaders.length-1){
                if(conf && j==start) {k--; continue;}
                if(s.columnClusters[k]!=s.columnClusters[k+1]){
                    spacerWidth++;

                }
            }
        }
    }
    
    public void CreateTRImage(String[] TRs, boolean MA) {

        //if(!MA) TRs = refine(TRs);
        
        
        int maxLen = 0;
        for(int i = 0; i < TRs.length; i++) {
            if(maxLen < TRs[i].length()) maxLen = TRs[i].length();            
        }
        int width = 0;
        int height = 0;
        if(!MA) {
            width = Math.max(200, 14 * maxLen);
            height =  Math.max(200, 18 * TRs.length);
        }
        else {
            width = (14 * maxLen);
            height = (18 * (TRs.length + 2))-15;
        }
        if(maxSize){
            width = Math.max(width, w);
            height = Math.max(height, h);
        }
        if(TRs.length == 1 && TRs[0].equals("")){width = 1; height = 1;}
       
        rendImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);       
        
        g2d = rendImage.createGraphics();
       
       // g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                       
        g2d.setFont(new Font("Arial", Font.BOLD, 18));   
        
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);
                
        int x = 0, y = 18;
        
        for(int i = 0; i < TRs.length; i++){    
             boolean label = true;
             for(int j = 0; j < TRs[i].length(); j++){
                 
                 if(TRs[i].charAt(j) == ',') label = false;
                 
                 float[] f = new float[3];
                 int[] rgb = new int[]{100,100,100};//AA_Colors.getRGB_Phys(TRs[i].charAt(j));
                 
                 if(label) {
                     rgb[0] = rgb[1] = rgb[2] = 200;
                     
                 } 
           
                 int q = 0; 
                 if(TRs[i].charAt(j) == '-') q = 4;
                 if(TRs[i].charAt(j) == 'I') q = 4;
                 if(TRs[i].charAt(j) == 'W') q = -2;
                 f = Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], f);
                 g2d.setColor(Color.getHSBColor(f[0],f[1],f[2]));                      
                 g2d.drawString(String.valueOf(TRs[i].charAt(j)), x + q, y);                 
                 x+= 14;

             }
             y += 18;
             x = 0;
        }

    }
  /*
   public void createMotifLogo(dataItem[] d, int scale, boolean entropy){
  
            String[] input = new String[d.length];
            for(int i = 0; i < input.length; i++) input[i] = new StringTokenizer(d[i].getIdentity(),",").nextToken();
            rendImage = ml.runLogo(input,Math.max(1,((double)scale/3)-4), entropy);
        
   }*/

    
    
    public String[] refine(String[] trs){
        HashMap h = new HashMap();
        ArrayList temp = new ArrayList();
        ArrayList rgbTemp = new ArrayList();

        for(int i = 0; i < trs.length; i++){

            if(!h.containsKey(trs[i])) {
                h.put(trs[i], trs[i]);
                temp.add(trs[i]);
                rgbTemp.add(new int[1][1]);
            }
        }

        trRGB[] trrgb = new trRGB[temp.size()];
        for(int j = 0; j < temp.size(); j++){
            trrgb[j] = new trRGB(temp.get(j).toString(), ((int[][])rgbTemp.get(j)));
        }
        
        if(sort){
        Arrays.sort(trrgb, new Comparator()
            { 
                public int compare(Object o1, Object o2)
                {
                    return (((trRGB) o1).tr.length() - ((trRGB) o2).tr.length());
                }
            });
        }

        trs = new String[trrgb.length];
        RGB = new ArrayList();
        for(int i = 0; i < trrgb.length; i++){
            trs[i] = trrgb[i].tr;
            RGB.add(trrgb[i].rgb);
        }
              
         
         return trs;
    }

    private class trRGB{
        String tr;
        int[][] rgb;
        public trRGB(String tr, int[][] rgb){
            this.tr = tr;
            this.rgb = rgb;
        }
    }
    
    private class sortByY implements Comparable{
        float y1;
        dataItem d;
        public sortByY(float y1, dataItem d) {this.y1 = y1; this.d = d;}
        public int compareTo(Object o){
           double y2 = ((sortByY)o).y1;
           return (y1 > y2 ? -1 : (y1 == y2 ? 0 : 1));
       }
    }

    

    private BufferedImage scaleFontToPixels(String text, int pxWidth, int pxHeight, boolean rotate, int heightBuffer, boolean white){
        if(pxWidth<=0 || pxHeight<=0) return new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
        BufferedImage string = (!rotate) ? new BufferedImage(2*pxWidth*text.length(),2*pxHeight, BufferedImage.TYPE_INT_ARGB)
        : new BufferedImage(pxHeight*2,(int)(pxHeight*heightBuffer), BufferedImage.TYPE_INT_ARGB);
       
        Graphics2D st = string.createGraphics();
        
        st.setRenderingHint
          (RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON);
       
                Font ft = new Font("Arial", Font.BOLD, pxHeight);//+(int)((double)yscale/2.7));
                st.setFont(ft);
                //set color of current character
                float[] f = new float[3];
                f = Color.RGBtoHSB(255, 255, 255, f);         
                if(white)st.setColor(Color.getHSBColor(f[0],f[1],f[2]));
                else st.setColor(Color.BLACK);
  
                 FontMetrics fm = st.getFontMetrics(ft);
  
                double width = fm.stringWidth(text);

                //vertical size of character
                double ySize = fm.getHeight();
                //rescale character width to 'xscale' : make all character widths equal
                double xfactor = (double)(pxWidth*text.length())/width;
                //rescale y character height to 'yscale' s.t. all characters with same frequency have same height
                double yfactor = pxHeight/ySize;

                 //get affine transform for g2d object
                java.awt.geom.AffineTransform at = st.getTransform();
                //scale character based on frequency in y axis and width in x axis s.t. all character widths are equal
                if(!rotate) at.scale(xfactor,yfactor);

                if(rotate) {
                    at.rotate(270*Math.PI/180);
                    at.scale(xfactor,yfactor);
                }
                //apply transform
                st.setTransform(at);

                //draw scaled character
                if(!rotate) st.drawString(text, 0, (int)(pxHeight));
                else {
                   st.drawString(text,-(int)((((double)1/xfactor)*pxHeight*heightBuffer)) ,pxHeight);
                }
                                
                
         
        return string;
    

    }
    
    
}

