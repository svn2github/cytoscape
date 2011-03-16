package BallBall;

public class Ball {
	private int x, y;
	private int dx, dy;
	private BallBall_Virtual_Space bbv_space;
	
	public static final int BFLAG_RIGHT_BOUNDARY = 1;
	public static final int BFLAG_LEFT_BOUNDARY  = 2;
	public static final int BFLAG_UPPER_BOUNDARY = 4;
	public static final int BFLAG_LOWER_BOUNDARY = 8;
	
	public static final int MAX_VELOCITY = 10;
	
	public Ball(BallBall_Virtual_Space vspc){
		bbv_space = vspc;
	}

	public void set_position(
			int new_x, int new_y){
		x = new_x;
		y = new_y;
	}
	
	public int get_position_x(){
		return x;
	}
	public int get_position_y(){
		return y;
	}
	
	public void set_velocity(int new_dx, int new_dy){
		dx = new_dx;
		dy = new_dy;
	}
	
	public int move(){
		int bflag = 0;
		x += dx;
		y += dy;
		dy += 1;
		
		if(x >= bbv_space.get_right_boundary() && dx > 0){
			x = bbv_space.get_right_boundary();
			dx *= -1;
			bflag += BFLAG_RIGHT_BOUNDARY;
		}
		if(x <= bbv_space.get_left_boundary() && dx < 0){
			x = bbv_space.get_left_boundary();
			dx *= -1;
			bflag += BFLAG_LEFT_BOUNDARY;
		}
		if(y >= bbv_space.get_lower_boundary() && dy > 0){
			y = bbv_space.get_lower_boundary();
			dy *= -1;
			bflag += BFLAG_LOWER_BOUNDARY;
		}
		if(y <= bbv_space.get_upper_boundary() && dy < 0){
			y = bbv_space.get_upper_boundary();
			dy *= -1;
			bflag += BFLAG_UPPER_BOUNDARY;
		}
		

		if(dy > MAX_VELOCITY){ dy = MAX_VELOCITY; }
		if(dy < -MAX_VELOCITY){ dy = -MAX_VELOCITY; }
		
		return bflag;
	}
	
	public void print_status_info(){
		System.out.println(x + "," + y + " D:" + dx + "," + dy);
	}
	
	public static void main(String args[]){
		BallBall_Virtual_Space vspc = new BallBall_Virtual_Space();
		Ball ball = new Ball(vspc);
		ball.set_position(10, 20);
		ball.set_velocity(3, 5);
		int i;
		for(i = 0;i < 100;i ++){
			ball.move();
			ball.move();
			System.out.println(ball.get_position_x() + " " + ball.get_position_y());
			ball.print_status_info();
			/* System.out.println(vspc.get_upper_boundary()); */
		}

	}
	
}
