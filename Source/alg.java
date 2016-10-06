import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class alg {
	int ax, ay, ao, bx, by, bo; // locations b is bot, a is nn

	boolean a = true;
	oi c = new oi();
	int[] feedback = new int[10];
	int[][] array = new int[100][100];
	Random rand = new Random();
	int step = 0;
	public alg(int a, int b, int c, int d) {
		ax = a;
		ay = b;
		bx = c;
		by = d;
	}
	public boolean bcheck(){
		return a;
	}
	public void bot() {
		ao = 0;
		array[ax][ay] = 1;
		for (int i = 0; i < 20; i++) {
			ax++;
			array[ax][ay] = 1;
			if(check(1, ax, ay)){
				break;
			}
			step++;
		}
		for (int i = 0; i < 15; i++) {
			ay++;
			array[ax][ay] = 1;
			if(check(1, ax, ay)){
				break;
			}
			step++;
		}
		for (int i = 0; i < 30; i++) {
			ax++;
			array[ax][ay] = 1;
			ay--;
			array[ax][ay] = 1;
			if(check(1, ax, ay)){
				break;
			}
			step++;
		}
		for (int i = 0; i < 10; i++) {
			ax--;
			array[ax][ay] = 1;
			if(check(1, ax, ay)){
				break;
			}
			step++;
		}
		for (int i = 0; i < 10; i++) {
			ay--;
			array[ax][ay] = 1;
			if(check(1, ax, ay)){
				break;
			}
			step++;
		for (int i = 0; i < 20; i++) {
			ax++;
			array[ax][ay] = 1;
			ay++;
			array[ax][ay] = 1;
			if(check(1, ax, ay)){
				break;
			}
			step++;
		}
		while (a) {
			int attempts = 0;
			ro();
			or: if (array[ax][ay] == 2) {
					rr();
						if (ao == 3) {
							ao = 0;
						} else {
							ao++;
						}
						if (attempts == 3) {
							a = false;
						}
					attempts++;
					ro();
					break or;
				// Control crashes into NN
			} else if (array[ax][ay] == 1) {
				rr();
				if (ao == 3) {
					ao = 0;
				} else {
					ao++;
				}
				if (attempts == 3) {
					rr();
					a = false;
				}
				attempts++;
				ro();
				break or;
				// COntrol crashes into Self
			} else if (ax == 0 | ay == 0 | ax == 100 | ay == 100) {
				// 3 = wall/border
				if (ao == 3) {
					ao = 0;
				} else {
					ao++;
				}
				if (attempts == 3) {
					a = false;
				}
				attempts++;
				ro();
				break or;
			}
			else{
				array[ax][ay] = 1;
				step++;
			}
		}

	}

	public void ro() {
		switch (ao) {
		case 0:
			ay++;
			break;
		case 1:
			ax++;
			break;
		case 2:
			ay--;
			break;
		case 3:
			ax--;
			break;
		}
	}

	public void rr() {
		switch (ao) {
		case 0:
			ay--;
			break;
		case 1:
			ax--;
			break;
		case 2:
			ay++;
			break;
		case 3:
			ax++;
			break;
		}
	}

	public void NN() {

	}

	public int getax() {
		return ax;
	}

	public int getay() {
		return ay;
	}

	public int getbx() {
		return bx;
	}

	public int getby() {
		return by;
	}
	public int getStep(){
		return step;
	}

	public boolean check(int b, int x, int y) {// 0 = empty 1 = bot/red 2 = nn/blue
		if (b == 2) {
			if (array[x][y] == 1) {
				// NN crashes into control
				a = false;
				return false;
			}
			if (array[x][y] == 2) {
				// NN crashes into self
				a = false;
				return false;

			}
			if (bx == 0 | by == 0 | bx == 100 | by == 100) {
				// 3 = wall/border
				a = false;
				return false;
			}
		}
		if (b == 1) {
			if (array[x][y] == 2) {
				a = false;
				// Control crashes into NN
				return false;
			}
			if (array[x][y] == 1) {
				a = false;
				// COntrol crashes into Self
				return false;
			}
			if (ax == 0 | ay == 0 | ax == 100 | ay == 100) {
				// 3 = wall/border
				a = false;
				return false;
			}
		}

		return true;
	}
}
