#include <string.h>
#include <stdio.h>
#include <math.h>

#define K 6 /* Number of states */
#define L 10 /* Length of sequence */
#define NUC_NUM 4 /* Number of bases */

#define MAX_PATH 100

#define TYPE_S 0
#define TYPE_N 1
#define TYPE_D 2

#define INVALID 1000
#define LOG0 -1000.0

enum nucleotide { a = 0, c, g, t };

static int type[K] = { TYPE_S, TYPE_N, TYPE_N, TYPE_N, TYPE_N, TYPE_N };

static double v[K][L+1]; /* log transformed */
static int ptr[K][L+1];
static double tr[K][K] = { 
/*  0    1    2    3    4    5 */
  0.0, 1.0, 0.0, 0.0, 0.0, 0.0, /* 0 */
  0.0, 0.0, 0.8, 0.2, 0.0, 0.0, /* 1 */
  0.0, 0.0, 0.5, 0.0, 0.5, 0.0, /* 2 */
  0.0, 0.0, 0.0, 0.0, 0.7, 0.3, /* 3 */
  0.0, 0.0, 0.0, 0.0, 0.0, 1.0, /* 4 */
  0.0, 0.0, 0.0, 0.0, 0.0, 0.0  /* 5 */
};

static double e[K][NUC_NUM] = { 
/*   a     c     g     t   */
  0.00, 0.00, 0.00, 0.00, /* 0 */
  0.40, 0.10, 0.30, 0.20, /* 1 */
  0.10, 0.10, 0.10, 0.70, /* 2 */ 
  0.20, 0.20, 0.30, 0.30, /* 3 */ 
  0.10, 0.30, 0.50, 0.10, /* 4 */ 
  0.70, 0.10, 0.10, 0.10  /* 5 */ 
};

static char x[L];

static int path[MAX_PATH];

int find_max(double array[], int n_array){
  double max;
  int i, max_i;

  if(n_array == 0){ return -1; }
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

char ntc(enum nucleotide n){

  switch(n){
  case a:return 'a';
  case t:return 't';
  case c:return 'c';
  case g:return 'g';
  default:return 'n';
  }
}


#define DEBUG1 if(l == 3 && i == 2)

/*
   l = state number
   i = sequence observation(1,2...i in array 0,1,..i-1)
*/
double viterbi(int l, int i){

  int k, k_max;
  double p, p_max;

  DEBUG1 printf("Calling by State %d Seq 1-%d\n", l, i);
  
  if(v[l][i] != INVALID){ 
    DEBUG1 printf("Record found for %d %d\n", l, i);
    return v[l][i];
  }
  
  switch(type[l]){
  case TYPE_S:
    DEBUG1 printf("TYPE S\n");
    if(i <= 0){ v[l][i] = 0.0; return 0.0; }
    else { v[l][i] = LOG0; return LOG0; }
    break; /* Unreachable */

  case TYPE_N:
    if(i <= 0){ return LOG0; }

    for(p_max = LOG0 - 1, k = 0;k < K;k ++){
      if(tr[k][l] > 0.0)p = viterbi(k, i - 1) + log(tr[k][l]);
      else p = LOG0;
      if(p > p_max){ p_max = p; k_max = k; }
      DEBUG1 printf("State %d -> %d(%d) %lf\n", k, l, i, exp(p));  
    }

    v[l][i] = p_max + log(e[l][ (int)cton(x[i - 1]) ]);
    ptr[l][i] = k_max;
    DEBUG1 printf("max for state %d(%d) is %d:%lf + %lf(%c)\n",
		  l, i, k_max, exp(p_max), 
		  log(e[l][ (int)cton(x[i - 1]) ]), x[i - 1]);

    break;

  case TYPE_D:

    for(p_max = LOG0 - 1, k = 0;k < K;k ++){
      if(tr[k][l] > 0.0)p = viterbi(k, i) + log(tr[k][l]);
      else p = LOG0;
      if(p > p_max){ p_max = p; k_max = k; }
      DEBUG1 printf("State %d -> %d(%d) %lf\n", k, l, i, exp(p));  
    }

    v[l][i] = p_max;
    ptr[l][i] = k_max;

    DEBUG1 printf("max for state %d(%d) is %d:%lf\n",
		  l, i, k_max, exp(p_max));
    break;
  }


  DEBUG1 printf("Returning %lf from (%d %d)\n", v[l][i], l, i);
  return v[l][i];

}

int ptr_to_path(int l, int i){

  int n = 0;
  int new_l;

  path[n ++] = l;
  while(type[l] != TYPE_S){
    new_l = ptr[l][i];
    if(type[l] == TYPE_N)i --;
    l = new_l;
    path[n ++] = l;
  }
  return n;

}


main(){

  int i, j;
  double result;

  int npath;

  int l;

  strcpy(x, "agga");
  l = strlen(x);

  for(i = 0;i < K; i ++)
    for(j = 0;j <= l;j ++){
      v[i][j] = INVALID;
      ptr[i][j] = INVALID;
    }

  result = viterbi(K - 1, l);
  npath = ptr_to_path(K - 1, l);

  printf("Viterbi table:\n");
  for(i = 0;i < K; i ++){
    for(j = 0;j <= l;j ++){
      printf("%.4lf\t", exp(v[i][j]));
    }
    putchar('\n');
  }

  putchar('\n');


  printf("Pointer to previous state\n");
  for(i = 0;i < K; i ++){
    for(j = 0;j <= l;j ++){
      printf("%2d ", ptr[i][j]);
    }
    putchar('\n');
  }


  printf("result = %lf\n", exp(result));

  for(i = npath - 1;i >= 0;i --)
    printf("%d ", path[i]);
  putchar('\n');

}





