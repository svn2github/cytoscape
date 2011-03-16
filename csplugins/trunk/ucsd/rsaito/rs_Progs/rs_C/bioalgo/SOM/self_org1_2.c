#include <stdio.h>
#include <stdlib.h>

#define X_LEN 5
#define Y_LEN 5
#define INPUT_DIM 3
#define RANGE_PARAM1 10
#define RANGE_PARAM2 0.005
#define MAX_TIME 10000
#define INFI 100000
#define EMPTY -1
#define UPDATE_PARAM 0.01
#define RANDOM_SEED 1

#define DEBUG_LEVEL 4

void print_ref_node_map(int ref_node_has[], int x_len, int y_len){

  int k;

  for(k = 0;k < x_len * y_len;k ++){
    if(ref_node_has[k] != INFI)printf("%d", ref_node_has[k]);
    else printf("*");
    if((k+1) % x_len  == 0)putchar('\n');
    else printf("\t");
  }

}

int neighbour(int j, int t, int positions[]){

  int x, y;
  double range, dist;
  int k, l;
  int count = 0;

  x = j % X_LEN;
  y = j / X_LEN;
  range = RANGE_PARAM1 / (RANGE_PARAM2 * t + 1);

#if DEBUG_LEVEL >= 4  
  printf("Range: %lf\n", range);
#endif

  if((l = y - range) < 0)l = 0;
  for(;l < Y_LEN && l <= y + range;l ++){
    if((k = x - range) < 0)k = 0;
    for(;k < X_LEN && k <= x + range;k ++){
      if((dist = (k - x)*(k - x) + (l - y)*(l - y)) <= range * range){
	positions[count ++] = k + l * X_LEN;
#if DEBUG_LEVEL >= 5
	printf("(%d, %d) = %.3lf <= %.3lf\n", k, l, dist, range * range);
#endif
      }
    }
  }

  return count;

}

double distance(double data1[], double data2[], int dim){

  double total;
  int k;

  for(total = 0.0, k = 0;k < dim;k ++)
    total += (data1[k] - data2[k]) * (data1[k] - data2[k]);
  return total;

}

int find_winner(double inp[], double w[X_LEN * Y_LEN][INPUT_DIM]){

  int min_j, j;
  double dist, min;
  
  for(min = INFI, min_j = 0, j = 0;j < X_LEN * Y_LEN;j ++){
    dist = distance(inp, w[j], INPUT_DIM);
#if DEBUG_LEVEL >= 7
    printf("Difference of weight between data and reference node (%d, %d)\t%lf\n", j % X_LEN, j / X_LEN, dist);
#endif
    if(dist < min){ 
      min = dist;
      min_j = j;
    }
  }
  
  return min_j;

}

double update_rate(int t){

  return (1.0 / (UPDATE_PARAM*t + 1.0));

}


void map_status(double **data_set, int data_num, 
		double w[X_LEN * Y_LEN][INPUT_DIM],
		int input_belong[], int ref_node_has[]){
  int i, j, winner;
  
  for(j = 0;j < X_LEN * Y_LEN;j ++)ref_node_has[j] = EMPTY;

  for(i = 0;i < data_num; i ++){
    winner = find_winner(data_set[i], w);
    input_belong[i] = winner;
    if(ref_node_has[ winner ] == EMPTY)ref_node_has[ winner ] = i;
    else ref_node_has[ winner ] = INFI;
  }

}

int next_data(double *inp, int t, double **data_set,
	      int input_dim, int data_num){

  int p, i;
  p = t % data_num;
  for(i = 0;i < input_dim;i ++)
    inp[i] = data_set[p][i];
  return p;
}

void self_org(double **data_set, int data_num){ 

  static double w[X_LEN * Y_LEN][INPUT_DIM];
  static double inp[INPUT_DIM];

  static int *input_belong, *ref_node_has;
  
  int t;
  int i, j, min_j;
  int k;
  double dist, min;
  static int neighbours[X_LEN * Y_LEN];
  int n_neighbours;

  input_belong = (int *)malloc(data_num * sizeof(int));
  ref_node_has = (int *)malloc(X_LEN * Y_LEN * sizeof(int));

  /* Initialize w[][] */

  for(j = 0;j < X_LEN * Y_LEN;j ++)
    for(i = 0;i < INPUT_DIM;i ++)
      w[j][i] = 1.0 * (rand() % 100); 


  /* Initialize w[][] END */

  for(t = 0;t < MAX_TIME;t ++){
    next_data(inp, t, data_set, INPUT_DIM, data_num);
#if DEBUG_LEVEL >= 2
    printf("***** Time %d (#%d)***** \n", t, t % data_num);
#endif
#if DEBUG_LEVEL >= 6
    
    printf("Weight:\n");
    for(j = 0;j < X_LEN * Y_LEN;j ++){
      printf("(%d,%d)\t", j % X_LEN, j / X_LEN);
      for(i = 0;i < INPUT_DIM;i ++)printf("%.4lf\t", w[j][i]);
      putchar('\n');
    }
    
#endif

    printf("Input data:\t");
    for(i = 0;i < INPUT_DIM;i ++){
      printf("%lf\t", inp[i]);
    }
    putchar('\n');
    min_j = find_winner(inp, w);
    printf("Min dist node = (%d, %d)\n", min_j % X_LEN, min_j / X_LEN);

#if DEBUG_LEVEL >= 4
    printf("Update rate: %lf\n", update_rate(t));
#endif

    n_neighbours = neighbour(min_j, t, neighbours);

    for(k = 0;k < n_neighbours;k ++){
      j = neighbours[k];
      for(i = 0;i < INPUT_DIM;i ++){
#if DEBUG_LEVEL >= 7
	printf("For neighbour %d, to connection %d, update = %lf (rate=%lf)\n",
	       j, i, update_rate(t) * (inp[i] - w[j][i]), update_rate(t));
#endif
	w[j][i] += update_rate(t) * (inp[i] - w[j][i]);
      }
    }

    map_status(data_set, data_num, w, input_belong, ref_node_has);
    printf("Data belongings:\n");
    for(k = 0;k < data_num;k ++)printf("%d\t", input_belong[k]);
    putchar('\n');
    putchar('\n');
    printf("Reference node map:\n");
    print_ref_node_map(ref_node_has, X_LEN, Y_LEN);
  }
  
  free(input_belong);
  free(ref_node_has);
}

main(){

  static double data0[] = { 4.0, 5.0, 2.0 };
  static double data1[] = { 4.1, 5.2, 2.3 };
  static double data2[] = { 70, 70, 70 };
  static double data3[] = { 4.3, 5.0, 2.1 };
  static double data4[] = { 20, 21, 22 };
  static double data5[] = { 3.8, 5.3, 2.0 };
  static double data6[] = { 1, 2, 3 };
  static double data7[] = { 5, 4, 3 };
  static double data8[] = { 1, 3, 3 };
  static double data9[] = { 1, 2.1, 3.1 };
  static double data10[] = { 5.1, 4.1, 3.1 };
  static double data11[] = { 1.1, 3.1, 3.1 };
  static double data12[] = { 50, 34, 13 };
  static double data13[] = { 45, 32, 15 };
  static double data14[] = { 49, 51, 21 };
  static double data15[] = { 3.9, 5.0, 2.1 };
  static double data16[] = { 21, 22, 23 };
  static double data17[] = { 23, 24, 25 };
  static double data18[] = { 25, 26, 27 };
  static double data19[] = { 4.5, 5.1, 2.2 };

  
  static double *data[20];

  data[0] = data0;
  data[1] = data1;
  data[2] = data2;
  data[3] = data3;
  data[4] = data4;
  data[5] = data5;
  data[6] = data6;
  data[7] = data7;
  data[8] = data8;
  data[9] = data9;
  data[10] = data10;
  data[11] = data11;
  data[12] = data12;
  data[13] = data13;
  data[14] = data14;
  data[15] = data15;
  data[16] = data16;
  data[17] = data17;
  data[18] = data18;
  data[19] = data19;
  self_org(data, 20);
  
}
