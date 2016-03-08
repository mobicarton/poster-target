package mobi.carton.postertarget;


import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.TrackableResult;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


// The renderer class for the PosterTargets.
public class PosterTargetRenderer
        implements
        GLSurfaceView.Renderer {


    private static final String LOGTAG = "ImageTargetRenderer";


    public static final String ARG_TRACKABLE_ID = "arg_id";
    public static final String ARG_TRACKABLE_NAME = "arg_name";


    private SampleApplicationSession vuforiaAppSession;
    private MainActivity mActivity;

    private Renderer mRenderer;

    public boolean mIsActive;

    private Handler mHandler = null;
    private int mCurrentlyTracked = 0;


    public PosterTargetRenderer(MainActivity activity, SampleApplicationSession session) {
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

        if (mHandler != null) {
            if (state.getNumTrackableResults() == 0) {
                if (mCurrentlyTracked != 0) {
                    mCurrentlyTracked = 0;
                    handleSendTrackable(mCurrentlyTracked, null);
                }
            }

            // did we find any trackables this frame?
            for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {
                TrackableResult result = state.getTrackableResult(tIdx);
                Trackable trackable = result.getTrackable();

                if (mCurrentlyTracked != trackable.getId()) {
                    mCurrentlyTracked = trackable.getId();
                    handleSendTrackable(mCurrentlyTracked, trackable.getName());
                }
            }
        }

        mRenderer.end();
    }


    public void setHandlerTracking(Handler handler){
        this.mHandler = handler;
    }


    private void handleSendTrackable(int id, String name) {
        Message message = mHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_TRACKABLE_ID, id);
        bundle.putString(ARG_TRACKABLE_NAME, name);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }
}
