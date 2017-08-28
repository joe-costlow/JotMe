package com.josephcostlow.jotme;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Joseph Costlow on 27-Aug-17.
 */

public class WidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
