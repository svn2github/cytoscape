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
        bounds = font.getStringBounds(token,
                                      grafx.getFontRenderContextFull()); }
      width = Math.max(width, bounds.getWidth());
      height += bounds.getHeight(); }
    rtrnVal2x[0] = (float) (width * fontScaleFactor);
    rtrnVal2x[1] = (float) (height * fontScaleFactor);
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
    // TODO: Don't calculate dimensions two million separate times.
    // TODO: Solve float/double madness.
    final float[] dims = new float[2];
    computeTextDimensions(grafx, text, font, fontScaleFactor, textAsShape,
                          dims);
    final float overallWidth = dims[0];
    final float overallHeight = dims[1];
    float currHeight = overallHeight / 2.0f;
    final StringTokenizer tokenizer = new StringTokenizer(text, "\n");
    while (tokenizer.hasMoreTokens()) {
      final String token = tokenizer.nextToken();
      final float textWidth;
      final float textHeight;
      if (textAsShape) {
        final GlyphVector glyphV;
        {
          final char[] charBuff = new char[token.length()];
          token.getChars(0, charBuff.length, charBuff, 0);
          glyphV = font.layoutGlyphVector
            (grafx.getFontRenderContextFull(), charBuff, 0, charBuff.length,
             Font.LAYOUT_NO_LIMIT_CONTEXT);
        }
        final Rectangle2D bounds = glyphV.getLogicalBounds();
        textWidth = (float) (bounds.getWidth() * fontScaleFactor);
        textHeight = (float) (bounds.getHeight() * fontScaleFactor); }
      else {
        final Rectangle2D bounds = font.getStringBounds
          (token, grafx.getFontRenderContextFull());
        textWidth = (float) (bounds.getWidth() * fontScaleFactor);
        textHeight = (float) (bounds.getHeight() * fontScaleFactor); }
      final float yCenter = (float)
        (textYCenter + currHeight - (textHeight / 2.0f));
      final float xCenter;
      if (textJustify == NodeDetails.LABEL_WRAP_JUSTIFY_CENTER) {
        xCenter = textXCenter; }
      else if (textJustify == NodeDetails.LABEL_WRAP_JUSTIFY_LEFT) {
        xCenter = (float)
          (textXCenter - (overallWidth - textWidth) / 2.0f); }
      else if (textJustify == NodeDetails.LABEL_WRAP_JUSTIFY_RIGHT) {
        xCenter = (float)
          (textXCenter + (overallWidth - textWidth) / 2.0f); }
      else {
        throw new IllegalStateException("textJustify value unrecognized"); }
      grafx.drawTextFull(font, fontScaleFactor, token, xCenter, yCenter, 0,
                         paint, textAsShape);
      currHeight -= textHeight; }
  }

}
