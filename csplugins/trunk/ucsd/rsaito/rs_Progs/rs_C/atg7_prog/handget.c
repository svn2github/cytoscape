#include <stdio.h>
#include <string.h>
#include "global_st.h"
#include "atg_func.h"

#define OUT_LINE_LEN 60

int handget_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n],"-handget") == 0){
    return 1;
  }
  else return 0;
}

void handget_head(char *line)
{

}

void handget_ent(char *entry, char *seqn, int max, int cds[], int ncds)
{
  int begin, end;
  int i,j,k,tmp;

  printf("%s",entry);
  printf("%s",entry_info.source);
  printf(" *** Retracting sequence ***\n");

  printf("Begin:");
  scanf("%d",&begin);
  
  printf("End:");
  scanf("%d",&end);

  if(begin <= end){
    for(i = begin; i <= end;i += OUT_LINE_LEN){
      if(i + OUT_LINE_LEN - 1 <= end)
	printf("# %d - %d\n",i,i + (OUT_LINE_LEN - 1));
      else printf("# %d - %d\n",i,end);
      for(j = 0;j < OUT_LINE_LEN && i + j <= end;j ++){
	if(i + j > 0)putchar(seqn[i + j - 1]);
	else putchar('-');
      }
      putchar('\n');
    }
  }
	
  else {
    tmp = begin;begin = end;end = tmp;
    
    for(i = end; i >= begin;i -= OUT_LINE_LEN){
      if(i - (OUT_LINE_LEN - 1) > 0)
	printf("# %d - %d\n",i,i - (OUT_LINE_LEN - 1));
      else printf("# %d - %d\n",i,1);
      for(j = 0;j < OUT_LINE_LEN && i - j >= begin;j ++){
	if(i - j > 0){
	  switch(seqn[i - j - 1]){
	     case 'a':putchar('t');break;
	     case 't':putchar('a');break;
	     case 'c':putchar('g');break;
	     case 'g':putchar('c');break;
	     default:putchar(seqn[i - j - 1]);break;
	  }
	}
	else putchar('-');
/*	printf("%d:",i-j); */
      }
      putchar('\n');
    }
  }
}


void handget_fin()
{
}

void handget_help()
{
  printf("-handget\t Manual retraction of sequences\n");
}

