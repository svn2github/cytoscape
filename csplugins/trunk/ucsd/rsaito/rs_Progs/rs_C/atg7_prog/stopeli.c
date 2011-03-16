#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

int stopeli_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-stopeli") == 0)return 1;
  else return 0;

}

void stopeli_head(char *head){

}

void stopeli_ent(struct gparam *entry_info, char seqn[], int max,
	       struct cds_info cds[], int ncds){
  int i,j;

  for(i = 0; i < ncds;i ++){
    if(cds[i].complement == 0){
      if(cds[i].cds_end != 0)
	strncpy(&seqn[ cds[i].cds_end - 2 - 1], "nnn", 3);
    }
    else {
      if(cds[i].cds_start != 0)
	strncpy(&seqn[ cds[i].cds_start - 1 ], "nnn", 3);
    }
  }
}

void stopeli_fin(){

}

void stopeli_help(){

  printf("-stopeli\t Eliminates stop codons. It will be nnn.\n");

}
