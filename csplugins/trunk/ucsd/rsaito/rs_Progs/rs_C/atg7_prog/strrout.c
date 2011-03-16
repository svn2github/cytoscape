#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include "global_st.h"

/* static variables used in this block */
static int chi_freedom = 2;

/* prototypes */
double gamma_r2(double);
double chi_density(double, int);
double chi_density2(double, int);
double daikei_integral(double (*)(double), double, double, int);
double daikei_integral_chi(double (*)(double), double, double, double);
double chi_integ_temp(double);
double norm_half(double);
double std_norm_dist(double);
double poisson(int, double);
double poisson_over(int, double);
double kaijo(int);
int spmatch(char [], char []);
int spmatchn(char [], char [], int);
int next_patsp(char [], int);
int next_patsp2(char [], int);
int testbit(struct bitset, int);
void setbit(struct bitset *, int, int);
char *compseqget(char [], int);
int incwi(char *, char *, int);
void rev(char *);
int countmatch(char *, char *, int);
int candrec(struct recbest10 *, struct recbest10[], int);
int comp_match(char, char);


double gamma_r2(double x)
/* accepts only for the case where x is natural number or 
   natural number + 1/2 */
{
   int i,j,k;
   double ans;

   if(x - (int)x == 0){
      ans = 1.0;
      for(i = (int)x - 1;i >= 1;i --)
         ans *= (double)i;
      return(ans);
    }
   else if(x - (int)x == 0.5){
      ans = 1.0;
      for(i = 0;i < x  - 0.5 - 0.1 /* 0.1 is extra */ ;i ++)
         ans *= (i + 0.5);
      return(ans * 1.77245385);
    }
   else {
      fprintf(stderr, "%lf was input to gamma_r2 function\n", x);
      exit(1);
    }
 }


double chi_density(double x_2, int m)
{
   double ans;

   if(m < 2){
      fprintf(stderr, "%d was input to chi_density function\n", m);
      exit(1);
    }
   ans = pow(x_2, 1.0*m/2 - 1) * exp(-0.5 * x_2);
   ans = ans / pow(2.0, 1.0*m/2) / gamma_r2(1.0*m / 2);
   return(ans);

 }

double chi_density2(double x_2, int m)
{
   double ans;

   if(m < 2){
      fprintf(stderr, "%d was input to chi_density function\n", m);
      exit(1);
    }
   ans = pow(x_2, 1.0*m/2 - 1) * exp(-0.5 * x_2);
   return(ans);

 }


double daikei_integral(double (*func)(double), double a, double b, int n)
{
   int i;
   double h;
   double ans;

   h  = (b - a) / n;
   ans = .0;
   for(i = 0; i <= n;i ++){
      if(i == 0)ans += (*func)(a);
      else if(i == n)ans += (*func)(b);
      else ans += 2 * (*func)(a + i * (b - a) / n);
    }
   ans *= h / 2;
   return(ans);
 }

double daikei_integral_chi(double (*func)(double), 
			   double a, double b, double h)
{
  double i;
  double ans;
  
  ans = .0;
  ans += (*func)(a);

  for(i = a + h; i < b;i += h){
    ans += 2 * (*func)(i);
  }
  ans += (*func)(i);
  ans *= h / 2;
  return(ans);
}

/* This function uses global variable "chi_freedom" */
double chi_integ_temp(double x)
{
   return(chi_density2(x, chi_freedom));

 }

double x_2_p1(double x)
{
   return x*x+1;
 }

/* This function uses global variable "chi_freedom" */
double chi_prob(int freedom, double chi){
#define CHI_ACC 0.001
  chi_freedom = freedom;
  return(1.0 - daikei_integral_chi(chi_integ_temp, .0, chi, CHI_ACC)
               / pow(2.0, 1.0*chi_freedom/2)/gamma_r2(1.0*chi_freedom/2));
}


double std_norm_dist(double x){
  
  return(1.0 / sqrt(2.0 * 3.1415926535) * exp( - pow(x, 2.0) / 2));

}

/* Integral value from 0 to x of standard normal distribution */
double norm_half(double x){
#define NORM_ACC 0.001

  if(x < 0.0)x *= -1.0;
  return daikei_integral_chi(std_norm_dist, 0.0, x, NORM_ACC);

}

double poisson(int x, double lambda){

  return(pow(lambda, x) / kaijo(x) * exp(-1.0 * lambda));

}

double poisson_over(int x, double lambda){

  int n;
  double ans;
  ans = 1.0;
  for(n = 0;n < x;n ++){
    ans -= poisson(n, lambda);
    if(ans < .0){ ans = .0; break; }
/*
    printf("%d:%lf(%lf) %lf(%lf)\n", n, poisson(n, lambda), 
	   log(poisson(n, lambda)), ans, log(ans)); 
*/
  }
  return ans;

}

double kaijo(int x){
  int n;
  double ans = 1.0;

  if(x <= 1)return 1;
  for(n = x;n > 0;n --)ans *= (double)n;
  return ans;

}

int lpatm(char *str1,char *line1)
/* return 1 if the first pattern matches
   abc & abcde -> 1     abcde & abc -> 0 */
{
   int i,flag;
   i = 0;flag = 0;
       
   while(1){
      if(str1[i] == '\0'){flag = 1; break; }
      if(line1[i] == '\0'){flag = 0; break; }
      if(str1[i] != line1[i]){flag = 0; break;}
      i ++;
    }
   return flag;
 }


int lpatms3(char *str1,char *line1)
/* if str1 is included in line1, 1 will be returned */
{
  int i;
  for(i = 0;i <= (int)(strlen(line1) - strlen(str1));i ++){
    if(lpatm(str1, &line1[i]))return 1;
  }
  return 0;
}

int lpatmAa(char *str1,char *line1)
/* return 1 if the first pattern matches
   aBc & AbCde -> 1     aBcde & Abc -> 0 */
{
   int i,flag;
   i = 0;flag = 0;
       
   while(1){
      if(str1[i] == '\0'){flag = 1; break; }
      if(line1[i] == '\0'){flag = 0; break; }
      if(str1[i] != line1[i] && str1[i] - line1[i] != 'A' - 'a'
	 && str1[i] - line1[i] != 'a' - 'A'){flag = 0; break;}
      i ++;
    }
   return flag;
 }

int find_word(char *str1,char *line1)
/* if str1 is included in line1, 1 will be returned 
   Upper/Lower case is ignored */
{
  int i;
  for(i = 0;i <= (int)(strlen(line1) - strlen(str1));i ++){
    if(lpatmAa(str1, &line1[i]))return 1;
  }
  return 0;
}

#define ONE_CHAR '.'
#define ZON_CHAR '-'

/* If the pattern specified by pat matches to the head of seq, 1 will be 
   returned. Otherwise, 0 will be returned. */
int spmatch(char seq[], char pat[])
{
  int i, j;
  int length;
  
  length = strlen(pat);
  for(i = 0;i < length;i ++){
    switch(pat[i]){
    case ONE_CHAR:
      if(seq[i] == '\0')return 0;
      break;
    case ZON_CHAR:
      if(spmatch(&seq[i], &pat[i + 1]) == 1)return 1;
      else if(seq[i] == '\0')return 0;
      break;
    default:
      if(pat[i] != seq[i])return 0;
      break;
    }
  }
  return 1;
}

int spmatchn(char seq[], char pat[], int n)
{
  static char seq1[30], pat1[30];
  int i;

  if(n > 30){
    fprintf(stderr, "Error in function spmatchn.\n");
    exit(1);
  }

  strncpy(seq1, seq, n); seq1[n] = '\0';
  strncpy(pat1, pat, n); pat1[n] = '\0';

  return spmatch(seq1, pat1);
}


/* if over, 1 will be returned and pat will be broken. */
int next_patsp(char pat[], int max){
  int i,j;

  for(i = 0;i < strlen(pat);i ++){
    switch(pat[i]){
      case 'a':pat[i] = 1;break;
      case 't':pat[i] = 2;break;
      case 'c':pat[i] = 3;break;
      case 'g':pat[i] = 4;break;
      case '.':pat[i] = 5;break;
      case '-':pat[i] = 6;break;
      case '\0':pat[i] = 0; break;
      }
  }

  i = 0;
  while(1){
    if(pat[i] == 0){
      if(strlen(pat) >= max)return 1;
      else { pat[i] = 1; pat[i + 1] = 0; break; }
    }
    pat[i] ++;
/*
    if(i == 0 || i == strlen(pat) - 1){
      if(pat[i] > 4)pat[i] = 1; 
      else { break; }
    }

    else */ if(pat[i] > 4)pat[i] = 1;
    else { break; }

    i ++;
  }

  for(i = 0;i < strlen(pat);i ++){
    switch(pat[i]){
      case 1:pat[i] = 'a';break;
      case 2:pat[i] = 't';break;
      case 3:pat[i] = 'c';break;
      case 4:pat[i] = 'g';break;
      case 5:pat[i] = '.';break;
      case 6:pat[i] = '-';break;
      case 0:pat[i] = '\0';break;
      }
  }
  return 0;

}

/* if over, 1 will be returned and pat will be broken. */
int next_patsp2(char pat[], int max){
  int i,j;

  for(i = 0;i < strlen(pat);i ++){
    switch(pat[i]){
      case 'a':pat[i] = 1;break;
      case 't':pat[i] = 2;break;
      case 'c':pat[i] = 3;break;
      case 'g':pat[i] = 4;break;
      case '.':pat[i] = 5;break;
      case '-':pat[i] = 6;break;
      case '\0':pat[i] = 0; break;
      }
  }

  i = 0;
  while(1){
    if(pat[i] == 0){
      if(strlen(pat) >= max)return 1;
      else { pat[i] = 1; pat[i + 1] = 0; break; }
    }
    pat[i] ++;
/*
    if(i == 0 || i == strlen(pat) - 1){
      if(pat[i] > 4)pat[i] = 1; 
      else { break; }
    }

    else */ if(pat[i] > 6)pat[i] = 1; /* !!! */
    else { break; }

    i ++;
  }

  for(i = 0;i < strlen(pat);i ++){
    switch(pat[i]){
      case 1:pat[i] = 'a';break;
      case 2:pat[i] = 't';break;
      case 3:pat[i] = 'c';break;
      case 4:pat[i] = 'g';break;
      case 5:pat[i] = '.';break;
      case 6:pat[i] = '-';break;
      case 0:pat[i] = '\0';break;
      }
  }
  return 0;

}

int testbit(struct bitset base, int b)
{
  switch(b){
  case 0:return base.b0;
  case 1:return base.b1;
  case 2:return base.b2;
  case 3:return base.b3;
  case 4:return base.b4;
  case 5:return base.b5;
  case 6:return base.b6;
  case 7:return base.b7;
  case 8:return base.b8;
  case 9:return base.b9;
  case 10:return base.b10;
  case 11:return base.b11;
  case 12:return base.b12;
  case 13:return base.b13;
  case 14:return base.b14;
  case 15:return base.b15;
  case 16:return base.b16;
  case 17:return base.b17;
  case 18:return base.b18;
  case 19:return base.b19;
  case 20:return base.b20;
  case 21:return base.b21;
  case 22:return base.b22;
  case 23:return base.b23;
  case 24:return base.b24;
  case 25:return base.b25;
  case 26:return base.b26;
  case 27:return base.b27;
  case 28:return base.b28;
  case 29:return base.b29;
  default:return -1;
  }

}

void setbit(struct bitset *base, int b, int n)
{
  switch(b){
  case 0: base->b0 = n; return;
  case 1: base->b1 = n; return;
  case 2: base->b2 = n; return;
  case 3: base->b3 = n; return;
  case 4: base->b4 = n; return;
  case 5: base->b5 = n; return;
  case 6: base->b6 = n; return;
  case 7: base->b7 = n; return;
  case 8: base->b8 = n; return;
  case 9: base->b9 = n; return;
  case 10: base->b10 = n; return;
  case 11: base->b11 = n; return;
  case 12: base->b12 = n; return;
  case 13: base->b13 = n; return;
  case 14: base->b14 = n; return;
  case 15: base->b15 = n; return;
  case 16: base->b16 = n; return;
  case 17: base->b17 = n; return;
  case 18: base->b18 = n; return;
  case 19: base->b19 = n; return;
  case 20: base->b20 = n; return;
  case 21: base->b21 = n; return;
  case 22: base->b22 = n; return;
  case 23: base->b23 = n; return;
  case 24: base->b24 = n; return;
  case 25: base->b25 = n; return;
  case 26: base->b26 = n; return;
  case 27: base->b27 = n; return;
  case 28: base->b28 = n; return;
  case 29: base->b29 = n; return;
  }
}

int cmpl(char c)
{
  switch(c){
  case 'a':return 't';break;
  case 't':return 'a';break;
  case 'c':return 'g';break;
  case 'g':return 'c';break;
  case 'A':return 'T';break;
  case 'T':return 'A';break;
  case 'C':return 'G';break;
  case 'G':return 'C';break;
  default:return c;break;

  }
}

char *compseqget(char seqn[], int max)
{
  int i;
  char *compseqn;

  compseqn = (char *)(malloc(max * sizeof(char)));
  if(compseqn ==  NULL){
    fprintf(stderr,"Memory full when making complement sequence.\n");
    exit(1);
  }
  for(i = max;i > 0;i --)compseqn[max - i] = cmpl(seqn[i - 1]);
  return compseqn;
}

/* Tests if string "str2" is included in "str1" within 
   the first "within" bases:ex. "ttatgag" "atg" 3 -> OK, 2 -> NO */
int incwi(char *str1, char *str2, int within){
  int i;

  for(i = 0;i < within  && str1[i] != '\0';i ++)
    if(spmatch(&str1[i], str2) == 1)return 1;
  return 0;

}

void rev(char str[]){
  
  int i,j;
  char tmp;

  j = strlen(str) - 1;
  for(i = 0;i < j;i ++, j --){
    tmp = str[i];
    str[i] = str[j];
    str[j] = tmp;
  }
  
}

int countmatch(char seq1[], char seq2[], int length)
{
  int i, ret;
  for(ret = 0, i = 0;i < length;i ++)
    if(seq1[i] == seq2[i])ret ++;

  return ret;

}

int candrec(struct recbest10 *rcandidate, struct recbest10 r[], int rec_num){

  int i,j,k, nth;

  /* Investigate where *rcandidate can be in rec_num */

  for(i = rec_num - 1;i >= 0;i --){
    if(rcandidate->value <= r[i].value)break;
  }

  nth = i + 1; /* from 0th */
  if(nth >= rec_num)return 0;

  for(i = rec_num - 1;i >= nth + 1;i --){
    r[i].value = r[i - 1].value;
    r[i].int1  = r[i - 1].int1;
    strcpy(r[i].str1,r[i - 1].str1);
    strcpy(r[i].str2,r[i - 1].str2);
  }
  r[nth].value = rcandidate->value;
  r[nth].int1  = rcandidate->int1;
  strcpy(r[nth].str1, rcandidate->str1);
  strcpy(r[nth].str2, rcandidate->str2);
  return 1;

}

int comp_match(char c1, char c2)
{

   static char *pair[] = {
      "at", "cg", "gt"
   };

   int i,j;

   for(i = 0; i < 3;i ++)
      if((c1 == pair[i][0] && c2 == pair[i][1]) ||
         (c1 == pair[i][1] && c2 == pair[i][0]))
            return 1;
   return 0;

}

