#include <stdio.h>

#define ONE_CHAR '.'
#define ZON_CHAR '-'

/* If the pattern specified by pat mathes to the head of seq, 1 will be 
   returned. Otherwise, 0 will be returned. */
int spmatch(seq, pat)
char seq[], pat[];
{
  int i, j;
  int length;
  
  length = strlen(pat);
  for(i = 0;i < length;i ++){
    switch(pat[i]){
    case ONE_CHAR:
      if(seq[i] == '\0')return 0;
      break;
    case ZON_CHAR:
      if(spmatch(&seq[i], &pat[i + 1]) == 1)return 1;
      else if(seq[i] == '\0')return 0;
      break;
    default:
      if(pat[i] != seq[i])return 0;
      break;
    }
  }
  return 1;
}

main(){

  char pat1[30],pat2[30];

  while(1){
    printf("pattern 1:");
    scanf("%s",pat1);

    printf("pattern 2:");
    scanf("%s",pat2);

    printf("%d\n", spmatch(pat1, pat2));

  }

}
