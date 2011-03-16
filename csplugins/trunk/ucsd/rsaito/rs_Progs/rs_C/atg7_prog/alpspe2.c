#include <stdio.h>
#include <string.h>

/* if over, 0 will be returned and pat will be broken. */
int next_patsp(char pat[], int max){
  int i,j;

  for(i = 0;i < strlen(pat);i ++){
    switch(pat[i]){
      case 'a':pat[i] = 1;break;
      case 't':pat[i] = 2;break;
      case 'c':pat[i] = 3;break;
      case 'g':pat[i] = 4;break;
      case '.':pat[i] = 5;break;
      case '-':pat[i] = 6;break;
      case '\0':pat[i] = 0; break;
      }
  }

  i = 0;
  while(1){
    if(pat[i] == 0){
      if(strlen(pat) >= max)return 1;
      else { pat[i] = 1; pat[i + 1] = 0; break; }
    }
    pat[i] ++;
/*
    if(i == 0 || i == strlen(pat) - 1){
      if(pat[i] > 4)pat[i] = 1; 
      else { break; }
    }

    else */ if(pat[i] > 6)pat[i] = 1;
    else { break; }

    i ++;
  }

  for(i = 0;i < strlen(pat);i ++){
    switch(pat[i]){
      case 1:pat[i] = 'a';break;
      case 2:pat[i] = 't';break;
      case 3:pat[i] = 'c';break;
      case 4:pat[i] = 'g';break;
      case 5:pat[i] = '.';break;
      case 6:pat[i] = '-';break;
      case 0:pat[i] = '\0';break;
      }
  }
  return 0;

}


main(){

  char pat[10];
  int i;

  for(i = 0;i < 10;i ++)pat[i] = 0;

  for(i = 0;i < 20736;i ++){
    if(next_patsp(pat, 4) == 1)break;
    else printf("%4d:%s\n",i,pat);
  }

}

