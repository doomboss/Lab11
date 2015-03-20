import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SwingConstants;
import javax.swing.JButton;


public class ImageProcessing extends JFrame implements ActionListener, ChangeListener{

	private JPanel contentPane;
	private JFileChooser fc;
	private JFileChooser fcs;
	private JMenuItem loadb;
	private JMenuItem saveb;
	private JButton restoreb;
	private File file;
	private static BufferedImage image;
	private static BufferedImage after;
	private static BufferedImage imagebackup;
    private static int width;
    private static int height;
    private static int CLAMP_EDGES=0;
    private static int WRAP_EDGES =1;

    private float aspect;
    private float temp;
    private JLabel imageview;
    private JFrame blurframe;
    private JScrollPane imagescroll;
    private String userformat;
    private Object[] formatoptions = {"jpeg","bmp","jpg"};
    private JMenuItem left90;
	private JMenuItem right90;
	private JMenuItem horizon;
	private JMenuItem vertical;
	private JSlider lightenslider;
	private JSlider darkenslider;
	private JSlider blurbar;
	private int blurradius;
	private float blurlevel;
	private ArrayList<Integer> r;
	private ArrayList<Integer> g;
	private ArrayList<Integer> b;
	private ArrayList<Integer> a;
	private ArrayList<Color> color;
	final int a0 = 0;final int a1 = 1;final int a2 = 2;final int a3 = 3;final int a4 = 4;//0 left 90; 1 right 90; 2 180 right; 3 horizontally flip; 4 vertically flip.
	final int b0 = 0, b1=1; //b0 darken; b1 lighten
	private FileNameExtensionFilter jpeg = new FileNameExtensionFilter("JPEG", "jpeg");
	private FileNameExtensionFilter bmp = new FileNameExtensionFilter("BMP", "bmp");
	private FileNameExtensionFilter jpg = new FileNameExtensionFilter("JPG", "jpg");
	private JMenuItem sharpen;
	private static Kernel kernel;
	private JMenu Colormenu;
	private JMenuItem grey;
	private JMenu mnShrink;
	private JMenuItem shrink;
	private JMenuItem gaussian;
	private ColorSpace cs;
	private ColorConvertOp op;
	private JMenuItem RGB;
	private JMenuItem enlarge;
	

	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ImageProcessing frame = new ImageProcessing();
					frame.setVisible(true);
					frame.setTitle("Image Editor");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ImageProcessing() {
		r = new ArrayList<> ();
		g = new ArrayList<> ();
		b = new ArrayList<> ();
		a = new ArrayList<> ();
		color = new ArrayList<Color> ();
		
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(jpg);
		fc.addChoosableFileFilter(jpeg);
		fc.addChoosableFileFilter(bmp);
		fc.setAcceptAllFileFilterUsed(false);
		fcs = new JFileChooser();
		fcs.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fcs.setFileFilter(jpg);
		fcs.addChoosableFileFilter(jpeg);
		fcs.addChoosableFileFilter(bmp);	
		fcs.setAcceptAllFileFilterUsed(false);
		fcs.addPropertyChangeListener(JFileChooser.FILE_FILTER_CHANGED_PROPERTY, new PropertyChangeListener()
		{
		  public void propertyChange(PropertyChangeEvent evt)
		  {
		    String extension = fcs.getFileFilter().getDescription().toLowerCase();
		    String path = "save";
		    fcs.setSelectedFile(new File( path +"."+ extension));
		  }
		});
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 850, 800);
		contentPane.setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 834, 21);
		contentPane.add(menuBar);
		
		JMenu imagemenu = new JMenu("File");
		menuBar.add(imagemenu);
		
		loadb = new JMenuItem("Load Image");
		imagemenu.add(loadb);
		loadb.addActionListener(this);
		
		saveb = new JMenuItem("Save Image");
		imagemenu.add(saveb);
		saveb.addActionListener(this);
		
		
		JMenu rotateopion = new JMenu("Rotate Option");
		menuBar.add(rotateopion);
		
		left90 = new JMenuItem("Rotate Left 90 degree");
		rotateopion.add(left90);
		left90.addActionListener(this);
		
		right90 = new JMenuItem("Rotate Right 90 degree");
		rotateopion.add(right90);
		right90.addActionListener(this);
		
		horizon = new JMenuItem("Flip Horizontally");
		rotateopion.add(horizon);
		horizon.addActionListener(this);
		
		vertical = new JMenuItem("Flip Vertically");
		rotateopion.add(vertical);
		vertical.addActionListener(this);
		
		JMenu Shrinkmenu = new JMenu("Blur");
		menuBar.add(Shrinkmenu);
		
		sharpen = new JMenuItem("Sharpen");
		Shrinkmenu.add(sharpen);sharpen.addActionListener(this);
		
		gaussian = new JMenuItem("Gaussian Blur");
		Shrinkmenu.add(gaussian);gaussian.addActionListener(this);
		
		Colormenu = new JMenu("Image Color");
		menuBar.add(Colormenu);
		
		grey = new JMenuItem("Grey");
		Colormenu.add(grey);
		grey.addActionListener(this);
		
		RGB = new JMenuItem("RGB?");
		Colormenu.add(RGB);
		RGB.addActionListener(this);
		
		mnShrink = new JMenu("Zoom");
		menuBar.add(mnShrink);
		
		shrink = new JMenuItem("Shrink");
		mnShrink.add(shrink);
		shrink.addActionListener(this);
		
		enlarge = new JMenuItem("Enlarge");
		mnShrink.add(enlarge);
		enlarge.addActionListener(this);
		
			
		imageview = new JLabel();
		imageview.setHorizontalAlignment(SwingConstants.CENTER);
		imageview.setBounds(0, 21, 834, 590);
		contentPane.add(imageview);
		
		imagescroll = new JScrollPane(imageview);
		imagescroll.setBounds(0, 21, 834, 590);
		contentPane.add(imagescroll);
		
		darkenslider = new JSlider();
		darkenslider.setMinorTickSpacing(1);
		darkenslider.setMaximum(10);
		darkenslider.setPaintLabels(true);
		darkenslider.setMajorTickSpacing(2);
		darkenslider.setPaintTicks(true);
		darkenslider.setValue(0);
		darkenslider.setBounds(68, 622, 255, 44);
		contentPane.add(darkenslider);
		darkenslider.addChangeListener(this);
		
		JLabel Darkening = new JLabel("Darken");
		Darkening.setHorizontalAlignment(SwingConstants.CENTER);
		Darkening.setFont(new Font("Times New Roman", Font.BOLD, 14));
		Darkening.setBounds(0, 622, 74, 26);
		contentPane.add(Darkening);
		
		lightenslider = new JSlider();
		lightenslider.setMinorTickSpacing(1);
		lightenslider.setMaximum(10);
		lightenslider.setMajorTickSpacing(2);
		lightenslider.setPaintLabels(true);
		lightenslider.setPaintTicks(true);
		lightenslider.setValue(0);
		lightenslider.setBounds(561, 622, 263, 44);
		contentPane.add(lightenslider);
		lightenslider.addChangeListener(this);
		
		JLabel Lighten = new JLabel("Lighten");
		Lighten.setHorizontalAlignment(SwingConstants.CENTER);
		Lighten.setFont(new Font("Times New Roman", Font.BOLD, 14));
		Lighten.setBounds(485, 622, 74, 26);
		contentPane.add(Lighten);
		
		restoreb = new JButton("Reset");
		restoreb.setFont(new Font("Times New Roman", Font.BOLD, 20));
		restoreb.setBounds(339, 618, 142, 47);
		contentPane.add(restoreb);
		restoreb.addActionListener(this);
		
		blurbar = new JSlider();
		blurbar.setToolTipText("");
		blurbar.setMinimum(1);
		blurbar.setPaintLabels(true);
		blurbar.setPaintTicks(true);
		blurbar.setMaximum(10);
		blurbar.setMajorTickSpacing(1);
		blurbar.setValue(1);
		blurbar.setBounds(68, 695, 255, 55);
		contentPane.add(blurbar);
		blurbar.addChangeListener(this);
	}
	
	public BufferedImage rotate(int rotatemethod){//0 left 90; 1 right 90; 2 180 right; 3 horizontally flip; 4 vertically flip.
		if(rotatemethod==0||rotatemethod==1){
			after = new BufferedImage(height, width, image.getType());
		}
		else if(rotatemethod==4||rotatemethod==3){
			after = new BufferedImage(width, height, image.getType());
		}
		for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
        int rgb=image.getRGB(i, j);
		switch(rotatemethod){
		case a0://0 left 90
			after.setRGB(j, width-i-1, rgb);
			break;
		case a1://1 right 90
			after.setRGB(height-j-1, i, rgb);
			break;
		case a3:// 3 horizontally flip
			after.setRGB(width-i-1, j, rgb);
			break;
		case a4://4 vertically flip.
			after.setRGB(i, height-j-1, rgb);
			break;
		} 
            }
            }
		
		width=after.getWidth();
	    height=after.getHeight();
			return after;
	}
	
	public BufferedImage shrinking(){
		int l=0,k=0;
		try{
		after = new BufferedImage(width/3, height/3, image.getType());
		for(int i=1;i<width;i+=3){
            for(int j=1;j<height;j+=3){
        		int rgb=image.getRGB(i, j);
        		after.setRGB(k, l, rgb);
        		l++;
            }
            l=0;
            k++;
            }
		}
		catch(ArrayIndexOutOfBoundsException ourofbound){
		}
		catch(Exception error){
			error.printStackTrace();
		}
		return after;
	}
	public BufferedImage enlarge(){
		int l=0,k=0;
		try{
		after = new BufferedImage(width*2, height*2, image.getType());
		for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
        		int rgb=image.getRGB(i, j);
        		after.setRGB(k, l, rgb);
        		after.setRGB(k, l+1, rgb);
        		after.setRGB(k+1, l, rgb);
        		after.setRGB(k+1, l+1, rgb);
        		l+=2;
            }
            l=0;
            k+=2;
            }
		}
		catch(ArrayIndexOutOfBoundsException ourofbound){
		}
		return after;
	}
	
	public void resetRGB(){
		r.clear();g.clear();b.clear();a.clear();
		for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
        int rgb=image.getRGB(i, j);
        r.add((rgb>>16)&0x0ff);
        g.add((rgb>>8) &0x0ff);
        b.add((rgb)    &0x0ff);
        a.add((rgb>>24)&0x0ff);
        }
	    }
	}
	
	public BufferedImage lightchange(int lightmethod, float parameter){
		float ratio;
		int red=0,green=0,blue=0,alpha=0,rgb;
		int count=0;
		after = new BufferedImage(width, height, image.getType());
		for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
		switch(lightmethod){
		case b0://0 darken
			ratio = parameter;
			red=(int) ((double)r.get(count)*ratio);
            green=(int) ((double)g.get(count)*ratio);
            blue=(int) ((double)b.get(count)*ratio);
			break;
		case b1://1 lighten
			ratio = parameter;
			red = (int) ((double)r.get(count)+ (255-r.get(count))*ratio);
            green= (int) ((double)g.get(count) + (255-g.get(count))*ratio);
            blue= (int) ((double)b.get(count) + (255-b.get(count))*ratio);
			break;	
		}
		rgb = (alpha << 24) + (red << 16) + (green << 8) + blue;
        after.setRGB(i, j, rgb);
        count++;
            }       
            }	
		return after;
	}
	
	public BufferedImage blur(int blurtype){
		switch(blurtype){
		case 0:
			int temp = blurradius*blurradius;
			float[] matrix = new float[temp];
			for(int i=0; i<temp; i++)
				matrix[i] = (float)1/temp;
			kernel = new Kernel(blurradius, blurradius, matrix);
			BufferedImageOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
			after = op.filter(image, null);
			break;
		case 1:
			kernel = new Kernel(3, 3,
					new float[] {
					-1f, -1f, -1f,
					-1f, 9f, -1f,
					-1f, -1f, -1f});
			BufferedImageOp op1 = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
			after = op1.filter(image, null);
			break;
		case 2:
			kernel = new Kernel(5, 5,
					new float[] {
					0.01f, 0.02f, 0.04f, 0.02f, 0.01f,
					0.02f, 0.04f, 0.08f, 0.04f, 0.02f,
					0.04f, 0.08f, 0.16f, 0.08f, 0.04f,
					0.02f, 0.04f, 0.08f, 0.04f, 0.02f,
					0.01f, 0.02f, 0.04f, 0.02f, 0.01f});
			BufferedImageOp op2 = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
			after = op2.filter(image, null);
			break;
			}
		return after;
	}
	
	public float getBlurlevel() {
		return blurlevel;
	}

	public void setBlurlevel(float blurlevel) {
		this.blurlevel = blurlevel;
		kernel = gaussiankernel(blurlevel);
	}
	
	 public BufferedImage gaussianfilter() {
	        int[] inPixels = new int[width*height];
	        int[] outPixels = new int[width*height];
	        image.getRGB( 0, 0, width, height, inPixels, 0, width );
			convolveAndTranspose(kernel, inPixels, outPixels, width, height, CLAMP_EDGES);
			convolveAndTranspose(kernel, outPixels, inPixels, height, width, CLAMP_EDGES);
	        after.setRGB( 0, 0, width, height, inPixels, 0, width );
	        return after;
	    }

		public static void convolveAndTranspose(Kernel kernel, int[] inPixels, int[] outPixels, int width, int height, int edgeAction) {
			float[] matrix = kernel.getKernelData( null );
			int cols = kernel.getWidth();
			int cols2 = cols/2;

			for (int y = 0; y < height; y++) {
				int index = y;
				int ioffset = y*width;
				for (int x = 0; x < width; x++) {
					float nr = 0, ng = 0, nb = 0, na = 0;
					int moffset = cols2;
					for (int col = -cols2; col <= cols2; col++) {
						float f = matrix[moffset+col];

						if (f != 0) {
							int ix = x+col;
							if ( ix < 0 ) {
								if ( edgeAction == CLAMP_EDGES )
									ix = 0;
								else if ( edgeAction == WRAP_EDGES )
									ix = (x+width) % width;
							} else if ( ix >= width) {
								if ( edgeAction == CLAMP_EDGES )
									ix = width-1;
								else if ( edgeAction == WRAP_EDGES )
									ix = (x+width) % width;
							}
							int rgb = inPixels[ioffset+ix];
							na += f * ((rgb >> 24) & 0xff);
							nr += f * ((rgb >> 16) & 0xff);
							ng += f * ((rgb >> 8) & 0xff);
							nb += f * (rgb & 0xff);
						}
					}
					int ia = (int)(na+0.5);
					int ir = (int)(nr+0.5);
					int ig = (int)(ng+0.5);
					int ib = (int)(nb+0.5);
					outPixels[index] = (ia << 24) | (ir << 16) | (ig << 8) | ib;
	                index += height;
				}
			}
		}

	public BufferedImage color(int coloroption){
		switch (coloroption){
		case 0: //grey
			cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			op = new ColorConvertOp(cs, null);
			after = op.filter(image, null);
			break;
		case 1: //red
			try{
			cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
			op = new ColorConvertOp(cs, null);
			after = op.filter(image, null);}
			catch(Exception e){
				e.printStackTrace();
			}
			break;
		}
		return after;
	}
	
	public void actionPerformed(ActionEvent item) {
		if(item.getSource().equals(loadb)){
			int userclick = fc.showOpenDialog(ImageProcessing.this);
			if(userclick == fc.APPROVE_OPTION){
				file = fc.getSelectedFile();
				System.out.println("File Path: "+file);
				Color c;
				try{
					System.out.println("Reading image");
					image = ImageIO.read(file);
				    width = image.getWidth();
				    height = image.getHeight();
				    aspect = height/width;
				    for(int i=0;i<width;i++){
			            for(int j=0;j<height;j++){
			        int rgb=image.getRGB(i, j);
			        c = new Color(rgb);
			        r.add((rgb>>16)&0x0ff);
	                g.add((rgb>>8) &0x0ff);
	                b.add((rgb)    &0x0ff);
	                a.add((rgb>>24)&0x0ff);
			        color.add(c);
			        }
				    }
				    imageview.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));
				    imagebackup = image;
				}
				catch(IOException ioe){
					System.out.println("Cannot access the file...");
					ioe.printStackTrace();
				}
			}
			else if(userclick== fc.CANCEL_OPTION){
				System.out.println("Cancel by user...");
			}
		}
		else if(item.getSource().equals(saveb)){
			fcs.setCurrentDirectory(file);
			fcs.setSelectedFile(file);
			int userclick = fcs.showSaveDialog(ImageProcessing.this);
			if(userclick == fcs.APPROVE_OPTION){
				
				System.out.println("Saving image");
				try{					
					ImageIO.write(image, userformat, file);
					System.out.println("Save Success!");
				}
				catch(IOException ioe){
					System.out.println("Cannot save to the selected file...");
				}
			}
			else if(userclick== fc.CANCEL_OPTION){
				System.out.println("Cancel by user...");
			}
		}
		else if(item.getSource().equals(left90)){
			try{image = rotate(a0);resetRGB();
			imageview.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));}
			catch(NullPointerException npe){
			System.out.println("no input image");
		}}
		else if(item.getSource().equals(right90)){
			try{image = rotate(a1);resetRGB();
			imageview.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));}
			catch(NullPointerException npe){
			System.out.println("no input image");}}
		else if(item.getSource().equals(horizon)){
			try{image = rotate(a3);resetRGB();
			imageview.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));}
			catch(NullPointerException npe){
			System.out.println("no input image");}}
		else if(item.getSource().equals(vertical)){
			try{image = rotate(a4);resetRGB();
			imageview.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));}
			catch(NullPointerException npe){
			System.out.println("no input image");}}
		else if(item.getSource().equals(restoreb)){
			image=imagebackup;
			width = image.getWidth();
			height = image.getHeight();
			aspect = height/width;
			resetRGB();
			imageview.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));
			darkenslider.setValue(0);lightenslider.setValue(0);
			}
		else if(item.getSource().equals(sharpen)){
			try{image = blur(1);
			imageview.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));
			}			
			catch(Exception e){
				e.printStackTrace();
			}
		}
		else if(item.getSource().equals(grey)){
			try{
				image = color(0);
				imageview.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));
			}			
			catch(Exception e){
				e.printStackTrace();
			}
		}
		else if(item.getSource().equals(shrink)){
			try{
				image = shrinking();
				width = image.getWidth();
				height = image.getHeight();		
				imageview.setIcon(new ImageIcon(image.getScaledInstance(width, height,after.SCALE_DEFAULT)));
				resetRGB();
			}			
			catch(Exception e){
				e.printStackTrace();
			}
		}
		else if(item.getSource().equals(gaussian)){
			try{image = blur(2);
			imageview.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));
			}			
			catch(Exception e){
				e.printStackTrace();
			}
		}
		else if(item.getSource().equals(RGB)){
			try{
				image = color(1);
				imageview.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));
			}			
			catch(Exception e){
				e.printStackTrace();
			}
		}
		else if(item.getSource().equals(enlarge)){
			image = enlarge();
			width = image.getWidth();
			height = image.getHeight();		
			imageview.setIcon(new ImageIcon(image.getScaledInstance(width, height,after.SCALE_DEFAULT)));
			resetRGB();
		}
		}

	public void stateChanged(ChangeEvent slider) {
		if(slider.getSource().equals(darkenslider)){
			try{temp= (float) (1- (double) darkenslider.getValue()/10);
			image = lightchange(b0, temp);
			imageview.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));}
			catch(NullPointerException npe){
				System.out.println("no input image");}
		}
		else if(slider.getSource().equals(lightenslider)){
			try{temp= (float) ( (double) lightenslider.getValue()/10);
			image = lightchange(b1, temp);
			imageview.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));}
			catch(NullPointerException npe){
				System.out.println("no input image");}
		}
		else if(slider.getSource().equals(blurbar)){
			try{
			image = imagebackup;
			setBlurlevel((float)blurbar.getValue());	
			blurradius = blurbar.getValue();
			image = blur(0);
			imageview.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));}
			catch(Exception error){
				error.printStackTrace();
			}
		}
		
	}
	public static Kernel gaussiankernel(float radius) {
		int r = (int)Math.ceil(radius);
		int rows = r*2+1;
		float[] matrix = new float[rows];
		float sigma = radius/3;
		float sigma22 = 2*sigma*sigma;
		float sigmaPi2 = 2*(float)Math.PI*sigma;
		float sqrtSigmaPi2 = (float)Math.sqrt(sigmaPi2);
		float radius2 = radius*radius;
		float total = 0;
		int index = 0;
		for (int row = -r; row <= r; row++) {
			float distance = row*row;
			if (distance > radius2)
				matrix[index] = 0;
			else
				matrix[index] = (float)Math.exp(-(distance)/sigma22) / sqrtSigmaPi2;
			total += matrix[index];
			index++;
		}
		for (int i = 0; i < rows; i++)
			matrix[i] /= total;

		return new Kernel(rows, 1, matrix);
	}
}