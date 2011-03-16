#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"


int hfindpat_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-hfindpat") == 0){
    return 1; 
  }
  else return 0;

}

void hfindpat_head(char *line){

}

void hfindpat_ent(struct gparam *entry_info, char seqn[], int max,
              struct cds_info cds[], int ncds){
  int start_pos;
  int i,j,k;
  int upmax, dnmax;
  char c_flag[3];
  static char pat[100];
  char *comp_seq;

  comp_seq = compseqget(seqn, max);

  while(1){

    printf("Input start position:");
    scanf("%d", &start_pos);
    if(start_pos == 0)break;

    printf("Input maximum distance upstream:");
    scanf("%d", &upmax);
    
    printf("Input maximum distance downstream:");
    scanf("%d", &dnmax);
    
    printf("Input pattern:");
    scanf("%s", pat);

    printf("Direct/Complement(d/c):");
    scanf("%s", &c_flag);
    
    if(c_flag[0] != 'c')
      for(i = start_pos - upmax; i <= start_pos + dnmax; i ++){
	if(i < 1)continue;
	if(i > max)break;
	if(spmatch(&seqn[i - 1], pat) == 1){
	  printf("%8d:", i);
	  for(j = i - 50;j < i + strlen(pat) + 50; j ++){
	    if(j == i)putchar(' ');
	    if(j == i + strlen(pat))putchar(' ');
	    if(j > 0 && j <= max)putchar(seqn[j - 1]);
	    else putchar(' ');
	  }
	  printf(" %4d\n", i - start_pos);
	}
      }
    else {
      for(i = start_pos + upmax;i >= start_pos - dnmax;i --){
	if(i < 1)break;
	if(i > max)continue;
	if(spmatch(&comp_seq[ max - i ], pat) == 1){
	  printf("%8d:", i);
	  for(j = max - i + 1 - 50;j < max - i + 1 + 50 + strlen(pat);
	      j ++){
	    if(j == max - i + 1)putchar(' ');
	    if(j == max - i + 1 + strlen(pat))putchar(' ');
	    if(j > 0 && j <= max)putchar(comp_seq[j - 1]);
	    else putchar(' ');
	  }
	  printf(" %4d\n", start_pos - i);
	}
      }
    }
  }
  free(comp_seq);
}


void hfindpat_fin(){


}

void hfindpat_help(){

  printf("-hfindpat\t Interactive pattern finding mode\n");

}
