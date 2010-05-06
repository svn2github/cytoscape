package org.idekerlab.PanGIAPlugin.utilities;

import org.idekerlab.PanGIAPlugin.utilities.files.*;
import java.io.*;
import java.util.*;
import org.idekerlab.PanGIAPlugin.data.*;

public class ByteConverter
{
	public static void main(String[] args)
	{
		//3^5 = 243   (-127 : 114)
		//115 -> convert to 3^1 set
		//116 -> convert to 3^2 set
		//117 -> convert to 3^3 set
		//118 -> convert to 3^4 set
		//127 -> \n
		
		
		FileUtil.delete("/cellar/data/ghannum/MQTL/GWAS/wtccc/BD/BD.byte");
		for (int i=1;i<=23;i++)
			textToByte("/cellar/data/ghannum/MQTL/GWAS/wtccc/BD/BD_"+i+".txt","/cellar/data/ghannum/MQTL/GWAS/wtccc/BD/BD.byte",true);
		//byteToText("/cellar/data/ghannum/MQTL/GWAS/wtccc/BD/BD_1.byte","/cellar/data/ghannum/MQTL/GWAS/wtccc/BD/BD_1.unbyte");
		
		//System.out.println(convertDataTextToByte(new String[]{"0","0","1","0","1"},3,0,5));
		//System.out.println(new StringVector(convertDataByteToText(convertDataTextToByte(new String[]{"0","1","2","0","2"},3,0,5)+128,3,5)));
		//System.out.println(new StringVector(convertDataByteToText(135,3,5)));
	}
	
	public static void byteToText(String fin, String fout)
	{
		BufferedWriter bw = FileUtil.getBufferedWriter(fout,false);
		
		try
		{
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fin));
			
			int b = bis.read();
			boolean first = true;
			int length = 5;
			
			//int count = 0;
			
			while (b!=-1)
			{
				//System.out.println(b);
				//System.out.println(new StringVector(convertDataByteToText(b,3,5)));
				//count++;
				//if (count==3) System.exit(0);
				
				if (b==255)
				{
					bw.write('\n');
					length = 5;
					first = true;
				}else if (b>242)
				{
					length = b-242;
				}else
				{
					for (String s : convertDataByteToText(b,3,length))
					{
						if (!first) bw.write("\t"+s);
						else
						{
							bw.write(s);
							first = false;
						}
					}
				}
				
				b = bis.read();
			}
			
			
			bis.close();
			bw.close();
		}catch (Exception e) {System.out.println("Error ConvertShortMatrixToBinary(): "+e.getMessage());e.printStackTrace();}
	}
	
	public static void textToByte(String fin, String fout, boolean append)
	{
		BufferedReader br = FileUtil.getBufferedReader(fin);
		
		System.out.println(fin);
		try
		{
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fout,true));
			
			String line = br.readLine();
			
			while (line!=null)
			{
				String[] entries = line.split("\t");

				for (int i=0;i<entries.length;i+=5)
				{
					if (i>=entries.length-4)
					{
						int newLen = entries.length-i;
						bos.write(114+newLen+128);
						bos.write(convertDataTextToByte(entries,3,i,newLen)+128);
						
					}else bos.write(convertDataTextToByte(entries,3,i,5)+128);
				}
				
				bos.write(127+128);
				
				line = br.readLine();
			}
						
			br.close();
			bos.close();
		}catch (Exception e) {System.out.println("Error ConvertShortMatrixToBinary(): "+e.getMessage());e.printStackTrace();System.exit(0);}
		
	}
	
	public static String[] convertDataByteToText(int data, int base, int length)
	{	
		String[] out = new String[length];
 		
		int[] basepow = new int[length];
		basepow[0] = 1;
		
		for (int i=1;i<basepow.length;i++)
			basepow[i] = basepow[i-1]*base;
					
		for (int i=basepow.length-1;i>=0;i--)
		{
			int count = 0;
			while (data>=basepow[i])
			{
				data-=basepow[i];
				count++;
			}
			
			out[i] = String.valueOf(count);
		}
			
		return out;
	}
	
	public static int[] convertDataByteToInt(int data, int base, int length)
	{	
		int[] out = new int[length];
 		
		int[] basepow = new int[length];
		basepow[0] = 1;
		
		for (int i=1;i<basepow.length;i++)
			basepow[i] = basepow[i-1]*base;
					
		for (int i=basepow.length-1;i>=0;i--)
		{
			out[i] = 0;
			while (data>=basepow[i])
			{
				data-=basepow[i];
				out[i]++;
			}
		}
			
		return out;
	}
	
	public static byte convertDataTextToByte(String[] data, int base, int index, int length)
	{
		byte out = -128;
		
		int[] basepow = new int[length];
		basepow[0] = 1;
		
		for (int i=1;i<length;i++)
			basepow[i] = basepow[i-1]*base;
					
		for (int i=0;i<length;i++)
			out += Byte.valueOf(data[index+i])*basepow[i];
		
		return out;
	}
	
	public static byte convertDataTextToByte(DataMatrix d, int row, int col, int base, int length)
	{
		byte out = -128;
		
		int[] basepow = new int[length];
		basepow[0] = 1;
		
		for (int i=1;i<length;i++)
			basepow[i] = basepow[i-1]*base;
					
		for (int i=0;i<length;i++)
			out += (int)d.getAsDouble(row, col+i)*basepow[i];
		
		return out;
	}
	
	public static int lineCount(String file)
	{
		int lineCount = 0;
		
		try
		{
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			
			int b = bis.read();
			
			while (b!=-1)
			{
				if (b==255) lineCount++;
				
				b = bis.read();
			}
			
			
			bis.close();
		}catch (Exception e) {System.out.println("Error ConvertShortMatrixToBinary(): "+e.getMessage());e.printStackTrace();}
		
		return lineCount;
	}
	
	public static byte[] convertToByte(int num, int length)
	{
		byte[] data = new byte[length];
		
		int[] ipow = new int[length];
		ipow[0] = 1;
		for (int i=1;i<length;i++)
			ipow[i] = 256*ipow[i-1];
		
		for (int i=length-1;i>=0;i--)
		{
			data[i] = (byte)(num/ipow[i]);
			num -= data[i]*ipow[i];
			data[i]-=128;
		}
		
		return data;
	}
	
	public static int convertToInteger(byte[] data)
	{
		int num=0;
		
		int[] ipow = new int[data.length];
		ipow[0] = 1;
		for (int i=1;i<data.length;i++)
			ipow[i] = 256*ipow[i-1];
		
		for (int i=0;i<data.length;i++)
			num+=(data[i]+128)*ipow[i];
		
		return num;
	}
	
	public static byte[] convertToByteArray(float data)
	{
		return convertToByteArray(Float.floatToRawIntBits(data));
	}
	
	public static byte[] convertToByteArray(int data)
	{
		
		return new byte[] {
				(byte)((data >> 24) & 0xff),
				(byte)((data >> 16) & 0xff),
				(byte)((data >> 8) & 0xff),
				(byte)((data >> 0) & 0xff),
		};
	}
	
	public static void writeDim(String file, int byteLength, int numRows, int numCols)
	{
		try
		{
			BufferedOutputStream bis = new BufferedOutputStream(new FileOutputStream(file,false));
			
			bis.write(convertToByte(numRows,byteLength));
			bis.write(convertToByte(numCols,byteLength));
						
			bis.close();
		}catch (Exception e) {System.err.println("Error ByteConverter.writeDim(): "+e.getMessage());e.printStackTrace();}
		
	}
}
