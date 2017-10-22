package se.chalmers.bookreviewclient.ui;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import se.chalmers.bookreviewclient.BookReviewApplication;
import se.chalmers.bookreviewclient.R;
import se.chalmers.bookreviewclient.model.Errors;
import se.chalmers.bookreviewclient.model.Language;
import se.chalmers.bookreviewclient.net.WebRequestManager;

public class NewReviewActivity extends AppCompatActivity implements WebRequestManager.WebRequestHandler {
    private RatingBar mRbBookRating;
    private EditText mEtReview;

    private ArrayList<Language> mLanguages;
    private int mBookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_review);

        Bundle extras = getIntent().getExtras();
        mBookId = extras.getInt(getString(R.string.key_book_id));
        String bookName = extras.getString(getString(R.string.key_book_title));

        // Get references to views
        TextView tvTitle = (TextView) findViewById(R.id.tv_book_title);
        mRbBookRating = (RatingBar) findViewById(R.id.rb_book_rating);
        mEtReview = (EditText) findViewById(R.id.et_review);

        // Fill data
        tvTitle.setText(getString(R.string.new_review_text, bookName));

        // Init button listeners
        Button btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(NewReviewActivity.this, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
                } else {
                    builder = new AlertDialog.Builder(NewReviewActivity.this);
                }
                builder.setTitle(R.string.dialog_title_cancel_review)
                        .setMessage(R.string.dialog_body_cancel_review)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                NewReviewActivity.this.finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });

        Button btnSave = (Button) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String token = ((BookReviewApplication) getApplication()).getUserToken();
                if (token == null) {
                    Toast.makeText(NewReviewActivity.this, getString(R.string.error_logged_out), Toast.LENGTH_SHORT).show();
                    finish();

                    return;
                }
                if (mRbBookRating.getRating() == 0) {
                    Toast.makeText(NewReviewActivity.this, R.string.error_rating_missing, Toast.LENGTH_SHORT).show();

                    return;
                }
                WebRequestManager.getInstance().postBookReview(mBookId, mRbBookRating.getRating(), mEtReview.getText().toString(), token, NewReviewActivity.this);
            }
        });
    }

    @Override
    public void onSuccess(Object data) {
        Toast.makeText(this, getString(R.string.review_added), Toast.LENGTH_SHORT).show();

        finish();
    }

    @Override
    public void onFailure(Errors error) {
        Toast.makeText(this, getString(R.string.error_server), Toast.LENGTH_SHORT).show();
    }
}
