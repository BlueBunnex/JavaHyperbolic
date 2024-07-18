import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

public class PoincareRenderer extends JPanel {
	
	private int sides;
	private double scale;
	
	public PoincareRenderer(int sides, double scale) {
		this.sides = sides;
		this.scale = scale;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.translate(this.getWidth() / 2, this.getHeight() / 2);
		
		Tile root = new Tile();

		// 7 = 0.3
		// 8 = 0.4
		// 9 = 0.48
		// 10= 0.53
		// 20= 0.75
		double s = 0.3;
		
		for (double a = 0; a < Math.PI * 2; a += (Math.PI * 2) / sides) {
			root.addVertex(s * Math.cos(a), s * Math.sin(a));
		}
		
		for (int i=0; i<sides; i++)
			root.circleInversionFromEdge(i, g, false, 2);
		
		// render
		g.setColor(Color.BLACK);
		
		root.render(g);
		
		// draw poincare circle
		g.setColor(Color.RED);
		g.drawOval((int) -scale, (int) -scale, (int) scale*2, (int) scale*2);
	}
	
	private static double dist(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	
	private static double dist2(double x1, double y1, double x2, double y2) {
		return Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
	}
	
	private class Tile {
		
		ArrayList<Double> x, y;
		ArrayList<Tile> children;
		
		Tile() {
			this.x = new ArrayList<Double>();
			this.y = new ArrayList<Double>();
			this.children = new ArrayList<Tile>();
		}
		
		// circle inversion constructor
		// using this formula: (3:10) https://www.youtube.com/watch?v=-XA47qOjuec
		Tile(Tile source, double px, double py, double pr) {
			this();
			
			pr = pr * pr;
			
			for (int i=0; i<source.x.size(); i++) {
				
				double initDist = dist2(source.x.get(i), source.y.get(i), px, py);
				
				x.add(((source.x.get(i) - px) * pr / initDist) + px);
				y.add(((source.y.get(i) - py) * pr / initDist) + py);
			}
		}
		
		void addVertex(double x, double y) {
			this.x.add(x);
			this.y.add(y);
		}
		
		// recursive
		void render(Graphics g) {
			
			for (int i=0; i<x.size(); i++) {
				g.drawLine(
						(int) (scale * x.get(i)),
						(int) (scale * y.get(i)),
						(int) (scale * x.get((i + 1) % x.size())),
						(int) (scale * y.get((i + 1) % x.size()))
					);
			}
			
			for (Tile child : children)
				child.render(g);
		}
		
		// currently doesn't work (calculations by grease164493)
//		Tile circleInversionFromEdge(int i, Graphics g) {
//			
//			double u1 = this.x.get(i);
//			double u2 = this.y.get(i);
//			double v1 = this.x.get((i+1) % this.x.size());
//			double v2 = this.y.get((i+1) % this.y.size());
//			
//			double N1 = u1 * u1 + u2 * u2 + 1;
//			double N2 = v1 * v1 + v2 * v2 + 1;
//			double D  = u1 * v2 - u2 * v1; 
//			
//			// goal: calculate the origin and radius of a circle that
//			// 1) is tangent to both of the edge's points 
//			// 2) forms right angles where it intersects the poincare circle
//			double px = (u2 * N2 - v2 * N1) / D;
//			double py = (v1 * N1 - u1 * N2) / D;
//			double radius = Math.sqrt(px * px + py * py - 1);
//			
//			g.drawOval(
//					(int) ((px - radius) * scale),
//					(int) ((py - radius) * scale),
//					(int) ((radius * 2) * scale),
//					(int) ((radius * 2) * scale)
//				);
//			
//			return new Tile(this, px, py, radius);
//		}
		
		void circleInversionFromEdge(int i, Graphics g, boolean flip, int depth) {
			
			double x1 = this.x.get(i);
			double y1 = this.y.get(i);
			double x2 = this.x.get((i+1)%x.size());
			double y2 = this.y.get((i+1)%y.size());
			
			// goal: calculate the origin and radius of a circle that
			// 1) is tangent to both of the edge's points 
			// 2) forms right angles where it intersects the poincare circle
			double px;
			double py;
			double radius = 0;
			
			do {
				
				double perpendicularAngle = Math.atan2(y1 - y2, x1 - x2) + Math.PI / 2 + (flip ? Math.PI : 0);
				
				double distOff = Math.sqrt(Math.pow(radius, 2) - Math.pow(dist(x1, y1, x2, y2) / 2, 2));
				
				// calculate circle with provided distance to the points
				px = (x1 + x2) / 2 + distOff * Math.cos(perpendicularAngle);
				py = (y1 + y2) / 2 + distOff * Math.sin(perpendicularAngle);
				
				// if this radius isn't close enough to a perpendicular one, increase
				
				// got radius through rewriting an equation found here
				// https://en.wikipedia.org/wiki/Poincaré_disk_model#By_analytic_geometry
				if (Math.abs(radius - Math.sqrt(px * px + py * py - 1)) < 0.00001) {
					break;
				} else {
					radius += 0.000001;
					
					if (radius > 3)
						break;
				}
			} while (true);
			
//			g.drawOval(
//					(int) ((px - radius) * scale),
//					(int) ((py - radius) * scale),
//					(int) ((radius * 2) * scale),
//					(int) ((radius * 2) * scale)
//				);
			
			Tile child = new Tile(this, px, py, radius);
			children.add(child);
			
			if (depth > 0) {
				child.circleInversionFromEdge((i + 2) % sides, g, !flip, depth-1);
				child.circleInversionFromEdge((i + 3) % sides, g, !flip, depth-1);
				child.circleInversionFromEdge((i + 4) % sides, g, !flip, depth-1);
			}
		}
		
	}
	
}
