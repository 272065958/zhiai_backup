package com.cjx.zhiai.advisory;

import android.content.Intent;
import android.os.Bundle;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.ease.VideoCallActivity;
import com.cjx.zhiai.ease.VoiceCallActivity;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;

/**
 * Created by cjx on 2017-01-08.
 */
public class ChatActivity extends BaseActivity implements EaseChatFragment.OnChatMenuClick{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        setToolBar(true, null, intent.getStringExtra("title"));

        //new出EaseChatFragment或其子类的实例
        EaseChatFragment chatFragment = new EaseChatFragment();
        chatFragment.setOnChatMenuClickListener(this);
        //传入参数
        Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        args.putString(EaseConstant.EXTRA_USER_ID, intent.getStringExtra(EaseConstant.EXTRA_USER_ID));
        chatFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
    }

    /**
     * make a voice call
     */
    @Override
    public void startVoiceCall(String toChatUsername) {
        if (!EMClient.getInstance().isConnected()) {
            showToast(getString(com.hyphenate.easeui.R.string.not_connect_to_server));
        } else {
            startActivity(new Intent(this, VoiceCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // voiceCallBtn.setEnabled(false);
//            inputMenu.hideExtendMenuContainer();
        }
    }

    /**
     * make a video call
     */
    @Override
    public void startVideoCall(String toChatUsername) {
        if (!EMClient.getInstance().isConnected())
            showToast(getString(com.hyphenate.easeui.R.string.not_connect_to_server));
        else {
            startActivity(new Intent(this, VideoCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // videoCallBtn.setEnabled(false);
//            inputMenu.hideExtendMenuContainer();
        }
    }
}
