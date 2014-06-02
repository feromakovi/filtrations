package sk.feromakovi.android.opencv.filtrations.processing;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;

public final class DilateProcessor extends ImageProcessor {

	@Override
	public Bitmap process(Bitmap image) {
		Mat mat = dilate(bitmapToMat(image));
		return matToBitmap(mat);
	}

	private Mat dilate(Mat mat) {
		Mat kernelMat = onParseKernel(mKernel);
		Imgproc.dilate(mat, mat, kernelMat);
		return mat;
	}

	@Override
	public CharSequence getName() {
		return "Dilate";
	}
}
