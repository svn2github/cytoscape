package BallBall;

import BallBall.Ball;
import BallBall.Player;

public class BallBall_Virtual_Space {

	final static int UPPER_END =   0;
	final static int LOWER_END = 255;
	final static int LEFT_END  =   0;
	final static int RIGHT_END = 255;
	
	final static int UPPER_BOUNDARY =  10;
	final static int LOWER_BOUNDARY = 200;
	final static int LEFT_BOUNDARY  =  15;
	final static int RIGHT_BOUNDARY = 210;
	
	private Ball ball;
	
	public BallBall_Virtual_Space(){
		ball = new Ball(this);
		ball.set_position(10, 20);
		ball.set_velocity(3, 5);
	}

	public int get_upper_end(){
		return UPPER_END;
	}
	
	public int get_lower_end(){
		return LOWER_END;
	}
	
	public int get_left_end(){
		return LEFT_END;
	}
	
	public int get_right_end(){
		return RIGHT_END;
	}
	
	public int get_upper_boundary(){
		return UPPER_BOUNDARY;
	}
	
	public int get_lower_boundary(){
		return LOWER_BOUNDARY;
	}
	
	public int get_left_boundary(){
		return LEFT_BOUNDARY;
	}
	
	public int get_right_boundary(){
		return RIGHT_BOUNDARY;
	}

	public int move(){
		int ball_status;
		ball_status = ball.move();
		
		return ball_status;
	}
	
	public Ball get_ball(){
		return ball;
	}
	
	public static void main(String args[]){
		BallBall_Virtual_Space vspc = new BallBall_Virtual_Space();

		int i;
		for(i = 0;i < 100;i ++){
			vspc.move();
			System.out.println(vspc.get_ball().get_position_x() + ", " + vspc.get_ball().get_position_y());
			/* System.out.println(vspc.get_upper_boundary()); */
		}

		for(i = 0;i < 100;i ++){
			vspc.move();
			System.out.println(vspc.get_ball().get_position_x() + ", " + vspc.get_ball().get_position_y());
			/* System.out.println(vspc.get_upper_boundary()); */
		}

	}
	
}
