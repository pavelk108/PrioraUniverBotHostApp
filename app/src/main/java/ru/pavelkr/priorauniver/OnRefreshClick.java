package ru.pavelkr.priorauniver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by pavel on 19.04.2017.
 */

public class OnRefreshClick implements View.OnClickListener {
    interface OnUpdateListener {
        void onUpdate();
    }

    private OnUpdateListener listener = null;

    OnRefreshClick(OnUpdateListener listener) {
        this.listener = listener;

    }
    @Override
    public void onClick(View v) {
        final Context context = v.getContext();
        EasyHTTPGet g = new EasyHTTPGet();
        g.setOnCompleteListener(new EasyHTTPGet.OnCompleteListener() {
            @Override
            public void onComplete(String resp) {
                try {
                    JSONObject json = new JSONObject(resp);
                    JSONArray arr = json.getJSONArray("result");
                    if (arr.length() == 0) {
                        Toast.makeText(context, "nothing", Toast.LENGTH_LONG).show();
                        return;
                    }
                    int next_id = -1;
                    for (int i = 0; i < arr.length(); i++) {
                        json = arr.getJSONObject(i);


                        MyBaseHelper baseHelper = new MyBaseHelper(context);
                        ContentValues newValues = new ContentValues();
                        int update_id = json.getInt("update_id");
                        newValues.put(MyBaseHelper.COLUMN_ID, update_id);
                        newValues.put(MyBaseHelper.COLUMN_MESS, json.toString());

                        baseHelper.getWritableDatabase().insert(
                                MyBaseHelper.MYBASE_TABLE_NAME, null, newValues);


                        boolean needtosay = true;
                        if (json.has("message")) {
                            JSONObject mes = json.getJSONObject("message");
                            if (mes.has("text")) {
                                String text = mes.getString("text");
                                JSONObject chat = mes.getJSONObject("chat");
                                int chat_id = chat.getInt("id");
                                if (text.startsWith("/subscribe")) {
                                    SubscribersTableHelper helper = new SubscribersTableHelper(context);
                                    Cursor cursor = helper.getReadableDatabase().rawQuery("select 1 from " +
                                            SubscribersTableHelper.SUBSCRIBERS_TABLE_NAME + " where " +
                                            SubscribersTableHelper.COLUMN_CHATID + " = " + chat_id + " limit 1", null);
                                    if (cursor.getCount() > 0) {
                                        EasyHTTPGet.easy_send(chat_id, "Вы уже подписаны");
                                        needtosay = false;
                                    } else {
                                        ContentValues values = new ContentValues();
                                        values.put(SubscribersTableHelper.COLUMN_CHATID, chat_id);
                                        if (chat.has("first_name")) {
                                            values.put(SubscribersTableHelper.COLUMN_FNAME, chat.getString("first_name"));
                                        }
                                        if (chat.has("last_name")) {
                                            values.put(SubscribersTableHelper.COLUMN_LNAME, chat.getString("last_name"));
                                        }
                                        helper.getWritableDatabase().insert(SubscribersTableHelper.SUBSCRIBERS_TABLE_NAME, null, values);
                                        EasyHTTPGet.easy_send(chat_id, "Вы успешно подписаны на рассылку о предстоящих поездках!\n" +
                                                "Чтобы записаться на поездку после ее анонса отправьте /iwonnago\n" +
                                                "Если Вы вдруг захотите отказаться от поездки, на которую уже записалились, отправьте /iperedumal");
                                        needtosay = false;
                                    }
                                    helper.close();
                                } else if (text.startsWith("/unsubscribe")) {
                                    SubscribersTableHelper helper = new SubscribersTableHelper(context);
                                    Cursor cursor = helper.getReadableDatabase().rawQuery("select 1 from " +
                                            SubscribersTableHelper.SUBSCRIBERS_TABLE_NAME + " where " +
                                            SubscribersTableHelper.COLUMN_CHATID + " = " + chat_id + " limit 1", null);
                                    if (cursor.getCount() == 0) {
                                        EasyHTTPGet.easy_send(chat_id, "Вы и не были подписаны");
                                    } else {
                                        helper.getWritableDatabase().delete(SubscribersTableHelper.SUBSCRIBERS_TABLE_NAME,
                                                SubscribersTableHelper.COLUMN_CHATID + " = " + chat_id, null);
                                        EasyHTTPGet.easy_send(chat_id, "Вы отказались от рассылки");
                                    }
                                    needtosay = false;
                                } else if (text.startsWith("/iperedumal")) {
                                    boolean in_list = false;
                                    SubscribersTableHelper helper = new SubscribersTableHelper(context);
                                    {
                                        Cursor cursor = helper.getReadableDatabase().rawQuery("select 1 from " +
                                                SubscribersTableHelper.SUBSCRIBERS_TABLE_NAME + " where " +
                                                SubscribersTableHelper.COLUMN_CHATID + " = " + chat_id + " and " +
                                                SubscribersTableHelper.COLUMN_IS_PASSENGER + " = 1 limit 1", null);
                                        in_list = cursor.getCount() > 0;
                                        cursor.close();
                                    }
                                    if (in_list) {
                                        ContentValues values = new ContentValues();
                                        values.put(SubscribersTableHelper.COLUMN_IS_PASSENGER, 0);
                                        helper.getWritableDatabase().update(SubscribersTableHelper.SUBSCRIBERS_TABLE_NAME,
                                                values, SubscribersTableHelper.COLUMN_CHATID + " = " + chat_id, null);
                                        EasyHTTPGet.easy_send(chat_id, "Понял, Вы не едете");
                                        needtosay = false;
                                    } else {
                                        Cursor cur = baseHelper.getReadableDatabase().rawQuery(
                                                "select * from " + MyBaseHelper.MYBASE_TABLE_NAME +
                                                        " where " + MyBaseHelper.COLUMN_FLAG + " = 0 " +
                                                        " and " + MyBaseHelper.COLUMN_ID + " < " + update_id, null);
                                        if (cur.moveToFirst()) {
                                            do {
                                                try {
                                                    JSONObject obj = new JSONObject(cur.getString(cur.getColumnIndex(MyBaseHelper.COLUMN_MESS)));
                                                    obj = obj.getJSONObject("message");
                                                    if (obj.getString("text").startsWith("/iwonnago")) {
                                                        if (obj.getJSONObject("chat").getInt("id") == chat_id) {
                                                            EasyHTTPGet.easy_send(chat_id, "Понял, ваша заявка отменена");
                                                            MyBaseHelper.mark_row_read(context, cur.getInt(0));
                                                            needtosay = false;
                                                            break;
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } while (cur.moveToNext());
                                        }
                                        cur.close();
                                        if (needtosay) {
                                            EasyHTTPGet.easy_send(chat_id, "Вы и не собирались ехать");
                                            needtosay = false;
                                        }
                                    }
                                    helper.close();
                                } else if (text.startsWith("/iwonnago")) {
                                    SubscribersTableHelper helper = new SubscribersTableHelper(context);
                                    Cursor cursor = helper.getReadableDatabase().rawQuery("select 1 from " +
                                            SubscribersTableHelper.SUBSCRIBERS_TABLE_NAME + " where " +
                                            SubscribersTableHelper.COLUMN_CHATID + " = " + chat_id + " limit 1", null);
                                    if (cursor.getCount() == 0) {
                                        EasyHTTPGet.easy_send(chat_id, "Вы не подписаны на рассылку!\nПодпишитесь /subscribe чтобы получать анонсы предстоящих поездок");
                                        MyBaseHelper.mark_row_read(context, update_id);
                                        needtosay = false;
                                    } else {
                                        if (System.currentTimeMillis() - AnonsProvider.getTime(context) > 2_000_000) {
                                            EasyHTTPGet.easy_send(chat_id, "Я не планирую ехать в ближайшее время, дождитесь анонса");
                                            needtosay = false;
                                            MyBaseHelper.mark_row_read(context, update_id);
                                        } else {
                                            if (helper.getReadableDatabase().rawQuery("select 1 from " +
                                                    SubscribersTableHelper.SUBSCRIBERS_TABLE_NAME +
                                                    " where " + SubscribersTableHelper.COLUMN_CHATID + " = " + chat_id  +
                                                    " and " + SubscribersTableHelper.COLUMN_IS_PASSENGER  + " = 1", null)
                                                            .getCount() > 0) {
                                                EasyHTTPGet.easy_send(chat_id, "Вы уже в списке");
                                                MyBaseHelper.mark_row_read(context, update_id);
                                                needtosay = false;
                                            } else {
                                                Cursor cur = baseHelper.getReadableDatabase().rawQuery(
                                                        "select * from " + MyBaseHelper.MYBASE_TABLE_NAME +
                                                                " where " + MyBaseHelper.COLUMN_FLAG + " = 0 " +
                                                                " and " + MyBaseHelper.COLUMN_ID + " < " + update_id, null);
                                                if (cur.moveToFirst()) {
                                                    do {
                                                        try {
                                                            JSONObject obj = new JSONObject(cur.getString(cur.getColumnIndex(MyBaseHelper.COLUMN_MESS)));
                                                            obj = obj.getJSONObject("message");
                                                            if (obj.getString("text").startsWith("/iwonnago")) {
                                                                if (obj.getJSONObject("chat").getInt("id") == chat_id) {
                                                                    EasyHTTPGet.easy_send(chat_id, "Вы уже подали заявку");
                                                                    MyBaseHelper.mark_row_read(context, update_id);
                                                                    needtosay = false;
                                                                    break;
                                                                }
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    } while (cur.moveToNext());
                                                }
                                                cur.close();
                                            }
                                        }
                                    }
                                    cursor.close();
                                    helper.close();
                                }
                            }
                        }
                        baseHelper.close();

                        next_id = json.getInt("update_id") + 1;


                        if (needtosay && json.has("message")) {
                            int chat_id = json.getJSONObject("message").getJSONObject("chat").getInt("id");
                            EasyHTTPGet.easy_send(chat_id, "Сообщение получено");
                        }
                    }
                    if (next_id > 0) {
                        EasyHTTPGet f = new EasyHTTPGet();
                        f.execute(EasyHTTPGet.BASE_URL + "getUpdates?offset=" + next_id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (listener != null) {
                    listener.onUpdate();
                }
            }
        });
        g.execute(EasyHTTPGet.BASE_URL + "getUpdates");


    }
}
