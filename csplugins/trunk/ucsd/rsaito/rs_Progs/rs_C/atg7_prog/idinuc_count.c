#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static int nuc_use[NUM_NUC + 2];
static int nuc_total;

static int dinuc_use[NUM_NUC + 2][NUM_NUC + 2];
static int dinuc_total;

int idinuc_count_par(int argc, char *argv[], int n){

  int i,j,k;
  if(strcmp(argv[n], "-idinuc_count") == 0){

    nuc_total = 0;
    for(i = 0;i < NUM_NUC;i ++)nuc_use[i] = 0;

    dinuc_total = 0;
    for(i = 0;i < NUM_NUC;i ++)
      for(j = 0;j < NUM_NUC;j ++)dinuc_use[i][j] = 0;

    return 1;
  }
  else return 0;
}

void idinuc_count_head(char *head){


}

void idinuc_count_ent(struct gparam *entry_info, char seqn[], int max,
		    struct cds_info cds[], int ncds){

  int i,j,k,m,n;
  int start, end;
  char *compseqn;
  char *ref_seq;
  char cn1, cn2;
  nucleotide nuc1, nuc2;
  int tmp;

  static double distr_nuc[NUM_NUC + 2], 
    distr_dinuc[NUM_NUC + 2][NUM_NUC + 2];
  /* distr_dinuc is markov matrix */

  compseqn = compseqget(seqn, max); 

  printf("5' position? :");
  scanf("%d", &m);
  printf("3' position? :");
  scanf("%d", &n);

  if(m < n){
    start = m;
    end = n;
    ref_seq = seqn;
  }
  else {
    start = max - m + 1;
    end   = max - n + 1;
    ref_seq = compseqn;
  }

  for(i = start;i <= end;i ++){
    nuc_use[ cton(ref_seq[i - 1]) ] ++;
    nuc_total ++;
  }
  for(i = 0;i < NUM_NUC;i ++){
    distr_nuc[i] = 1.0 * nuc_use[i] / nuc_total;
  }
  
  for(i = start;i < end;i ++){
    nuc1 = cton(ref_seq[i - 1]);
    nuc2 = cton(ref_seq[i]);
    dinuc_use[nuc1][nuc2] ++;
    dinuc_total ++;
  }

  for(i = 0;i < NUM_NUC;i ++){
    tmp = 0;
    for(j = 0;j < NUM_NUC;j ++)tmp += dinuc_use[i][j];
    for(j = 0;j < NUM_NUC;j ++){
      distr_dinuc[i][j] = 1.0 * dinuc_use[i][j] / tmp;
    }
  }

  free(compseqn); 

  /* Nucleotide usage display */
  printf("Nucleotide total = %d\n", nuc_total);
  for(i = 0;i < NUM_NUC;i ++){
    cn1 = ntc((nucleotide)i);
    printf("%c %3d;  ", cn1, nuc_use[i]);
  }
  putchar('\n');


  /* Dinucleotide usage display */
  printf("Dinuc Total = %d\n", dinuc_total);
  for(i = 0;i < NUM_NUC;i ++){
    cn1 = ntc((nucleotide)i);
    for(j = 0;j < NUM_NUC;j ++){
      cn2 = ntc((nucleotide)j);
      printf("%c%c %3d;  ", cn1,cn2, dinuc_use[i][j]); 
    }
    putchar('\n');
  }
  for(i = 0;i < NUM_NUC;i ++){
    for(j = 0;j < NUM_NUC;j ++){
      printf("%.4lf ",distr_dinuc[i][j]); 
    }
    putchar('\n');
  }
  putchar('\n');

}

void idinuc_count_fin(){


}

void idinuc_count_help(){

  printf("-idinuc_count\t Intaractive nucleotide/dinucleotide counter(One entry only)\n");

}


