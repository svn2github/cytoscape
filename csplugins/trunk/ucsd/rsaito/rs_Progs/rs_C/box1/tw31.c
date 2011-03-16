#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include "rsgraph.h"
#include "apcon.h"

   int block();
   int pblock();
   int ball();

   GC gc_black;
   GC gc_white;
   GC gc_red;
   GC gc_green;
   GC gc_yellow;

   XGCValues gv1;
   

/* variables used for movement of player's block */
   int pbhwid; /* width of player's block devided by 2 */
   int pbheight; /* height of player's block */
   int pblocx; /* center position of player's block (location x) */
   int pblocy; /* center position of player's block (location y) */
   int pbhwc; /* requested size for changing pbhwid */

#define LEFTEDGE 0
#define RIGHTEDGE 599
#define TOPEDGE 0
#define BOTTOMEDGE 415

   int ballx,bally; /* location of ball */
   int ballvx,ballvy; /* force vector of ball */
   int fall = 0;

   int blwid = 48; /* width of block */
   int blhet = 18; /* height of block */

   int score = 0;
   int nblocks = 96;

main(){
  int x,y,b;
  int i,n,s,t,fcn;

  printf("version 1.0\n");

  apinit();
  rsopen(250,150,RIGHTEDGE+1,BOTTOMEDGE+1);

  gv1.foreground = MyColor(d, "black");
  gc_black = XCreateGC(d, w, GCForeground, &gv1);

  gv1.foreground = MyColor(d, "white");
  gc_white = XCreateGC(d, w, GCForeground, &gv1);

  gv1.foreground = MyColor(d, "red");
  gc_red = XCreateGC(d, w, GCForeground, &gv1);

  gv1.foreground = MyColor(d, "green");
  gc_green = XCreateGC(d, w, GCForeground, &gv1);

  gv1.foreground = MyColor(d, "yellow");
  gc_yellow = XCreateGC(d, w, GCForeground, &gv1);


  apreg((int (*)())pblock);
  apreg((int (*)())ball);

  for(n = 0;n < 600; n+=50){
    fcn = apreg((int (*)())block);
    apv[fcn][1] = n+1;
    apv[fcn][2] = 50;
    apv[fcn][3] = 1;
  }
  for(n = 0;n < 600; n+=50){
    fcn = apreg((int (*)())block);
    apv[fcn][1] = n+1;
    apv[fcn][2] = 70;
    apv[fcn][3] = 3;
  }
  for(n = 0;n < 600; n+=50){
    fcn = apreg((int (*)())block);
    apv[fcn][1] = n+1;
    apv[fcn][2] = 90;
    apv[fcn][3] = 3;
  }
  for(n = 0;n < 600; n+=50){
    fcn = apreg((int (*)())block);
    apv[fcn][1] = n+1;
    apv[fcn][2] = 110;
    apv[fcn][3] = 5;
  }
  for(n = 0;n < 600; n+=50){
    fcn = apreg((int (*)())block);
    apv[fcn][1] = n+1;
    apv[fcn][2] = 150;
    apv[fcn][3] = 4;
  }
  for(n = 0;n < 600; n+=50){
    fcn = apreg((int (*)())block);
    apv[fcn][1] = n+1;
    apv[fcn][2] = 170;
    apv[fcn][3] = 3;
  }
  for(n = 0;n < 600; n+=50){
    fcn = apreg((int (*)())block);
    apv[fcn][1] = n+1;
    apv[fcn][2] = 190;
    apv[fcn][3] = 1;
  }

for(n = 0;n < 600; n+=50){
    fcn = apreg((int (*)())block);
    apv[fcn][1] = n+1;
    apv[fcn][2] = 210;
    apv[fcn][3] = 1;
  }

while(1){
   

apcall();
   if(nblocks <= 0){
      rssymbol(180,200,"CONGRADURATIONS!!","cyan");
      break;
   }
   if(fall >= 6){
      rssymbol(200,260,"    GAME OVER","red");
      break; 
   }

   for(i = 0;i < 500000;i ++);

}



  sleep(7);
  printf("%d\n",score);
  rsclose();


}

int pblock(func, var)
  int (**func)();
  int var[];
/* depends on grobal */
{
  int x,y,b;
  if(var[0] == 0){
     var[0] = 1;
     pbhwid = 30;
     pbheight = 15;
     pblocx = 300;
     pblocy = 380;
     pbhwc = 0;
     rsrect1(pblocx - pbhwid, pblocy - pbheight/2,
            pbhwid*2, pbheight, 1);
  }


  rsmsstat(&x,&y,&b);
  if(pblocx != x){
     rsrect1(pblocx - pbhwid, pblocy - pbheight/2,
            pbhwid*2, pbheight, 0);
     pblocx = x;
     if(pblocx < LEFTEDGE + pbhwid)pblocx = LEFTEDGE + pbhwid;
     if(pblocx > RIGHTEDGE - pbhwid)pblocx = RIGHTEDGE - pbhwid;
     rsrect1(pblocx - pbhwid, pblocy - pbheight/2,
            pbhwid*2, pbheight, 1);
  }

  if(pbhwc > 0){
     rsrect1(pblocx - pbhwid, pblocy - pbheight/2,
            pbhwid*2, pbheight, 0);
     pbhwid = pbhwc;
     rsrect1(pblocx - pbhwid, pblocy - pbheight/2,
            pbhwid*2, pbheight, 1);
     pbhwc = 0;
   }

}

int ball(func, var)
  int (**func)();
  int var[];
/* depends on grobal */
{
   int pbltx,pblty,pbrbx,pbrby, w,h, rbx,rby;
   float bcdec;

/* left top location of player's block */
   pbltx = pblocx - pbhwid;
   pblty = pblocy - pbheight/2;

/* right bottom location of player's block */
   pbrbx = pblocx + pbhwid;
   pbrby = pblocy + pbheight/2;

   w = pbrbx - pbltx;
   h = pbrby - pblty;

   rbx = ballx - pbltx;
   rby = bally - pblty;

/* relative location x of ball */
   bcdec = 1.0 * (pblocx - ballx) / pbhwid;

  if(var[0] == 0){
     var[0] = 1;
     ballx = 200;
     bally = 250;
     ballvx = 1;
     ballvy = 1;
     rscirc1(ballx, bally, 3, 2);
   }
  rscirc1(ballx, bally, 3, 0);

/* ball speed up */
  if(ballvy < 2 && ballvy > -2 && bally < 150)ballvy *= 2;


  ballx += ballvx;
  bally += ballvy;

  rscirc1(ballx, bally, 3, fall+1);

  if(ballx < LEFTEDGE && ballvx < 0)ballvx *= -1;
  if(ballx > RIGHTEDGE && ballvx > 0)ballvx *= -1;

  if(bally < TOPEDGE && ballvy < 0){
  pbhwc = 20;
  ballvy *= -1;
  }

  if(bally > BOTTOMEDGE && ballvy > 0){
     ballvy *= -1;
     fall ++;
  }

  if(ballx > pbltx && ballx < pbrbx &&
     bally > pblty && bally < pbrby && /* ball in player's block */
     ballvy > 0){
     if((rby < rbx * h / w) && (rby < (w - rbx) * h / w)){
        ballvy *= -1;
        if(bcdec >= 0.5 && bcdec < 0.75)ballvx = -2;
        else if(bcdec >= 0.75)ballvx = -3;
        else if(bcdec < 0.5 && bcdec >= 0)ballvx = -1;
	else if(bcdec > -0.5 && bcdec < 0)ballvx = 1;
	else if(bcdec <= -0.5 && bcdec > -0.75)ballvx = 2;
        else if(bcdec <= -0.75)ballvx = 3;        
     }
     else if(bcdec > 0){
        ballvy *= -1;
        ballvx = -5;
     }
     else if(bcdec < 0){
        ballvy *= -1;
        ballvx = 5;
     }
  }

}


int block(func, var)
 int (**func)();
 int var[];
/* depends on grobal */
{ 
#define BLOCKADJ 4
  int mx,my,mb;
  int bx,by, bex,bey, rbx,rby;
  if(var[0] == 0){
       var[0] = 1;
       switch(var[3]){
          case 1:rsfill(var[1],var[2],blwid,blhet,"green");break;
          case 2:rsfill(var[1],var[2],blwid,blhet,"yellow");break;
          case 3:rsfill(var[1],var[2],blwid,blhet,"pink");break;
          case 4:rsfill(var[1],var[2],blwid,blhet,"purple");break;
          case 5:rsfill(var[1],var[2],blwid,blhet,"red");break;
          default:rsfill(var[1],var[2],blwid,blhet,"red");break;
       }
  }     
/* left top corner of block */
  bx = var[1]; 
  by = var[2];

/* right bottom of block */
  bex = var[1] + blwid;
  bey = var[2] + blhet;

  rbx = ballx - bx;
  rby = bally - by;

  if(ballx > bx && ballx < bex &&
     bally > by && bally < bey){
       score ++;
/*       rsmouse(&mx,&my,&mb); */

/*     printf("rbx:%d rby:%d blwid:%d blhet:%d\n",rbx,rby,blwid,blhet); */
       if(var[3] <= 1){rsfill(bx, by, blwid, blhet, "black");
                       nblocks --; 
		       *func = NULL;}
       else {var[3]--;var[0]=0;}

       if((rby >= rbx * blhet/blwid && rby >= (blwid-rbx) * blhet/blwid)
          || rby >= blhet - BLOCKADJ){
          if(ballvy < 0)ballvy *= -1;
       }       
       else if(rby < rbx * blhet/blwid && rby < (blwid-rbx) * blhet/blwid){
          if(ballvy > 0)ballvy *= -1;
          if(ballvx > 2)ballvx = 2;
          if(ballvx < -2)ballvx = -2;
       }
       else if(rby < rbx * blhet/blwid && rby >= (blwid-rbx) * blhet/blwid){
          if(ballvx < 0)ballvx *= -1;
       }
       else if(rby >= rbx * blhet/blwid && rby < (blwid-rbx) * blhet/blwid){
          if(ballvx > 0)ballvx *= -1;
       }
      
  }
}

rsrect1(x,y,width,height,colorn)
     int x,y,width,height;
     int colorn;
{
     if(colorn == 0)XDrawRectangle(d, w, gc_black, x, y, width, height);  
     else XDrawRectangle(d, w, gc_white, x, y, width, height);  

     XFlush(d);
}
rscirc1(x,y,r,colorn)
     int x,y,r;
     int colorn;
{    if(colorn != 0)colorn = colorn / 3 + 1;  
     switch(colorn){
        case 0:XDrawArc(d, w, gc_black, x-r, y-r, r*2, r*2, 0, 360*64);break;
        case 1:XDrawArc(d, w, gc_green, x-r, y-r, r*2, r*2, 0, 360*64);break;
        case 2:XDrawArc(d, w, gc_yellow, x-r, y-r, r*2, r*2, 0, 360*64);break;
        default:XDrawArc(d, w, gc_red, x-r, y-r, r*2, r*2, 0, 360*64);break;
     }
     XFlush(d);
}



