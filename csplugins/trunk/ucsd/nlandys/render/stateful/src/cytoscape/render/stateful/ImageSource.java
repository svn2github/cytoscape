package cytoscape.render.stateful;

import java.awt.Image;

public interface ImageSource
{

  public Image createImageBuffer(int width, int height);

}
