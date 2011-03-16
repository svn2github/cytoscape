#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

#define MAX_ENT 100
static char match_pat[MAX_ENT][30];
static int upstream[MAX_ENT];
static int nent;

int patonly2m_par(int argc, char *argv[], int n){
  int i;

  if(strcmp(argv[n], "-patonly2m") == 0){
    nent = atoi(argv[n + 1]);
    for(i = 0;i < nent; i ++){
      strcpy(match_pat[i], argv[n+2 + i*2]);
      upstream[i] = atoi(argv[n+2 + i*2 + 1]);
    }
/*
    for(i = 0;i < nent;i ++){
      printf("Resistered to patonly2m: %s %d\n",
	     match_pat[i], upstream[i]);
    }
*/
    return nent*2+2;
  }
  else return 0;
}

void patonly2m_head(char *line){
  
}

void patonly2m_ent(struct gparam *entry_info, char seqn[], int max,
		  struct cds_info cds[], int ncds){
  int i, j, k;
  char *comp_seq;
  
  comp_seq = compseqget(seqn, max);

  for(i = 0;i < ncds;i ++){
    
    if(cds[i].complement == 0 && cds[i].cds_start > 0){

      for(j = 0;j < nent;j ++){
	if(cds[i].cds_start - upstream[j] - 1 < 0 ||
	   cds[i].cds_start - upstream[j] - 1 + strlen(match_pat[j]) - 1 >= max);
	else if(strncmp(match_pat[j], &seqn[ cds[i].cds_start - upstream[j] -1],
			strlen(match_pat[j])) == 0)
	  { valid_cds[i] = 0; break; }
      }
    }
    else if(cds[i].complement == 1 && cds[i].cds_end > 0){

      for(j = 0;j < nent;j ++){
	if(max - cds[i].cds_end - upstream[j] < 0 ||
	   max - cds[i].cds_end - upstream[j] + strlen(match_pat[j]) - 1 >= max);


	else if(strncmp(match_pat[j], &comp_seq[ max - cds[i].cds_end - upstream[j]],
			strlen(match_pat[j])) == 0)

	  { valid_cds[i] = 0; break; }
      }
    }
  }
  free(comp_seq);

}

void patonly2m_fin(){
  
}

void patonly2m_help(){
  
  printf("-patonly2m\t Only accepts translation initiation site ");
  printf("which does not have sequence pattern specified upstream in ");
  printf("specified location. Multiple entries allowed. State number of entries.\n");
  
}




