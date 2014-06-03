package sk.feromakovi.android.opencv.filtrations;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import sk.feromakovi.android.opencv.filtrations.processing.ErodeProcessor;
import sk.feromakovi.android.opencv.filtrations.processing.Filter2dProcessor;
import sk.feromakovi.android.opencv.filtrations.processing.DilateProcessor;
import sk.feromakovi.android.opencv.filtrations.processing.ImageProcessor;
import sk.feromakovi.android.opencv.filtrations.util.ImagePicker;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	@SuppressWarnings("serial")
	private final List<ImageProcessor> mImageProcessors = new ArrayList<ImageProcessor>() {
		{
			add(new DilateProcessor());
			add(new ErodeProcessor());
			add(new Filter2dProcessor());
		}
	};

	private final ImagePicker mImagePicker = new ImagePicker(this);

	private ImageView mImage;
	private Button mResetButton;

	private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: 
				Toast.makeText(MainActivity.this, "OpenCV loaded", Toast.LENGTH_SHORT).show();
				break;
			default: 
				super.onManagerConnected(status);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.mImage = (ImageView) findViewById(R.id.ImageView_image);
		this.mResetButton = (Button) findViewById(R.id.Button_resetButton);

		if (savedInstanceState != null) {
			if (mImagePicker.dispatchRestoreState(savedInstanceState) != null) {
				mImage.setImageBitmap(mImagePicker.getReceivedBitmap());
			}
		}

		if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this,
				mOpenCVCallBack))
			Toast.makeText(this, "Could not load OpenCV", Toast.LENGTH_SHORT)
					.show();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mImagePicker.dispatchSaveState(outState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mImagePicker.dispatchActivityResult(requestCode, resultCode, data, mImage);
		if(resultCode == Activity.RESULT_OK)
			mResetButton.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_action_import_picture:
			mImagePicker.pickImage();
			return true;
		case R.id.menu_action_fire:
			if (mImagePicker.getReceivedBitmap() != null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				CharSequence[] options = new CharSequence[mImageProcessors
						.size()];
				for (int i = 0; i < mImageProcessors.size(); i++)
					options[i] = mImageProcessors.get(i).getName();
				builder.setItems(options, this).create().show();
			} else
				Toast.makeText(this, "No image!!!", Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onResetButtonClick(View resetButton) {
		if (mImagePicker.getReceivedBitmap() != null)
			mImage.setImageBitmap(mImagePicker.getReceivedBitmap());
		else
			mImage.setImageResource(R.drawable.ic_no_image);
		this.mResetButton.setVisibility(View.GONE);
	}

	@Override
	public void onClick(DialogInterface arg0, int which) {
		ImageProcessor selectedProcessor = mImageProcessors.get(which);
		Bitmap processedBitmap = selectedProcessor.processBitmap(mImagePicker
				.getReceivedBitmap());
		if (processedBitmap != null) {
			mImage.setImageBitmap(processedBitmap);
			mResetButton.setVisibility(View.VISIBLE);
		} else
			Toast.makeText(this, "Operation failed!", Toast.LENGTH_SHORT)
					.show();
	}
}
