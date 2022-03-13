package com.ocr.service;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * An utils class.
 */
public class Utils {
    private static final String FOLDER_PATH = "images-base/";
    protected static final int RESIZE = 44;
    private static final int[][] CONFUSION_MATRIX = new int[10][10];

    /**
     * Match an image with a learning chain vector and stock them into a map.
     *
     * @return the map of images and learning chain vectors
     */
    public Map<ImagePlus, double[][]> matchImageVector() {
        Map<ImagePlus, double[][]> imageVectorMap = new HashMap<>();
        ArrayList<ImagePlus> imagesList = listModifiedImages();

        for (ImagePlus img : imagesList) {
            int[][] matrix = constructMatrixWithBinaryValues(img);
            imageVectorMap.put(img, learningChain(matrix));
        }

        return imageVectorMap;
    }

    /**
     * Determine the vector which corresponds to the learning chain.
     *
     * @param matrix matrix of the image
     * @return the vector which corresponds to the learning chain
     */
    private double[][] learningChain(int[][] matrix) {
        double[][] vResult = new double[3][];

        vResult[0] = new double[] { new Isoperimeter().isoperimeterReport(matrix) };
        vResult[1] = new Zoning().zoningVectoring(matrix);
        vResult[2] = new VerticalProfile().verticalProfileVectoring(matrix);

        return vResult;
    }

    /**
     * Create a list of binarized and resized images.
     *
     * @return the list of binarized and resized images
     */
    protected ArrayList<ImagePlus> listModifiedImages() {
        File folder = new File(FOLDER_PATH);
        File[] files = folder.listFiles();
        ArrayList<ImagePlus> imagesModified = new ArrayList<>();

        for (File file : files) {
            String fileType = "Unknown type.";

            try {
                fileType = Files.probeContentType(file.toPath());
            } catch (IOException ioException) {
                System.out.println("Unknown type for " + file.getName() + ".");
            }

            if (file.isFile() && file.getName().charAt(0) != '.' && file.getName().charAt(0) != '+'
                    && file.getName().charAt(0) != '-' && fileType.equals("image/png")) {
                ImagePlus image = resizeAndBinarizeImage(IJ.openImage(FOLDER_PATH + file.getName()));
                imagesModified.add(image);
            }
        }

        return imagesModified;
    }

    /**
     * Binarize and resize an image.
     *
     * @param image image to binarize and to resize
     * @return the binarized and resized image
     */
    private ImagePlus resizeAndBinarizeImage(ImagePlus image) {
        new ImageConverter(image).convertToGray8();
        ImageProcessor ip = image.getProcessor();
        byte[] pixels = (byte[]) ip.getPixels();
        int height = ip.getHeight();
        int width = ip.getWidth();

        // binarization
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pix = pixels[i * width + j] & 0xff;
                if (pix < 120) {
                    pixels[i * width + j] = (byte) 0;
                } else {
                    pixels[i * width + j] = (byte) 255;
                }
            }
        }

        // resize the image to 15x15
        image.setProcessor(ip.resize(RESIZE,RESIZE));

        return image;
    }

    /**
     * Construct a matrix, with binary values, of an image.
     *
     * @param image image to convert into matrix
     * @return the matrix of the image
     */
    protected int[][] constructMatrixWithBinaryValues(ImagePlus image) {
        ImageProcessor ip = image.getProcessor();
        int matrix[][] = new int[RESIZE][RESIZE];

        for (int i = 0; i < RESIZE; i++) {
            for (int j = 0; j < RESIZE; j++) {
                if (ip.getPixelValue(i,j) == 255.0) {
                    ip.putPixelValue(i, j, 1);
                }

                matrix[i][j] = (int) ip.getPixelValue(i,j);
            }
        }

        return matrix;
    }

    /**
     * Calculate the euclidean distance between two vectors which correspond to learning chains.
     *
     * @param v1 first learning chain vector
     * @param v2 second learning chain vector
     * @return the euclidean distance between v1 and v2
     */
    private double vectorDistance(double[][] v1, double[][] v2) {
        double euclideanDistance = 0;
        double zoningVerticalProfileAverage = 0;

        for (int i = 0; i < v1.length; ++i) {
            for (int j = 0; j < v1[i].length; ++j) {
                // zoning or vertical profile vector
                if (i == 1 || i == 2) {
                    zoningVerticalProfileAverage += Math.pow(Math.abs(v1[i][j] - v2[i][j]), 2);
                } else {
                    euclideanDistance += Math.pow(Math.abs(v1[i][j] - v2[i][j]), 2);
                }
            }

            // zoning or vertical profile vector
            if (i == 1 || i == 2) {
                euclideanDistance += zoningVerticalProfileAverage / v1[i].length;
                zoningVerticalProfileAverage = 0;
            }
        }

        return Math.sqrt(euclideanDistance);
    }

    /**
     * Calculate the percentage of recognition of an OCR.
     *
     * @param imageVectorMap map which matches an image with a learning chain vector
     * @return the percentage of recognition of the OCR
     */
    private int percentageRecognition(Map<ImagePlus, double[][]> imageVectorMap) {
        int percentageRecognition = 0;
        ImagePlus imageMinVectorDistance = null;

        for (ImagePlus currentImage : imageVectorMap.keySet()) {
            for (ImagePlus imageNext : imageVectorMap.keySet()) {
                if (!currentImage.equals(imageNext)) {
                    if (imageMinVectorDistance == null) {
                        imageMinVectorDistance = imageNext;
                    } else {
                        double euclideanDistance1 = vectorDistance(imageVectorMap.get(currentImage), imageVectorMap.get(imageMinVectorDistance));
                        double euclideanDistance2 = vectorDistance(imageVectorMap.get(imageNext), imageVectorMap.get(currentImage));
                        double minDistance = Math.min(euclideanDistance1, euclideanDistance2);

                        if (minDistance != euclideanDistance1) {
                            imageMinVectorDistance = imageNext;
                        }
                    }
                }
            }

            updateConfusionMatrix(currentImage.getTitle().charAt(0), imageMinVectorDistance.getTitle().charAt(0));

            if (currentImage.getTitle().charAt(0) == imageMinVectorDistance.getTitle().charAt(0)) {
                ++percentageRecognition;
            }
        }

        return percentageRecognition;
    }

    /**
     * Update a confusion matrix of an OCR.
     *
     * @param expected expected number to be recognized
     * @param result number recognized
     */
    private void updateConfusionMatrix(char expected, char result) {
        int row = Character.digit(result, 10);
        int col = Character.digit(expected, 10);

        ++CONFUSION_MATRIX[row][col];
    }

    /**
     * Display a confusion matrix of an OCR.
     */
    public void displayConfusionMatrix() {
        int percentageRecognition = percentageRecognition(matchImageVector());

        System.out.println("\n | 0  1  2  3  4  5  6  7  8  9");
        System.out.println("-------------------------------");

        for (int i = 0; i < CONFUSION_MATRIX.length; ++i) {
            System.out.print(i + "| ");

            for (int j = 0; j < CONFUSION_MATRIX.length; ++j) {
                if (CONFUSION_MATRIX[i][j] == 10) {
                    System.out.print(CONFUSION_MATRIX[i][j]+ " ");
                } else {
                    System.out.print(CONFUSION_MATRIX[i][j]+ "  ");
                }
            }

            System.out.println();
        }

        System.out.println("-------------------------------\n");

        System.out.println("The recognition rate is " + percentageRecognition + "%.");
    }
}