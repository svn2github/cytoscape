public class MPMain
{
    protected static String usage =
        "Usage: <max path len> \n" +
        "       <interaction sif file>\n" +
        "       <expression pvals file>\n" +
        "       <expression pval threshold>\n" +
        "       <Cytoscape-format edge attribute file>\n" +
        "       <protein-DNA pvalue threshold: -1 to use all>\n" +
        "       <output directory>\n" +
        "       <output base filename>\n" +
        "       <ko cutoff>\n" +
        "       [print|profile <optional>]\n"
        ;
    
    public static void main(String[] args)
    {
        if(args.length < 8)
        {
            System.err.println(usage);
            System.exit(1);
        }
        
        int pathLength = Integer.parseInt(args[0]);

        String i = args[1];
        String exp = args[2];
        double expThresh = Double.parseDouble(args[3]);
        String edge = args[4];
        double edgeThresh = Double.parseDouble(args[5]);
        String outdir = args[6];
        String outfile = args[7];
        int explainCutoff = Integer.parseInt(args[8]);

        String type = "";
        if(args.length >= 10)
        {
            type = args[9];
        }

        String candidate=null;
        if(args.length >= 11)
        {
            candidate = args[10];
        }
        
        try
        {
            MaxProduct mp;
          
            if (type.equalsIgnoreCase("print"))
            {
                System.out.println("### Will print factor graph");
                mp = new PrintFGMaxProduct();
            }
            else if (type.equalsIgnoreCase("profile"))
            {
                System.out.println("### Will profile factor graph");
                mp = new ProfileMaxProduct();
            }
            else
            {
                mp = new MaxProduct();
            }

            if(candidate != null)
            {
                mp.setInteractionFile(i, candidate);
            }
            else
            {
                mp.setInteractionFile(i);
            }
            
            mp.setMaxPathLength(pathLength);
            mp.setKOExplainCutoff(explainCutoff);
            mp.setExpressionFile(exp, expThresh);
            mp.setEdgeFile(edge, edgeThresh);
            mp.run(outdir, outfile);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}
