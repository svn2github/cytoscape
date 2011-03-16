#include <string>
#include <map>

using namespace std;

namespace Usefuls {
  
  class Assoc_array_str {
  private:
    map<string, string> _h;
    
  public:
    void set(string, string);
    string get(string);
  };
  
}

