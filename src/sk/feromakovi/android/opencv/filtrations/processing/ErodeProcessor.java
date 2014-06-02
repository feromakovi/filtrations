package sk.feromakovi.android.opencv.filtrations.processing;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;

public final class ErodeProcessor extends ImageProcessor{
	
	@Override
	public Bitmap process(Bitmap image) {
		Mat mat = erode(bitmapToMat(image));
		return matToBitmap(mat);
	}

	private Mat erode(Mat mat) {
		Mat kernelMat = onParseKernel(mKernel);
		Imgproc.erode(mat, mat, kernelMat);
		return mat;
	}

	@Override
	public CharSequence getName() {
		return "Erode";
	}
}
