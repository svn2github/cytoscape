#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static int min_cds_len;

int min_cds_len_par(int argc, char *argv[], int n){
  
  if(strcmp(argv[n], "-min_cds_len") == 0){
    min_cds_len = atoi(argv[n + 1]);
    return 2;
  }
  else return 0;
}

void min_cds_len_head(char *line){
  
}

void min_cds_len_ent(struct gparam *entry_info, char seqn[], int max,
	       struct cds_info cds[], int ncds){
  int i, j, k;
  int len;
  
  for(i = 0;i < ncds;i ++){
    if(cds[i].cds_start == 0 || cds[i].cds_end == 0){
      valid_cds[i] = 0; continue;
    }
    
    len = cds[i].cds_end - cds[i].cds_start;

    if(len < min_cds_len){ valid_cds[i] = 0; continue; }

  }
}

void min_cds_len_fin(){
  
}

void min_cds_len_help(){
  
  printf("-min_cds_len\t Only accepts data which starts with specified start codon\n");
  
}
