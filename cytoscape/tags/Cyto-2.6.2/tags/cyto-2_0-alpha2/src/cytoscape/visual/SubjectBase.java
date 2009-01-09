package cytoscape.visual;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.util.ArrayList;

/**
 * Abstract Base Class for Subject in the Subject / Observer Pattern.
 * Also Known as Publisher / Subscriber Pattern.
 *
 * A Subject class notifies all its subscribers whenever its state
 * changes.
 *
 * Note that this code duplicates some code in the AbstractCalculator class.
 * May be a good place to refactor in the future.
 */
public abstract class SubjectBase {
    /**
     * An Array List of All Observers who want to be notified of changes.
     */
    protected ArrayList observers = new ArrayList();

    /**
     * Add a ChangeListener. When the state underlying the
     * calculator changes, all ChangeListeners will be notified.
     *
     * @param listener ChangeListener to add
     */
    public void addChangeListener(ChangeListener listener) {
        this.observers.add(listener);
    }

    /**
     * Remove a ChangeListener from the calcaultor. When the state underlying
     * the calculator changes, all ChangeListeners will be notified.
     *
     * @param listener ChangeListener to add
     */
    public void removeChangeListener(ChangeListener listener) {
        this.observers.remove(listener);
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.
     */
    public void fireStateChanged() {
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = this.observers.size() - 1; i >= 0; i--) {
            ChangeListener listener = (ChangeListener) this.observers.get(i);
            ChangeEvent changeEvent = new ChangeEvent(this);
            listener.stateChanged(changeEvent);
        }
    }
}