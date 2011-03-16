package Applet1;

import java.applet.Applet;
import java.awt.*;

public class BoundBall extends Applet implements Runnable {
    private Point      p1;     //ボール1の座標
    private Point      p2;     //ボール2の座標
    private Point      v1;     //ボール1の速度ベクトル
    private Point      v2;     //ボール2の速度ベクトル
    private Dimension  size;   //ボールの大きさ
    private Rectangle  region; //座標が移動できる領域
    private boolean    loop;   //ループの継続条件
    private Image      img;    //ダブルバッファリング用イメージ
    private Graphics2D gc;     //↑のグラフィックコンテキスト

    public void init() {
        setBackground(new Color(192,224,255));
        size = new Dimension(40,40);
        p1 = new Point((getWidth()  - size.width )/3,
                       (getHeight() - size.height)/2);
        p2 = new Point((getWidth()  - size.width )*2/3,
                       (getHeight() - size.height)/2);
        v1 = new Point(-5,5);
        v2 = new Point( 5,5);
        region = new Rectangle(0, 0, getWidth() - size.width,
                                     getHeight() - size.height);
        loop = true;
        img  = createImage(getWidth(),getHeight());//イメージ作成
        gc   = (Graphics2D)img.getGraphics();      //Graphics取得
        gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        //自分を渡してThreadクラスを作成しスレッド起動
        new Thread(this).start();
    }
    public void paint(Graphics g) {
        g.drawImage(img,0,0,null);
    }
    public void update(Graphics g) {
        paint(g);
    }
    //座標pを速度ベクトルvに従って移動させ
    //領域の外に出ている場合は反射させる
    private void nextPosition(Point p, Point v, Rectangle region) {
        p.x += v.x;
        p.y += v.y;
        if(p.x < region.x) {
            p.x = region.x * 2 - p.x;
            v.x *= -1;
        }else if(p.x > region.x + region.width) {
            p.x = (region.x + region.width) * 2 - p.x;
            v.x *= -1;
        }
        if(p.y < region.y) {
            p.y = region.y * 2 - p.y;
            v.y *= -1;
        }else if(p.y > region.y + region.height) {
            p.y = (region.y + region.height) * 2 - p.y;
            v.y *= -1;
        }
    }
    public void destroy() {
        loop = false;   //ループの終了
    }
    public void run() {
        while(loop) {
            nextPosition(p1,v1,region);
            nextPosition(p2,v2,region);
            gc.clearRect(0,0,getWidth(),getHeight());//クリア
            gc.setColor(Color.BLUE);   //ボール1を青で描く
            gc.fillOval(p1.x, p1.y, size.width, size.height);
            gc.setColor(Color.RED);    //ボール2を赤で描く
            gc.fillOval(p2.x, p2.y, size.width, size.height);
            repaint();
            try {
                Thread.sleep(33);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
