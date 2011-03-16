#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static int min_base;
static int arb_base;

int palind3_par(int argc, char *argv[], int n){

 

  if(strcmp(argv[n], "-palind3") == 0){
    min_base = atoi(argv[n + 1]);
    arb_base = atoi(argv[n + 2]);
    return 3;
  }
  else return 0;
}

void palind3_head(char *head){


}

void palind3_ent(struct gparam *entry_info, char seqn[], int max,
		    struct cds_info cds[], int ncds){
  int i,j,k,p,q;
  int plen, hplen;

  for(i = 0;i < max - 1;i ++){
    for(p = i,q = i + 1 + arb_base;
	p >= 0 && q < max && comp_match(seqn[p],seqn[q]);
	p --, q++);
    hplen = i - p;
    plen  = hplen * 2;
    if(plen >= min_base){
      printf("Length: %d Position: %d %d ", 
	     plen, p + 1 + 1, p + 1 + plen + arb_base + 1 - 1);
      printf("Sequence: "); 
      for(j = p + 1;j < q;j ++){
	putchar(seqn[j]);
	if(j == p + hplen)putchar(' ');
	else if(j == p + hplen + arb_base)putchar(' ');
      }
      putchar('\n');
    }
  }
}

void palind3_fin(){


}

void palind3_help(){

  printf("-palind3\t Searches for palindrome sequence (allowing g-t match) with some arbitrary bases: State minimun size and number of arbitrary sequence inside\n"); 

}
