package tw.com.defood.beacon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class BeaconParseQueryAdapter extends ParseQueryAdapter<BeaconInfo> {
    private final ListClickHandler mListClickHandler;
    private TextView mContent;
    private ImageView mIcon;

    public static interface ListClickHandler {
        public void onDetailClick(BeaconInfo beaconInfo);
    }


    public BeaconParseQueryAdapter(Context context, ListClickHandler ListClickHandler) {
        super(context, getQueryFactory(context));
        mListClickHandler = ListClickHandler;

    }

    private static ParseQueryAdapter.QueryFactory<BeaconInfo> getQueryFactory(
            final Context context) {
        ParseQueryAdapter.QueryFactory<BeaconInfo> factory = new ParseQueryAdapter.QueryFactory<BeaconInfo>() {
            public ParseQuery<BeaconInfo> create() {
                ParseQuery<BeaconInfo> query = BeaconInfo.getQuery();
                query.fromLocalDatastore();
                return query;
            }
        };
        return factory;
    }

    @Override
    public View getItemView(final BeaconInfo beaconInfo, View view,
                            ViewGroup parent) {
        View rootView;

        if (view == null) {
            LayoutInflater mInflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rootView = mInflater.inflate(R.layout.item_content, parent,
                    false);
        } else {
            rootView = view;
        }

        mIcon = (ImageView) rootView.findViewById(R.id.icon);
        if (beaconInfo.getType().equals("video")) {
            mIcon.setImageResource(R.drawable.youtube);
        } else {
            mIcon.setImageResource(R.drawable.chrome);
        }


        mContent = (TextView) rootView.findViewById(R.id.content);
        mContent.setText(beaconInfo.getName() + "\n" + beaconInfo.getTitle());
        mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListClickHandler.onDetailClick(beaconInfo);
            }
        });

        return rootView;
    }

}