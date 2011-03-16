#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"

static int second_flag = 0;
static char *obj_seq;
static int seq_total;
static char orf[10000];

int narasen_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-narasen") == 0){
    return 1;
  }
  else return 0;
}

void narasen_head(char *line){

}

void narasen_ent(char *entry, char seqn[], int max, 
	       struct cds_info cds[], int ncds)
{
  int i,j,k,m,n;
  if(second_flag){
    printf("%s",entry);
    for(i = 0;i < ncds;i ++){
      if(cds[i].cds_start <= 0 || cds[i].cds_end <= 0 ||
	 cds[i].cds_start >= cds[i].cds_end)continue;
      for(j = 0;j < seq_total;j ++){
	if(strncmp(&seqn[ cds[i].cds_start - 1], &obj_seq[j],
		   cds[i].cds_end - cds[i].cds_start + 1) == 0){
	  printf("[%d:%d-%d] ",i,cds[i].cds_start, cds[i].cds_end);
	  if(cds[i].complement == 0){
	    printf("%d..%d ",j + 1, 
		   j + 1 + cds[i].cds_end - cds[i].cds_start );
	  }
	  else printf("complement(%d..%d) ", j + 1, 
		 j + 1 + cds[i].cds_end - cds[i].cds_start );
	  printf("/gene=\"%s\" ",cds[i].gene);
	  printf("/product=\"%s\" ",cds[i].product);
	  putchar('\n');
	}
      }
    }
  }
  
  else {
    printf("Reading sequence into memory...\n");
    obj_seq = (char *)malloc(max);
    seq_total = max;
    strncpy(obj_seq, seqn, max);
    second_flag = 1;
  }
}

void narasen_fin(){

  if(second_flag)free(obj_seq);

}

void narasen_help(){

  printf("-narasen\t Comparative gene search\n");

}
