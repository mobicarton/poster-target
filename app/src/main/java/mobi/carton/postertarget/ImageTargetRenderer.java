package mobi.carton.postertarget;


import android.opengl.GLSurfaceView;
import android.util.Log;

import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.TrackableResult;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


// The renderer class for the ImageTargets sample.
public class ImageTargetRenderer
        implements
        GLSurfaceView.Renderer {


    private static final String LOGTAG = "ImageTargetRenderer";


    private SampleApplicationSession vuforiaAppSession;
    private ImageTargets mActivity;

    private Renderer mRenderer;


    public boolean mIsActive;


    public ImageTargetRenderer(ImageTargets activity, SampleApplicationSession session) {
        mActivity = activity;
        vuforiaAppSession = session;
    }


    // Called to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mIsActive)
            return;

        // Call our function to render content
        renderFrame();
    }


    // Called when the surface is created or recreated.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");

        // Function for initializing the renderer.
        mRenderer = Renderer.getInstance();
        // Hide the Loading Dialog
        mActivity.loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);

        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        vuforiaAppSession.onSurfaceCreated();
    }


    // Called when the surface changed size.
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");

        // Call Vuforia function to handle render surface size changes:
        vuforiaAppSession.onSurfaceChanged(width, height);
    }


    // The render function.
    private void renderFrame() {
        State state = mRenderer.begin();
        mRenderer.drawVideoBackground();

        // did we find any trackables this frame?
        for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {
            TrackableResult result = state.getTrackableResult(tIdx);
            Trackable trackable = result.getTrackable();

            String userData = (String) trackable.getUserData();
            Log.d(LOGTAG, "UserData:Retreived User Data	\"" + userData + "\"");
        }

        mRenderer.end();
    }
}
