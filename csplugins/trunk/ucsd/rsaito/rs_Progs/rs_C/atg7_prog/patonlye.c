#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static char match_pat[30];
static int upstream;
static int downstream;

int patonlye_par(int argc, char *argv[], int n){
  
  if(strcmp(argv[n], "-patonlye") == 0){
    strcpy(match_pat, argv[n + 1]);
    upstream = atoi(argv[n + 2]);
    downstream = atoi(argv[n + 3]);
    return 4;
  }
  else return 0;
}

void patonlye_head(char *line){
  
}

void patonlye_ent(struct gparam *entry_info, char seqn[], int max,
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
    if(0 < start && start <= max)
      for(j = -upstream + start;j <= downstream + start;j ++){
        if(j != start && strncmp(match_pat, &seqp[j - 1], strlen(match_pat))
	   == 0)
          valid_cds[i] = 0;
      }
  }
  free(compseqn);

}

void patonlye_fin(){
  
}

void patonlye_help(){
  
  printf("-patonlye\t Only accepts translation initiation site ");
  printf("which does not have specified sequence pattern in ");
  printf("specified range\n");
  
}




