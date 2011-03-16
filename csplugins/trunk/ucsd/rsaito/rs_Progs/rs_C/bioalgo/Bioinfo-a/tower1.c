#include <stdio.h>

char remaining(char a, char b){

  if((a == 'A' && b == 'B') || (a == 'B' && b == 'A')){ return 'C'; }
  if((a == 'A' && b == 'C') || (a == 'C' && b == 'A')){ return 'B'; }
  if((a == 'B' && b == 'C') || (a == 'C' && b == 'B')){ return 'A'; }

}

void transit(char from, char to, int num){

  int using;

  if(num == 1){ printf("%c -> %c\n", from, to); }
  else {
    using = remaining(from, to);
    transit(from, using, num - 1);
    transit(from, to, 1);
    transit(using, to, num - 1);
  }

}


main(){

  transit('A', 'B', 3);


}

