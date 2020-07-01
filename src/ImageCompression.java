import javax.swing.*;


public class ImageCompression {

	public static void main(String[] args) {
		JFrame frame = new JFrame("Image Compression - VQ");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		new MenuBar(frame);
		frame.add(new MainPanel());

		frame.pack();
		frame.setVisible(true);
	}
}