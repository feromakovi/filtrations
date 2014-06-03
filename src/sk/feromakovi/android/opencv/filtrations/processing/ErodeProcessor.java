package sk.feromakovi.android.opencv.filtrations.processing;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public final class ErodeProcessor extends ImageProcessor{
	
	@Override
	protected Mat process(Mat mat, Mat kernel) {
		return erode(mat, kernel);
	}
	
	private Mat erode(Mat mat, Mat kernelMat) {
		Imgproc.erode(mat, mat, kernelMat);
		return mat;
	}

	@Override
	public CharSequence getName() {
		return "Erode";
	}
}
