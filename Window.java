import javax.swing.JFrame;

// https://elfnor.com/hyperbolic-tiling-in-3d.html

public class Window extends JFrame {
	
	public static void main(String[] args) {
		new Window();
	}
	
	public Window() {
		this.setTitle("hyperbolic owo");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(400, 400);
		this.setLocationRelativeTo(null);
		
		this.setContentPane(new PoincareRenderer(7));
		
		this.setVisible(true);
	}

}
