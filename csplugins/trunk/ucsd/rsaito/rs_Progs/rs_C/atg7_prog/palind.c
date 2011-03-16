#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

#define EQUAL 0
#define EQUAL_OR_MORE 1
#define EQUAL_OR_LESS -1

static int min_base;
static int equal_flag;

int palind_par(int argc, char *argv[], int n){

 

  if(strcmp(argv[n], "-palind") == 0){
    min_base = atoi(argv[n + 1]);
    switch(argv[n + 1][strlen(argv[n + 1]) - 1]){
       case 'e':equal_flag = EQUAL;break;
       case 'l':equal_flag = EQUAL_OR_LESS;break;
       default:equal_flag = EQUAL_OR_MORE;break;
    }
    return 2;
  }
  else return 0;
}

void palind_head(char *head){


}

void palind_ent(struct gparam *entry_info, char seqn[], int max,
		    struct cds_info cds[], int ncds){
  int i,j,k,p,q;
  int plen, hplen;

  for(i = 0;i < max - 1;i ++){
    for(p = i,q = i + 1;
	p >= 0 && q < max && cmpl(seqn[p]) == seqn[q];
	p --, q++);
    hplen = i - p;
    plen  = hplen * 2;
    if((plen >= min_base && equal_flag == EQUAL_OR_MORE) ||
       (plen == min_base && equal_flag == EQUAL) ||
       (plen <= min_base && equal_flag == EQUAL_OR_LESS && plen > 0)){
      printf("Length: %d Position: %d %d ", 
	     plen, p + 1 + 1, p + 1 + plen + 1 - 1);
      printf("Sequence: "); 
      for(j = p + 1;j < q;j ++)putchar(seqn[j]);
      putchar('\n');
    }
  }
}

void palind_fin(){


}

void palind_help(){

  printf("-palind\t Searches for palindrome sequence: State minimun size\n");

}


