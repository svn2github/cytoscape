#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

#include "global_st.h"
#include "atg_func.h"

static char pat[10];
static char db_xref[5000][20];
static int num_cds;

int ec970506_par(int argc, char *argv[], int n){

  if(strcmp(argv[n], "-ec970506") == 0){
    strcpy(pat, argv[n + 1]);
    num_cds = 0;
    return 2;
  }
  else return 0;

}

void ec970506_head(char *line){
  int n, i, j;
  static int reading_cds;
  n = 0;

  for(i = 0;i < 21;i ++)if(line[i] != ' ')break;
  if(i < 21)reading_cds = 0;
  if(strncmp(&line[21], "/partial", 8) == 0)reading_cds = 0;
  if(strncmp(&line[5], "CDS", 3) == 0)reading_cds = 1;

  if(reading_cds == 0)return;

  if(strncmp("/db_xref=", &line[21], strlen("/db_xref=")) == 0){
    for(i = 35, j = 0; line[i] != '"' && line[i] != '\0';i ++,j++)
      db_xref[num_cds][j] = line[i];
    db_xref[num_cds][j] = '\0'; /* printf("%d:%s\n", num_cds, db_xref[num_cds]); */
    num_cds ++;
  }
}


void ec970506_ent(struct gparam *entry_info, char seqn[], int max,
	      struct cds_info cds[], int ncds){

  int i,j,k,m,n;
  char *compseqn;
  int count;

  compseqn = (char *)(malloc(max * sizeof(char)));
  for(i = max;i > 0;i --)compseqn[max - i] = cmpl(seqn[i - 1]);

  for(i = 0;i < ncds;i ++){
    if(cds[i].complement == 0){
      if(strncmp(&seqn[cds[i].cds_start - 1], pat, strlen(pat)) == 0)
	printf("/%s/{ print }\n", db_xref[i]);
    }
    else {
      if(strncmp(&compseqn[max - cds[i].cds_end], pat, strlen(pat)) == 0)
	printf("/%s/{ print }\n", db_xref[i]);
    }
  }

  free(compseqn);
}


void ec970506_fin(){


}

void ec970506_help(){

  printf("-ec970506\t Only used for E.Coli. State start codon pattern.\n");

}


