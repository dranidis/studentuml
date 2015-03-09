package edu.city.studentuml.util;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//ImageExporter.java
import edu.city.studentuml.view.DiagramView;
import java.awt.Component;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.w3c.tools.codec.Base64Encoder;

public class ImageExporter {

    public static final String PNG = ".png";

    // static method exportToImage() takes a diagram view and saves the
    // drawn elements as an image after showing a file chooser dialog.
    // In the future this method can be extended to handle any image type
    // by adding one method for each image type and giving the user
    // the option to select the format
    public static void exportToImage(DiagramView view, Component parent) {
        BufferedImage image = view.getImage();

        // get the parent component where to show the file chooser
        Frame owner = null;

        if (parent instanceof Frame) {
            owner = (Frame) parent;
        } else {
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        }

        JFileChooser fileChooser = new JFileChooser();

        if (fileChooser.showSaveDialog(owner) != fileChooser.APPROVE_OPTION) {
            return;
        }

        // THIS IS TEMPORARILY; SHOULD BE CHANGED LATER
        File outputFile = fileChooser.getSelectedFile();
        int beginIndex = outputFile.getName().lastIndexOf('.');
        if (beginIndex == -1) {
            outputFile = new File(outputFile.getAbsolutePath() + PNG);
        } else {
            String fileType = outputFile.getName().substring(beginIndex);
            if (!fileType.equals(PNG)) {
                outputFile = new File(outputFile.getAbsolutePath() + PNG);
            }
        }

        exportToPNGImage(image, outputFile);
    }

    public static void exportToPNGImage(BufferedImage image, File file) {
        try {
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportToJPEGImage(BufferedImage image, File file) {
        try {
            ImageIO.write(image, "JPEG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // exports the diagram image in view to a Base64 encoded string
    public static String exportToJPEGImageString(DiagramView view) {
        return exportToJPEGImageStringByScale(view, 1);
    }

    public static String exportToJPEGImageStringByScale(DiagramView view, double scale) {
        BufferedImage image = view.getImageByScale(scale, scale);

        return exportToImageString(image, "JPEG");
    }

    public static String exportToJPEGImageStringByHeight(DiagramView view, int height) {
        BufferedImage image = view.getImageByHeight(height);

        return exportToImageString(image, "JPEG");
    }

    public static String exportToJPEGImageStringByWidth(DiagramView view, int width) {
        BufferedImage image = view.getImageByWidth(width);

        return exportToImageString(image, "JPEG");
    }

    public static String exportToJPEGImageStringByDimensions(DiagramView view, int width, int height) {
        BufferedImage image = view.getImageByDimensions(width, height);

        return exportToImageString(image, "JPEG");
    }

    // exports the diagram image in view to a Base64 encoded string
    public static String exportToPNGImageString(DiagramView view) {
        return exportToPNGImageStringByScale(view, 1);
    }

    public static String exportToPNGImageStringByScale(DiagramView view, double scale) {
        BufferedImage image = view.getImageByScale(scale, scale);

        return exportToImageString(image, "PNG");
    }

    public static String exportToPNGImageStringByHeight(DiagramView view, int height) {
        BufferedImage image = view.getImageByHeight(height);

        return exportToImageString(image, "PNG");
    }

    public static String exportToPNGImageStringByWidth(DiagramView view, int width) {
        BufferedImage image = view.getImageByWidth(width);

        return exportToImageString(image, "PNG");
    }

    public static String exportToPNGImageStringByDimensions(DiagramView view, int width, int height) {
        BufferedImage image = view.getImageByDimensions(width, height);

        return exportToImageString(image, "PNG");
    }

    public static void exportToImageFile(BufferedImage image, File file, String format) {
        try {
            ImageIO.write(image, format, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] exportToImageByteArray(BufferedImage image, String format) {
        byte[] imageAsRawBytes = {};
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, format, baos);
            baos.flush();
            imageAsRawBytes = baos.toByteArray();
            baos.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getStackTrace());
        }

        return imageAsRawBytes;
    }

    private static String exportToImageString(BufferedImage image, String format) {
        String imageString;
        byte[] imageAsRawBytes = exportToImageByteArray(image, format);

        InputStream input = new ByteArrayInputStream(imageAsRawBytes);
        OutputStream output = new ByteArrayOutputStream();

        //bytes to string
        Base64Encoder b64enc = new Base64Encoder(input, output);

        try {
            b64enc.process();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
        }

        imageString = ((ByteArrayOutputStream) output).toString();

        return imageString;
    }
}
