#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#include "global_st.h"
#include "atg_func.h"

static int utrrange;
static int cdsrange;
static char pat[1000];

int patfreq_simple_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-patfreq_simple") == 0){
    strcpy(pat, argv[n + 1]);
    utrrange = atoi(argv[n + 2]);
    cdsrange = atoi(argv[n + 3]);
    return 4;
  }
  else return 0;
}

void patfreq_simple_head(char *line){

}

void patfreq_simple_ent(struct gparam *entry_info, char seqn[], int max,
			struct cds_info cds[], int ncds){
  
  if(ncds != 1)return;
  
  



}

void patfreq_simple_fin(){



}

void patfreq_simple_help(){


}
