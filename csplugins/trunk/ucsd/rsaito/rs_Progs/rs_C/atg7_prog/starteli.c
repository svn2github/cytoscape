#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

int starteli_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-starteli") == 0)return 1;
  else return 0;

}

void starteli_head(char *head){

}

void starteli_ent(struct gparam *entry_info, char seqn[], int max,
	       struct cds_info cds[], int ncds){
  int i,j;

  for(i = 0; i < ncds;i ++){
    if(cds[i].complement == 0){
      if(cds[i].cds_start != 0)
	strncpy(&seqn[ cds[i].cds_start - 1], "nnn", 3);
    }
    else {
      if(cds[i].cds_end != 0)
	strncpy(&seqn[ cds[i].cds_end - 1 - 2], "nnn", 3);
    }
  }
}

void starteli_fin(){

}

void starteli_help(){

  printf("-starteli\t Eliminates start codons. It will be nnn.\n");

}
