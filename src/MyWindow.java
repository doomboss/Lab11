import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.awt.Dimension;

public class MyWindow implements ActionListener{

    private JFrame frmMwImageProcessing;
    private JMenuItem loadImageItem;
    private JMenuItem saveImageItem;
    private JMenuItem exitItem;
    private JLabel imageLabel;
    private BufferedImage image;
    private int width;
    private int height;
    private float aspect;
    private String filename;
    private File file;
    private JScrollPane scrollPane;
    private JButton darkenBtn;

    private JPanel panel;
    private JButton lightenBtn;
    //transformPixelsation types
    final int darkenType = 10;
    final int lightenType=11;
    final int desaturateType=12;
    final int blurType=13;
    final int flipHType=0;
    final int flipVType=1;
    final int rotateClockwiseType=2;
    final int rotateCounterClockwiseType=3;
   
    private JMenu flipMenu;
    private JMenuItem flipHItem;
    private JMenuItem flipVItem;
    private JMenu rotateMenu;
    private JMenuItem rotateCounterClockwiseItem;
    private JMenuItem rotateClockwiseItem;
   

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MyWindow window = new MyWindow();
                    window.frmMwImageProcessing.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public MyWindow() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmMwImageProcessing = new JFrame();
        frmMwImageProcessing.setTitle("MW Image Processing");
        frmMwImageProcessing.setBounds(100, 100, 720, 600);
        frmMwImageProcessing.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        JMenuBar menuBar = new JMenuBar();
        frmMwImageProcessing.getContentPane().add(menuBar, BorderLayout.NORTH);
       
        JMenu mnNewMenu = new JMenu("File");
        menuBar.add(mnNewMenu);
       
        loadImageItem = new JMenuItem("Load Image");
        mnNewMenu.add(loadImageItem);
        loadImageItem.addActionListener(this);
       
        saveImageItem = new JMenuItem("Save Image");
        mnNewMenu.add(saveImageItem);
        saveImageItem.addActionListener(this);
       
        exitItem = new JMenuItem("Exit");
        mnNewMenu.add(exitItem);
       
        flipMenu = new JMenu("Flip");
        menuBar.add(flipMenu);
       
        flipHItem = new JMenuItem("Flip Horizontal");
        flipMenu.add(flipHItem);
        flipHItem.addActionListener(this);
       
        flipVItem = new JMenuItem("Flip Vertical");
        flipMenu.add(flipVItem);
        flipVItem.addActionListener(this);
       
        rotateMenu = new JMenu("Rotate");
        menuBar.add(rotateMenu);
       
        rotateClockwiseItem = new JMenuItem("Rotate Clockwise");
        rotateMenu.add(rotateClockwiseItem);
        rotateClockwiseItem.addActionListener(this);
       
        rotateCounterClockwiseItem = new JMenuItem("Rotate Counterclockwise");
        rotateMenu.add(rotateCounterClockwiseItem);
        rotateCounterClockwiseItem.addActionListener(this);

        imageLabel = new JLabel();

        scrollPane = new JScrollPane(imageLabel);
        scrollPane.setPreferredSize(new Dimension(500,500));
        frmMwImageProcessing.getContentPane().add(scrollPane, BorderLayout.CENTER);
       
        panel = new JPanel();
        frmMwImageProcessing.getContentPane().add(panel, BorderLayout.SOUTH);
       
        darkenBtn = new JButton("Darken");
        panel.add(darkenBtn);
       
        lightenBtn = new JButton("Lighten");
        panel.add(lightenBtn);
        lightenBtn.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                image=transformPixels(lightenType,0.2f);
                imageLabel.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));
            }});
        darkenBtn.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                image=transformPixels(darkenType,0.8f);
                imageLabel.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));
               
            }});
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int returnValue;
        if(e.getSource().equals(loadImageItem)){

            JFileChooser fc = new JFileChooser();
            //uncomment the line below and add Icon64, ThumbNailFileView to see thumbnails
            //fc.setFileView(new ThumbNailFileView(frmMwImageProcessing));
            returnValue = fc.showOpenDialog(frmMwImageProcessing);
            if(returnValue==JFileChooser.APPROVE_OPTION){
                file = fc.getSelectedFile();
                String path = file.getPath();
                System.out.println("Path: "+path);
                if(isImage(path)){
                    System.out.println("This is an image");
                    loadImage(file);
                }
            }
        }else if(e.getSource().equals(saveImageItem)){
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(file);
            returnValue = fc.showSaveDialog(frmMwImageProcessing);
            if(returnValue==JFileChooser.APPROVE_OPTION){
                file = fc.getSelectedFile();               
                //String path = file.getPath();
                try {
                    ImageIO.write(image, "jpg", file);                   
                } catch (IOException e1) {
                    System.out.println("Error: Failed to save image");
                    e1.printStackTrace();
                }
            }

        }else if(e.getSource()==exitItem){
            System.exit(0);
        }else if(e.getSource()==flipHItem){
            image=transformCoords(flipHType,0);
            imageLabel.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));
        }else if(e.getSource()==flipVItem){
            image=transformCoords(flipVType,0);
            imageLabel.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));
        }else if(e.getSource()==rotateClockwiseItem){
            image=transformCoords(rotateClockwiseType,0);
            imageLabel.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));
        }else if(e.getSource()==rotateCounterClockwiseItem){
            image=transformCoords(rotateCounterClockwiseType,0);
            imageLabel.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));
        }
       
    }
    public boolean isImage(String filename){
        int lastDot = filename.lastIndexOf('.');
        String ext = filename.substring(lastDot+1, filename.length());
        return ext.equals("jpg")||ext.equals("JPG")||ext.equals("gif")||ext.equals("GIF")||ext.equals("bmp")||ext.equals("BMP");
    }
   
    public boolean loadImage(File file){
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            System.out.println("Failed to read file: "+file);           
            e.printStackTrace();
            if(image==null){
                //throw new RuntimeException("Invalid image file: "+file);
                return false;
            }           
        }
        width=image.getWidth();
        height= image.getHeight();
        aspect=height/width;       
       
        imageLabel.setIcon(new ImageIcon(image.getScaledInstance(width, height,image.SCALE_DEFAULT)));
        return true;
    }

    public BufferedImage transformCoords(int xFormType, float param){
        /**
         * Creates a new image by changing the coordinates of existing pixels
         * XFormType:
         *         0: flip horizontal
         *         1: flip vertical
         *         2: rotate 90 clockwise
         *         3. rotate 90 counterclockwise
         */
        BufferedImage after;
        if(xFormType==rotateClockwiseType||xFormType==rotateCounterClockwiseType){
           
            after=new BufferedImage(height, width, image.getType());
        }else{
            after=new BufferedImage(width, height, image.getType());
        }
        for(int i=0;i<width;i++)
            for(int j=0;j<height;j++){
                int rgb=image.getRGB(i, j);            
                switch(xFormType){
                case flipHType:
                    after.setRGB(width-i-1, j, rgb);
                    break;
                case flipVType:
                    after.setRGB(i, height-j-1, rgb);
                    break;
                case rotateClockwiseType:                   
                    after.setRGB(height-j-1, i, rgb);
                    break;
                case rotateCounterClockwiseType:                   
                    after.setRGB(j, width-i-1, rgb);
                    break;
                }   
            }
       
        width=after.getWidth();
        height=after.getHeight();
        return after;
    }
   
    public BufferedImage transformPixels(int xFormType, float param){
        /**
         * Creates a new image "after" from the image by applying a transformation
         * xFormType:
         *         10: darken
         *         11: Lighten
         *         12: desaturate
         *         13: blur
         */
        int alpha=0,red=0,green=0,blue=0;
        int rgb=0; //the 4-byte color of a single pixel
        BufferedImage after=new BufferedImage(width, height, image.getType());

        /**
         * Note: this method only works if the type is TYPE_INT_ARGB
         * Darkens the image by multiplying the R, G and B values by ratio
         *
         * Note on shifting: the color is stored in a 4 byte int.  The bytes are alpha, red, green, blue.
         * The hex mask 0x0ff means the first 24 bits are 0 and the last 8 are 1
         * So to get blue simply do a bit-wise AND operation with the mask
         * For green shift 8 bits to the right, then AND with mask, etc. @mw
         */

        float ratio;
        for(int i=0;i<image.getWidth();i++)
            for(int j=0;j<image.getHeight();j++){
                rgb=image.getRGB(i, j);
                red = (rgb>>16)&0x0ff;
                green=(rgb>>8) &0x0ff;
                blue= (rgb)    &0x0ff;
                alpha=(rgb>>24)&0x0ff;

                switch(xFormType){
                case darkenType: //darken
                    ratio = param;
                    red*=ratio;System.out.println(red);
                    green*=ratio;System.out.println(green);
                    blue*=ratio;System.out.println(blue);
                    break;
                case lightenType: //lightens by moving each color towards 255
                    ratio = param;
                    red += (255-red)*ratio;
                    green+=(255-green)*ratio;
                    blue+=(255-blue)*ratio;
                    break;
                }
                // create a 32 bit int with alpha, red, green blue from left to right
                rgb = (alpha << 24) + (red << 16) + (green << 8) + blue;
                after.setRGB(i, j, rgb);
            }
        return after;
    }

}