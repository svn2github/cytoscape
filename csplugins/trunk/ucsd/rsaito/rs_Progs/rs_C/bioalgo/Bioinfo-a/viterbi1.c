#include <string.h>
#include <stdio.h>
#include <math.h>

#define DEBUG1 if(l == 1 && i == 2)

#define K 3
#define L 5
#define NUC_NUM 4

#define INVALID 1000

enum nucleotide { a = 0, c, g, t };

static double v[K][L]; /* log transformed */
static int ptr[K][L];
static double tr[K][K] = { 0.33, 0.33, 0.33,
                          0.33, 0.33, 0.33,
                          0.33, 0.33, 0.33 };

static double e[K][NUC_NUM] = { 0.25, 0.25, 0.25, 0.25,
                                0.25, 0.25, 0.25, 0.25,
                                0.25, 0.25, 0.25, 0.25 };

static char x[L] = {'a', 't', 'c', 'g', 'a' };

int find_max(double array[], int n_array){
  double max;
  int i, max_i;

  if(n_array == 0){ return 0.0; }
  max = array[0];
  max_i = 0;

  for(i = 1;i < n_array;i ++)
    if(max < array[i]){ max = array[i]; max_i = i; }

  return max_i;

}

enum nucleotide cton(char chr){

  switch(chr){
  case 'a':return a;
  case 't':return t;
  case 'c':return c;
  case 'g':return g;
  default:fprintf(stderr, "Error in cton. char %c\n", chr);
  }

}

/*
   l = state number
   i = sequence observation
*/
double viterbi(int l, int i){

  int k, k_max;
  double *p;

  DEBUG1 printf("Calling by (%d %d)\n", l, i);

  if((p = (double *)malloc(K*sizeof(double))) == NULL){
    fprintf(stderr, "Insufficient memory...\n");
    exit(0);
  }

  if(i < 0){ return (l == 0) ? 0.0 : -1000.0; }
  else if(v[l][i] != INVALID){ return v[l][i]; }
  for(k = 0;k < K;k ++)
    p[k] = viterbi(k, i - 1) + log(tr[k][l]);
  k_max = find_max(p, K);
  DEBUG1 printf("Max was %d -> %lf in (%d %d)\n", k_max, p[k_max], l, i);

  v[l][i] = p[k_max] + log(e[l][ (int)cton(x[i]) ]);
  DEBUG1 printf("Emission from state %d(%d) is %lf\n",
		l, (int)cton(x[i]), e[l][ (int)cton(x[i]) ]);

  ptr[l][i] = k_max;

  free(p);

  DEBUG1 printf("Returning %lf from (%d %d)\n", v[l][i], l, i);
  return v[l][i];

}

void ptr_to_path(int fstate){


}


main(){

  int i, j;
  double result;

  int path[100];
  int npath;


  for(i = 0;i < K; i ++)
    for(j = 0;j < L;j ++)v[i][j] = INVALID;

  result = viterbi(K - 1, L - 1);
  printf("result = %lf\n", result);
}
