package fgraph;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

import cern.colt.list.IntArrayList;

public class SubmodelAlgorithms
{
    private static Logger logger = Logger.getLogger(SubmodelAlgorithms.class.getName());

    /**
     * Merge submodels that share one or more variables.
     *
     * @param models a list of Submodel objects
     * @return a list of merged models
     */
    public static List mergeSubmodels(List models)
    {
        List merged = new ArrayList();
        
        for(int x=0; x < models.size(); x++)
        {
            boolean isDistinct = true;
            Submodel mX = (Submodel) models.get(x);

            for(int y=0; y < merged.size(); y++)
            {
                Submodel mY = (Submodel) merged.get(y);

                if(mY.overlaps(mX))
                {
                    logger.info("overlap found: m=" + x
                                + ", merged=" + y);
                    mY.merge(mX);
                    isDistinct = false;
                    break;
                }
            }

            if(isDistinct)
            {
                logger.info(mX + " is distinct submodel");
                merged.add(mX);
            }
        }

        return merged;
    }

    /**
     * Merge submodels that share one or more variables.
     *
     * @param models a list of Submodel objects
     * @return a list of merged models
     */
    public static List mergeSubmodelsByIndepVar(List models)
    {
        List cliques = new ArrayList();
        
        for(int x=0; x < models.size(); x++)
        {
            boolean isDistinct = true;
            Submodel m = (Submodel) models.get(x);

            for(int y=0; y < cliques.size(); y++)
            {
                IntArrayList c = (IntArrayList) cliques.get(y);

                for(int z=0; z < c.size(); z++)
                {
                    int modelInClique = c.get(z);

                    Submodel sm = (Submodel) models.get(modelInClique);
                    int indepVar = sm.getIndependentVar();

                    if(m.containsVar(indepVar))
                    {
                        logger.info("indep var " + indepVar
                                    + " of model="
                                    + ((Submodel) models.get(modelInClique))
                                    + " in clique=" + (1+y)
                                    + " found in model " + m.getId()
                                    + ". Merging " + m.getId()
                                    + " into clique " + (1+y));

                        c.add(x);
                        isDistinct = false;
                        break;
                    }
                }
            }

            if(isDistinct)
            {
                logger.info("creating new clique "
                            + (1+cliques.size())
                            + " from model " + m.getId());

                IntArrayList newClique = new IntArrayList();
                newClique.add(x);
                cliques.add(newClique);
            }
        }

        logger.info("found " + cliques.size() + " cliques");
        // now merge the cliques into models

        Submodel.resetId();
        List mergedModels = new ArrayList();

        for(int x=0; x < cliques.size(); x++)
        {
            Submodel m = new Submodel();
            mergedModels.add(m);
            
            IntArrayList clique = (IntArrayList) cliques.get(x);

            StringBuffer old = new StringBuffer();
            for(int y=0; y < clique.size(); y++)
            {
                Submodel oldModel = (Submodel) models.get(clique.get(y));
                m.merge(oldModel);

                old.append(clique.get(y));
                if(y !=  (clique.size() -1))
                {
                    old.append(", ");
                }
                
                if(y==0)
                {
                    m.setIndependentVar(oldModel.getIndependentVar());
                }
            }

            logger.info("created new model " + m.getId()
                        + " from old models " + old.toString());
        }

        logger.info("created " + mergedModels.size() + " merged models");
        
        return mergedModels;
    }

    

    /**
     * Merge all submodels into one model
     *
     * @param models a list of Submodel objects
     * @return a list containing the merged model
     */
    public static List mergeAllSubmodels(List models)
    {
        List merged = new ArrayList();

        Submodel m0 = (Submodel) models.get(0);
        for(int x=1; x < models.size(); x++)
        {
            m0.merge((Submodel) models.get(x));
        }

        merged.add(m0);
        return merged;
    }

    /*
    public static filter(List submodels)
    {
        for(ListIterator it = submodels.listIterator(); it.hasNext();)
            {
                Submodel m = (Submodel) it.next(); 
                int numKO = countKO(m);
                m.setNumExplainedKO(numKO);
                if( numKO < 1 )
                {
                    logger.info("Submodel " + m.getId() + " explains no KO's");
                                
                    it.remove();
                }
            }
    }
    */
}
