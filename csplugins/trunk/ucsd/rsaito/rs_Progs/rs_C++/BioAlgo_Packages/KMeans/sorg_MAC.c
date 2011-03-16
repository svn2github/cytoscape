#include <iostream.h>
#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include "sorgar.h"
#include "rsgraph3.h"

#define MAX_DOTS 200
#define N_CLUSTERS 5 

#define WIDTH 768
#define HEIGHT 512

void draw_so(SORGAR dots[], SORGAR grav[], SORGAR unit[], int num,
	     int unitnum, int **dotg, int ite_steps){
  int i,j,k, ct;
  static char ite[20];

  strcpy(ite, "Iteration:");
  doub_to_str(ite_steps * 1.0, 0, &ite[ strlen(ite) ]); 

  rsclear();

  for(i = 0;i < num;i ++){
    for(j = 0;j < unitnum;j ++){
      if(dotg[i][j]){
	rslinep((int)dots[i].arr[0], (int)dots[i].arr[1],
		(int)unit[j].arr[0], (int)unit[j].arr[1], "blue");
	break;
      }
    }
  }


  for(i = 0;i < num;i ++){
    rsarcp((int)dots[i].arr[0], (int)dots[i].arr[1], 3, "yellow");
  }
  
  for(i = 0;i < unitnum;i ++){
    ct = 0;
    for(j = 0;j < num;j ++)ct += dotg[j][i];
    if(ct > 0)rsarcp((int)grav[i].arr[0], (int)grav[i].arr[1], 5, "red");
    rsarcp((int)unit[i].arr[0], (int)unit[i].arr[1], 5, "white");
  }
  
  rssymbolp(WIDTH - 220, HEIGHT - 20, ite, "green");

  rsflush();

}



main(){

  int x, y, b;
  int i,j,k,num,unitnum,flag;
  char dummy;
  char str[20];

  int **dotg;


  static SORGAR dots[MAX_DOTS];
  static SORGAR unit[MAX_DOTS];
  static SORGAR grav[MAX_DOTS];
  SORGAR center;

  rsopen(100,100, WIDTH, HEIGHT);
  rsclear();
  
  flag = 1; num = 0; unitnum = 0;
  while(flag || num <= 0 || unitnum <= 0){
    rsmouse(&x, &y, &b);
    
    switch(keyst(8)){
    case 0:
      dots[ num ].app(double(x));
      dots[ num ].app(double(y));
      rsarcp((int)dots[ num ].arr[0], (int)dots[ num ].arr[1], 3, "yellow");
      rsflush();
      cout << '#' << num << ' ' 
	<< '(' << x << ", " << y << ')' << " - " << b << '\n' << flush;
      num ++;
      break;

    case 1:
      unit[ unitnum ].app(double(x));
      unit[ unitnum ].app(double(y));
      rsarcp((int)unit[ unitnum ].arr[0], (int)unit[ unitnum ].arr[1],
	     5, "white");
      rsflush();
      cout << '#' << unitnum << ' ' 
	<< '(' << x << ", " << y << ')' << " - " << b << '\n' << flush;
      unitnum ++;
      break;

    case 8:
      flag = 0;
      break;
    }
    if(num >= MAX_DOTS || unitnum >= MAX_DOTS)flag = 0;
  }

  dotg = make2dint(num, unitnum);

  for(k = 0;k < 100;k ++){

    so_belong(dots, grav, unit, num, unitnum, dotg);
    so_grav(dots, grav, unit, num, unitnum, dotg);

    draw_so(dots, grav, unit, num, unitnum, dotg, k);

    so_unitm(dots, unit, grav, num, unitnum, dotg, 0.1);

    sleep(1);
  }

  cin >> dummy;
  rsclose();
}
