package net.bluemix.seek;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ErrorToast {

    private Activity mActivity;
    private Toast mToast;

    public ErrorToast(Activity activity) {
        mActivity = activity;
        View view = mActivity.getLayoutInflater().inflate(
                R.layout.toast_layout,
                (ViewGroup) activity.getWindow().getDecorView(),
                false
        );

        mToast = new Toast(mActivity.getApplicationContext());
        mToast.setView(view);
    }

    public static ErrorToast makeToast(Activity activity, String message, int duration) {
        ErrorToast result = new ErrorToast(activity);

        result.mToast.setDuration(duration);
        result.mToast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        result.setText(message);

        return result;
    }

    public void show() {
        mToast.show();
    }

    public void cancel() {
        mToast.cancel();
    }

    public void setText(String message) {
        TextView txtMessage = (TextView) mToast.getView().findViewById(R.id.txtMessage);
        txtMessage.setText(message);
    }

    public void setText(int messageId) {
        setText(mActivity.getText(messageId).toString());
    }

    public boolean isShowing() {
        return mToast.getView().isShown();
    }

    public void setDuration(int duration) {
        mToast.setDuration(duration);
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        mToast.setGravity(gravity, xOffset, yOffset);
    }
}
