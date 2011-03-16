#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static int upstream;
static int downstream;

int eliatg_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-eliatg") == 0){
    upstream = atoi(argv[n + 1]);
    downstream = atoi(argv[n + 2]);
    return 3;
  }
  else return 0;
}

void eliatg_head(int argc, char *argv[], int n){

}

void eliatg_ent(struct gparam *entry_info, char seqn[], int max,
	       struct cds_info cds[], int ncds){
    int i, j, k, start, end;
    char *compseqn;
    char *seqp;

    compseqn = compseqget(seqn, max);
    for(i = 0;i < ncds;i ++){
      if(cds[i].complement){
	seqp = compseqn;
	start = max - cds[i].cds_end + 1;
      }
      else {
	seqp = seqn;
	start = cds[i].cds_start;
      }
      
      for(j = -upstream * 3 + start;j <= downstream * 3 + start;j += 3){
	if(j != start && strncmp("atg", &seqp[j - 1], 3) == 0)
	  valid_cds[i] = 0;
      }
    }
    free(compseqn);
}

void eliatg_fin(){
  
}

void eliatg_help(){

  printf("-eliatg\t Eliminates sequences which has \"atg\" in the same frame. State range(upstream and downstream on number of amino acids)\n");

}



