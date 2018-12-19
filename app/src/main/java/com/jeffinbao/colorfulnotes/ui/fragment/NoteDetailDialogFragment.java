package com.jeffinbao.colorfulnotes.ui.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.jeffinbao.colorfulnotes.R;
import com.jeffinbao.colorfulnotes.constants.NConstants;
import com.jeffinbao.colorfulnotes.model.Note;
import com.jeffinbao.colorfulnotes.utils.OSUtil;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;

/**
 * Author: baojianfeng
 * Date: 2015-11-11
 */
public class NoteDetailDialogFragment extends BaseDialogFragment implements View.OnClickListener, AMapLocationListener {
    private static final String TAG = "NoteDetailFragment";

    private static final String NOTE_LAST_UPDATE_TIME = "note_last_update_time";
    private static final String NOTE_CREATE_TIME = "note_create_time";
    private static final String NOTE_CREATE_LOCATION = "note_create_location";

    private static final int UPDATE_LOCATION_TEXT_VIEW = 0x001;

    private TextView createLocationTextView;
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationClientOption;
    private String createLocation;
    private LocationReturnSuccessListener listener;

    private NoteDetailHandler handler;

    private static class NoteDetailHandler extends Handler {
        private WeakReference<NoteDetailDialogFragment> weakReference;

        public NoteDetailHandler(NoteDetailDialogFragment fragment) {
            weakReference = new WeakReference<NoteDetailDialogFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {

            NoteDetailDialogFragment fragment = weakReference.get();
            if (null == fragment) {
                return;
            }

            switch (msg.what) {
                case UPDATE_LOCATION_TEXT_VIEW: {
                    fragment.createLocationTextView.setText(fragment.createLocation);
                    fragment.createLocationTextView.setTextColor(fragment.getResources().getColor(R.color.black_transparency_87));
                    break;
                }
            }
        }
    }

    public static NoteDetailDialogFragment getInstance(Note note) {
        NoteDetailDialogFragment fragment = new NoteDetailDialogFragment();

        Bundle args = new Bundle();
        args.putString(NOTE_LAST_UPDATE_TIME, note.getLastUpdateTime());
        args.putString(NOTE_CREATE_TIME, note.getCreateTime());
        args.putString(NOTE_CREATE_LOCATION, note.getCreateLocation());
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new NoteDetailHandler(this);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.NoteDialogFragmentStyle);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("NoteActivity");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("NoteActivity");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String lastUpdateTime = getArguments().getString(NOTE_LAST_UPDATE_TIME);
        String createTime = getArguments().getString(NOTE_CREATE_TIME);
        String createLocation = getArguments().getString(NOTE_CREATE_LOCATION);

        TextView lastUpdateTimeTextView = (TextView) view.findViewById(R.id.fragment_note_detail_last_update_time);
        TextView createTimeTextView = (TextView) view.findViewById(R.id.fragment_note_detail_create_time);
        createLocationTextView = (TextView) view.findViewById(R.id.fragment_note_detail_create_location);

        createLocationTextView.setOnClickListener(this);

        lastUpdateTimeTextView.setText(lastUpdateTime);
        createTimeTextView.setText(createTime);

        if (!TextUtils.isEmpty(createLocation)) {
            createLocationTextView.setEnabled(false);
            createLocationTextView.setText(createLocation);
        } else {
            createLocationTextView.setEnabled(true);
            createLocationTextView.setText(R.string.note_set_create_location);
            createLocationTextView.setTextColor(getResources().getColor(R.color.material_deep_teal_500));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_note_detail_create_location: {
                startGetLocationTask();
                break;
            }
        }
    }

    private void startGetLocationTask() {
//        if (!OSUtil.isNetWorkAvailable(getActivity())) {
//            Toast.makeText(getActivity().getApplicationContext(), R.string.turn_on_network, Toast.LENGTH_SHORT).show();
//            return;
//        }

        if (!OSUtil.isNetworkAvailable()) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.turn_on_network, Toast.LENGTH_SHORT).show();
            return;
        }

        locationClient = new AMapLocationClient(getActivity().getApplicationContext());
        locationClient.setLocationListener(this);

        locationClientOption = new AMapLocationClientOption();
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationClientOption.setInterval(2000);

        locationClient.setLocationOption(locationClientOption);
        locationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (null != createLocation && null != locationClient) {
            locationClient.stopLocation();
            return;
        }

        if (null != aMapLocation) {
            if (aMapLocation.getErrorCode() == 0) {
                createLocation = aMapLocation.getCountry() + NConstants.DOT +
                        aMapLocation.getProvince() + NConstants.DOT +
                        aMapLocation.getCity() + NConstants.DOT +
                        aMapLocation.getDistrict();

                handler.sendEmptyMessage(UPDATE_LOCATION_TEXT_VIEW);

                if (null != listener) {
                    listener.onLocationSuccessListener(createLocation);
                }



                Log.d(TAG, "Location info: " + createLocation);
            }
        } else {
            Log.d(TAG, "location errcode: " + aMapLocation.getErrorCode() + ", errInfo: " + aMapLocation.getErrorInfo());
        }
    }

    public void setLocationReturnSuccessListener(LocationReturnSuccessListener listener) {
        this.listener = listener;
    }

    public interface LocationReturnSuccessListener {
        void onLocationSuccessListener(String location);
    }
}
