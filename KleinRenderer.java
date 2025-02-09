import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
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
		
		// set the center of the canvas to the drawing origin
		g.translate(this.getWidth() / 2, this.getHeight() / 2);
		
		// draw exterior disk
		g.setColor(Color.RED);
		g.drawOval((int) -scale, (int) -scale, (int) scale*2, (int) scale*2);
		
		// avoid weird calculations by returning if the mouse is outside of the hyperbolic plane
		if (Math.sqrt(mouseX * mouseX + mouseY * mouseY) > 0.99)
			return;
		
		// generate a circle around the mouse using many points that are all
		// equidistant to it in hyperbolic space
		// (we use approximations here because I suck at math)
		Polygon poly = new Polygon();
		
		final double desiredRadius = 0.5;
		
		for (double a=0; a<Math.PI * 2; a += 0.1) {
			double x = mouseX;
			double y = mouseY;
			double r;
			do {
				x += 0.001 * Math.cos(a);
				y += 0.001 * Math.sin(a);
				r = hyperbolicDistance(mouseX, mouseY, x, y);
				
			} while (r < desiredRadius);
			
			poly.addPoint((int) (x * scale), (int) (y * scale));
		}
		
		// render it!
		g.setColor(Color.BLACK);
		g.fillOval((int) (mouseX * scale) - 2, (int) (mouseY * scale) - 2, 5, 5);
		g.drawPolygon(poly);
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
		
		double slope = (qy - py) / (qx - px);
		
		// get the two points on the circle that intersect the line that our two points make
		// (if the two points have the same x value this does NOT work)
		if (px < qx) {
			double holdx = px;
			double holdy = py;
			px = qx;
			py = qy;
			qx = holdx;
			qy = holdy;
		}
		
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
					/ (euclidianDistance(ax, ay, px, py) * euclidianDistance(qx, qy, bx, by))
					) / 2;
	}
	
}
