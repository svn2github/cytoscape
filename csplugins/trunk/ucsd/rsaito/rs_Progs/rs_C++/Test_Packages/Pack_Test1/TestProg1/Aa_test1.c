#include <iostream>
#include "../Usefuls/Assoc_array.h"

using namespace std;

int main(){
  Usefuls::Assoc_array_str assoc;

  assoc.set("Konnichiwa", "Hello");
  cout << assoc.get("Konnichiwa") << endl;

  return 0;
}
