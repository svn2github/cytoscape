package cytoscape.visual;

import java.awt.BasicStroke;
import java.awt.Stroke;

/**
 *
 * Define line stroke.
 *
 * TODO: need to modify rendering engine to fully support dash lines.
 *
 * @author kono
 *
 */
public enum LineTypeDef {SOLID(null), DASH("4.0f,4.0f"), LONG_DASH("8.0f,3.0f"), 
    DASH_DOT("12.0f,3.0f,3.0f,3.0f");
    private float[] strokeDef;

    private LineTypeDef(String def) {
        if (def == null)
            strokeDef = null;
        else {
            final String[] parts = def.split(",");
            strokeDef = new float[parts.length];

            for (int i = 0; i < strokeDef.length; i++)
                strokeDef[i] = Float.parseFloat(parts[i]);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param width DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Stroke getStroke(float width) {
        if (strokeDef != null) {
            System.out.println("Def found: " + strokeDef.length);

            return new BasicStroke(width, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_MITER, 10.0f, strokeDef, 0.0f);
        } else
            return new BasicStroke(width);
    }
}
