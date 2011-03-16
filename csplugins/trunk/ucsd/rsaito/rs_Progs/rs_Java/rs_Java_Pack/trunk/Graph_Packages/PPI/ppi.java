package PPI;

public class ppi {
    public proteinNode proteinNode1;
    public proteinNode proteinNode2;

    public ppi(proteinNode proteinNode1, proteinNode proteinNode2) {
        this.proteinNode1 = proteinNode1;
        this.proteinNode2 = proteinNode2;
    }

    public String toString(){
        return this.proteinNode1.get_Protein_ID() + "\t"
        + this.proteinNode2.get_Protein_ID();
    }
    public boolean equals(ppi data) {
        if (data.proteinNode1.equals(this.proteinNode1)) {
            if(data.proteinNode2.equals(proteinNode2)){
            return true;
            }else{
                return false;
            }
        } else {
            return false;
        }
    }
    public int hashCode(){
    	return proteinNode1.hashCode() ^ proteinNode2.hashCode();
    }
}

