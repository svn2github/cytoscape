#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"
static int last; /* 遺伝子と遺伝子が何塩基以上離れているかを指定 */
/* パラメータ処理 */
int trsite6_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-trsite6") == 0){
    last = atoi(argv[n + 1]);
    return 2; 
  }/* コード領域の数だけ処理を繰り返す(終) */
  else return 0;
}
void trsite6_head(char *line){
}
/* エントリ処理 */
void trsite6_ent(struct gparam *entry_info, char seqn[], int max,
              struct cds_info cds[], int ncds){
  int i,s,l,space,ll,ss,spaces;
  printf("%s", entry_info->entry_line); 
  for(i = 1;i < ncds;i ++){ /* コード領域の数だけ処理を繰り返す(始) */
    if(cds[i].complement == 0 && cds[i].cds_start != 0 && cds[i-1].complement !=0 && cds[i-1].cds_end !=0){
/*コード領域の一方が右、もう一方が左を向いている領域ならば*/
      s = cds[i].cds_start;
      l = cds[i-1].cds_end;
      space = s-l;
      if(space <= last){
	valid_cds[i] = 0;
	valid_cds[i-1] = 0;
      } 
    }
    if(cds[i].complement == 0 &&  cds[i].cds_start != 0 && cds[i-1].cds_end != 0 && cds[i-1].complement ==0){
/* コード領域が右向きならば */
      valid_cds[i] = 0;
    }
    if(cds[i].complement == 1 &&  cds[i].cds_start != 0 && cds[i-1].cds_end != 0 && cds[i-1].complement ==1){
/* コード領域が左向きならば */
      valid_cds[i-1] = 0;
    }
  }
}
/* 最終処理 */
void trsite6_fin(){
  printf("Finished!!\n");
}
/* ヘルプ */
void trsite6_help(){
  printf("-trsite6\t Accepts sequences only if two CDSs are ");
  printf("opposite oriented and separated by specified bases or ");
  printf("farther\n");
}




