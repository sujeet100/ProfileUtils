package com.mavedev.profileutils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

public class ProfileUtils extends Activity {
	
	private static final int PICK_FRIENDS_ACTIVITY = 1;
	private static final int SELECT_FRIENDS_ACTIVITY = 2;
	ProfilePictureView profilePictureView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_utils);
		profilePictureView = (ProfilePictureView) findViewById(R.id.user_profile_pic);
		Button button = (Button) findViewById(R.id.export_contacts_view);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onClickGetContactsList();
			}
		});
		
		ensureOpenSession();

	}

	protected void onClickGetContactsListUsingFriendFragment() {
		if (ensureOpenSession()) {
            Intent intent = new Intent(this, PickFriendsActivity.class);
            // Note: The following line is optional, as multi-select behavior is the default for
            // FriendPickerFragment. It is here to demonstrate how parameters could be passed to the
            // friend picker if single-select functionality was desired, or if a different user ID was
            // desired (for instance, to see friends of a friend).
            PickFriendsActivity.populateParameters(intent, null, true, true);
            startActivityForResult(intent, SELECT_FRIENDS_ACTIVITY);
        }
	}
	
	protected void onClickGetContactsList() {
		if (ensureOpenSession()) {
            Intent intent = new Intent(this, SelectFriendsActivity.class);
            startActivityForResult(intent, SELECT_FRIENDS_ACTIVITY);
        }
	}

	private boolean ensureOpenSession() {

		if (Session.getActiveSession() == null
				|| Session.getActiveSession().isClosed()) {
			Session.openActiveSession(this, true, new Session.StatusCallback() {

				// callback when session changes state
				@SuppressWarnings("deprecation")
				@Override
				public void call(Session session, SessionState state,
						Exception exception) {
					onSessionStateChanged(session, state, exception);
				}
			});
			return false;
		}else{
			return true;
		}

	}

	protected void onSessionStateChanged(Session session, SessionState state,
			Exception exception) {
		if (session.isOpened()) {
			getUserName(session, state, exception);
		}
	}

	private void getUserName(Session session, SessionState state,
			Exception exception) {

		// make request to the /me API
		Request.newMeRequest(session, new Request.GraphUserCallback() {

			// callback after Graph API response with user object
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (user != null) {
					TextView welcome = (TextView) findViewById(R.id.username);
					welcome.setText("Welcome " + user.getName() + "!");
					profilePictureView.setProfileId(user.getId());
				}
			}
		}).executeAsync();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile_utils, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

}
