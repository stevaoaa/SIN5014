package usp.icmc.labes.view;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;


import net.coobird.thumbnailator.Thumbnails;
import usp.icmc.labes.control.Histogram;
import usp.icmc.labes.control.PDI;
import usp.icmc.labes.control.Utils;
import usp.icmc.labes.model.Mask;


public class PDIView {

	private final int WITDH_PANEL_SIZE = 682;
	private final int HEIGHT_PANEL_SIZE = 477;
	
	//higher the value less noise
	int INTERVAL_NOISE = 50;
	
	//intensity
	int INTENSITY = 10;
	
	//mean
	int KERNEL_SIZE = 3;
	
	//quantization
	int FINAL_COLORS_NUMBER = 12;
	
	//splitting
	int COLOR_DISPLACEMENT = 128;
	int SPLITING_DISPLACEMENT = 20;

	private JFrame frmSin;

	private PDI pdi = new PDI();

	BufferedImage image = null;
	BufferedImage processedImage = null;


	/**
	 * Resize the loaded image to a pre defined resolution
	 * 
	 * @param imageToResize
	 * @param labelToDisplay
	 */
	private void resizeDisplay(BufferedImage imageToResize, JLabel labelToDisplay) {

		try {
			processedImage = Thumbnails.of(imageToResize).size(WITDH_PANEL_SIZE, HEIGHT_PANEL_SIZE).asBufferedImage();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File Not Found.");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Can't Read the File.");
		}

		// set into the label
		labelToDisplay.setIcon(new ImageIcon(processedImage));
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PDIView window = new PDIView();
					window.frmSin.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PDIView() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSin = new JFrame();
		frmSin.getContentPane().setBackground(Color.LIGHT_GRAY);
		frmSin.setTitle("SIN5014 - Fundamentos de Processamento Gr\u00E1fico (Stev\u00E3o Andrade)");
		frmSin.setResizable(false);
		frmSin.setBounds(100, 100, 800, 600);
		frmSin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmSin.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmOpen = new JMenuItem("Open");

		mnFile.add(mntmOpen);

		// Exit menu option
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				System.exit(0);

			}
		});

		// save the processed image
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser fs = new JFileChooser(new File("c:\\"));
				fs.setDialogTitle("Save a File (*.jpg)");

				FileNameExtensionFilter filter = new FileNameExtensionFilter("Image File", "jpg", "jpge", "png");
				fs.setFileFilter(filter);

				int result = fs.showSaveDialog(null);

				if (result == JFileChooser.APPROVE_OPTION) {

					String filename = fs.getSelectedFile().toString() + ".jpg";

					File file = new File(filename);

					try {

						ImageIO.write(processedImage, "jpg", file);

					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, "Can't save the file");
					}

				}

			}
		});
		mnFile.add(mntmSave);
		mnFile.add(mntmExit);

		JMenu mnIntensity = new JMenu("Setup");
		menuBar.add(mnIntensity);

		// change the intensity value
		JMenuItem mntmConfiguration = new JMenuItem("Configuration");
		mntmConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				ConfigurationDialog intensityDialog = new ConfigurationDialog();

				intensityDialog.setVisible(true);

			}
		});
		mnIntensity.add(mntmConfiguration);
		frmSin.getContentPane().setLayout(null);

		final JPanel originalPanel = new JPanel();
		originalPanel.setBackground(Color.LIGHT_GRAY);
		originalPanel.setBounds(67, 49, 682, 477);
		frmSin.getContentPane().add(originalPanel);

		final JLabel imageLabel = new JLabel("");
		originalPanel.add(imageLabel);

		// create a histogram of the image
		JButton btnHistogram = new JButton("");
		btnHistogram.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (processedImage == null) {

					JOptionPane.showMessageDialog(null, "Open a image first!");
				} else {

					// histogram test
					int[] array;

					// get the frequency of each color and store into a array
					array = Utils.getFrequencyToHistogram(processedImage);

					Histogram h = new Histogram("Frequency Histogram", "Frequency Histogram", array);
					h.pack();
					h.setVisible(true);

				}
			}
		});
		btnHistogram.setToolTipText("Histogram");
		btnHistogram.setIcon(new ImageIcon(PDIView.class.getResource("/usp/icmc/labes/resources/histogram.png")));
		btnHistogram.setBounds(0, 0, 51, 38);
		frmSin.getContentPane().add(btnHistogram);

		// Set a image to gray scale
		JButton btnGrayScale = new JButton("");
		btnGrayScale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (image == null) {

					JOptionPane.showMessageDialog(null, "Open a image first!");
				} else {
					BufferedImage grayImage = null;
					grayImage = pdi.setGrayScale(processedImage);

					processedImage = grayImage;

					resizeDisplay(processedImage, imageLabel);

				}

			}
		});
		btnGrayScale.setToolTipText("Gray Scale");
		btnGrayScale.setIcon(new ImageIcon(PDIView.class.getResource("/usp/icmc/labes/resources/gray.png")));
		btnGrayScale.setBounds(57, 0, 51, 38);
		frmSin.getContentPane().add(btnGrayScale);

		// increases the value of the intensity in the pixels of the image
		JButton btnIntensityUp = new JButton("");
		btnIntensityUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (image == null) {

					JOptionPane.showMessageDialog(null, "Open a image first!");
				} else {

					BufferedImage intensityImage = null;
					

					intensityImage = pdi.changeColorIntensity(processedImage, INTENSITY);
					processedImage = intensityImage;
					resizeDisplay(processedImage, imageLabel);

				}

			}
		});
		btnIntensityUp.setToolTipText("Intensity Up");
		btnIntensityUp.setIcon(new ImageIcon(PDIView.class.getResource("/usp/icmc/labes/resources/up.png")));
		btnIntensityUp.setBounds(118, 0, 51, 38);
		frmSin.getContentPane().add(btnIntensityUp);

		// reduces the value of the intensity in the pixels of the image
		JButton btnIntensityDown = new JButton("");
		btnIntensityDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (image == null) {

					JOptionPane.showMessageDialog(null, "Open a image first!");
				} else {

					BufferedImage intensityImage = null;

					intensityImage = pdi.changeColorIntensity(processedImage, (-INTENSITY));

					processedImage = intensityImage;
					resizeDisplay(processedImage, imageLabel);

				}

			}
		});
		btnIntensityDown.setToolTipText("Intensity Down");
		btnIntensityDown.setIcon(new ImageIcon(PDIView.class.getResource("/usp/icmc/labes/resources/down.png")));
		btnIntensityDown.setBounds(179, 0, 51, 38);
		frmSin.getContentPane().add(btnIntensityDown);
		
		
		//mean filter
		JButton btnMeanFilter = new JButton("");
		btnMeanFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (image == null) {

					JOptionPane.showMessageDialog(null, "Open a image first!");
				} else {

					BufferedImage meanImage = null;
					Mask kernel = new Mask(KERNEL_SIZE, KERNEL_SIZE);
					
					// mean filter is always 1/ (width x height) of the kernel
					double meanFilter;
					meanFilter = (double) 1 / (kernel.getWidth() * kernel.getHeight());
					
					//weights is a matrix with kernel size
					double[][] weights = new double [kernel.getWidth()][kernel.getHeight()];
					
					//fulfill the matrix with the values of the meanFilter
					for(int i= 0; i< kernel.getWidth(); i++)
						for(int j = 0; j < kernel.getHeight(); j++)
							weights[i][j] = meanFilter;
						
					
					//set the weights to the kernel
					kernel.setWeights(weights);

					
					meanImage = pdi.convolutionFilter(processedImage, kernel, weights);

					processedImage = meanImage;
					resizeDisplay(processedImage, imageLabel);

				}
				
				
			}
		});
		btnMeanFilter.setToolTipText("Mean Filter");
		btnMeanFilter.setIcon(new ImageIcon(PDIView.class.getResource("/usp/icmc/labes/resources/mean.png")));
		btnMeanFilter.setBounds(240, 0, 51, 38);
		frmSin.getContentPane().add(btnMeanFilter);
		
		
		//median filter
		JButton btnMedianFilter = new JButton("");
		btnMedianFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if (image == null) {

					JOptionPane.showMessageDialog(null, "Open a image first!");
				} else {

					BufferedImage medianImage = null;
					Mask kernel = new Mask(KERNEL_SIZE, KERNEL_SIZE);
					
					medianImage = pdi.medianFilter(processedImage, kernel);

					processedImage = medianImage;
					resizeDisplay(processedImage, imageLabel);

				}

				
			}
		});
		btnMedianFilter.setToolTipText("Median Filter");
		btnMedianFilter.setIcon(new ImageIcon(PDIView.class.getResource("/usp/icmc/labes/resources/median.png")));
		btnMedianFilter.setBounds(301, 0, 51, 38);
		frmSin.getContentPane().add(btnMedianFilter);

		// equalize a image
		JButton btnEqualization = new JButton("");
		btnEqualization.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (image == null) {

					JOptionPane.showMessageDialog(null, "Open a image first!");
				} else {

					BufferedImage equalizedImage = null;

					equalizedImage = pdi.imageEqualization(processedImage);

					processedImage = equalizedImage;
					resizeDisplay(processedImage, imageLabel);

				}

			}
		});
		btnEqualization.setToolTipText("Equalizer");
		btnEqualization.setIcon(new ImageIcon(PDIView.class.getResource("/usp/icmc/labes/resources/equalizer.png")));
		btnEqualization.setBounds(362, 0, 51, 38);
		frmSin.getContentPane().add(btnEqualization);
		
		
		//high pass filter
		JButton btnHighPassFilter = new JButton("");
		btnHighPassFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				if (image == null) {

					JOptionPane.showMessageDialog(null, "Open a image first!");
				} else {

					BufferedImage highImage = null;
					Mask kernel = new Mask(KERNEL_SIZE, KERNEL_SIZE);
					
					
					//weights is a matrix with kernel size -> Highpass filter
					double[][] weights = {{-1,-1,-1},{-1,8,-1},{-1,-1,-1}};
										
					//set the weights to the kernel
					kernel.setWeights(weights);

					highImage = pdi.convolutionFilter(processedImage, kernel, weights);

					processedImage = highImage;
					resizeDisplay(processedImage, imageLabel);

				}
				
			}
		});
		btnHighPassFilter.setToolTipText("High Pass Filter");
		btnHighPassFilter
				.setIcon(new ImageIcon(PDIView.class.getResource("/usp/icmc/labes/resources/low_frequency.png")));
		btnHighPassFilter.setBounds(611, 0, 51, 38);
		frmSin.getContentPane().add(btnHighPassFilter);
		
		
		//border filter
		JButton btnBorderOperator = new JButton("");
		btnBorderOperator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (image == null) {

					JOptionPane.showMessageDialog(null, "Open a image first!");
				} else {

					BufferedImage borderImage = null;
					Mask kernel = new Mask(KERNEL_SIZE, KERNEL_SIZE);
					
					
					//weights is a matrix with kernel size -> Sobel upright border detector
					double[][] weights = {{1,2,1},{0,0,0},{-1,-2,-1}};
										
					//set the weights to the kernel
					kernel.setWeights(weights);

					borderImage = pdi.convolutionFilter(processedImage, kernel, weights);

					processedImage = borderImage;
					resizeDisplay(processedImage, imageLabel);

				}
				
			}
		});
		btnBorderOperator.setToolTipText("Border Operator");
		btnBorderOperator.setIcon(new ImageIcon(PDIView.class.getResource("/usp/icmc/labes/resources/border.png")));
		btnBorderOperator.setBounds(672, 0, 51, 38);
		frmSin.getContentPane().add(btnBorderOperator);

		// set noise to a image
		JButton btnNoise = new JButton("");
		btnNoise.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (image == null) {

					JOptionPane.showMessageDialog(null, "Open a image first!");
				} else {

					BufferedImage noisedImage = null;
										
					noisedImage = pdi.generateNoise(processedImage, INTERVAL_NOISE);

					processedImage = noisedImage;
					resizeDisplay(processedImage, imageLabel);

				}

			}
		});
		btnNoise.setIcon(new ImageIcon(PDIView.class.getResource("/usp/icmc/labes/resources/noise.png")));
		btnNoise.setToolTipText("Noise (Salt and pepper)");
		btnNoise.setBounds(733, 0, 51, 38);
		frmSin.getContentPane().add(btnNoise);
		
		//image quantization
		JButton btnQuantization = new JButton("");
		btnQuantization.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (image == null) {

					JOptionPane.showMessageDialog(null, "Open a image first!");
				} else {

					BufferedImage quantizedImage = null;
					
					//this parameter should be controlled
					quantizedImage = pdi.quantizationImage(processedImage, FINAL_COLORS_NUMBER);

					processedImage = quantizedImage;
					resizeDisplay(processedImage, imageLabel);
				}
				
			}
		});
		btnQuantization.setIcon(new ImageIcon(PDIView.class.getResource("/usp/icmc/labes/resources/quantization.png")));
		btnQuantization.setToolTipText("Quantization");
		btnQuantization.setBounds(423, 0, 51, 38);
		frmSin.getContentPane().add(btnQuantization);
		
		
		//image splitting
		JButton btnSpliting = new JButton("");
		btnSpliting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (image == null) {

					JOptionPane.showMessageDialog(null, "Open a image first!");
				} else {

					BufferedImage splitImage = null;
					
					//this parameters need to be controllable
					splitImage = pdi.splitImage(processedImage, COLOR_DISPLACEMENT, SPLITING_DISPLACEMENT);

					processedImage = splitImage;
					resizeDisplay(processedImage, imageLabel);

				}
			}
		});
		btnSpliting.setIcon(new ImageIcon(PDIView.class.getResource("/usp/icmc/labes/resources/split.png")));
		btnSpliting.setToolTipText("Spliting");
		btnSpliting.setBounds(484, 0, 51, 38);
		frmSin.getContentPane().add(btnSpliting);
		
		JButton btnGradient = new JButton("");
		btnGradient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (image == null) {

					JOptionPane.showMessageDialog(null, "Open a image first!");
				} else {

					BufferedImage gradientImage = null;

					gradientImage = pdi.gradientImage(processedImage);

					processedImage = gradientImage;
					resizeDisplay(processedImage, imageLabel);

				}
			}
				
		});
		btnGradient.setIcon(new ImageIcon(PDIView.class.getResource("/usp/icmc/labes/resources/gradient.png")));
		btnGradient.setToolTipText("Gradient");
		btnGradient.setBounds(550, 0, 51, 38);
		frmSin.getContentPane().add(btnGradient);

		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				JFileChooser fs = new JFileChooser(new File("c:\\"));
				fs.setDialogTitle("Select a image");

				FileNameExtensionFilter filter = new FileNameExtensionFilter("Image File", "jpg", "jpge", "png");
				fs.setFileFilter(filter);

				int result = fs.showOpenDialog(null);

				if (result == JFileChooser.APPROVE_OPTION) {

					try {

						String path = fs.getSelectedFile().getPath();

						// have the path.. create the buffered image

						image = ImageIO.read(new File(path));

						// resize and displays
						resizeDisplay(image, imageLabel);

					} catch (FileNotFoundException e) {
						JOptionPane.showMessageDialog(null, "File Not Found.");
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "Can't Read the File.");
					}
				}

			}
		});

	}
}