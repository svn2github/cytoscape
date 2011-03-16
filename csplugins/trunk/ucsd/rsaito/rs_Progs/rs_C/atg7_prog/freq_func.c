
/* ＡＴＧＣ含量の頻度を％表示する */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#include "global_st.h"
#include "atg_func.h"

#define A 0
#define C 1
#define G 2
#define T 3

static char type[5] = {"ACGT"};


static int ctr[4];  /* タンパク質ごとのＯ値 */
static int E[4];    /* 生物ごとのＥ値 */


int freq_par(int argc, char *argv[], int n)
{
  int i;

  if(strcmp(argv[n],"-freq") == 0){
    


    /* 大域変数の初期化 */
    for( i = 0; i < 4; i++ ){
      ctr[i] = 0;
      E[i] = 0;
    }

    return 1;    /* 使うパラメータの数 */
  }

  else return 0;
}
  
void freq_head(char *line){


   int i, fl = 0;

   if( strncmp("BASE COUNT", line, 10) == 0 ){
/*     printf("%s",line); */
     for( i = 10; line[i] != '\0' && fl < 4; i++ ){
      if( isdigit(line[i]) != 0 ){         /* 数値だったら */
/*	printf("test:%s\n", line[i]); */
	E[fl] = atoi(&line[i]);
	fl++;
	for( ; isalpha(line[i]) == 0 && line[i] != '\0'; i++ ){
	  /* alphabet がくるまで i を incliment する*/ 
	}
      }
    }
/*     printf("E[A]: %d  E[C]: %d  E[G]: %d  E[T]: %d\n", 
	    E[A],  E[C],  E[G],  E[T]); */
   }
}

void freq_ent(struct gparam *entry_info, char seqn[], int max,
	      struct cds_info cds[], int ncds)
{

/* 
   cds[].cds_start  翻訳開始領域(complementのときは翻訳終了)
   cds[].cds_end    翻訳終了領域(complementのときは翻訳開始)
   cds[].complement 0 = 翻訳が通常の向き 1 = 翻訳が逆向き
   entry            LOCUS名
*/



  int i, j, cdslen, num=0;
  double chi,e_a,e_t,e_c,e_g;

/*  printf("this is test again\n"); */


  for(i = 0; i < ncds; i++){

    /* 初期化 */
    for( j = 0; j < 4; j++ ){
      ctr[j] = 0;
    }

    cdslen = cds[i].cds_end - cds[i].cds_start + 1;  /* 遺伝子の塩基数 */
    printf("[%s]:%d",cds[i].product,num ++);    

    /* normal */
    if(cds[i].complement == 0 && 
       cds[i].cds_start != 0 && cds[i].cds_end != 0){   

      for( j = 0; j < cdslen; j++ ){
	if( seqn[ cds[i].cds_start + j ] == 'a' ) ctr[A]++;
	if( seqn[ cds[i].cds_start + j ] == 't' ) ctr[T]++;
	if( seqn[ cds[i].cds_start + j ] == 'c' ) ctr[C]++;      
	if( seqn[ cds[i].cds_start + j ] == 'g' ) ctr[G]++;      
      }/* for */

    }/* if normal */
    
    /* complement */
    else if(cds[i].complement == 1 && 
	    cds[i].cds_start != 0 && cds[i].cds_end != 0){  

      for( j = 0; j < cdslen; j++ ){
	if( seqn[ cds[i].cds_start + j ] == 'a' ) ctr[T]++;
	if( seqn[ cds[i].cds_start + j ] == 't' ) ctr[A]++;
	if( seqn[ cds[i].cds_start + j ] == 'c' ) ctr[G]++;      
	if( seqn[ cds[i].cds_start + j ] == 'g' ) ctr[C]++;      
      }/* for */

    }/* else if complement */

    else { /* printf("???\n"); */ cdslen = 0;}

/*    printf("\t A:%lf%% T:%lf%% C:%lf%% G:%lf%%  [%s] \n",
	   1.0 * ctr_a/cdslen * 100, 1.0 * ctr_t/cdslen * 100,
	   1.0 * ctr_c/cdslen*100, 1.0 * ctr_g/cdslen * 100, 
	   entry_info->source );
*/

    e_a = 1.0 * cdslen * E[A] / (E[A] + E[T] + E[C] + E[G]);
    e_t = 1.0 * cdslen * E[T] / (E[A] + E[T] + E[C] + E[G]);
    e_c = 1.0 * cdslen * E[C] / (E[A] + E[T] + E[C] + E[G]);
    e_g = 1.0 * cdslen * E[G] / (E[A] + E[T] + E[C] + E[G]);

    chi = 1.0 * (ctr[A] - e_a)*(ctr[A] - e_a) / e_a +
          1.0 * (ctr[T] - e_t)*(ctr[T] - e_t) / e_t +
          1.0 * (ctr[C] - e_c)*(ctr[C] - e_c) / e_c +
          1.0 * (ctr[G] - e_g)*(ctr[G] - e_g) / e_g;

    printf("@ A:%.2lf%% @ T:%.2lf%% @ C:%.2lf%% @ G:%.2lf%% @ %lf @ [%s] \n",
	   1.0 * ctr[A]/cdslen * 100, 1.0 * ctr[T]/cdslen * 100,
	   1.0 * ctr[C]/cdslen * 100, 1.0 * ctr[G]/cdslen * 100, 
	   chi_prob(4-1,chi), entry_info->source );
/*
    printf("AO:%d / AE:%.2lf ",ctr[A], e_a);
    printf("TO:%d / TE:%.2lf ",ctr[T], e_t);
    printf("CO:%d / CE:%.2lf ",ctr[C], e_c);
    printf("GO:%d / GE:%.2lf ",ctr[G], e_g);
    putchar('\n');
*/
  }/*   for(i = 0; i < ncds; i++)   */

}

/* 結果の表示 */
void freq_fin()
{
  int i;
/*
  for( i = 0; i < 4; i++ ){
    printf("E[%c] %d\n", type[i], E[i]);
  }

  printf("Analyses of base contents of gene finished.\n");
*/
}

void freq_help()
{

  printf("-freq Analyses of base contents of genes\n");

}
