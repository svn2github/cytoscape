package cytoscape;

public class CytoscapeConfig {

  public CytoscapeConfig() {
  }

  public String[] getArgs() {
    return CytoscapeInit.getArgs();
  }

  public java.util.Properties getProperties() {
    return CytoscapeInit.getProperties();
  }

}
