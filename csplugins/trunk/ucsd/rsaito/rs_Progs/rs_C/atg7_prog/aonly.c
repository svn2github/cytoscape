#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"
static int last; /* 遺伝子と遺伝子が何塩基以上離れているかを指定 */
/* パラメータ処理 */
int aonly_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-aonly") == 0){
    last = atoi(argv[n + 1]);
    return 2; /* パラメータが２つ */
  }
  else return 0;
}
void aonly_head(char *line){
}
/* エントリ処理 */
void aonly_ent(struct gparam *entry_info, char seqn[], int max,
              struct cds_info cds[], int ncds){
  int i,s,l,space,ss,ll,spaces;  
  valid_cds[0]=0;
  for(i = 1;i < ncds;i ++){ /* コード領域の数だけ処理を繰り返す(始) */
    if(cds[i].complement == 0 && cds[i].cds_start != 0 && cds[i-1].complement ==0 && cds[i-1].cds_end !=0){
      /* コード領域が二重鎖の反対側でなく、翻訳開始領域が明確(始) */
      s = cds[i].cds_start;
      l = cds[i-1].cds_end;
      space = s-l;
      if(space < last) valid_cds[i] = 0;
    }/* コード領域が二重鎖の反対側でなく、翻訳開始領域が明確(終) */
    if(cds[i].complement == 1 &&  cds[i].cds_start != 0 && cds[i-1].cds_end != 0 && cds[i-1].complement ==1){
      /* コード領域が二重鎖の反対側にある */
      ss = cds[i].cds_start;
      ll = cds[i-1].cds_end;
      spaces = ss-ll;
      if(spaces < last)valid_cds[i-1] = 0;
    }
    if(cds[i].complement==0 && cds[i-1].complement==1){
      valid_cds[i]=0;
      valid_cds[i-1]=0;
    }
  }
} /* コード領域の数だけ処理を繰り返す(終) */
/* 最終処理 */
void aonly_fin(){
  printf("Finished!!\n");
}
/* ヘルプ */
void aonly_help(){
  printf("-aonly\t Only accepts if two same oriented codons are separated by ");
  printf("specified bases or farther\n");
}




