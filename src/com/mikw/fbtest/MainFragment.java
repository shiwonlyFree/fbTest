package com.mikw.fbtest;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;

import eu.masconsult.blurview.library.FrameLayoutWithBluredBackground;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * Implements main fragment life cycle.
 * 
 * @author mikw
 * 
 */
public class MainFragment extends Fragment {

	private static final String TAG = MainFragment.class.getName(); // "MainFragment";

	private UiLifecycleHelper uiHelper;
	private ProfilePictureView profilePictureView;

	private FrameLayoutWithBluredBackground pictureBlur;
	private SeekBar blurSeekBar;

	private static final int SEEK_BAR_START = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main, container, false);

		setView(view);

		setBlur();

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
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
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	/**
	 * Initialize and set components of the view.
	 * 
	 * @param view
	 */
	private void setView(View view) {
		profilePictureView = (ProfilePictureView) view.findViewById(R.id.profile_pic);
		profilePictureView.setCropped(true);

		LoginButton authButton = (LoginButton) view.findViewById(R.id.login);
		authButton.setFragment(this);
		pictureBlur = (FrameLayoutWithBluredBackground) view.findViewById(R.id.blured_layout);
		blurSeekBar = (SeekBar) view.findViewById(R.id.radius_slider);
	}

	/**
	 * Initialize blur seek bar and add the listener.
	 */
	private void setBlur() {
		blurSeekBar.setProgress((int) pictureBlur.getBlurRadius());
		blurSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (progress > SEEK_BAR_START) {
					pictureBlur.setBlurRadius(progress);
				}
			}
		});
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (session != null && session.isOpened()) {
			Log.i(TAG, "Logged in...");
			makeMeRequest(session);
		} else if (session != null && state.isClosed()) {
			Log.i(TAG, "Logged out...");
			profilePictureView.setProfileId(null);
			blurSeekBar.setProgress(SEEK_BAR_START);
		}
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private void makeMeRequest(final Session session) {
		Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (session == Session.getActiveSession()) {
					if (user != null) {
						profilePictureView.setProfileId(user.getId());
						blurSeekBar.setProgress(SEEK_BAR_START);
					}
				}
				if (response.getError() != null) {
					// Handle errors
				}
			}
		});
		request.executeAsync();
	}
}
