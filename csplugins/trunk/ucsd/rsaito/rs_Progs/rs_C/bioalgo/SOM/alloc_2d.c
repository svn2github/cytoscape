#include <stdio.h>

double **malloc_2d_double(int m, int n){
  
  int i;
  double **p;
  
  p = (double **)malloc(m * sizeof(double *));

  for(i = 0;i < m;i ++)
    p[i] = (double *)malloc(n * sizeof(double));

  return p;

}

void free_2d_double(double **p, int m){
  
  int i;
  for(i = 0;i < m;i ++)free(p[i]);
  free(p);

}

main(){


  double **a;
  int i, j;

  a = malloc_2d_double(10, 3);

  for(i = 0;i < 10;i ++)
    for(j = 0;j < 3;j ++)
      a[i][j] = i * j + 1.0;

  for(i = 0;i < 10;i ++)
    for(j = 0;j < 3;j ++)
      printf("%d %d %lf\n", i, j, a[i][j]);

  

}
