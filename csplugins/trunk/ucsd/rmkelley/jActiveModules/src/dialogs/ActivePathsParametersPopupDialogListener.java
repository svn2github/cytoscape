//---------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package csplugins.jActiveModules.dialogs;
import csplugins.jActiveModules.data.ActivePathFinderParameters;
//---------------------------------------------------------------------------------------
public interface ActivePathsParametersPopupDialogListener {
  public void setActivePathsParameters (ActivePathFinderParameters apfParams);
  public void cancelActivePathsFinding ();
}
//---------------------------------------------------------------------------------------
