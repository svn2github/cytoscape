#include <stdio.h>

static char pat[20];
static int from, to;

int patfind_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n], "-patfind") == 0){
    from = atoi(argv[n + 1]);
    to   = atoi(argv[n + 2]);
    strcpy(pat, argv[n + 3]);
    return 4;
  }
  else return 0;
}

void patfind_head(char *line){

}

void patfind_ent(char *entry, char seqn[], int max, 
		 struct cds_info cds[], int ncds)
{
  int i, j, k;
  char *compseqn;
  compseqn = (char *)(malloc(max * sizeof(char)));
  for(i = max;i > 0;i --)compseqn[max - i] = cmpl(seqn[i - 1]);

  for(i = 0;i < ncds;i ++){
    if(cds[i].complement == 0){
      if(cds[i].cds_start > 0)


    }
    else {


    }
  }

  free(compseqn);
}

void patfind_fin(){


}

void patfind_help(){

  printf("-patfind\t Finds pattern and displays gene and product\n");

}


