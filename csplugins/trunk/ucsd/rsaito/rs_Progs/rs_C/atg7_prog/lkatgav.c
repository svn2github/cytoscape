#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "global_st.h"
#include "atg_func.h"

static char pat[20];
static int pat_dist_total_up, pat_dist_total_down;
static int num_sample_up, num_sample_down;


int lkatgav_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-lkatgav") == 0){
    strcpy(pat, argv[n + 1]);
    pat_dist_total_up = 0;
    pat_dist_total_down = 0;
    num_sample_up = 0;
    num_sample_down = 0;
    return 2;
  }
  else return 0;
}

void lkatgav_head(char *line){

}

void lkatgav_ent(struct gparam *entry_info, char seqn[], int max,
		 struct cds_info cds[], int ncds){
  int i,j,k,n;
  char *compseqn;
  compseqn = compseqget(seqn, max);
/*
  printf("Looking entry %s", entry_info->entry_line);
*/
  for(n = 0;n < ncds;n ++){
    if(valid_cds[n] == 0)continue;
    if(cds[n].complement == 0 && cds[n].cds_start > 0){
      for(i = cds[n].cds_start - strlen(pat);i > 0;i --)
	if(strncmp(pat, &seqn[i - 1], strlen(pat)) == 0){
	  pat_dist_total_up += i - cds[n].cds_start;
	  num_sample_up ++;
/*
	  printf("In %dth CDS:pattern %s found %d bases from start codon.\n",
		 n, pat, i - cds[n].cds_start);
*/
	  break;
	}
      for(i = cds[n].cds_start + 3;i < max;i ++)
	if(strncmp(pat, &seqn[i - 1], strlen(pat)) == 0){
	  pat_dist_total_down += i - cds[n].cds_start;
	  num_sample_down ++;
/*
	  printf("In %dth CDS:pattern %s found %d bases from start codon.\n",
		 n, pat, i - cds[n].cds_start);
*/
	  break;
	}
    }
    else if(cds[n].complement == 1 && cds[n].cds_end > 0){
      for(i = max - cds[n].cds_end + 1 - strlen(pat);i > 0;i --)
	if(strncmp(pat, &compseqn[i - 1], strlen(pat)) == 0){
	  pat_dist_total_up += i - ( max - cds[n].cds_end + 1);
	  num_sample_up ++;
/*
	  printf("In %dth CDS:pattern %s found %d bases from start codon.\n",
		 n, pat, i - ( max - cds[n].cds_end + 1));
*/
	  break;
	}
      for(i = max - cds[n].cds_end + 1 + 3;i < max;i ++)
	if(strncmp(pat, &compseqn[i - 1], strlen(pat)) == 0){
	  pat_dist_total_down += i - ( max - cds[n].cds_end + 1);
	  num_sample_down ++;
/*
	  printf("In %dth CDS:pattern %s found %d bases from start codon.\n",
		 n, pat, i - ( max - cds[n].cds_end + 1));
*/
	  break;
	}
    }
  }
  free(compseqn);
}

void lkatgav_fin(){

  printf("Total distance upstream ... %d ( %d )\n",
	 pat_dist_total_up, num_sample_up);
  printf("Total distance downstream ... %d ( %d )\n",
	 pat_dist_total_down, num_sample_down);
}

void lkatgav_help(){

  printf("-lkatgav\t Calculates average distance between start codons and ");
  printf("specified pattern that is nearest to start codon\n");

}
