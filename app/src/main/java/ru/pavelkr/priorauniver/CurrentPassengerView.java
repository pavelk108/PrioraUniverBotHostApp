package ru.pavelkr.priorauniver;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by pavel on 20.04.2017.
 */

public class CurrentPassengerView extends FrameLayout {
    public CurrentPassengerView(Context context, String mes, View.OnClickListener listener) {
        super(context);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        final View rootView = inflate(context, R.layout.fragment_current_passenger, null);
        addView(rootView);
        ((TextView) rootView.findViewById(R.id.info_text)).setText(mes);
        rootView.findViewById(R.id.kick_button).setOnClickListener(listener);
    }
}
