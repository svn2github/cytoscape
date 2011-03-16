#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#include "global_st.h"
#include "atg_func.h"

static char seqfile_name[20];
static FILE *fp;
static int percentage;
static char *buffer;
static int buf_size;

int patfind_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-patfind") == 0){
    if((fp = fopen(argv[n + 1], "r")) == NULL){
      /*
      fprintf(stderr, "Cannot find sequence pattern file \"%s\"\n", argv[1]);
      exit(1);
      */
      buffer = argv[n + 1];
      buf_size = strlen(argv[n + 1]);
    }
    else { 
      nseqread(&buffer, &buf_size, fp);
      fclose(fp);
    }

    printf("Sequence:%s\n", buffer);
    printf("Size:%d\n", buf_size);

    percentage = atoi(argv[n + 2]);
    return 3;
  }
  else return 0;
}

void patfind_head(char *line){

}

void patfind_ent(struct gparam *entry_info, char seqn[], int max,
		 struct cds_info cds[], int ncds){

  int i,j;
  int nmatch;

  for(i = 0;i < max;i ++){

    nmatch = 0;
    for(j = i;j < i + buf_size && j < max;j ++){
      if(buffer[j - i] == seqn[j]){
	nmatch ++;
      }
      if(100.0*(buf_size - (j - i + 1) + nmatch) / buf_size < percentage){
	/*
	printf("Cancel at %d(i:%d j:%d nmatch:%d buf_size:%d\n",
	       i + 1, i, j, nmatch, buf_size);
	       */
	break;
      }
    }
    if(100.0 * nmatch / buf_size >= percentage){
      printf("%.2lf%% match from %d :", 100.0 * nmatch / buf_size, i + 1);
      for(j = i;j < i + buf_size && j < max;j ++){
	if(buffer[j - i] == seqn[j]){
	  putchar(toupper(seqn[j]));
	}
	else putchar(seqn[j]);
      }
      putchar('\n');
    }
  }

}

void patfind_fin(){


}

void patfind_help(){

  printf("-patfind\t Finds pattern: State sequence pattern file and homology percentage\n"); 


}

