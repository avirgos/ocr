package com.ocr.hmi;

import com.ocr.service.Utils;

/**
 * An OCR.
 */
public class OCR {
    /**
     * OCR program.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Utils().displayConfusionMatrix();
    }
}