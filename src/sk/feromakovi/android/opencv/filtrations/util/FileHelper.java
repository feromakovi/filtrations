package sk.feromakovi.android.opencv.filtrations.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

public class FileHelper {

	public static String bitmapToString(Bitmap b){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	    b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	    return Base64.encodeToString(baos.toByteArray(),Base64.DEFAULT); //"data:image/png;base64," +
	}
	
	public static Bitmap stringToBitmap(String base){
		byte[] decodedByte = Base64.decode(base, 0);
	    return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}
	
	public static void deleteFolder(String filename) {
		java.io.File f = new java.io.File(filename);
		if (f.isFile()) {
			f.delete();
		}
		if (f.isDirectory() && !f.getName().equals(".") && !f.getName().equals("..")) {
			String[] files = f.list();
			for (String item : files) {
				deleteFolder(filename + "/" + item);
			}
		}
		if (f.isDirectory()) {
			f.delete();
		}
	}

	public static Bitmap getBitmapFromUri(Context ctx, Uri imageUri) {
		Bitmap b;
		try {
			b = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), imageUri);
			if(Math.max(b.getHeight(), b.getWidth()) > 2000){
				return Bitmap.createScaledBitmap(b, b.getWidth() / 2, b.getHeight() / 2, true);
			}else
				return b;
		} catch (Exception e) {
			e.printStackTrace();
		} 		
		return null;
	}
	
	public static Bitmap getBitmapFromFile(String filePath){
		Bitmap b = null;
		try{
			b = BitmapFactory.decodeFile(filePath);
			if(Math.max(b.getHeight(), b.getWidth()) > 2000)
				b = Bitmap.createScaledBitmap(b, b.getWidth() / 2, b.getHeight() / 2, true);
			b = toGrayscale(b);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	/**
     * Convert bitmap to the grayscale
     *
     * @param bmpOriginal Original bitmap
     * @return Grayscale bitmap
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        final int height = bmpOriginal.getHeight();
        final int width = bmpOriginal.getWidth();

        final Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas c = new Canvas(bmpGrayscale);
        final Paint paint = new Paint();
        final ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        final ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
	
	public static boolean copy(File file, String path, boolean rewrite) {
		try {
			File dest = new File(path);
			if (dest.exists() && !rewrite)
				return false;
			if (dest.exists())
				dest.delete();
			dest.createNewFile();
			int read = -1;
			byte[] buffer = new byte[1024];
			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(dest);
			while ((read = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, read);
			}
			fos.flush();
			fis.close();
			fos.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
