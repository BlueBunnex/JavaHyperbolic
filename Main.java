import javax.swing.JFrame;

// https://elfnor.com/hyperbolic-tiling-in-3d.html

public class Main {
	
	public static void main(String[] args) {
		JFrame window = new JFrame();
		
		window.setTitle("hyperbolic owo");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(800, 800);
		window.setLocationRelativeTo(null);
		
		window.setContentPane(new KleinRenderer(300.0));
		//this.setContentPane(new PoincareRenderer(7, 200.0));
		
		window.setVisible(true);
	}
	
}
