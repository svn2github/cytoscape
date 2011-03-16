#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static int upto;
static int down;

int pseudo_atg_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-pseudo_atg") == 0){
    upto = atoi(argv[n + 1]);
    down = atoi(argv[n + 2]);
    return 3;
  }
  else return 0;
}

void pseudo_atg_head(char *line){

}

void pseudo_atg_ent(struct gparam *entry_info, char seqn[], int max,
		    struct cds_info cds[], int ncds){
  int i,j,k;
  int begin, end;
  char *compseqn;

  compseqn = compseqget(seqn, max);

  for(i = 0; i < ncds; i ++){
    if(valid_cds[i] == 0)continue;

    if(cds[i].complement == 0){
      for(j = cds[i].cds_start - 30;j <= cds[i].cds_start + 30;j ++){
	if(j > 0 && j <= max &&
	   strncmp(&seqn[j - 1], "atg", 3) == 0 &&
	   j != cds[i].cds_start){
	  for(k = j - upto; k <= j + down;k ++){
	    if(k > 0 && k <= max)putchar(seqn[k - 1]);
	    else putchar(' ');
	    if(k == j - 1 || k == j + 2)
	      putchar(' ');
	  }
	  printf(" (CDS %d (%d))\n", i, j - cds[i].cds_start);
	}
      }
    }
    else {
      for(j = max - cds[i].cds_end + 1 - 30;
	  j <= max - cds[i].cds_end + 1 + 30;j ++)
	if(j > 0 && j <= max &&
	   strncmp(&compseqn[j - 1], "atg", 3) == 0 &&
	   j != max - cds[i].cds_end + 1){
	  for(k = j - upto; k <= j + down;k ++){
	    if(k > 0 && k <= max)
	      putchar(compseqn[k - 1]);
	    else putchar(' ');
	    if(k == j - 1 || k == j + 2)
	      putchar(' ');
	  }
	  printf(" (CDS %d (%d))\n", i, j - (max - cds[i].cds_end + 1));
	}
    }
  }

  free(compseqn);
}

void pseudo_atg_fin(){


}

void pseudo_atg_help(){

  printf("-pseudo_atg\t Gets sequences around AUG triplet other than start codon. State upstream and downstream.\n");


}


