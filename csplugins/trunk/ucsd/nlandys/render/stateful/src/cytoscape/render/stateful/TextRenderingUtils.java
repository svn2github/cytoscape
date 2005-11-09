package cytoscape.render.stateful;

import cytoscape.render.immed.GraphGraphics;
import java.awt.Font;
import java.awt.Paint;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.util.StringTokenizer;

final class TextRenderingUtils
{

  // No constructor.
  private TextRenderingUtils() { }

  /**
   * @param text potentially multi-line text.
   */
  public final static void computeTextDimensions(final GraphGraphics grafx,
                                                 final String text,
                                                 final Font font,
                                                 final double fontScaleFactor,
                                                 final boolean textAsShape,
                                                 final float[] rtrnVal2x)
  {
    final StringTokenizer tokenizer = new StringTokenizer(text, "\n");
    double width = 0.0d;
    double height = 0.0d;
    while (tokenizer.hasMoreTokens()) {
      final String token = tokenizer.nextToken();
      final Rectangle2D bounds;
      if (textAsShape) {
        final GlyphVector glyphV;
        {
          final char[] charBuff = new char[token.length()];
          token.getChars(0, charBuff.length, charBuff, 0);
          glyphV = font.layoutGlyphVector
            (grafx.getFontRenderContextFull(), charBuff, 0, charBuff.length,
             Font.LAYOUT_NO_LIMIT_CONTEXT);
        }
        bounds = glyphV.getLogicalBounds(); }
      else {
        bounds = font.getStringBounds(text,
                                      grafx.getFontRenderContextFull()); }
      width = Math.max(width, bounds.getWidth());
      height += bounds.getHeight(); }
    rtrnVal2x[0] = (float) width;
    rtrnVal2x[1] = (float) height;
  }

  public final static void renderHorizontalText(final GraphGraphics grafx,
                                                final String text,
                                                final Font font,
                                                final double fontScaleFactor,
                                                final float textXCenter,
                                                final float textYCenter,
                                                final byte textJustify,
                                                final Paint paint,
                                                final boolean textAsShape)
  {
  }

}
