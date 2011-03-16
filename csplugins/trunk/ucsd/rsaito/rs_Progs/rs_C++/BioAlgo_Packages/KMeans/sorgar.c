#include <math.h>
#include <string.h>
#include <iostream.h>
#include "sorgar.h"

SORGAR::SORGAR(){
   num = 0;
}

SORGAR SORGAR::operator+(SORGAR sorgarp){

   SORGAR sorgar_ret = *this; /* Copies class itself(content of "this value")
                                 to class for return value */
   int loop;

   if(sorgarp.num > num)loop = num;
   else loop = sorgarp.num;

   for(int i = 0;i < loop;i ++)
      sorgar_ret.arr[i] += sorgarp.arr[i];

   return sorgar_ret;
}

SORGAR SORGAR::operator-(SORGAR sorgarp){

   SORGAR sorgar_ret = *this; /* Copies class itself(content of "this value")
                                 to class for return value */
   int loop;

   if(sorgarp.num > num)loop = num;
   else loop = sorgarp.num;

   for(int i = 0;i < loop;i ++)
      sorgar_ret.arr[i] -= sorgarp.arr[i];

   return sorgar_ret;
}

SORGAR SORGAR::operator*(double sorgarv){

   SORGAR sorgar_ret = *this; /* Copies class itself(content of "this value")
                                 to class for return value */
   for(int i = 0;i < num;i ++)
      sorgar_ret.arr[i] *= sorgarv;

   return sorgar_ret;
}

SORGAR SORGAR::operator/(double sorgarv){

   SORGAR sorgar_ret = *this; /* Copies class itself(content of "this value")
                                 to class for return value */
   for(int i = 0;i < num;i ++)
      sorgar_ret.arr[i] /= sorgarv;

   return sorgar_ret;
}

void SORGAR::app(double data){
   if(num < MAXDIM){
     arr[num] = data;
     num ++;
   }
}

void SORGAR::display(){

  for(int i = 0;i < num;i ++){
     cout << i << ':' << arr[i] << ' '; 
  }
  cout << '\n';
}

double euc_dist(SORGAR &sorg1, SORGAR &sorg2){

  int dim;
  double ed;

  if(sorg1.num > sorg2.num)dim = sorg2.num;
  else dim = sorg1.num;
  
  ed = 0.0;
  for(int i = 0;i < dim;i ++)
    ed += pow(sorg2.arr[i] - sorg1.arr[i], 2.0);

  return sqrt(ed);

}

/* Decides which cluster that dot dotn belongs to.
   Result will be in matrix dotg */
void so_belong_opt(SORGAR dots[], SORGAR units[], int n_dot, int n_clus,
		   int **dotg, int dotn){
  int i,j,k,m,n,p,q,r,s,t,u,v,w, min;
  double edist, min_dist;

  for(j = 0;j < n_clus;j ++)dotg[ dotn ][ j ] = 0;

  for(j = 0;j < n_clus;j ++){
    edist = euc_dist(dots[ dotn ], units[j]);
    if(j == 0){ min = j; min_dist = edist; }
    else if(min_dist > edist){ min = j; min_dist = edist; }
  }

  dotg[ dotn ][ min ] = 1;

  // cout << "Dot #" << dotn << " belongs to " << min << '\n' << flush ;
}

/* Decides the belongings for all the dots. */ 
void so_belong(SORGAR dots[], SORGAR grav[], SORGAR units[], 
	       int n_dot, int n_clus, int **dotg){
  int i,j,k,m,n,p,q,r,s,t,u,v,w;

  for(i = 0;i < n_dot;i ++)
    so_belong_opt(dots, units, n_dot, n_clus, dotg, i);

}

/* Calculates gravity centers for each clusters. n_dot must be positive. */
void so_grav(SORGAR dots[], SORGAR grav[], SORGAR units[], 
	     int n_dot, int n_clus, int **dotg){
  int i,j,k,m,n,p,q,r,s,t,u,v,w, ct;
  SORGAR gravcalc;
  
  for(j = 0;j < n_clus;j ++){
    ct = 0;
    gravcalc = dots[0] * 0;
    for(i = 0;i < n_dot;i ++){
      gravcalc = gravcalc + (dots[i] * dotg[i][j]);
      ct += dotg[i][j];
    }
    if(ct > 0)grav[j] = gravcalc / (double)ct;
  }
}

void so_unitm(SORGAR dots[], SORGAR units[], SORGAR grav[],
	      int n_dot, int n_clus, int **dotg, double rate){
  int i,j,k, ct;

  for(i = 0;i < n_clus;i ++){
    ct = 0;
    for(j = 0;j < n_dot;j ++)ct += dotg[j][i];
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






