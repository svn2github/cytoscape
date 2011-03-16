#include <stdio.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

static int na, nt, nc, ng, nn;

int atcgcon_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-atcgcon") == 0){
    nn = na = nt = nc = ng = 0;
    return 1;
  }
  else return 0;
}

void atcgcon_head(char *line){

}

void atcgcon_ent(struct gparam *entry_info, char seqn[], int max,
              struct cds_info cds[], int ncds){

  int i,j,k,m,n;
  char *compseqn;

/*  compseqn = compseqget(seqn, max); */

  for(i = 0;i < ncds;i ++){
    if(cds[i].complement == 0)
      for(j = cds[i].cds_start;j <= cds[i].cds_end;j ++){
	nn ++;
	switch(seqn[j - 1]){
	case 'a':na ++;break;
	case 't':nt ++;break;
	case 'c':nc ++;break;
	case 'g':ng ++;break;
	}
      }
    else for(j = cds[i].cds_start;j <= cds[i].cds_end; j++){
      nn ++;
      switch(seqn[j - 1]){
      case 'a':nt ++;break;
      case 't':na ++;break;
      case 'c':ng ++;break;
      case 'g':nc ++;break;
      }
    }
  }

/*  free(compseqn); */
}

void atcgcon_fin(){

  printf("a:%10d / %10d = %2.2lf%%\n", na, nn, 100.0*na/nn);
  printf("t:%10d / %10d = %2.2lf%%\n", nt, nn, 100.0*nt/nn);
  printf("c:%10d / %10d = %2.2lf%%\n", nc, nn, 100.0*nc/nn);
  printf("g:%10d / %10d = %2.2lf%%\n", ng, nn, 100.0*ng/nn);

  printf("GC content:%.2lf%%\n", 100.0*(nc + ng) / nn);

}

void atcgcon_help(){

  printf("-atcgcon\t Displays rate of ATCG in coding region\n");

}


		       
