#include <string.h>
#include <stdio.h>

#include "global_st.h"
#include "atg_func.h"

int nucseq_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-nucseq") == 0)
    return 1;
  return 0;
}

void nucseq_ent(struct gparam *entry_info, char seqn[], int max,
              struct cds_info cds[], int ncds){
  int i;
  for(i = 0;i < max;i ++){
    putchar(seqn[i]);
    if((i+1) % 50 == 0)putchar('\n');
  }
  putchar('\n');
}

void nucseq_help(){

  printf("-nucseq\t Displays nucleotide sequences in the file.\n");

}
