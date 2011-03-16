#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static int nuc_use[NUM_NUC];
static int nuc_total;

static int codon_use[NUM_NUC][NUM_NUC][NUM_NUC];
static int codon_total;

static int dinuc_use[NUM_NUC][NUM_NUC];
static int dinuc_total;


/* Sum of distribution must be equal to 1 */
int randm2(double distr[], int num_class, double prob){

 double sub_total;
 int i,j;

 for(i = 0,sub_total = distr[i];
     i < num_class;
     sub_total += distr[++ i]){
   if(prob < sub_total)return i; 
 }
 return i;

}


int rand_pro_par(int argc, char *argv[], int n){

  int i,j,k;
  if(strcmp(argv[n], "-rand_pro") == 0){

    nuc_total = 0;
    for(i = 0;i < NUM_NUC;i ++)nuc_use[i] = 0;

    codon_total = 0;
    for(i = 0;i < NUM_NUC;i ++)
      for(j = 0;j < NUM_NUC;j ++)
	for(k = 0;k < NUM_NUC;k ++)codon_use[i][j][k] = 0;

    dinuc_total = 0;
    for(i = 0;i < NUM_NUC;i ++)
      for(j = 0;j < NUM_NUC;j ++)dinuc_use[i][j] = 0;

    return 1;
  }
  else return 0;
}

void rand_pro_head(char *head){


}

void rand_pro_ent(struct gparam *entry_info, char seqn[], int max,
		    struct cds_info cds[], int ncds){

  int i,j,k,m,n;
  int start, end;
  char *compseqn;
  char *ref_seq;
  char *rand_seq;
  int n1, n2, n3, tmp;
  char cn1, cn2, cn3;
  static double distr_nuc[NUM_NUC], 
    distr_dinuc[NUM_NUC][NUM_NUC],
    distr_codon[NUM_NUC*NUM_NUC*NUM_NUC];
  /* distr_dinuc is markov matrix */

  double prob;

  compseqn = compseqget(seqn, max); 

  for(i = 0;i < max;i ++){
    nuc_use[ cton(seqn[i]) ] ++;
    nuc_total ++;
  }

  for(i = 0;i < ncds;i ++){
    if(valid_cds[i] == 0)continue;
    if(cds[i].cds_start == 0 || cds[i].cds_end == 0 ||
       (cds[i].cds_end - cds[i].cds_start + 1) % 3 != 0)continue;
    
    if(cds[i].complement == 0){
      start = cds[i].cds_start;
      end   = cds[i].cds_end;
      ref_seq = seqn;
    }
    else {
      start = max - cds[i].cds_end   + 1;
      end   = max - cds[i].cds_start + 1;
      ref_seq = compseqn;
    }
    
    for(j = start + 3;j < end - 2;j += 3){
      n1 = (int)cton(ref_seq[j     - 1]);
      n2 = (int)cton(ref_seq[j + 1 - 1]);
      n3 = (int)cton(ref_seq[j + 2 - 1]);
      codon_use[n1][n2][n3] ++;
      codon_total ++;
    } 
  }

  for(i = 0;i < ncds - 1;i ++){

    for(j = cds[i].cds_end + 1;j < cds[i + 1].cds_start - 1;j ++){
      n1 = (int)cton(seqn[j - 1]);
      n2 = (int)cton(seqn[j + 1 - 1]);
      dinuc_use[n1][n2] ++;
      dinuc_total ++;
    }
  }
  
  for(i = 0;i < NUM_NUC;i ++){
    distr_nuc[i] = 1.0 * nuc_use[i] / nuc_total;
  }

  for(i = 0;i < NUM_NUC;i ++){
    tmp = 0;
    for(j = 0;j < NUM_NUC;j ++)tmp += dinuc_use[i][j];
    for(j = 0;j < NUM_NUC;j ++){
      distr_dinuc[i][j] = 1.0 * dinuc_use[i][j] / tmp;
    }
  }

  printf("LOCUS       ------    ------- bp    DNA   circular  BCT       -------2000\n");
  putchar('\n');


  for(i = 0;i < NUM_NUC;i ++)
    for(j = 0;j < NUM_NUC;j ++)
      for(k = 0;k < NUM_NUC;k ++)
	distr_codon[i * NUM_NUC * NUM_NUC + j * NUM_NUC + k] = 
	  1.0 * codon_use[i][j][k] / codon_total;

  /* Nucleotide usage display */
  printf("Nucleotide total = %d\n", nuc_total);
  for(i = 0;i < NUM_NUC;i ++){
    cn1 = ntc((nucleotide)i);
    printf("%c %3d;  ", cn1, nuc_use[i]);
  }
  putchar('\n');

  for(i = 0;i < NUM_NUC;i ++)
    printf("%d:%.2lf ", i, distr_nuc[i]);
  putchar('\n');
  putchar('\n');


  /* Dinucleotide Usage(in non protein coding regions) display */
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

  /* Codon usage display */
  printf("Codon total * 3 = %d\n", codon_total);
  for(i = 0;i < NUM_NUC;i ++){
    cn1 = ntc((nucleotide)i);
    for(j = 0;j < NUM_NUC;j ++){
      cn2 = ntc((nucleotide)j);
      for(k = 0;k < NUM_NUC;k ++){
	cn3 = ntc((nucleotide)k);
	printf("%c%c%c %3d;  ", cn1,cn2,cn3, codon_use[i][j][k]);
      }
      putchar('\n');
    }
    putchar('\n');
  }

  for(i = 0;i < NUM_NUC * NUM_NUC * NUM_NUC;i ++)
    printf("%d:%.4lf ",i,distr_codon[i]); 
  putchar('\n');

  for(i = 0;i < ncds;i ++){
    start = cds[i].cds_start;
    end   = cds[i].cds_end;
    if(start == 0 || end == 0)continue;
    if((end + 1 - start) % 3 != 0)continue;
    if(cds[i].complement == 0)
      printf("     CDS             %d..%d\n", start, end);
    else       printf("     CDS             complement(%d..%d)\n", start, end);
    printf("                     /codon_start=1\n");

  }


  /* Generates random sequence */
  rand_seq = (char *)malloc(max * sizeof(char));
  prob = 1.0 * (random() % 10000) / 10000; 
  n1 = randm2(distr_nuc, NUM_NUC, prob);
  rand_seq[0] = ntc((nucleotide)n1);

  for(i = 1;i < max;i ++){
    prob = 1.0 * (random() % 10000) / 10000; 
    n1 = randm2(distr_dinuc[n1], NUM_NUC, prob);
    rand_seq[i] = ntc((nucleotide)n1);
  }


  for(i = 0;i < ncds;i ++){
    start = cds[i].cds_start;
    end   = cds[i].cds_end;

    if(start == 0 || end == 0)continue;

    if((end + 1 - start) % 3 != 0)continue;
    
    if(cds[i].complement == 0){
      for(j = start + 3;j < end - 2;j += 3){
	prob = 1.0 * (random() % 10000) / 10000; 
	n = randm2(distr_codon, NUM_NUC * NUM_NUC * NUM_NUC, prob);
	n1 = n / (NUM_NUC * NUM_NUC);
	n2 = (n % (NUM_NUC * NUM_NUC)) / NUM_NUC;
	n3 = n % NUM_NUC;
	rand_seq[j     - 1] = ntc((nucleotide)n1);
	rand_seq[j + 1 - 1] = ntc((nucleotide)n2);
	rand_seq[j + 2 - 1] = ntc((nucleotide)n3);
      }
    }
    else {
      for(j = end - 3;j > start + 2;j -= 3){
	prob = 1.0 * (random() % 10000) / 10000; 
	n = randm2(distr_codon, NUM_NUC * NUM_NUC * NUM_NUC, prob);
	n1 = n / (NUM_NUC * NUM_NUC);
	n2 = (n % (NUM_NUC * NUM_NUC)) / NUM_NUC;
	n3 = n % NUM_NUC;
	rand_seq[j     - 1] = cmpl(ntc((nucleotide)n1));
	rand_seq[j - 1 - 1] = cmpl(ntc((nucleotide)n2));
	rand_seq[j - 2 - 1] = cmpl(ntc((nucleotide)n3));
      }
    }
  }
  
  for(i = 0;i < ncds;i ++){
    start = cds[i].cds_start;
    end   = cds[i].cds_end;
    
    rand_seq[start     - 1] = seqn[start     - 1];
    rand_seq[start + 1 - 1] = seqn[start + 1 - 1];
    rand_seq[start + 2 - 1] = seqn[start + 2 - 1];
    
    rand_seq[end     - 1] = seqn[end     - 1];
    rand_seq[end - 1 - 1] = seqn[end - 1 - 1];
    rand_seq[end - 2 - 1] = seqn[end - 2 - 1];
  }

  /* prints random sequence */
  printf("ORIGIN\n");
  for(i = 0;i < max;i ++){
    putchar(rand_seq[i]);
    if((i + 1) % 50 == 0)putchar('\n');
  }
  printf("\n//\n");


  free(compseqn); 
  free(rand_seq);
}

void rand_pro_fin(){


}

void rand_pro_help(){

  printf("-rand_pro\t Generates random sequences according to first ordered markov model and codon usage. (No introns allowed & One entry only)\n");

}

