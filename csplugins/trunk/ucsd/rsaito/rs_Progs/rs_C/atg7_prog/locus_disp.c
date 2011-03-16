#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

#include "global_st.h"
#include "atg_func.h"

int locus_disp_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-locus_disp") == 0)return 1;
  else return 0;

}

void locus_disp_head(char *line){

}

void locus_disp_ent(struct gparam *entry_info, char seqn[], int max,
		    struct cds_info cds[], int ncds){

  printf("%s", entry_info->entry_line);

}

void locus_disp_fin(){


}

void locus_disp_help(){

  printf("-locus_disp\t Prints valid LOCUS line\n");

}


