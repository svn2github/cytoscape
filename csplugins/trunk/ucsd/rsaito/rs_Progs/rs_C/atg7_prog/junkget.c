#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static int min_dist, j_length;

int junkget_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-junkget") == 0){
    min_dist = atoi(argv[n + 1]);
    j_length = atoi(argv[n + 2]);
    return 3;
  }
  else return 0;
}

void junkget_head(char *line){

}

void junkget_ent(struct gparam *entry_info, char seqn[], int max,
              struct cds_info cds[], int ncds){
  int i,j,m,n;
  for(n = 0;n < ncds - 1; n ++){
    if(cds[n].cds_end != 0 && cds[n + 1].cds_start != 0 &&
       cds[n + 1].cds_start - cds[n].cds_end >= min_dist){
      j = (cds[n + 1].cds_start - cds[n].cds_end - j_length) / 2
	+ cds[n].cds_end;
      printf("Spacer between ORF #%d and ORF #%d(%d - %d)\n",
	     n, n + 1, j, j + j_length - 1);
      for(i = 0;i < j_length;i ++)
	putchar(seqn[i + j]);
      putchar('\n');
    }
  }
}

void junkget_fin(){

  printf("Finished.\n");

}

void junkget_help(){

  printf("-junkget\t Extracts spacer sequences. State minimun spacer length and length of sequence to extract.\n");

}

