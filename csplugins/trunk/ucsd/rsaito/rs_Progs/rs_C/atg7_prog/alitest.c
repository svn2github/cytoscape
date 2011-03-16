#include <stdio.h>

extern double sd_match(char [], char [],char [], char [], int *,
		       int [], int []);

void main(){

  static char seque1[3000],seque2[3000], result1[3000], result2[3000];
  int result_len;
  static int match_res1[3000], match_res2[3000];
  double score;
  int i,ct;

  printf("Sequence 1:");
  scanf("%s", seque1);
  printf("Sequence 2:");
  scanf("%s", seque2);

  score = sd_match(seque1, seque2, result1, result2, &result_len, 
		  match_res1, match_res2);
  
  ct = 0;
  for(i = 0;i < result_len;i ++)
    if(result1[i] == '-')putchar(' ');
  else {
    if(match_res1[ct] == 1)putchar('*');
    else putchar(' ');
    ct ++;
  }
  putchar('\n');

  for(i = 0;i < result_len;i ++)
    putchar(result1[i]);
  putchar('\n');

  for(i = 0;i < result_len;i ++)
    putchar(result2[i]);
  putchar('\n');

  ct = 0;
  for(i = 0;i < result_len;i ++)
    if(result2[i] == '-')putchar(' ');
  else {
    if(match_res2[ct] == 1)putchar('*');
    else putchar(' ');
    ct ++;
  }
  putchar('\n');

  printf("Score:%lf\n", score);

}

