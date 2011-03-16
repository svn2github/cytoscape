#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static char start[5];

int only_par(int argc, char *argv[], int n){
  
  if(strcmp(argv[n], "-only") == 0){
    strcpy(start, argv[n + 1]);
    return 2;
  }
  else return 0;
}

void only_head(char *line){
  
}

void only_ent(struct gparam *entry_info, char seqn[], int max,
	       struct cds_info cds[], int ncds){
  int i, j, k;
  char comp_seq[10];
  
  for(i = 0;i < ncds;i ++){

    if(cds[i].complement == 0 && cds[i].cds_start > 0){

      if(strncmp(start, &seqn[ cds[i].cds_start - 1 ] ,3) != 0)
	
	valid_cds[i] = 0;
    }
    else if(cds[i].complement == 1 && cds[i].cds_end > 0){

      for(j = cds[i].cds_end;j >= cds[i].cds_end - 2;j --){
	comp_seq[cds[i].cds_end - j] = seqn[j - 1];
      }

      for(j = 0;j <= 2;j ++){
	if(comp_seq[j] == 'a')comp_seq[j] = 't';
	else if(comp_seq[j] == 't')comp_seq[j] = 'a';
	else if(comp_seq[j] == 'g')comp_seq[j] = 'c';
	else if(comp_seq[j] == 'c')comp_seq[j] = 'g';
      }
      if(strncmp(start, comp_seq, 3) != 0) valid_cds[i] = 0;
    }
  }
}

void only_fin(){
  
}

void only_help(){
  
  printf("-only\t Only accepts data which starts with specified start codon\n");
  
}




