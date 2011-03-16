#include<stdio.h>

int factorial(int n){
   if(n==1)n=1;
else n=n*factorial(n-1);
return n;
}
main(){
int a;
a=factorial(3);
printf("%d\n",a);
}

