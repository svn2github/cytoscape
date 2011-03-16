#include <stdio.h>
#include <time.h>
#include "rsgraph2dd.c" /* for X Window */

#define NMATCH 10
#define NGAME 100000
#define D_POINT 4.0
#define B_POINT 6.0

#define EVOL 0.001
#define NDRATE 0.1

#define RECFILE "str_dist"

/* for X Window */

#define FWIDTH 500
#define FHEIGHT 500
#define TLX 10
#define TLY 10

main(){

  int n,s;
  int *old_a; /* old action board */
  int *stb; /* strategy board */
  int *new_a; /* new action board */
  int *score_b; /* score board */
  int x_len,y_len;
  int gamen,matchn;

  int count[64];
  int monitor_mode = 0;
  int record_mode = 0;
  int xwindow_mode = 0;

  FILE *fp;

  if(xwindow_mode == 1)rsopen(100,100,TLX+FWIDTH+TLX,TLY+FHEIGHT+TLY);
       /* opens X Window */

  if(record_mode == 1)
    fp = fopen(RECFILE,"w");

  srand(time(NULL));

  x_len = 100;y_len = 100;

  old_a   = (int *)malloc(x_len*y_len*sizeof(int));
  new_a   = (int *)malloc(x_len*y_len*sizeof(int));
  stb     = (int *)malloc(x_len*y_len*sizeof(int));
  score_b = (int *)malloc(x_len*y_len*sizeof(int));

  strspread(stb,x_len,y_len);
  printf("stragegies spreaded.\n");

  gamen = 0;
  while(gamen < NGAME){

    if(xwindow_mode == 1)
      xvisual(stb,x_len,y_len); /* put strategy data to X Window */
    
    if(record_mode == 1){
      strwritefile(stb,x_len,y_len,fp);
      fclose(fp);
      fp = fopen(RECFILE,"a");
    }

    if(monitor_mode == 1){
      printf("game %d:\n",gamen);
      printf("strategies:\n");
      print_board((int *)stb,(int *)stb,x_len,y_len);
      putchar('\n');
    }

    for(s = 0;s < y_len;s ++)for(n = 0;n < x_len;n ++)
      score_b[n + s*x_len] = 0;

    matchn = 0;
    while(matchn < NMATCH){
      if(matchn == 0)first_act((int *)stb,(int *)new_a,x_len,y_len);
      else next_act((int *)stb,(int *)old_a,(int *)new_a,x_len,y_len);

      matchn ++;
      
      if(monitor_mode == 1)printf("match NO.%d\n",matchn);
      if(monitor_mode == 1){
	printf("\naction:\n");
	print_board((int *)stb,(int *)new_a,x_len,y_len);
	putchar('\n');
      }

      for(s = 0;s < y_len;s ++)for(n = 0;n < x_len;n ++)
	score_b[n + s*x_len] += cal_sc((int *)stb,new_a,x_len,y_len,n,s);

      if(monitor_mode == 1){
	printf("\nscore:\n");
	print_board((int *)stb,(int *)score_b,x_len,y_len);
	putchar('\n');
      }

      if(monitor_mode == 1){
	printf("input return key\n");
	getchar();
      }

      for(s = 0;s < y_len;s ++)for(n = 0;n < x_len;n ++)
	old_a[n + s*x_len] = new_a[n + s*x_len];
    }
	
    death((int *)stb,(int *)score_b,x_len,y_len);

    if(monitor_mode == 1){
        printf("result of death:\n");
	print_board((int *)stb,(int *)stb,x_len,y_len);
    }

    ndeath(stb,x_len,y_len);

    if(monitor_mode == 1){
       printf("result of natural death:\n");
       print_board((int *)stb,(int *)stb,x_len,y_len);
    }



    if(monitor_mode == 1){
      printf("birh will occur...\n");
      printf("\nscore:\n");
      print_board((int *)stb,(int *)score_b,x_len,y_len);
      putchar('\n');
    }
    
    if(monitor_mode == 1){
       printf("input return key\n");
       getchar();
    }

    birth((int *)stb,(int *)score_b,x_len,y_len);

    if(monitor_mode == 1){
      printf("result of birth:\n");
      print_board((int *)stb,(int *)stb,x_len,y_len);
    }

    evolve(stb,x_len,y_len);

    if(monitor_mode == 1){
       printf("result of evolution:\n");
       print_board((int *)stb,(int *)stb,x_len,y_len);
     }

    if(monitor_mode == 1){
       printf("input return key\n");
       getchar();
    }

    gamen ++;

    if(gamen % 10 == 0){
      for(n = 0;n < 64;n ++)count[n] = 0;
      for(n = 0;n < x_len * y_len;n ++)
	if(0 <= stb[n] && stb[n] < 64)count[stb[n]] ++;
      printf("%d",gamen);
      for(n = 0;n < 64;n ++)printf(",%d", count[n]);
      putchar('\n');
    }
  }

  if(record_mode == 1){
    strwritefile(stb,x_len,y_len,fp);
    fclose(fp);
  }
  if(xwindow_mode == 1){
    xvisual(stb,x_len,y_len);
    printf("input return key\n");
    getchar();
  }
}

next_act(str_board,old_aboard,new_aboard,x_len,y_len)
int *str_board,*old_aboard,*new_aboard;
int x_len,y_len;
{
	int n,s;
	int ndefect;
	int nact;

	for(s = 0;s < y_len;s ++)
		for(n = 0;n < x_len;n ++){
			ndefect = count_d(old_aboard,x_len,y_len,n,s);
			if(str_board[n + s*x_len] == -1)nact = 0;
			else 
	 nact = (str_board[n + s*x_len] & powint(2,4-ndefect))!=0;
			new_aboard[n + s*x_len] = nact;
		}
}

first_act(str_board,new_aboard,x_len,y_len)
int *str_board,*new_aboard;
int x_len,y_len;
{
	int n,s,nact;
	for(s = 0;s < y_len;s ++)
		for(n = 0;n < x_len;n ++){
			if(str_board[n + s*x_len] == -1)nact = 0;
			else
	  nact = (str_board[n + s*x_len] & powint(2,5))!=0;
			new_aboard[n + s*x_len] = nact;
		}
}

int count_d(old_aboard,x_len,y_len,x,y)
int *old_aboard;
int x_len,y_len;
int x,y;
{
	int count = 0;

	if(x - 1 >= 0 && old_aboard[x-1 + y * x_len])count ++;
	if(x + 1 < x_len && old_aboard[x+1 + y * x_len])count ++;
	if(y - 1 >= 0 && old_aboard[x + (y-1) * x_len])count ++;
	if(y + 1 < y_len && old_aboard[x + (y+1) * x_len])count ++;
	return count;
}

int powint(x,n)
int x,n;
{
	int ct;
	int ans = 1;
	for(ct = 0;ct < n;ct ++,ans *= x);
	return ans;
}

print_board(str_board,board,x_len,y_len)
int *str_board,*board;
int x_len,y_len;
{

	int n,s;
	for(s = 0;s < y_len;s ++){
		for(n = 0;n < x_len; n ++)
			if(str_board[n + s*x_len] == -1)
				printf("  -");
			else printf("%3d",board[n+s*x_len]);
		putchar('\n');
	}
}

int cal_sc(str_board,aboard,x_len,y_len,x,y)
/* score will be 0 if there is no strategy in loc. x,y */
int *str_board,*aboard;
int x_len,y_len,x,y;
{
	int score = 0;

	if(str_board[x + y*x_len] == -1)return 0;

	if(x-1 < 0 || str_board[x-1 + y*x_len] == -1)score += 1;
	else score += sc_mat(aboard[x + y*x_len],aboard[x-1 + y*x_len]);

	if(x+1 >= x_len || str_board[x+1 + y*x_len] == -1)score +=1;
	else score += sc_mat(aboard[x + y*x_len],aboard[x+1 + y*x_len]);

	if(y-1 < 0 || str_board[x + (y-1)*x_len] == -1)score += 1;
	else score += sc_mat(aboard[x + y*x_len],aboard[x + (y-1)*x_len]);

	if(y+1 >= y_len || str_board[x + (y+1)*x_len] == -1)score +=1;
	else score += sc_mat(aboard[x + y*x_len],aboard[x + (y+1)*x_len]);

	return score;
}
int sc_mat(ms,rs)
int ms,rs;
{
	if(ms == 0 && rs == 0)return 3;
	if(ms == 0 && rs == 1)return 0;
	if(ms == 1 && rs == 0)return 5;
	if(ms == 1 && rs == 1)return 1;
}

death(str_board,score_b,x_len,y_len)
int *str_board,*score_b;
int x_len,y_len;
{
  int n,s;

  for(s = 0;s < y_len;s ++)
    for(n = 0;n < x_len;n ++)
      if(1.0 * score_b[n + s*x_len] / NMATCH < D_POINT){
	str_board[n + s*x_len] = -1;
	score_b[n + s*x_len] = 0;
      }
}

birth(str_board,score_b,x_len,y_len)
int *str_board,*score_b;
int x_len,y_len;
{
  int n,s;
  int loc;
  int bear_monitor = 0;

if(rand() % 2 == 1){
  for(s = 0;s < y_len; s++)
    for(n = 0;n < x_len;n ++)
      if(str_board[n + s*x_len] == -1)
	if((loc = birth_sub(str_board,score_b,x_len,y_len,n,s)) != -1){
	  str_board[n + s*x_len] = str_board[loc];
	  if(bear_monitor == 1)
	    printf("[%d %d] bears [%d %d]\n",loc%x_len,loc/x_len,n,s); 
	  score_b[loc] -= (B_POINT - D_POINT)*NMATCH;
	  score_b[n + s*x_len] = 0;
          if(bear_monitor == 1){
	    printf("\nstrategies:\n");
	    print_board((int *)str_board,(int *)str_board,x_len,y_len);
	    putchar('\n');

	    printf("\nscore:\n");
	    print_board((int *)str_board,(int *)score_b,x_len,y_len);
	    putchar('\n');
	    printf("input return key\n");
	    getchar();
	  }
	}
}
else {
  for(s = y_len - 1;s >= 0; s --)
    for(n = x_len - 1;n >= 0;n --)
      if(str_board[n + s*x_len] == -1)
	if((loc = birth_sub(str_board,score_b,x_len,y_len,n,s)) != -1){
	  str_board[n + s*x_len] = str_board[loc];
          if(bear_monitor == 1)
	    printf("[%d %d] bears [%d %d]\n",loc%x_len,loc/x_len,n,s); 

	  score_b[loc] -= (B_POINT - D_POINT)*NMATCH;
	  score_b[n + s*x_len] = 0;
          if(bear_monitor == 1){
	    printf("\nstrategies:\n");
	    print_board((int *)str_board,(int *)str_board,x_len,y_len);
	    putchar('\n');

	    printf("\nscore:\n");
	    print_board((int *)str_board,(int *)score_b,x_len,y_len);
	    putchar('\n');
	    printf("input return key\n");
	    getchar();
	  }
	}

}

}
/* returns location where strategy which will bear exist */
/* x,y should be blank cell */
int birth_sub(str_board,score_b,x_len,y_len,x,y)
int *str_board,*score_b;
int x_len,y_len,x,y;
{
  int n,s;
  int birth_list[4]; /* list of location of strategies which has 
			ability to bear */
  int num_birth = 0; /* number of direction */
  int greatest = 0;

  int bear_monitor = 0;

  if(y > 0 && str_board[x + (y-1)*x_len] != -1 &&
     score_b[x + (y-1)*x_len] >= B_POINT*NMATCH){
    /* existance of strategy having ability of bearing */
    if(score_b[x + (y-1)*x_len] > greatest){
      birth_list[0] = x + (y-1)*x_len;
      num_birth = 1;
      greatest = score_b[x + (y-1)*x_len];
    }
    else if(score_b[x + (y-1)*x_len] == greatest){
      birth_list[num_birth] = x + (y-1)*x_len;
      num_birth ++;
    }
  }

  if(x > 0 && str_board[x-1 + y*x_len] != -1 &&
     score_b[x-1 + y*x_len] >= B_POINT*NMATCH){
    /* existance of strategy having ability of bearing */
    if(score_b[x-1 + y*x_len] > greatest){
      birth_list[0] = x-1 + y*x_len;
      num_birth = 1;
      greatest = score_b[x-1 + y*x_len];
    }
    else if(score_b[x-1 + y*x_len] == greatest){
      birth_list[num_birth] = x-1 + y*x_len;
      num_birth ++;
    }
  }

  if(y < y_len-1  && str_board[x + (y+1)*x_len] != -1  &&
     score_b[x + (y+1)*x_len] >= B_POINT*NMATCH){
    /* existance of strategy having ability of bearing */
    if(score_b[x + (y+1)*x_len] > greatest){
      birth_list[0] = x + (y+1)*x_len;
      num_birth = 1;
      greatest = score_b[x + (y+1)*x_len];
    }
    else if(score_b[x + (y+1)*x_len] == greatest){
      birth_list[num_birth] = x + (y+1)*x_len;
      num_birth ++;
    }
  }

  if(x < x_len-1 && str_board[x+1 + y*x_len] != -1 &&
     score_b[x+1 + y*x_len] >= B_POINT*NMATCH){
    /* existance of strategy having ability of bearing */
    if(score_b[x+1 + y*x_len] > greatest){
      birth_list[0] = x+1 + y*x_len;
      num_birth = 1;
      greatest = score_b[x+1 + y*x_len];
    }
    else if(score_b[x+1 + y*x_len] == greatest){
      birth_list[num_birth] = x+1 + y*x_len;
      num_birth ++;
    }
  }

if(bear_monitor == 1){
  if(num_birth != 0){
    printf("around blanc cell (%d,%d)\n",x,y);
    for(n = 0;n < num_birth;n ++)
      printf("loc (%d,%d):",birth_list[n] % x_len,
	                    birth_list[n] / x_len);
    putchar('\n');
  }
}

  if(num_birth == 0)return -1;
  else return(birth_list[rand() % num_birth]);
}

strspread(str_board,x_len,y_len)
int *str_board;
int x_len,y_len;
{
  int x,y;
  for(x = 0;x < x_len;x ++)
    for(y = 0;y < y_len;y ++)
      str_board[x + y*x_len] = -1;

  for(x = x_len/4;x < x_len - x_len/4;x ++)
    for(y = y_len/4;y < y_len - y_len/4;y ++)
      if(rand()%100 >= 50)str_board[x + y*x_len] = rand()%64;

}

ndeath(str_board,x_len,y_len)
/* warning: this function does not treat score */
int *str_board;
int x_len,y_len;
{
  int x,y;
  for(y = 0;y < y_len;y ++)
    for(x = 0;x < x_len;x ++)
      if(rand()%10000*1.0/10000 < NDRATE)
	str_board[x + y*x_len] = -1;
}

evolve(str_board,x_len,y_len)
int *str_board;
int x_len,y_len;
{
  int x,y;
  for(y = 0;y < y_len;y ++)
    for(x = 0;x < x_len;x ++){
      if(str_board[x + y*x_len] != -1)
	str_board[x + y*x_len] = thunder(str_board[x + y*x_len]);
    }
}


int thunder(strategy_code)
int strategy_code;
{
  int result = 0;
  int n,bit_code;

  for(n = 0;n < 6;n ++){
    if((strategy_code & 32) != 0)bit_code = 1;
    else bit_code = 0;
    bit_code = thunder_sub(bit_code);
    result *= 2;
    result += bit_code;
    strategy_code *= 2;
  }
  return result;
}

/* file must already be opened */
strwritefile(str_board,x_len,y_len,fp)
int *str_board,x_len,y_len;
FILE *fp;
{
  int ct,strc;
  for(ct = 0;ct < x_len * y_len;ct ++){
    strc = str_board[ct];
    if(strc == -1)strc = 255;
    fputc(strc,fp);
  }
}


int thunder_sub(bit_code)
int bit_code;
{
  switch(bit_code){
  case 0:if(rand() % 10000 * 1.0 / 10000 < EVOL)return 1;
         else return 0;
  case 1:if(rand() % 10000 * 1.0 / 10000 < EVOL)return 0;
         else return 1;
  default:printf("CODE ERROR:%d\n",bit_code);
  }
}

xvisual(str_board,x_len,y_len)
int *str_board;
int x_len,y_len;
{
  int n,s;
  unsigned long color_c;

  printf("xvisual function called.\n");
  for(s = 0;s < y_len;s ++)
    for(n = 0;n < x_len;n ++){
      switch(str_board[n + s*x_len]){
         case -1:color_c = black;break;
	 case  0:color_c = white;break;
	 case  1:color_c = moccasin;break;
	 case  2:color_c = cornsilk;break;
	 case  3:color_c = ivory;break;
	 case  4:color_c = seashell;break;
	 case  5:color_c = honeydew;break;
	 case  6:color_c = azure;break;
	 case  7:color_c = lavender;break;
	 case  8:color_c = navy;break;
	 case  9:color_c = sky_blue;break;
	 case 10:color_c = peru;break;
	 case 11:color_c = turquoise;break;
	 case 12:color_c = aquamarine;break;
	 case 13:color_c = khaki;break;
	 case 14:color_c = gold;break;
	 case 15:color_c = blue;break; /* TFT1 */
	 case 16:color_c = sienna;break;
	 case 17:color_c = purple;break; /* CHUYO */ 
	 case 18:color_c = burlywood;break;
	 case 19:color_c = beige;break;
	 case 20:color_c = wheat;break;
	 case 21:color_c = tan;break;
	 case 22:color_c = chocolate;break;
	 case 23:color_c = firebrick;break;
	 case 24:color_c = salmon;break;
	 case 25:color_c = coral;break;
	 case 26:color_c = tomato;break;
	 case 27:color_c = maroon;break;
	 case 28:color_c = violet;break;
	 case 29:color_c = plum;break;
	 case 30:color_c = orchid;break;
	 case 31:color_c = thistle;break;
	 case 32:color_c = gainsboro;break;
	 case 33:color_c = linen;break;
	 case 34:color_c = bisque;break;
	 case 35:color_c = papaya_whip;break;
	 case 36:color_c = blanched_almond;break;
	 case 37:color_c = peach_puff;break;
	 case 38:color_c = lemon_chiffon;break;
	 case 39:color_c = mint_cream;break;
	 case 40:color_c = alice_blue;break;
	 case 41:color_c = misty_rose;break;
	 case 42:color_c = royal_blue;break;
	 case 43:color_c = sky_blue;break;
	 case 44:color_c = pale_turquoise;break;
	 case 45:color_c = spring_green;break;
	 case 46:color_c = yellow;break; /* !CHUYO (RYOKYOKU) */
	 case 47:color_c = olive_drab;break;
	 case 48:color_c = orange;break; /* !TFT1 (AMJ) */
	 case 49:color_c = sandy_brown;break;
	 case 50:color_c = orange_red;break;
	 case 51:color_c = hot_pink;break;
	 case 52:color_c = blue_violet;break;
	 case 53:color_c = cornsilk2;break;
	 case 54:color_c = aquamarine2;break;
	 case 55:color_c = orange_red;break;
	 case 56:color_c = light_goldenrod;break;
	 case 57:color_c = goldenrod;break;
	 case 58:color_c = gray40;break;
	 case 59:color_c = gray50;break;
	 case 60:color_c = gray80;break;
	 case 61:color_c = gray70;break;
	 case 62:color_c = gray60;break;
	 case 63:color_c = red;break;
	 default:printf("color error...\n");exit(1);break;
      }
      rsfill_i(FWIDTH*n/x_len+1+TLX,
	       FHEIGHT*s/y_len+1+TLY,
	       FWIDTH/x_len - 2,
	       FHEIGHT/y_len -2,
	       color_c);
    }
  rs_imageput(0,0,TLX+FWIDTH+TLX,TLY+FHEIGHT+TLY);

}

