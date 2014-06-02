package sk.feromakovi.android.opencv.filtrations.processing;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;

public final class ConvolutionFilter extends ImageProcessor {

	@Override
	public Bitmap process(Bitmap image) {
		Mat processedMat = filter2D(bitmapToMat(image));
		return matToBitmap(processedMat);
	}

	private Mat filter2D(Mat image){
		Mat kernelMat = onParseKernel(mKernel);
	    Imgproc.filter2D(image, image, image.depth(), kernelMat);
	    return image;
	}

//	private Mat pixelize(Mat image) {
//		int rows = (int) image.height();
//		int cols = (int) image.width();
//
//		int left = cols / 8;
//		int top = rows / 8;
//
//		int width = cols;
//		int height = rows;
//
//		Mat rgbaInnerWindow = image.submat(top, top + height, left, left
//				+ width);
//		Imgproc.resize(rgbaInnerWindow, image, new Size(), 0.1, 0.1,
//				Imgproc.INTER_NEAREST);
//		Imgproc.resize(image, rgbaInnerWindow, rgbaInnerWindow.size(), 0., 0.,
//				Imgproc.INTER_NEAREST);
//		rgbaInnerWindow.release();
//		return image;
//	}

	@Override
	public CharSequence getName() {
		return "Convolution filter";
	}

}
