import cytoscape.util.intr.IntEnumerator;

public class RTree
{

  public void empty() {}

  public int insert(double minX, double minY,
                    double maxX, double maxY)
  {
    return -1;
  }

  public boolean remove(int identifier)
  {
    return false;
  }

  public IntEnumerator intersected(double minX, double minY,
                                   double maxX, double maxY)
  {
    return null;
  }

  public IntEnumerator enclosed(double minX, double minY,
                                double maxX, double maxY)
  {
    return null;
  }
  
  // Spacial join?

}
