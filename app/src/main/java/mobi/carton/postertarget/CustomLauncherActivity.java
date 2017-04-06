package mobi.carton.postertarget;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import mobi.carton.library.CartonPrefs;
import mobi.carton.library.LauncherActivity;

import static mobi.carton.library.CartonActivity.EXTRA_NO_LAUNCHER;

/**
 * Created by damienbrun on 2017-04-05.
 */

public class CustomLauncherActivity extends LauncherActivity {


    private static final int PERMISSION_CAMERA = 1;

    private boolean mLaunchable;


    /*
    LIFECYCLE
    */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLaunchable = false;

        checkAskPermission();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, do your work....
                    Log.d("CustomLauncher", "CAMERA permission was granted");
                    mLaunchable = true;
                } else {
                    // permission denied
                    // Disable the functionality that depends on this permission.
                    Toast.makeText(this, "CAMERA needed for Augmented Reality experience", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' statements for other permssions
        }
    }


    private void checkAskPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Log.d("CustomLauncher", "version >= VERSION M");

            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Log.d("CustomLauncher", "CAMERA permission not granted");

                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);

                /*
                // User may have declined earlier, ask Android if we should show him a reason
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    // show an explanation to the user
                    // Good practise: don't block thread after the user sees the explanation, try again to request the permission.

                } else {
                    Log.d("CustomLauncher", "request CAMERA permission");
                    // request the permission.
                    // CALLBACK_NUMBER is a integer constants
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
                    // The callback method gets the result of the request.
                }
                */
            } else {
                mLaunchable = true;
            }
        } else {
            mLaunchable = true;
        }
    }


    private void relaunchMain() {
        if (mLaunchable) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(EXTRA_NO_LAUNCHER, true);
            startActivity(intent);
            finish();
        } else {
            checkAskPermission();
        }
    }


    @Override
    public void clickWithout(View v) {
        CartonPrefs.setWithoutCarton(getApplicationContext(), true);
        relaunchMain();
    }


    @Override
    public void onDirectionChanged(int azimuth, int pitch, int roll) {
        if ((pitch < 10 && pitch > -10) && (roll < 10 && roll > - 10)) {
            CartonPrefs.setWithoutCarton(getApplicationContext(), false);
            relaunchMain();
        }
    }
}
