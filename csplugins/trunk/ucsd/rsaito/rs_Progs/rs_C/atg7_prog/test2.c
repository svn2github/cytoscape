#include <stdio.h>

#define ONE_CHAR '.'
#define ZON_CHAR '-'

int spmatch(char *, char *);
int spmatchn(char *, char *, int);

/* If the pattern specified by pat mathes to the head of seq, 1 will be 
   returned. Otherwise, 0 will be returned. */
int spmatch(char seq[], char pat[])
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

int spmatchn(char seq[], char pat[], int n)
{
  static char seq1[30], pat1[30];
  int i;

  if(n > 30){
    fprintf(stderr, "Error in function spmatchn.\n");
    exit(1);
  }

  strncpy(seq1, seq, n); seq1[n] = '\0';
  strncpy(pat1, pat, n); pat1[n] = '\0';

  return spmatch(seq1, pat1);
}

main(){

  int n;
  static char seq[20], pat[20];

  printf("Input sequence:");
  scanf("%s", seq);

  printf("Input pattern:");
  scanf("%s", pat);

  printf("Input number:");
  scanf("%d", &n);

  printf("%d\n", spmatchn(seq, pat, n));

}
