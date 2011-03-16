#include <iostream.h>

class Date {
   int mo, da, yr;
public:
   Date(){}
   Date(int m, int d, int y){ mo = m; da = d; yr = y; }
   void display(){ cout << mo << '/' << da << '/' << yr << '\n'; }
   Date operator+(int);
 };

static int dys[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

Date Date::operator+(int n){

   Date dt = *this;
   n += dt.da;
   while(n > dys[dt.mo - 1]){
      n -= dys[dt.mo - 1];
      if(++ dt.mo == 13){
         dt.mo = 1;
         dt.yr ++;
       }
    }
   dt.da = n;
   return dt;
 }

main(){
   
  Date olddate(2, 20, 90);
  Date newdate;
  olddate.display();
  newdate = olddate + 21;
  olddate.display();
  newdate.display();
}
