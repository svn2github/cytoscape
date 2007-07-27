package com.agilent.labs.excentricLabelsPlugin;

import cytoscape.data.Semantics;

public class GlobalLabelConfig {
    private static String currentAttributeName = Semantics.CANONICAL_NAME;

    public static String getCurrentAttributeName () {
        return currentAttributeName;
    }

    public static void setCurrentAttributeName (String attributeName) {
        currentAttributeName = attributeName;
    }


}
