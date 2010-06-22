package BiNGO;

import java.util.HashMap;

/**
 * Created by
 * User: risserlin
 * Date: Jun 13, 2006
 * Time: 1:00:50 PM
 */
public class BingoAlgorithm {

    /**
     * constant string for the none-label in the combobox.
     */
    public static final String NONE = "---";
    public static final String CUSTOM = "Custom...";
    /**
     * constant string for the name Hypergeometric Distribution.
     */
    public static final String HYPERGEOMETRIC = "Hypergeometric test";
    /**
     * constant string for the name Binomial Test.
     */
    public static final String BINOMIAL = "Binomial test";
    /**
     * constant string for the parent-child intersection test.
     */
    public static final String PARENT_CHILD_INTERSECTION = "Parent-child-intersection test";
    /**
     * constant string for the Benjamini & Hochberg FDR correction.
     */
    public static final String OVERSTRING = "Overrepresentation";

    /**
     * constant string for the Benjamini & Hochberg FDR correction.
     */
    public static final String BENJAMINI_HOCHBERG_FDR = "Benjamini & Hochberg False Discovery Rate (FDR) correction";
    /**
     * constant string for the Bonferroni FWER correction.
     */
    public static final String BONFERRONI = "Bonferroni Family-Wise Error Rate (FWER) correction";

    /**
     * constant strings for the checking versus option.
     */
    public static final String GRAPH = "Test cluster versus network";
    public static final String GENOME = "Test cluster versus whole annotation";
    public static final String VIZSTRING = "Visualization";

    /**
     * constant string for the none-label in the combobox.
     */
    public static final String CATEGORY = "All categories";
    public static final String CATEGORY_BEFORE_CORRECTION = "Overrepresented categories before correction";
    public static final String CATEGORY_CORRECTION = "Overrepresented categories after correction";

    //parameters to use for the calculations.
    private BingoParameters params;

    public BingoAlgorithm(BingoParameters params) {
        this.params = params;
    }

    public CalculateTestTask calculate_distribution() {
        CalculateTestTask test = null;
        HashMap testMap = null;

        if (params.getTest().equals(NONE)){
            test = new StandardDistributionCount(params.getAnnotation(), params.getOntology(), params.getSelectedNodes(), params.getAllNodes(), params.getAlias());
        } else if (params.getTest().equals(HYPERGEOMETRIC)) {
            if (params.getOverOrUnder().equals(OVERSTRING)) {
                test = new HypergeometricTestCalculate(new StandardDistributionCount(params.getAnnotation(), params.getOntology(), params.getSelectedNodes(), params.getAllNodes(), params.getAlias()));
            } else {
                test = new HypergeometricTestCalculateUnder(new StandardDistributionCountNeg(params.getAnnotation(), params.getOntology(), params.getSelectedNodes(), params.getAllNodes(), params.getAlias()));
            }
        } else if (params.getTest().equals(BINOMIAL)) {
            if (params.getOverOrUnder().equals(OVERSTRING)) {
                test = new BinomialTestCalculate(new StandardDistributionCount(params.getAnnotation(), params.getOntology(), params.getSelectedNodes(), params.getAllNodes(), params.getAlias()));
            } else {
                test = new BinomialTestCalculateUnder(new StandardDistributionCountNeg(params.getAnnotation(), params.getOntology(), params.getSelectedNodes(), params.getAllNodes(), params.getAlias()));
            }        
        } else if (params.getTest().equals(PARENT_CHILD_INTERSECTION)) {
            if (params.getOverOrUnder().equals(OVERSTRING)) {
                test = new HypergeometricTestCalculate(new ParentChildIntersectionCount(params.getAnnotation(), params.getOntology(), params.getSelectedNodes(), params.getAllNodes(), params.getAlias()));
            } else {
                //to be implemented
            }
        }
        
            //  Configure JTask
            /*JTaskConfig config = new JTaskConfig();

            //  Show Cancel/Close Buttons
            config.displayCancelButton(true);
            config.displayStatus(true);
            */
            //  Execute Task via TaskManager
            //  This automatically pops-open a JTask Dialog Box.
            //  This method will block until the JTask Dialog Box is disposed.
            //boolean success = TaskManager.executeTask(test, config);
        return test;
    }

    public CalculateCorrectionTask calculate_corrections(HashMap testMap) {
        HashMap correctionMap = null;
        CalculateCorrectionTask correction = null;

        if (params.getCorrection().equals(NONE)){
        } else {
            if (params.getCorrection().equals(BONFERRONI)) {
                correction = new Bonferroni(testMap, params.getSignificance().toString());
            } else if (params.getCorrection().equals(BENJAMINI_HOCHBERG_FDR)) {
                correction = new BenjaminiHochbergFDR(testMap, params.getSignificance().toString());
            } else {
                correctionMap = null;
            }
            //  Configure JTask
            /*JTaskConfig config = new JTaskConfig();

            //  Show Cancel/Close Buttons
            config.displayCancelButton(true);
            config.displayStatus(true); */

            //  Execute Task via TaskManager
            //  This automatically pops-open a JTask Dialog Box.
            //  This method will block until the JTask Dialog Box is disposed.
            //only perform if a correction method has been chosen

        }
        return correction;
    }
}



