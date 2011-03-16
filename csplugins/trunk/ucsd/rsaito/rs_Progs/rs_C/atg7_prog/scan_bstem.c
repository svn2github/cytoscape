#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "global_st.h"
#include "atg_func.h"

#define DEBUG_LEVEL 3

static int pos1, pos2;
static int range;
static int broken;
static int counter[4];
static int flag;
static int broken_seq1[4][20];
static int broken_seq2[4][20];
static int length_of_stem[4][20];

int scan_bstem_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-scan_bstem") == 0){
    pos1 = atoi(argv[n + 1]);
    pos2 = atoi(argv[n + 2]);
    range = atoi(argv[n + 3]);
    broken = atoi(argv[n + 4]);
    return 5;
  }
  else return 0;
}

void scan_bstem_head(char *line){

}


extern int comp_match(char c1, char c2);


void reverse(char *seq){ /*配列を逆順にする*/
  int i;
  int length;

  static char cseq[1000];

  length=strlen(seq);
  strncpy(cseq, seq, length);

  for(i=0;i<length;i++){
     seq[i]=cseq[length-i-1];
  }
}


/* seq1,seq2の配列をp_seq1, p_seq2にコピーし、初めのp1_i, p1_j文字を
   切り取って反転させる。上流配列の解析の準備として有効 */
void cut_p1(char *seq1,char *seq2,char *p_seq1,char *p_seq2,int p1_i,int p1_j)
{
  strncpy(p_seq1, seq1, range*2+1);
  strncpy(p_seq2, seq2, range*2+1);

  p_seq1[p1_i]='\0';
  p_seq2[p1_j]='\0';
  
  reverse(p_seq1);
  reverse(p_seq2);

  p_seq1[p1_i]='\0';
  p_seq2[p1_j]='\0';
}

/* seq1, seq2の配列のp2_i, p2_j以降をp_seq1, p_seq2にコピーする。
   下流配列の解析に有効 */
void cut_p2(char *seq1,char *seq2,char *p_seq1,char *p_seq2,int p2_i,int p2_j)
{
  strncpy(p_seq1, &seq1[p2_i], range*2+1-p2_i);
  p_seq1[range*2+1-p2_i]='\0';
  strncpy(p_seq2, &seq2[p2_j], range*2+1-p2_j);
  p_seq2[range*2+1-p2_j]='\0';
}

int search_next_stem(char *p_seq1,char *p_seq2,int flag)
/* flag は切断部分の位置と長さを記録する場所を示す。0なら記録しない。 */
{
  int i,j,k;
  
  int max_n=0;
  int p,p1,p2;
  int seq1_len,seq2_len;
  k=0;

  seq1_len=strlen(p_seq1);
  seq2_len=strlen(p_seq2);

  /* brokenの範囲内で最も対合する箇所を特定する。*/ 
  max_n = 0;
  for(i=0;i<=broken;i++){
    for(j=0;j<=broken;j++){
      k = 0;
      while(comp_match(p_seq1[i+k],p_seq2[j+k])==1 && 
	    i+k<seq1_len && j+k<seq2_len) {
	k++;
      } /* i,jからの連続対合数をカウント */
      if(k > max_n){	
	max_n = k;
	p1 = i;
	p2 = j;
	if(flag > 0){ /* flagが0でなければ位置(相対)とステムの長さを記録 */
	  broken_seq1[flag - 1][counter[flag - 1]] = i;
	  broken_seq2[flag - 1][counter[flag - 1]] = j;
	  length_of_stem[flag - 1][counter[flag - 1]] = k;
	} /* flagは記録の種類。親関数で指定。 */
      }
    }
  }
  if(max_n >= 4){ /* 4塩基以上対号したら、まだ伸ばしていく。 */
    p_seq1 = &p_seq1[max_n+p1];
    p_seq2 = &p_seq2[max_n+p2];
    if(flag > 0)counter[flag-1]++; /* ステム数カウントアップ */
    max_n=search_next_stem(p_seq1,p_seq2,flag) + max_n;
  }
  else max_n = 0;
  return max_n;
}

/* 上流側の対合結果を表示？ startは親関数で持っている対合開始位置。
   b1, b2はbroken情報。b1がseqに対応する。stem lengthはグローバルを使用する。
   nは配列の種類を指定するフラッグ。 */
void print_seq_up(char *seq,int start,int b1[4][20],int b2[4][20],int n){
  int i,j;

/* 5'末端まで戻る */
  for(i=0;i<counter[n];i++){
    start=start-b1[n][i]-length_of_stem[n][i];
  }

  for(i = counter[n] - 1;i >= 0; i--){

    /* 対合部分は大文字で表示 */
    for(j=start;j<start+length_of_stem[n][i];j++){
      putchar(toupper(seq[j]));
    }

    /* 対合していない部分の表示 */
    for(j=start+length_of_stem[n][i];
	j<start+length_of_stem[n][i]+b1[n][i];j++){
	putchar(seq[j]);
      }
    
    /* もう一本の配列と比べて配列が短い場合はギャップで埋める。 */
    if(b1[n][i]<b2[n][i]){
      for(j=0;j<b2[n][i]-b1[n][i];j++)putchar('-');
    }

    /* 次の対合位置へ */
    start=start+length_of_stem[n][i]+b1[n][i];

  }
}

/* 下流側の対合結果を表示？ startは親関数で持っている対合開始位置。
   b1, b2はbroken情報。b1がseqに対応する。stem lengthはグローバルを使用する。
   nは配列の種類を指定するフラッグ。 */
void print_seq_down(char *seq,int start,int b1[4][20],int b2[4][20],int n){
  int i,j;

  for(i=0;i<counter[n];i++){
    /* 非対合部分を表示 */
    for(j = start;j < start + b1[n][i];j ++)putchar(seq[j]);

    /* もう一本の配列より短い場合はギャップを入れる */
    if(b1[n][i]<b2[n][i]){
      for(j = 0;j < b2[n][i] - b1[n][i];j ++)putchar('-');
    }

    /* 対合部分は大文字で表示 */
    for(j=start+b1[n][i];j<start+length_of_stem[n][i]+b1[n][i];j++){
      putchar(toupper(seq[j]));
    }

    /* 次の(非)対合部分へ */
    start=start+length_of_stem[n][i]+b1[n][i];

  }
}


void scan_bstem_ent(struct gparam *entry_info, char seqn[], int max,
		 struct cds_info cds[], int ncds){
  int i,j,k;

  char *compseqn;
  static char seq1[1000],seq2[1000];
  static char cseq2[1000];
  static char p1_seq1[1000],p1_seq2[1000];
  static char p2_seq1[1000],p2_seq2[1000];

  int max_n=0;
  int base;
  int max_n_i=0;
  int max_n_j=0;
  int total_n=0;
  int max_base=0;
  int max_base_i=0;
  int max_base_j=0;
  int pos_i[2];
  int pos_j[2];
  int p1_i,p1_j,p2_i,p2_j;
  int complement_flag;
  int a1, a2;

  for(i=0;i<4;i++)counter[i]=0;
  
  compseqn = compseqget(seqn, max);
  
  if(pos1 > pos2){ /* In case of complement */
    complement_flag = 1;
    pos1 = max - pos1 + 1; /* 一番最初の添字が1だという仮定 */
    pos2 = max - pos2 + 1; /* で位置をcomplementに補正 */
    strncpy(seq1, &compseqn[pos1-1-range], range*2+1);
    seq1[ range*2 + 1 ] = '\0';
    strncpy(seq2, &compseqn[pos2-1-range], range*2+1);
    seq2[ range*2 + 1 ] = '\0';
  }
  else{
    complement_flag = 0;
    strncpy(seq1, &seqn[pos1-1-range], range*2+1);
    seq1[ range*2 + 1 ] = '\0';
    strncpy(seq2, &seqn[pos2-1-range], range*2+1);
    seq2[ range*2 +1]='\0';
  }

  strncpy(cseq2, seq2, range*2+1);
  reverse(cseq2);
  /* これでseq1とcseq2に解析対象の配列が入る */
  
  flag=0;
  k=0;

  /* rangeの範囲内の塩基の対合を見ていく */
  for(i=0;i<range*2+1;i++){
    for(j=0;j<range*2+1;j++){
      k = 0;
      while(comp_match(seq1[i+k],cseq2[j+k]) == 1 
	    && i + k < range*2 && j + k < range*2)
	k++;
      /* seq1[i],seq2[j]からの連続対合を数える */

      if(k>max_base){   /* 連続最大対合数と位置を記録 */
	max_base=k;
	max_base_i=i;
	max_base_j=j;
      }

      if(k>=4){ /* 4塩基以上対合していればそこから塩基の対合を探す */
	p1_i=i;   /* 5'側対合末端...でいいのか？ */
	p1_j=j;   /* cut_p1, cut_p2の機能のちがいより問題なしと思われる */
	
	p2_i=i+k; /* 3'側対合末端の1塩基外 */
	p2_j=j+k;
	
       /* seq1, cseq2の最初のp1_i, p1_j文字を切り取って反転させて
          p1_seq1,p1_seq2に入れる */
	cut_p1(seq1,cseq2,p1_seq1,p1_seq2,p1_i,p1_j);
	total_n = search_next_stem(p1_seq1,p1_seq2,flag) + k;

	/* seq1, cseq2のp2_i,p2_j以降の配列をp2_seq1,p2_seq2に入れる */
	cut_p2(seq1,cseq2,p2_seq1,p2_seq2,p2_i,p2_j);
	total_n = search_next_stem(p2_seq1,p2_seq2,flag) + total_n;

	if(total_n > max_n){ /* 合計対合数の最大値の記録 */
	  max_n=total_n;
	  max_n_i=i;
	  max_n_j=j;
	  base=k; /* 中心ステムの対合数 */
	}
      }
    }
  }


/* 対合箇所の特定:flagを立てて記録していく */

/* 合計対合数最大のところの上流の対合を調べる flag = 1 */
  cut_p1(seq1,cseq2,p1_seq1,p1_seq2,max_n_i,max_n_j);
  total_n = search_next_stem(p1_seq1,p1_seq2,1);

/* 合計対合数最大のところの下流の対合を調べる flag = 2 */
  cut_p2(seq1,cseq2,p2_seq1,p2_seq2,max_n_i+base,max_n_j+base);
  total_n = search_next_stem(p2_seq1,p2_seq2,2);

/* 最大連続対合のところの上流の対合を調べる flag = 3 */
  cut_p1(seq1,cseq2,p1_seq1,p1_seq2,max_base_i,max_base_j);
  total_n = search_next_stem(p1_seq1,p1_seq2,3);

/* 最大連続対合のところの下流の対合を調べる flag = 4 */
  cut_p2(seq1,cseq2,p2_seq1,p2_seq2,max_base_i+max_base,max_base_j+max_base);
  total_n = search_next_stem(p2_seq1,p2_seq2,4);


/* 合計対合数最大の配列の結果表示 */
/*
  printf("***** **************************** *****\n");
  printf("***** Result of total-max matching *****\n");
  printf("***** **************************** *****\n");
*/
/* 上流側対合表示 */
/*
  printf("Sequence:\n");
  print_seq_up(seq1,max_n_i,broken_seq1,broken_seq2,0);
*/
/* 対合の中心ステムを表示 */
/*
  for(i=max_n_i;i<max_n_i+base;i++){
    putchar(toupper(seq1[i]));
  }
*/
/* 下流側対合表示 */
/*
  print_seq_down(seq1,max_n_i+base,broken_seq1,broken_seq2,1);
  printf("\n");
*/
/* (相補側の配列も同様) */
/*
  print_seq_up(cseq2,max_n_j,broken_seq2,broken_seq1,0);
  for(i=max_n_j;i<max_n_j+base;i++){
    putchar(toupper(cseq2[i]));
  }
  print_seq_down(cseq2,max_n_j+base,broken_seq2,broken_seq1,1);
  printf("\n\n");

  printf("Total matching...%d\n\n", max_n);
*/
#if DEBUG_LEVEL >= 4
  printf("Main stem position:\n");
  a1 = pos1 - range + max_n_i;
  a2 = pos2 + range - max_n_j;
  if(complement_flag){
    a1 = max - a1 + 1;
    a2 = max - a2 + 1;
  }
  printf("   Absolute: (%d %d)\n", a1, a2);
  printf("   From range terminal: (%d %d)\n", max_n_i, max_n_j);
#endif
#if DEBUG_LEVEL >= 5
  printf("\nBroken information upstream:\n");
  for(i = counter[0] - 1;i >= 0;i --){
    printf("seq1(%d): Stem:%d Broken:%d   ",
	   i, length_of_stem[0][i], broken_seq1[0][i]);
    printf("seq2(%d): Stem:%d Broken:%d\n",
	   i, length_of_stem[0][i], broken_seq2[0][i]);
  }
  printf("\nBroken information downstream:\n");
  for(i = 0;i < counter[1];i ++){
    printf("seq1(%d): Broken:%d Stem:%d   ",
	   i,broken_seq1[1][i], length_of_stem[1][i]);
    printf("seq2(%d): Broken:%d Stem:%d\n",
	   i,broken_seq2[1][i], length_of_stem[1][i]);
  }
#endif
  pos_i[0]=max_n_i;
  pos_j[0]=max_n_j;
  for(i=0;i<counter[0];i++){
    pos_i[0] = pos_i[0] - broken_seq1[0][i] - length_of_stem[0][i];
    pos_j[0] = pos_j[0] - broken_seq2[0][i] - length_of_stem[0][i];
  }

/* pos1,pos2からの相対位置の計算 */
  pos_i[0] =  pos_i[0] - range;
  pos_j[0] = -pos_j[0] + range;
/*
  printf("Stem position:\n");
  a1 = pos1 + pos_i[0];
  a2 = pos2 + pos_j[0];
  if(complement_flag){
    a1 = max - a1 + 1;
    a2 = max - a2 + 1;
  }
  
  printf("   Absolute: (%d %d)\n", a1, a2);
  printf("   From user indicated: (%d %d)\n", pos_i[0], pos_j[0]);

  putchar('\n');
*/

/* 最大連続対合の結果表示 */
/*
  printf("** ************************* ***********\n");
  printf("** Result of max consecutive matching ** \n");
  printf("** ************************* ***********\n");
  printf("Sequence:\n");
*/

  a1 = pos1 - range + max_base_i;
  a2 = pos2 + range - max_base_j;
  if(complement_flag){
    a1 = max - a1 + 1;
    a2 = max - a2 + 1;
  }


  printf("&\n");
/*  printf("Main stem position:\n"); */
/*  printf("   Absolute: (%d %d)\n",a1, a2); */
/*  printf("   From range terminal: (%d %d)\n", max_base_i, max_base_j); */
  printf("%d & %d & %d &\n", 
	 max_base_i - range,
	 range - max_base_j,
	 max_base);

/* 上流側対合表示 */
  print_seq_up(seq1,max_base_i,broken_seq1,broken_seq2,2);

/* 対合の中心ステムを表示 */
  for(i=max_base_i;i<max_base_i+max_base;i++){
    putchar(toupper(seq1[i]));
  }
/* 下流側対合表示 */
  print_seq_down(seq1,max_base_i+max_base,broken_seq1,broken_seq2,3);
  printf("\\\\\n& & & &\n");



/* (相補側の配列も同様) */
  print_seq_up(cseq2,max_base_j,broken_seq2,broken_seq1,2);
  for(i=max_base_j;i<max_base_j+max_base;i++){
    putchar(toupper(cseq2[i]));
  }
  print_seq_down(cseq2,max_base_j+max_base,broken_seq2,broken_seq1,3);
  printf("\\\\\n\n");
/*
  printf("Max consecutive matching...%d\n\n", max_base);
*/
#if DEBUG_LEVEL >= 5
  printf("\nBroken information upstream:\n");
  for(i = counter[2] - 1;i >= 0;i --){
    printf("seq1(%d): Stem:%d Broken:%d   ",
	   i, length_of_stem[2][i], broken_seq1[2][i]);
    printf("seq2(%d): Stem:%d Broken:%d\n",
	   i, length_of_stem[2][i], broken_seq2[2][i]);
  }
  printf("\nBroken information downstream:\n");
  for(i = 0;i < counter[3];i ++){
    printf("seq1(%d): Broken:%d Stem:%d   ",
	   i,broken_seq1[3][i], length_of_stem[3][i]);
    printf("seq2(%d): Broken:%d Stem:%d\n",
	   i,broken_seq2[3][i], length_of_stem[3][i]);
  }
#endif
  pos_i[1]=max_base_i;
  pos_j[1]=max_base_j;
  for(i=0;i<counter[2];i++){
    pos_i[1]=pos_i[1]-broken_seq1[2][i]-length_of_stem[2][i];
    pos_j[1]=pos_j[1]-broken_seq2[2][i]-length_of_stem[2][i];
  }

/* pos1,pos2からの相対位置の計算 */
  pos_i[1] =  pos_i[1] - range;
  pos_j[1] = -pos_j[1] + range;

  a1 = pos1 + pos_i[1];
  a2 = pos2 + pos_j[1];
  if(complement_flag){
    a1 = max - a1 + 1;
    a2 = max - a2 + 1;
  }
/*
  printf("Stem position:\n");
  printf("   Absolute: (%d %d)\n", a1, a2);
  printf("   From user indicated: (%d %d)\n", pos_i[1], pos_j[1]);

  putchar('\n');
*/
  free(compseqn);
}

void scan_bstem_fin(){

}

void scan_bstem_help(){

  printf("-scan_bstem Searches broken for stem. State 4 numbers: Position 1, 2, range, number of broken bases\n");

}





