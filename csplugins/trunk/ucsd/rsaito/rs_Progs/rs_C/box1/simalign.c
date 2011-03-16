#include <stdio.h>

#define FIRSTGAP -8
#define GAP 0
#define OUTGAP 0

/* アミノ酸のスコア計算関数群 */
int change(char c)
{
  char amino[23] = "ARNDCQEGHILKMFPSTWYVBZX";

  int i;

  for(i = 0; i < 23; i++)
    if(c == amino[i]) return(i);
  return 0;
}

int point(int x, int y)
{
  int p[23][23] = 
{{ 2,-2, 0, 0,-2, 0, 0, 1,-1,-1,-2,-1,-1,-4, 1, 1, 1,-6,-3, 0, 0, 0, 0},
 {-2, 6, 0,-1,-4, 1,-1,-3, 2,-2,-3, 3, 0,-4, 0, 0,-1, 2,-4,-2,-1, 0, 0},
 { 0, 0, 2, 2,-4, 1, 1, 0, 2,-2,-3, 1,-2,-4,-1, 1, 0,-4,-2,-2, 2, 1, 0},
 { 0,-1, 2, 4,-5, 2, 3, 1, 1,-2,-4, 0,-3,-6,-1, 0, 0,-7,-4,-2, 3, 3, 0},
 {-2,-4,-4,-5,12,-5,-5,-3,-3,-2,-6,-5,-5,-4,-3, 0,-2,-8, 0,-2,-4,-5, 0},
 { 0, 1, 1, 2,-5, 4, 2,-1, 3,-2,-2, 1,-1,-5, 0,-1,-1,-5,-4,-2, 1, 3, 0},
 { 0,-1, 1, 3,-5, 2, 4, 0, 1,-2,-3, 0,-2,-5,-1, 0, 0,-7,-4,-2, 2, 3, 0},
 { 1,-3, 0, 1,-3,-1, 0, 5,-2,-3,-4,-2,-3,-5,-1, 1, 0,-7,-5,-1, 0,-1, 0},
 {-1, 2, 2, 1,-3, 3, 1,-2, 6,-2,-2, 0,-2,-2, 0,-1,-1,-3, 0,-2, 1, 2, 0},
 {-1,-2,-2,-2,-2,-2,-2,-3,-2, 5, 2,-2, 2, 1,-2,-1, 0,-5,-1, 4,-2,-2, 0},
 {-2,-3,-3,-4,-6,-2,-3,-4,-2, 2, 6,-3, 4, 2,-3,-3,-2,-2,-1, 2,-3,-3, 0},
 {-1, 3, 1, 0,-5, 1, 0,-2, 0,-2,-3, 5, 0,-5,-1, 0, 0,-3,-4,-2, 1, 0, 0},
 {-1, 0,-2,-3,-5,-1,-2,-3,-2, 2, 4, 0, 6, 0,-2,-2,-1,-4,-2, 2,-2,-2, 0},
 {-4,-4,-4,-6,-4,-5,-5,-5,-2, 1, 2,-5, 0, 9,-5,-3,-3, 0, 7,-1,-5,-5, 0},
 { 1, 0,-1,-1,-3, 0,-1,-1, 0,-2,-3,-1,-2,-5, 6, 1, 0,-6,-5,-1,-1, 0, 0},
 { 1, 0, 1, 0, 0,-1, 0, 1,-1,-1,-3, 0,-2,-3, 1, 2, 1,-2,-3,-1, 0, 0, 0},
 { 1,-1, 0, 0,-2,-1, 0, 0,-1, 0,-2, 0,-1,-3, 0, 1, 3,-5,-3, 0, 0,-1, 0},
 {-6, 2,-4,-7,-8,-5,-7,-7,-3,-5,-2,-3,-4, 0,-6,-2,-5,17, 0,-6,-5,-6, 0},
 {-3,-4,-2,-4, 0,-4,-4,-5, 0,-1,-1,-4,-2, 7,-5,-3,-3, 0,10,-2,-3,-4, 0},
 { 0,-2,-2,-2,-2,-2,-2,-1,-2, 4, 2,-2, 2,-1,-1,-1, 0,-6,-2, 4,-2,-2, 0},
 { 0,-1, 2, 3,-4, 1, 2, 0, 1,-2,-3, 1,-2,-5,-1, 0, 0,-5,-3,-2, 2, 2, 0},
 { 0, 0, 1, 3,-5, 3, 3,-1, 2,-2,-3, 0,-2,-5, 0, 0,-1,-6,-4,-2, 2, 3, 0},
 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

  return(p[x][y]);
}

int matrix1(char a, char b)
{
  return(point(change(a), change(b)) + 8);
}

/* アミノ酸のスコア計算群(ここまで) */


/* アラインメントのノードのデータ */
  struct align_node {
     int score;
     int direct;
   };


main()
{
#define UP 1
#define LEFT 2
#define UPLEFT 3

#define ADIMSZ 64


  int n,s,c,c1;
  int len1,len2;
  char k1,k2;

  int ul_score;
  struct align_node ndp[ADIMSZ][ADIMSZ];
  
  char amino1[ADIMSZ],amino2[ADIMSZ];

  int tracearc[300];

  char align_r1[150];
  char align_r2[150];

  printf("amino1:");
  scanf("%s",amino1);
  printf("amino2:");
  scanf("%s",amino2);

  len1 = strlength(amino1);
  len2 = strlength(amino2);

  
init_align_map(amino1,amino2,ndp);


calc_score_map(amino1,amino2,ndp);

  for(s = 0;s <= len2 ; s ++)
     for(n = 0; n <= len1; n++){
        printf("%3d",ndp[n][s].score);
        if(n == len1)putchar('\n');
     }
  putchar('\n');

  for(s = 0;s <= len2 ; s ++)
     for(n = 0; n <= len1; n++){
        printf("%3d",ndp[n][s].direct);
        if(n == len1)putchar('\n');
     }

printf("\n*** Result of alignment ***\nSCORE:%d\n",ndp[len1][len2].score);

  n = len1;
  s = len2;
  c = 0;
  while(n > 0 || s > 0){
     switch(ndp[n][s].direct){
        case UP:tracearc[c] = UP; s-- ;break;
        case LEFT:tracearc[c] = LEFT; n--; break;
        case UPLEFT:tracearc[c] = UPLEFT; n--; s--; break;
	}
     c ++;
  }
  tracearc[c] = '\0';  

  n = 0;
  s = 0;
  for(c --,c1 = 0;c >= 0;c --,c1 ++){
     switch(tracearc[c]){
        case UPLEFT:align_r1[c1] = amino1[n];n++;
                    align_r2[c1] = amino2[s];s++;break;

        case LEFT:  align_r1[c1] = amino1[n];n++;
                    align_r2[c1] = '-';break;

        case UP:    align_r1[c1] = '-';
                    align_r2[c1] = amino2[s];s++;break;
     }
  }
  align_r1[c1] = '\0';
  align_r2[c1] = '\0';

  printf("%s\n",align_r1);
  printf("%s\n",align_r2);
  


/*
while(1){
  printf("matrix test...\n");
  scanf("%s", &k1);
  scanf("%s", &k2);
  printf("%d\n", matrix1(k1,k2));
}
*/



   }


int strlength(string)
     char *string;
{    int ct = 0;
     while(string[ct]!= '\0')ct++;
     return ct; 
     }


/* ノードの初期化 一番外側のノード(アウトギャップ)を計算 */
init_align_map(amino1,amino2,ndp)
char *amino1,*amino2;
struct align_node *ndp;
{
   int len1,len2;
   int n,s;

   len1 = strlength(amino1);
   len2 = strlength(amino2);

   for(n = 1; n <= len1; n ++){
      ndp[n*ADIMSZ + 0].score = OUTGAP * n;
      ndp[n*ADIMSZ + 0].direct = LEFT;
   }

   for(s = 1; s <= len2; s ++){
      ndp[0*ADIMSZ + s].score = OUTGAP * s;
      ndp[0*ADIMSZ + s].direct= UP;
   }

   ndp[0*ADIMSZ + 0].direct=  0;
   ndp[0*ADIMSZ + 0].score =  0;

}

calc_score_map(amino1,amino2,ndp)
char *amino1,*amino2;
struct align_node ndp[ADIMSZ][ADIMSZ];
{
   int len1,len2;
   int n,s;

   int ul_score,lf_score,up_score;

   len1 = strlength(amino1);
   len2 = strlength(amino2);



  for(s = 1;s <= len2 ; s ++)
     for(n = 1;n <= len1; n ++){
        ul_score = ndp[n-1][s-1].score + matrix1(amino1[n-1],amino2[s-1]);
	lf_score = calc_lf_score(n, s, ndp);
	up_score = calc_up_score(n, s, ndp);

      if(ul_score >= lf_score && ul_score >= up_score){
           ndp[n][s].score = ul_score;
	   ndp[n][s].direct = UPLEFT;
        }
        else if(lf_score >= up_score){ 
           ndp[n][s].score = lf_score;
           ndp[n][s].direct= LEFT;
        }
        else { 
           ndp[n][s].score = up_score;
           ndp[n][s].direct= UP;
        }
      }
 }


/* n, s > 0 */
calc_lf_score(n, s, ndp)
int n,s;
struct align_node ndp[ADIMSZ][ADIMSZ];
{
  switch(ndp[n-1][s].direct){
  case LEFT:   return ndp[n-1][s].score + GAP;break;
  default:return ndp[n-1][s].score + FIRSTGAP + GAP;break;
  }
}

calc_up_score(n, s, ndp)
int n,s;
struct align_node ndp[ADIMSZ][ADIMSZ];
{
  switch(ndp[n][s-1].direct){
  case UP:     return ndp[n][s-1].score + GAP;break;
  default:return ndp[n][s-1].score + FIRSTGAP + GAP;break;
  }
}










