#include <iostream>
#include "point.h"
#include "plot_field.h"
#include "initialize.h"

using namespace std;

void initialize_by_user_input(POINT dots[],
			      POINT unit[],
			      int &n_points,
			      int &n_refs,
			      Plot_field &fld){
  
  int x, y, b;
  int flag;

  flag = 1; n_points = 0; n_refs = 0;
  while(flag || n_points <= 0 || n_refs <= 0){
    fld.mouse(&x, &y, &b);
    
    switch(fld.key(8)){
    case 0:
      dots[ n_points ].append(double(x));
      dots[ n_points ].append(double(y));
      fld.plot_each_point((int)dots[ n_points ].get_array(0),
			  (int)dots[ n_points ].get_array(1));
      cout << '#' << n_points << ' ' 
	<< '(' << x << ", " << y << ')' << " - " << b << '\n' << flush;
      n_points ++;
      break;

    case 1:
      unit[ n_refs ].append(double(x));
      unit[ n_refs ].append(double(y));
      fld.plot_each_ref((int)unit[ n_refs ].get_array(0),
			(int)unit[ n_refs ].get_array(1));
      cout << '#' << n_refs << ' ' 
	<< '(' << x << ", " << y << ')' << " - " << b << '\n' << flush;
      n_refs ++;
      break;

    case 8:
      flag = 0;
      break;
    }
    if(n_points >= MAX_POINTS || n_refs >= MAX_POINTS)flag = 0;
  }


}
