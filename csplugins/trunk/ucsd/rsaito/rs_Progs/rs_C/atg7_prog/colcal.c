#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <iostream.h>

#include "global_st.h"
#include "atg_func.h"

#include "colcalh.c"

const static double U4_matrix[4][4] = {
  { 1.0, 0.0, 0.0, 0.0 }, 
  { 0.0, 1.0, 0.0, 0.0 },
  { 0.0, 0.0, 1.0, 0.0 },
  { 0.0, 0.0, 0.0, 1.0 } };

static int non_coding_matrix[NUM_NUC][NUM_NUC];
static double markov_non_coding_matrix[NUM_NUC][NUM_NUC];

static int coding_0_1_matrix[NUM_NUC][NUM_NUC];
static double markov_coding_0_1_matrix[NUM_NUC][NUM_NUC];

static int coding_1_2_matrix[NUM_NUC][NUM_NUC];
static double markov_coding_1_2_matrix[NUM_NUC][NUM_NUC];

static int coding_2_0_matrix[NUM_NUC][NUM_NUC];
static double markov_coding_2_0_matrix[NUM_NUC][NUM_NUC];

#define MAX_NSEQUENCE 20000

static char *sequence_p[MAX_NSEQUENCE];
static int nsequence;

static char dinuc_raw[MAX_NSEQUENCE * 2];
static char *dinuc_p[MAX_NSEQUENCE];


static int utrr, cdsr, step;
static int procedure_type;
static int spos1, spos2;

double markov_prob(int, int);
double independent_prob(int, int);
double mutual_information(int, int);
void print_dinuc_matrix(int, int);
void print_dinuc_matrix_oe(int, int);
void dinuc_to_markov(int dinuc[NUM_NUC][NUM_NUC],
		double markov[NUM_NUC][NUM_NUC]);

void dinuc_to_markov(int dinuc[NUM_NUC][NUM_NUC],
		double markov[NUM_NUC][NUM_NUC]){
  int i, j;
  int sub_total;

  for(i = 0;i < NUM_NUC;i ++){
    sub_total = 0;
    for(j = 0;j < NUM_NUC;j ++)sub_total += dinuc[i][j];
    for(j = 0;j < NUM_NUC;j ++)
      markov[i][j] = 1.0 * dinuc[i][j] / sub_total;
  }
}


int colcal_par(int argc, char *argv[], int n)
{
  int i, j;

  if(strcmp(argv[n], "-colcal") == 0){
    for(i = 0;i < NUM_NUC;i ++)
      for(j = 0;j < NUM_NUC;j ++){
	non_coding_matrix[i][j] = 0;
	coding_0_1_matrix[i][j] = 0;
	coding_1_2_matrix[i][j] = 0;
	coding_2_0_matrix[i][j] = 0;
      }
    utrr = atoi(argv[n + 1]);
    cdsr = atoi(argv[n + 2]);
    step = atoi(argv[n + 3]);
    nsequence = 0;
    for(i = 0;i < MAX_NSEQUENCE;i ++)
      dinuc_p[i] = &dinuc_raw[i * 2];
    procedure_type = 1;
    return 4;
  }
  else if(strcmp(argv[n], "-colcal2") == 0){
    for(i = 0;i < NUM_NUC;i ++)
      for(j = 0;j < NUM_NUC;j ++){
	non_coding_matrix[i][j] = 0;
	coding_0_1_matrix[i][j] = 0;
	coding_1_2_matrix[i][j] = 0;
	coding_2_0_matrix[i][j] = 0;
      }
    utrr = atoi(argv[n + 1]);
    cdsr = atoi(argv[n + 2]);
    spos1 = atoi(argv[n + 3]);
    spos2 = atoi(argv[n + 4]);
    nsequence = 0;
    for(i = 0;i < MAX_NSEQUENCE;i ++)
      dinuc_p[i] = &dinuc_raw[i * 2];
    procedure_type = 2;
    return 5;
  }
  else if(strcmp(argv[n], "-colcal3") == 0){
    for(i = 0;i < NUM_NUC;i ++)
      for(j = 0;j < NUM_NUC;j ++){
	non_coding_matrix[i][j] = 0;
	coding_0_1_matrix[i][j] = 0;
	coding_1_2_matrix[i][j] = 0;
	coding_2_0_matrix[i][j] = 0;
      }
    utrr = atoi(argv[n + 1]);
    cdsr = atoi(argv[n + 2]);
    step = atoi(argv[n + 3]);
    nsequence = 0;
    for(i = 0;i < MAX_NSEQUENCE;i ++)
      dinuc_p[i] = &dinuc_raw[i * 2];
    procedure_type = 3;
    return 4;
  }
  else if(strcmp(argv[n], "-colcal4") == 0){
    for(i = 0;i < NUM_NUC;i ++)
      for(j = 0;j < NUM_NUC;j ++){
	non_coding_matrix[i][j] = 0;
	coding_0_1_matrix[i][j] = 0;
	coding_1_2_matrix[i][j] = 0;
	coding_2_0_matrix[i][j] = 0;
      }
    utrr = atoi(argv[n + 1]);
    cdsr = atoi(argv[n + 2]);
    nsequence = 0;
    for(i = 0;i < MAX_NSEQUENCE;i ++)
      dinuc_p[i] = &dinuc_raw[i * 2];
    procedure_type = 4;
    return 3;
  }
  else if(strcmp(argv[n], "-colcal5") == 0){
    for(i = 0;i < NUM_NUC;i ++)
      for(j = 0;j < NUM_NUC;j ++){
	non_coding_matrix[i][j] = 0;
	coding_0_1_matrix[i][j] = 0;
	coding_1_2_matrix[i][j] = 0;
	coding_2_0_matrix[i][j] = 0;
      }
    utrr = atoi(argv[n + 1]);
    cdsr = atoi(argv[n + 2]);
    nsequence = 0;
    for(i = 0;i < MAX_NSEQUENCE;i ++)
      dinuc_p[i] = &dinuc_raw[i * 2];
    procedure_type = 5;
    return 3;
  }

  else if(strcmp(argv[n], "-colcal6") == 0){
    for(i = 0;i < NUM_NUC;i ++)
      for(j = 0;j < NUM_NUC;j ++){
	non_coding_matrix[i][j] = 0;
	coding_0_1_matrix[i][j] = 0;
	coding_1_2_matrix[i][j] = 0;
	coding_2_0_matrix[i][j] = 0;
      }
    utrr = atoi(argv[n + 1]);
    cdsr = atoi(argv[n + 2]);
    nsequence = 0;
    for(i = 0;i < MAX_NSEQUENCE;i ++)
      dinuc_p[i] = &dinuc_raw[i * 2];
    procedure_type = 6;
    return 3;
  }
  else return 0;
}

void colcal_head(char *line){

}

void colcal_ent(struct gparam *entry_info, char seqn[], int max,
		struct cds_info cds[], int ncds){
  
  int i,j,k,m,n;
  int start, end;
  char *compseqn;
  char *ref_seq;
  enum nucleotide n1, n2;

  compseqn = compseqget(seqn, max); 

  for(j = 1;j < cds[0].cds_start; j ++){
    n1 = cton(seqn[j - 1]);
    n2 = cton(seqn[j + 1 - 1]);
    non_coding_matrix[n1][n2] ++;
  }

  for(i = 0;i < ncds - 1;i ++){
    if(valid_cds[i + 1] == 0)continue;
    if(cds[i].cds_end == 0 || cds[i + 1].cds_start == 0)continue;
    /*
    else printf("%d - %d\n", cds[i].cds_end + 1, cds[i + 1].cds_start);
    */
    for(j = cds[i].cds_end + 1;j < cds[i + 1].cds_start;j ++){
      n1 = cton(seqn[j - 1]);
      n2 = cton(seqn[j + 1 - 1]);
      non_coding_matrix[n1][n2] ++;
    }
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

    for(j = start;j < end;j += 3){
      n1 = cton(ref_seq[j - 1]);
      n2 = cton(ref_seq[j + 1 - 1]);
      coding_0_1_matrix[n1][n2] ++;
    } 
    for(j = start;j < end;j += 3){
      n1 = cton(ref_seq[j + 1 - 1 ]);
      n2 = cton(ref_seq[j + 1 + 1 - 1]);
      coding_1_2_matrix[n1][n2] ++;
    } 
    for(j = start;j < end - 3;j += 3){
      n1 = cton(ref_seq[j + 2 - 1 ]);
      n2 = cton(ref_seq[j + 2 + 1 - 1]);
      coding_2_0_matrix[n1][n2] ++;
    } 
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

    if(start - utrr > 0 && start + cdsr <= max){
      sequence_p[nsequence] = (char *)malloc((utrr + cdsr)*sizeof(char));
      for(j = -utrr;j < cdsr;j ++){
	sequence_p[nsequence][j + utrr] = ref_seq[j + start - 1];
      }
      nsequence ++;
      if(nsequence >= MAX_NSEQUENCE){
	fprintf(stderr, "Number of sequences exceeded the threshold.");
	exit(1);
      }
    }
  }

  free(compseqn); 
}

void colcal_fin(){
  int i, j;

  dinuc_to_markov(non_coding_matrix, markov_non_coding_matrix);
  dinuc_to_markov(coding_0_1_matrix, markov_coding_0_1_matrix);
  dinuc_to_markov(coding_1_2_matrix, markov_coding_1_2_matrix);
  dinuc_to_markov(coding_2_0_matrix, markov_coding_2_0_matrix);
  /*
  for(i = 0;i < NUM_NUC;i ++){
    printf("%c ", ntc((enum nucleotide)i));
    for(j = 0;j < NUM_NUC;j ++){
      printf("%4d ", non_coding_matrix[i][j]);
    }
    putchar('\n');
  }

  for(i = 0;i < NUM_NUC;i ++){
    printf("%c ", ntc((enum nucleotide)i));
    for(j = 0;j < NUM_NUC;j ++){
      printf("%.4lf ", markov_non_coding_matrix[i][j]);
    }
    putchar('\n');
  }

  for(i = 0;i < NUM_NUC;i ++){
    printf("%c ", ntc((enum nucleotide)i));
    for(j = 0;j < NUM_NUC;j ++){
      printf("%4d ", coding_0_1_matrix[i][j]);
    }
    putchar('\n');
  }

  for(i = 0;i < NUM_NUC;i ++){
    printf("%c ", ntc((enum nucleotide)i));
    for(j = 0;j < NUM_NUC;j ++){
      printf("%.4lf ", markov_coding_0_1_matrix[i][j]);
    }
    putchar('\n');
  }

  for(i = 0;i < NUM_NUC;i ++){
    printf("%c ", ntc((enum nucleotide)i));
    for(j = 0;j < NUM_NUC;j ++){
      printf("%4d ", coding_1_2_matrix[i][j]);
    }
    putchar('\n');
  }

  for(i = 0;i < NUM_NUC;i ++){
    printf("%c ", ntc((enum nucleotide)i));
    for(j = 0;j < NUM_NUC;j ++){
      printf("%.4lf ", markov_coding_1_2_matrix[i][j]);
    }
    putchar('\n');
  }

  for(i = 0;i < NUM_NUC;i ++){
    printf("%c ", ntc((enum nucleotide)i));
    for(j = 0;j < NUM_NUC;j ++){
      printf("%4d ", coding_2_0_matrix[i][j]);
    }
    putchar('\n');
  }

  for(i = 0;i < NUM_NUC;i ++){
    printf("%c ", ntc((enum nucleotide)i));
    for(j = 0;j < NUM_NUC;j ++){
      printf("%.4lf ", markov_coding_2_0_matrix[i][j]);
    }
    putchar('\n');
  }
  */
  /*
  for(i = 0;i < nsequence;i ++){
    for(j = -utrr;j < cdsr;j ++)putchar(sequence_p[i][j + utrr]);
    putchar('\n');
  }
  */

  if(procedure_type == 2){
    print_dinuc_matrix(spos1, spos2);
  }
  else if(procedure_type == 3){
    for(i = -utrr;i < cdsr - step;i ++){
      print_dinuc_matrix_oe(i, i + step);
    }
  }
  else if(procedure_type == 4){
    for(i = -utrr;i < cdsr - 1;i ++){
      for(j = i + 1;j < cdsr;j ++){
      printf("MarkvP %d %d = %lf\n", i, j, markov_prob(i,j));
      printf("IndepP %d %d = %lf\n", i, j,
	     independent_prob(i, j));
      printf("MutuaI %d %d = %lf\n", i, j,
	     mutual_information(i, j));
      }
    }
  }
  else if(procedure_type == 5){
    printf(" , ");
    for(j = -utrr;j < cdsr;j ++)printf("%d, ",j);
    putchar('\n');
    for(i = -utrr;i < cdsr;i ++){
      printf("%d,", i);
      for(j = -utrr;j < cdsr;j ++){
	/* if(i >= j)printf(" , ");
	else */ printf("%lf,",mutual_information(i, j));
      }
      putchar('\n');
    }
  }
  else if(procedure_type == 6){
    for(i = -utrr;i < cdsr;i ++){
      for(j = -utrr;j < cdsr;j ++){
	if(i != j)printf("%d %d %lf\n", i, j, mutual_information(i, j));
	else printf("%d %d %lf\n", i, j, 0.0);
      }
      putchar('\n');
    }
  }
  else {
    for(i = -utrr;i < cdsr - step;i ++){
      printf("MarkvP %d %d = %lf\n", i, i + step, markov_prob(i,i + step));
      printf("IndepP %d %d = %lf\n", i, i + step, 
	     independent_prob(i, i + step));
      printf("MutuaI %d %d = %lf\n", i, i + step, 
	     mutual_information(i, i + step));
    }
  }

  /* 配列用のメモリの解放。最後に行うこと。 */
  for(i = 0;i < nsequence;i ++)
    free(sequence_p[i]);
}

void colcal_help(){

  printf("-colcal\t Measure base correlations. State how far upstream, downstream and distance between 2 positions\n");
  printf("-colcal2\t Prints dinucleotide matrix between two positions. State how far upstream and downstream(Almost dummy, but must be far enough), two positions\n");
  printf("-colcal3\t Prints dinucleotide O/E matrix between two positions along the sequences. State how far upstream, downstream and step\n");
  printf("-colcal4\t Prints markov prob., independent prob., mutual info. for all stated range. State how far upstream, and downstream.\n");
  printf("-colcal5\t Shows mutual information of all the pairs in stated range. State upstream, and downstream.\n");
  printf("-colcal6\t Shows mutual information of all the pairs in stated range. State upstream, and downstream. (GNU splot parametric format)\n");

}

/* This function uses global variables */
double markov_prob(int pos1, int pos2){
  
  static Dinuc_Matrix dm;
  int i,j, cpos1, cpos2;
  
  /*  printf("Position %d - %d\n", pos1, pos2); */

  cpos1 = pos1 + utrr;
  cpos2 = pos2 + utrr;

  for(i = 0;i < nsequence;i ++){
    dinuc_p[i][0] = sequence_p[i][cpos1];
    dinuc_p[i][1] = sequence_p[i][cpos2];
  }

  dm.initialize(dinuc_p, nsequence, U4_matrix);
  
  for(i = pos1 + 1;i <= 0 && i <= pos2;i ++){
    dm.multiply_mmatrix(markov_non_coding_matrix);
    /* printf("Non-coding %d - %d\n", i - 1, i); 
    dm.markov_matrix_print(); */
  }
  for(i = 1;i <= pos2;i ++){
    switch(i % 3){
    case 1:dm.multiply_mmatrix(markov_coding_0_1_matrix);
      /* printf("Coding01 %d - %d\n", i - 1, i); 
      dm.markov_matrix_print(); */
      break;
    case 2:dm.multiply_mmatrix(markov_coding_1_2_matrix);
      /* printf("Coding12 %d - %d\n", i - 1, i); 
      dm.markov_matrix_print(); */
      break;
    case 0:dm.multiply_mmatrix(markov_coding_2_0_matrix);
      /* printf("Coding20 %d - %d\n", i - 1, i); 
      dm.markov_matrix_print(); */
      break;
    }
  }
  
  return dm.log_likelihood();

}

/* This function uses global variables */
double independent_prob(int pos1, int pos2){
  
  static Dinuc_Matrix dm;
  int i,j;

  pos1 += utrr;
  pos2 += utrr;

  for(i = 0;i < nsequence;i ++){
    dinuc_p[i][0] = sequence_p[i][pos1];
    dinuc_p[i][1] = sequence_p[i][pos2];
  }

  dm.initialize(dinuc_p, nsequence, markov_non_coding_matrix);
  return dm.independent_test();

}

/* This function uses global variables */
double mutual_information(int pos1, int pos2){
  
  static Dinuc_Matrix dm;
  int i,j;

  pos1 += utrr;
  pos2 += utrr;

  for(i = 0;i < nsequence;i ++){
    dinuc_p[i][0] = sequence_p[i][pos1];
    dinuc_p[i][1] = sequence_p[i][pos2];
  }

  dm.initialize(dinuc_p, nsequence, markov_non_coding_matrix);
  return dm.mutual_information();

}

/* This function uses global variables */
void print_dinuc_matrix(int pos1, int pos2){
  
  static Dinuc_Matrix dm;
  int i,j;

  pos1 += utrr;
  pos2 += utrr;

  for(i = 0;i < nsequence;i ++){
    dinuc_p[i][0] = sequence_p[i][pos1];
    dinuc_p[i][1] = sequence_p[i][pos2];
  }

  dm.initialize(dinuc_p, nsequence, markov_non_coding_matrix);
  printf("Dinucleotide matrix\n");
  dm.dinuc_matrix_print();
  printf("Dinucleotide ratio matrix\n");
  dm.dinuc_matrix_print_r();
  printf("Dinucleotide O/E matrix\n");
  dm.dinuc_matrix_print_oe();

}

/* This function uses global variables */
void print_dinuc_matrix_oe(int pos1, int pos2){
  
  static Dinuc_Matrix dm;
  int i,j;

  printf("Dinucleotide O/E matrix %d %d\n", pos1, pos2);

  pos1 += utrr;
  pos2 += utrr;

  for(i = 0;i < nsequence;i ++){
    dinuc_p[i][0] = sequence_p[i][pos1];
    dinuc_p[i][1] = sequence_p[i][pos2];
  }

  dm.initialize(dinuc_p, nsequence, markov_non_coding_matrix);
  dm.dinuc_matrix_print_oe();

}









