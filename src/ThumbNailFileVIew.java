import java.awt.Component;
import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.metal.MetalIconFactory;

//ThumbNailFileView.java
//A simple implementation of the FileView class that provides a 32X32 image of
//each GIF or JPG file for its icon. This could be SLOW for large images, as we
//simply load the real image and then scale it.
//taken from http://www.java2s.com/Code/Java/Swing-JFC/showthumbnailsofgraphicfiles.htm
//

class ThumbNailFileView extends FileView {

    private Icon fileIcon = MetalIconFactory.getTreeLeafIcon();

    private Icon folderIcon = MetalIconFactory.getTreeFolderIcon();

    private Component observer;

    public ThumbNailFileView(Component c) {
        // We need a component around to create our icon's image
        observer = c;
    }

    public String getDescription(File f) {
        // We won't store individual descriptions, so just return the
        // type description.
        return getTypeDescription(f);
    }

    public Icon getIcon(File f) {
        // Is it a folder?
        if (f.isDirectory()) {
            return folderIcon;
        }

        // Ok, it's a file, so return a custom icon if it's an image file
        String name = f.getName().toLowerCase();
        if (name.endsWith(".jpg") || name.endsWith(".gif")) {
            return new Icon64(f.getAbsolutePath(), observer);
        }

        // Return the generic file icon if it's not
        return fileIcon;
    }

    public String getName(File f) {
        String name = f.getName();
        return name.equals("") ? f.getPath() : name;
    }

    public String getTypeDescription(File f) {
        String name = f.getName().toLowerCase();
        if (f.isDirectory()) {
            return "Folder";
        }
        if (name.endsWith(".jpg")) {
            return "JPEG Image";
        }
        if (name.endsWith(".gif")) {
            return "GIF Image";
        }
        return "Generic File";
    }

    public Boolean isTraversable(File f) {
        // We'll mark all directories as traversable
        return f.isDirectory() ? Boolean.TRUE : Boolean.FALSE;
    }
}