#include <iostream>
#include "point.h"
#include "plot_field.h"
#include "initialize.h"

using namespace std;

main(){
  
  static POINT points[MAX_POINTS];
  static POINT ref_position[MAX_POINTS];
  static POINT target_position[MAX_POINTS];

  int n_points; // Number of points
  int n_refs;   // Number of reference points
  int **points_belong; // Belongings of each point

  Plot_field fld;
  
  initialize_by_user_input(points, ref_position, n_points, n_refs, fld);
  points_belong = make2dint(n_points, n_refs);
  
  for(int k = 0;k < 100;k ++){

    determine_cluster(points_belong, 
		      target_position, ref_position, 
		      points, n_points, n_refs);

    calc_gravity_center(points_belong,
			target_position, ref_position, 
			points, n_points, n_refs);

    fld.plot_kmeans(points_belong, 
		    target_position, ref_position,
		    points, n_points, n_refs, k);
    
    pt_unitm(points_belong, 
	     target_position, ref_position, points,
	     n_points, n_refs, 0.1);

    sleep(1);
  }

  char dummy;
  cin >> dummy;

}
