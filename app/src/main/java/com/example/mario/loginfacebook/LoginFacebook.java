package com.example.mario.loginfacebook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class LoginFacebook extends AppCompatActivity {
    private TextView mensaje;
    private LoginButton loginButton;
    private ProfilePictureView imageProfile;
    private Profile profile;
    private ProfileTracker profileTracker;

    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                // App code
            }
        };
        
        
        setContentView(R.layout.activity_main);
        loginButton= (LoginButton) findViewById(R.id.login_button);
        mensaje= (TextView) findViewById(R.id.mensajedelogin);
        imageProfile= (ProfilePictureView) findViewById(R.id.imageprofile);

        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        profile=Profile.getCurrentProfile();
        if (profile!=null){
            profile=Profile.getCurrentProfile();
            mensaje.setText(profile.getName());
            imageProfile.setProfileId(profile.getId());
            imageProfile.setVisibility(View.VISIBLE);
        }
        else {
            mensaje.setText("");
            imageProfile.setVisibility(View.INVISIBLE);

        }

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                try {
                                    profile=Profile.getCurrentProfile();
                                    mensaje.setText(profile.getName());
                                    imageProfile.setProfileId(profile.getId());
                                    imageProfile.setVisibility(View.VISIBLE);
                                    Log.d("link",object.getString("link"));
                                    Log.d("link",object.getString("email"));
                                    Log.d("link",object.getString("birthday"));
                                    Log.d("link",object.getString("gender"));
                                    Log.d("link", object.getJSONObject("picture").getJSONObject("data").getString("url"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                // Application code
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,birthday,picture.type(normal),gender");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                mensaje.setText("Login Cancel");

            }

            @Override
            public void onError(FacebookException error) {
                mensaje.setText("Login Error:"+error.getMessage());


            }
        });


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
