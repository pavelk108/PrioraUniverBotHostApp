package ru.pavelkr.priorauniver;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by pavel on 19.04.2017.
 */

public class LogFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_log, container, false);
        final TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format, 2));

        rootView.findViewById(R.id.getlogbutton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringBuilder sb = new StringBuilder();

                        MyBaseHelper baseHelper = new MyBaseHelper(v.getContext());
                        Cursor cursor = baseHelper.getReadableDatabase().
                                rawQuery("select * from " + MyBaseHelper.MYBASE_TABLE_NAME + " order by 1 desc limit 5", null);

                        cursor.moveToFirst();
                        do {
                            sb.append(cursor.getString(1));
                            sb.append("\n");
                            sb.append("\n");
                        } while (cursor.moveToNext());

                        textView.setText(sb.toString());
                        cursor.close();
                        baseHelper.close();
                    }
                }
        );

        return rootView;
    }
}
