package com.example.reico_000.prescriptionreadernfc;

/**
 * Created by Yosua Susanto on 23/9/2015.
 */

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyController {

    public static final String TAG = VolleyController.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static VolleyController mInstance;

    private VolleyController(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);

    }

//    public static synchronized VolleyController getInstance() {
//        if (mInstance == null) {
//            mInstance = new VolleyController();
//        }
//        return mInstance;
//    }

    public static VolleyController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyController(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
