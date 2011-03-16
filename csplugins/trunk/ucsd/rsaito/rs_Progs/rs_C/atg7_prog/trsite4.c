#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
static int upto; /* 上流何塩基まで表示するかを指定 */
static int down; /* 下流何塩基まで表示するかを指定 */
static int last; /* 遺伝子と遺伝子が何塩基以上離れているかを指定 */
/* パラメータ処理 */
int trsite4_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-trsite4") == 0){
    upto = atoi(argv[n + 1]); 
    /* ２番目のパラメータを上流で表示する塩基数とする */
    down = atoi(argv[n + 2]);
    /* ３番目のパラメータを下流で表示する塩基数とする */
    last = atoi(argv[n + 3]);
    /* ４番目のパラメータを下流で表示する塩基数とする */
    return 4; /* パラメータが４つ */
  }
  else return 0;
}
void trsite4_head(char *line){
}
/* エントリ処理 */
void trsite4_ent(struct gparam *entry_info, char seqn[], int max,
              struct cds_info cds[], int ncds){
  int i, j, begin, end, k, dobe, doen, begins, ends, js, ks, dobes, doens,s,l,space,ll,ss,spaces;
  printf("%s", entry_info->entry_line); 

  for(i = 1;i < ncds;i ++){ /* コード領域の数だけ処理を繰り返す(始) */
    if(cds[i].complement == 0 && cds[i].cds_start != 0 && cds[i-1].complement ==0 && cds[i-1].cds_end !=0){
      /* コード領域が二重鎖の反対側でなく、翻訳開始領域が明確(始) */
      s = cds[i].cds_start;
      l = cds[i-1].cds_end;
      space = s-l;
      printf("-> %d\n", space);
      if(space >= last){
	begin = cds[i].cds_start - upto; /* 上流の表示開始地点 */
	end = cds[i].cds_start + 2;      /* 開始コドンの最後の塩基 */
	printf("%d",i);
	for(j = begin;j <= end;j ++){ /* 塩基を上流から開始コドンまで表示 */
	  if(j >= 1 && j <= max)putchar(seqn[j-1]);
	  else putchar(' '); /* データがないときは、空白を表示 */
	}
	putchar('\n');
	dobe = cds[i].cds_start + 1; /* 下流の表示開始地点 */
	doen = cds[i].cds_start + down; /* 下流の表示最終地点 */
	for(k = dobe; k <= doen; k++){ /* 塩基を下流の始めから終わりまで表示 */
	  if(k >= 1 && k<= max)putchar(seqn[k+1]);
	  else putchar(' ');
	}
	putchar('\n');
      } /* コード領域が二重鎖の反対側でなく、翻訳開始領域が明確(終) */
    }
    if(cds[i].complement == 1 &&  cds[i].cds_start != 0 && cds[i-1].cds_end != 0 && cds[i-1].complement ==1){
      /* コード領域が二重鎖の反対側にある */
      ss = cds[i].cds_start;
      ll = cds[i-1].cds_end;
      spaces = ss-ll;
      printf("<- %d\n",spaces);
      if(spaces >= last){
	begins = cds[i-1].cds_end + upto; /*相補鎖でいう上流翻訳開始地点*/
	ends = cds[i-1].cds_end - 2;      /* 相補鎖でいう開始コドンの最後の塩基 */
	printf("%d",i-1);
	for(js = begins;js >= ends;js --){ /* 塩基を上流から開始コドンまで表示 */
	  if(js >= 1 && js <= max){
	    if(seqn[js-1]=='a')	putchar('t');
	    if(seqn[js-1]=='t')	putchar('a');
	    if(seqn[js-1]=='c')	putchar('g');
	    if(seqn[js-1]=='g')	putchar('c');
	  }
	  else putchar(' '); /* データがないときは、空白を表示 */
	}
	putchar('\n');
	dobes = cds[i-1].cds_end -3 ; /* 相補鎖でいう下流の表示開始地点 */
	doens = cds[i-1].cds_end - down -2; /* 相補鎖でいう下流の表示最終地点 */
	for(ks = dobes; ks >= doens; ks--){/*塩基を下流の始めから終わりまで表示*/
	  if(ks >= 1 && ks<= max){
	    if(seqn[ks-1]=='a')	putchar('t');
	    if(seqn[ks-1]=='t')	putchar('a');
	    if(seqn[ks-1]=='c')	putchar('g');
	    if(seqn[ks-1]=='g')	putchar('c');	
	  }
	  else putchar(' ');
	}
	putchar('\n');
      }
    }
  } /* コード領域の数だけ処理を繰り返す(終) */
}
/* 最終処理 */
void trsite4_fin(){
  printf("Finished!!\n");
}
/* ヘルプ */
void trsite4_help(){
  printf("-trsite4\t Displays sequences around translation initiation sites\n");
}




