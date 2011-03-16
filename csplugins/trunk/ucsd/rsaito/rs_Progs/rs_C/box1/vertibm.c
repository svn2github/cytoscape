#include "stdio.h"
#include "g.h"
#include "math.h"
#include "string.h"
#include "stdlib.h"
#include "mouse.h"
#define PAI 3.141592653575

void Mess(int x,int y,char *strings){ /* Message Print Only */
  printf("%c[%0d;%0dH",0x1b,y,x);printf(strings);
}
char Ink(){
  char key;
  if(kbhit()==0)return '';
  key=getch();
  if(key==0)return getch();
  return key;
}
int Sgn(int a){ /* Signal Function */
 if(a>0)return(1);
 if(a<0)return(-1);
 return(0);
}
void Color(int c){/* 30:black  32:gray1  34:gray2  36:white */
  printf("%c[%dm",0x1b,c);
}
void Music(int a){
  switch(a){
  case 1: /* Goal */
    SOUND("5C",2000);SOUND("4A",2000);SOUND("4G",1000);SOUND("4A",1000);
    SOUND("5C",6000);
    break;
  case 2: /* Vertical */
    SOUND("4C",5000);SOUND("3B",5000);SOUND("4C",5000);
    SOUND("4E",5000);SOUND("4D",5000);SOUND("4E",5000);
    SOUND("4F",5000);SOUND("4E",5000);SOUND("4F",5000);
    SOUND("5C",10000);
    break;
  case 3: /* Go Forward */
    SOUND("4C",2000);SOUND("4D",2000);SOUND("3B",2000);SOUND("4C",2000);
    SOUND("3F",6000);
    break;
  }
}
void Screen1(int l){ /* Make Screen (Clouds and titles) */
  int i,j,h;
  GMODE(4);
  switch(l/5)
    {
    case 0: /* Clouds */
      for(i= 0;i<320;i=i+20)for(j=0;j<10;j++)CIRCLE(i+10,30-j,10,2);
      for(i=20;i<320;i=i+20)for(j=0;j<10;j++)CIRCLE(i   ,28-j,10,1);
      for(i= 0;i<320;i=i+20)for(j=0;j<10;j++)CIRCLE(i+10,25-j,10,3);
      for(i=20;i<320;i=i+20)for(j=0;j<10;j++)CIRCLE(i   ,21-j,10,2);
      for(i= 0;i<320;i=i+20)for(j=0;j<12;j++)CIRCLE(i+10,16-j,10,1);
      for(i=20;i<320;i=i+20)for(j=0;j<10;j++)CIRCLE(i   ,10-j,10,3);
      for(i=190;i<200;i++)LINE(0,i,319,i,0);
      break;
    case 1: /* Moon */
      for(i=0;i<15;i++){CIRCLE(50,25,i,3);CIRCLE(50,26,i,3);}
      for(i=0;i<11;i++){CIRCLE(54,25,i,0);CIRCLE(54,26,i,0);}
      for(h=0;h<=5;h+=5){
	for(j=200;j<=300;j+=20){
	  for(i=0;i<4;i++)CIRCLE(j+1-h,33-i-h,6,1);
	  for(i=0;i<=5;i++)CIRCLE(j-h,30-h,i,3);
	  for(i=0;i<4;i++)CIRCLE(j+11-h,28-i-h,6,1);
	  for(i=0;i<=5;i++)CIRCLE(j+10-h,25-h,i,3);
	}
      }
      break;
    case 2: /* Stars */
      for(i=0;i<100;i++)PSET(rand()%320,rand()%30+10,rand()%3+1);
    }
  for(i=0;i<11;i++)LINE(0,i,319,i,0);
  Color(36);Mess(6,0,"VERTICAL");
  LOCATE(25,0);printf("GRAVITY %d",l);
}
void Screen2(){ /* Make and Clear Screen */
  int i;
  for(i=41;i<50;i++)LINE(0,i,320,i,0);
  for(i=7;i<24;i++)Mess(0,i,"                                        \n");
  Mess(0,24,"                                        ");
}
void Hand(int x,int y){ /* Print Main Character */
  LINE(x+4,y-2,x+4,y+2,2);/* Body */
  LINE(x+5,y-2,x+5,y+2,2);
  LINE(x+6,y-2,x+6,y+2,2);

  LINE(x+6,y-2,x+3,y+1,3);/* Hand */
  LINE(x+5,y-2,x+2,y+1,3);
  LINE(x-1,y+1,x+1,y+1,3);
  PSET(x+1,y,3);
}
void Head(int x,int y){
  LINE(x+5,y-6,x+6,y-6,3);/* Head */
  LINE(x+4,y-5,x+6,y-5,3);
  LINE(x+5,y-4,x+6,y-4,2);
  LINE(x+5,y-3,x+6,y-3,2);
  PSET(x+4,y-2,2);/* Shoulder */
  LINE(x+5,y-2,x+6,y-2,3);
}
void Footrun(int x,int y,int c){
  PSET(x+1,y+6,c);/* Right Foot */
  LINE(x+2,y+4,x+2,y+6,c);
  LINE(x+3,y+3,x+3,y+6,c);
  PSET(x+4,y+3,c);

  if(c==1)c=3;
  LINE(x+5,y+3,x+6,y+3,c);/* Left foot */
  LINE(x+6,y+4,x+9,y+4,c);
  LINE(x+7,y+5,x+9,y+5,c);
  PSET(x+9,y+6,c);
}
void Footstop(int x,int y,int c){ /* Print Foots */
  PSET(x+3,y+6,c);/* Rifht Foot */
  LINE(x+4,y+3,x+4,y+5,c);

  if(c==1)c=3;
  PSET(x+4,y+6,c);/* Left Foot */
  LINE(x+5,y+3,x+5,y+6,c);
  LINE(x+6,y+3,x+6,y+6,c);
}
void Triangle(int x,int y,int c){/* triangle scale */
  LINE(x+10,y-10,x+10,y-62,c);
  LINE(x+10,y-10,x+40,y-10,c);
  LINE(x+40,y-10,x+10,y-62,c);

  LINE(x+18,y-18,x+18,y-32,c);
  LINE(x+18,y-18,x+26,y-18,c);
  LINE(x+26,y-18,x+18,y-32,c);

  LINE(x+14,y-14,x+10,y-14,c);
  LINE(x+14,y-14,x+14,y-10,c);
}
void CommandPrint(int x){/* start:x=-1  option:x=1 */
  Color(34-x*2);
  Mess(15,7,"GAME START");
  Color(34+x*2);
  Mess(15,9,"  OPTION");
}
void Window(int x1,int y1,int x2,int y2){/* Open Window */
  LINE(x1+1,y1+1,x2+1,y1+1,1);
  LINE(x1+1,y1+1,x1+1,y2+1,1);
  LINE(x1+1,y2+1,x2+1,y2+1,1);
  LINE(x2+1,y1+1,x2+1,y2+1,1);

  LINE(x1,y1,x2,y1,3);
  LINE(x1,y1,x1,y2,3);
  LINE(x1,y2,x2,y2,3);
  LINE(x2,y1,x2,y2,3);
}
void WinMess(int ComNo){/* Print Messages in Window */
  Color(36);Mess(3,3,"*OPTIONS*");
  if(ComNo==1)Color(36);else Color(32);Mess(3,5," KEY");
  if(ComNo==2)Color(36);else Color(32);Mess(3,6," RULE");
  if(ComNo==3)Color(36);else Color(32);Mess(3,7," GRAVITY");
  if(ComNo==4)Color(36);else Color(32);Mess(3,8," RETURN");
}
void ClearWin(int x,int y,int lx,int ly){/* Close Window */
  int i,j;
  for(i=y;i<y+ly;i++){
    LOCATE(x,i);
    for(j=0;j<lx;j++)printf(" ");
  }
}
void Demo(){
  int i;
  long int L;
  for(i=0;i<320;i+=50){
    L=sqrt((double)(150-i)*(150-i)+16900);
    LINE(100*(150-i)/L+i,180-13000/L,i,180,3);
    Hand(i,180);
    Head(i,180);
    if(i/10%2)Footstop(i,180,1);
    else Footrun(i,180,1);
  }
  Color(34);Mess(18,25,"1994 S.Yano Presents");
}
int Maketitle(int gravity){/* Title Screen */
  int i,tf=0,tc=0,com=-1,comno,mx=0,my=200,omx,omy,ml=0,mr=0;
  char k='',mf;
  GMODE(4);
  mf=m_ini();
  Demo();
  while(1){
    CommandPrint(com);
    mr=0;ml=0;
    while(k!=' ' && k!=13 && k!=27 && ml!=-1 && mr!=-1){
      k=Ink();
      if(tf==16){tf=0;if(tc==0)tc=3;else tc=0;}
      Triangle(150,180,tc);
      for(i=30;i<36;i+=2){
	if(mf==1){
	  omy=my;
	  m_get(&ml,&mr,&mx,&my);
	  if(my!=omy){com=Sgn(my-omy);CommandPrint(com);}
	}
	Color(i);Mess(13,3,"V E R T I C A L");
	if(k==72){com*=-1;CommandPrint(com);k='';}
	if(k==80){com*=-1;CommandPrint(com);k='';}
	if(k==27)break;
      }
      tf++;
    }
    if(com==1 && k!=27){
      Window(10,10,90,70);
      k='';comno=3;mr=0;ml=0;
      while(k!=27){
	k=Ink();
	while(ml==-1||mr==-1)m_get(&ml,&mr,&mx,&my);
	if(mf==1){
	  omy=my;
	  m_get(&ml,&mr,&mx,&my);
	  if(my-omy!=0)k=76+Sgn(my-omy)*4;
	}
	if(k==72){comno--;if(comno==0)comno=1;}
	if(k==80){comno++;if(comno==5)comno=4;}
	WinMess(comno);
	if(k==' ' || k==13 || ml==-1 || mr==-1){
	  while(ml==-1||mr==-1)m_get(&ml,&mr,&mx,&my);
	  switch(comno){
	  case 1:
	    Window(102,0,310,77);
	    Color(36);
	    Mess(14,2,"    * KEY OPERATION *    ");
	    Mess(14,3,"                         ");
	    Mess(14,4," MOVE RIGHT ... [ >] KEY ");
	    Mess(14,5," MOVE LEFT  ... [< ] KEY ");
	    Mess(14,6,"         or MOUSE BUTTONS");
	    Mess(14,7,"                         ");
	    Mess(14,8," QUIT       ... [ESC] KEY");
	    Mess(14,9,"                         ");
	    LINE(242,27,252,27,3);
	    LINE(242,35,252,35,3);
	    mr=0;ml=0;
	    while(Ink()=='' && ml==0 && mr==0)
	      if(mf==1)m_get(&ml,&mr,&mx,&my);
	    ClearWin(13,1,28,10);
	    break;
	  case 2:
	    Window(102,0,318,46);
	    Color(36);
	    Mess(14,2,"         * RULE *         ");
	    mr=0;ml=0;
	    while(Ink()=='' && ml==0 && mr==0){
	      if(mf==1)m_get(&ml,&mr,&mx,&my);
	      for(i=2;i<8;i+=2){
		Color(30+i);
		Mess(14,3," Let's get to the goal    ");
		Mess(14,4,"        keeping the pole  ");
		Mess(14,5,"                 vertical!");
	      }
	    }
	    ClearWin(13,1,28,6);
	    break;
	  case 3:
	    Window(200,25,300,60);
	    Color(36);
	    Mess(27,5,"SET GRAVITY");
	    Mess(27,7,"[< ]   [ >]");
	    LINE(219,51,227,51,3);
	    LINE(275,51,283,51,3);
	    k='';mr=0;ml=0;
	    while(k!=' ' && k!=13 && k!=27 && ml==0 && mr==0){
	      k=Ink();
	      if(mf==1){
		omx=mx;
		m_get(&ml,&mr,&mx,&my);
		if(mx-omx!=0)k=76+Sgn(mx-omx);
	      }
	      if(k==75){gravity--;if(gravity<1)gravity=1;}
	      if(k==77){gravity++;if(gravity==16)gravity=15;}
	      if(k=='0')gravity=0;
	      LOCATE(32,7);printf("%X",gravity);
	    }
	    k='';comno=4;
	    ClearWin(26,4,13,5);
	    break;
	  case 4:
	    k=27;
	  }
	  while(ml==-1||mr==-1)m_get(&ml,&mr,&mx,&my);
	  CommandPrint(com);
	  Color(36);Mess(13,3,"V E R T I C A L");
	}
      }
      k='';
      ClearWin(2,2,11,8);
    }
    else if(k==27)return(-1);else return(gravity);
  }
}

void Ready(int x,int y,int l){
  int i,j;
  Hand(x,y);Head(x,y);Footstop(x,y,1);
  LINE(x,y,x,y-l,3);
  Mess(17,8,"READY");
  for(i=0;i<25;i++){
    for(j=0;j<40;j++)SOUND("5E",12);
    for(j=0;j<40;j++)SOUND("5E",10);
  }
  Mess(17,8,"     ");
}
void Goal(int x,int y,int xx,int yy,float degree){ /* Process for Goal */
  int deg,i;
  float degf;
  Hand(x,y);Head(x,y);LINE(x,y,xx,yy,3);
  deg=(int)(degree*10);
  degf=(float)deg/10;
  Color(36);Mess(17,7,"GOAL!");
  Music(1);
  if(x==xx){
    for(i=0;i<100;i++){
      Color(30+i%4*2);
      Mess(15,9,"             ");
      Mess(15,9,"VERTICALITY!!");
      Triangle(155,180,i%4);
    }
    Music(2);
  }
  else{
    LOCATE(13,9);printf("%.1f degrees",degf);
    if(deg<900){
      LOCATE(11,10);printf("%.1f degrees more.",90-degf);}
    if(deg>900){
      LOCATE(11,10);printf("%.1f degrees over.",degf-90);}
  }
}
void Mid(char *st,int n,int y){ /* Print a Part of background */
  char outst[41];
  strncpy(outst,st+n,40);
  outst[40]='\0';
  LOCATE(1,y);printf("%s",outst);
}
void Over(int x,int y,int xx,int yy,int l,float n,float v,int m,int s
	  ,char *st){
  int i,u=-8,w,hx,hy,mxx,myy,ox,oy,oxx,oyy;
  hx=x;hy=y;
  w=Sgn(xx-x)*3;
  Hand(x,y);Head(x,y);Footstop(x,y,1);
  SOUND("4F",500);
  for(i=0;i<30;i++){
    mxx=x+l*cos(n*PAI);  
    myy=y+l*sin(n*PAI);
    oxx=xx;oyy=yy;ox=x;oy=y;
    xx=mxx;yy=myy;
    if(yy>180){n=n-v;yy=y+l*sin(n*PAI);v=-v*.5;SOUND("4F",150);}
    if(y>180){y=y-u;u=-u*.5;SOUND("4F",150);}
    x=x+w;y=y+u;
    Mid(st,s,22);
    LINE(ox,oy,oxx,oyy,0);
    LINE(x,y,xx,yy,3);
    n=n+v;u++;
    v=v+cos(n*PAI)/1000;
    Hand(hx,hy);Head(hx,hy);Footstop(hx,hy,1);
  }
  Color(36);Mess(15,8,"GAME OVER");
  LOCATE(12,10);printf("You walked %d steps",55-m);
}
char Replay(char mf){
  int c=1,mx=0,my=200,omy=0,ml=0,mr=0;
  char k=0;

  ClearWin(13,12,14,4);
  Window(96,86,208,118);
  Color(34);
  Mess(14,13,"REPLAY");
  Mess(14,14,"      ");
  while(k!=' ' && k!=13 && k!=27){
    k=Ink();
    if(mf==1){
      omy=my;
      m_get(&ml,&mr,&mx,&my);
      if(my-omy!=0)c=-Sgn(my-omy);
    }
    if(k==72||k==80)c*=-1;
    Color(34+c*2);Mess(21,13,"[Yes]");
    Color(34-c*2);Mess(21,14,"[No ]");
    if(k=='y'){c=1;break;}
    if(k=='n'){c=-1;break;}
    if(mr==-1)break;
  }
  if(k==27)c=-1;
  Color(36);
  return c;
}
void main(int argc,char *argv[]){
  int x,y,xx,yy,l=120,end,scroll,fscroll,maxstep,gravity=3,mxx,myy,
  ml=0,mr=0,mx,my,erasef=0,oxx,oyy;
  float n,v,vz=.001;
  char k='',replay=1,mf;
  char *st="_________S.Yano_____|GOAL|_______|ooooooo|________o_______|_______o_______|START|_o|||o___ossso___o__xxx";
  char *sf="_______Presents__|GOAL|_________________|OOOOOOOOOOOOOOO|_______________O__[DynaBook]___|_______________O_______________|_______O_______O | | | O_______OSSSSSSSO_______O_____xxx";
  if(argc > 2){printf("Usage: vert [GRAVITY]\n");return;}
  if(argc==2)gravity=atoi(argv[1]);
  vz=(float)gravity/3000;/* */
  gravity=Maketitle(gravity);
  if(gravity==0)vz=0;
  if(gravity<0){Color(36);GMODE(3);return;}
  Screen1(gravity);
  mf=m_ini();
  while(replay!=-1){
    end=0;replay=1;x=150;y=177;xx=150;yy=177;
    n=.5;v=0;scroll=55;fscroll=101;maxstep=55;
    Screen2();
    Mid(st,scroll,22);Mid(sf,fscroll,24);
    Ready(x,y,l);
    while(scroll>6){
      k=Ink();
      if(mf==1)m_get(&ml,&mr,&mx,&my);
      mxx=x+l*cos(n*PAI);myy=y-l*sin(n*PAI);
      if(xx!=mxx||yy!=myy){erasef=1;oxx=xx;oyy=yy;}
      xx=mxx;yy=myy;
      if(k==77||mr==-1){
	Footstop(x,y,0);Footrun(x,y,1);
	Mid(st,++scroll,22);v+=vz*sin(n*PAI);Head(x,y);
	fscroll+=2;Mid(sf,fscroll,24);
	Footrun(x,y,0);Footstop(x,y,1);
      }
      if(k==75||ml==-1){
	Footstop(x,y,0);Footrun(x,y,1);
	Mid(st,--scroll,22);v-=vz*sin(n*PAI);Head(x,y);
	fscroll-=2;Mid(sf,fscroll,24);
	if(scroll<maxstep)maxstep=scroll;
	Footrun(x,y,0);Footstop(x,y,1);
      }
      if(erasef==1){LINE(x,y,oxx,oyy,0);Head(x,y);erasef=0;}
      LINE(x,y,xx,yy,3);
      if(scroll<=6)break;
      if(yy>180){end=1;break;}
      if(k=='q' || k==27){end=2;break;}
      if(scroll>81){end=3;break;}
      n=n+v;
      v=v-cos(n*PAI)/1000*gravity/6;
      while(k!='')k=Ink();
    }
    switch(end)
      {
      case 0:
	Goal(x,y,xx,yy,n*180);break;
      case 1:
	Over(x,y,xx,yy,l,n,v,maxstep,scroll,st);break;
      case 2:
	Mess(12,10,"Quit");break;
      case 3:
	Mess(12,10,"Go forward!");Music(3);
      }
    replay=Replay(mf);
  }
  GMODE(3);
}

/*  [VERTICAL] - DOS GAME PROGRAM -         Programmed by Shuichi Yano  */
/*                                              last updated 1995.5.17  */
