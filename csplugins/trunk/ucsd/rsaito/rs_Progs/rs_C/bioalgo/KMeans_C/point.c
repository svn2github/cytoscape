#include <math.h>
#include <string.h>
#include <iostream>
#include "point.h"

using namespace std;

POINT::POINT(){
  dim = 0;
}

int POINT::get_dim(){

  return dim;

}

double *POINT::get_array(){

  double *ret = new double[dim];
  for(int i = 0;i < dim;i ++)
    ret[i] = p_array[i];

  return ret;
}

double POINT::get_array(int i){
  
  return p_array[i];

}

POINT POINT::operator+(POINT pointp){

   POINT point_ret = *this; /* Copies class itself (content of "this value")
			       to class for return value */
   int loop;

   if(pointp.dim > dim)loop = dim;
   else loop = pointp.dim;

   for(int i = 0;i < loop;i ++)
      point_ret.p_array[i] += pointp.p_array[i];

   return point_ret;
}

POINT POINT::operator-(POINT pointp){

   POINT point_ret = *this; /* Copies class itself (content of "this value")
			       to class for return value */
   int loop;

   if(pointp.dim > dim)loop = dim;
   else loop = pointp.dim;

   for(int i = 0;i < loop;i ++)
      point_ret.p_array[i] -= pointp.p_array[i];

   return point_ret;
}

POINT POINT::operator*(double pointv){

   POINT point_ret = *this; /* Copies class itself(content of "this value")
			       to class for return value */
   for(int i = 0;i < dim;i ++)
      point_ret.p_array[i] *= pointv;

   return point_ret;
}

POINT POINT::operator/(double pointv){

   POINT point_ret = *this; /* Copies class itself(content of "this value")
			       to class for return value */
   for(int i = 0;i < dim;i ++)
      point_ret.p_array[i] /= pointv;

   return point_ret;
}

void POINT::append(double data){
   if(dim < MAXDIM){
     p_array[dim] = data;
     dim ++;
   }
}

void POINT::display(){

  for(int i = 0;i < dim;i ++){
     cout << i << ':' << p_array[i] << ' '; 
  }
  cout << '\n';
}

double euc_dist(POINT &sorg1, POINT &sorg2){

  int dim;
  double ed;

  if(sorg1.get_dim() > sorg2.get_dim())dim = sorg2.get_dim();
  else dim = sorg1.get_dim();
  
  ed = 0.0;
  for(int i = 0;i < dim;i ++)
    ed += pow(sorg2.get_array(i) - sorg1.get_array(i), 2.0);

  return sqrt(ed);

}

/* Decides the belongings for all the dots. 
   Result will be in matrix "belong" */
void determine_cluster(int **belong,
		       POINT grav[], POINT units[], POINT dots[],
		       int n_dot, int n_clus){

  for(int i = 0;i < n_dot;i ++){
    double edist, min_dist;
    int min_j;
    for(int j = 0;j < n_clus;j ++)belong[ i ][ j ] = 0;

    for(int j = 0;j < n_clus;j ++){
      edist = euc_dist(dots[i], units[j]);
      if(j == 0){ min_j = j; min_dist = edist; }
      else if(min_dist > edist){ min_j = j; min_dist = edist; }
    }

    belong[ i ][ min_j ] = 1;
    
  }

}

/* Calculates gravity centers for each clusters. n_dot must be positive. */
void calc_gravity_center(int **belong,
			 POINT grav[], POINT units[], POINT dots[],
			 int n_dot, int n_clus){
  int i,j,k,m,n,p,q,r,s,t,u,v,w, ct;
  POINT gravcalc;
  
  for(j = 0;j < n_clus;j ++){
    ct = 0;
    gravcalc = dots[0] * 0;
    for(i = 0;i < n_dot;i ++){
      gravcalc = gravcalc + (dots[i] * belong[i][j]);
      ct += belong[i][j];
    }
    if(ct > 0)grav[j] = gravcalc / (double)ct;
  }
}

void pt_unitm(int **belong,
	      POINT grav[], POINT units[], POINT dots[],
	      int n_dot, int n_clus, double rate){
  int i,j,k, ct;

  for(i = 0;i < n_clus;i ++){
    ct = 0;
    for(j = 0;j < n_dot;j ++)ct += belong[j][i];
    if(ct > 0)units[i] = units[i] + (grav[i] - units[i]) * rate;
  }
}


void free2dint(int **ar2d){

  delete ar2d[0];
  delete ar2d;

}


int **make2dint(int m, int n){
  
  int *ar;
  int **ar_table;
  int t;

  ar = new int[m * n];
  ar_table = new int *[ m ];
  for(t = 0;t < m;t ++)
    ar_table[ t ] = &ar[t * n];

  return ar_table;

}


/* converts v to string. point indicates how small the number will be
   calculated. ex.point = -2 ... down to 0.01 unit */
void doub_to_str(double v, int point, char str[]){

  int i,j,k,di;

  j = 0;
  if(v < 0){ v = -v; str[j] = '-'; j ++; }
  if(v < 1.0)di = 0;
  else di = (int)log10(v);

  for(i = di;i >= point;i --){
/*    printf("%lf : ",v); */
    if(i == -1){ str[j] = '.'; j ++; }
    str[j] = (char)(v / pow(10.0, i)) + '0'; 
/*    putchar(str[j]); putchar('\n'); */
    j ++;
    v -= pow(10.0, i) * (int)(v / pow(10.0, i));
  }
  str[j] = '\0';
/*  printf("[%s]\n", str); */
}






