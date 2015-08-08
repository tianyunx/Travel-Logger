package com.example.android.travellogger;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.travellogger.provider.JournalAdapter;
import com.example.android.travellogger.provider.TravelContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class JournalsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private JournalAdapter mJournalsAdapter;

    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;

    private static final String[] DB_ROWS = {
            TravelContract.JournalEntry.COLUMN_ID,
            TravelContract.JournalEntry.COLUMN_NAME
    };

    public static final int COL_JOURNAL_ID = 0;
    public static final int COL_JOURNAL_NAME = 1;

    public JournalsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_main, container, false);
//        String[] data = {
//                "Journal 1 Title",
//                "Journal 2 Title",
//                "Journal 3 Title",
//                "Journal 4 Title",
//                "Journal 5 Title",
//                "Journal 6 Title",
//                "Journal 7 Title"
//        };
//        List<String> titlesList = new ArrayList<String>(Arrays.asList(data));


        mJournalsAdapter = new JournalAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.journals_fragment_main, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview_journal);
        mListView.setAdapter(mJournalsAdapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                mPosition = position;
                if(cursor != null) {

                    String string = TravelContract.EntryEntry.CONTENT_URI.buildUpon()
                            .appendPath(Integer.toString(cursor.getInt(COL_JOURNAL_ID)))
                            .build().toString();

                    //on click, we need to replace the fragment from main activity with
                    //the list of posts.
                    if(getActivity().findViewById(R.id.detail_container) == null) {
                        Intent intent = new Intent(getActivity(), DisplayPostsActivity.class);
                        intent.putExtra("uri", string);
                        startActivity(intent);
                    } else {

                        PostsFragment fragment = new PostsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("uri", string);
                        fragment.setArguments(bundle);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.fragment, new PostsFragment())
                                .commit();
                    }
                }
            }
        });

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle b)
    {
        String sortOrder = TravelContract.JournalEntry.COLUMN_ID + " DESC";
        return new CursorLoader(getActivity(),
                TravelContract.JournalEntry.CONTENT_URI,
                DB_ROWS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        mJournalsAdapter.swapCursor(data);
        if(mPosition != ListView.INVALID_POSITION)
        {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mJournalsAdapter.swapCursor(null);
    }
}
