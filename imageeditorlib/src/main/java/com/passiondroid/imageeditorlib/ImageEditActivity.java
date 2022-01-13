package com.passiondroid.imageeditorlib;

import static com.passiondroid.imageeditorlib.ImageEditor.EXTRA_IMAGE_PATH;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.passiondroid.imageeditorlib.utils.FragmentUtil;

import java.io.File;

public class ImageEditActivity extends BaseImageEditActivity
    implements PhotoEditorFragment.OnFragmentInteractionListener {
  private String oldImagePath;
  //private View touchView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image_edit);

    oldImagePath = getIntent().getStringExtra(EXTRA_IMAGE_PATH);
    if (oldImagePath != null) {
      FragmentUtil.addFragment(this, R.id.fragment_container,
          PhotoEditorFragment.newInstance(oldImagePath));
    }
  }

  @Override
  public void onDoneClicked(String imagePath) {

    Intent intent = new Intent();
    intent.putExtra(ImageEditor.EXTRA_EDITED_PATH, imagePath);
    if (oldImagePath != null){
      File oldImage = new File(oldImagePath);
      if (oldImage.exists()){
        boolean b = oldImage.delete();
      }
    }
    setResult(Activity.RESULT_OK, intent);
    finish();
  }

  @Override
  public void onBackPressed() {
    DeletePhoto();
    super.onBackPressed();
  }

  @Override
  protected void onDestroy() {
    DeletePhoto();
    super.onDestroy();
  }

  private void DeletePhoto() {
    File oldImage = new File(oldImagePath);
    if (oldImage.exists()){
      boolean b = oldImage.delete();
    }
  }
}
