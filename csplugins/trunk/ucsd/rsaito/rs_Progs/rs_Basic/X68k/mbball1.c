
main(){
  char dummy[5][31];
  char chr[255];
  char s1[4], s2[4]; /* str s1$[4],s2$[4] */
  int kr[1], xr[1], yr[1], tr[1], pr[1], js[1];
  char p1dx[255], p1dy[255], p2dx[255], p2dy[255];
  char dx[255], dy[255], p1dt[255], p2dt[255];
  int xp,yp,x,y,sc1,sc2,x3,x4,c3,c4,y3,t3,a,i,k,j,ct,n1,n2;
  for(i=0;i <= 255;i ++){
    p1dx[i] = 231;
    p1dy[i] = 155;
    p2dx[i] = 8;
    p2dy[i] = 155;
    dx[i] = i;
    dy[i] = 155;
    p1dt[i] = 0;
    p2dt[i] = 0;
  }

  /* cls; Clear Screen */

  printf("Player 1 name:");
  scanf("%s", s1);
  if(s1[0] == '\0')strcpy(s1, "1P ");
  printf("Player 2 name:");
  scanf("%s", s2);
  if(s2[0] == '\0')strcpy(s2, "2P ");

  SETSPRITE();
  SETMUSIC();

  /* cls; Clear Screen */

  do {
    TITLE();
    n1=0;
    n2=0;
    if(m_stat(3) == 0)m_play(3);
    for(i = 0;i <= 70000;i ++);
    do {
      do {
	for(k = 0;k <= 1;k ++){
	  for(ct = 0;ct <= 1500;ct ++);
	  PLCONTROL(0);
	  PLCONTROL(1);
	  if n1<255 then {
	      p1dx[n1] = xr[0];
	      p2dx[n1] = xr[1];
	      p1dy[n1] = yr[0];
	      p2dy[n1] = yr[1];
	      p1dt[n1] = tr[0];
	      p2dt[n1] = tr[1];
	      n1 += 1;
	    }
	}
	if(n2 < 255){
	  dx(n2)=x;
	  dy(n2)=y;
	  n2 += 1;
	} 
      } while(BLCONTROL() != 1);
      if(x > 120)sc2 += 1;
      if(x < 120)sc1 += 1;
      m_play(2);
      for(a = 0;a <= 70000;a ++);
      /* locate 7,2 */
      printf("%s", sc2);
      /* locate 21,2 */
      printf("%s", sc1);
      /* locate 6,0 */
      printf("%s", s2);
      /* locate 20,0 */
      printf("%s", s1);
      
      if(x <= 120)x = 223;
      else x = 16;
      y = 150;
      xp = 0;
      yp = 12;
      vvreset();
    } while (sc1 < 15 && sc2 < 15);
    if(sc1 == 15){
      x3 = 210;
      x4 = 190;
      t3 = 0;
      c4 = 3;
    }
    if(sc2 == 15){
      x3 = 22;
      x4 = 22;
      t3 = 2;
      c4 = 2;
    }
    symbol(x4,40,"Winner!!",1,2,1,15,0);
    m_play(4);
    y3 = 219;
    for(i = 0;i <= 12;i ++){
      for(k = -10;k <= 10;k ++){
        y3 += k;
        for(j = 0;j <= 2000;j ++);
        sp_move(c4,x3,y3,t3);
      }
    }
    for(i = 0;i <= 200000;i ++);
  } while(1);
}

/* END of main */

void PLCONTROL(int p){

  int dpv0, dvp1, dvp2, dvp3;

  if(strig(p+1) == 1 && kr(p) = 0){
    kr(p) = 1;
    pr(p) = 13;
  }
  if(kr(p) == 0 && tr(p) = 0){
    if((stick(p+1) % 3) == 0 && (stick(p+1)<>0)){
      if(xr(p) < (235*abs(sgn(p==0))+112*abs(sgn(p==1))))xr(p)=xr(p)+4;
    }
    if((stick(p+1)+2) % 3 == 0){
      if((xr(p) > (130*abs(sgn(p==0))+0)))xr(p)=xr(p)-4;
    }
  }
  
  if (strig(p+1) and 2)=2 then {
      tr(p)=5:if abs(xr(p)-x)<16 and abs(yr(p)-y)<16 then {
           xp=-(xr(p)-x)/1.5#*(1-kr(p))-16*kr(p)+32*kr(p)*p
           yp=13-25*kr(p)
           m_play(1)
           }
    }
 tr(p)=tr(p)-abs(sgn(tr(p)>0))
 js(p)=js(p)+1:if js(p)>1 then js(p)=0
 if kr(p)=1 and js(p)=1 then {
      yr(p)=yr(p)-pr(p)
      pr(p)=pr(p)-1
      if pr(p)=-14 then kr(p)=0
 }
 dpv0=3-p:dpv1=xr(p):dpv2=yr(p)+64:dpv3=sgn(tr(p))+(2*p)
 sp_move(dpv0,dpv1,dpv2,dpv3)
 endfunc
 func int BLCONTROL()
 x=x+xp:y=y-yp:if yp>-12 then yp=yp-1
 if x<8 and xp<0 then xp=xp*-1:m_play(5)
 if x>231 and xp>0 then xp=xp*-1:m_play(5)
 sp_move(0,x,y+64,4):sp_move(1,x,221,5)
 if y>150 then return(1) else return(0)
 endfunc
 /*
 func TITLE()
 int i
 m_play(3)
 repeat
 wipe():cls
 symbol(10,10,"BALLBALL",5,10,0,251,0)
 for i=0 to 16:symbol(11,11,"BALLBALL",5,10,0,i,0):next
 for i=0 to 15:symbol(11,11,"BALLBALL",5,10,0,i,0):next
 symbol(10,135," Produced by Gokan ",1,1,2,255,0)
 symbol(28,180,"PUSH 1P TRRIGER !",1,2,2,45,0):sp_disp(0)
 i=30
 while strig(1)<>2
 i=i+1
 if i>150000 then DEMOPLAY():i=0:break
 endwhile
 until i<>0
 vvreset():x=16:y=150:xp=0:yp=12:sc1=0:sc2=0
 sp_move(0,x,y+64,4):sp_move(1,x,221,5)
 sp_move(2,xr(1),yr(1)+64,2):sp_move(3,xr(0),yr(0)+64,0)
 wipe():fill(0,218,255,255,15):LINEDRAW()
 sp_disp(1)
 locate 7,2:print sc2:locate 21,2:print sc1
 locate 6,0:print s2$:locate 20,0:print s1$
 endfunc
 func SETSPRITE()
 int k,i,r,j,v,q
 screen 0,2,1,1:window(0,0,255,255)
 sp_init()
 dummy={
 0,0,4,32,15,240,31,248,
 157,122,221,123,223,251,239,247,
 224,7,95,246,63,248,127,254,
 0,0,127,254,63,252,126,126,
 /*
 4,32,15,240,31,248,27,184,
 29,120,31,248,15,240,32,4,
 103,246,115,251,125,251,125,247,
 28,15,99,246,63,248,126,126,
 /*
 0,0,4,32,15,240,31,248,
 94,185,222,187,223,251,239,247,
 224,7,111,250,31,252,127,254,
 0,0,127,254,63,252,126,126,
 /*
 4,32,15,240,31,248,29,216,
 30,184,31,248,15,244,96,6,
 223,238,223,222,239,190,111,188,
 0,26,127,230,63,252,126,126,
 /*
 7,224,31,248,63,252,127,254,
 127,254,255,255,255,255,255,255,
 255,255,255,255,255,255,127,254,
 127,254,63,252,31,248,7,224,
 /*
 0,0,0,0,0,0,0,0,
 0,0,0,0,0,0,0,0,
 0,0,0,0,0,0,15,240,
 127,254,255,255,127,254,15,240 }
 /*
 symbol(61,141,"BALLBALL",2,3,2,15,0)
 symbol(60,140,"BALLBALL",2,3,2,128,0)
 symbol(82,130,"wait a moment!",1,1,2,255,0)
 sp_clr()
 for k=0 to 5
 for i=0 to 31
 r=dummy(k,i)
 for j=0 to 7
 r=r*2
 if r>255 then v=k+1:chr(i*8+j)=v:r=r-256 else v=0:chr(i*8+j)=v
 if i mod 2=0 then q=0 else q=64
 fill(j*8+q,int(i/2)*8+7,j*8+q+7,int(i/2)*8+14,v*16+8)
 next
 next
 sp_def(k,chr)
 next
 for i=0 to 15:sp_color(i,0):next
 sp_color(1,65535)
 sp_color(2,65535)
 sp_color(3,1984)
 sp_color(4,1984)
 sp_color(5,65472)
 sp_color(6,2114)
 endfunc
 /*
 func SETMUSIC()
 int i
 m_init():m_tempo(200)
 for i=1 to 8:m_alloc(i,300):m_assign(i,i):next
 m_trk(1,"v15@45l16o2e&d&c")
 m_trk(2,"v15o4@27l64ededededededededededededededede")
 m_trk(3,"v12@10o4l8cf.f32fcf.f32fcfagf<c.cd.>b-32b-<dc.>a32a4b-.e32a4b-.e32a4b-.e32e4<c4.cd>b-32b-<dc.>a32a4b-.e32eef.")
 m_trk(4,"v12o5l8f2e2d.>b-32b-b-b-<edc.>a32aaa4cdl4cb-agfl8<c>af2")
 m_trk(5,"v13@2l32o2cdcc")
 endfunc
 func vvreset()
 kr={0,0}:xr={231,8}:yr={155,155}:tr={0,0}:pr={0,0}
 endfunc
 func DEMOPLAY()
 int n1,n2,ct,k,d1,d2
 wipe():LINEDRAW():sp_disp(1)
 while strig(1)=0 and n1<250
 for k=0 to 1
 for ct=0 to 3000:next
 d1=sgn(p1dt(n1)):d2=sgn(p2dt(n1))+2
 sp_move(3,p1dx(n1),p1dy(n1)+64,d1)
 sp_move(2,p2dx(n1),p2dy(n1)+64,d2):n1=n1+1
 next
 sp_move(0,dx(n2),dy(n2)+64,4):sp_move(1,dx(n2),221,5)
 n2=n2+1
 endwhile
 endfunc
 func LINEDRAW()
 line(16,162+64,239,162+64,255)
 line(8,179+64,247,179+64,255)
 line(16,162+64,8,179+64,255)
 line(239,162+64,247,179+64,255)
 line(128,162+64,128,179+64,255)
 endfunc
