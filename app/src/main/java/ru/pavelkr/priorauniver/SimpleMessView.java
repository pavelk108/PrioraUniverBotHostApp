package ru.pavelkr.priorauniver;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by pavel on 20.04.2017.
 */

public class SimpleMessView extends FrameLayout {

    public SimpleMessView(Context context, String mes, View.OnClickListener listener) {
        super(context);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        final View rootView = inflate(context, R.layout.fragment_simple_mess, null);
        addView(rootView);
        ((TextView) rootView.findViewById(R.id.messtext)).setText(mes);
        rootView.findViewById(R.id.hidebutton).setOnClickListener(listener);
    }
}
