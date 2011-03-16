#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "global_st.h"
#include "atg_func.h"

static int n_bases;

int dash3_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-dash3") == 0){
    n_bases = atoi(argv[n + 1]);
    return 2;
  }
  else return 0;
}

void dash3_head(char *line){

}

void dash3_ent(struct gparam *entry_info, char seqn[], int max,
              struct cds_info cds[], int ncds){

  int i,j,k,m,n, n_gc;

  if(ncds < 1)return;

  printf("%s\t", cds[0].gene);
  for(i = max - n_bases + 1; i <= max;i ++)
    if(i)putchar(seqn[i - 1]);
    else putchar(' ');

  putchar('\n');

}

void dash3_fin(){

}

void dash3_help(){

  printf("-dash3\t Displays 3' terminal sequences. State number of bases\n");

}
