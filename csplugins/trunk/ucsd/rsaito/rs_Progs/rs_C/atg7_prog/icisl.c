#include <stdio.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

int icisl_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-icisl") == 0){
    return 1;
  }
  else return 0;
}

void icisl_head(char *line){

}

void icisl_ent(struct gparam *entry_info, char seqn[], int max,
              struct cds_info cds[], int ncds){

  int i, j, dist;

  for(i = 0;i < ncds ;i ++){
    if(valid_cds[i] == 0)continue;

    if(cds[i].complement == 0 && i - 1 >= 0){
      dist = cds[i].cds_start - cds[i - 1].cds_end;
    }

    else if(cds[i].complement == 1 && i + 1 < ncds){
      dist = cds[i + 1].cds_start - cds[i].cds_end; 
    }
    else continue;
    printf("%d: %d\n", i,dist);
  }
}

void icisl_fin(){


}

void icisl_help(){

  printf("-icisl\t Displays distance to previous CDS\n");

}


