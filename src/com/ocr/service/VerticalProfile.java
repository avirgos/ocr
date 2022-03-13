package com.ocr.service;

/**
 * A vertical profile specification.
 */
public class VerticalProfile {
    /**
     * Determine the vertical profile vector of an image.
     *
     * @param matrix matrix of the image
     * @return the vertical profile vector of the image
     */
    protected double[] verticalProfileVectoring(int[][] matrix) {
        double[] v = new double[matrix.length];

        for (int col = 0; col < 15; ++col) {
            for (int row = 0; row < 15; ++row) {
                if (matrix[row][col] == 0) {
                    ++v[row];
                }
            }
        }

        return v;
    }
}