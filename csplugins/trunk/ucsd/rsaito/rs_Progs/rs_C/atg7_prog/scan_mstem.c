#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static int pos1, pos2;
static int nnuc, step, cpflag;
static int range1_1, range1_2, range2_1, range2_2;

int scan_mstem_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-scan_mstem") == 0){
    pos1 = atoi(argv[n + 1]);
    pos2 = atoi(argv[n + 2]);
    nnuc = atoi(argv[n + 3]);
    range1_1 = atoi(argv[n + 4]);
    range1_2 = atoi(argv[n + 5]);
    range2_1 = atoi(argv[n + 6]);
    range2_2 = atoi(argv[n + 7]);
    step = atoi(argv[n + 8]);
    cpflag = atoi(argv[n + 9]);
    return 10;
  }
  else return 0;
}

void scan_mstem_head(char *head){

}

void scan_mstem_ent(struct gparam *entry_info, char seqn[], int max,
		    struct cds_info cds[], int ncds){
  
  int i,j,k;
  char *compseqn;
  int scanp1, scanp2;
  char *scanseq;

  static char result1[1000], result2[1000];
  int result_len;
  static int match_res1[500], match_res2[500];
  double score, min_score;
  
  int max_stem, maxpos1, maxpos2, rpos1, rpos2, apos1, apos2;
  
  static char flgseq[5000],lseq[5000];
  
  compseqn = compseqget(seqn, max);
  
  if(cpflag){
    scanp1 = max - pos1 + 1;
    scanp2 = max - pos2 + 1;
    scanseq = compseqn;
  }
  else {
    scanp1 = pos1;
    scanp2 = pos2;
    scanseq = seqn;
  }

  for(i = -range1_1;i <= range1_2;i += step){
    strncpy(flgseq, &scanseq[scanp1 + i - 1], nnuc);
    flgseq[nnuc] = '\0';

    min_score = 0.0;
    for(j = -range2_1;j <= range2_2;j += step){
      strncpy(lseq, &scanseq[scanp2 + j - 1], nnuc);
      lseq[nnuc] = '\0';
      rev(lseq);
      
      printf("(%d, %d)\n", i, j);
      printf("flagment:%s\n", flgseq);
      printf("sequence:%s\n\n", lseq);
    
      score = sd_match_opt(flgseq, lseq, result1, result2, &result_len, 
			   match_res1, match_res2);
   
      disp_res(result1, result2, match_res1, match_res2, result_len);
      printf("Score = %.2lf\n", score);
     
      if(min_score > score)min_score = score;
    }
    printf("%d %.2lf\n", i, min_score);
  }
}


void scan_mstem_fin(){

}

void scan_mstem_help(){

  printf("-scan_mstem Searches for minimum free energy pairing.");
  printf("State 6 numbers:");
  printf("Position 1, 2, size of flagmental sequence, two ranges from 1 and 2, step, complement = 1\n");

}

