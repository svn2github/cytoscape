#include <stdio.h>
#include <string.h>
#include "global_st.h"

static char inp_pat[1000];

int spsim_par(int argc, char *argv[], int n)
{
  if(strcmp(argv[n],"-spsim") == 0){
    return 1;
  }
  else return 0;
}

void spsim_head(char *line){

}

void spsim_ent(char *entry, char seqn[], int max, 
		struct cds_info cds[], int ncds){
  int i,j,k,m,n;
  printf("Input sequence pattern from atg:");
  scanf("%s",inp_pat);
  for(i = 0;i < max + 1 - strlen(inp_pat);i ++){
    if(strncmp(&seqn[i],inp_pat,strlen(inp_pat)) == 0){
      printf("From %d:\n",i + 1);
      for(j = i;j < max - 2;j ++){
	putchar(seqn[j]);
	if((j - i) % 3 == 0 && (strncmp(&seqn[j],"cac",3) == 0 ||
	   strncmp(&seqn[j],"cat",3) == 0 ||
	   strncmp(&seqn[j],"XXX",3) == 0)){
	  putchar(seqn[j + 1]); putchar(seqn[j + 2]);
	  break;
	}
      }
      printf("\nTerminal %d:\n",j + 2 + 1);
    }
  }
}

  
void spsim_fin(){

}

void spsim_help(){
  printf("-spsim\t Simple similarity search\n");
}

