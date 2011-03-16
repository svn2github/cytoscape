#include <stdio.h>
#include "rsgraph2dd.c"

#define NGAME 20

#define RECFILE "str_dist"

/* for X Window */

#define FWIDTH 500
#define FHEIGHT 500
#define TLX 10
#define TLY 10

main(){

  FILE *fp;
  int x_len,y_len;
  int *strhistbuf;
  int generation,n;

  x_len = 100;
  y_len = 100;

  strhistbuf = (int *)malloc(x_len * y_len * (NGAME + 1) * sizeof(int));
  if(strhistbuf == NULL)printf("error in memory allocation\n");
  else printf("memory allocation finished...%d\n",x_len*y_len*(NGAME+1));

  fp = fopen(RECFILE,"r");
  if(fp == NULL)printf("file open error\n");
  for(n = 0;n < x_len * y_len * (NGAME + 1);n ++){
    strhistbuf[n] = fgetc(fp);
    if(strhistbuf[n] == 255)strhistbuf[n] = -1;
  }
  fclose(fp);

  rsopen(100,100,TLX+FWIDTH+TLX,TLY+FHEIGHT+TLY);
       /* opens X Window */

  printf("input return key to start\n");
  getchar();
  for(generation = 0;generation <= NGAME;generation ++){
    if(generation == 0)
      xvisual(&strhistbuf[generation * x_len * y_len],x_len,y_len);
    else xvisual_comp(&strhistbuf[(generation - 1) * x_len * y_len],
		      &strhistbuf[generation * x_len * y_len],
		      x_len,y_len);
  }

  printf("input return key to end\n");
  getchar();

  rsclose();
}
 
xvisual(str_board,x_len,y_len)
int *str_board;
int x_len,y_len;
{
  int n,s;
  unsigned long color_c;

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
xvisual_comp(str_board_o,str_board_n,x_len,y_len)
int *str_board_o,*str_board_n;
int x_len,y_len;
{
  int n,s;
  unsigned long color_c;

  for(s = 0;s < y_len;s ++)
    for(n = 0;n < x_len;n ++){
      if(str_board_o[n + s*x_len] != str_board_n[n + s*x_len]){
	switch(str_board_n[n + s*x_len]){
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
    }
  rs_imageput(0,0,TLX+FWIDTH+TLX,TLY+FHEIGHT+TLY);

}












