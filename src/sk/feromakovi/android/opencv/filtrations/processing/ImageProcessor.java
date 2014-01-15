package sk.feromakovi.android.opencv.filtrations.processing;

import android.graphics.Bitmap;

public interface ImageProcessor {
	
	public Bitmap process(final Bitmap image);
	public CharSequence getName(); 

}
