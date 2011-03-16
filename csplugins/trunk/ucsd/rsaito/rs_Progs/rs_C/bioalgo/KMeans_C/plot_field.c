#include <string.h>
#include "rsgraph3.h"
#include "point.h"
#include "plot_field.h"

#define WIDTH 768
#define HEIGHT 512
#define WIN_X 100
#define WIN_Y 100

Plot_field::Plot_field(){

  rsopen(WIN_X, WIN_Y, WIDTH, HEIGHT);
  rsclear();

}

Plot_field::~Plot_field(){

  rsclose();

}

int Plot_field::key(int key_group){

  return keyst(key_group);

}

void Plot_field::mouse(int *x, int *y, int *b){

  rsmouse(x, y, b);

}

void Plot_field::plot_each_point(int x, int y){

  rsarcp(x, y, 3, "yellow");
  rsflush();

}

void Plot_field::plot_each_ref(int x, int y){

  rsarcp(x, y, 5, "white");
  rsflush();

}

void Plot_field::plot_kmeans(int **belong, 
			     POINT grav[], POINT unit[], POINT dots[],
			     int num, int unitnum, int ite_steps){
  int i,j,k, ct;
  static char ite[20];

  strcpy(ite, "Iteration:");
  doub_to_str(ite_steps * 1.0, 0, &ite[ strlen(ite) ]); 

  rsclear();

  for(i = 0;i < num;i ++){
    for(j = 0;j < unitnum;j ++){
      if(belong[i][j]){
	rslinep((int)dots[i].get_array(0),
		(int)dots[i].get_array(1),
		(int)unit[j].get_array(0),
		(int)unit[j].get_array(1), "blue");
	break;
      }
    }
  }


  for(i = 0;i < num;i ++){
    rsarcp((int)dots[i].get_array(0),
	   (int)dots[i].get_array(1), 3, "yellow");
  }
  
  for(i = 0;i < unitnum;i ++){
    ct = 0;
    for(j = 0;j < num;j ++)ct += belong[j][i];
    if(ct > 0)rsarcp((int)grav[i].get_array(0),
		     (int)grav[i].get_array(1), 5, "red");
    rsarcp((int)unit[i].get_array(0),
	   (int)unit[i].get_array(1), 5, "white");
  }
  
  rssymbolp(WIDTH - 220, HEIGHT - 20, ite, "green");

  rsflush();

}



