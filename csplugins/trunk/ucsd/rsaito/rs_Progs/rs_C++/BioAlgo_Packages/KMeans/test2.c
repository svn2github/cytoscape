#include <stdio.h>
#include <math.h>

/* converts v to string. point indicates how small the number will be
   calculated. ex.point = -2 ... down to 0.01 unit */
void doub_to_str(double v, int point, char str[]){

  int i,j,k,di;

  j = 0;
  if(v < 0){ v = -v; str[j] = '-'; j ++; }
  di = (int)log10(v);
  if(di < 0)di = 0;

  for(i = di;i >= point;i --){
/*    printf("%lf : ",v); */
    if(i == -1){ str[j] = '.'; j ++; }
    str[j] = (char)(v / pow(10.0, i)) + '0'; 
/*    putchar(str[j]); putchar('\n'); */
    j ++;
    v -= pow(10.0, i) * (int)(v / pow(10.0, i));
  }
  str[j] = '\0';
/*  printf("[%s]\n", str); */
}

main(){

  char str[20];
  int i;
  double v;

  while(1){
    scanf("%lf %d", &v, &i);
    doub_to_str(v, i, str);
    printf("[%s]\n", str);
  }

}
