package sk.feromakovi.android.opencv.filtrations.processing;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public final class Filter2dProcessor extends ImageProcessor {
	
	@Override
	protected Mat process(Mat mat, Mat kernelMat) {
		return filter2D(mat, kernelMat);
	}

	private Mat filter2D(Mat image, Mat kernelMat){
	    Imgproc.filter2D(image, image, image.depth(), kernelMat);
	    return image;
	}

	@Override
	public CharSequence getName() {
		return "Filter2D";
	}
}
