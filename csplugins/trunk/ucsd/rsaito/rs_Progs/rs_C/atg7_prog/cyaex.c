#include <string.h>
#include <stdio.h>

#include "global_st.h"
#include "atg_func.h"

static char *cyaex[] = {
      "gacgttatttgtaactttttagtctatgtctaaactta",
      "aatttgcccacgaggttttacttttatggctaaatcca",
      "taggcaatagaacttaggaggatttatggctcttgtac",
      "ctagttagtaaaaggagattatttagtggcaattacaa",
      "cattgtcgacaaaggaatcaggattatgatggaaaatc",
      "tctttaagatcacggaggtttacacttggctcttcccc",
      "gcgattttgagaggataagtaagacatgggattatttg",
      "tcctagctatcattgctaaaacactatggacacctttg",
      "ggaggcagtttcaagggacaattcaatggttacactag",
      "gaaaaactgattaggaacgataaccatggcagaaatta",
      "gattatccattccccagaggtttccatggctccccaag",
      "gtaaagattagagtcgataaaagccatgctgtttcgtc",
      "gttgaaatacttagagacccttcccatgtccaaaaata",
      "ccgccctagaaggtgccgtcacaccatgaccagttttg",
      "ccataaagtttttgctgttttcaccatgaccgatcgcc",
      "tgcattgtattaggaactatctgttatggccttacaac",
      "gtcgtttagatagccagaggtaaccatgacccccgaac",
      "ttttgtatttcgttggagaaaaattgtggatctaagcc",
      "tatttatattaaggagataaacagcatgactgaggaac",
      "aactgccacaccgttaccgaagatcatgagccaagtta",
      "tattgagtagagaatttaaatttaaatggcttacgcac",
      "gtcggtacgttagttggagaaaattgtgaagttagctg",
      "aaataatctcccacaagatttttccatgcgcatcctga",
      "tttttgaaaaattcctagattatctatgcaacaacgtg",
      "ttaatttttggagtttttacggattatggctatggcta",
      "taaagtcaagtaggagattaattcaatgttcgacgtat",
      "aatccttaaatttagacggttcattatgacacagattt",
      "cattctaacgggagataccagaacaatgaaaacccctt",
      "tgtcattcaatcttgagaaaatatcgtgctagacgaac",
      "taaatctttacggaggaatccatccatgagtatcgtca",
      "cacccaagcctttacctaaaattctatgacccccaaat",
      "aaatgggttgcccaatgaaatttgtatgatgagcaaag",
      "cctacaaaccccggaaccatctcccatgactgccgaaa",
      "accgttgtaacatttcttaacacctatgccattaccca",
      "aagttccaaattcagcataggttttatgagcagaacgg",
      "taagacatttaagaatttttttaacatggccactgcct",
      "cttatgaaattccattttcatccctatgacagaactct",
      "accttagaattgaggttatcgccccatgaacagttttg",
      "aactcgtcaaaggggaatacctgaaatgattcatcaag",
      "acagaaaaagcaaaaggaggttgccatggaacgcacat",
      "tgatcgttgagccctttctctcaccatgaccctttctt",
      "agtaagatttaaggaaaccaaaactatgcaagacgcaa",
      "ttttttgttgccgaggagcaggtctatggctaccagtc",
      "ttaaattaataaattcttcttgtttatgcccatttctc",
      "ttttgttaaacggagataatccagtatggctacgatta",
      "aacgttgttttcggtaagaattattatgttgcgggact",
      "atcattcaacggaaaaaataatcctatgccaaacgcct",
      "tcaatctcaatcaccgaaaacttgaatggctaaatcaa",
      "gtttttttgagaagaggaaacatctatggcatcctata",
      "cacgaggtttagaaggatttccagtatgagtgctaccc",
      "ctgataattctgcataccgacacctatgaatacaattt",
      "cgctatcaaagggaaattcctaaaaatgacgttaacag",
      "attaatgtcattaaggtaaaaaaacatggctgaaattc",
      "gatcattgcacgaggagtaccaggtttgaaaaaagtag",
      "ctacttacctataggagtcatttctatgtccgctgcaa",
      "gtgttcaggagtttttagaatcgccatgttaggtcaat",
      "gactatcatctggaggatgacttctatggccgctattt",
      "atcagcaaggaaaacttttaaatcgatgaaaactttac",
      "agggtgtaaagccgaggagggcgttattgctgagccac",
      "tcgcaattatctaaggaattaaatcatggccttaaatc",
      "ttttctctggttttggagctaatttatgtccatttatg",
      "cagccattaccgacgagtaaaagctatgaacgtgattg",
      "tgccgccactgtcaaggagaacaatatgtccgcccaat",
      "tattccctgggagtaacttaaacctatgtcaattgcag",
      "aacgaaatcattagggaccctgaccatgcaaaaagccg",
      "atcaaaagagttcttgaccattaccatgagtttactgg",
      "ccctcggttaaggagccgatagtcaatgtcccatagtg",
      "accacttcccgtttacggttcccctatgatcagcattg",
      "taagccaaccctagagaaaagccctatgaatgtcggcg",
      "tttttatttttcattggcaaaaattatggctgatcccc",
      "acaaatgcaaccataaaaaaagtccatgcaaaaccaag",
      "tccaatccaaaatcaggtagccattatgatttttcccg",
      "end"
    };

int cyaex_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-cyaex") == 0){
    return 1;
  }
  else return 0;
}


void cyaex_ent(struct gparam *entry_info, char seqn[], int max,
		 struct cds_info cds[], int ncds){
  int i,j,k;

  i = 0;
  while(strcmp(cyaex[i], "end") != 0){
    printf("Dealing #%d...\n", i);
    for(j = 0;j < max - 40;j ++)
      if((k = countmatch(&seqn[j], cyaex[i], strlen(cyaex[i])))
                                           == strlen(cyaex[i]))
	printf("%d : %d %d %d\n", i, j + 1, j + 25 + 1, k);
    i ++;
  }
}

void cyaex_help(){

  printf("-cyaex\t For Synechosystis only\n");

}

