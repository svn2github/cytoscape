#include <X11/Xlib.h>
#include <X11/Xutil.h>

/* rsfunctions are for single window only */

Display *d;
Window w;
GC gc;
Font f;
XImage *image;

unsigned long black,white,blue,yellow,red,green,purple,gray,orange;
unsigned long pink,brown,cyan;
unsigned long moccasin,cornsilk,ivory,seashell,honeydew,azure,lavender;
unsigned long navy,sky_blue,turquoise,aquamarine,khaki,gold,goldenrod;
unsigned long sienna,peru,burlywood,beige,wheat,tan,chocolate,firebrick;
unsigned long salmon,coral,tomato,maroon,violet,plum,orchid,thistle;
unsigned long gainsboro,linen,bisque;
unsigned long papaya_whip,blanched_almond,peach_puff,lemon_chiffon;
unsigned long mint_cream,alice_blue,misty_rose,royal_blue,sky_blue;
unsigned long pale_turquoise,spring_green,green_yellow,olive_drab;
unsigned long light_goldenrod,sandy_brown,orange_red,hot_pink,blue_violet;
unsigned long cornsilk2,aquamarine2,gray60,gray70,gray80,gray40,gray50;

unsigned long MyColor(d,color)
     Display *d;
     char *color;
{    
     Colormap cmap;
     XColor c0,c1;

     cmap = DefaultColormap(d, 0);

     XAllocNamedColor(d, cmap, color, &c1, &c0);

     return(c1.pixel);
     }

rsopen(x,y,width,height)
   int x;
   int y;
   unsigned int width;
   unsigned int height;
{    
     XSetWindowAttributes att;
     XEvent e;
     

     if((d = XOpenDisplay(NULL)) == NULL){
       puts("can't open display\n");
       printf("display name:%s\n",XDisplayName(NULL));
       exit(1);
     }

     w = XCreateSimpleWindow(d,DefaultRootWindow(d),x,y,width,height,
			     5,MyColor(d,"purple"),MyColor(d,"black"));
/*
     att.override_redirect = True;
     XChangeWindowAttributes(d,w,CWOverrideRedirect,&att);
*/
     XSelectInput(d,w,ButtonPressMask | ButtonReleaseMask | ExposureMask);

     gc = XCreateGC(d, w, 0, 0); 

/*     f = XLoadFont(d,"fg-40"); */
     XStoreName(d,w,"rsGraph System");

     XMapWindow(d,w);
     XFlush(d);

     while(XNextEvent(d,&e),e.xany.window != w && e.type != Expose);

     image = XGetImage(d,w,0,0,width,height,AllPlanes,ZPixmap);

     black = MyColor(d,"black");
     white = MyColor(d,"white");
     blue  = MyColor(d,"blue");
     yellow= MyColor(d,"yellow");
     red =   MyColor(d,"red");
     green = MyColor(d,"green");
     purple= MyColor(d,"purple");
     gray =  MyColor(d,"gray");
     orange= MyColor(d,"orange");
     pink =  MyColor(d,"pink");
     brown = MyColor(d,"brown");
     cyan =  MyColor(d,"cyan");
     moccasin =  MyColor(d,"moccasin");
     cornsilk =  MyColor(d,"cornsilk");
     ivory =  MyColor(d,"ivory");
     seashell =  MyColor(d,"seashell");
     honeydew =  MyColor(d,"honeydew");
     azure =  MyColor(d,"azure");
     lavender =  MyColor(d,"lavender");
     navy =  MyColor(d,"navy");
     sky_blue =  MyColor(d,"sky blue");
     turquoise = MyColor(d,"turquoise");
     aquamarine =  MyColor(d,"aquamarine");
     khaki =  MyColor(d,"khaki");
     gold =  MyColor(d,"gold");
     goldenrod =  MyColor(d,"goldenrod");
     sienna =  MyColor(d,"sienna");
     peru =  MyColor(d,"peru");
     burlywood =  MyColor(d,"burlywood");
     beige =  MyColor(d,"beige");
     wheat =  MyColor(d,"wheat");
     tan = MyColor(d,"tan");
     chocolate =  MyColor(d,"chocolate");
     firebrick =  MyColor(d,"firebrick");
     salmon =  MyColor(d,"salmon");
     coral =  MyColor(d,"coral");
     tomato = MyColor(d,"tomato");
     maroon = MyColor(d,"maroon");
     violet = MyColor(d,"violet");
     plum = MyColor(d,"plum");
     orchid = MyColor(d,"orchid");
     thistle =  MyColor(d,"thistle");
     gainsboro =  MyColor(d,"gainsboro");
     linen = MyColor(d,"linen");
     bisque = MyColor(d,"bisque");
     papaya_whip = MyColor(d,"papaya whip");
     blanched_almond = MyColor(d,"blanched almond");
     peach_puff =  MyColor(d,"peach puff");
     lemon_chiffon =  MyColor(d,"lemon chiffon");
     mint_cream =  MyColor(d,"mint cream");
     alice_blue =  MyColor(d,"alice blue");
     misty_rose = MyColor(d,"misty rose");
     royal_blue =  MyColor(d,"royal blue");
     sky_blue =  MyColor(d,"sky blue");
     pale_turquoise =  MyColor(d,"pale turquoise");
     spring_green =  MyColor(d,"spring green");
     green_yellow =  MyColor(d,"green yellow");
     olive_drab =  MyColor(d,"olive drab");
     light_goldenrod =  MyColor(d,"light goldenrod");
     sandy_brown =  MyColor(d,"sandy brown");
     orange_red =  MyColor(d,"orange red");
     hot_pink =  MyColor(d,"hot pink");
     blue_violet =  MyColor(d,"blue violet");
     cornsilk2 =  MyColor(d,"cornsilk2");
     aquamarine2 =  MyColor(d,"aquamarine2");
     gray60 = MyColor(d,"gray60");
     gray70 = MyColor(d,"gray70");
     gray80 =  MyColor(d,"gray80");
     gray40 = MyColor(d,"gray40");
     gray50 = MyColor(d,"gray50");

}

rsclose(){

     XCloseDisplay(d);
     }


rspset(x,y,color)
     int x,y;
     char *color;
{
     XGCValues gv;


     if(color != NULL){
          gv.foreground = MyColor(d,color);
          XChangeGC(d,gc,GCForeground, &gv);
     }

     XDrawPoint(d,w,gc,x,y);
     XFlush(d);

}


     
rsline(x1,y1,x2,y2,color)
     int x1,y1,x2,y2;
     char *color;
{
     XGCValues gv;

     if(color != NULL){
          gv.line_width = 2;
          gv.foreground = MyColor(d,color);
          XChangeGC(d,gc,GCLineWidth | GCForeground, &gv);
     } 
     
     XDrawLine(d,w,gc,x1,y1,x2,y2);
     XFlush(d);
}
   
rscirc(x,y,r,color)
     int x,y,r;
     char *color;
{
     XGCValues gv;

     if(color != NULL){
          gv.foreground = MyColor(d,color);
	  XChangeGC(d,gc, GCForeground, &gv);
     }
     
     XDrawArc(d, w, gc, x-r, y-r, r*2, r*2, 0, 360*64);
     XFlush(d);
}

rsrect(x,y,width,height,color)
     int x,y,width,height;
     char *color;
{
     XGCValues gv; 
     
     if(color != NULL){
          gv.foreground = MyColor(d,color);
	  XChangeGC(d,gc, GCForeground, &gv);
     }
     
     XDrawRectangle(d, w, gc, x, y, width, height);  
     XFlush(d);
}

rsfill(x,y,width,height,color)
     int x,y,width,height;
     char *color;
{
     XGCValues gv;

     if(color != NULL){
	  gv.foreground = MyColor(d,color);
	  XChangeGC(d,gc, GCForeground, &gv);
     }

     XFillRectangle(d, w, gc, x, y, width, height);
}

rsfill_i(x,y,width,height,cp)
int x,y,width,height;
unsigned long cp;
{
  int n,s;
  for(s = y;s < y + height;s ++)
    for(n = x;n < x + width;n ++)
      XPutPixel(image,n,s,cp);
}

rs_imageput(x,y,width,height)
{
  XPutImage(d,w,gc,image,x,y,x,y,width,height);
  XFlush(d);
}

rsmouse(x,y,b)
     int *x;
     int *y;
     int *b;
{
     XEvent e;
     while(1){ XNextEvent(d,&e);
	       if (e.type == ButtonPress){  *x = e.xbutton.x;
					    *y = e.xbutton.y;
					    *b = e.xbutton.button;
					    }
               if (e.type == ButtonRelease)break;
            }
     }
    
rssymbol(x,y,string,color)
     int x,y;
     char *string;
     char *color;
{
     XGCValues gv;

     if(color != NULL){
          gv.foreground = MyColor(d,color);
          XChangeGC(d,gc,GCForeground,&gv);
     } 

     XSetFont(d,gc,f);
     XDrawString(d,w,gc,x,y,string,strlength(string));

     XFlush(d);
}

int keyst(keyseg)
     int keyseg;
{
     char keys_return[32];
     XQueryKeymap(d, keys_return);
     return (int)keys_return[keyseg];
}



rsmsstat(x,y,b)
     int *x,*y,*b;
{
  Window root_r,child_r;
  int rx_r,ry_r;

  XQueryPointer(d,w,&root_r,&child_r,&rx_r,&ry_r,
		x,y,b);
}

int strlength(string)
     char *string;
{    int ct = 0;
     while(string[ct]!= '\0')ct++;
     return ct; 
     }
     





