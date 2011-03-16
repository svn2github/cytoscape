#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static char pat[10];

int testvc_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-testvc") == 0){
    strcpy(pat, argv[n + 1]);
    return 2;
  }
  else return 0;
}

void testvc_head(char *line){

}

void testvc_ent(struct gparam *entry_info, char seqn[], int max,
		struct cds_info cds[], int ncds){
  char *compseqn;
  int i,j,k;

  compseqn = compseqget(seqn, max);

  for(i = 0;i < ncds;i ++){
    if(cds[i].complement == 0){
      if(cds[i].cds_start == 0)continue;
      if(strncmp(&seqn[ cds[i].cds_start - 1], pat, strlen(pat))
	 != 0)valid_cds[i] = 0;
    } 
    else {
      if(cds[i].cds_end == 0)continue;
      if(strncmp(&compseqn[ max - cds[i].cds_end + 1 -1 ], pat, strlen(pat))
	 != 0)valid_cds[i] = 0;
    }
  }
  
  free(compseqn);
}


void testvc_fin(){

}

void testvc_help(){
  printf("-testvc\t Specify start codon\n");
}

