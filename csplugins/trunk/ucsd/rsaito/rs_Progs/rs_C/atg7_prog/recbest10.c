#include <stdio.h>
#include <string.h>

struct recbest10 {
  double value;
  char str1[10];
  char str2[10];
};


int candrec(struct recbest10 *rcandidate, struct recbest10 r[], int rec_num){

  int i,j,k, nth;

  /* Investigate where *rcandidate can be in rec_num */

  for(i = rec_num - 1;i >= 0;i --){
    if(rcandidate->value <= r[i].value)break;
  }

  nth = i + 1; /* from 0th */
  if(nth >= rec_num)return 0;

  for(i = rec_num - 1;i >= nth + 1;i --){
    r[i].value = r[i - 1].value;
    strcpy(r[i].str1,r[i - 1].str1);
    strcpy(r[i].str2,r[i - 1].str2);
  }
  r[nth].value = rcandidate->value;
  strcpy(r[nth].str1, rcandidate->str1);
  strcpy(r[nth].str2, rcandidate->str2);
  return 1;

}

void main(){

#define MAX_REC 5

  int i,j;
  double k;
  char str1[10], str2[10];

  struct recbest10 r[MAX_REC], rcand;

  for(i = 0;i < MAX_REC;i ++){
    r[i].value = 0.0;
    r[i].str1[0] = '\0';
  }

  while(1){
    scanf("%lf %s", &k, str1);
    rcand.value = k;
    strcpy(rcand.str1,str1);

    printf("%d\n", candrec(&rcand, r, MAX_REC));

    for(i = 0;i < MAX_REC;i ++){
      printf("%d : %lf %s\n", i, r[i].value, r[i].str1);
    }

  }
}


