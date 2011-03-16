/* 翻訳開始領域の上流ｎ塩基を表示 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

int lenoseq;

int testt_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n],"-testt") == 0){
    lenoseq = atoi(argv[n + 1]);
    return 2;    /* 使うパラメータの数 */
  }
  else return 0;
}
  

void testt_head(char *line){

}

void testt_ent(char *entry, char seqn[], int max, struct cds_info cds[], int ncds)
{

/* 
   cds[].cds_start  翻訳開始領域(complementのときは翻訳終了)
   cds[].cds_end    翻訳終了領域(complementのときは翻訳開始)
   cds[].complement 0 = 翻訳が通常の向き 1 = 翻訳が逆向き
   entry            LOCUS名
*/

  int i, j, begin, minus;
  int num = 0;

  if(ncds == 0 || cds[0].cds_start == 1)return;
  printf("%s",entry);
  printf("Number of cds:%d\n",ncds);
  for(i = 0; i < ncds; i++){

    if(cds[i].complement == 0 && cds[i].cds_start != 0){   /* normal */
      begin = cds[i].cds_start - lenoseq -1;

      if( begin < 0 )   begin = 0;
      else{ }
      

      for( j = begin; j < cds[i].cds_start + 2 + 10; j++ ){
	printf("%c",seqn[j]);
	if(j == cds[i].cds_start - 2 || j == cds[i].cds_start + 1)
	  putchar(' ');
      }
      printf("%4d:[%s:%s]",num ++, cds[i].product,cds[i].gene);
      printf("\n");
      
    }
    
    else if(cds[i].complement == 1 && cds[i].cds_end != 0){  /* complement */
      begin = cds[i].cds_end + lenoseq -1;

      if( begin > max )  begin = max;
      else{ }


      for( j = begin; j > cds[i].cds_end -4 - 10; j-- ){
	printf("%c", cmpl(seqn[j]));
	if(j == cds[i].cds_end || j == cds[i].cds_end - 3)
	  putchar(' ');
      }
      printf("%4d:[%s:%s]",num ++, cds[i].product,cds[i].gene);
      printf("\n");
    }

    else printf("???\n");

  }/*   for(i = 0; i < ncds; i++)   */

}

/* 結果の表示 */
void testt_fin()
{
  
  printf("Finished.\n");

}

void testt_help()
{

  printf("-testt\t Displays nucleotides around start codon(state 1 number)\n");

}

