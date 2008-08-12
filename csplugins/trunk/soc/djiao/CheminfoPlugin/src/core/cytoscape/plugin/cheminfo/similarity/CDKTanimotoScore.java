package cytoscape.plugin.cheminfo.similarity;

import java.util.BitSet;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.similarity.Tanimoto;
import org.openscience.cdk.smiles.SmilesParser;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.plugin.cheminfo.ChemInfoPlugin;
import cytoscape.plugin.cheminfo.ChemInfoPlugin.AttriType;

/**
 * This is an implementation of SimilarityScore class. It uses the Tanimoto
 * coefficient method provided by CDK. It uses the fingerprint of a molecule,
 * which is also calculated using CDK.
 *
 */
public class CDKTanimotoScore extends SimilarityScore {

	public CDKTanimotoScore(CyEdge edge) {
		super(edge);
	}
	
	public CDKTanimotoScore(CyNode node1, CyNode node2, String attribute, AttriType attrType) {
		super (node1, node2, attribute, attrType);
	}

	@Override
	public double calculateSimilarity() {
		String smiles1 = ChemInfoPlugin.getSmiles(node1, attribute, attrType);
		String smiles2 = ChemInfoPlugin.getSmiles(node2, attribute, attrType);
		
		IMolecule m1 = null;
		IMolecule m2 = null;
		if (!ChemInfoPlugin.isBlank(smiles1) && !ChemInfoPlugin.isBlank(smiles2)) {
			try {
				SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder
						.getInstance());
				m1 = sp.parseSmiles(smiles1);
				m2 = sp.parseSmiles(smiles2);
			} catch (InvalidSmilesException ise) {
			}
		}
		double score = Double.MIN_VALUE;
		if (m1 != null && m2 != null) {
			Fingerprinter fper = new Fingerprinter();
			try {
				BitSet fp1 = fper.getFingerprint(m1);
				BitSet fp2 = fper.getFingerprint(m2);
				score = Tanimoto.calculate(fp1, fp2);
			} catch (Exception ex) {
			}
		}
		return score;
	}
}
