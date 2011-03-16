#include <iostream.h>

class Cube {

 private:
  int height,width,depth;
 public:
  Cube(int ht, int wd, int dp);
  int volume();
  void depth_change(int dp);         

};

 Cube::Cube(int ht, int wd, int dp){
   height = ht; width = wd; depth = dp;
   
 }

int Cube::volume(){
  return height * width * depth;
}
void Cube::depth_change(int dp){        
  depth = dp;
}

main(){

  Cube thiscube(3,4,5);
  cout << thiscube.volume() << '\n';

  thiscube.depth_change(3);
  cout << thiscube.volume() << '\n';


}
