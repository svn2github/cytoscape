// CytoscapeVersion: identify (and describe) successive versions of cytoscape
//-----------------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape;
//-----------------------------------------------------------------------------------
public class CytoscapeVersion {
  private String versionString = "1.20";
  private String dateString = "2002/03/28";

  private String [] briefHistory = {
      "1.1  (2001/12/12) initial version",
      "1.2  (2001/12/18) sped up node selection by name",
      "1.3  (2001/12/20) node synonyms displayed in NodeProps dialog",
      "1.4  (2001/12/21) edge attribute files supported",
      "1.5  (2001/12/28) edge attributes now can control edge color",
      "1.6  (2002/01/01) popup dialog 'relocation flash' now fixed",
      "1.7  (2002/01/04) checkEnviroment centralized, now checks for java version",
      "1.8  (2002/01/07) active paths dialog bounds checking fixed",
      "1.9  (2002/01/07) IPBiodataServer.getGoTermName exception fixed",
      "1.10 (2002/01/22) selected nodes make new window; active paths bug fixed",
      "1.11 (2002/02/04) automatic running of active paths from command line\n" +
       "                 data passed to ActivePathsFinder via arrays",
      "1.12 (2002/02/19) reorganized directories; gene common names supported",
      "1.20 (2002/03/28) now uses plugin architecture; redesign of VizMapping underway",
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
