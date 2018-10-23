package ru.pavelkr.priorauniver;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pavel on 19.04.2017.
 */

public class MainFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        rootView.findViewById(R.id.refresh_button).setOnClickListener(new OnRefreshClick(
                new OnRefreshClick.OnUpdateListener() {
                    @Override
                    public void onUpdate() {
                        redraw();
                    }
                }
        ));

        final TextView text_to_send = (TextView) rootView.findViewById(R.id.text_to_send);
        rootView.findViewById(R.id.send_to_subs_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SubscribersTableHelper helper = new SubscribersTableHelper(getContext());
                        Cursor cursor = helper.getReadableDatabase().
                                rawQuery("select " + SubscribersTableHelper.COLUMN_CHATID +
                                            " from " + SubscribersTableHelper.SUBSCRIBERS_TABLE_NAME +
                                        " group by " + SubscribersTableHelper.COLUMN_CHATID, null);

                        if (cursor.moveToFirst()) {
                            do {
                                try {
                                    EasyHTTPGet.easy_send(cursor.getInt(0), text_to_send.getText().toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } while (cursor.moveToNext());
                        }

                        cursor.close();
                        helper.close();
                    }
                }
        );

        final TextView text_to_pass_send = (TextView) rootView.findViewById(R.id.text_to_send_passengers);
        rootView.findViewById(R.id.send_to_passengers_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SubscribersTableHelper helper = new SubscribersTableHelper(getContext());
                        Cursor cursor = helper.getReadableDatabase().
                                rawQuery("select " + SubscribersTableHelper.COLUMN_CHATID +
                                        " from " + SubscribersTableHelper.SUBSCRIBERS_TABLE_NAME +
                                        " where " + SubscribersTableHelper.COLUMN_IS_PASSENGER + " = 1 " +
                                        " group by " + SubscribersTableHelper.COLUMN_CHATID, null);

                        if (cursor.moveToFirst()) {
                            do {
                                try {
                                    EasyHTTPGet.easy_send(cursor.getInt(0), text_to_pass_send.getText().toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } while (cursor.moveToNext());
                        }

                        cursor.close();
                        helper.close();
                    }
                }
        );

        final TextView text_anons = (TextView) rootView.findViewById(R.id.anons_text);
        rootView.findViewById(R.id.anons_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SubscribersTableHelper helper = new SubscribersTableHelper(getContext());
                        Cursor cursor = helper.getReadableDatabase().
                                rawQuery("select " + SubscribersTableHelper.COLUMN_CHATID +
                                        " from " + SubscribersTableHelper.SUBSCRIBERS_TABLE_NAME +
                                        " group by " + SubscribersTableHelper.COLUMN_CHATID, null);

                        String text = "Открыт прием заявок!\n" +
                                text_anons.getText() +
                                "\nНапишите /iwonnago, чтобы поехать";
                        AnonsProvider.startAnons(getContext(), text_anons.getText().toString());
                        if (cursor.moveToFirst()) {
                            do {
                                try {
                                    EasyHTTPGet.easy_send(cursor.getInt(0), text);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } while (cursor.moveToNext());
                        }
                        cursor.close();
                        helper.close();
                        redraw();
                    }
                }
        );

        rootView.findViewById(R.id.refresh_messcontainer_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       redraw();
                    }
                }
        );
        return rootView;

    }

    @Override
    public void onResume(){
        super.onResume();
        redraw();
    };

    public void redraw() {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM   hh:mm:ss");

        LinearLayout container = (LinearLayout) getView().findViewById(R.id.messagescontainer);
        container.removeAllViews();

        {
            TextView current_anons_mess = (TextView) getView().findViewById(R.id.current_anons_mess_text);
            current_anons_mess.setText(AnonsProvider.getAnons(getContext()));
            TextView current_anons_time = (TextView) getView().findViewById(R.id.current_anons_time_text);
            long time = AnonsProvider.getTime(getContext());
            String t;
            if (time == 0) {
                t = "Сброшено";
            } else {
                t = dateFormat.format(new Date(time));
            }
            current_anons_time.setText(t);

            getView().findViewById(R.id.reset_anons_button).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SubscribersTableHelper helper = new SubscribersTableHelper(getContext());
                            Cursor cursor = helper.getReadableDatabase().
                                    rawQuery("select " + SubscribersTableHelper.COLUMN_CHATID +
                                            " from " + SubscribersTableHelper.SUBSCRIBERS_TABLE_NAME +
                                            " group by " + SubscribersTableHelper.COLUMN_CHATID, null);
                            if (cursor.moveToFirst()) {
                                do {
                                    try {
                                        EasyHTTPGet.easy_send(cursor.getInt(0), "Поездка отменяется, никуда не едем");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } while (cursor.moveToNext());
                            }

                            cursor.close();
                            ContentValues values = new ContentValues();
                            values.put(SubscribersTableHelper.COLUMN_IS_PASSENGER, 0);
                            helper.getWritableDatabase().update(SubscribersTableHelper.SUBSCRIBERS_TABLE_NAME, values,
                                    SubscribersTableHelper.COLUMN_IS_PASSENGER + " = 1", null);

                            helper.close();

                            AnonsProvider.resetAnons(getContext());
                            redraw();
                        }
                    }
            );

            getView().findViewById(R.id.go_anons_button).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SubscribersTableHelper helper = new SubscribersTableHelper(getContext());
                            Cursor cursor = helper.getReadableDatabase().
                                    rawQuery("select " + SubscribersTableHelper.COLUMN_CHATID +
                                            " from " + SubscribersTableHelper.SUBSCRIBERS_TABLE_NAME +
                                            " where " + SubscribersTableHelper.COLUMN_IS_PASSENGER  + " = 0 " +
                                            " group by " + SubscribersTableHelper.COLUMN_CHATID, null);

                            if (cursor.moveToFirst()) {
                                do {
                                    try {
                                        EasyHTTPGet.easy_send(cursor.getInt(0), "Едем, заявки больше не принимаются");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } while (cursor.moveToNext());
                            }

                            cursor.close();
                            ContentValues values = new ContentValues();
                            values.put(SubscribersTableHelper.COLUMN_IS_PASSENGER, 0);
                            helper.getWritableDatabase().update(SubscribersTableHelper.SUBSCRIBERS_TABLE_NAME, values,
                                    SubscribersTableHelper.COLUMN_IS_PASSENGER + " = 1", null);

                            helper.close();

                            AnonsProvider.resetAnons(getContext());
                            redraw();
                        }
                    }
            );
        }



        {
            SubscribersTableHelper helper = new SubscribersTableHelper(getContext());
            Cursor cursor = helper.getReadableDatabase().rawQuery("select * from " +
                    SubscribersTableHelper.SUBSCRIBERS_TABLE_NAME + " where " +
                    SubscribersTableHelper.COLUMN_IS_PASSENGER + " = 1", null);
            if (cursor.moveToFirst()) {
                do {
                    final int user_id = cursor.getInt(cursor.getColumnIndex(SubscribersTableHelper.COLUMN_CHATID));
                    String name = cursor.getString(cursor.getColumnIndex(SubscribersTableHelper.COLUMN_FNAME)) +
                            cursor.getString(cursor.getColumnIndex(SubscribersTableHelper.COLUMN_LNAME));
                    View v = new CurrentPassengerView(getContext(), name + "   едет ",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SubscribersTableHelper helper = new SubscribersTableHelper(getContext());
                                    ContentValues values = new ContentValues();
                                    values.put(SubscribersTableHelper.COLUMN_IS_PASSENGER, 0);
                                    helper.getWritableDatabase().update(SubscribersTableHelper.SUBSCRIBERS_TABLE_NAME,
                                            values, SubscribersTableHelper.COLUMN_CHATID + " = " + user_id, null);
                                    helper.close();
                                    EasyHTTPGet.easy_send(user_id, "Извините, я передумал, Вы не едете");
                                    redraw();
                                }
                            });
                    container.addView(v);
                } while (cursor.moveToNext());
            }
            cursor.close();
            helper.close();
        }
        {
            MyBaseHelper helper = new MyBaseHelper(getContext());
            Cursor cursor = helper.getReadableDatabase().query(MyBaseHelper.MYBASE_TABLE_NAME,
                    null, MyBaseHelper.COLUMN_FLAG + " = 0", null, null, null, null);
            try {
                if (cursor.moveToFirst()) {
                    do {
                        final int row_id = cursor.getInt(0);
                        try {
                            JSONObject json = new JSONObject(cursor.getString(cursor.getColumnIndex(MyBaseHelper.COLUMN_MESS)));
                            json = json.getJSONObject("message");
                            if (!json.has("text")) {
                                MyBaseHelper.mark_row_read(getContext(), row_id);
                                continue;
                            }

                            String mes = json.getString("text");
                            long date_int = json.getInt("date");
                            String date_string = dateFormat.format(new Date(date_int * 1000));
                            json = json.getJSONObject("from");
                            String user = json.getString("first_name") + " " + (json.has("last_name") ? json.getString("last_name") : "");
                            final int user_id = json.getInt("id");
                            View v;
                            if (mes.startsWith("/iwonnago")) {
                                v = new MessIWonnaGoView(getContext(), date_string + "\n" + user + " хочет поехать",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                switch (v.getId()) {
                                                    case MessIWonnaGoView.OK_BUTTON:
                                                        SubscribersTableHelper helper = new SubscribersTableHelper(getContext());
                                                        ContentValues values = new ContentValues();
                                                        values.put(SubscribersTableHelper.COLUMN_IS_PASSENGER, 1);
                                                        helper.getWritableDatabase().update(SubscribersTableHelper.SUBSCRIBERS_TABLE_NAME,
                                                                values, SubscribersTableHelper.COLUMN_CHATID + " = " + user_id, null);
                                                        helper.close();
                                                        EasyHTTPGet.easy_send(user_id, "Поздравляю! Вы едете!");
                                                        MyBaseHelper.mark_row_read(getContext(), row_id);
                                                        redraw();
                                                        break;
                                                    case MessIWonnaGoView.REJECT_BUTTON:
                                                        EasyHTTPGet.easy_send(user_id, "Извините, вы не едете(");
                                                        MyBaseHelper.mark_row_read(getContext(), row_id);
                                                        redraw();
                                                        break;
                                                    case MessIWonnaGoView.HIDE_BUTTON:
                                                        MyBaseHelper.mark_row_read(getContext(), row_id);
                                                        redraw();
                                                        break;
                                                    default:
                                                }
                                            }
                                        });
                            } else {
                                v = new SimpleMessView(getContext(), date_string + "\n" + user + ": " + mes,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                MyBaseHelper.mark_row_read(getContext(), row_id);
                                                redraw();
                                                Log.d("log_log_log", "onClick: ");
                                            }
                                        });
                            }
                            container.addView(v);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            MyBaseHelper.mark_row_read(getContext(), row_id);
                        }
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
                helper.close();
            }
        }
    }
}

