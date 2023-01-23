package edu.city.studentuml.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.net.URL;
import java.awt.image.BufferedImage;


import javax.swing.ImageIcon;
import javax.swing.UIManager;

/**
 * Loads an image icon from resources.
 * The image is invcerted if the theme is dark.
 */
public class MyImageIcon extends ImageIcon {

    public MyImageIcon(URL imgURL) {
        super(imgURL);

        if (Theme.isDark()) {
            invertIcon();
        }
    }

    private void invertIcon() {
        Image i = this.getImage();
        BufferedImage image = new BufferedImage(i.getWidth(null), i.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = image.createGraphics();
        g2.drawImage(i, 0, 0, null);
        g2.dispose();

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int argb = image.getRGB(x, y);
                int a = (argb >> 24) & 0xff;
                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = argb & 0xff;
                r = 255 - r;
                g = 255 - g;
                b = 255 - b;
                argb = (a << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, y, argb);
            }
        }
        setImage(image);
    }

}
