package fgraph;

import java.io.FileInputStream;

import java.util.Properties;
import java.util.MissingResourceException;

public class MPMain
{
    private static final String cname = MPMain.class.getName();
    
    protected static String usage = "Usage: <properties-file> \n";
    
    public static void main(String[] args)
    {
        if(args.length != 1)
        {
            System.err.println(usage);
            System.exit(1);
        }

        String propFile = args[0];
        try
        {
            Properties defaults = new Properties();            
            defaults.setProperty("type", "");
            defaults.setProperty("candidate.genes", "");
            defaults.setProperty("yeang.data.format", "false");

            Properties props = new Properties(defaults);
            
            try
            {
                FileInputStream pin = new FileInputStream(propFile);
                props.load(pin);
            }
            catch(Exception e)
            {
                System.err.println("Error reading properties file: "
                                   + propFile);
                e.printStackTrace();
                System.exit(1);
            }
            
            int pathLength = readInt(props, "max.path.length");
            String interactionSif = readString(props, "interaction.network");
            String exp = readString(props, "expression.file");
            double expThresh = readDouble(props, "expression.threshold");
            String edge = readString(props, "edge.attributes");
            double edgeThresh = readDouble(props, "protein-DNA.threshold");
            String outdir = readString(props, "output.dir");
            String outfile = readString(props, "output.filename");
            int explainCutoff = readInt(props, "min.ko.per.model");

            String type = readString(props, "type");
            String candidate = readString(props, "candidate.genes");

            boolean decompose = readBoolean(props, "decomposeModel");
            boolean yeangDataFormat = readBoolean(props, "yeang.data.format");

            MaxProduct mp;
            
            if (type.equalsIgnoreCase("print"))
            {
                System.out.println("### Will print factor graph");
                mp = new PrintFGMaxProduct();
            }
            /*
            else if (type.equalsIgnoreCase("profile"))
            {
                System.out.println("### Will profile factor graph");
                mp = new ProfileMaxProduct();
            }
            */
            else
            {
                mp = new MaxProduct();
            }

            /*
            if(!candidate.equals(""))
            {
                mp.setInteractionFile(interactionSif, candidate);
            }
            else
            {
            mp.setInteractionFile(interactionSif);
            }
            */

            mp.setInteractionFile(interactionSif);
            
            mp.setDecomposeModel(decompose);
            mp.setMaxPathLength(pathLength);
            mp.setKOExplainCutoff(explainCutoff);
            mp.setExpressionFile(exp, expThresh);
            mp.setEdgeFile(edge, edgeThresh);
            mp.run(outdir, outfile, yeangDataFormat);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static String readString(Properties p, String name)
        throws MissingResourceException
    {
        String s = p.getProperty(name);

        if(s == null)
        {
            throw new MissingResourceException(name, cname, name);
        }

        return s;
    }


    private static int readInt(Properties p, String name)
        throws MissingResourceException
    {
        String s = readString(p, name);

        return Integer.parseInt(s);
    }


    private static double readDouble(Properties p, String name)
        throws MissingResourceException
    {
        String s = readString(p, name);

        return Double.parseDouble(s);
    }

    
    private static boolean readBoolean(Properties p, String name)
        throws MissingResourceException
    {
        String s = readString(p, name);

        return Boolean.valueOf(s).booleanValue();
    }

    
}
