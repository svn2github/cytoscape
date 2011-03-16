#include <stdio.h>
#include <math.h>

double euc_distance(v1, v2, dim)
double v1[], v2[];
int dim;
{
   int i;
   double dist;

   dist = 0;
   for(i = 0;i < dim;i ++){
      dist += (v1[i] - v2[i]) * (v1[i] - v2[i]);
   }
   return sqrt(dist);

}

void add_vector(v1, v2, v3,dim)
double v1[], v2[], v3[];
int dim;
{
   int i,j,k;
   for(i = 0;i < dim;i ++){
      v3[i] = v1[i] + v2[i];
   }
}

void sum_vector(v1, v2, dim)
double v1[], v2[];
int dim;
{
   int i,j,k;
   for(i = 0;i < dim;i ++){
      v2[i] += v1[i];
   }
}

void sub_vector(v1, v2, v3, dim)
double v1[], v2[], v3[];
int dim;
{
   int i,j,k;
   for(i = 0;i < dim;i ++){
      v3[i] = v1[i] - v2[i];
   }
}

void mul_vector(v1, v2, mul, dim)
double v1[], v2[], mul;
int dim;
{
   int i,j,k;
   for(i = 0;i < dim;i ++){
      v2[i] = v1[i] * mul;
   }
}

void ipoints(num, ptsx, ptsy)
int num, ptsx[], ptsy[];
{
   int i,b;
   for(i = 0;i < num;i ++){
      rsmouse(&ptsx[i], &ptsy[i], &b);    
      rsarcp(ptsx[i], ptsy[i], 3, "red");
      rsflush();
   }
}

double **make2d(m, n)
int m, n;
{
   int i,j;
   double *top, **table;

   top = (double *)malloc(m * n * sizeof(double));
   table = (double **)malloc(m * sizeof(double *));

   for(i = 0;i < m;i ++)
      table[i] = &top[i * n];

   return table;
}


main(){

#define NPOINT 20
  int i,j,k;
  int dim;
  int ptsx[100], ptsy[100];
  double v1[5], v2[5], v3[5];
  double **vec;
  
  dim = 2;
  
}

