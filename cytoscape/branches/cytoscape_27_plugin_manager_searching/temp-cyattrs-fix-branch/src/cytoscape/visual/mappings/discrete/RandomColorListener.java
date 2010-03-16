package cytoscape.visual.mappings.discrete;

import cytoscape.visual.mappings.DiscreteMapping;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.*;

/**
 *  User wants to Seed the Discrete Mapper with Random Color Values.
 */
public class RandomColorListener implements ActionListener {
    private DiscreteMapping dm;
    private TreeSet mappedKeys;

    /**
     * Constructs a ValueChangeListener.
     */
    public RandomColorListener(DiscreteMapping dm, TreeSet mappedKeys) {
        this.dm = dm;
        this.mappedKeys = mappedKeys;
    }

    /**
     *  User wants to Seed the Discrete Mapper with Random Color Values.
     */
    public void actionPerformed (ActionEvent e) {
        Calendar cal = Calendar.getInstance();
        int seed = cal.get(Calendar.SECOND);
        Random rand = new Random(seed);
        Iterator iterator = mappedKeys.iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            int r = rand.nextInt(255);
            int g = rand.nextInt(255);
            int b = rand.nextInt(255);
            Color c1 = new Color(r,g,b);
            dm.putMapValue(key,c1);
        }
    }
}