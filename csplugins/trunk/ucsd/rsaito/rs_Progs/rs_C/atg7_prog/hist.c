#include <stdio.h>
#include <stdlib.h>
#define HISTMAX 1000

void main(int argc, char *argv[]){

  static int hist[HISTMAX];
  double value_begin;
  double value_step;
  double value;
  FILE *fp;
  static char line[1000];
  int histn;
  int i;

  if(argc != 4){
    fprintf(stderr, "Three parameters are required.\n");
    exit(1);
  }

  if((fp = fopen(argv[1],"r")) == NULL){
    fprintf(stderr, "File \"%s\" not found.\n", argv[1]);
    exit(1);
  }


  value_begin = atol(argv[2]);
  value_step  = atol(argv[3]);

  printf("Value begin:%lf\n", value_begin);
  printf("Value step :%lf\n", value_step);

  while(fgets(line, 1000, fp) != NULL){

    if(line[0] != '\0' && line[0] != '\n'){
      value = atof(line);
      histn = (value - value_begin) / value_step;
      if(histn >= HISTMAX || histn < 0){
	fprintf(stderr, "Value out of range.\n");
	fprintf(stderr, "%lf -> %lf - %lf\n",value,
		histn * value_step + value_begin,
		(histn+1)*value_step+value_begin);
	exit(1);
      }
      hist[ histn ] ++;
    }
  }
  fclose(fp);

  for(i = 0;i < HISTMAX;i ++){
    if(hist[i] != 0){
      printf("%.2lf %d\n", i * value_step + value_begin, hist[i]);
    }
  }
}
