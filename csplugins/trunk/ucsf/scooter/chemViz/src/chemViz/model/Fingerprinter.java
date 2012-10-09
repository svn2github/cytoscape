/*
  Copyright (c) 2012 University of California, San Francisco

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package chemViz.model;

import org.openscience.cdk.fingerprint.EStateFingerprinter;
import org.openscience.cdk.fingerprint.ExtendedFingerprinter;
import org.openscience.cdk.fingerprint.GraphOnlyFingerprinter;
import org.openscience.cdk.fingerprint.HybridizationFingerprinter;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.fingerprint.KlekotaRothFingerprinter;
import org.openscience.cdk.fingerprint.MACCSFingerprinter;
import org.openscience.cdk.fingerprint.PubchemFingerprinter;
import org.openscience.cdk.fingerprint.SubstructureFingerprinter;

public enum Fingerprinter {
	CDK("CDK", new org.openscience.cdk.fingerprint.Fingerprinter()),
	ESTATE("E-State", new EStateFingerprinter()),
	EXTENDED("Extended CDK", new ExtendedFingerprinter()),
	GRAPHONLY("Graph Only", new GraphOnlyFingerprinter()),
	HYBRIDIZATION("Hybridization", new HybridizationFingerprinter()),
	KLEKOTAROTH("Klekota & Roth", new KlekotaRothFingerprinter()),
	MACCS("MACCS", new MACCSFingerprinter()),
	PUBCHEM("Pubchem", new PubchemFingerprinter()),
	SUBSTRUCTURE("Substructure bitset", new SubstructureFingerprinter());

  private String name;
  private IFingerprinter fingerprinter;
  private Fingerprinter(String str, IFingerprinter fp) { name=str; fingerprinter = fp;}
  public String toString() { return name; }
  public String getName() { return name; }
  public IFingerprinter getFingerprinter() { return fingerprinter; }
}
