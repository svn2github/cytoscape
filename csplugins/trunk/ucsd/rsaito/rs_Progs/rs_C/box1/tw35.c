#include <stdio.h>
#include "rsgraph.h"

int HORI = 5;
int VERT = 5;

#define CWIDTH 32
#define CHEIGHT 32

#define ESPACE 40

int pcell[25][25];

print_pcell(){

   int i,j;
   for(j = 0;j < VERT;j ++){
      for(i = 0;i < HORI; i++)
         printf("%d ",pcell[i][j]);
      putchar('\n');
   }
}

feed_pcell(){

   int i,j;
   for(j = 0;j < VERT;j ++){
      for(i = 0;i < HORI;i ++)
	 pcell[i][j] = (j * HORI + i) % 2;
   }

}

flip_draw(m,n)
int m, n;
{
   if(pcell[m][n] != 0)pcell[m][n] = 0;
   else pcell[m][n] = 1;
   if(pcell[m][n])
      rsfill(ESPACE + m * CWIDTH + 1, ESPACE + n * CHEIGHT + 1,
             CWIDTH - 2, CHEIGHT - 2, "cyan");
   else 
      rsfill(ESPACE + m * CWIDTH + 1, ESPACE + n * CHEIGHT + 1,
             CWIDTH - 2, CHEIGHT - 2, "red");

}

flip_pcell(m,n)
int m,n;
{
   flip_draw(m, n);
   if(m - 1 >= 0)flip_draw(m - 1, n);
   if(n - 1 >= 0)flip_draw(m, n - 1);
   if(m + 1 < HORI)flip_draw(m + 1, n);
   if(n + 1 < VERT)flip_draw(m, n + 1);
}

draw_pcell(){

   int i,j;
   for(j = 0;j < VERT;j ++)
     for(i = 0;i < HORI;i ++)
	if(pcell[i][j])
	   rsfill(ESPACE + i * CWIDTH + 1, ESPACE + j * CHEIGHT + 1,
	          CWIDTH - 2, CHEIGHT - 2, "cyan");
	else 
	   rsfill(ESPACE + i * CWIDTH + 1, ESPACE + j * CHEIGHT + 1,
	          CWIDTH - 2, CHEIGHT - 2, "red");

}

mousef(i, j)
int *i, *j;
{
   int x,y,b;
   rsmouse(&x, &y, &b);
   
   *i = (x - ESPACE) / CWIDTH;
   *j = (y - ESPACE) / CHEIGHT;
/*
   printf("hori:%d vert:%d\n",*i, *j);
*/
}

int pcell_check(){

   int i,j,bflag;
   
   for(j = 0, bflag = 0;j < VERT;j ++){
      for(i = 0;i < HORI;i ++)
	 if(pcell[i][j] == 1){ bflag = 1; break; }
      if(bflag)break;
   }
   if(i == HORI && j == VERT)return 1;

   for(j = 0, bflag = 0;j < VERT;j ++){
      for(i = 0;i < HORI;i ++)
	 if(pcell[i][j] == 0){ bflag = 1; break; }
      if(bflag)break;
   }
   if(i == HORI && j == VERT)return 1;

   return 0;
}

renew_pcell(){

  feed_pcell(); 
  draw_pcell();

}

rsmouse_e(x,y,b)
int *x;
int *y;
int *b;
{
   XEvent e;
   while(1){ 
      XNextEvent(d,&e);
      if (e.type == ButtonPress){  
	 *x = e.xbutton.x;
         *y = e.xbutton.y;
         *b = e.xbutton.button;
      }
      if (e.type == ButtonRelease)break;
      if (e.type == Expose)draw_pcell();
   }
}

main(argc, argv)
int argc;
char *argv[];
{
   int i,j,k;

   if(argc == 1){HORI = 5; VERT = 5;}
   else if(argc == 2){HORI = atoi(argv[1]); VERT = atoi(argv[1]); }
   else if(argc == 3){HORI = atoi(argv[1]); VERT = atoi(argv[2]); }
   else { fprintf(stderr, "Parameter error...\n"); exit(1); }
   if(HORI < 5 || HORI > 19 || VERT < 5 || VERT > 19){
      fprintf(stderr, "Parameter out of range...\n");
      exit(1);
   }
     


   feed_pcell();

   rsopen(100,100,ESPACE + CWIDTH * HORI + ESPACE,
		  ESPACE + CHEIGHT* VERT + ESPACE);

   XStoreName(d,w, "BioDoor");
   draw_pcell();

   k = 0;
   while(1){
      mousef(&i, &j); k ++;
      if(i >= 0 && i < HORI && j >= 0 && j < VERT){
         flip_pcell(i,j);
      }
      if(pcell_check() == 1){ renew_pcell(); }
   }

}
