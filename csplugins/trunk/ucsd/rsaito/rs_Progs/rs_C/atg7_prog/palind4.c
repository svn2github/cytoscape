#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static int min_base;
static int arb_base;
static char str1[50], str2[50];

int palind4_par(int argc, char *argv[], int n){

 

  if(strcmp(argv[n], "-palind4") == 0){
    min_base = atoi(argv[n + 1]);
    arb_base = atoi(argv[n + 2]);
    return 3;
  }
  else return 0;
}

void palind4_head(char *head){


}

void palind4_ent(struct gparam *entry_info, char seqn[], int max,
		    struct cds_info cds[], int ncds){

  int h_base;
  int i, j, ct;

  h_base = min_base / 2;

  for(i = 0;i < h_base;i ++)str1[i] = 'a'; str1[i] = '\0';
  for(i = 0;i < h_base;i ++)str2[i] = 'a'; str2[i] = '\0';

  while(1){

    printf("%s ", str1);
    for(i = 0;i < arb_base;i ++)putchar('n');
    printf(" %s ", str2);

    ct = 0;
    for(i = 0;i < max - h_base*2 - arb_base;i ++){
      if(strncmp(&seqn[i], str1, h_base) == 0 &&
	 strncmp(&seqn[i+h_base+arb_base], str2, h_base) == 0){
	ct ++;
      }
    }
    printf("%d ", ct);

    for(i = 0,j = h_base - 1;j >= 0;i ++, j --)
      if(comp_match(str1[i], str2[j]) == 0)break;
    if(j < 0)printf("palindrome ");
    else putchar(' ');

    for(i = 0,j = h_base - 1;j >= 0;i ++, j --)
      if(cmpl(str1[i]) !=  str2[j])break;
    if(j < 0)printf("P");
    else putchar(' ');

    putchar('\n');

    if(next_patsp(str1, h_base) == 1){
      for(i = 0;i < h_base;i ++)str1[i] = 'a'; str1[i] = '\0';
      if(next_patsp(str2, h_base) == 1)break;
    }


  }

  /* comp_match(seqn[p],seqn[q]); */

}

void palind4_fin(){


}

void palind4_help(){

  printf("-palind4\t Counts all the palindrome sequence with some arbitrary bases: State size and number of arbitrary sequence inside\n"); 

}
