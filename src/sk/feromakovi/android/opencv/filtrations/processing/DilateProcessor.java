package sk.feromakovi.android.opencv.filtrations.processing;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public final class DilateProcessor extends ImageProcessor {

	@Override
	protected Mat process(Mat mat, Mat kernel) {
		return dilate(mat, kernel);
	}

	private Mat dilate(Mat mat, Mat kernelMat) {
		Imgproc.dilate(mat, mat, kernelMat);
		return mat;
	}

	@Override
	public CharSequence getName() {
		return "Dilate";
	}
}
