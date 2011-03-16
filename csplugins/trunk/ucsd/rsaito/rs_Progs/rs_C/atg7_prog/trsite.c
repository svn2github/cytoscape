#include <stdio.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

int trsite_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-trsite") == 0){
    return 1;
  }
  else return 0;
}

void trsite_head(char *line){

}

void trsite_ent(struct gparam *entry_info, char seqn[], int max,
	      struct cds_info cds[], int ncds){
  int i,j,k;
  for(i = 0;i < ncds;i ++){
    if(valid_cds[i] == 0)continue;
    if(cds[i].complement == 0){
      for(j = -10;j < 10;j ++)
	if(cds[i].cds_start + j > 0 && cds[i].cds_start + j <= max)
	  putchar(seqn[ cds[i].cds_start + j - 1]);
        else putchar(' ');
      putchar('\n');
    }
  }
}

void trsite_fin(){

  printf("Finished!!\n");
}

void trsite_help(){
  printf("-trsite Displays nucleotide sequence of translation initiation sites.\n");

}
