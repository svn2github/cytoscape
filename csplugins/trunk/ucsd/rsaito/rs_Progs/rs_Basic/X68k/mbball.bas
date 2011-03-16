   10 dim char dummy(5,31):dim char chr(255)
   20 str s1$[4],s2$[4]
   30 dim int kr(1),xr(1),yr(1),tr(1),pr(1),js(1)
   40 dim char p1dx(255),p1dy(255),p2dx(255),p2dy(255)
   50 dim char dx(255),dy(255),p1dt(255),p2dt(255)
   60 int xp,yp,x,y,s1,s2,x3,x4,c3,c4,y3,t3,a,i,k,j,ct,n1,n2
   70 for i=0 to 255
   80 p1dx(i)=231:p1dy(i)=155:p2dx(i)=8:p2dy(i)=155
   90 dx(i)=i:dy(i)=155:p1dt(i)=0:p2dt(i)=0
  100 next
  110 cls
  120 input"Player 1 name";s1$:if s1$="" then s1$="1P "
  130 input"Player 2 name";s2$:if s2$="" then s2$="2P "
  140 SETSPRITE():SETMUSIC():cls
  150 repeat
  160 TITLE():n1=0:n2=0
  170 if m_stat(3)=0 then m_play(3)
  180 for i=0 to 70000:next
  190 repeat
  200 repeat
  210 for k=0 to 1
  220 for ct=0 to 1500:next
  230 PLCONTROL(0):PLCONTROL(1)
  240 if n1<255 then {
  250 p1dx(n1)=xr(0):p2dx(n1)=xr(1)
  260 p1dy(n1)=yr(0):p2dy(n1)=yr(1)
  270 p1dt(n1)=tr(0):p2dt(n1)=tr(1)
  280 n1=n1+1
  290 }
  300 next
  310 if n2<255 then dx(n2)=x:dy(n2)=y:n2=n2+1
  320 until BLCONTROL()=1
  330 if x>120 then s2=s2+1
  340 if x<120 then s1=s1+1
  350 m_play(2):for a=0 to 70000:next
  360 locate 7,2:print s2:locate 21,2:print s1
  370 locate 6,0:print s2$:locate 20,0:print s1$
  380 if x<=120 then x=223 else x=16
  390 y=150:xp=0:yp=12
  400 vvreset()
  410 until (s1=15 or s2=15)
  420 if s1=15 then x3=210:x4=190:t3=0:c4=3
  430 if s2=15 then x3=22:x4=22:t3=2:c4=2
  440 symbol(x4,40,"Winner!!",1,2,1,15,0):m_play(4)
  450 y3=219:for i=0 to 12
  460 for k=-10 to 10
  470 y3=y3+k:for j=0 to 2000:next
  480 sp_move(c4,x3,y3,t3)
  490 next:next
  500 for i=0 to 200000
  510 next
  520 until 0
  530 end
  540 /*
  550 func PLCONTROL(p;int)
  560 int dpv0,dvp1,dvp2,dvp3
  570 if strig(p+1)=1 and kr(p)=0 then kr(p)=1:pr(p)=13
  580 if kr(p)=0 and tr(p)=0 then {
  590      if (stick(p+1) mod 3)=0 and (stick(p+1)<>0) then {
  600           if (xr(p)<(235*abs(sgn(p=0))+112*abs(sgn(p=1)))) then xr(p)=xr(p)+4
  610           }
  620      if (stick(p+1)+2) mod 3=0 then {
  630           if (xr(p)>(130*abs(sgn(p=0))+0)) then xr(p)=xr(p)-4
  640           }
  650 }
  660 if (strig(p+1) and 2)=2 then {
  670      tr(p)=5:if abs(xr(p)-x)<16 and abs(yr(p)-y)<16 then {
  680           xp=-(xr(p)-x)/1.5#*(1-kr(p))-16*kr(p)+32*kr(p)*p
  690           yp=13-25*kr(p)
  700           m_play(1)
  710           }
  720 }
  730 tr(p)=tr(p)-abs(sgn(tr(p)>0))
  740 js(p)=js(p)+1:if js(p)>1 then js(p)=0
  750 if kr(p)=1 and js(p)=1 then {
  760      yr(p)=yr(p)-pr(p)
  770      pr(p)=pr(p)-1
  780      if pr(p)=-14 then kr(p)=0
  790 }
  800 dpv0=3-p:dpv1=xr(p):dpv2=yr(p)+64:dpv3=sgn(tr(p))+(2*p)
  810 sp_move(dpv0,dpv1,dpv2,dpv3)
  820 endfunc
  830 func int BLCONTROL()
  840 x=x+xp:y=y-yp:if yp>-12 then yp=yp-1
  850 if x<8 and xp<0 then xp=xp*-1:m_play(5)
  860 if x>231 and xp>0 then xp=xp*-1:m_play(5)
  870 sp_move(0,x,y+64,4):sp_move(1,x,221,5)
  880 if y>150 then return(1) else return(0)
  890 endfunc
  900 /*
  910 func TITLE()
  920 int i
  930 m_play(3)
  940 repeat
  950 wipe():cls
  960 symbol(10,10,"BALLBALL",5,10,0,251,0)
  970 for i=0 to 16:symbol(11,11,"BALLBALL",5,10,0,i,0):next
  980 for i=0 to 15:symbol(11,11,"BALLBALL",5,10,0,i,0):next
  990 symbol(10,135," Produced by Gokan ",1,1,2,255,0)
 1000 symbol(28,180,"PUSH 1P TRRIGER !",1,2,2,45,0):sp_disp(0)
 1010 i=30
 1020 while strig(1)<>2
 1030 i=i+1
 1040 if i>150000 then DEMOPLAY():i=0:break
 1050 endwhile
 1060 until i<>0
 1070 vvreset():x=16:y=150:xp=0:yp=12:s1=0:s2=0
 1080 sp_move(0,x,y+64,4):sp_move(1,x,221,5)
 1090 sp_move(2,xr(1),yr(1)+64,2):sp_move(3,xr(0),yr(0)+64,0)
 1100 wipe():fill(0,218,255,255,15):LINEDRAW()
 1110 sp_disp(1)
 1120 locate 7,2:print s2:locate 21,2:print s1
 1130 locate 6,0:print s2$:locate 20,0:print s1$
 1140 endfunc
 1150 func SETSPRITE()
 1160 int k,i,r,j,v,q
 1170 screen 0,2,1,1:window(0,0,255,255)
 1180 sp_init()
 1190 dummy={
 1200 0,0,4,32,15,240,31,248,
 1210 157,122,221,123,223,251,239,247,
 1220 224,7,95,246,63,248,127,254,
 1230 0,0,127,254,63,252,126,126,
 1240 /*
 1250 4,32,15,240,31,248,27,184,
 1260 29,120,31,248,15,240,32,4,
 1270 103,246,115,251,125,251,125,247,
 1280 28,15,99,246,63,248,126,126,
 1290 /*
 1300 0,0,4,32,15,240,31,248,
 1310 94,185,222,187,223,251,239,247,
 1320 224,7,111,250,31,252,127,254,
 1330 0,0,127,254,63,252,126,126,
 1340 /*
 1350 4,32,15,240,31,248,29,216,
 1360 30,184,31,248,15,244,96,6,
 1370 223,238,223,222,239,190,111,188,
 1380 0,26,127,230,63,252,126,126,
 1390 /*
 1400 7,224,31,248,63,252,127,254,
 1410 127,254,255,255,255,255,255,255,
 1420 255,255,255,255,255,255,127,254,
 1430 127,254,63,252,31,248,7,224,
 1440 /*
 1450 0,0,0,0,0,0,0,0,
 1460 0,0,0,0,0,0,0,0,
 1470 0,0,0,0,0,0,15,240,
 1480 127,254,255,255,127,254,15,240 }
 1490 /*
 1500 symbol(61,141,"BALLBALL",2,3,2,15,0)
 1510 symbol(60,140,"BALLBALL",2,3,2,128,0)
 1520 symbol(82,130,"wait a moment!",1,1,2,255,0)
 1530 sp_clr()
 1540 for k=0 to 5
 1550 for i=0 to 31
 1560 r=dummy(k,i)
 1570 for j=0 to 7
 1580 r=r*2
 1590 if r>255 then v=k+1:chr(i*8+j)=v:r=r-256 else v=0:chr(i*8+j)=v
 1600 if i mod 2=0 then q=0 else q=64
 1610 fill(j*8+q,int(i/2)*8+7,j*8+q+7,int(i/2)*8+14,v*16+8)
 1620 next
 1630 next
 1640 sp_def(k,chr)
 1650 next
 1660 for i=0 to 15:sp_color(i,0):next
 1670 sp_color(1,65535)
 1680 sp_color(2,65535)
 1690 sp_color(3,1984)
 1700 sp_color(4,1984)
 1710 sp_color(5,65472)
 1720 sp_color(6,2114)
 1730 endfunc
 1740 /*
 1750 func SETMUSIC()
 1760 int i
 1770 m_init():m_tempo(200)
 1780 for i=1 to 8:m_alloc(i,300):m_assign(i,i):next
 1790 m_trk(1,"v15@45l16o2e&d&c")
 1800 m_trk(2,"v15o4@27l64ededededededededededededededede")
 1810 m_trk(3,"v12@10o4l8cf.f32fcf.f32fcfagf<c.cd.>b-32b-<dc.>a32a4b-.e32a4b-.e32a4b-.e32e4<c4.cd>b-32b-<dc.>a32a4b-.e32eef.")
 1820 m_trk(4,"v12o5l8f2e2d.>b-32b-b-b-<edc.>a32aaa4cdl4cb-agfl8<c>af2")
 1830 m_trk(5,"v13@2l32o2cdcc")
 1840 endfunc
 1850 func vvreset()
 1860 kr={0,0}:xr={231,8}:yr={155,155}:tr={0,0}:pr={0,0}
 1870 endfunc
 1880 func DEMOPLAY()
 1890 int n1,n2,ct,k,d1,d2
 1900 wipe():LINEDRAW():sp_disp(1)
 1910 while strig(1)=0 and n1<250
 1920 for k=0 to 1
 1930 for ct=0 to 3000:next
 1940 d1=sgn(p1dt(n1)):d2=sgn(p2dt(n1))+2
 1950 sp_move(3,p1dx(n1),p1dy(n1)+64,d1)
 1960 sp_move(2,p2dx(n1),p2dy(n1)+64,d2):n1=n1+1
 1970 next
 1980 sp_move(0,dx(n2),dy(n2)+64,4):sp_move(1,dx(n2),221,5)
 1990 n2=n2+1
 2000 endwhile
 2010 endfunc
 2020 func LINEDRAW()
 2030 line(16,162+64,239,162+64,255)
 2040 line(8,179+64,247,179+64,255)
 2050 line(16,162+64,8,179+64,255)
 2060 line(239,162+64,247,179+64,255)
 2070 line(128,162+64,128,179+64,255)
 2080 endfunc
