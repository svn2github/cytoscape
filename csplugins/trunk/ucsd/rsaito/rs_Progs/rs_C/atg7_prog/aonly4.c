#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static int ijou,inai; /* 遺伝子と遺伝子が何塩基以上離れているかを指定 */
/* パラメータ処理 */
int aonly4_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-aonly4") == 0){
    ijou = atoi(argv[n + 1]);
    inai = atoi(argv[n + 2]);
    return 3; /* パラメータが３つ */
  }
  else return 0;
}
void aonly4_head(char *line){
}
/* エントリ処理 */
void aonly4_ent(struct gparam *entry_info, char seqn[], int max,
              struct cds_info cds[], int ncds){
  int i,j,dist;


  for(i = 0;i < ncds;i ++){ /* コード領域の数だけ処理を繰り返す(始) */
    
    if(cds[i].complement == 0 && i > 0){
      dist = cds[i].cds_start - cds[i-1].cds_end;
      if(dist < ijou || dist > inai)valid_cds[i] = 0;
    }
    
    else if(cds[i].complement == 1 && i < ncds - 1){
      /* コード領域が二重鎖の反対側にある */
      dist = cds[i+1].cds_start - cds[i].cds_end;
      if(dist < ijou || dist > inai)valid_cds[i] = 0;
    }
  } /* コード領域の数だけ処理を繰り返す(終) */

}

  /* 最終処理 */
void aonly4_fin(){

}
/* ヘルプ */
void aonly4_help(){
  printf("-aonly4\t Accepts only sequences whose start codons are separated by specified bases from the previous gene(State min and max)\n");
}





