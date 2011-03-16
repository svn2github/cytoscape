#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include "rsgraph.h"

#define TATE 6 /* 0-5 */
#define YOKO 9 /* 0-8 */
#define TATEHA 48
#define YOKOHA 48
#define SSX 48
#define SSY 48

#define CDOWN 0
#define CUP 1
#define CGONE 2

struct lvs {char num;
	    int stat;
	    };

main(){

  int mx,my,mb;
  
  struct lvs cards[TATE+1][YOKO+1];

  struct pl {char name[20];
             int point;
	     };
  struct pl player[30]; /* player 1 -> player[1] */

  int nplayer,curplayer;
  int gcard;
  int ct1,ct2;
  int lx,ly,lx1,ly1,lx2,ly2;
  int rand1,rand2,temp;

  printf("how many players? :");
  scanf("%d",&nplayer);

  for(ct1=1;ct1<=nplayer;ct1++){
	printf("input name of player %d:",ct1);
	scanf("%s",player[ct1].name);
	player[ct1].point=0;
	}

  srand((unsigned int)time(NULL));

  for(ct1 = 0;ct1 < TATE ;ct1++)
     for(ct2 = 0;ct2 < YOKO ;ct2++){
	cards[ct1][ct2].num='A'+(ct2+ct1*(YOKO))/4;
        cards[ct1][ct2].stat=CDOWN;
    }

  for(ct1 = 0;ct1 < 1000; ct1++){
     rand1=rand()%(TATE*YOKO);
     rand2=rand()%(TATE*YOKO);
     temp=cards[rand1/YOKO][rand1%YOKO].num;
     cards[rand1/YOKO][rand1%YOKO].num=cards[rand2/YOKO][rand2%YOKO].num;
     cards[rand2/YOKO][rand2%YOKO].num=temp;
     }


  rsopen(250,150,600,416);
  setmasume(SSX,SSY,TATE,YOKO,TATEHA,YOKOHA);

  curplayer=1;
  gcard = 0;

while(gcard<(TATE*YOKO)){
  rssymbol(80,400,player[curplayer].name,"white");

  while(pickcard(&lx1,&ly1,&mb,cards)!=0)disbo(cards);
  printf("x1:%d y1:%d button:%d\n",lx1,ly1,mb); 
  cards[ly1][lx1].stat=CUP;
  disbo(cards);

  while(pickcard(&lx2,&ly2,&mb,cards)!=0)disbo(cards);
  printf("x2:%d y2:%d button:%d\n",lx2,ly2,mb);
  cards[ly2][lx2].stat=CUP; 
  disbo(cards); 

  if(cards[ly1][lx1].num==cards[ly2][lx2].num){
       cards[ly1][lx1].stat=cards[ly2][lx2].stat=CGONE;
       gcard+=2; 
       player[curplayer].point = player[curplayer].point + 2; 
       printf("score of player %d:%d\n",curplayer,player[curplayer].point);
       }
  else {cards[ly1][lx1].stat=cards[ly2][lx2].stat=CDOWN;
        rssymbol(80,400,player[curplayer].name,"blue");
        curplayer+=1;if(curplayer>nplayer)curplayer=1;
	cpoint(&lx,&ly,&mb); 
 
       }

  disbo(cards);
  setmasume(SSX,SSY,TATE,YOKO,TATEHA,YOKOHA);
}
  rsclose();
  printf("\n");
  printf("***Result of this match***\n");
  for(ct1=1;ct1<=nplayer;ct1++){
	printf("player%2d:%-10s:%2d points.\n",ct1,player[ct1].name,
	                                          player[ct1].point);
  }
  printf("\n");

}

int pickcard(lx,ly,mb,bo)
  int *lx;
  int *ly;
  int *mb;
  struct lvs bo[TATE+1][YOKO+1];
  {
   while(1){
      cpoint(lx,ly,mb); 
      if(*lx>=YOKO || *ly>=TATE)return 1;
      if(*lx<0     || *ly<0    )return 2;
      if(bo[*ly][*lx].stat==CDOWN)return 0;
      }
  }




cpoint(lx,ly,mb)
  int *lx;
  int *ly;
  int *mb;
  {
   rsmouse(lx,ly,mb);
   *lx = ((int)*lx-SSX)/YOKOHA;
   *ly = ((int)*ly-SSY)/TATEHA;
}


disbo(bo)
  struct lvs bo[TATE+1][YOKO+1];
  {
   int ct1,ct2,cstatus;
   for(ct1 = 0;ct1 < TATE;ct1++)
      for(ct2 = 0;ct2 < YOKO ;ct2++){
         cstatus = bo[ct1][ct2].stat;
       switch(cstatus){
	    case CDOWN:dischar(ct2,ct1,bo[ct1][ct2].num,"blue");break;
	    case CUP:  dischar(ct2,ct1,bo[ct1][ct2].num,"yellow");break;
	    case CGONE:dischar(ct2,ct1,bo[ct1][ct2].num,"gray");break;
	    default:break;
	    }
       } /* x,y -> yoko,tate */
}


dischar(lx,ly,c,color)
  int lx,ly;
  char c;
  char *color;
{
  char dum[2];
  dum[0]=c;dum[1]='\0';
  rssymbol(lx*YOKOHA+SSX+10,ly*TATEHA+SSY+40,dum,color);

}

setmasume(x,y,tate,yoko,tateha,yokoha)
  int x,y,tate,yoko,tateha,yokoha;
{
   int ct;
   for(ct = 0;ct <= tate;ct++)
	 rsline(x,y+tateha*ct,x+yoko*yokoha,y+tateha*ct,"green");
	
   for(ct = 0;ct <= yoko;ct++)
         rsline(x+yokoha*ct,y,x+yokoha*ct,y+tate*tateha,"green");
}



