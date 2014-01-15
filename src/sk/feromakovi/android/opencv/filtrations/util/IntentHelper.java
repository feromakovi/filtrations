package sk.feromakovi.android.opencv.filtrations.util;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;

public class IntentHelper {

	public static Intent imageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		return intent;
	}

	public static Intent imageFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File file = ImagePicker.getPickedFile();
		if (file.exists())
			file.delete();
		Uri mImageCaptureUri = Uri.fromFile(file);
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
				mImageCaptureUri);
		intent.putExtra("return-data", true);
		return intent;
	}

	public static Intent web(String url) {
		Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		return webIntent;
	}

	public static Intent sendMail(String address, String subject, String text) {
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("message/rfc822");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, text);
		emailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		return emailIntent;
	}

	public static Intent pickEmail() {
		Intent contact = new Intent(Intent.ACTION_PICK,
				ContactsContract.CommonDataKinds.Email.CONTENT_URI);
		return contact;
	}

	public static Intent pickContact() {
		Intent contact = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);
		return contact;
	}

	public static boolean isIntentAvailable(Context context, Intent intent) {
		List<ResolveInfo> list = context.getPackageManager()
				.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		return !list.isEmpty();
	}
	
	public static Intent share(final String text){
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, text);
		return shareIntent;
	}
	
	public static Intent share(final String text, final String subject){
		Intent shareIntent = share(text);
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		return shareIntent;
	}
}
