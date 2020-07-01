import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuBar {

	public MenuBar(JFrame frame) {
		JMenuBar menuBar = new JMenuBar();

		JMenu menu = new JMenu("Menu");
		JMenuItem aboutMenu = new JMenuItem("About");
		JMenuItem exitMenu = new JMenuItem("Exit");

		menu.add(aboutMenu);
		menu.add(exitMenu);
		menuBar.add(menu);
		frame.setJMenuBar(menuBar);
		
		aboutMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				JOptionPane.showMessageDialog(null, "Image Compression - VQ", "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		exitMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				System.exit(0);
			}
		});
	}
}
