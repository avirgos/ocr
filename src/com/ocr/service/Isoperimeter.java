package com.ocr.service;

/**
 * An isoperimeter specification.
 */
public class Isoperimeter {
    /**
     * Get the number of neighbours of a cell.
     *
     * @param matrix matrix of the image
     * @param i row of the cell
     * @param j column of the cell
     * @return the number of neighbours of a cell
     */
    private int numbersOfNeighbours(int[][] matrix, int i, int j) {
        int count = 0;

        // up
        if (i > 0 && matrix[i - 1][j] == 0) {
            ++count;
        }

        // left
        if (j > 0 && matrix[i][j - 1] == 0) {
            ++count;
        }

        // down
        if (i < matrix.length - 1 && matrix[i + 1][j] == 0) {
            ++count;
        }

        // right
        if (j < matrix.length - 1 && matrix[i][j + 1] == 0) {
            ++count;
        }

        return count;
    }

    /**
     * Calculate the perimeter of an image.
     *
     * @param matrix matrix of the image
     * @return the perimeter of the image
     */
    private int perimeterImage(int[][] matrix) {
        int perimeter = 0;

        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix.length; ++j) {
                if (matrix[i][j] == 0) {
                    perimeter += (4 - numbersOfNeighbours(matrix, i, j));
                }
            }
        }

        return perimeter;
    }

    /**
     * Calculate the surface of an image.
     *
     * @param matrix matrix of the image
     * @return the surface of the image
     */
    private int surfaceImage(int[][] matrix) {
        int surface = 0;

        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix.length; ++j) {
                if (matrix[i][j] == 0) {
                    ++surface;
                }
            }
        }

        return surface;
    }

    /**
     * Calculate the isoperimeter report of an image.
     *
     * @param matrix matrix of the image
     * @return the isoperimeter report of the image
     */
    protected double isoperimeterReport(int[][] matrix) {
        return perimeterImage(matrix) / (4 * Math.PI * surfaceImage(matrix));
    }
}