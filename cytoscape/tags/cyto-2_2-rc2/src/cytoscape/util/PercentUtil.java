package cytoscape.util;

/**
 * Helper Class for Calculating Percentage of Multi-Step Processes.
 */
public class PercentUtil {
    private double stepMultiple;

    /**
     * Constructor.
     *
     * @param numSteps Number of Steps in Process.
     */
    public PercentUtil(int numSteps) {
        this.stepMultiple = 100.0 / numSteps;
    }

    /**
     * Calculates Global Percent Based on Current Step, Current Value,
     * and MaxValue.
     *
     * @param currentStep  Current Step Number.
     * @param currentValue Current Value Number.
     * @param maxValue     Max Value Number.
     * @return an integer value between 0..100.
     */
    public int getGlobalPercent(int currentStep, int currentValue,
            int maxValue) {
        double currentPercent = calcCurrentPercent(currentValue, maxValue);
        double value = (stepMultiple * currentStep) + currentPercent;
        return (int) value;
    }

    /**
     * Calculates Current Local Percentage.
     *
     * @param currentValue Current Value Number.
     * @param maxValue     Max Value Number.
     * @return an integer value between 0..100.
     */
    private double calcCurrentPercent(int currentValue, int maxValue) {
        return (currentValue * stepMultiple) / maxValue;
    }
}