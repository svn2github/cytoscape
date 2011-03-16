

using namespace std;

namespace RNA1 {

  double e(int i, int j,
	   int k, int l,
	   char r1_1, char r2_1,
	   char r1_2, char r2_2){
    return 0.0;
  }

  class Two_RNA_Seq {

  private:
    int len1;
    int len2;

  public:
    char *seq1;
    char *seq2;

    double E(int i, int j){
      double e_pairing_min = 99999999;
  
      int min_k, min_l;

      for(int k = i+1;k < len1;k ++)
	for(int l = j+1;l < len2;l ++){
	  double e_bp = e(i, j, k, l, 
			  seq1[i], seq2[j], seq1[k], seq2[l]);
      
	}

      return 0.0;
    }

  };

}


