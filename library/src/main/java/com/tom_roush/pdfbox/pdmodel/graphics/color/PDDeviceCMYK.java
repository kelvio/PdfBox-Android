package com.tom_roush.pdfbox.pdmodel.graphics.color;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColor;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceColorSpace;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class PDDeviceCMYK extends PDDeviceColorSpace {

    /**  The single instance of this class. */
    public static PDDeviceCMYK INSTANCE;
    static
    {
        INSTANCE = new PDDeviceCMYK();
    }

    private final PDColor initialColor = new PDColor(new float[] { 0, 0, 0, 1 }, this);
    private volatile boolean initDone = false;
    private boolean usePureJavaCMYKConversion = false;

    protected PDDeviceCMYK()
    {
    }

   

    @Override
    public String getName()
    {
        return COSName.DEVICECMYK.getName();
    }

    @Override
    public int getNumberOfComponents()
    {
        return 4;
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent)
    {
        return new float[] { 0, 1, 0, 1, 0, 1, 0, 1 };
    }

    @Override
    public PDColor getInitialColor()
    {
        return initialColor;
    }

    @Override
    public float[] toRGB(float[] value) throws IOException
    {
        return cmykToRgb(value[0], value[1], value[2], value[3]);
    }

    @Override
    public Bitmap toRGBImage(Bitmap raster) throws IOException {
        if (raster.getConfig() != Bitmap.Config.ALPHA_8)
        {
            Log.e("PdfBox-Android", "Raster in PDDevicGrey was not ALPHA_8");
        }

        int width = raster.getWidth();
        int height = raster.getHeight();

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        ByteBuffer buffer = ByteBuffer.allocate(raster.getRowBytes() * height);
        raster.copyPixelsToBuffer(buffer);
        byte[] gray = buffer.array();

        int[] rgb = new int[width * height];

        image.getPixels(rgb, 0, width, 0, 0, width, height);
        for (int pixelIdx = 0; pixelIdx < width * height; pixelIdx++)
        {
            int value = gray[pixelIdx];
            rgb[pixelIdx] = Color.argb(255, value, value, value);
        }
        image.setPixels(rgb, 0, width, 0, 0, width, height);
        return image;
    }

    private static float[] cmykToRgb(float c, float m, float y, float k) {
        float r = 255 * (1 - c/100) * (1 - k/100);
        float g = 255 * (1 - m/100) * (1 - k/100);
        float b = 255 * (1 - y/100) * (1 - k/100);
        float[] rgb = new float[]{ r, g, b };

        return rgb;
    }

}
