package com.colearning.android.podcastpoll;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class PodcastPollFragment extends Fragment {

	protected static final String TAG = "PodcastPollFragment";

	private Button btnYes;
	private Button btnNo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_podcast_catcher, container, false);

		btnYes = (Button) view.findViewById(R.id.btnYes);
		btnYes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "Yes was clicked!!!");
				handleStartingLikePodcastResponseActivity(true);
			}
		});

		btnNo = (Button) view.findViewById(R.id.btnNo);
		btnNo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "No was clicked!!!");
				handleStartingLikePodcastResponseActivity(false);
			}
		});

		return view;
	}

	public void handleStartingLikePodcastResponseActivity(boolean doesLikePodcasts) {
		Intent intent = new Intent(getActivity(), LikePodcastResponseActivity.class);
		intent.putExtra(LikePodcastResponseFragment.DOES_LIKE_PODCASTS, doesLikePodcasts);
		startActivityForResult(intent, 0);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		boolean didChangeMind = data.getBooleanExtra(LikePodcastResponseFragment.CHANGED_MIND, false);

		int stringId = R.string.change_mind_no_response;
		if (didChangeMind) {
			stringId = R.string.change_mind_yes_response;
		}

		Toast.makeText(getActivity(), stringId, Toast.LENGTH_SHORT).show();
	}

}
