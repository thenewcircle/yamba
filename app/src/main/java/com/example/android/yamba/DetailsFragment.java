package com.example.android.yamba;


import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailsFragment extends Fragment {
	private TextView textUser, textMessage, textCreatedAt;

    //Best practice is to use a factory and feed parameters into arguments
    public static DetailsFragment newInstance(long statusId) {
        DetailsFragment fragment = new DetailsFragment();

        Bundle args = new Bundle();
        args.putLong(StatusContract.Column.ID, statusId);
        fragment.setArguments(args);

        return fragment;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_item, container, false);

		textUser = (TextView) view.findViewById(R.id.list_item_text_user);
		textMessage = (TextView) view.findViewById(R.id.list_item_text_message);
		textCreatedAt = (TextView) view
				.findViewById(R.id.list_item_text_created_at);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getArguments() != null) {
            long id = getArguments().getLong(StatusContract.Column.ID);
            updateView(id);
        }
	}
	
	public void updateView(long id) {
		if (id == -1) {
			textUser.setText("");
			textMessage.setText("");
			textCreatedAt.setText("");
			return;
		}

		Uri uri = ContentUris.withAppendedId(StatusContract.CONTENT_URI, id);
		
		Cursor cursor = getActivity().getContentResolver().query(uri, null,
				null, null, null);
		if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }
		
		String user = cursor.getString(cursor
				.getColumnIndex(StatusContract.Column.USER));
		String message = cursor.getString(cursor
				.getColumnIndex(StatusContract.Column.MESSAGE));
		long createdAt = cursor.getLong(cursor
				.getColumnIndex(StatusContract.Column.CREATED_AT));
		
		textUser.setText(user);
		textMessage.setText(message);
		textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(createdAt));

        cursor.close();
	}
}
