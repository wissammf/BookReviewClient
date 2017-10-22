package se.chalmers.bookreviewclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import se.chalmers.bookreviewclient.BookReviewApplication;
import se.chalmers.bookreviewclient.R;

public class MainActivity extends AppCompatActivity implements LoginDialogFragment.LoginDialogListener {
    private Button mBtnNewReview;
    private Button mBtnLogout;
    private TextView mTvLoginInfo;

    private boolean mLoggedIn;
    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnNewReview = (Button) findViewById(R.id.btn_new_review);
        mBtnLogout = (Button) findViewById(R.id.btn_logout);
        mTvLoginInfo = (TextView) findViewById(R.id.tv_login_info);

        String userToken = ((BookReviewApplication) getApplication()).getUserToken();
        mUsername = ((BookReviewApplication) getApplication()).getUsername();
        mLoggedIn = userToken != null;

        mBtnNewReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the user has logged in before
                if (mLoggedIn) {
                    IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                    integrator.setPrompt("Scan the QR code on the tablet!");
                    integrator.setBeepEnabled(false);
                    integrator.setBarcodeImageEnabled(true);
                    integrator.setOrientationLocked(false);
                    integrator.initiateScan();
                } else {
                    FragmentManager fm = getSupportFragmentManager();
                    LoginDialogFragment dialogFragment = LoginDialogFragment.newInstance();
                    dialogFragment.setLoginDialogListener(MainActivity.this);
                    dialogFragment.show(fm, "tag");
                }
            }
        });

        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BookReviewApplication) getApplication()).logout();

                mUsername = null;
                mLoggedIn = false;

                setupViews();
            }
        });

        setupViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String scannedContent = result.getContents();
                String[] contents = scannedContent.split("@");

                if (contents.length == 2) {
                    Intent intent = new Intent(MainActivity.this, NewReviewActivity.class);
                    intent.putExtra(MainActivity.this.getString(R.string.key_book_id), Integer.valueOf(contents[0]));
                    intent.putExtra(MainActivity.this.getString(R.string.key_book_title), contents[1]);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.error_get_scanned_content, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onLogin(String username, String token) {
        ((BookReviewApplication) getApplication()).saveUsername(username);
        ((BookReviewApplication) getApplication()).saveUserToken(token);

        mLoggedIn = true;
        mUsername = username;

        setupViews();
    }

    private void setupViews() {
        if (mLoggedIn) {
            mBtnNewReview.setText(R.string.btn_new_review);
            mTvLoginInfo.setText(String.format(getString(R.string.logged_in_text), mUsername));
            mBtnLogout.setVisibility(View.VISIBLE);
        } else {
            mBtnNewReview.setText(R.string.btn_login);
            mTvLoginInfo.setText(R.string.not_logged_in_text);
            mBtnLogout.setVisibility(View.GONE);
        }
    }
}
