#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "global_st.h"
#include "atg_func.h"

int cds_echo_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-cds_echo") == 0){
    return 1;
  }
  else return 0;
}

void cds_echo_head(char *line){

}

void cds_echo_ent(struct gparam *entry_info, char seqn[], int max,
		  struct cds_info cds[], int ncds){

  int i;
  for(i = 0;i < ncds;i ++){
    if(valid_cds[i] == 0)continue;

    if(cds[i].complement == 0){
      /*      printf("     CDS             %d..%d\n", cds[i].cds_start, cds[i].cds_end);
       */
      printf("%d %d n\n", cds[i].cds_start, cds[i].cds_end);
    }
    else {
      /*      printf("     CDS             complement(%d..%d)\n", cds[i].cds_start, cds[i].cds_end)
       */
      printf("%d %d c\n", cds[i].cds_start, cds[i].cds_end);
    }
  }
}

void cds_echo_fin(){


}

void cds_echo_help(){

  printf("-cds_echo\t Displays information about CDS start and end in GenBank format.\n");

}
