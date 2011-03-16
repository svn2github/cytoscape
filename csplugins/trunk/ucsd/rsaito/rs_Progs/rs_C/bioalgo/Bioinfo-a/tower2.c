#include <string.h>
#include <stdio.h>

enum pole { A = 0, B, C };

static char a_pole[10];
static char b_pole[10];
static char c_pole[10];

static char a_num, b_num, c_num;

char remaining(char a, char b){

  if((a == 'A' && b == 'B') || (a == 'B' && b == 'A')){ return 'C'; }
  if((a == 'A' && b == 'C') || (a == 'C' && b == 'A')){ return 'B'; }
  if((a == 'B' && b == 'C') || (a == 'C' && b == 'B')){ return 'A'; }

}

void transit(char from, char to, int num){

  int using;
  char plate;

  if(num == 1){
    switch(from){
    case 'A': plate = a_pole[a_num]; a_pole[a_num] = '\0'; a_num --; break;
    case 'B': plate = b_pole[b_num]; b_pole[b_num] = '\0'; b_num --; break;
    case 'C': plate = c_pole[c_num]; c_pole[c_num] = '\0'; c_num --; break;
    }
    switch(to){
    case 'A': a_pole[++ a_num] = plate; break;
    case 'B': b_pole[++ b_num] = plate; break;
    case 'C': c_pole[++ c_num] = plate; break;
    }
    printf("%c -> %c\n", from, to);
    printf("A:%s\n", a_pole);
    printf("B:%s\n", b_pole);
    printf("C:%s\n", c_pole);
    putchar('\n');
  }
  else {
    using = remaining(from, to);
    transit(from, using, num - 1);
    transit(from, to, 1);
    transit(using, to, num - 1);
  }

}


main(){

  a_num = -1;
  b_num = -1;
  c_num = -1;

  strcpy(a_pole, "abcde");
  a_num = strlen(a_pole) - 1;

  transit('A', 'B', a_num + 1);

}

