package cytoscape.task.ui;

import java.awt.*;

/**
 * Used to configure the JTask UI component.
 */
public class JTaskConfig {

    /**
     * Display the Status Field.
     */
    private boolean statusFlag = false;

    /**
     * Display the Time Elapsed Field.
     */
    private boolean timeElapsedFlag = false;

    /**
     * Display the Estimated Time Remaining Field.
     */
    private boolean timeRemainingFlag = false;

    /**
     * Display User Cancel / Close Buttons.
     */
    private boolean userButtonFlag = false;

    /**
     * Disposes of Component When Task Completes.
     */
    private boolean autoDispose = true;

    /**
     * Specifies the amount of time to wait before deciding whether or not
     * to show the TaskMonitorUI component.
     */
    private int millisToDecideToPopup = 0;

    /**
     * Owner, such as a JFrame.
     * Primarily used to center the JTask component relative to the owner.
     */
    private Container owner;

    public void displayStatus(boolean flag) {
        this.statusFlag = flag;
    }

    public void displayTimeElapsed(boolean flag) {
        this.timeElapsedFlag = flag;
    }

    public void displayTimeRemaining(boolean flag) {
        this.timeRemainingFlag = flag;
    }

    public void displayUserButtons(boolean flag) {
        this.userButtonFlag = flag;
    }

    public void setAutoDispose(boolean flag) {
        this.autoDispose = flag;
    }

    public void setMillisToDecideToPopup(int ms) {
        this.millisToDecideToPopup = ms;
    }

    public void setOwner(Container owner) {
        this.owner = owner;
    }

    boolean getStatusFlag() {
        return this.statusFlag;
    }

    boolean getTimeElapsedFlag() {
        return this.timeElapsedFlag;
    }

    boolean getTimeRemainingFlag() {
        return this.timeRemainingFlag;
    }

    boolean getUserButtonFlag() {
        return this.userButtonFlag;
    }

    boolean getAutoDispose() {
        return autoDispose;
    }

    int getMillisToDecideToPopup() {
        return millisToDecideToPopup;
    }

    Container getOwner() {
        return owner;
    }
}