#include <iostream>           // cout, endl
#include <string>             // string
#include <map>                // map

using namespace std;

namespace Usefuls {
  
  class Assoc_array_str {
  private:
    map<string, string> _h;
    
  public:
    void set(string key, string val); // Sets pair of key and value
    string get(string key); // Gets value corresponding to key
  };
  
  void Assoc_array_str::set(string key, string val){
    cout << "Input key: " << key << endl;
    cout << "Input value: " << val << endl;
    _h.insert(pair<string, string>(key, val));
  }
  
  string Assoc_array_str::get(string key){
    
    map<string, string>::iterator p;
    p = _h.find(key);
    if(p == _h.end()){
      throw "The key " + key + " does not exist.";
    }
    return p->second;
  }

}

#ifndef NOMAIN

int main(){

  Usefuls::Assoc_array_str assoc;

  assoc.set("Konnichiwa", "Hello");
  assoc.set("Konbanwa", "Good evening");
  cout << assoc.get("Konnichiwa") << endl;
  cout << assoc.get("Konbanwa") << endl;
  cout << assoc.get("Konbanwine") << endl;

  return 0;
}

#endif
