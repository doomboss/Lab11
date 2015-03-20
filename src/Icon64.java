import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Icon64 extends ImageIcon {
    private Component observer;
   
    public Icon64(String f, Component c) {
        super(f);
        observer=c;
        observer.setVisible(true);
     
      Image i = observer.createImage(64, 64);
      i.getGraphics().drawImage(getImage(), 0, 0, 64, 64, observer);
      setImage(i);
    }

    public int getIconHeight() {
      return 64;
    }

    public int getIconWidth() {
      return 64;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
      g.drawImage(getImage(), x, y, c);
    }
  }