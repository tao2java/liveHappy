package com.wxgh.livehappy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wxgh.livehappy.R;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


/**
 * Created by Administrator on 2016/5/16.
 */
public class PhoneLoginVerificationCode extends Fragment {
    private View view;
    private EditText et_phone, et_verificationcode;//手机号，验证码
    private ImageView iv_get_sms_code;//获取短信验证码按钮
    private TextView tv_login;//登陆按钮
    private String phoneNum, SMSCode;//手机号，短信验证码


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.phoneloginverificationcode, container, false);
        initView();
        initSMS();

        return view;
    }
    public void getPassword(){

    }
    /**
     * 第三方登录
     *
     * @param tuk QQ登录信息
     */
   /*public void thirdPartyLogin(TencentUserToken tuk) {

        String url = ConstantManger.SERVER_IP + ConstantManger.THIRD_PARTY_LOGIN;//"?Access_token=1& Openid=1& ThirdPartyType=1&appid=1";
        RequestBody requestBody = null;
        if (tuk != null) {
            requestBody = new FormBody.Builder().add("appid", tencentAppID).add("Access_token", tuk.getAccess_token()).add("Openid", tuk.getOpenid()).add("ThirdPartyType", "0").build();
        }
        Request request = new Request.Builder().url(url).post(requestBody).build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                ReturnJson returnJson = new Gson().fromJson(json, ReturnJson.class);
                if (returnJson.getError() == 200 && returnJson.getUsers() != null) {//获取用户信息成功
                    StaticManger.user = returnJson.getUsers().get(0);
                    loginOk();
                }
            }
        });
    }*/
    /**
     * 短信验证的回掉
     */
    EventHandler eh = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {

            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
//                       验证失败走异常，成功返回---- {phone=15321763573, country=86}
                    handler.sendEmptyMessage(1);//将smssdk注册代码注销
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    if ((Boolean) data) {
                        //验证成功，此号码在本设备上验证过了，不会再发短信
                        handler.sendEmptyMessage(1);//将smssdk注册代码注销
                    }
                    Log.d("sms", "EVENT_GET_VERIFICATION_CODE--------------" + data + "");
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
//                        ArrayList<HashMap<String,Object>> phoneMap = (ArrayList<HashMap<String,Object>>) data;
                }
            } else {
                ((Throwable) data).printStackTrace();
            }
        }
    };

    /**
     * 初始化短信
     */
    private void initSMS() {
        SMSSDK.initSDK(getActivity(), "14d8a28bd8720", "5d108ef6045a50adeffb70afd9ec2273");
        SMSSDK.registerEventHandler(eh); //注册短信回调()
//        SMSSDK.getSupportedCountries();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        et_phone = (EditText) view.findViewById(R.id.et_phone);
        et_verificationcode = (EditText) view.findViewById(R.id.et_verificationcode);
        iv_get_sms_code = (ImageView) view.findViewById(R.id.iv_get_sms_code);
        tv_login = (TextView) view.findViewById(R.id.tv_login);

        iv_get_sms_code.setOnClickListener(click);
        tv_login.setOnClickListener(click);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_get_sms_code://获取短信验证码
                    if (getPhoneNum()) {
                        SMSSDK.getVerificationCode("86", phoneNum);
                    }
                    break;
                case R.id.tv_login://登陆
                    phoneNum = et_phone.getText().toString().trim();

                    if (phoneNum.equals("") || phoneNum.length() != 11) {
                        Toast.makeText(getContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SMSSDK.submitVerificationCode("86", phoneNum, SMSCode);
                    Log.d("sms", "tijiao");
                    break;
            }
        }
    };

    /**
     * 获取手机号，并检查是否正确
     *
     * @return
     */
    public boolean getPhoneNum() {
        phoneNum = et_phone.getText().toString().trim();
        if (phoneNum.equals("") || phoneNum.length() != 11) {
            Toast.makeText(getContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 获取验证码，并检查是否正确
     *
     * @return
     */
    public boolean getSMSCode() {
        SMSCode = et_verificationcode.getText().toString().trim();
        if (SMSCode.equals("")) {
            Toast.makeText(getContext(), "请输入短信验证码", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    // 验证通过之后，将smssdk注册代码注销
                    SMSSDK.unregisterEventHandler(eh);
                    break;
            }
            return false;
        }
    });

}
