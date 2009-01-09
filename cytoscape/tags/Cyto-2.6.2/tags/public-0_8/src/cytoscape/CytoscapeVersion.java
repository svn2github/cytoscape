// CytoscapeVersion: identify (and describe) successive versions of cytoscape
//-----------------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape;
//-----------------------------------------------------------------------------------
public class CytoscapeVersion {
  private String versionString = "0.8";
  private String dateString = "2002/06/17";

  private String [] briefHistory = {
      "0.1  (2001/12/12) initial version",
      "0.2  (2001/12/18) sped up node selection by name",
      "0.3  (2001/12/20) node synonyms displayed in NodeProps dialog",
      "0.4  (2001/12/21) edge attribute files supported",
      "0.5  (2001/12/28) edge attributes now can control edge color",
      "0.6  (2002/01/01) popup dialog 'relocation flash' now fixed",
      "0.7  (2002/01/04) checkEnviroment centralized, now checks for java version",
      "0.8  (2002/01/07) active paths dialog bounds checking fixed",
      "0.9  (2002/01/07) IPBiodataServer.getGoTermName exception fixed",
      "0.10 (2002/01/22) selected nodes make new window; active paths bug fixed",
      "0.11 (2002/02/04) automatic running of active paths from command line\n" +
       "                 data passed to ActivePathsFinder via arrays",
      "0.12 (2002/02/19) reorganized directories; gene common names supported",
      "0.20 (2002/03/28) now uses plugin architecture; redesign of VizMapping underway",
      "0.8  (2002/06/17) first alpha release",
      };

//-----------------------------------------------------------------------------------
public String getVersion ()
{
  return "cytoscape version " + versionString + ", " + dateString;
}
//------------------------------------------------------------------------------
public String toString ()
{
  return getVersion ();
}
//-----------------------------------------------------------------------------------
public String getBriefHistory ()
{
  StringBuffer sb = new StringBuffer ();
  for (int i=0; i < briefHistory.length; i++) {
    sb.append (briefHistory [i]);
    sb.append ("\n");
    }

  return sb.toString ();

} // getBriefHistory
//-----------------------------------------------------------------------------------
public static void main (String [] args)
{
  CytoscapeVersion app = new CytoscapeVersion ();
  System.out.println (app.getVersion ());
  // System.out.println (app.getBriefHistory ());
}
//-----------------------------------------------------------------------------------
} // class 
