package ucsd.jfmoxley.metascape;

import java.util.*;

public class MetNode {

  String id;
  String label;
  String type;
  Graphics graphics;

  public MetNode() {
    graphics = new Graphics();
  }

  public String getId() {return id;}
  public void setId(String s) {id = s;}
  public String getLabel() {return label;}
  public void setLabel(String s) {label = s;}
  public String getType() {return type;}
  public void setType(String s) {type = s;} 

  public class Graphics {

    double x;
    double y;

    public Graphics() {
    }

    public double getX() {return x;}
    public void setX(double d) {x = d;}
    public double getY() {return y;}
    public void setY(double d) {y = d;}

  }

  public void define(String defineId, String defineLabel, String defineType, double defineX, double defineY, Vector attributes){
    id = defineId;
    label = defineLabel;
    type = defineType;
    graphics.x = defineX;
    graphics.y = defineY;
  }

  public void WhoAmI() {
    System.out.println("I am the almight MetNode!!");
  }

}
