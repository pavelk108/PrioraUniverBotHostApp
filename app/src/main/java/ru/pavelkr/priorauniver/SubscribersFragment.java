package ru.pavelkr.priorauniver;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by pavel on 20.04.2017.
 */

public class SubscribersFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_subscribers, container, false);
        final TextView textView = (TextView) rootView.findViewById(R.id.easy_text);

        rootView.findViewById(R.id.show_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SubscribersTableHelper helper = new SubscribersTableHelper(getContext());
                        Cursor cursor = helper.getReadableDatabase().
                                rawQuery("select * from " + SubscribersTableHelper.SUBSCRIBERS_TABLE_NAME, null);

                        StringBuilder sb = new StringBuilder();
                        if (cursor.moveToFirst()) {
                            do {
                                sb.append(cursor.getString(1));
                                sb.append(" ");
                                sb.append(cursor.getString(2));
                                sb.append(" ");
                                sb.append(cursor.getString(3));
                                sb.append("\n");
                            } while (cursor.moveToNext());
                        }
                        textView.setText(sb.toString());
                        cursor.close();
                        helper.close();
                    }
                }
        );
        return rootView;
    }
}
