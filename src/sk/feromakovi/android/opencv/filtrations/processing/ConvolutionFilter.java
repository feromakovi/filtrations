package sk.feromakovi.android.opencv.filtrations.processing;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;

public final class ConvolutionFilter implements ImageProcessor {

	@Override
	public Bitmap process(Bitmap image) {
		Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8U,
				new Scalar(4));
		Utils.bitmapToMat(image, mat);
		Mat processedMat = filter2D(mat);
		Bitmap resultBitmap = Bitmap.createBitmap(processedMat.cols(),
				processedMat.rows(), Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(processedMat, resultBitmap);
		return resultBitmap;
	}

	private Mat filter2D(Mat image){
		Mat convMat = new Mat(3, 3, CvType.CV_32F);
		convMat.put(0,0, 0.125);
	    convMat.put(0,1, 0.5);
	    convMat.put(0,2, 0.125);
	    convMat.put(1,0, 0);
	    convMat.put(1,1, 0);
	    convMat.put(1,2, 0);
	    convMat.put(2,0, -0.125);
	    convMat.put(2,1, -0.5);
	    convMat.put(2,2, -0.125);
	    Imgproc.filter2D(image, image, image.depth(), convMat);
	    return image;
	}

	private Mat pixelize(Mat image) {
		int rows = (int) image.height();
		int cols = (int) image.width();

		int left = cols / 8;
		int top = rows / 8;

		int width = cols;
		int height = rows;

		Mat rgbaInnerWindow = image.submat(top, top + height, left, left
				+ width);
		Imgproc.resize(rgbaInnerWindow, image, new Size(), 0.1, 0.1,
				Imgproc.INTER_NEAREST);
		Imgproc.resize(image, rgbaInnerWindow, rgbaInnerWindow.size(), 0., 0.,
				Imgproc.INTER_NEAREST);
		rgbaInnerWindow.release();
		return image;
	}

	@Override
	public CharSequence getName() {
		return "Convolution filter";
	}

}
