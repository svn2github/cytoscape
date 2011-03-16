#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static char match_pat[30];
static int upstream;

int patonly2_par(int argc, char *argv[], int n){
  
  if(strcmp(argv[n], "-patonly2") == 0){
    strcpy(match_pat, argv[n + 1]);
    upstream = atoi(argv[n + 2]);
    return 3;
  }
  else return 0;
}

void patonly2_head(char *line){
  
}

void patonly2_ent(struct gparam *entry_info, char seqn[], int max,
		  struct cds_info cds[], int ncds){
  int i, j, k;
  char *comp_seq;
  
  comp_seq = compseqget(seqn, max);

  for(i = 0;i < ncds;i ++){

    if(cds[i].complement == 0 && cds[i].cds_start > 0){

      if(cds[i].cds_start - upstream - 1 < 0 ||
	 cds[i].cds_start - upstream - 1 + strlen(match_pat) - 1 >= max);


      else if(strncmp(match_pat, &seqn[ cds[i].cds_start - upstream -1],
		      strlen(match_pat)) == 0)
	
	valid_cds[i] = 0;
    }
    else if(cds[i].complement == 1 && cds[i].cds_end > 0){

      if(max - cds[i].cds_end - upstream < 0 ||
	 max - cds[i].cds_end - upstream + strlen(match_pat) - 1 >= max);


      else if(strncmp(match_pat, &comp_seq[ max - cds[i].cds_end - upstream],
		      strlen(match_pat)) == 0)

	valid_cds[i] = 0;
    }
  }
  free(comp_seq);
}

void patonly2_fin(){
  
}

void patonly2_help(){
  
  printf("-patonly2\t Only accepts translation initiation site ");
  printf("which does not have sequence pattern specified upstream in ");
  printf("specified location\n");
  
}




