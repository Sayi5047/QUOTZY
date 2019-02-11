package io.hustler.qtzy.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.hustler.qtzy.R;
import io.hustler.qtzy.ui.apiRequestLauncher.Base.BaseResponse;
import io.hustler.qtzy.ui.apiRequestLauncher.Constants;
import io.hustler.qtzy.ui.apiRequestLauncher.ListnereInterfaces.ResponseListener;
import io.hustler.qtzy.ui.apiRequestLauncher.Restutility;
import io.hustler.qtzy.ui.apiRequestLauncher.request.ReqUserGoogleSignup;
import io.hustler.qtzy.ui.apiRequestLauncher.request.ReqUserSignup;
import io.hustler.qtzy.ui.apiRequestLauncher.response.ResLoginUser;
import io.hustler.qtzy.ui.utils.Toast_Snack_Dialog_Utils;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    private static final int RC_SIGN_IN = 404;
    private static final String TAG = "LOGIN";
    private static final int MINIMUM_AGE = 13;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    int age;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView, mNameView, mCountryView, mGenderView, mDobView;
    private View mProgressView;
    private View mLoginFormView;
    private SignInButton mGoogleSignin;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
//        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mNameView = findViewById(R.id.name);
        mCountryView = findViewById(R.id.country);
        mDobView = findViewById(R.id.dob);
        mGenderView = findViewById(R.id.gender);
        mGoogleSignin = findViewById(R.id.g_signin);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignInOrSignup();
            }
        });
        mDobView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar myCalendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, day);
                        String format = "dd/MM/yyyy";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                        Calendar calendar = Calendar.getInstance();
                        age = calendar.get(Calendar.YEAR) - myCalendar.get(Calendar.YEAR);
                        mDobView.setText(simpleDateFormat.format(myCalendar.getTime()));
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(LoginActivity.this, onDateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                Calendar calendar = Calendar.getInstance();


                calendar.set(Calendar.YEAR, (calendar.get(Calendar.YEAR) - MINIMUM_AGE));
                datePickerDialog.updateDate(2000, 0, 1);
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                datePickerDialog.show();

            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signupOrLoginUser();
            }
        });

        getIntentDetails();
    }

    private void getIntentDetails() {
        boolean isLogin = getIntent().getBooleanExtra("IS_LOGIN", false);
        if (isLogin) {
            mDobView.setVisibility(View.GONE);
            mCountryView.setVisibility(View.GONE);
            mGenderView.setVisibility(View.GONE);
            mNameView.setVisibility(View.GONE);
        } else {
            mDobView.setVisibility(View.VISIBLE);
            mCountryView.setVisibility(View.VISIBLE);
            mGenderView.setVisibility(View.VISIBLE);
            mNameView.setVisibility(View.VISIBLE);
        }
    }

    private void signupOrLoginUser() {
        ReqUserSignup reqUserSignup = new ReqUserSignup();
        reqUserSignup.setName(mNameView.getText().toString());
        reqUserSignup.setEmail(mEmailView.getText().toString());
        reqUserSignup.setAge((short) age);
        reqUserSignup.setDob(mDobView.getText().toString());
        reqUserSignup.setPassword(mPasswordView.getText().toString());
        reqUserSignup.setCountry(Locale.getDefault().getCountry());
        reqUserSignup.setLocale(Locale.getDefault().getLanguage());
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.Authorising));
        progressDialog.show();
        new Restutility(LoginActivity.this).signUp(new ResponseListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                progressDialog.hide();
                ResLoginUser resLoginUser = ((ResLoginUser) baseResponse);
                switch (baseResponse.getStatuscode()) {
                    case (Constants.USER_CREATED):
                        // TODO: 10-02-2019 Save in shared preferences and log the user in
                        Toast_Snack_Dialog_Utils.show_ShortToast(LoginActivity.this, (resLoginUser.getName()));

                        break;
                    case (Constants.USER_ALREADY_EXISTS):
                        Toast_Snack_Dialog_Utils.show_ShortToast(LoginActivity.this, (Constants.USER_ALREADY_EXISTS) + "");

                        break;
                    default:
                        Toast_Snack_Dialog_Utils.show_ShortToast(LoginActivity.this, resLoginUser.getMessage());
                        break;
                }
            }

            @Override
            public void onError(String message) {
                progressDialog.hide();
                Toast_Snack_Dialog_Utils.show_ShortToast(LoginActivity.this, message);
            }
        }, LoginActivity.this, reqUserSignup);
    }

    private void googleSignInOrSignup() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            String idToken = account.getIdToken();
            String personId = account.getId();
            ReqUserGoogleSignup reqUserGoogleSignup = new ReqUserGoogleSignup();
            reqUserGoogleSignup.setgId(idToken);
            reqUserGoogleSignup.setPersonId(personId);
            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.Authorising));
            progressDialog.show();
            new Restutility(LoginActivity.this).gSignup(new ResponseListener() {
                @Override
                public void onSuccess(BaseResponse baseResponse) {
                    progressDialog.cancel();
                    ResLoginUser resLoginUser = ((ResLoginUser) baseResponse);
                    switch (baseResponse.getStatuscode()) {
                        case (Constants.USER_CREATED):
                            // TODO: 10-02-2019 Save in shared preferences and log the user in
                            Toast_Snack_Dialog_Utils.show_ShortToast(LoginActivity.this, (resLoginUser.getName()));

                            break;
                        case (Constants.USER_ALREADY_EXISTS):
                            Toast_Snack_Dialog_Utils.show_ShortToast(LoginActivity.this, (Constants.USER_ALREADY_EXISTS) + "");

                            break;
                        default:
                            Toast_Snack_Dialog_Utils.show_ShortToast(LoginActivity.this, resLoginUser.getMessage());
                            break;

                    }
                }

                @Override
                public void onError(String message) {
                    progressDialog.cancel();
                    Toast_Snack_Dialog_Utils.show_ShortToast(LoginActivity.this, message);
                }
            }, LoginActivity.this, reqUserGoogleSignup);

        } catch (ApiException e) {

            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast_Snack_Dialog_Utils.show_ShortToast(LoginActivity.this, "Failure");
        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

