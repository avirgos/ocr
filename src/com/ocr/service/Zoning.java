package com.ocr.service;

/**
 * A zoning specification.
 */
public class Zoning {
    /**
     * Determine the zoning vector of an image.
     *
     * @param matrix matrix of the image
     * @return the zoning vector of the image
     */
    protected double[] zoningVectoring(int[][] matrix) {
        double[] v = new double[16];
        int n = 0;

        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < 4; ++col) {
                v[n] = getZone(matrix, row, col, Utils.RESIZE/4);
                ++n;
            }
        }

        return v;
    }

    /**
     * Get a zone which corresponds to a line and a column.
     *
     * @param matrix matrix of the image
     * @param line line of the matrix
     * @param col column of the matrix
     * @param zoneSize size of each zone
     * @return the zone which corresponds to the line and the column
     */
    private int getZone(int[][] matrix, int line, int col, int zoneSize) {
        int nbBlackP = 0;

        for (int pixelL = line*zoneSize; pixelL < (line*zoneSize)+zoneSize; ++pixelL) {
            for (int pixelC = col*zoneSize; pixelC < (col*zoneSize)+zoneSize; ++pixelC) {
                if (matrix[pixelL][pixelC] == 0) {
                    ++nbBlackP;
                }
            }
        }

        return nbBlackP;
    }
}