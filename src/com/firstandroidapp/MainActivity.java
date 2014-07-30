package com.firstandroidapp;

import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.facebook.widget.ProfilePictureView;

public class MainActivity extends FragmentActivity {
	private ProfilePictureView profilePictureView;
	
	private LoginButton loginBtn;
	private Button postImageBtn;
	private Button updateStatusBtn;
 
	private TextView userName;
 
	private UiLifecycleHelper uiHelper;
 
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
 
	private static String message = "Sample status posted from android app";
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 //kayhash   https://developers.facebook.com/docs/android/getting-started
		uiHelper = new UiLifecycleHelper(this, statusCallback);
		uiHelper.onCreate(savedInstanceState);
 
		setContentView(R.layout.activity_main);
		profilePictureView = (ProfilePictureView)findViewById(R.id.selection_profile_pic);
		profilePictureView.setCropped(true);
		
		userName = (TextView) findViewById(R.id.user_name);
		loginBtn = (LoginButton) findViewById(R.id.fb_login_button);
		loginBtn.setUserInfoChangedCallback(new UserInfoChangedCallback() {
			@Override
			public void onUserInfoFetched(GraphUser user) {
				if (user != null) {
					userName.setText("Hello, " + user.getName() );
					profilePictureView.setProfileId(user.getId());
					
				} else {
					userName.setText("You are not logged");
					profilePictureView.setProfileId(null);
				}
			}
		});
 
		postImageBtn = (Button) findViewById(R.id.post_image);
		postImageBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				postImage();
			}
		});
		 
		 
		updateStatusBtn = (Button) findViewById(R.id.update_status);
		updateStatusBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			 
			}
		});
 
		buttonsEnabled(false);
	}
/*	// resmi getiren metod
	private void makeMeRequest(final Session session) {
	    // Make an API call to get user data and define a 
	    // new callback to handle the response.
	    Request request = Request.newMeRequest(session, 
	            new Request.GraphUserCallback() {
	        @Override
	        public void onCompleted(GraphUser user, Response response) {
	            // If the response is successful
	            if (session == Session.getActiveSession()) {
	                if (user != null) {
	                	System.out.println("makemerequest");
	                    // Set the id for the ProfilePictureView
	                    // view that in turn displays the profile picture.
	                    profilePictureView.setProfileId(user.getId());
	                    // Set the Textview's text to the user's name.
	                    
	                }
	            }
	            if (response.getError() != null) {
	                // Handle errors, will do so later.
	            }
	        }
	    });
	    request.executeAsync();
	} 
	 
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
		 if (session != null && session.isOpened()) {
		        // Get the user's data.
		        makeMeRequest(session);
		    }
    }
	 */
	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if (state.isOpened()) {
				buttonsEnabled(true);
				Log.d("FacebookSampleActivity", "Facebook session opened");
				  
			} else if (state.isClosed()) {
				buttonsEnabled(false);
				Log.d("FacebookSampleActivity", "Facebook session closed");
			}
		}
	};
 
	public void buttonsEnabled(boolean isEnabled) {
		postImageBtn.setEnabled(isEnabled);
		updateStatusBtn.setEnabled(isEnabled);
	}
 
	public void postImage() {
		if (checkPermissions()) {
			Bitmap img = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher);
			Request uploadRequest = Request.newUploadPhotoRequest(
					Session.getActiveSession(), img, new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							Toast.makeText(MainActivity.this,
									"Photo uploaded successfully",
									Toast.LENGTH_LONG).show();
						}
					});
			uploadRequest.executeAsync();
		} else {
			requestPermissions();
		}
	}
 
	public void postStatusMessage() {
		if (checkPermissions()) {
			Request request = Request.newStatusUpdateRequest(
					Session.getActiveSession(), message,
					new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							if (response.getError() == null)
								Toast.makeText(MainActivity.this,
										"Status updated successfully",
										Toast.LENGTH_LONG).show();
						}
					});
			request.executeAsync();
		} else {
			requestPermissions();
		}
	}
 
	public boolean checkPermissions() {
		Session s = Session.getActiveSession();
		if (s != null) {
			return s.getPermissions().contains("publish_actions");
		} else
			return false;
	}
 
	public void requestPermissions() {
		Session s = Session.getActiveSession();
		if (s != null)
			s.requestNewPublishPermissions(new Session.NewPermissionsRequest(
					this, PERMISSIONS));
	}
 
	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		buttonsEnabled(Session.getActiveSession().isOpened());
	}
 
	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}
 
	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}
 
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}
 
	@Override
	public void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		uiHelper.onSaveInstanceState(savedState);
	}
 
}