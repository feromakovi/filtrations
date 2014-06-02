package sk.feromakovi.android.opencv.filtrations.processing;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import android.graphics.Bitmap;
import android.renderscript.Script.KernelID;

public abstract class ImageProcessor {
	
	private static final String STRING_KERNEL_SOBEL = "-1#0#1@-2#0#2@-1#0#1";
	
	protected String mKernel = STRING_KERNEL_SOBEL;
	
	public abstract Bitmap process(final Bitmap image);
	public abstract CharSequence getName(); 
	
	protected Mat bitmapToMat(Bitmap bitmap){
		Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8U, new Scalar(4));
		Utils.bitmapToMat(bitmap, mat);
		return mat;
	}
	
	protected Bitmap matToBitmap(Mat mat){
		Bitmap resultBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(mat, resultBitmap);
		return resultBitmap;
	}
	
	public void setKernel(String stringKernel){
		if(stringKernel != null && stringKernel.length() > 0)
			this.mKernel = stringKernel;
		else
			throw new IllegalArgumentException("Given kernel parameter doesn't match specified string format");
	}
	
	protected Mat onParseKernel(String sKernel){
		try{
			String[] lines = mKernel.split("@");
			final int cLines = lines.length;
			final Mat kernelMat = new Mat(cLines, cLines, CvType.CV_8U);
			for(int l = 0; l < lines.length; l++){
				String line = lines[l];
				String[] cls = line.split("#");
				for(int c = 0; c < cls.length; c++){
					String number = cls[c];
					double value = Double.parseDouble(number);
					kernelMat.put(l, c, value);
				}
			}
			return kernelMat;
		}catch(Exception e){
			e.printStackTrace();
		}
		return onParseKernel(STRING_KERNEL_SOBEL);		
	}
}
