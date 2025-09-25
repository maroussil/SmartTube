package com.liskovsoft.smartyoutubetv2.tv.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.liskovsoft.smartyoutubetv2.common.app.presenters.SplashPresenter;
import com.liskovsoft.smartyoutubetv2.common.app.views.SplashView;
import com.liskovsoft.smartyoutubetv2.common.misc.MotherActivity;
import com.liskovsoft.smartyoutubetv2.common.misc.UserStateManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class SplashActivity extends MotherActivity implements SplashView {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private Intent mNewIntent;
    private SplashPresenter mPresenter;
    private UserStateManager mUserStateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNewIntent = getIntent();

        mPresenter = SplashPresenter.instance(this);
        mPresenter.setView(this);
        mPresenter.onViewInitialized();

        mUserStateManager = UserStateManager.getInstance(this);
        checkUserCredits();

        //finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mNewIntent = intent;

        mPresenter.onViewInitialized();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onViewDestroyed();
    }

    @Override
    public Intent getNewIntent() {
        return mNewIntent;
    }

    @Override
    public void finishView() {
        try {
            finish();
        } catch (NullPointerException e) {
            // NullPointerException: Attempt to invoke virtual method 'void com.android.server.wm.DisplayContent.moveStack(com.android.server.wm.TaskStack, boolean)'
            e.printStackTrace();
        }
    }

    private void checkUserCredits() {
        String userId = mUserStateManager.getUserId();
        if (userId == null) {
            return;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://your-credit-server.com/api/v1/check_credits?id=" + userId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Log the error or show a toast to the user
                Log.e(TAG, "Failed to check credits", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // Log the error or show a toast to the user
                    Log.e(TAG, "Failed to check credits: " + response);
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    JSONObject json = new JSONObject(responseBody);
                    String status = json.optString("status");
                    mUserStateManager.setCreditsStatus(status);

                    if ("exhausted".equals(status)) {
                        runOnUiThread(() -> showCreditsExhaustedUI());
                    }
                } catch (JSONException e) {
                    // Log the error or show a toast to the user
                    Log.e(TAG, "Failed to parse credit check response", e);
                }
            }
        });
    }

    private void showCreditsExhaustedUI() {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(WebViewActivity.EXTRA_URL, "https://your-credit-server.com/top_up");
        startActivity(intent);
    }
}