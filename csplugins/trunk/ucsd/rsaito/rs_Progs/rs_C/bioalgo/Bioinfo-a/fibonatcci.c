#include <stdio.h>

#define MAX_REC 2000000

static int fib_rec[MAX_REC];

int fibonatcci(int n){
   int ret;

   if(fib_rec[n] > 0)return fib_rec[n];
   if(n == 1 || n == 2)ret = 1;
   else ret = fibonatcci(n - 1) + fibonatcci(n - 2);

   fib_rec[n] = ret;
   return ret; 

}

main(){

   int i;
   for(i = 0;i < MAX_REC;i ++)
      fib_rec[i] = -1;

   printf("%d\n", fibonatcci(5));

}
