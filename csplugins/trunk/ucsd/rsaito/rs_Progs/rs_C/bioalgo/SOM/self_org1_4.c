#include <string.h>
#include <stdlib.h>
#include <stdio.h>

#define RANGE_PARAM1 25
#define RANGE_PARAM2 0.000001
#define UPDATE_PARAM 0.00001

#define MAX_LABEL_LEN 200

#define INFI 100000
#define EMPTY -1
#define RANDOM_SEED 1

#define DEBUG_LEVEL 0

double **malloc_2d_double(int m, int n){
  
  int i;
  double **p;
  
  p = (double **)malloc(m * sizeof(double *));

  for(i = 0;i < m;i ++)
    p[i] = (double *)malloc(n * sizeof(double));

  return p;

}

void free_2d_double(double **p, int m){
  
  int i;
  for(i = 0;i < m;i ++)free(p[i]);
  free(p);

}

char **malloc_2d_char(int m, int n){
  
  int i;
  char **p;
  
  p = (char **)malloc(m * sizeof(char *));

  for(i = 0;i < m;i ++)
    p[i] = (char *)malloc(n * sizeof(char));

  return p;

}

void free_2d_char(char **p, int m){
  
  int i;
  for(i = 0;i < m;i ++)free(p[i]);
  free(p);

}


void print_ref_node_map(int ref_node_has[], int x_len, int y_len){

  int k;

  for(k = 0;k < x_len * y_len;k ++){
    if(ref_node_has[k] != INFI)printf("%d", ref_node_has[k]);
    else printf("*");
    if((k+1) % x_len  == 0)putchar('\n');
    else printf("\t");
  }

}

int neighbour(int j, int t, int x_len, int y_len, 
	      int positions[]){

  int x, y;
  double range, dist;
  int k, l;
  int count = 0;

  x = j % x_len;
  y = j / x_len;
  range = RANGE_PARAM1 / (RANGE_PARAM2 * t + 1);

#if DEBUG_LEVEL >= 4  
  printf("Range: %lf\n", range);
#endif

  if((l = y - range) < 0)l = 0;
  for(;l < y_len && l <= y + range;l ++){
    if((k = x - range) < 0)k = 0;
    for(;k < x_len && k <= x + range;k ++){
      if((dist = (k - x)*(k - x) + (l - y)*(l - y)) <= range * range){
	positions[count ++] = k + l * x_len;
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

int find_winner(double inp[], double **w, int x_len, int y_len, int dim){

  int min_j, j;
  double dist, min;
  
  for(min = INFI, min_j = 0, j = 0;j < x_len * y_len;j ++){
    dist = distance(inp, w[j], dim);
#if DEBUG_LEVEL >= 7
    printf("Difference of weight between data and reference node (%d, %d)\t%lf\n", j % x_len, j / x_len, dist);
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
		double **w, int x_len, int y_len, int dim,
		int input_belong[], int ref_node_has[]){
  int i, j, winner;
  
  for(j = 0;j < x_len * y_len;j ++)ref_node_has[j] = EMPTY;

  for(i = 0;i < data_num; i ++){
    winner = find_winner(data_set[i], w, x_len, y_len, dim);
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

void self_org(double **data_set, int data_num,
	      int dim, int x_len, int y_len, 
	      int iterations,
	      int input_belong[], int ref_node_has[]){ 

  static double **w;
  static double *inp;

  int t;
  int i, j, min_j;
  int k;
  double dist, min;
  int *neighbours;
  int n_neighbours;

  w = malloc_2d_double(x_len * y_len, dim);
  inp = (double *)malloc(dim * sizeof(double));
  neighbours = (int *)malloc(x_len * y_len * sizeof(int));

  /* Initialize w[][] */

  for(j = 0;j < x_len * y_len;j ++)
    for(i = 0;i < dim;i ++)
      w[j][i] = 1.0 * (rand() % 100); 


  /* Initialize w[][] END */

  for(t = 0;t < iterations;t ++){
    next_data(inp, t, data_set, dim, data_num);
#if DEBUG_LEVEL >= 2
    printf("***** Time %d (#%d) ***** \n", t, t % data_num);
#endif
#if DEBUG_LEVEL >= 6
    
    printf("Weight:\n");
    for(j = 0;j < x_len * y_len;j ++){
      printf("(%d,%d)\t", j % x_len, j / x_len);
      for(i = 0;i < dim;i ++)printf("%.4lf\t", w[j][i]);
      putchar('\n');
    }
    
#endif

#if DEBUG_LEVEL >= 4
    printf("Input data:\t");
    for(i = 0;i < dim;i ++){
      printf("%lf\t", inp[i]);
    }
    putchar('\n');
#endif

    min_j = find_winner(inp, w, x_len, y_len, dim);

#if DEBUG_LEVEL >= 4
    printf("Min dist node = (%d, %d)\n", min_j % x_len, min_j / x_len);
    printf("Update rate: %lf\n", update_rate(t));
#endif

    n_neighbours = neighbour(min_j, t, x_len, y_len, neighbours);

    for(k = 0;k < n_neighbours;k ++){
      j = neighbours[k];
      for(i = 0;i < dim;i ++){
#if DEBUG_LEVEL >= 7
	printf("For neighbour %d, to connection %d, update = %lf (rate=%lf)\n",
	       j, i, update_rate(t) * (inp[i] - w[j][i]), update_rate(t));
#endif
	w[j][i] += update_rate(t) * (inp[i] - w[j][i]);
      }
    }

#if DEBUG_LEVEL >= 4
    
    map_status(data_set, data_num, w, x_len, y_len, dim, 
	       input_belong, ref_node_has);
    printf("Data belongings:\n");
    for(k = 0;k < data_num;k ++)printf("%d\t", input_belong[k]);
    putchar('\n');
    putchar('\n');
    printf("Reference node map:\n");
    print_ref_node_map(ref_node_has, x_len, y_len);
#endif

  }

  map_status(data_set, data_num, w, x_len, y_len, dim, 
	     input_belong, ref_node_has);

  free_2d_double(w, x_len * y_len);
  free(inp);
  free(neighbours);

}

double *num_split(char *line, char delimit, int *n_ret){

  int n = 0;
  int p, tmp_p = 0;
  double *array;
  static char tmp[30];

  for(p = 0;line[p] != '\0';p ++)
    if(line[p] == delimit)n ++;
  array = (double *)malloc((n+1) * sizeof(double));

  n = 0;
  for(p = 0;line[p] != '\0'; p++){
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
                   int *n_ret, int *len_min, int *len_max, char ***data_label){

  FILE *fp;
  static char line[10000];
  int n = 0;
  double **data;
  int len;
  int i;

  if((fp = fopen(filename, "r")) == NULL){
    fprintf(stderr, "File \"%s\" not found.\n", filename);
    exit(0);
  }

  while(fgets(line, 10000, fp) != NULL)n ++;

  data = (double **)malloc(n * sizeof(double *));
  *data_label = malloc_2d_char(n, MAX_LABEL_LEN);
  
  rewind(fp);
  n = 0;
  *len_min = 100000;
  *len_max = 0;
  while(fgets(line, 10000, fp) != NULL){
    for(i = 0;line[i] != '\t' && line[i] != '\0'; i ++);
    strncpy((*data_label)[n], line, i);
    (*data_label)[n][i] = '\0';
    i ++;

    data[n ++ ] = num_split(&line[i], '\t', &len);
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
  int i, j, k;
  int *input_belong, *ref_node_has;
  char **data_label;

  char *filename;
  int x_len;
  int y_len;
  int iterations;

  if(argc != 4){
    fprintf(stderr, "Number of parameters not appropriate.\n");
    exit(1);
  }

  filename = argv[1];
  x_len = atoi(argv[2]);
  y_len = atoi(argv[3]);
  iterations = 30000000;

  data = read_data(filename, &ndata, &len_min, &len_max, &data_label);

  input_belong = (int *)malloc(ndata * sizeof(int));
  ref_node_has = (int *)malloc(x_len * y_len * sizeof(int));

  /*
  printf("Data reading completed.\n");
  for(i = 0;i < ndata;i ++){
    printf("%s\t", data_label[i]);
    for(j = 0;j < len_min;j ++)
      printf("%lf ", data[i][j]);
    putchar('\n');
  }
  putchar('\n');
  */

  printf("Number of data: %d, Min: %d, Max: %d\n\n", ndata, len_min, len_max);

  if(len_min != len_max){
    fprintf(stderr, "Data dimension not equal.\n");
    exit(1);
  }

  self_org(data, ndata, len_min, x_len, y_len,
	   iterations, input_belong, ref_node_has);

  printf("Final result (Trial %d):\n", iterations);

  for(k = 0;k < ndata;k ++)
    printf("%s\t%d\t%d\n", 
	   data_label[k], input_belong[k] % x_len, input_belong[k] / x_len);
 
  /*
  printf("Data belongings:\n");
  for(k = 0;k < ndata;k ++)printf("%d\t", input_belong[k]);
  putchar('\n');
  putchar('\n');
  printf("Reference node map:\n");
  print_ref_node_map(ref_node_has, x_len, y_len);
  */

  free(input_belong);
  free(ref_node_has);
  free_2d_char(data_label, ndata);

}
