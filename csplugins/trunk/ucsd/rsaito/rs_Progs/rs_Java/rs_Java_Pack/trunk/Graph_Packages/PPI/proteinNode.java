package PPI;

public class proteinNode {
	private String protein_ID;
	
	proteinNode(String protein_ID){
		this.protein_ID = protein_ID;
	}
	
	String get_Protein_ID(){
		return protein_ID;
	}
}