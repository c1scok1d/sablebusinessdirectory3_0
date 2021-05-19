package com.macinternetservices.sablebusinessdirectory.ui.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserInfo;
import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.MainActivity;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.binding.FragmentDataBindingComponent;
import com.macinternetservices.sablebusinessdirectory.databinding.FragmentUserLoginBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSFragment;
import com.macinternetservices.sablebusinessdirectory.utils.AutoClearedValue;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.PSDialogMsg;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewmodel.user.UserViewModel;
import com.macinternetservices.sablebusinessdirectory.viewobject.User;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;


/**
 * UserLoginFragment
 */
public class UserLoginFragment extends PSFragment {


    //region Variables


    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private UserViewModel userViewModel;

    private PSDialogMsg psDialogMsg;

    @VisibleForTesting
    private AutoClearedValue<FragmentUserLoginBinding> binding;

    private AutoClearedValue<ProgressDialog> prgDialog;

    private CallbackManager callbackManager;

    private boolean checkFlag;

    private String token;

    //Firebase test
    private FirebaseAuth mAuth;

    //google login

    private GoogleSignInClient mGoogleSignInClient;

    //endregion

    //Firebase test
    FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String email = "";
    private String userEmail= "";
    private String userPassword= "";

    //region Override Methods

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        FacebookSdk.sdkInitialize(getContext());

        callbackManager = CallbackManager.Factory.create();
        // Inflate the layout for this fragment
        FragmentUserLoginBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_login, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        return binding.get().getRoot();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Constants.REQUEST_CODE__GOOGLE_SIGN);
    }


    private void handleFacebookAccessToken(AccessToken accessToken){
        String token = pref.getString(Constants.NOTI_TOKEN, Constants.USER_NO_DEVICE_TOKEN);
        Utils.psLog("HandleFacebookAccessToken: "+ accessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());

        if (getActivity() != null) {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Utils.psLog("signInWithCredential:success");
                                user = mAuth.getCurrentUser();
                                if (user != null) {
                                    List<? extends UserInfo> userInfoList = user.getProviderData();

                                    String email = "";
                                    String uid = "";
                                    String displayName = "";
                                    String photoUrl = "";
                                    for (int i = 0; i < userInfoList.size(); i++) {

                                        email = userInfoList.get(i).getEmail();

                                        if (email != null && !email.equals("")) {
                                            uid = userInfoList.get(i).getUid();
                                            displayName = userInfoList.get(i).getDisplayName();
                                            photoUrl = String.valueOf(userInfoList.get(i).getPhotoUrl());

                                            break;
                                        }

                                    }
                                    userViewModel.registerFBUser(uid,displayName, email, photoUrl, token);
                                } else {
                                    // Error Message
                                    Toast.makeText(UserLoginFragment.this.getActivity(), getString(R.string.login__fail_account), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Utils.psLog("signInWithCredential:failure"+ task.getException());
                                handleFirebaseAuthError(email);
                            }
                        }
                    });
        }
    }

    private void handleFirebaseAuthError(String email){
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if(task.isSuccessful()){
                    SignInMethodQueryResult result = task.getResult();
                    List<String> signInMethod = result.getSignInMethods();

                    Utils.psLog("SignInMethod  ="+ signInMethod);
                    if(signInMethod.contains(Constants.EMAILAUTH)){
                        psDialogMsg.showErrorDialog("["+email+"]"+getString(R.string.login__auth_email),getString(R.string.app__ok));
                        psDialogMsg.show();
                    }else if(signInMethod.contains(Constants.GOOGLEAUTH)){
                        psDialogMsg.showErrorDialog("["+email+"]"+getString(R.string.login__auth_google),getString(R.string.app__ok));
                        psDialogMsg.show();
                    }else if(signInMethod.contains(Constants.FACEBOOKAUTH)){
                        psDialogMsg.showErrorDialog("["+email+"]"+getString(R.string.login__auth_facebook),getString(R.string.app__ok));
                        psDialogMsg.show();
                    }
                }
            }
        });
    }

    public FirebaseUser createUserWithEmailAndPassword(String email, String password){
        try{
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(UserLoginFragment.this.getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Utils.psLog("createUserWithEmail:success");
                                user = mAuth.getCurrentUser();
                                doSubmit(email, binding.get().passwordEditText.getText().toString());

                            } else {

                                //fail
                                if (!email.equals(Constants.DEFAULTEMAIL)) {
                                    createUserWithEmailAndPassword(Constants.DEFAULTEMAIL, Constants.DEFAULTPASSWORD);
                                } else {
                                    //fail
                                    signInWithEmailAndPassword(Constants.DEFAULTEMAIL, Constants.DEFAULTPASSWORD);
                                }
                            }
                        }

                    });
        }catch (Exception exception){

            //If sign in fails, display a message to the user.
            Utils.psLog("createUserWithEmail:failure");

            psDialogMsg.showErrorDialog( getString(R.string.login__exception_error), getString(R.string.app__ok));
            psDialogMsg.show();

        }

        return  user;
    }

    private FirebaseUser signInWithEmailAndPassword(String email, String password){
        Utils.psLog(email + " " + password);
        try {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(UserLoginFragment.this.getActivity(), new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful()) {
                        try {
                            //Success
                            Utils.psLog("signInWithEmail:success" + userEmail);
                            user = mAuth.getCurrentUser();
                            Utils.psLog("doSubmit");
                            doSubmit(userEmail, binding.get().passwordEditText.getText().toString());
                        }catch(Exception e) {
                            Utils.psErrorLog("", e);
                        }
                    }

                    else {
                        // Fail
                        if(!email.equals(Constants.DEFAULTEMAIL)) {
                            createUserWithEmailAndPassword(email,email);
                        }else {
                            userViewModel.isLoading= false;
                            updateLoginBtnStatus();
                            // Error Handling
                            Utils.psLog("handleFirebaseAuthError");
                            handleFirebaseAuthError(binding.get().emailEditText.getText().toString().trim());
                        }

                    }

                }

            });
        }catch (Exception e){
            Utils.psLog("signInWithEmail:failure");
            psDialogMsg.showWarningDialog(getString(R.string.login__failure_error), getString(R.string.app__ok));
            psDialogMsg.show();
        }

        return  user;
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        if (getActivity() != null) {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(getActivity(), task -> {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            if (user != null) {
                                List<? extends UserInfo> userInfoList = user.getProviderData();

                                String email = "";
                                String uid = "";
                                String displayName = "";
                                String photoUrl = "";
                                for (int i = 0; i < userInfoList.size(); i++) {

                                    email = userInfoList.get(i).getEmail();

                                    if (email != null && !email.equals("")) {
                                        uid = userInfoList.get(i).getUid();
                                        displayName = userInfoList.get(i).getDisplayName();
                                        photoUrl = String.valueOf(userInfoList.get(i).getPhotoUrl());

                                        break;
                                    }

                                }
                                userViewModel.setGoogleLoginUser(uid, displayName, email, photoUrl, token);

                            } else {
                                // Error Message
                                Toast.makeText(UserLoginFragment.this.getActivity(), getString(R.string.login__fail_account), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(UserLoginFragment.this.getActivity(), getString(R.string.login__fail), Toast.LENGTH_LONG).show();
                            String email = user.getEmail();
                            handleFirebaseAuthError(email);
                        }
                    });
        }
    }


    @Override
    protected void initUIAndActions() {

        dataBindingComponent.getFragmentBindingAdapters().bindFullImageDrawable(binding.get().bgImageView, getResources().getDrawable(R.drawable.login_app_bg));

        if(getActivity() instanceof MainActivity)  {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            ((MainActivity) this.getActivity()).binding.toolbar.setBackgroundColor(getResources().getColor(R.color.global__primary));
            ((MainActivity)getActivity()).updateToolbarIconColor(Color.WHITE);
            ((MainActivity)getActivity()).updateMenuIconWhite();
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = firebaseAuth -> {
            user = mAuth.getCurrentUser();
            if (user != null) {
                int a;
//                Toast.makeText(UserLoginFragment.this.getContext(), "Successfully singned in with" + currentUser.getUid(), Toast.LENGTH_SHORT).show();
            }
        };

        //end
        binding.get().phoneLoginButton.setOnClickListener(v -> Utils.navigateAfterPhoneLogin(getActivity(),navigationController));

        // Configure Google Sign In
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        if (getActivity() != null) {
            mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);
        }
        //end

        psDialogMsg = new PSDialogMsg(getActivity(), false);

        // Init Dialog
        prgDialog = new AutoClearedValue<>(this, new ProgressDialog(getActivity()));
        //prgDialog.get().setMessage(getString(R.string.message__please_wait));

        prgDialog.get().setMessage((Utils.getSpannableString(getContext(), getString(R.string.message__please_wait), Utils.Fonts.MM_FONT)));
        prgDialog.get().setCancelable(false);

        //fadeIn Animation
        fadeIn(binding.get().getRoot());

        binding.get().loginButton.setOnClickListener(view -> {

            Utils.hideKeyboard(getActivity());

            if (connectivity.isConnected()) {
                 userEmail = binding.get().emailEditText.getText().toString().trim();
                 userPassword = binding.get().passwordEditText.getText().toString().trim();

                Utils.psLog("Email " + userEmail);
                Utils.psLog("Password " + userPassword);

                if (userEmail.equals("")) {

                    psDialogMsg.showWarningDialog(getString(R.string.error_message__blank_email), getString(R.string.app__ok));
                    psDialogMsg.show();
                    return;
                }

                if (userPassword.equals("")) {

                    psDialogMsg.showWarningDialog(getString(R.string.error_message__blank_password), getString(R.string.app__ok));
                    psDialogMsg.show();
                    return;
                }

                if (!userViewModel.isLoading) {

                    userViewModel.isLoading= false;
                    updateLoginBtnStatus();
                    Utils.psLog("Sign in with email and password");
                    signInWithEmailAndPassword(userEmail,userEmail);

                }
            } else {

                psDialogMsg.showWarningDialog(getString(R.string.no_internet_error), getString(R.string.app__ok));
                psDialogMsg.show();
            }

        });

        binding.get().registerButton.setOnClickListener(view ->
                Utils.navigateAfterRegister(UserLoginFragment.this.getActivity(), navigationController));

        binding.get().forgotPasswordButton.setOnClickListener(view ->
                Utils.navigateAfterForgotPassword(UserLoginFragment.this.getActivity(), navigationController));

        if (Config.ENABLE_FACEBOOK_LOGIN) {
            binding.get().fbLoginButton.setVisibility(View.VISIBLE);
        } else {
            binding.get().fbLoginButton.setVisibility(View.GONE);
        }
        if (Config.ENABLE_GOOGLE_LOGIN) {
            binding.get().googleLoginButton.setVisibility(View.VISIBLE);
        } else {
            binding.get().googleLoginButton.setVisibility(View.GONE);
        }
        if (Config.ENABLE_PHONE_LOGIN){
            binding.get().phoneLoginButton.setVisibility(View.VISIBLE);
        }else{
            binding.get().phoneLoginButton.setVisibility(View.GONE);
        }
        //google login
        binding.get().googleLoginButton.setOnClickListener(v -> signIn());

        //for check privacy and policy
        binding.get().privacyPolicyCheckbox.setOnClickListener(v -> {
            if (binding.get().privacyPolicyCheckbox.isChecked()) {

                navigationController.navigateToPrivacyPolicyActivity(getActivity(),Constants.EMPTY_STRING);
                userViewModel.checkFlag = true;
                binding.get().googleSignInView.setVisibility(View.GONE);
                binding.get().facebookSignInView.setVisibility(View.GONE);
                binding.get().phoneSignInView.setVisibility(View.GONE);
                binding.get().fbLoginButton.setEnabled(true);
                binding.get().googleLoginButton.setEnabled(true);
                binding.get().phoneLoginButton.setEnabled(true);
            } else {

                userViewModel.checkFlag = false;
                binding.get().googleSignInView.setVisibility(View.VISIBLE);
                binding.get().facebookSignInView.setVisibility(View.VISIBLE);
                binding.get().phoneSignInView.setVisibility(View.VISIBLE);
                binding.get().fbLoginButton.setEnabled(false);
                binding.get().googleLoginButton.setEnabled(false);
                binding.get().phoneLoginButton.setEnabled(false);

            }
        });

        // For First Time Loading
        if (!userViewModel.checkFlag) {
            binding.get().googleSignInView.setVisibility(View.VISIBLE);
            binding.get().facebookSignInView.setVisibility(View.VISIBLE);
            binding.get().phoneSignInView.setVisibility(View.VISIBLE);
            binding.get().fbLoginButton.setEnabled(false);
            binding.get().googleLoginButton.setEnabled(false);
            binding.get().phoneLoginButton.setEnabled(false);

        } else {
            binding.get().googleSignInView.setVisibility(View.GONE);
            binding.get().facebookSignInView.setVisibility(View.GONE);
            binding.get().phoneSignInView.setVisibility(View.GONE);
            binding.get().fbLoginButton.setEnabled(true);
            binding.get().googleLoginButton.setEnabled(true);
            binding.get().phoneLoginButton.setEnabled(true);

        }

        if (Config.ENABLE_FACEBOOK_LOGIN || Config.ENABLE_GOOGLE_LOGIN || Config.ENABLE_PHONE_LOGIN) {
            binding.get().privacyPolicyCheckbox.setVisibility(View.VISIBLE);
        } else {
            binding.get().privacyPolicyCheckbox.setVisibility(View.GONE);
        }

        View.OnClickListener onClickListener = v -> {

            psDialogMsg.showWarningDialog(getString(R.string.error_message__to_check_agreement), getString(R.string.app__ok));
            psDialogMsg.show();

        };

        binding.get().facebookSignInView.setOnClickListener(onClickListener);

        binding.get().googleSignInView.setOnClickListener(onClickListener);

        binding.get().phoneSignInView.setOnClickListener(onClickListener);
    }

    private void updateLoginBtnStatus() {
        if (userViewModel.isLoading) {
            binding.get().loginButton.setText(getResources().getString(R.string.message__loading));
        } else {
            binding.get().loginButton.setText(getResources().getString(R.string.login__login));
        }
    }

    private void doSubmit(String email, String password) {

        //prgDialog.get().show();
        userViewModel.setUserLogin(new User(
                "",
                "",
                "",
                "",
                "",
                email,
                email,
                "",
                password,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                token));

        userViewModel.isLoading = true;

    }


    @Override
    protected void initViewModels() {
        userViewModel = new ViewModelProvider(this, viewModelFactory).get(UserViewModel.class);
    }

    @Override
    protected void initAdapters() {

    }

    @Override
    protected void initData() {

        token = pref.getString(Constants.NOTI_TOKEN, Constants.USER_NO_DEVICE_TOKEN);

        userViewModel.getLoadingState().observe(this, loadingState -> {

            if (loadingState != null && loadingState) {
                prgDialog.get().show();
            } else {
                prgDialog.get().cancel();
            }

            updateLoginBtnStatus();

        });

        userViewModel.getUserLoginStatus().observe(this, listResource -> {

            if (listResource != null) {

                Utils.psLog("Got Data" + listResource.message + listResource.toString());

                switch (listResource.status) {
                    case LOADING:
                        // Loading State
                        // Data are from Local DB

                        break;
                    case SUCCESS:
                        // Success State
                        // Data are from Server

                        if (listResource.data != null) {
                            try {

                                Utils.updateUserLoginData(pref, listResource.data.user);
                                Utils.navigateAfterUserLogin(getActivity(),navigationController);

                            } catch (NullPointerException ne) {
                                Utils.psErrorLog("Null Pointer Exception.", ne);
                            } catch (Exception e) {
                                Utils.psErrorLog("Error in getting notification flag data.", e);
                            }

                            userViewModel.setLoadingState(false);

                        }

                        break;
                    case ERROR:
                        // Error State

                        psDialogMsg.showErrorDialog(listResource.message, getString(R.string.app__ok));
                        psDialogMsg.show();

                        userViewModel.setLoadingState(false);

                        break;
                    default:
                        // Default

                        userViewModel.setLoadingState(false);

                        break;
                }

            } else {

                // Init Object or Empty Data
                Utils.psLog("Empty Data");

            }
        });

        binding.get().fbLoginButton.setPermissions(Collections.singletonList("email"));
        binding.get().fbLoginButton.setFragment(this);
        binding.get().fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                handleFacebookAccessToken(loginResult.getAccessToken());

                                String name = "";


                                String id = "";
                                String imageURL = "";
                                try {
                                    if (object != null) {

                                        name = object.getString("name");

                                    }
                                    //link.setText(object.getString("link"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    if (object != null) {

                                        email = object.getString("email");

                                    }
                                    //link.setText(object.getString("link"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    if (object != null) {

                                        id = object.getString("id");

                                    }
                                    //link.setText(object.getString("link"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email,name,id");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

                Utils.psLog("OnCancel.");
            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
                Utils.psLog("OnError." + e);
            }


        });


        userViewModel.getRegisterFBUserData().observe(this, listResource -> {

            if (listResource != null) {

                Utils.psLog("Got Data" + listResource.message + listResource.toString());

                switch (listResource.status) {
                    case LOADING:
                        // Loading State
                        // Data are from Local DB

                        prgDialog.get().show();

                        break;
                    case SUCCESS:
                        // Success State
                        // Data are from Server

                        if (listResource.data != null) {
                            try {

                                Utils.updateUserLoginData(pref, listResource.data.user);
                                Utils.navigateAfterUserLogin(getActivity(),navigationController);

                            } catch (NullPointerException ne) {
                                Utils.psErrorLog("Null Pointer Exception.", ne);
                            } catch (Exception e) {
                                Utils.psErrorLog("Error in getting notification flag data.", e);
                            }

                            userViewModel.isLoading = false;
                            prgDialog.get().cancel();

                        }

                        break;
                    case ERROR:
                        // Error State

                        userViewModel.isLoading = false;
                        prgDialog.get().cancel();

                        break;
                    default:
                        // Default
                        //userViewModel.isLoading = false;
                        //prgDialog.get().cancel();
                        userViewModel.isLoading = false;
                        prgDialog.get().cancel();

                        psDialogMsg.showErrorDialog(listResource.message, getString(R.string.app__ok));
                        psDialogMsg.show();
                        break;
                }

            } else {

                // Init Object or Empty Data
                Utils.psLog("Empty Data");

            }

        });

        userViewModel.getGoogleLoginData().observe(this, listResource -> {

            if (listResource != null) {

                Utils.psLog("Got Data" + listResource.message + listResource.toString());

                switch (listResource.status) {
                    case LOADING:
                        // Loading State
                        // Data are from Local DB

                        prgDialog.get().show();

                        break;
                    case SUCCESS:
                        // Success State
                        // Data are from Server

                        if (listResource.data != null) {
                            try {

                                Utils.updateUserLoginData(pref,listResource.data.user);
                                Utils.navigateAfterUserLogin(getActivity(),navigationController);

                            } catch (NullPointerException ne) {
                                Utils.psErrorLog("Null Pointer Exception.", ne);
                            } catch (Exception e) {
                                Utils.psErrorLog("Error in getting notification flag data.", e);
                            }

                            userViewModel.isLoading = false;
                            updateLoginBtnStatus();
                            prgDialog.get().cancel();

                        }

                        break;
                    case ERROR:
                        // Error State

                        userViewModel.isLoading = false;
                        prgDialog.get().cancel();
                        psDialogMsg.showErrorDialog(listResource.message, getString(R.string.app__ok));
                        psDialogMsg.show();

                        break;
                    default:
                        // Default
                        //userViewModel.isLoading = false;
                        //prgDialog.get().cancel();
                        break;
                }

            } else {

                // Init Object or Empty Data
                Utils.psLog("Empty Data");

            }

        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE__GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

}

//endregion

