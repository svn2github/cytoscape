float[] data;

void setup(){
  size(200,200);
  String[] stuff   = loadStrings("bargraph.txt");
  data             = float(split(stuff[0],','));
}

void draw(){
  background(255);
  stroke(255);

  float sep  = 200/data.length;
  float maxval = max(data);
  float unit = 200/maxval;
  for (int i=0; i<data.length; i++){
    fill(0);
    rect(i*sep,200-data[i]*unit,sep,data[i]*unit);
  }
}


