#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static int ijou,inai; /* 遺伝子と遺伝子が何塩基以上離れているかを指定 */
/* パラメータ処理 */
int aonlyes_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-aonlyes") == 0){
    ijou = atoi(argv[n + 1]);
    inai = atoi(argv[n + 2]);
    return 3; /* パラメータが３つ */
  }
  else return 0;
}
void aonlyes_head(char *line){
}
/* エントリ処理 */
void aonlyes_ent(struct gparam *entry_info, char seqn[], int max,
              struct cds_info cds[], int ncds){
  int i,j,dist;
  valid_cds[0]=0;
  for(i = 1;i < ncds;i ++){ /* コード領域の数だけ処理を繰り返す(始) */
    
    if(cds[i].complement == 0 && cds[i-1].complement ==0){
      dist = cds[i].cds_start - cds[i-1].cds_end;
      if(dist >= ijou && dist <= inai)valid_cds[i] = 0;
    }
    
    else if(cds[i].complement == 1 && cds[i-1].complement ==1){
      /* コード領域が二重鎖の反対側にある */
      dist = cds[i].cds_start - cds[i-1].cds_end;
      if(dist >= ijou && dist <= inai)valid_cds[i-1] = 0;
    }
  } /* コード領域の数だけ処理を繰り返す(終) */
}

  /* 最終処理 */
void aonlyes_fin(){

}
/* ヘルプ */
void aonlyes_help(){
  printf("-aonlyes\t eliminates -ijou -inai douitu houkou \n");
}




