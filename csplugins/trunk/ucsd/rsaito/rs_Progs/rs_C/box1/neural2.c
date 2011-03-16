/*                                                        */
/* ****************************************************** */
/* ニューラルネットワークを使って４色問題を解くプログラム */
/* ****************************************************** */
/*                                                        */
/* １９９６年 武藤研 新人研修会                           */
/*                                                        */
/* このプログラムは gcc を使ってコンパイルすること        */

#define VERSION "March 29, 1996"

#include <stdio.h>


void main(){

#define N_CTRY 7 /* 国の数 0 から (N_CTRY - 1) まで */

/* 各ニューロンに関する情報 */

  int  U[N_CTRY][4]; /* 発火するか否かを決定する情報 */
  int dU[N_CTRY][4]; /* 動作式から得られる情報  Uに足される */
  int  V[N_CTRY][4]; /* 0 = 発火していない   1 = 発火している */


/* 国が接しているか否かに関する情報 */

  int D[N_CTRY][N_CTRY] = {
    { 0, 1, 1, 0, 1, 0, 0 },
    { 1, 0, 1, 1, 0, 0, 0 },
    { 1, 1, 0, 1, 1, 0, 0 },
    { 0, 1, 1, 0, 1, 1, 0 },
    { 1, 0, 1, 1, 0, 1, 1 },
    { 0, 0, 0, 1, 1, 0, 1 },
    { 0, 0, 0, 0, 1, 1, 0 }
  }; /* 0 = 接していない  1 = 接している */
     /* 同じ国同士は接していないものとみなす */

  int seed; /* 乱数シードの No. */

  int x,y,m,n,i,j,counter,fin_flag,dummy; /* 他目的変数 */

/* 乱数シードの設定 */

  printf("Input random number seed:");
  scanf("%d", &seed);
  for( n = 0 ; n < seed ; n ++)dummy = rand();


/* Uの値の初期化 */

  initialize(U); /* Uの値をランダムに設定する */
  calc_V(U,V);   /* Uの値よりVの情報(発火するか否か)を決定 */
  display_N(U);
  display_N(V);

/* メインループ */
  counter = 0;
  while(1){
    counter ++;
    printf("trial %d\n",counter);
    fin_flag = calc_dU(dU,V,D); /* V,D(国同士が接している情報)より
				   dUを計算する */
    calc_U(U,dU);     /* 動作式よりUの次の状態を決定する */
    calc_V(U,V);     /* Uの値よりVの情報(発火するか否か)を決定 */
    if(fin_flag)break; /* dUが収束したら終了 */
    display_N(V);
    putchar('\n');
  }

/* 結果を出力し、終了 */
  printf("Final Result:\n");
  display_N(V);

}


/* Uの値の初期化 ランダムに設定する */
initialize(int U[N_CTRY][4])
{
  int x,i;

  for(x = 0;x < N_CTRY;x ++)
    for(i = 0;i < 4;i ++)U[x][i] = rand() % 100 - 100/2;
}

/* Vの値(発火しているか否か)をUの情報より決定する */
calc_V(int U[N_CTRY][4], int V[N_CTRY][4])
{
  int x,i;

  for(x = 0;x < N_CTRY;x ++){
    for(i = 0;i < 4;i ++){
      if(U[x][i] >= 0)V[x][i] = 1;
      else V[x][i] = 0;
    }
  }
}

/* Uの値を各ニューロンの動作式より計算する */
int calc_U(int U[N_CTRY][4],int dU[N_CTRY][4])
{
  int x,i;
  for(x = 0;x < N_CTRY;x ++)
    for(i = 0;i < 4;i ++)
      U[x][i] += dU[x][i];
}



/* V(発火状態),D(国が接している状態)より動作式dUを計算する */
int calc_dU(int dU[N_CTRY][4], int V[N_CTRY][4], int D[N_CTRY][N_CTRY])
{
  int x,i,dU_temp,fin,fin_flag;

  fin_flag = 1; /* 1 のままなら収束 */
  for(x = 0;x < N_CTRY;x ++)
    for(i = 0;i < 4;i ++){
/*      printf("In country %2d whose color is %d\n",x,i); */
      dU_temp = calc_each_dU(x,i,V,D,&fin);
      if(fin != 0)fin_flag = 0; /* 収束条件 */
/*       printf("Country %2d Color %d:dU = %d\n\n",x,i,dU_temp); */
      dU[x][i] = dU_temp;
    }
  return(fin_flag);
}

/* 国番号x 色番号i に関するニューロンの動作式を計算する */
/* *finには各ニューロンの収束式(収束なら0)が入る */
int calc_each_dU(int x,int i,int V[N_CTRY][4],int D[N_CTRY][N_CTRY],
		 int *fin)
{
#define A 5
#define B 5
#define C 5
#define C1 5
#define C2 5

  int j,y;
  int m,n;
  int result;

  int vA,vB,vC,vC1,vC2;
  
  vA = 0; /* 自分が発色している色の数 */
  for(j = 0;j < 4;j ++)vA += V[x][j];
  vA = vA - 1; /* １色を引く */

  vB = 0;
  for(y = 0;y < N_CTRY;y ++){
    if(y != x){
      m = 0; /* y の国の回りにある国の数 */
      for(n = 0;n < N_CTRY;n ++)m += D[y][n];
      vB += D[x][y] * V[y][i] * m;
/*
      if(D[x][y] && V[y][i]){
	printf("Country %d and %d is close to each other\n",x,y);
	printf("Country %d has %d countries around\n",y,m);
	for(n = 0;n < N_CTRY;n ++)printf("%d ",D[x][n]);
	printf("\nvB is now %d\n",vB);
      }
*/
    }
  }

/* ヒルクライミングターム */
  vC = 0;
  for(j = 0;j < 4;j ++)
    vC += V[x][j]; /* 色が１色でも塗られているかの判定 */
/*  printf("Country %d is colored by %d colours\n",x,vC); */

  vC1 = 0; /* x が接している領域の数 */
  for(n = 0;n < N_CTRY;n ++)vC1 += D[x][n];

  m = 0; /* x が接している領域が接している領域の合計 */
  for(y = 0;y < N_CTRY;y ++)
    for(n = 0;n < N_CTRY;n ++)m += D[x][y] * D[y][n];
  vC2 = m / vC1; /* 平均をとる */
/*
  printf("vA:%d vB:%d vC:%d vC1:%d vC2:%4lf\n",vA,vB,vC,vC1,1.0*m/vC1);
*/

  result = -1 * A * vA 
              - B * vB
              + C * h(vC) * (C1 * vC1 + C2 * vC2); /* 動作式 */

  *fin =   -1 * A * vA
              - B * vB * V[x][i]
              + C * h(vC) * (C1 * vC1 + C2 * vC2); /* 収束条件 */

  return(result);
}

/* xが0のときのみ1を返し、それ以外の時は0を返す */
int h(int x){
  if(x == 0)return 1;
  else return 0;
}


/* 全て0なら1を返す 通常は各ニューロンの動作式を入力とする */
all_zero_N(int N[N_CTRY][4])
{
  int x,i;
  for(x = 0;x < N_CTRY;x ++)
    for(i = 0;i < 4;i ++)
      if(N[x][i] != 0)return 0;
  return 1;
}


/* 各ニューロンの値を表示する */
display_N(int N[N_CTRY][4])
{
  int x,i;
  for(x = 0; x < N_CTRY;x ++){
    for(i = 0;i < 4;i ++)printf("%3d ",N[x][i]);
    putchar('\n');
  }
}

