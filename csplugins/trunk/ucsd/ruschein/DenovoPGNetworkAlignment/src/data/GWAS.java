package data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import utilities.files.FileUtil;

/**
 * This class is meant to deal with cases and controls for WTCCC data-set
 * @author rsrivas
 *
 */
public class GWAS implements Externalizable
{
	
	private ByteMatrix caseMatrix;
	private ByteMatrix controlMatrix;
	public static final byte lineDelimiter = 3;
	public static final int chrm10 = 16437;
	public static final int chrm11 = 14629;
	public static final int chrm12 = 13271;
	public static final int chrm13 = 9058;
	public static final int chrm14 = 8135;
	public static final int chrm15 = 8205;
	public static final int chrm16 = 7749;
	public static final int chrm17 = 7534;
	public static final int chrm18 = 6723;
	public static final int chrm19 = 4669;
	public static final int chrm1 = 25786;
	public static final int chrm20 = 7634;
	public static final int chrm21 = 3943;
	public static final int chrm22 = 4178;
	public static final int chrm2 = 21138;
	public static final int chrm3 = 18288;
	public static final int chrm4 = 13915;
	public static final int chrm5 = 14902;
	public static final int chrm6 = 17988;
	public static final int chrm7 = 15093;
	public static final int chrm8 = 12561;
	public static final int chrm9 = 13151;
	public static final int chrmX = 5539;
	public static final int ALL = 270526;
	public static final int BD = 1868;
	public static final int CAD = 1926;
	public static final int CD = 1748;
	public static final int control_58C = 1480;
	public static final int control_NBS = 1458;
	public static final int combinedControls = GWAS.control_58C+GWAS.control_NBS;
	public static final int HT = 1952;
	public static final int RA = 1860;
	public static final int T1D = 1963;
	public static final int T2D = 1924;
	public static final int allDiseases = 16179;


	public GWAS(String caseMatrixFile, String controlMatrixFile, int markerSet, int diseaseSet, int controlSet)
	{

		caseMatrix = GWAS.readInBinaryFile(caseMatrixFile, markerSet, diseaseSet);
		controlMatrix = GWAS.readInBinaryFile(controlMatrixFile, markerSet, controlSet);
	}
	
	public GWAS(String caseMatrixFile, String controlMatrixFile, int markerSet, int diseaseSet)
	{
		caseMatrix = GWAS.readInBinaryFile(caseMatrixFile, markerSet, diseaseSet);
		controlMatrix = GWAS.readInBinaryFile(controlMatrixFile, markerSet, GWAS.combinedControls);
	}
	
	public GWAS(String caseMatrixFile, String controlMatrixFile, int diseaseSet)
	{
		caseMatrix = GWAS.readInBinaryFile(caseMatrixFile, GWAS.ALL, diseaseSet);
		controlMatrix = GWAS.readInBinaryFile(caseMatrixFile, GWAS.ALL, GWAS.combinedControls);
	}
	
	/**
	 * Copy constructor
	 * @param newCaseMatrix Case matrix to copy
	 * @param newControlMatrix Control matrix to copy
	 */
	public GWAS(ByteMatrix newCaseMatrix, ByteMatrix newControlMatrix)
	{
		caseMatrix = new ByteMatrix(newCaseMatrix);
		controlMatrix = new ByteMatrix(newControlMatrix);
	}
	
	/**
	 * Utility function in order to 
	 * @param fileName
	 */
	public static ByteMatrix readInBinaryFile (String fileName, int markers, int disease)
	{
		ByteMatrix data = new ByteMatrix(markers,disease,0);
		try 
		{
			 DataInputStream stream = new DataInputStream(new BufferedInputStream(new FileInputStream("/cellar/users/rsrivas/Data/WTCCC/Data/full_data_files/BD/test2.dat")));
			 InflaterInputStream in = new InflaterInputStream(stream);
			 ByteArrayOutputStream bout = new ByteArrayOutputStream(1024);
			 int b;
			 int row = 0;
			 while((b=in.read())!=-1)
			 {
				 if(b!=GWAS.lineDelimiter)
					 bout.write(b);
				 else
				 {
					 data.setRow(row, new ByteVector(bout.toByteArray()));
					 row++;
					 bout = new ByteArrayOutputStream(1024);
				 }
			 }
			 in.close();
			 bout.close();
			
			/*BinaryFile bin = new BinaryFile(fileName,"r");
			boolean EOF = false;
			int row=0;
			int col=0;
			while(!EOF)
			{
				try
				{
					byte read = bin.readByte();
					//System.out.println(blah1);
					if(read!=GWAS.lineDelimiter)
					{
						//System.out.println(read+"\t"+col);
						data.set(row, col, read);
						col+=1;
					}
					else
					{
						System.out.println(read);
						row+=1;
						if(row%10000==0) System.out.println(row+" out of "+markers);
						col=0;
					}
				}
				catch (EOFException e) 
				{
					EOF = true;
				}
			}*/
			return data;
		}
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
			System.exit(0);
			return null;
		}
	}
	
	/**
	 * Utility function in order to convert tab delimited files to a single binary file
	 */
	public static void convertAllTabFiles2BinaryFile (String tabFileDir, int markerCount, int peopleCount, String diseaseID, String outputFileName)
	{
		//Initialize Byte Matrix
		ByteMatrix data = new ByteMatrix(GWAS.chrm1,GWAS.BD);
		
		//Iterate through all chromosomes in order to read in massive byte matrix
		System.out.println("Official read in : ");
		//for(Chromosomes chrm : Chromosomes.values())
		String chrm = "1";
		for(int k=0; k<1; k++)
		{
			BufferedReader in = FileUtil.getBufferedReader(tabFileDir+"/"+diseaseID+"_"+chrm.toString()+".txt");
			System.out.println("Working on "+chrm.toString());
			String line;
			int lineNum = 0;
			int total = FileUtil.countLines(tabFileDir+"/"+diseaseID+"_"+chrm.toString()+".txt");
			try {
				//while((line=in.readLine())!=null)
				for(int i=0; i<GWAS.chrm1; i++)
				{
					line = in.readLine();
					String[] fields = line.split("\t");
					for(int col=0; col<fields.length; col++)
					//for(int col=0; col<5; col++)
					{
						byte b = Byte.valueOf(fields[col]);
						data.set(lineNum, col, b);
					}
					lineNum+=1;
					if(lineNum%1000==0) System.out.println("\t"+lineNum+" out of "+total);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//Output to binary file
		System.out.println("Outputting!");
		
		try
		{
			Deflater d = new Deflater(Deflater.HUFFMAN_ONLY);
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("/cellar/users/rsrivas/Data/WTCCC/Data/full_data_files/BD/test2.dat")));
			DeflaterOutputStream dout = new DeflaterOutputStream(out,d);
			for(int i=0; i<data.dim(0); i++)
			{
				for(int j=0; j<data.dim(1); j++)
					dout.write(data.getRow(i).asByteArray());	
				dout.write(GWAS.lineDelimiter);
				if(i%10==0) System.out.println("\t"+i+" out of "+data.dim(0));
			}
			dout.close();
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
			System.exit(0);
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,ClassNotFoundException 
	{
		
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException 
	{
		
	}
	

}
