import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;


public class MainPanel extends JPanel {

	private final JPanel mainPanel;
	private JPanel imagePanel;
	private JButton image, training, delete, compress;
	private JSpinner refinement;
	private String imgOriginal, imgTraining, imgCompression;

	public MainPanel() {
		setLayout(new BorderLayout());
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(actionPanel(), BorderLayout.NORTH);
		mainPanel.add(imagePanel(), BorderLayout.CENTER);
		add(mainPanel);
	}

	private JPanel actionPanel() {
		JPanel actionPanel = new JPanel();
		actionPanel.setPreferredSize(new Dimension(300, 60));
		actionPanel.setBorder(new TitledBorder(new EtchedBorder(), "Image Action"));

		image = new JButton("Add Image");
		training = new JButton("Add Training");
		JLabel refinementLabel = new JLabel("Refinement:");
		refinement = new JSpinner(new SpinnerNumberModel(6,1,60,1));
		delete = new JButton("Delete");
		compress = new JButton("Compress");

		image.addActionListener(new ButtonListener());
		training.addActionListener(new ButtonListener());
		delete.addActionListener(new ButtonListener());
		compress.addActionListener(new ButtonListener());

		actionPanel.add(image);
		actionPanel.add(training);
		actionPanel.add(refinementLabel);
		actionPanel.add(refinement);
		actionPanel.add(delete);
		actionPanel.add(compress);

		return actionPanel;
	}

	private JPanel imagePanel() {
		imagePanel = new JPanel(new GridLayout(1,2));
		imagePanel.add(originalPanel());
		imagePanel.add(trainingPanel());
		imagePanel.add(compressionPanel());
		return imagePanel;
	}

	private JPanel originalPanel() {
		JPanel originalPanel = new JPanel(new GridLayout(1,1));
		originalPanel.setPreferredSize(new Dimension(300, 300));
		originalPanel.setBorder(new TitledBorder(new EtchedBorder(), "Original Image"));

		JLabel picLabel = new JLabel();
		if(imgOriginal != null) {
			try {
				BufferedImage image = ImageIO.read(new File(imgOriginal));
				picLabel = new JLabel(new ImageIcon(image));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		originalPanel.add(picLabel);
		return originalPanel;
	}
	
	private JPanel trainingPanel() {
		JPanel trainingPanel = new JPanel(new GridLayout(1,1));
		trainingPanel.setPreferredSize(new Dimension(300, 300));
		trainingPanel.setBorder(new TitledBorder(new EtchedBorder(), "Training Image"));

		JLabel picLabel = new JLabel();
		if(imgTraining != null) {
			try {
				BufferedImage image = ImageIO.read(new File(imgTraining));
				picLabel = new JLabel(new ImageIcon(image));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		trainingPanel.add(picLabel);
		return trainingPanel;
	}

	private JPanel compressionPanel() {
		JPanel compressionPanel = new JPanel(new GridLayout(1,1));
		compressionPanel.setPreferredSize(new Dimension(300, 300));
		compressionPanel.setBorder(new TitledBorder(new EtchedBorder(), "Compression Image"));

		JLabel picLabel = new JLabel();
		if(imgCompression != null) {
			try {
				BufferedImage image = ImageIO.read(new File(imgCompression));
				picLabel = new JLabel(new ImageIcon(image));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		compressionPanel.add(picLabel);
		return compressionPanel;
	}

	public class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent actionEvent) {

			if(actionEvent.getSource() == image || actionEvent.getSource() == training) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.home")));

				FileNameExtensionFilter imf = new FileNameExtensionFilter("Images", ImageIO.getReaderFileSuffixes());
				fc.setFileFilter(imf);

				int action = fc.showSaveDialog(null);
				if(action == JFileChooser.APPROVE_OPTION) {
					
					if(actionEvent.getSource() == image) {
						imgOriginal = fc.getSelectedFile().toString();
					}
					else {
						imgTraining = fc.getSelectedFile().toString();
					}
					System.out.println("IMAGE: " + imgOriginal);
					System.out.println("TRAINING: " + imgTraining);
					revalidate();
				}
			}

			if(actionEvent.getSource() == delete) {
				imgOriginal = null;
				imgTraining = null;
				imgCompression = null;
				revalidate();
			}

			if(actionEvent.getSource() == compress) {
				if(imgOriginal == null) {
					JOptionPane.showMessageDialog(null, "No image!", "Information", JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					System.out.println("REFINEMENT: " + (int) refinement.getValue());
					imgCompression = Compression.compression(imgOriginal, imgTraining, (int) refinement.getValue());
					revalidate();
				}
			}
		}
		
		private void revalidate() {
			mainPanel.remove(imagePanel);
			mainPanel.add(imagePanel());
			mainPanel.revalidate();
		}
	}
}