package se.chalmers.bookreviewclient.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import se.chalmers.bookreviewclient.R;
import se.chalmers.bookreviewclient.model.Errors;
import se.chalmers.bookreviewclient.net.WebRequestManager;

public class LoginDialogFragment extends DialogFragment implements WebRequestManager.WebRequestHandler {
    public interface LoginDialogListener {
        void onLogin(String username, String token);
    }

    private EditText mEtUsername;
    private Bitmap mQrCode;
    private LoginDialogListener mLoginDialogListener;

    public static LoginDialogFragment newInstance() {
        LoginDialogFragment f = new LoginDialogFragment();

        Bundle args = new Bundle();
        f.setArguments(args);

        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_dialog, container, false);

        mEtUsername = rootView.findViewById(R.id.et_username);
        final EditText etPassword = rootView.findViewById(R.id.et_password);

        Button btnLogin = rootView.findViewById(R.id.btn_login);
        Button btnCancel = rootView.findViewById(R.id.btn_cancel);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEtUsername.getText().length() > 0 && etPassword.getText().length() > 0) {
                    WebRequestManager.getInstance().login(mEtUsername.getText().toString(), etPassword.getText().toString(), LoginDialogFragment.this);
                } else {
                    Toast.makeText(getContext(), getString(R.string.error_empty_username_password), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return rootView;
    }

    @Override
    public void onSuccess(Object data) {
        String token = (String) data;
        if (mLoginDialogListener != null) {
            mLoginDialogListener.onLogin(mEtUsername.getText().toString(), token);
        }

        dismiss();
    }

    @Override
    public void onFailure(Errors error) {
        String errorMessage = "";

        switch (error) {
            case UsernamePasswordIncorrectError:
                errorMessage = getString(R.string.error_empty_username_password);
                break;
            case ServerError:
            case ConnectionError:
                errorMessage = getString(R.string.error_server);
                break;
        }

        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void setLoginDialogListener(LoginDialogListener loginDialogListener) {
        this.mLoginDialogListener = loginDialogListener;
    }
}
