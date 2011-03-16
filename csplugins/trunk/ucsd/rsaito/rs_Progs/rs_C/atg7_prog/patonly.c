#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static char match_pat[30];
static int upstream;

int patonly_par(int argc, char *argv[], int n){
  
  if(strcmp(argv[n], "-patonly") == 0){
    strcpy(match_pat, argv[n + 1]);
    upstream = atoi(argv[n + 2]);
    return 3;
  }
  else return 0;
}

void patonly_head(char *line){
  
}

void patonly_ent(struct gparam *entry_info, char seqn[], int max,
	       struct cds_info cds[], int ncds){
  int i, j, k;
  char *comp_seq;
  
  comp_seq = compseqget(seqn, max);

  for(i = 0;i < ncds;i ++){

    if(cds[i].complement == 0 && cds[i].cds_start > 0){

      if(cds[i].cds_start - upstream - 1 < 0 ||
	 cds[i].cds_start - upstream - 1 + strlen(match_pat) - 1 >= max)
	valid_cds[i] = 0;
      
      else if(strncmp(match_pat, &seqn[ cds[i].cds_start - upstream -1],
		 strlen(match_pat)) != 0)
	valid_cds[i] = 0;
    }
    else if(cds[i].complement == 1 && cds[i].cds_end > 0){

      if(max - cds[i].cds_end - upstream < 0 ||
	 max - cds[i].cds_end - upstream + strlen(match_pat) - 1 >= max)
      	valid_cds[i] = 0;
      
      else if(strncmp(match_pat, &comp_seq[ max - cds[i].cds_end - upstream],
		 strlen(match_pat)) != 0)
	valid_cds[i] = 0;
    }
  }
  free(comp_seq);
}

void patonly_fin(){
  
}

void patonly_help(){
  
  printf("-patonly\t Only accepts translation initiation site ");
  printf("which has sequence pattern specified upstream in ");
  printf("specified location\n");

}




