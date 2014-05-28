package sk.feromakovi.android.opencv.filtrations.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import sk.feromakovi.android.opencv.filtrations.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;

/**
 * 
 * 
 * @author Martin Albedinsky
 */
public class ImagePicker {

	private static final int REQUEST_CODE_FROM_GALLERY = 2872;
    private static final int REQUEST_CODE_FROM_CAMERA = 2973;

    private static final String BUNDLE_SAVED_BITMAP = "ImagePicker.SavedBitmap";

    /**
     * Image view to which will be picked image assigned.
     */
    private ImageView mImageView;

    /**
     * Bitmap as received result.
     */
    private String mResultBitmapFile = null;

    private PickerContext mPickerContext;

    private SetUpImageHandler mSetUpImageHandler = null;

    /**
     * Creates new image picker tied to the given activity context.
     *
     * @param activity
     */
    public ImagePicker(Activity activity) {
        this.mPickerContext = new PickerActivityContext(activity);
    }

    /**
     * Creates new image picker tied to the given fragment context.
     *
     * @param fragment
     */
    public ImagePicker(Fragment fragment) {
        this.mPickerContext = new PickerFragmentContext(fragment);
    }

    /**
     * Registers callback to be invoked when this image picker successfully
     * process the received result from activity and bitmap obtained from that
     * result should be assigned to the current image view.
     *
     * @param handler
     */
    public void setSetUpImageHandler(SetUpImageHandler handler) {
        this.mSetUpImageHandler = handler;
    }

    /**
     * Performs image pick activities (show the chooses dialog, ...).
     */
    public void pickImage() {
        // Show pick dialog.
        this.createPickDialog(mPickerContext).show();
    }

    /**
     * Dispatches message about that, that result was received.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @param imageView   Image view to which should be picked image assigned.
     * @return <code>True</code> if result was processed, <code>false</code>
     * otherwise.
     */
    public final boolean dispatchActivityResult(int requestCode, int resultCode, Intent data, ImageView imageView) {
        this.mImageView = imageView;
        return onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Saves this picker instance state.
     *
     * @param outState
     */
    public final void dispatchSaveState(Bundle outState) {
        if (outState == null) {
            outState = new Bundle();
        }
        onSaveState(outState);
    }

    /**
     * Restores this picker instance state.
     *
     * @param savedState
     * @return Saved received bitmap, or <code>null</code> if no bitmap was
     * saved.
     */
    public final String dispatchRestoreState(Bundle savedState) {
        if (savedState != null) {
            mResultBitmapFile = onRestoreState(savedState);
        }
        return mResultBitmapFile;
    }

    /**
     * Returns current received bitmap.
     *
     * @return Bitmap or <code>null</code> if there is no received bitmap yet.
     */
    public Bitmap getReceivedBitmap() {
        return (mResultBitmapFile == null) ? null : FileHelper.getBitmapFromFile(mResultBitmapFile);
    }

    public String getBitmapFilePath(){
        return mResultBitmapFile;
    }

    /**
     * @return
     */
    public ImageView getImageView() {
        return mImageView;
    }

    /**
     * Invoked when result is being received to the activity/fragment context.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return
     */
    protected boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean processed = false;
        if (resultCode != -1) return processed;

        switch (requestCode) {
            case REQUEST_CODE_FROM_CAMERA:
                mResultBitmapFile = Uri.fromFile(getPickedFile()).getPath();
                this.performSetUpImage(mResultBitmapFile);
                processed = true;
                break;
            case REQUEST_CODE_FROM_GALLERY:
                if (data != null) {
                    mResultBitmapFile = getRealPathFromURI(data);
                    if (mResultBitmapFile == null)
                        mResultBitmapFile = data.getData().getPath();
                    this.performSetUpImage(mResultBitmapFile);
                    processed = true;
                }
                break;
        }

        return processed;
    }

    /**
     * Invoked to set up the given <var>bitmap</var> to the given image.
     *
     * @param imageView Always valid image view.
     * @param bitmap    Always valid bitmap.
     */
    protected void onSetUpImage(ImageView imageView, Bitmap bitmap) {
        if (mSetUpImageHandler != null) {
            mSetUpImageHandler.onSetUpImage(imageView, bitmap);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * Invoked to save picker state.
     *
     * @param outState
     */
    protected void onSaveState(Bundle outState) {
        if (mResultBitmapFile != null) {
            outState.putString(BUNDLE_SAVED_BITMAP, mResultBitmapFile);
        }
    }

    /**
     * Invoked to restore picker state.
     *
     * @param savedState
     * @return
     */
    protected String onRestoreState(Bundle savedState) {
        return savedState.getString(BUNDLE_SAVED_BITMAP);
    }

    private void performSetUpImage(String resultBitmapFile) {
        if (resultBitmapFile != null && mImageView != null) {
            this.mResultBitmapFile = resultBitmapFile;

            try {
                onSetUpImage(mImageView, getReceivedBitmap());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("TAG", "Can't set up received bitmap. Bitmap(" + resultBitmapFile + ") or image view(" + mImageView + ") is invalid.");
        }
    }

    private AlertDialog createPickDialog(final PickerContext pickerContext) {
        final Context context = pickerContext.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Create options for dialog.
        CharSequence[] options = {context.getString(R.string.App_Text_Camera),
                context.getString(R.string.App_Text_Gallery)};

        // Set up dialog.
        builder.setCancelable(true).setTitle(context.getString(R.string.Dialog_ChoosePicture_title));
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                int requestCode = REQUEST_CODE_FROM_CAMERA;
                switch (which) {
                    case 0:
                        intent = IntentHelper.imageFromCamera();
                        requestCode = REQUEST_CODE_FROM_CAMERA;
                        break;
                    case 1:
                        intent = IntentHelper.imageFromGallery();
                        requestCode = REQUEST_CODE_FROM_GALLERY;
                        break;
                }
                pickerContext.startActivityForResult(Intent.createChooser(intent, context.getString(R.string.Dialog_importWith_title)), requestCode);
            }
        });

        return builder.create();
    }

	/*private Parcelable obtainBitmap(Bundle bundle) {
        Parcelable bitmap = null;

		if (bundle != null) {
			if (bundle.containsKey(BUNDLE_RECEIVED_BITMAP)) {
				bitmap = bundle.getParcelable(BUNDLE_RECEIVED_BITMAP);
			} else if (bundle.containsKey(BUNDLE_SAVED_BITMAP)) {
				bitmap = bundle.getParcelable(BUNDLE_SAVED_BITMAP);
			}
		}
		return bitmap;
	}*/

    /**
     * @author Martin Albedinsky
     */
    public static interface SetUpImageHandler {

        /**
         * Invoked after successfully received and processed activity result.
         *
         * @param imageView Image passed to the image picker in
         *                  {@link ImagePicker#dispatchActivityResult(int, int, Intent, ImageView)}
         *                  .
         * @param bitmap    Always valid bitmap obtained from activity result.
         */
        public void onSetUpImage(ImageView imageView, Bitmap bitmap);
    }

    /**
     * Base interface for picker context wrapper.
     */
    private interface PickerContext {

        public void startActivityForResult(Intent intent, int requestCode);

        public Context getContext();
    }

    /**
     * Activity context wrapper.
     */
    private class PickerActivityContext implements PickerContext {

        private Activity mActivity;

        private PickerActivityContext(Activity activity) {
            this.mActivity = activity;
        }

        @Override
        public void startActivityForResult(Intent intent, int requestCode) {
            mActivity.startActivityForResult(intent, requestCode);
        }

        @Override
        public Context getContext() {
            return mActivity;
        }
    }

    /**
     * Fragment context wrapper.
     */
    private class PickerFragmentContext implements PickerContext {

        private Fragment mFragment;

        private PickerFragmentContext(Fragment fragment) {
            this.mFragment = fragment;
        }

        @Override
        public void startActivityForResult(Intent intent, int requestCode) {
            mFragment.startActivityForResult(intent, requestCode);
        }

        @Override
        public Context getContext() {
            return mFragment.getActivity();
        }
    }

    public String getRealPathFromURI(Intent data) {
        Uri selectedImage = data.getData();

        try {
            InputStream stream = mPickerContext.getContext().getContentResolver().openInputStream(data.getData());
            Bitmap pictureBitmap = BitmapFactory.decodeStream(stream);
            File out = getPickedFile();
            if (out.exists())
                out.delete();
            FileOutputStream fos = new FileOutputStream(out);
            pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            stream.close();
            fos.flush();
            fos.close();
            return out.getPath();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static File getPickedFile() {
        return new File(Environment.getExternalStorageDirectory(), "tmp_photo.jpg");
    }
}
