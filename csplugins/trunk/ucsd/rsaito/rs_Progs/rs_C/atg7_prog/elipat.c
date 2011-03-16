#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static int pos1;
static int pos2;
static char pat[20];

int elipat_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-elipat") == 0){
    pos1 = atoi(argv[n + 1]);
    pos2 = atoi(argv[n + 2]);
    strcpy(pat, argv[n + 3]);
    return 4;
  }
  else return 0;
}

void elipat_head(int argc, char *argv[], int n){

}

void elipat_ent(struct gparam *entry_info, char seqn[], int max,
	       struct cds_info cds[], int ncds){
    int i, j, k, start, end;
    char *compseqn;
    char *seqp;

    compseqn = compseqget(seqn, max);
    for(i = 0;i < ncds;i ++){
      if(cds[i].complement){
	seqp = compseqn;
	start = max - cds[i].cds_end + 1;
      }
      else {
	seqp = seqn;
	start = cds[i].cds_start;
      }
      
      for(j = pos1 + start;j <= pos2 + start; j ++){
	if(j < 1)j = 1;
	else if(j + strlen(pat) - 1 > max)break;
	if(j != start && strncmp(pat, &seqp[j - 1], strlen(pat)) == 0)
	  valid_cds[i] = 0;
      }
    }
    free(compseqn);
}

void elipat_fin(){
  
}

void elipat_help(){

  printf("-elipat\t Eliminates sequences which have pattern in the specified range. Specify range and pattern\n");

}



