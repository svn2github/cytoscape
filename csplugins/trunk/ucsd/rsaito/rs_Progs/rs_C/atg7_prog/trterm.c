#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static int upto;
static int downto;

int trterm_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-trterm") == 0){
    upto = atoi(argv[n + 1]);
    downto=atoi(argv[n + 2]);
    return 3;
  }
  else return 0;
}

void trterm_head(char *head){

}

void trterm_ent(struct gparam *entry_info, char seqn[], int max,
	       struct cds_info cds[], int ncds){
  int i,j;
  char *comp_seq;
  
  comp_seq = compseqget(seqn, max);

  for(i = 0;i < ncds;i ++){
    /* printf("%s %4d:", cds[i].gene, i); */
    printf("%s\t", cds[i].gene);
    if(cds[i].complement == 0 && cds[i].cds_end != 0){
      for(j = -upto; j <= downto;j ++){
	if(j == - 2)putchar('\t');
	if(j == + 1)putchar('\t');
	/*
	  if(j + cds[i].cds_end > max)putchar('-');
	*/
	if(j + cds[i].cds_end > max)putchar(' ');
	  
	else putchar(seqn[ j + cds[i].cds_end - 1 ]);
      }
      putchar('\n');
    }
    else if(cds[i].complement == 1 && cds[i].cds_start != 0){
      for(j = -upto; j <= downto;j ++){
	if(j == - 2)putchar('\t');
	if(j == + 1)putchar('\t');
	if(max - cds[i].cds_start + 1 + j > max)putchar(' ');
	else putchar(comp_seq[ max - cds[i].cds_start + 1 - 1 + j ]);
      }
      putchar('\n');
    }
  }

  free(comp_seq);
}

void trterm_fin(){

}

void trterm_help(){

  printf("-trterm Displays sequences around translation termination sites:");
  printf("State number of bases upstream and downstream\n");

}
