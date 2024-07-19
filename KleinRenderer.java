import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

public class KleinRenderer extends JPanel implements MouseMotionListener {
	
	private double scale;
	
	private double mouseX = 0;
	private double mouseY = 0;
	
	public KleinRenderer(double scale) {
		
		this.addMouseMotionListener(this);
		this.scale = scale;
		
		new Timer(1000 / 60, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				repaint();
			}
			
		}).start();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.translate(this.getWidth() / 2, this.getHeight() / 2);
		
		// draw poincare circle
		g.setColor(Color.RED);
		g.drawOval((int) -scale, (int) -scale, (int) scale*2, (int) scale*2);
		
		if (Math.sqrt(mouseX * mouseX + mouseY * mouseY) > 0.95)
			return;
		
		// render
		g.setColor(Color.BLACK);
		
		// render a circle using points that are all equidistant to the center
		double x = mouseX,
			   y = mouseY,
			   r = 1;
		
		g.fillOval((int) (x * scale) - 2, (int) (y * scale) - 2, 5, 5);
		
		for (double a=0; a<Math.PI * 2; a += 0.5) {
			double nx = x;
			double ny = y;
			double hr;
			do {
				nx += 0.01 * Math.cos(a);
				ny += 0.01 * Math.sin(a);
				hr = hyperbolicDistance(x, y, nx, ny);
				
			} while (hr < r);
			
			g.fillOval((int) (nx * scale) - 2, (int) (ny * scale) - 2, 5, 5);
		}
	}

	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	public void mouseMoved(MouseEvent e) {
		mouseX = (e.getX() - this.getWidth()  / 2.0) / scale;
		mouseY = (e.getY() - this.getHeight() / 2.0) / scale;
	}
	
	/*
	 * Helper functions
	 */
	
	private static double euclidianDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
	
	// https://en.wikipedia.org/wiki/Beltrami–Klein_model#Distance_formula
	private static double hyperbolicDistanceToOrigin(double x, double y) {
		double euclidianDist = Math.sqrt(x * x + y * y);
		
		return Math.log((1 + euclidianDist) / (1 - euclidianDist)) / 2;
	}
	
	private double hyperbolicDistance(double px, double py, double qx, double qy) {
		if (px < qx) {
			double holdx = px;
			double holdy = py;
			px = qx;
			py = qy;
			qx = holdx;
			qy = holdy;
		}
		
		double slope = (qy - py) / (qx - px);
		
		// get the two points on the circle that intersect the line that our two points make
		double C = py - slope * px;
		double D = 1 + slope * slope;
		double E = C / D;
		double F = Math.sqrt(D - C * C) / D;
		
		double ax = -slope * E + F;
		double ay = E + slope * F;
		double bx = -slope * E - F;
		double by = E - slope * F;
		
		// do the math for the distance
		return Math.log(
					(euclidianDistance(ax, ay, qx, qy) * euclidianDistance(px, py, bx, by))
					/
					(euclidianDistance(ax, ay, px, py) * euclidianDistance(qx, qy, bx, by))
				) / 2;
	}
	
}
