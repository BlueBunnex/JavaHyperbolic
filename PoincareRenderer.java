import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

public class PoincareRenderer extends JPanel {
	
	private int sides;
	
	public PoincareRenderer(int sides) {
		this.sides = sides;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// calculate
		Tile center = new Tile();
		
		for (double a = 0; a < Math.PI * 2; a += (Math.PI * 2) / sides) {
			center.addVertex(50 * Math.cos(a), 50 * Math.sin(a));
		}
		
		double px = -200;
		double py = 0;
		double pr = dist(px, py, center.x.get(4), center.y.get(4)); 
		Tile side = new Tile(center, px, py, pr);
		
		// render
		g.setColor(Color.BLACK);
		g.translate(this.getWidth() / 2, this.getHeight() / 2);
		
		center.render(g);
		side.render(g);
		
		g.setColor(Color.RED);
		g.fillOval((int) px-2, (int) py-2, 5, 5);
		g.drawOval((int) (px-pr), (int) (py-pr), (int) pr*2, (int) pr*2);
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
				
				System.out.println(initDist + ", " + finalDist);
				
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
						x.get(i).intValue(),
						y.get(i).intValue(),
						x.get((i+1)%x.size()).intValue(),
						y.get((i+1)%x.size()).intValue()
					);
			}
		}
		
	}
	
}