package com.srapp.thermalprint.async;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.EscPosPrinterCommands;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.textparser.PrinterTextParser;
import com.dantsu.escposprinter.textparser.PrinterTextParserColumn;

public class PrinterTextParserImg implements IPrinterTextParserElement {
    
    /**
     * Convert Drawable instance to a hexadecimal string of the image data.
     *
     * @param printerSize A EscPosPrinterSize instance that will print the image.
     * @param drawable Drawable instance to be converted.
     * @return A hexadecimal string of the image data. Empty string if Drawable cannot be cast to BitmapDrawable.
     */
    public static String bitmapToHexadecimalString(EscPosPrinterSize printerSize, Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return PrinterTextParserImg.bitmapToHexadecimalString(printerSize, (BitmapDrawable) drawable);
        }
        return "";
    }
    
    /**
     * Convert BitmapDrawable instance to a hexadecimal string of the image data.
     *
     * @param printerSize A EscPosPrinterSize instance that will print the image.
     * @param bitmapDrawable BitmapDrawable instance to be converted.
     * @return A hexadecimal string of the image data.
     */
    public static String bitmapToHexadecimalString(EscPosPrinterSize printerSize, BitmapDrawable bitmapDrawable) {
        return PrinterTextParserImg.bitmapToHexadecimalString(printerSize, bitmapDrawable.getBitmap());
    }
    
    /**
     * Convert Bitmap instance to a hexadecimal string of the image data.
     *
     * @param printerSize A EscPosPrinterSize instance that will print the image.
     * @param bitmap Bitmap instance to be converted.
     * @return A hexadecimal string of the image data.
     */
    public static String bitmapToHexadecimalString(EscPosPrinterSize printerSize, Bitmap bitmap) {
        return PrinterTextParserImg.bytesToHexadecimalString(printerSize.bitmapToBytes(bitmap));
    }
    
    /**
     * Convert byte array to a hexadecimal string of the image data.
     *
     * @param bytes Bytes contain the image in ESC/POS command.
     * @return A hexadecimal string of the image data.
     */
    public static String bytesToHexadecimalString(byte[] bytes) {
        StringBuilder imageHexString = new StringBuilder();
        for (byte aByte : bytes) {
            String hexString = Integer.toHexString(aByte & 0xFF);
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }
            imageHexString.append(hexString);
        }
        return imageHexString.toString();
    }
    
    /**
     * Convert hexadecimal string of the image data to bytes ESC/POS command.
     *
     * @param hexString Hexadecimal string of the image data.
     * @return Bytes contain the image in ESC/POS command.
     */
    public static byte[] hexadecimalStringToBytes(String hexString) throws NumberFormatException {
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int pos = i * 2;
            bytes[i] = (byte) Integer.parseInt(hexString.substring(pos, pos + 2), 16);
        }

        return bytes;
    }
    
    
    private int length;
    private byte[] image;
    
    /**
     * Create new instance of PrinterTextParserImg.
     *
     * @param printerTextParserColumn Parent PrinterTextParserColumn instance.
     * @param textAlign Set the image alignment. Use PrinterTextParser.TAGS_ALIGN_... constants.
     * @param hexadecimalString Hexadecimal string of the image data.
     */
    public PrinterTextParserImg(PrinterTextParserColumn printerTextParserColumn, String textAlign, String hexadecimalString) {
        this(printerTextParserColumn, textAlign, PrinterTextParserImg.hexadecimalStringToBytes(hexadecimalString));
    }

    /**
     * Create new instance of PrinterTextParserImg.
     *
     * @param printerTextParserColumn Parent PrinterTextParserColumn instance.
     * @param textAlign Set the image alignment. Use PrinterTextParser.TAGS_ALIGN_... constants.
     * @param image Bytes contain the image in ESC/POS command.
     */
    public PrinterTextParserImg(PrinterTextParserColumn printerTextParserColumn, String textAlign, byte[] image) {
        EscPosPrinter printer = printerTextParserColumn.getLine().getTextParser().getPrinter();

        int
                byteWidth = ((int) image[4] & 0xFF) + ((int) image[5] & 0xFF) * 256,
                width = byteWidth * 8,
                height = ((int) image[6] & 0xFF) + ((int) image[7] & 0xFF) * 256,
                nbrByteDiff = (int) Math.floor(((float) (printer.getPrinterWidthPx() - width)) / 8f),
                nbrWhiteByteToInsert = 0;

        switch (textAlign) {
            case PrinterTextParser.TAGS_ALIGN_CENTER:
                nbrWhiteByteToInsert = Math.round(((float) nbrByteDiff) / 2f);
                break;
            case PrinterTextParser.TAGS_ALIGN_RIGHT:
                nbrWhiteByteToInsert = nbrByteDiff;
                break;
        }

        if (nbrWhiteByteToInsert > 0) {
            int newByteWidth = byteWidth + nbrWhiteByteToInsert;
            byte[] newImage = initImageCommand(newByteWidth, height);
            for (int i = 0; i < height; i++) {
                System.arraycopy(image, (byteWidth * i + 8), newImage, (newByteWidth * i + nbrWhiteByteToInsert + 8), byteWidth);
            }
            image = newImage;
        }

        this.length = (int) Math.ceil(((float) byteWidth * 8) / ((float) printer.getPrinterCharSizeWidthPx()));
        this.image = image;
    }
    public static byte[] initImageCommand(int width, int height) {
        // Assuming we need a byte array for image data
        // Width in bytes, so we might need to round it up to the nearest multiple of 8
        int byteWidth = (width + 7) / 8; // round up to the nearest byte
        byte[] imageData = new byte[byteWidth * height + 8]; // +8 for command header or any additional data
        // Initialize with zeros or any specific pattern if needed
        for (int i = 0; i < imageData.length; i++) {
            imageData[i] = 0; // or some default value
        }
        return imageData;
    }

    /**
     * Get the image width in char length.
     *
     * @return int
     */
    @Override
    public int length() throws EscPosEncodingException {
        return this.length;
    }

    /**
     * Print image
     *
     * @param printerSocket Instance of EscPosPrinterCommands
     * @return this Fluent method
     */
    @Override
    public PrinterTextParserImg print(EscPosPrinterCommands printerSocket) {
        try {
            printerSocket.printImage(this.image);
        } catch (EscPosConnectionException e) {
            // Handle the exception (e.g., log the error, notify the user, etc.)
            e.printStackTrace(); // or use your logging mechanism
        }

        return this;
    }
}
