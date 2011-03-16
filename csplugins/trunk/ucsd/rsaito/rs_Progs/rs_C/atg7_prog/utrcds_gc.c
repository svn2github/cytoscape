#include <stdio.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

int utrcds_gc_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-utrcds_gc") == 0){
    return 1;
  }
  else return 0;
}

void utrcds_gc_head(char *line){

}

void utrcds_gc_ent(struct gparam *entry_info, char seqn[], int max,
              struct cds_info cds[], int ncds){

  int i,j,k,m,n, n_gc;
  char *compseqn;

/*  compseqn = compseqget(seqn, max); */

  for(i = 0;i < 1;i ++){ /* i < ncds */
    if(valid_cds[i] == 0){ continue; }
    if(cds[i].complement == 0){
      for(n_gc = 0, j = 1; j < cds[i].cds_start;j ++)
	if(seqn[j - 1] == 'c' || seqn[j - 1] == 'g')n_gc ++;
      printf("%s\t%d\t%d\t%.2lf", cds[i].gene, n_gc, cds[i].cds_start - 1,
	     100.0 * n_gc / (cds[i].cds_start - 1));

      for(n_gc = 0, j = cds[i].cds_start;j <= cds[i].cds_end;j ++)
	if(seqn[j - 1] == 'c' || seqn[j - 1] == 'g')n_gc ++;
      printf("\t%d\t%d\t%.2lf", n_gc, cds[i].cds_end - cds[i].cds_start + 1,
	     100.0 * n_gc / (cds[i].cds_end - cds[i].cds_start + 1));

      for(n_gc = 0, j = cds[i].cds_end + 1;j <= max;j ++)
	if(seqn[j - 1] == 'c' || seqn[j - 1] == 'g')n_gc ++;
      printf("\t%d\t%d\t%.2lf\n", n_gc, max - cds[i].cds_end,
	     100.0 * n_gc / (max - cds[i].cds_end));
      
    }
    /* Complement under construction!!
    else for(j = cds[i].cds_start;j <= cds[i].cds_end; j++){
      nn ++;
      switch(seqn[j - 1]){
      case 'a':nt ++;break;
      case 't':na ++;break;
      case 'c':ng ++;break;
      case 'g':nc ++;break;
      }
    }
    */
  }

/*  free(compseqn); */
}

void utrcds_gc_fin(){

}

void utrcds_gc_help(){

  printf("-utrcds_gc\t GC contents of 5'UTR, CDS and 3'UTR. No complement accepted.\n");

}


		       
