package de.costmatrixcreation.main;

public class Config {
	
	public static boolean gui;
	
	public static float threshold;
	
	public static int costModel;
	
	public static String blastFile;
	
	public static String fastaFile;
	
	public static String similarityFile;
	
	public static String costMatrixDirectory;
	
	public static boolean createSimilarityFile;
	
	public static boolean splitAndWriteCostMatrices;
	
	public static double penaltyForMultipleHighScoringPairs;
	
	public static double blastCutoff;
	
	public static int coverageFactor;
	
	public static int linesInSimilarityFile;
	
	public static float upperBound;
	
	public static boolean reducedMatrix;
	
	
	public static void init(Args options){
		
		createSimilarityFile = true;
		
		splitAndWriteCostMatrices = true;
		
		blastCutoff = 0.01;
		
		penaltyForMultipleHighScoringPairs = blastCutoff; 
		
		coverageFactor = 0;
		
		linesInSimilarityFile = 0;
		
		upperBound = 100;
		
		threshold = 10;
		
		costModel = 0;
		
		gui = true;
		
		reducedMatrix = true;
		
		setOptionalConfigurationVariables(options);
		
		if(!gui) testAndPrintErrors();
		
	}
	
	private static void setOptionalConfigurationVariables(Args options) {
		
			try {
				reducedMatrix = options.getBoolValue("rm");
			} catch (Exception e) {
			}
		
			try {
				gui = options.getBoolValue("gui");
			} catch (Exception e) {
			}
		
		
			try{
				createSimilarityFile = options.getBoolValue("cs");
			} catch (ArgsParseException e) {
			}
			
			try{
				splitAndWriteCostMatrices = options.getBoolValue("sp");
			} catch (ArgsParseException e) {
			}
			
			try {
				blastFile = options.getStringValue("b");
			} catch (ArgsParseException e) {
			}
			
			try {
				similarityFile = options.getStringValue("s");
			} catch (ArgsParseException e) {
			}
			
			try {
				fastaFile = options.getStringValue("f");
			} catch (ArgsParseException e) {
			}
			  
			try {
				costMatrixDirectory = options.getStringValue("c");
			} catch (ArgsParseException e) {
			}
			
			try {
				threshold = options.getFloatValue("t");
			} catch (ArgsParseException e) {
			}
			
			try {
				costModel = options.getIntValue("m");
			} catch (ArgsParseException e) {
			}
			
			try {
				blastCutoff = options.getDoubleValue("bc");
			} catch (ArgsParseException e) {
			}
			
			try {
				penaltyForMultipleHighScoringPairs = options.getDoubleValue("p");
			} catch (ArgsParseException e) {
			}
			
			try{
				coverageFactor = options.getIntValue("cf");
			} catch (ArgsParseException e) {
			}
			
			try{
				upperBound = options.getFloatValue("ub");
			} catch (ArgsParseException e) {
			}
			
	}
	
	private static void testAndPrintErrors(){
		
		boolean error = false;
		
		if(createSimilarityFile){
			
			if(blastFile==null||similarityFile==null||fastaFile==null){
				System.out.println("You have at least to specify: \n -blastFile (-b)  \n -similarityFile (-s) \n -fastaFile (-f)  \n -costModel (-m)");
				error = true;
			}
			
		}
		
		if(splitAndWriteCostMatrices){
			
			if(similarityFile==null||costMatrixDirectory==null){
				if(error){
					System.out.println(" -costMatrixDirectory (-c)  \n -threshold (-t)");
				}else{
					System.out.println("You have at least to specify: \n -similarityFile (-s) \n -costMatrixDirectory (-c)  \n -threshold (-t)");
					error = true;
				}
			}
			
		}
		
		if(error){
			printVariables();
			CostMatrixCreator.printUsage();
			System.exit(1);
		}
		
	}

	private static void printVariables() {
		
		System.out.println("threshold = " + threshold);
		
		System.out.println("costModel = " + costModel);
		
		System.out.println("blastFile = " + blastFile);
		
		System.out.println("fastaFile = " + fastaFile);
		
		System.out.println("similarityFile = " + similarityFile);
		
		System.out.println("costMatrixDirectory = " + costMatrixDirectory);
		
		System.out.println("createSimilarityFile = " + createSimilarityFile);
		
		System.out.println("splitAndWriteCostMatrices = " + splitAndWriteCostMatrices);
		
		System.out.println("penaltyForMultipleHighScoringPairs = " + penaltyForMultipleHighScoringPairs);
		
		System.out.println("blastCutoff = " + blastCutoff);
		
		System.out.println("coverageFactor = " + coverageFactor);
		
		System.out.println("upperBound = " + upperBound);

			
	}

}
