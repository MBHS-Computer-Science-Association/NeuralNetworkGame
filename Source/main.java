import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class main extends JPanel {
	int ax, ay, bx, by;
	Random rand = new Random();

	public main() {
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		ax = 25;
		ay = 50;
		bx = 75;
		by = 50;
		g.setColor(Color.red);
		alg a = new alg(ax, ay, bx, by);

	}

}
