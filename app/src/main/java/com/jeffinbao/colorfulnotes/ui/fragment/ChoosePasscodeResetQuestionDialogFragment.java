package com.jeffinbao.colorfulnotes.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeffinbao.colorfulnotes.R;
import com.jeffinbao.colorfulnotes.ui.adapter.BaseListAdapter;
import com.jeffinbao.colorfulnotes.ui.adapter.ResetQuestionListAdapter;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * Author: baojianfeng
 * Date: 2016-02-08
 */
public class ChoosePasscodeResetQuestionDialogFragment extends BaseDialogFragment implements BaseListAdapter.OnItemClickListener {
    private static final String PASSCODE_RESET_QUESTION = "passcode_reset_question";
    private static final String CURRENT_RESET_QUESTION = "current_reset_question";

    private ResetQuestionChangedListener listener;

    public static ChoosePasscodeResetQuestionDialogFragment getInstance(ArrayList<String> questionNames, String currentQuestion) {

        ChoosePasscodeResetQuestionDialogFragment fragment = new ChoosePasscodeResetQuestionDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(PASSCODE_RESET_QUESTION, questionNames);
        args.putString(CURRENT_RESET_QUESTION, currentQuestion);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.NoteDialogFragmentStyle);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("PasscodeResetActivity");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("PasscodeResetActivity");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passcode_reset_questions, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> questionList = getArguments().getStringArrayList(PASSCODE_RESET_QUESTION);
        String currentQuestion = getArguments().getString(CURRENT_RESET_QUESTION);
        ResetQuestionListAdapter adapter = new ResetQuestionListAdapter(getActivity(), questionList, currentQuestion);
        adapter.setItemClickListener(this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_passcode_reset_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels * 50 / 100;
        recyclerView.getLayoutParams().height = height;

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position, String name) {
        listener.onResetQuestionChanged(name, position);
        dismiss();
    }

    public void setResetQuestionChangedListener(ResetQuestionChangedListener listener) {
        this.listener = listener;
    }

    public interface ResetQuestionChangedListener {
        void onResetQuestionChanged(String name, int position);
    }
}
