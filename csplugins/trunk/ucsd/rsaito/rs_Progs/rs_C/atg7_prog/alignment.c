#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define GAPM '-'

/* プロトタイプ宣言 */
double free_e(char [], char []);
double str_penalty(int, int);

/* structure of matrix (ban)
         A     C
         T     C
      o     o     o
    (0,0) (0,1) (0,2)
ATC    (0,0) -------------locations of first letters
      o     o     o
    (1,0) (1,1)
C-T    (1,0)   \
      o ->  o     o
        ^   ^
        this arrow is kept in this node

*/

#define UP 1
#define LEFT 2
#define CROSS 3

struct dpmatrix_d {
   double score;
   int dir;
};

double base_pair_test(char [], char [], int, int, struct dpmatrix_d **);

struct dpmatrix_d **banmake(int tate,int yoko)
/* makes dp matrix with size tate x yoko */
/* tate = s_len + 1 */
{
  struct dpmatrix_d *rec,**rec_table;
  int n;

  rec = (struct dpmatrix_d *)malloc(tate * yoko * sizeof(struct dpmatrix_d));
  rec_table = (struct dpmatrix_d **)
                 malloc(tate * sizeof(struct dpmatrix_d *));

  for(n = 0;n < tate; n++)
    rec_table[n] = &rec[n * yoko];

  return rec_table;
}

void banfree(struct dpmatrix_d **ban) 
/* release memory for ban */
{
  free(ban[0]);
  free(ban);
}

void match_res(char seque1[], char seque2[], struct dpmatrix_d **ban,
	       int nth1, int nth2,
	       int match_res1[], int match_res2[]){

  int i,j;

  if(base_pair_test(seque1, seque2, nth1, nth2, ban) == 1000.0)return;

/* It is sure that two base pairs will match. */  
  i = nth1 - 1;
  j = nth2 - 1;

  match_res1[ i ] = 1;
  match_res2[ j ] = 1;

  while(i > 0 && j > 0){
    switch(ban[i][j].dir){
    case UP:i --; break;
    case LEFT:j --; break;
    case CROSS:i --; j --;
      match_res1[i] = 1;
      match_res2[j] = 1;
      return; 
    }
  }
}



int bantoresult_sd(char seque1[], char seque2[],
struct dpmatrix_d **ban, char seq1_res[], char seq2_res[],
int match_res1[], int match_res2[])
/* put result by looking at ban */
/* result length will be returned */
/* memory for result must be allocated in the main */
{
   int i,j,i2,j2;

   int nc1,nc2;
   int res_len; /* result length */
   int begin,end;
   char tmpchar;

   nc1 = strlen(seque1);
   nc2 = strlen(seque2);

   res_len = 0;

   while(nc1 > 0 || nc2 > 0){
/*    printf("Location (%d, %d)\n", nc1, nc2); */
      switch(ban[nc1][nc2].dir){
       case UP:
	nc1 --;  /* printf("UP\n"); */ 
	seq1_res[res_len] = seque1[nc1];
	seq2_res[res_len] = GAPM;
	break;
	
       case LEFT:
	nc2 --;  /* printf("LEFT\n"); */ 
	seq1_res[res_len] = GAPM;
	seq2_res[res_len] = seque2[nc2];
	break;
       case CROSS:
	match_res(seque1, seque2, ban, nc1, nc2,
		  match_res1, match_res2);
	nc1 --;nc2 --;  /* printf("CROSS\n"); */
	seq1_res[res_len] = seque1[nc1];
	seq2_res[res_len] = seque2[nc2];
	break;
      }
      res_len ++;
   }
   /* printf("result length %d\n",res_len); */

   for(begin = 0,end = res_len - 1;begin < end;begin ++,end --){
     tmpchar = seq1_res[begin];
     seq1_res[begin] = seq1_res[end];
     seq1_res[end] = tmpchar;

     tmpchar = seq2_res[begin];
     seq2_res[begin] = seq2_res[end];
     seq2_res[end] = tmpchar;
   } /* reverse */

   seq1_res[ res_len ] = '\0';
   seq2_res[ res_len ] = '\0';
 
/*
   for(i = 0;i < strlen(seq1_res);i ++)
     printf("%d",match_res1[i]);

   putchar('\n');

   for(i = 0;i < strlen(seq2_res);i ++)
     printf("%d",match_res2[i]);

   putchar('\n');
*/
 
   return res_len;
}

void print_ban_sd(struct dpmatrix_d **ban, int t, int y)
{
   int i,j;

   printf("printing matrix(%d %d) score\n",t,y);
   for(i = 0;i <= t;i ++){
     for(j = 0;j <= y;j ++){
        printf("%5.2lf ",ban[i][j].score);
     }
     putchar('\n');
   }
   printf("printing matrix(%d %d) dir\n",t,y);
   for(i = 0;i <= t;i ++){
     for(j = 0;j <= y;j ++){
	printf("%3d",ban[i][j].dir);
     }
     putchar('\n');
   }
} 

int min3_d(double x,double y,double z)
{

  if(z <= x){
    if(z <= y)return 2;
    else return 1;
  }
  else if(y <= x)return 1;
  else return 0;
}

double sd_match_test(char seq1[], char seq2[]){

  static char pat1[5];
  static char pat2[5];

  pat1[0] = seq1[0]; pat1[1] = seq2[0]; pat1[2] = '\0';
  pat2[0] = seq1[1]; pat2[1] = seq2[1]; pat2[2] = '\0';

  return free_e(pat1, pat2);

}

double base_pair_test(char seque1[], char seque2[], int nth1, int nth2,
		      struct dpmatrix_d **ban){
  int seque1_len, seque2_len;
  int i,j, di, dj;
  static char pat[5];

/* This decision must be done in main routine.
  if(ban[nth1][nth2].dir != CROSS)return 1000.0;
*/

  seque1_len = strlen(seque1);
  seque2_len = strlen(seque2);

  i = nth1 - 1; j = nth2 - 1;

  if(i > 0 && j > 0 && ban[i][j].dir == CROSS &&
     sd_match_test(&seque1[i - 1], &seque2[j - 1]) != 1000.0)
    return sd_match_test(&seque1[i - 1], &seque2[j - 1]);
  
  while(i > 0 && j > 0 && (nth1 - i == 1 || nth2 - j == 1)){
/*
    printf("Looking at location (%d, %d)\n", i, j);
*/
    if(nth1 - i == 1 && ban[i][j].dir == CROSS){
      pat[0] = seque2[j - 1];
      pat[1] = seque2[nth2 - 1];
      pat[2] = '\0';
      if(sd_match_test(&seque1[nth1 - 2], pat) != 1000)
	return sd_match_test(&seque1[nth1 - 2], pat);
    }

    if(nth2 - j == 1 && ban[i][j].dir == CROSS){
      pat[0] = seque1[i - 1];
      pat[1] = seque1[nth1 - 1];
      pat[2] = '\0';
      if(sd_match_test(&seque2[nth2 - 2], pat) != 1000)
	return sd_match_test(&seque2[nth2 - 2], pat);
    }
    switch(ban[i][j].dir){
    case UP: i --; break;
    case LEFT:j --;break;
    case CROSS:i --; j --; break;
    default:fprintf(stderr, "Illegal direction...\n"); exit(1); break;
    }
  }
  return 1000.0;
}



double uscore_calc_sd(char seque1[], char seque2[], int nth1, int nth2,
		      struct dpmatrix_d **ban){
  int seque1_len, seque2_len;
  int i,j, di, dj;

  seque1_len = strlen(seque1);
  seque2_len = strlen(seque2);

  i = nth1 - 1; j = nth2;

  while(i > 1 && j > 1){
/*
    printf("Looking at location (%d, %d)\n", i, j);
*/

    if(ban[i][j].dir == CROSS && 
       base_pair_test(seque1, seque2, i, j, ban) != 1000.0)break;
    
    switch(ban[i][j].dir){
    case UP: i --; break;
    case LEFT:j --;break;
    case CROSS:i --; j --; break;
    default:fprintf(stderr, "Illegal direction...\n"); exit(1); break;
    }
  }

  di = nth1 - i;
  dj = nth2 - j;
/*
  printf("Final position (%d, %d)...(%d, %d)\n", i, j, di, dj);
*/
  if(i <= 1 || j <= 1)return 0.0;

  if(seque1_len == nth1 && seque2_len == nth2){
    if(di > 1 && dj == 0)return -3.3;
    else if(di == 1 & dj >= 1)return -3.3;
    else if(di > 1 && dj >= 1)return -0.8;
    else return 0.0;
  }


  if(dj == 0 && di == 1)return 3.3;
  else if(dj == 1 && di == 1)return 0.8 - 3.3;
  else return 0.0;

}

double sscore_calc_sd(char seque1[], char seque2[], int nth1, int nth2,
		      struct dpmatrix_d **ban){
  int seque1_len, seque2_len;
  int i,j, di, dj;

  seque1_len = strlen(seque1);
  seque2_len = strlen(seque2);

  i = nth1; j = nth2 - 1;

  while(i > 1 && j > 1){
/*
    printf("Looking at location (%d, %d)\n", i, j);
*/
    
    if(ban[i][j].dir == CROSS &&
       base_pair_test(seque1, seque2, i, j, ban) != 1000.0)break;
    
    switch(ban[i][j].dir){
    case UP: i --; break;
    case LEFT:j --;break;
    case CROSS:i --; j --; break;
    default:fprintf(stderr, "Illegal direction...\n"); exit(1); break;
    }
  }

  di = nth1 - i;
  dj = nth2 - j;
/*
  printf("Final position (%d, %d)...(%d, %d)\n", i, j, di, dj);
*/
  if(i <= 1 || j <= 1)return 0.0;

  if(seque1_len == nth1 && seque2_len == nth2){
    if(di == 0 && dj > 1)return -3.3;
    else if(di >= 1 && dj == 1)return -3.3;
    else if(di >= 1 && dj > 1)return -0.8;
    else return 0;
  }

  if(di == 0 && dj == 1)return 3.3;
  else if(di == 1 && dj == 1)return 0.8 - 3.3;
  else return 0.0;
}

double dscore_calc_sd(char seque1[], char seque2[], int nth1, int nth2,
		      struct dpmatrix_d **ban){
  int seque1_len, seque2_len;
  int i,j, di, dj;
  static char pat[5];

  seque1_len = strlen(seque1);
  seque2_len = strlen(seque2);

  if(base_pair_test(seque1, seque2, nth1, nth2, ban) != 1000)
    return base_pair_test(seque1, seque2, nth1, nth2, ban);

  i = nth1 - 1; j = nth2 - 1;

  while(i > 1 && j > 1){
/*    
    printf("Looking at location (%d, %d)\n", i, j);
*/    
    if(ban[i][j].dir == CROSS && 
       base_pair_test(seque1, seque2, i, j, ban) != 1000)
      break;
    
    switch(ban[i][j].dir){
    case UP: i --; break;
    case LEFT:j --;break;
    case CROSS:i --; j --; break;
    default:fprintf(stderr, "Illegal direction...\n"); exit(1); break;
    }
  }
  
  di = nth1 - i;
  dj = nth2 - j;
/*
  printf("Final position (%d, %d)...(%d, %d)\n", i, j, di, dj);
*/
  if(i <= 1 || j <= 1)return 0.0;

  if(di == 1 && dj == 1)return +0.8;
  else if(di == 1){
    if(seque1_len == nth1 && seque2_len == nth2)
      return -3.3;
    else return 0.8 - 3.3;
  }
  else if(dj == 1){
    if(seque1_len == nth1 && seque2_len == nth2)
      return -3.3;
    else return 0.8 - 3.3;
  }
  else if(seque1_len == nth1 && seque2_len == nth2)
    return -0.8;
  else return 0.0;
}



double sd_match(char seque1[], char seque2[], 
		char result1[], char result2[], int *result_len,
		int match_res1[], int match_res2[])
{
  struct dpmatrix_d **ban;
  int seque1_len, seque2_len;
  double ret_score;
  double uscore, sscore, dscore;
  int i,j;

  seque1_len = strlen(seque1);
  seque2_len = strlen(seque2);

  for(i = 0;i < seque1_len;i ++)
    match_res1[i] = 0;
  for(i = 0;i < seque2_len;i ++)
    match_res2[i] = 0;

  ban = banmake(seque1_len + 1, seque2_len + 1);
  ban[0][0].score = 0.0;
  ban[0][0].dir = 0;

  for(i = 1;i <= seque1_len;i ++){
    ban[i][0].score = 0.0;
    ban[i][0].dir = UP;
  }

  for(j = 1;j <= seque2_len;j ++){
    ban[0][j].score = 0.0;
    ban[0][j].dir = LEFT;
  }

  for(i = 1;i <= seque1_len;i ++)
    for(j = 1;j <= seque2_len;j ++){
      uscore = uscore_calc_sd(seque1, seque2, i, j, ban)
	+ ban[i-1][j].score;
      sscore = sscore_calc_sd(seque1, seque2, i, j, ban)
	+ ban[i][j-1].score;
      dscore = dscore_calc_sd(seque1, seque2, i, j, ban)
	+ ban[i-1][j-1].score;
/*
      printf("At (%d, %d)... UP %lf   LEFT %lf   Cross %lf ",
	     i, j, uscore, sscore, dscore);
*/
      switch(min3_d(uscore,sscore,dscore)){
         case 0:ban[i][j].score = uscore;
                ban[i][j].dir = UP;break;
         case 1:ban[i][j].score = sscore;
                ban[i][j].dir = LEFT;break;
         case 2:ban[i][j].score = dscore;
                ban[i][j].dir = CROSS;break;
         default:printf("Error in arc direction\n");break;
	 }
/*
      printf("%d\n",min3_d(uscore, sscore, dscore));
*/
    }
/*
  print_ban_sd(ban, seque1_len, seque2_len);
*/
  ret_score = ban[seque1_len][seque2_len].score;
  *result_len = bantoresult_sd(seque1, seque2, ban, result1, result2,
			       match_res1, match_res2);
  banfree(ban);
  return ret_score;
}


#define DEBUG_LEVEL 0

/* makes double matrix with size tate x yoko */
double **doublemx_make(int tate,int yoko)
{
  double *rec,**rec_table;
  int n;

  rec = (double *)malloc(tate * yoko * sizeof(double));
  rec_table = (double **)
                 malloc(tate * sizeof(double *));

  for(n = 0;n < tate; n++)
    rec_table[n] = &rec[n * yoko];

  return rec_table;
}

/* release memory for double matrix */
void doublemx_free(double **mx) 
{
  free(mx[0]);
  free(mx);
}

/* makes int matrix with size tate x yoko */
int **intmx_make(int tate,int yoko)
{
  int *rec,**rec_table;
  int n;

  rec = (int *)malloc(tate * yoko * sizeof(int));
  rec_table = (int **)
                 malloc(tate * sizeof(int *));

  for(n = 0;n < tate; n++)
    rec_table[n] = &rec[n * yoko];

  return rec_table;
}

/* release memory for int matrix */
void intmx_free(int **mx) 
{
  free(mx[0]);
  free(mx);
}



struct seqgraph {

  char *seq1, *seq2; /* 配列1,2とその長さ */
  int  seq1_len, seq2_len;

  double **min_free; /* 自由エネルギーの最小値格納行列
			seq1[p]とseq2[q]が結合する時のp,q以降の
			最小自由エネルギー */

  int **min_free_valid; /* 最小値行列に値が格納されているなら1
			   そうでなければ0 */

  int **min_next1; /* 最小の自由エネルギーをとる時の次の配列1の結合位置 */
  int **min_next2; /* 最小の自由エネルギーをとる時の次の配列2の結合位置 */

};

/* 塩基c1とc2が対合可能なら1を返す。そうでなければ0を返す。 */
int bpairok(char c1, char c2)
{

   const static char *pair[] = {
      "at", "cg", "gt"
   };

   int i,j;

   for(i = 0; i < 3;i ++)
      if((c1 == pair[i][0] && c2 == pair[i][1]) ||
         (c1 == pair[i][1] && c2 == pair[i][0]))
            return 1;
   return 0;

}


/* pとqが結合する時のp,q以降の部分の最低自由エネルギー */
/* 再帰アルゴリズム: 
   find_min(p.q) = find(i,j) + basepair(i,j,i-1,j-1) + penalty(p,q,i-1,j-1)
*/
double find_min(struct seqgraph *sg, int p, int q){

  int i, j, pos1 = -1, pos2 = -1;
  double min = 0.0, fe1,fe, penalty, basep;
  static char pair5[2], pair3[2];
#if DEBUG_LEVEL >= 3
  printf("*** Calculating minimum free energy of (%d, %d)\n", p, q);
#endif
  if(sg->min_free_valid[p][q]){
#if DEBUG_LEVEL >= 3
    printf("Min_free(%d, %d) have already been calculated(%.2lf)\n",
	   p, q, sg->min_free[p][q]);
#endif
    return sg->min_free[p][q];
  }
  /* すでに計算済みならその値を使う */

  else if(p == sg->seq1_len - 1 || q == sg->seq2_len - 1){
#if DEBUG_LEVEL >= 3
    printf("Sequence reached end at (%d, %d)\n", p, q);
#endif
    return 0.0;
  }
  /* 配列の末端に達している */

  else if(p > sg->seq1_len - 1 || q > sg->seq2_len - 1){
    fprintf(stderr, "Error in recognizing sequence length...\n");
    exit(0);
  }

  penalty = 0; basep = -4.0; /* temporary */

  for(i = p;i < sg->seq1_len - 1;i ++)
    for(j = q;j < sg->seq2_len - 1;j ++){

      /* 最初の5'末端以外は末端同士が結合していなくてはならない */
      if(p != 0 && q != 0 && i == p && j != q)i ++;
      if(p != 0 && q != 0 && i != p && j == q)j ++;

      if(bpairok(sg->seq1[i], sg->seq2[j]) &&
	 bpairok(sg->seq1[i + 1], sg->seq2[j + 1])){
#if DEBUG_LEVEL >= 3
	printf("(%d, %d)Sequence match at (%d, %d)\n", p,q,i, j);
#endif
	fe1 = find_min(sg, i + 1, j + 1);
#if DEBUG_LEVEL >= 3
	printf("** Returning to calculation of minimum free energy at (%d, %d)\n", p, q);
#endif
	/* ペナルティ計算 */
	if(p == 0 && q == 0)penalty = 0.0;
	else if(p == i && q == j)penalty = 0.0;
	else penalty = str_penalty(i - p - 1, j - q - 1);

	/* 対合による自由エネルギーの計算 */
	pair5[0] = sg->seq1[i]    ; pair5[1] = sg->seq2[j];
	pair3[0] = sg->seq1[i + 1]; pair3[1] = sg->seq2[j + 1];
	basep = free_e(pair5, pair3);

	fe  = fe1 + penalty + basep;
#if DEBUG_LEVEL >= 3
	printf("(%d, %d) - Min(%d, %d) = %.2lf(bpr) + %.2lf(pe) +%.2lf(min) = %.2lf (min %.2lf)\n",
	       p, q, i, j, basep, penalty, fe1, fe, min);
#endif
	if(fe < min){ 
	  min = fe;
	  pos1 = i;
	  pos2 = j;
#if DEBUG_LEVEL >= 3
	  printf("Found new minimum free energy\n");
#endif
	} 
      }
    }

  sg->min_free[p][q] = min;
  sg->min_next1[p][q] = pos1;
  sg->min_next2[p][q] = pos2;
  sg->min_free_valid[p][q] = 1;
#if DEBUG_LEVEL >= 3
  printf("At position (%d, %d) min = %lf  pos1 = %d pos2 = %d\n",p,q,
	 sg->min_free[p][q], sg->min_next1[p][q], sg->min_next2[p][q]);
#endif
  return min;
}

/* seqgraphの初期化。配列2本を渡す。 */
void seqgraph_init(struct seqgraph *sg, char *seq1, char *seq2){

  int i,j;
#if DEBUG_LEVEL >= 5
  printf("Initializing seqgraph...\n");
  printf("Length of seq1 = %d, seq2 = %d\n", strlen(seq1), strlen(seq2));
#endif
  sg->seq1 = (char *)malloc((strlen(seq1)+1)*sizeof(char));
  sg->seq1_len = strlen(seq1);
  strcpy(sg->seq1, seq1);

  sg->seq2 = (char *)malloc((strlen(seq2)+1)*sizeof(char));
  sg->seq2_len = strlen(seq2);
  strcpy(sg->seq2, seq2);

  sg->min_free = doublemx_make(sg->seq1_len, sg->seq2_len);
  sg->min_free_valid = intmx_make(sg->seq1_len, sg->seq2_len);
  sg->min_next1 = intmx_make(sg->seq1_len, sg->seq2_len);
  sg->min_next2 = intmx_make(sg->seq1_len, sg->seq2_len);

  for(i = 0;i < sg->seq1_len;i ++)
    for(j = 0;j < sg->seq2_len;j ++){
      sg->min_free_valid[i][j] = 0;
      sg->min_next1[i][j] = -1;
      sg->min_next2[i][j] = -1;
    }
#if DEBUG_LEVEL >= 5
  printf("seqgraph initialization completed.\n");
#endif

}

void seqgraph_free(struct seqgraph *sg){

  free(sg->seq1); 
  free(sg->seq2);
  doublemx_free(sg->min_free);
  intmx_free(sg->min_free_valid);
  intmx_free(sg->min_next1);
  intmx_free(sg->min_next2);

}

/* If return value is INP, invalid */
#define INP 1000.0
double free_e(char pair5[], char pair3[])
{
  const static double free_e_mat[6][6] = {
/*         3' AU    UA    CG    GC    UG    GU */
/* 5' AU */{ -0.9, -0.9, -2.1, -1.7, -0.7, -0.5 },
/*    UA */{ -1.1, -0.9, -2.3, -1.8, -0.5, -0.7 },
/*    CG */{ -1.8, -1.7, -2.9, -2.0, -1.5, -1.5 },
/*    GC */{ -2.3, -2.1, -3.4, -2.9, -1.9, -1.3 },
/*    UG */{ -0.7, -0.5, -1.3, -1.5, -0.5,  INP },
/*    GU */{ -0.5, -0.7, -1.9, -1.5,  INP, -0.5 }};
  int i;
  char *pair_point[2];
  int di_code[2];

  pair_point[0] = pair5;
  pair_point[1] = pair3;
/*
  printf("Comparing %s and %s\n", pair5, pair3);
*/
  for(i = 0;i < 2;i ++){
         if(strncmp("at", pair_point[i], 2) == 0)di_code[i] = 0; 
    else if(strncmp("ta", pair_point[i], 2) == 0)di_code[i] = 1; 
    else if(strncmp("cg", pair_point[i], 2) == 0)di_code[i] = 2; 
    else if(strncmp("gc", pair_point[i], 2) == 0)di_code[i] = 3; 
    else if(strncmp("tg", pair_point[i], 2) == 0)di_code[i] = 4; 
    else if(strncmp("gt", pair_point[i], 2) == 0)di_code[i] = 5; 
    else di_code[i] = 1000;
  }
  if(di_code[0] == 1000 || di_code[1] == 1000)return INP; 
  else return free_e_mat[ di_code[0] ][ di_code[1] ];

}

double str_penalty(int n1, int n2){

  int b1, b2;

  const static double bulge[] = {
/*  ?0    ?1    ?2    ?3    ?4    ?5    ?6    ?7    ?8    ?9  */ 
    0.0,  3.3,  5.2,  6.0,  6.7,  7.4,  8.2,  9.1, 10.0, 10.5, /*  0- 9 */
   11.0, 11.0, 11.8, 11.8, 12.5, 12.5, 13.0, 13.0, 13.6, 13.6, /* 10-19 */
   14.0, 14.0, 14.0, 14.0, 14.0, 15.0, 15.0, 15.0, 15.0, 15.0, /* 20-29 */
   15.8 };

  const static double interior[] = {
/*  ?0    ?1    ?2    ?3    ?4    ?5    ?6    ?7    ?8    ?9  */ 
    0.0,  0.8,  0.8,  1.3,  1.7,  2.1,  2.5,  2.6,  2.8,  3.1, /*  0- 9 */
    3.6,  3.6,  4.4,  4.4,  5.1,  5.1,  5.6,  5.6,  6.2,  6.2, /* 10-19 */
    6.6,  6.6,  6.6,  6.6,  6.6,  7.6,  7.6,  7.6,  7.6,  7.6, /* 20-29 */
    8.4 };
#if DEBUG_LEVEL >= 8     
  printf("%d %d\n", sizeof(bulge) / sizeof(double),
	            sizeof(interior) / sizeof(double));
#endif
  b1 = (n1 >= n2) ? n1 : n2;
  b2 = (n2 <  n1) ? n2 : n1;
  
  if(b2){
    if(b1 < sizeof(interior)/sizeof(double))return interior[b1];
    else return interior[ sizeof(interior) / sizeof(double) - 1 ];
  }
  else {
    if(b1 < sizeof(bulge) / sizeof(double))return bulge[b1];
    else return bulge[ sizeof(interior) / sizeof(double) - 1 ];
  }
}



void getres_seqgraph(struct seqgraph *sg, char result1[], char result2[],
		     int *result_len, int match_res1[], int match_res2[]){
  int i,j,k, u,v, n1,n2,lead, diff;
#if DEBUG_LEVEL >= 7
  printf("Investigation of results...\n");
  for(i = 0;i < sg->seq1_len;i ++)
    for(j = 0;j < sg->seq2_len;j ++)
      if(sg->min_free_valid[i][j])
	printf("At (%d, %d)...Min = %.2lf Next (%d %d)\n", 
	       i, j, sg->min_free[i][j],
	       sg->min_next1[i][j], sg->min_next2[i][j]);

#endif
  i = 0; j = 0; u = 0; v = 0; lead = 0;
  while(i < sg->seq1_len){
    if(sg->min_next1[i][j] >= 0){
      n1 = sg->min_next1[i][j];
      n2 = sg->min_next2[i][j];
#if DEBUG_LEVEL >= 7
      printf("Next connection is at %d in seq1\n", n1);
#endif
      for(k = i;k < n1;k ++){
	result1[u ++] = sg->seq1[k]; 
#if DEBUG_LEVEL >= 7
	putchar(sg->seq1[k]);
#endif
      } /* 次の結合直前までの塩基を格納 */
#if DEBUG_LEVEL >= 7
      putchar('\n');
#endif
      diff = n2 - n1;
      /* seq1 と seq2 の結合位置のちがいをギャップで調整 */
#if DEBUG_LEVEL >= 7
      printf("Difference is %d\n", diff);
#endif
      for(k = 0;k < diff - lead;k ++)
	result1[u ++] = GAPM;
      lead = diff;
      result1[u ++] = sg->seq1[n1];
#if DEBUG_LEVEL >= 7 
      putchar(sg->seq1[n1]);putchar('\n');
#endif
      match_res1[n1    ] = 1;
      match_res1[n1 + 1] = 1;
      i = n1 + 1; j = n2 + 1;
    }
    else {
#if DEBUG_LEVEL >= 7
      printf("No more connection in seq1(Looking seq1 from %d to %d)\n",
	     i, sg->seq1_len);
#endif
      for(;i < sg->seq1_len;i ++)
	result1[u ++] = sg->seq1[i];
      break;
    }
  }
#if DEBUG_LEVEL >= 7
  printf("Main investigation of seq1 result finished.\n");
#endif
  i = 0; j = 0; lead = 0;
  while(j < sg->seq2_len){
    if(sg->min_next2[i][j] >= 0){
      n1 = sg->min_next1[i][j];
      n2 = sg->min_next2[i][j];
#if DEBUG_LEVEL >= 7
      printf("Next connection is at %d in seq2\n", n2);
#endif
      for(k = j;k < n2;k ++){
	result2[v ++] = sg->seq2[k];
#if DEBUG_LEVEL >= 7
	putchar(sg->seq2[k]);
#endif
      } /* 次の結合直前までの塩基を格納 */
#if DEBUG_LEVEL >= 7
      putchar('\n');
#endif
      diff = n1 - n2;
      /* seq1 と seq2 の結合位置のちがいをギャップで調整 */
      for(k = 0;k < diff - lead;k ++)
	result2[v ++] = GAPM;
      lead = diff;
      result2[v ++] = sg->seq2[n2]; 
#if DEBUG_LEVEL >= 7
      putchar(sg->seq2[n2]);putchar('\n');
#endif
      match_res2[n2    ] = 1;
      match_res2[n2 + 1] = 1;
      i = n1 + 1; j = n2 + 1;
    }
    else {
#if DEBUG_LEVEL >= 7
      printf("No more connection in seq2(Looking seq2 from %d to %d)\n",
	     j, sg->seq2_len);
#endif
      for(;j < sg->seq2_len;j ++)
	result2[v ++] = sg->seq2[j];
      break;
    }
  }

  if(u < v)for(i = u;i < v;i ++)result1[i] = GAPM;
  else if(u > v)for(j = v;j < u;j ++)result2[j] = GAPM;

  *result_len = (u >= v) ? u : v;

}

double sd_match_opt(char seque1[], char seque2[], 
		char result1[], char result2[], int *result_len,
		int match_res1[], int match_res2[])
{
  static struct seqgraph sg;
  double min_fe;
  int seque1_len, seque2_len;
  int i;

  /* Initialization of match_res1,2. (Jan.25,1999) */
  seque1_len = strlen(seque1);
  seque2_len = strlen(seque2);
  for(i = 0;i < seque1_len;i ++)
    match_res1[i] = 0;
  for(i = 0;i < seque2_len;i ++)
    match_res2[i] = 0;

  seqgraph_init(&sg, seque1, seque2);
  min_fe = find_min(&sg, 0, 0);
  getres_seqgraph(&sg, result1, result2, result_len, 
		  match_res1, match_res2);
  seqgraph_free(&sg);
  return min_fe;
}

void disp_res(char result1[], char result2[], 
	      int match_res1[], int match_res2[], int result_len){
  int i,j,ct;

  ct = 0;
  for(i = 0;i < result_len;i ++)
    if(result1[i] == GAPM)putchar(' ');
  else {
    if(match_res1[ct] == 1)putchar('*');
    else putchar(' ');
    ct ++;
  }
  putchar('\n');

  for(i = 0;i < result_len;i ++)
    putchar(result1[i]);
  putchar('\n');

  for(i = 0;i < result_len;i ++)
    putchar(result2[i]);
  putchar('\n');

  ct = 0;
  for(i = 0;i < result_len;i ++)
    if(result2[i] == GAPM)putchar(' ');
  else {
    if(match_res2[ct] == 1)putchar('*');
    else putchar(' ');
    ct ++;
  }
  putchar('\n');

}

