#include <string.h>
#include <stdio.h>

double *num_split(char *line, char delimit, int *n_ret){

  int n = 0;
  int p, tmp_p = 0;
  double *array;
  static char tmp[30];

  for(p = 0;line[p] != '\0';p ++)if(line[p] == delimit)n ++;
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


main(){

  char *line = "-10.2,-5.0,2.1,1.9, 2.5, -1.3 ";
  double *array;
  int n;
  int i;
  array = num_split(line, ',', &n);
  for(i = 0;i < n;i ++)printf("%d\t%lf\n", i, array[i]);
  
}
