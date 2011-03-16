#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_RNA_LEN 100
#define INVALID 1000.0

static int d_matrix[MAX_RNA_LEN][MAX_RNA_LEN];
static double e_matrix[MAX_RNA_LEN][MAX_RNA_LEN];
static int con_matrix[MAX_RNA_LEN][MAX_RNA_LEN];
static char r[MAX_RNA_LEN];

char cmpl(char n){
  switch(n){
  case 'a':return 't';
  case 't':return 'a';
  case 'c':return 'g';
  case 'g':return 'c';
  default:return '?';
  }
}

double alpha(char n1, char n2){

  return (n1 == cmpl(n2)) ? -1.0 : 0.0;

}

int find_min(double array[], int n_array){
  double min;
  int i, min_i;

  if(n_array == 0){ return 0.0; }
  min = array[0];
  min_i = 0;

  for(i = 1;i < n_array;i ++)
    if(min > array[i]){ min = array[i]; min_i = i; }
 
  return min_i; 

}

d_to_con(int i, int j){

  if(i >= j)return;
  if(d_matrix[i][j] == i){
    con_matrix[i][j] = 1;
    d_to_con(i + 1, j - 1);
  }
  else {
    d_to_con(i, d_matrix[i][j] - 1);
    d_to_con(d_matrix[i][j], j);
  }

}


double ES(int i, int j){

  double con;
  double *disj, disj_min;
  double min;
  int k, disj_min_k;

  printf("Function ES(%d %d) called.\n", i, j);

  disj = (double *)malloc((j - i)*sizeof(double));

  if(i >= j)min = 0.0;
  else if(e_matrix[i][j] != INVALID)min = e_matrix[i][j];
  else {
    con = ES(i + 1, j - 1) + alpha(r[i], r[j]);
    for(k = i + 1;k <= j;k ++)
      disj[k - i - 1] = ES(i, k - 1) + ES(k, j);
    disj_min_k = find_min(disj, j - i);
    disj_min = disj[ disj_min_k ];

    if(con < disj_min){
      min = con;
      d_matrix[i][j] = i;
    }
    else {
      min = disj_min;
      d_matrix[i][j] = disj_min_k + i + 1;
    }
    e_matrix[i][j] = min;

    printf("Free energy at position %d %d\n", i, j);
    printf("con: %lf disj_min: %lf\n", con, disj_min);
    printf("min: %lf\n", min);
 
  }

  free(disj);
  return min;
  
}

main(){

#define L 9

  double min;
  int i,j;

  for(i = 0;i < MAX_RNA_LEN;i ++)
    for(j = 0;j < MAX_RNA_LEN;j ++){
      e_matrix[i][j] = INVALID;
      con_matrix[i][j] = 0;
    }
  strcpy(r, "acgatagcta");
  
  min = ES(0, L);

  printf("Min = %lf\n", min);

  for(i = 0;i <= L;i ++){
    for(j = 0;j <= L;j ++)
      if(e_matrix[i][j] == INVALID)printf("-%2.2lf ", 0.0); 
      else printf("%+2.2lf ", e_matrix[i][j]);
    putchar('\n');
  }

  for(i = 0;i <= L;i ++){
    for(j = 0;j <= L;j ++)
      if(d_matrix[i][j] == i)printf("XX "); 
      else printf("%2d ", d_matrix[i][j]);
    putchar('\n');
  }


  d_to_con(0, L);


  for(i = 0;i <= L;i ++){
    for(j = 0;j <= L;j ++)
      printf("%d ", con_matrix[i][j]);
    putchar('\n');
  }



}

