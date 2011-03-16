#include <string.h>
#include <stdio.h>

#define H 0
#define V 1
#define D 2
#define NDIR 3

#define INVALID 1000

#define Gap_Penalty -5
#define GAPM '-'

struct {
   int score;
   int direction;
} score_path_matrix[30][30];

char seq1[100], seq2[100];

void print_board(int m, int n){

  int i, j;

  for(i = 0;i <= m;i ++){
    for(j = 0;j <= n;j ++)printf("%2d ", score_path_matrix[i][j].score);
    putchar('\n');
  }

  putchar('\n');

  for(i = 0;i <= m;i ++){
    for(j = 0;j <= n;j ++)
      switch(score_path_matrix[i][j].direction){
      case H: printf("%2s ", "-"); break;
      case V: printf("%2s ", "|"); break;
      case D: printf("%2s ", "\\"); break;
      }
    putchar('\n');
  }
}


int score(char a, char b){

  if(a == b)return 10;
  else return -7;

}

int find_max_elem(int array[], int n){
  int i,max,index;

  for(max = array[0], index = 0, i = 1;i < n;i ++){
    if(max < array[i]){ max = array[i]; index = i; }
  }
  return index;

}

int find_max_score(int i, int j){

   static int score_tmp[NDIR];
   int max_dir;
   int max_score;
 
   if(score_path_matrix[i][j].score != INVALID){
     return score_path_matrix[i][j].score;
   }
   else if(i == 0){ 
     max_score = 0; max_dir = H;
   }
   else if(j == 0){
     max_score = 0; max_dir = V;
   }
   else {
     score_tmp[V] = find_max_score(i - 1, j) + Gap_Penalty;
     score_tmp[H] = find_max_score(i, j - 1) + Gap_Penalty;
     score_tmp[D] = find_max_score(i - 1, j - 1) + 
       score(seq1[i - 1], seq2[j - 1]);
 
     max_dir = find_max_elem(score_tmp, NDIR);
     max_score = score_tmp[max_dir];
   
   }

   score_path_matrix[i][j].score     = max_score;
   score_path_matrix[i][j].direction = max_dir;
   return max_score;

}

int make_score_path_matrix(int seq1_len, int seq2_len){

  static int score_tmp[NDIR];
  int max_dir;
  int max_score;

  int i,j;

  for(i = 0;i <= seq1_len;i ++){
    for(j = 0;j <= seq2_len;j ++){
      if(i == 0){ max_score = 0; max_dir = H; }
      else if(j == 0){ max_score = 0; max_dir = V; }
      else {
        score_tmp[V] = find_max_score(i - 1, j) + Gap_Penalty;
        score_tmp[H] = find_max_score(i, j - 1) + Gap_Penalty;
        score_tmp[D] = find_max_score(i - 1, j - 1) + 
          score(seq1[i - 1], seq2[j - 1]);
        
        max_dir = find_max_elem(score_tmp, NDIR);
        max_score = score_tmp[max_dir];
      }
      score_path_matrix[i][j].score     = max_score;
      score_path_matrix[i][j].direction = max_dir;
    }
  }
  
  return score_path_matrix[seq1_len][seq2_len].score;
  
}

void board_to_alignment(char a_seq1[], char a_seq2[], int m, int n){

  int i, j, p;
  char tmp;

  for(p = 0, i = m, j = n;
      i != 0 || j != 0;){
    switch(score_path_matrix[i][j].direction){
    case V:
      a_seq1[p] = seq1[ -- i];
      a_seq2[p] = GAPM;
      p ++; break;
    case H:
      a_seq1[p] = GAPM;
      a_seq2[p] = seq2[ -- j];
      p ++; break;
    case D:
      a_seq1[p] = seq1[ -- i];
      a_seq2[p] = seq2[ -- j];
      p ++; break;
    }

  }

  for(i = 0, j = p - 1;i < j;i ++, j--){
    tmp = a_seq1[i];
    a_seq1[i] = a_seq1[j];
    a_seq1[j] = tmp;

    tmp = a_seq2[i];
    a_seq2[i] = a_seq2[j];
    a_seq2[j] = tmp;
  }

  a_seq1[p] = '\0';
  a_seq2[p] = '\0';

}


main(){

  int i,j;
  int seq1_len, seq2_len;
  
  static char a_seq1[30], a_seq2[30];

  for(i = 0;i < 30;i ++)
    for(j = 0;j < 30;j ++)score_path_matrix[i][j].score = INVALID;

  printf("Initialization OK!\n");
  sleep(1);

  strcpy(seq1, "atgc");
  strcpy(seq2, "atcgc");

  seq1_len = strlen(seq1);
  seq2_len = strlen(seq2);
  
  printf("%d\n", make_score_path_matrix(seq1_len, seq2_len));
  print_board(seq1_len, seq2_len);

  board_to_alignment(a_seq1, a_seq2, seq1_len, seq2_len);
  printf("%s\n%s\n", a_seq1, a_seq2);

}

