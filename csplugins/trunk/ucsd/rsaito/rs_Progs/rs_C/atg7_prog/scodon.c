#include <stdio.h>
#include "global_st.h"

/* パラメータの検査をする関数 */
int scodon_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-scodon") == 0){ /* パラメータは "-scodon" */
    return 1; /* パラメータの数 = 1 */
  }
  else return 0; /* "-scodon"ではなかった */
}


void scodon_head(char *line){

}

/* 各エントリに対する処理 */
void scodon_ent(struct gparam *entry_info, char seqn[], int max,
              struct cds_info cds[], int ncds){
  int i;
  
  printf("%s",entry_info->entry_line); /* エントリのLOCUSの行を表示 */

  for(i = 0;i < ncds;i ++){ /* エントリの中に含まれるCDSの数だけ実行 */

    if(cds[i].complement == 0 && cds[i].cds_start != 0){
      /* コード領域がDNA二重鎖の反対側でなく、翻訳開始領域が明確 */

      putchar(seqn[ cds[i].cds_start     - 1 ]); /* 開始コドンの１文字目 */
      putchar(seqn[ cds[i].cds_start + 1 - 1 ]); /* 開始コドンの２文字目 */
      putchar(seqn[ cds[i].cds_start + 2 - 1 ]); /* 開始コドンの３文字目 */
      putchar('\n'); /* 改行 */
    }
  }
}

/* 最終処理 */
void scodon_fin(){

  printf("Finished!!\n");

}

/* ヘルプ */
void scodon_help(){

  printf("-scodon\t Displays start codon\n");

}
