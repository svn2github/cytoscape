#include <stdlib.h>
#include <stdio.h>

#define X_LEN 10
#define Y_LEN 10
#define INPUT_DIM 3
#define RANGE_PARAM1 10
#define RANGE_PARAM2 0.005
#define MAX_TIME 10000
#define INFI 100000
#define EMPTY -1
#define UPDATE_PARAM 0.01
#define RANDOM_SEED 1

#define DEBUG_LEVEL 0

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

#if DEBUG_LEVEL >= 4
    printf("Input data:\t");
    for(i = 0;i < INPUT_DIM;i ++){
      printf("%lf\t", inp[i]);
    }
    putchar('\n');
#endif

    min_j = find_winner(inp, w);

#if DEBUG_LEVEL >= 4
    printf("Min dist node = (%d, %d)\n", min_j % X_LEN, min_j / X_LEN);
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

#if DEBUG_LEVEL >= 4
    
    map_status(data_set, data_num, w, input_belong, ref_node_has);
    printf("Data belongings:\n");
    for(k = 0;k < data_num;k ++)printf("%d\t", input_belong[k]);
    putchar('\n');
    putchar('\n');
    printf("Reference node map:\n");
    print_ref_node_map(ref_node_has, X_LEN, Y_LEN);
#endif
    if(t + 1 == MAX_TIME){
      printf("Final result (Trial %d):\n", t + 1);
      map_status(data_set, data_num, w, input_belong, ref_node_has);
      printf("Data belongings:\n");
      for(k = 0;k < data_num;k ++)printf("%d\t", input_belong[k]);
      putchar('\n');
      putchar('\n');
      printf("Reference node map:\n");
      print_ref_node_map(ref_node_has, X_LEN, Y_LEN);
    }
  }
  
  free(input_belong);
  free(ref_node_has);
}

double *num_split(char *line, char delimit, int *n_ret){

  int n = 0;
  int p, tmp_p = 0;
  double *array;
  static char tmp[30];

  for(p = 0;line[p] != '\0' && line[p] != '#';p ++)
    if(line[p] == delimit)n ++;
  array = (double *)malloc((n+1) * sizeof(double));

  n = 0;
  for(p = 0;line[p] != '\0' && line[p] != '#'; p++){
    if(line[p] != delimit)tmp[tmp_p ++] = line[p];
    else {
      tmp[tmp_p] = '\0';
      sscanf(tmp, "%lf", &array[n ++]);
      tmp_p = 0;
    }
    
  }
  tmp[tmp_p] = '\0';
  sscanf(tmp, "%lf", &array[n ++]);

  *n_ret = n;
  return array;

}

double **read_data(char *filename,
                   int *n_ret, int *len_min, int *len_max){

  FILE *fp;
  static char line[10000];
  int n = 0;
  double **data;
  int len;

  if((fp = fopen(filename, "r")) == NULL){
    fprintf(stderr, "File \"%s\" not found.\n", filename);
    exit(0);
  }

  while(fgets(line, 10000, fp) != NULL)n ++;
  close(fp);

  data = (double **)malloc(n * sizeof(double *));
  
  fp = fopen(filename, "r");
  n = 0;
  *len_min = 100000;
  *len_max = 0;
  while(fgets(line, 10000, fp) != NULL){
    data[n ++ ] = num_split(line, '\t', &len);
    if(len < *len_min)*len_min = len;
    if(len > *len_max)*len_max = len;
  }
  close(fp);

  *n_ret = n;
  return data;
}

main(int argc, char *argv[]){

  int ndata, len_min, len_max;
  double **data;
  int i, j;
  
  data = read_data(argv[1], &ndata, &len_min, &len_max);

  printf("Data reading completed.\n");
  for(i = 0;i < ndata;i ++){
    for(j = 0;j < len_min;j ++)
      printf("%lf ", data[i][j]);
    putchar('\n');
  }
  putchar('\n');
  printf("Number of data: %d, Min:%d, Max:%d\n\n", ndata, len_min, len_max);
  
  self_org(data, ndata);
}
