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
		
		// generate tiles
		ArrayList<Tile> allTiles = new ArrayList<Tile>();
		
		Tile center = new Tile();
		for (double a = 0; a < Math.PI * 2; a += (Math.PI * 2) / sides) {
			center.addVertex(0.3 * Math.cos(a), 0.3 * Math.sin(a));
		}
		allTiles.add(center);
		
		for (int i=0; i<sides; i++) {
			Tile leaf = center.circleInversionFromEdge(i, g, false);
			allTiles.add(leaf);
			
			allTiles.add(leaf.circleInversionFromEdge((i + 2) % sides, g, true));
			allTiles.add(leaf.circleInversionFromEdge((i + 3) % sides, g, true));
			allTiles.add(leaf.circleInversionFromEdge((i + 4) % sides, g, true));
		}
		
		// render
		g.setColor(Color.BLACK);
		
		for (Tile tile : allTiles)
			tile.render(g);
		
		// draw poincare circle
		g.setColor(Color.RED);
		g.drawOval((int) -scale, (int) -scale, (int) scale*2, (int) scale*2);
	}
	
	private static double dist(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	
	private class Tile {
		
		ArrayList<Double> x, y;
		
		Tile() {
			this.x = new ArrayList<Double>();
			this.y = new ArrayList<Double>();
		}
		
		// circle inversion constructor
		// using this formula: (3:10) https://www.youtube.com/watch?v=-XA47qOjuec
		Tile(Tile source, double px, double py, double pr) {
			this();
			
			pr = pr * pr;
			
			for (int i=0; i<source.x.size(); i++) {
				
				double initDist  = dist(source.x.get(i), source.y.get(i), px, py);
				double finalDist = pr / initDist;
				
				x.add(((source.x.get(i) - px) * finalDist / initDist) + px);
				y.add(((source.y.get(i) - py) * finalDist / initDist) + py);
			}
		}
		
		void addVertex(double x, double y) {
			this.x.add(x);
			this.y.add(y);
		}
		
		void render(Graphics g) {
			
			for (int i=0; i<x.size(); i++) {
				g.drawLine(
						(int) (scale * x.get(i)),
						(int) (scale * y.get(i)),
						(int) (scale * x.get((i+1)%x.size())),
						(int) (scale * y.get((i+1)%x.size()))
					);
			}
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
		
		Tile circleInversionFromEdge(int i, Graphics g, boolean flip) {
			
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
				
				// calculate circle with provided distance to the points
				px = (x1 + x2) / 2 + radius * Math.cos(perpendicularAngle);
				py = (y1 + y2) / 2 + radius * Math.sin(perpendicularAngle);
				
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
			
			return new Tile(this, px, py, radius);
		}
		
	}
	
}
