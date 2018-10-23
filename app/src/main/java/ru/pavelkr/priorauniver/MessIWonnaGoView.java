package ru.pavelkr.priorauniver;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by pavel on 20.04.2017.
 */

public class MessIWonnaGoView extends FrameLayout {
    public static final int OK_BUTTON = R.id.ok_button;
    public static final int REJECT_BUTTON = R.id.reject_button;
    public static final int HIDE_BUTTON = R.id.hide_button;

    public MessIWonnaGoView(Context context, String mes, View.OnClickListener listener) {
        super(context);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        final View rootView = inflate(context, R.layout.fragment_mess_iwonnago, null);
        addView(rootView);
        ((TextView) rootView.findViewById(R.id.messtext)).setText(mes);
        rootView.findViewById(OK_BUTTON).setOnClickListener(listener);
        rootView.findViewById(REJECT_BUTTON).setOnClickListener(listener);
        rootView.findViewById(HIDE_BUTTON).setOnClickListener(listener);
    }
}
