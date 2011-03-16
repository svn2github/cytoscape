#define N_DATA 4 /* データ数 */
#define DIM 21

/* クラスター情報 */
struct CLUSTER {
  int n; /* データ数 */
  int gene[N_DATA]; /* 保持しているデータ番号の集合 */
}; 

/* プロトタイプ宣言 */
double  corr_single(double a[], double b[], int n);
double  corr_clust(struct CLUSTER& a, struct CLUSTER& b,
                   double d[N_DATA][N_DATA]);
void merge(struct CLUSTER& a, struct CLUSTER& b, struct CLUSTER& merged);

/* n次元データa, bの相関係数を計算する */
double corr_single(double a[], double b[], int n){
  int i;
  double d = 0.0;
  double mean_a, mean_b;
  double var_a, var_b;
  double corr;
  
  for(mean_a = 0, i = 0;i < n;i ++)mean_a += a[i] / n;
  for(mean_b = 0, i = 0;i < n;i ++)mean_b += b[i] / n;
  for(var_a = 0, i = 0;i < n;i ++)
    var_a += (a[i] - mean_a)*(a[i] - mean_a) / n;
  for(var_b = 0, i = 0;i < n;i ++)
    var_b += (b[i] - mean_b)*(b[i] - mean_b) / n;
  for(corr = 0, i = 0;i < n;i ++)
    corr += (a[i] - mean_a) / sqrt(var_a)
      * (b[i] - mean_b) / sqrt(var_b) / n;

  return corr;
}

/* クラスターa, bの要素間の相関を計算する */
double corr_clust(struct CLUSTER& a, struct CLUSTER& b,
                  double d[N_DATA][N_DATA]){
  int i, k;
  double max;
  
  max = d[ a.gene[0] ][ b.gene[0] ];
  for (i = 0; i < a.n; i ++){
      for (k = 0; k < b.n; k ++){
          if (max < d[ a.gene[i] ][ b.gene[k] ])
              max = d[ a.gene[i] ][ b.gene[k] ];
        }
    }
  return max;
}

/* クラスターa, bを統合し、結果をクラスターnewに入れる */
void merge(struct CLUSTER& a, struct CLUSTER& b, struct CLUSTER& merged){
  int i, n;
  
  n = 0;
  for (i = 0; i < a.n; i ++)
      merged.gene[ n++ ] = a.gene[i];
  for (i=0; i < b.n; i++)
      merged.gene[ n++ ] = b.gene[i];
  merged.n = n;
}

/* 次元数dimのデータ群dataを階層的クラスタリング
   データ数N_DATAはグローバル変数として与えられる */
void h_cluster(double data[N_DATA][DIM]){
  int i, j, n;
  double d[N_DATA][N_DATA];   /* データ間の相関行列 */
  double corr, corr_max;      /* データ間の相関 */
  int c1, c2;
  
  static struct CLUSTER cl[N_DATA]; /* クラスター情報 */
  static struct CLUSTER w;     /* ワーク用 */
  
  /* クラスター情報の初期化 */
  for (i=0; i < N_DATA; i++){
      cl[i].n = 1;
      cl[i].gene[0] = i;
    }
  
  /* 相関行列の作成 */
  for (i = 0; i < N_DATA; i++)
      for (j = 0; j < N_DATA; j++)
        d[i][j] = corr_single(data[i], data[j], DIM);
  
  /* 階層的クラスタリング */
  n = N_DATA - 1;
  while (n > 0){
    corr_max = -1.0;
    for (i = 0; i < n; i ++){
        for (j = i + 1; j <= n; j ++){
              corr = corr_clust(cl[i], cl[j], d);
            if (corr > corr_max){ 
                corr_max = corr;
                c1 = i, c2 = j;
              }	    
          }
      }

    printf("[ Merge %d ] Merging the following two clusters:\n",
           N_DATA - n - 1);
    printf("(1) Cluster containing data #");
    for(i = 0;i < cl[ c1 ].n;i ++){
      printf("%d", cl[ c1 ].gene[i]);
      if(i < cl[ c1 ].n - 1)putchar(',');
    }
    printf("\n(2) Cluster containing data #");
    for(i = 0;i < cl[ c2 ].n;i ++){
      printf("%d", cl[ c2 ].gene[i]);
      if(i < cl[ c2 ].n - 1)putchar(',');
    }
    printf("\nDistance between these two clusters = %lf\n\n", corr_max);
    
    merge(cl[ c1 ], cl[ c2 ], w); /* クラスターを統合 */
    cl[ c1 ] = w; /* クラスターc2をc1へ統合して格納 */	  
    cl[ c2 ] = cl[n]; /* クラスターc2を破棄し、一番後ろのクラスターを代わりに格納 */
    n--; /* クラスターを１つ減らす */
  }
}