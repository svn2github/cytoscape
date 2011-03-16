#include <string.h>
#include <ctype.h>
#include <stdio.h>

#define DEBUG_LEVEL 0

#define H 0
#define V 1
#define D 2
#define J 3
#define NDIR 4
#define STOP 10

#define INVALID -99999
/* INVALID must be large negative number */

#define MATCH_SCORE 4
#define MATCH_n_SCORE 0
#define UNMATCH_SCORE -7
#define GAP_OPEN -15
#define GAP_EXT -0
#define INTRON_PENALTY -9

/* 2 x GAP_OPEN < UNMATCH_SCORE */
/* GAP_OPEN < INTRON_PENALTY < GAP_EXT */
/* 2 x INTRON_PENALTY < GAP_OPEN + GAP_EXT */

/* Recommended settings for isoforms
MATCH_SCORE 4
MATCH_n_SCORE 0
UNMATCH_SCORE -7
GAP_OPEN -15
GAP_EXT -0
INTRON_PENALTY -9
*/

#define MIN_INTRON_LEN 4 /* Do not set this value below 4 */
#define MIN_EXON_SCORE 100 /* This parameter is currently not in use */

#define GAPM '-'  /* GAP mark */
#define INTRM '~' /* GAP mark for unspliced intron */

#define MAX_SEQLEN 30000
#define MAX_ALEN 100000
#define LINE 1000

struct score_path_matrix {
  int score;
  int direction;
};

static char *seq1, *seq2;
/* Sequence patterns to be aligned */

static struct score_path_matrix **spm;
/* Matrix for maximum score upto each node */

static struct score_path_matrix **max_d_b, **max_h_b, **max_v_b;
/*
  Matrix for maximum score upto each node if optimal previous node
  is upper-left, left, up, respectively.
  max_?_b[][].direction shows which matrix (max_d_b, max_h_b max_v_b or
  max_j_b) were used for the optimal score calculation at the previous node.
*/

static struct score_path_matrix **max_j_b;
/*
  Matrix for maximun score upto each note if optimal previous node
  is "immature jump".
  max_j_b[][].direction should be "D".
*/

static int **j_prev_pos;
/* Previous position if "immature jump" is used. */

static int *gt_pos;
/* Positions where dinucleotide "gt" are found. */

static int *ag_pos_f;
/*
  Flags for positions where dinucleotide "ag" are found.
   0 = "ag" not found, 1 = "ag" found.
*/

/* Converts amino acid to codon */
void conv_to_nuc(char amino, char r[]){
  /* char r[4] must be prepared */

  switch(amino){
  case 'A': strcpy(r, "gcn"); break; 
  case 'C': strcpy(r, "tgn"); break; 
  case 'D': strcpy(r, "gan"); break; 
  case 'E': strcpy(r, "gan"); break; 
  case 'F': strcpy(r, "ttn"); break; 
  case 'G': strcpy(r, "ggn"); break; 
  case 'H': strcpy(r, "can"); break; 
  case 'I': strcpy(r, "atn"); break; 
  case 'K': strcpy(r, "aan"); break; 
  case 'L': strcpy(r, "ntn"); break; 
  case 'M': strcpy(r, "atg"); break; 
  case 'N': strcpy(r, "aan"); break; 
  case '/': strcpy(r, "tnn"); break; 
  case 'P': strcpy(r, "ccn"); break; 
  case 'Q': strcpy(r, "can"); break; 
  case 'R': strcpy(r, "ngn"); break; 
  case 'S': strcpy(r, "nnn"); break; 
  case 'T': strcpy(r, "acn"); break; 
  case 'V': strcpy(r, "gtn"); break; 
  case 'W': strcpy(r, "tgg"); break; 
  case 'Y': strcpy(r, "tan"); break; 
  default: strcpy(r, "???"); break; 
  }

}

/* Reads protein sequence and convert into nucleotide sequence. */
int readpro(char *filename, char proseq[]){
  int i, loc;
  static char line[LINE];
  FILE *fp;
  char r[4];

  if((fp = fopen(filename, "r")) == NULL){
    fprintf(stderr, "File \"%s\" not found.\n", filename);
    exit(1);
  }

  loc = 0;
  while(fgets(line, LINE, fp) != NULL){
    if(line[0] == '>'){ continue; }
    for(i = 0;line[i] != '\0';i ++){
      if(isupper(line[i])){
	conv_to_nuc(line[i], r);
	strncpy(&proseq[loc], r, 3);
	loc += 3;
      }
    }
  }
  proseq[loc] = '\0';
  fclose(fp);
  return loc;

}

/* Reads nucleic acid sequence. */
int readnuc(char *filename, char nucseq[]){
  int i, loc;
  static char line[LINE];
  FILE *fp;

  if((fp = fopen(filename, "r")) == NULL){
    fprintf(stderr, "File \"%s\" not found.\n", filename);
    exit(1);
  }

  loc = 0;
  while(fgets(line, LINE, fp) != NULL){
    if(line[0] == '>'){ continue; }
    for(i = 0;line[i] != '\0';i ++){
      if(isalpha(line[i])){
	if(isupper(line[i]))line[i] = tolower(line[i]);
	nucseq[loc] = line[i];
	loc ++;
      }
    }
  }

  nucseq[loc] = '\0';
  fclose(fp);
  return loc;

}


/* Reverse string. */
void string_rev(char str1[]){

  int i, j, len;
  char tmp;

  len = strlen(str1);
  for(i = 0, j = len - 1;i < j;i ++, j--){
    tmp = str1[i];
    str1[i] = str1[j];
    str1[j] = tmp;
  }
}

/* Print board information concering on its score and previous direction */
void print_board(struct score_path_matrix **p){

  int i, j;
  int m, n;

  m = strlen(seq1);
  n = strlen(seq2);
  
  printf("  "); for(i = 0;i < n;i ++)printf(" %c ", seq2[i]);
  putchar('\n');
  for(i = 0;i <= m;i ++){
      if(i < m)printf("%c ", seq1[i]);
      else printf("  ");
      for(j = 0;j <= n;j ++)printf("%2d ", p[i][j].score);
      putchar('\n');
  }

  putchar('\n');

  printf("  "); for(i = 0;i < n;i ++)printf(" %c ", seq2[i]);
  putchar('\n');
  for(i = 0;i <= m;i ++){
      if(i < m)printf("%c ", seq1[i]);
      else printf("  ");
      for(j = 0;j <= n;j ++)
	  switch(p[i][j].direction){
	      case H: printf("%2s ", "-"); break;
	      case V: printf("%2s ", "|"); break;
	      case D: printf("%2s ", "\\"); break;
	      case J: printf("%2s ", "J"); break;
	      case STOP: printf("%2s ", "*"); break;
	  }
      putchar('\n');
  }
  putchar('\n');
}

/* Calculates match/unmatch score */
int score(char a, char b){

  if(a == 'n' || b == 'n'){ return MATCH_n_SCORE; }
  if(a == b){ return MATCH_SCORE; }
  if(a != b){ return UNMATCH_SCORE; }

}

/* Finds where element having maximal value is localed. */
int find_max_elem(int array[], int n){
  int i,max,index;

  for(max = array[0], index = 0, i = 1;i < n;i ++){
    if(max < array[i]){ max = array[i]; index = i; }
  }
  return index;

}

int find_max_score(int i, int j){
  
  /* make GAP penalty 0 at the bottom */

   int score_tmp[NDIR];
   int max_dir;
   int max_score;

   if(DEBUG_LEVEL >= 10)printf("*** (%d, %d) ***\n", i, j);

   if(spm[i][j].score != INVALID){
     if(DEBUG_LEVEL >= 10)
       printf("There is a record! Score:%d\n", spm[i][j].score);
     return spm[i][j].score;
   }
   else if(i == 0){ 
     if(DEBUG_LEVEL >= 10)printf("At the top!\n");
     max_score = 0; max_dir = STOP;
   }
   else if(j == 0){
     if(DEBUG_LEVEL >= 10)printf("At the left!\n");
     max_score = 0; max_dir = STOP;
   }
   else {
     if(DEBUG_LEVEL >= 10)
       printf("Searching four direction from (%d %d)\n", i, j);

     score_tmp[D] = max_d(i, j);
     score_tmp[H] = max_h(i, j);
     score_tmp[V] = max_v(i, j);
     score_tmp[J] = max_j(i, j);

     if(DEBUG_LEVEL >= 10){
       printf("Searching result for (%d %d)\n", i, j);
       printf("From above: %d ", score_tmp[V]);
       printf("From left : %d ", score_tmp[H]);
       printf("From diago: %d ", score_tmp[D]);
       printf("From diago: %d ", score_tmp[D]);
     }

     max_dir = find_max_elem(score_tmp, NDIR);
     max_score = score_tmp[max_dir];

     if(max_score < 0){ 
       max_score = 0;
       max_dir = STOP;
     }

     if(DEBUG_LEVEL >= 10)
       printf("Max dir: %d Max score %d\n", max_dir, max_score);

   }

   if(DEBUG_LEVEL >= 10)printf("Final result for (%d %d)\n", i, j);
   if(DEBUG_LEVEL >= 10)
     printf("max score for (%d %d) is %d(%d)\n", i, j, max_score, max_dir);
   spm[i][j].score     = max_score;
   spm[i][j].direction = max_dir;
   return max_score;

}


int max_d(int i, int j){

   int score_tmp[NDIR];
   int max_dir, max_score;

   if(DEBUG_LEVEL >= 10)printf("*** D (%d, %d) ***\n", i, j);

   if(max_d_b[i][j].score != INVALID){
     if(DEBUG_LEVEL >= 10)printf("There is a record! Score:%d\n",
			   max_d_b[i][j].score);
     return max_d_b[i][j].score;
   }
   else if(i == 0){ 
     if(DEBUG_LEVEL >= 10)printf("D: At the top!\n");
     max_score = 0; max_dir = H;
   }
   else if(j == 0){
     if(DEBUG_LEVEL >= 10)printf("At the left!\n");
     max_score = 0; max_dir = V;
   }
   else {
     if(DEBUG_LEVEL >= 10)
       printf("Searching three direction from (%d %d)\n", i, j);
     score_tmp[D] = max_d(i - 1, j - 1) + score(seq1[i - 1], seq2[j - 1]);
     score_tmp[H] = max_h(i - 1, j - 1) + score(seq1[i - 1], seq2[j - 1]);
     score_tmp[V] = max_v(i - 1, j - 1) + score(seq1[i - 1], seq2[j - 1]);
     score_tmp[J] = max_j(i - 1, j - 1) + score(seq1[i - 1], seq2[j - 1]);

     if(DEBUG_LEVEL >= 10){
       printf("Searching result for D: (%d %d)\n", i, j);
       printf("From diago : %d ", score_tmp[D]);
       printf("From left  : %d ", score_tmp[H]);
       printf("From above : %d ", score_tmp[V]);
       printf("Splice jump: %d ", score_tmp[J]);
     }

     max_dir = find_max_elem(score_tmp, NDIR);
     max_score = score_tmp[max_dir];

     if(max_score < 0){ 
       max_score = 0;
       max_dir = STOP;
     }
     
     if(DEBUG_LEVEL >= 10)
       printf("Max dir: %d Max score %d\n", max_dir, max_score);

   }

   if(DEBUG_LEVEL >= 10){
     printf("Final result for D (%d %d)\n", i, j);
     printf("max score for (%d %d) is %d(%d)\n", i, j, max_score, max_dir);
   }

   max_d_b[i][j].score = max_score;
   max_d_b[i][j].direction = max_dir;
   return max_score;

}

int max_h(int i, int j){

   int score_tmp[NDIR];
   int max_score, max_dir;

   if(DEBUG_LEVEL >= 10)printf("*** H (%d, %d) ***\n", i, j);

   if(max_h_b[i][j].score != INVALID){
     if(DEBUG_LEVEL >= 10)
       printf("There is a record! Score:%d\n", max_h_b[i][j]);
     return max_h_b[i][j].score;
   }
   else if(i == 0){ 
     if(DEBUG_LEVEL >= 10)printf("H: At the top!\n");
     max_score = 0; max_dir = STOP;
   }
   else if(j == 0){
     if(DEBUG_LEVEL >= 10)printf("At the left!\n");
     max_score = 0; max_dir = STOP;
   }
   else {
     if(DEBUG_LEVEL >= 10)
       printf("Searching three direction from (%d %d)\n", i, j);
     score_tmp[D] = max_d(i, j - 1) + GAP_OPEN;
     score_tmp[H] = max_h(i, j - 1) + GAP_EXT;
     score_tmp[V] = INVALID;
     score_tmp[J] = INVALID;

     if(DEBUG_LEVEL >= 10){
       printf("Searching result for D: (%d %d)\n", i, j);
       printf("From diago : %d ", score_tmp[D]);
       printf("From left  : %d ", score_tmp[H]);
       printf("From above : %d ", score_tmp[V]);
       printf("Splice jump: %d ", score_tmp[J]);
     }

     max_dir = find_max_elem(score_tmp, NDIR);
     max_score = score_tmp[max_dir];

     if(max_score < 0){ 
       max_score = 0;
       max_dir = STOP;
     }

     if(DEBUG_LEVEL >= 10)
       printf("Max dir: %d Max score %d\n", max_dir, max_score);

   }

   if(DEBUG_LEVEL >= 10){
     printf("Final result for H (%d %d)\n", i, j);
     printf("max score for (%d %d) is %d(%d)\n", i, j, max_score, max_dir);
   }

   max_h_b[i][j].score = max_score;
   max_h_b[i][j].direction = max_dir;
   return max_score;

}


int max_v(int i, int j){

   int score_tmp[NDIR];
   int max_score, max_dir;

   if(DEBUG_LEVEL >= 10)
     printf("*** V (%d, %d) ***\n", i, j);

   if(max_v_b[i][j].score != INVALID){
     if(DEBUG_LEVEL >= 10)
       printf("There is a record! Score:%d\n", max_v_b[i][j]);
     return max_v_b[i][j].score;
   }
   else if(i == 0){ 
     if(DEBUG_LEVEL >= 10)printf("V: At the top!\n");
     max_score = 0; max_dir = STOP;
   }
   else if(j == 0){
     if(DEBUG_LEVEL >= 10)printf("V: At the left!\n");
     max_score = 0; max_dir = STOP;
   }
   else {
     if(DEBUG_LEVEL >= 10)
       printf("Searching three direction from (%d %d)\n", i, j);
     score_tmp[D] = max_d(i - 1, j) + GAP_OPEN;
     score_tmp[H] = INVALID;
     score_tmp[V] = max_v(i - 1, j) + GAP_EXT;
     score_tmp[J] = INVALID;
     
     if(DEBUG_LEVEL >= 10){
       printf("Searching result for V: (%d %d)\n", i, j);
       printf("From diago : %d ", score_tmp[D]);
       printf("From left  : %d ", score_tmp[H]);
       printf("From above : %d ", score_tmp[V]);
       printf("Splice jump: %d ", score_tmp[J]);
     }
     
     max_dir = find_max_elem(score_tmp, NDIR);
     max_score = score_tmp[max_dir];
     
     if(max_score < 0){ 
       max_score = 0;
       max_dir = STOP;
     }

     if(DEBUG_LEVEL >= 10)
       printf("Max dir: %d Max score %d\n", max_dir, max_score);

   }

   if(DEBUG_LEVEL >= 10){
     printf("Final result for H (%d %d)\n", i, j);
     printf("max score for (%d %d) is %d(%d)\n", i, j, max_score, max_dir);
   }
   

   max_v_b[i][j].score = max_score;
   max_v_b[i][j].direction = max_dir;
   return max_score;

}

int max_j(int i, int j){

   static int score_tmp[10000];
   int j_score, max_score, max_dir;
   int n_jump;
   int pos, j_pos;

   if(DEBUG_LEVEL >= 10)printf("*** J (%d, %d) ***\n", i, j);

   if(max_j_b[i][j].score != INVALID){
     if(DEBUG_LEVEL >= 10)
       printf("There is a record! Score:%d\n", max_j_b[i][j].score);
     return max_j_b[i][j].score;
   }

   else if(i == 0){ 
     if(DEBUG_LEVEL >= 10)printf("J: At the top!\n");
     max_score = INVALID; max_dir = STOP;
   }
   else if(j == 0){
     if(DEBUG_LEVEL >= 10)printf("J: At the left!\n");
     max_score = INVALID; max_dir = STOP;
   }
   else if(ag_pos_f[i] == 0){
     if(DEBUG_LEVEL >= 10)printf("J: No \"AG\" consensus!\n");
     max_score = INVALID; max_dir = STOP;
   }
   else {
     if(DEBUG_LEVEL >= 10)
       printf("Searching jump from (%d %d)\n", i, j);
     
     for(max_score = INVALID, j_pos = INVALID, n_jump = 0;
	 gt_pos[n_jump] + MIN_INTRON_LEN <= i &&
	 gt_pos[n_jump] < i && (pos = gt_pos[n_jump]) != INVALID;
	 n_jump ++){
       j_score = max_d(pos, j) + INTRON_PENALTY + (i - pos - 1)*GAP_EXT;
       if(DEBUG_LEVEL >= 10){
	 printf("Jump from %d to %d is %d + %d = %d\n",
		pos, i, max_d(pos, j), 
		INTRON_PENALTY + (i - pos - 1)*GAP_EXT,	j_score);
       }
       if(max_score < j_score){
	 max_score = j_score;
	 j_pos = pos;
       }
     }
   }
   
   if(max_score < 0){ 
     max_score = INVALID - 1;
     max_dir = STOP;
   }

   max_j_b[i][j].score = max_score;
   max_j_b[i][j].direction = D;
   j_prev_pos[i][j] = j_pos;
   
   if(DEBUG_LEVEL >= 10)
     printf("J: Max score (%d %d) -> (%d %d) : %d\n",
	  j_pos, j, i, j, max_score);

   return max_score;

}

int board_to_alignment(char a_seq1[], char a_seq2[],
		       int *a_start1, int *a_start2, 
		       int *a_end1, int *a_end2, int *intron){

  int i, j, k, p;
  int i_len, j_len, max_score = -1000;
  int m, n;
  struct score_path_matrix **b_p;
  int mode;
  char tmp;

  static struct score_path_matrix **b_pa[NDIR];
  b_pa[D] = max_d_b;
  b_pa[H] = max_h_b;
  b_pa[V] = max_v_b;
  b_pa[J] = max_j_b;

  i_len = strlen(seq1);
  j_len = strlen(seq2);

  for(i = 0;i <= i_len;i ++)for(j = 0;j <= j_len;j ++){
    if(spm[i][j].score > max_score){
      max_score = spm[i][j].score;
      m = i;
      n = j;
    }
  }
  if(DEBUG_LEVEL >= 3){
    printf("Local alignment start: (%d %d) = %d\n", m, n, max_score);
  } /* Trace back start */

  /*   This configuration is for global alignment.
  m = i_len;
  n = j_len; 
  */

  *a_end1 = m;
  *a_end2 = n;

  mode = spm[m][n].direction;
  b_p = b_pa[mode];
  *intron = 0;

  for(p = 0, i = m, j = n;
      (i > 0 && j > 0) && b_p[i][j].direction != STOP;){
    if(DEBUG_LEVEL >= 3)printf("mode %d :(%d %d) -> %d\n",
			       mode, i, j, b_p[i][j].direction);
    switch(mode){
    case D:
      a_seq1[p] = seq1[i - 1];
      a_seq2[p] = seq2[j - 1];
      mode = b_p[i][j].direction; /* "mode" must be changed before changing
				     i or j */
      b_p = b_pa[mode];
      p ++; i --; j --;
      break;
    case H:
      a_seq1[p] = GAPM;
      a_seq2[p] = seq2[j - 1];
      mode = b_p[i][j].direction;
      b_p = b_pa[mode];
      p ++; j --;
      break;
    case V:
      a_seq1[p] = seq1[i - 1];
      a_seq2[p] = GAPM;
      mode = b_p[i][j].direction;
      b_p = b_pa[mode];
      p ++; i --; 
      break;
    case J:
      (*intron) ++;
      for(k = i;k > j_prev_pos[i][j];k --){
	a_seq1[p] = seq1[k - 1];
	a_seq2[p] = INTRM;
	p ++;
      }
      mode = b_p[i][j].direction; /* Expected to be always "D" */
      b_p = b_pa[mode];
      i = j_prev_pos[i][j];
      break;
    }
  }

  *a_start1 = i;
  *a_start2 = j;

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

  return max_score;

}

void print_alignment(char *a_seq1, char *a_seq2,
		     int a_start1, int a_start2, int width){
  
  int i, c_point;
  int a_len;
  int cpos1, cpos2;

  a_len = strlen(a_seq1);

  cpos1 = a_start1;
  cpos2 = a_start2;

  c_point = 0;
  while(c_point < a_len){
    printf("%5d ", cpos1);
    for(i = c_point;i < c_point + width && i < a_len;i ++){
      putchar(a_seq1[i]);
      if(a_seq1[i] != GAPM && a_seq1[i] != INTRM)cpos1 ++;
    }
    printf("%5d ", cpos1);
    putchar('\n');

    printf("      ");
    for(i = c_point;i < c_point + width && i < a_len;i ++){ 
      if((a_seq1[i] == 'n' && a_seq2[i] != GAPM && a_seq2[i] != INTRM) ||
	 (a_seq1[i] != GAPM && a_seq1[i] != INTRM && a_seq2[i] == 'n'))
	putchar('.');
      else if(a_seq1[i] == a_seq2[i])putchar('|');
      else putchar(' ');
    }
    putchar('\n');

    printf("%5d ", cpos2);
    for(i = c_point;i < c_point + width && i < a_len;i ++){ 
      putchar(a_seq2[i]);
      if(a_seq2[i] != GAPM && a_seq2[i] != INTRM)cpos2 ++;
    }
    printf("%5d ", cpos2);
    putchar('\n');
    putchar('\n');
    c_point = i;
  }  

}

struct score_path_matrix **make_score_path_matrix(m, n){

  int i, j;
  struct score_path_matrix **p;

  p = (struct score_path_matrix **)
    malloc((m)*sizeof(struct score_path_matrix *));

  for(i = 0;i < m;i ++){
    p[i] = (struct score_path_matrix *)
      malloc(n*sizeof(struct score_path_matrix));
  }

  for(i = 0;i < m;i ++)for(j = 0;j < n;j ++){
    p[i][j].score = INVALID;
    p[i][j].direction = INVALID;
  }

  return p;

}

void destroy_score_path_matrix(struct score_path_matrix **p, int m){
  
  int i;

  for(i = 0;i < m;i ++){ free(p[i]); }
  free(p);
}

void initialize(char *s1, char *s2){

  int i,j;
  int i_len, j_len;

  i_len = strlen(s1);
  j_len = strlen(s2);

  seq1 = (char *)malloc((i_len+1) * sizeof(char));
  seq2 = (char *)malloc((j_len+1) * sizeof(char));
  
  strcpy(seq1, s1);
  strcpy(seq2, s2);

  if(DEBUG_LEVEL >= 10)
    printf("2 sequences with length %d and %d allocated.\n", i_len, j_len);

  spm = make_score_path_matrix(i_len+1, j_len+1);
  max_d_b = make_score_path_matrix(i_len+1, j_len+1);
  max_h_b = make_score_path_matrix(i_len+1, j_len+1);
  max_v_b = make_score_path_matrix(i_len+1, j_len+1);
  max_j_b = make_score_path_matrix(i_len+1, j_len+1);

  if(DEBUG_LEVEL >= 10)printf("%d x %d matrix allocated!\n", i_len, j_len);

  j_prev_pos = (int **)malloc((i_len+1)*sizeof(int *));
  for(i = 0;i <= i_len;i ++)
    j_prev_pos[i] = (int *)malloc((j_len+1)*sizeof(int));

  gt_pos = (int *)malloc((i_len+1)*sizeof(int));
  ag_pos_f = (int *)malloc((i_len+1)*sizeof(int));
  
  for(i = 0, j = 0;i <= i_len - 2;i ++)
    if(strncmp(&seq1[i], "gt", 2) == 0)gt_pos[j ++] = i;
  gt_pos[j] = INVALID;
  for(i = 0;i <= i_len;i ++){
    if(i < 2)ag_pos_f[i] = 0;
    else if(strncmp(&seq1[i - 2], "ag", 2) == 0)ag_pos_f[i] = 1;
    else ag_pos_f[i] = 0;
  }

}

void terminate(){

  int i;
  int i_len;

  i_len = strlen(seq1);

  free(seq1);
  free(seq2);

  destroy_score_path_matrix(spm, i_len + 1);
  destroy_score_path_matrix(max_d_b, i_len + 1);
  destroy_score_path_matrix(max_h_b, i_len + 1);
  destroy_score_path_matrix(max_v_b, i_len + 1);
  destroy_score_path_matrix(max_j_b, i_len + 1);

  for(i = 0;i <= i_len;i ++)free(j_prev_pos[i]);
  free(j_prev_pos);

  free(gt_pos);
  free(ag_pos_f);

}

int **make_int_matrix(int m, int n){

  int i, j;
  int **matrix;

  matrix = (int **)malloc((m+1)*sizeof(int *));
  for(i = 0;i <= m;i ++)
    matrix[i] = (int *)malloc((n+1)*sizeof(int));
  return matrix;

}

void delete_int_matrix(int **matrix, int m, int n){

  int i;
  for(i = 0;i <= m;i ++)free(matrix[i]);
  free(matrix);

}

main(int argc, char *argv[]){

  int i, j;
  static char s1[MAX_SEQLEN], s2[MAX_SEQLEN];
  static char a_seq1[MAX_ALEN], a_seq2[MAX_ALEN];
  int a_start1, a_start2;
  int a_end1, a_end2;
  int s1_len, s2_len;
  int score, max_score;
  int intron;

  s1_len = readnuc(argv[1], s1);
  s2_len = readpro(argv[2], s2);
  /* s2_len = readnuc(argv[2], s2); */

  initialize(s1, s2);

  for(i = 0;i <= s1_len;i ++)
    for(j = 0;j <= s2_len;j ++){
	score = find_max_score(i, j);
		if(DEBUG_LEVEL >= 10)printf("(%d %d) = %d\n", i, j, score);
	}

  if(DEBUG_LEVEL >= 3){
    printf("Max score calculation finished.\n");
    printf("Max score table\n");
    print_board(spm);
    printf("Max diagonal score table\n");
    print_board(max_d_b);
    printf("Max horizontal score table\n");
    print_board(max_h_b);
    printf("Max vertical score table\n");
    print_board(max_v_b);
  }

  max_score = board_to_alignment(a_seq1, a_seq2,
			      &a_start1, &a_start2, &a_end1, &a_end2,
			      &intron);

  /*
  print_alignment(a_seq1, a_seq2, a_start1, a_start2, 60);
  printf("Score: %d\n", max_score);
  */
  printf("Introns: %d\n", intron);

  terminate();

}

