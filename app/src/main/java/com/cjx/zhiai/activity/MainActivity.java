package com.cjx.zhiai.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.fragment.MainDoctorFragment;
import com.cjx.zhiai.fragment.MainPeopleFragment;
import com.cjx.zhiai.fragment.MyDoctorFragment;
import com.cjx.zhiai.fragment.MyPeopleFragment;
import com.cjx.zhiai.fragment.ScanFragment;
import com.cjx.zhiai.util.LocationUtil;
import com.cjx.zhiai.util.Tools;

import java.io.File;

/**
 * Created by cjx on 2016/6/28.
 * 程序主界面
 */
public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    final int REQUEST_LOCATION_PERMISSION = 10;
    MainPeopleFragment mainPeopleFragment;
    MainDoctorFragment mainDoctorFragment;
    ScanFragment scanFragment;
    MyPeopleFragment myPeopleFragment;
    MyDoctorFragment myDoctorFragment;
    Fragment[] fragments;
    TextView[] itemTexts;
    ImageView[] itemIcons;

    ViewPager viewPager;
    MainPagerAdapter adapter;

    int prevIndex; // 指定当前tab选中的位置

    MenuItem recordItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.remove("android:support:fragments");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolBar(false, null, -1);
        registerRefreshReceiver(new IntentFilter(MyApplication.ACTION_USER_INFO_UPDATE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewPager == null) {
            init();
            if(checkPermission()){
                LocationUtil.getInstance().startLocation(getApplicationContext()); // 获取定位
            }
            MyApplication.getInstance().initHuanXin(); // 初始化环信
        }
    }

    @Override
    public void onBackPressed() {
        if (prevIndex != 0) {
            setSelection(0);
        } else {
            deleteTmepDir();
            moveTaskToBack(true);
        }
    }

    // 初始化首页各个对象
    private void init() {
        final int page_count = 3;
        itemTexts = new TextView[page_count];
        itemTexts[0] = (TextView) findViewById(R.id.main_item_main_text);
        itemTexts[1] = (TextView) findViewById(R.id.main_item_scan_text);
        itemTexts[2] = (TextView) findViewById(R.id.main_item_my_text);

        itemIcons = new ImageView[page_count];
        itemIcons[0] = (ImageView) findViewById(R.id.main_item_main_icon);
        itemIcons[1] = (ImageView) findViewById(R.id.main_item_scan_icon);
        itemIcons[2] = (ImageView) findViewById(R.id.main_item_my_icon);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        fragments = new Fragment[page_count];
        scanFragment = new ScanFragment();

        fragments[1] = scanFragment;
        MyApplication app = MyApplication.getInstance();
        if (app.userType.equals(app.USER_TYPE_DOCTOR)) {
            mainDoctorFragment = new MainDoctorFragment();
            fragments[0] = mainDoctorFragment;
            myDoctorFragment = new MyDoctorFragment();
            fragments[2] = myDoctorFragment;

            itemTexts[0].setText(R.string.main_item_work);
            itemTexts[1].setText(R.string.main_item_say);

            itemIcons[0].setImageResource(R.drawable.main_desk);
            itemIcons[1].setImageResource(R.drawable.main_talk);
            itemIcons[2].setImageResource(R.drawable.main_doctor);
        } else {
            mainPeopleFragment = new MainPeopleFragment();
            myPeopleFragment = new MyPeopleFragment();
            fragments[0] = mainPeopleFragment;
            fragments[2] = myPeopleFragment;
        }

        adapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);

        setCurrentTab(0);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 主界面tab的点击回调
     *
     * @param v 点中的view
     */
    public void tabClick(View v) {
        if (itemTexts == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.main_item_main:
                setSelection(0);
                break;
            case R.id.main_item_scan:
                setSelection(1);
                break;
            case R.id.main_item_my:
                setSelection(2);
                break;
        }
    }

    /**
     * 设置当前选中的tab
     *
     * @param position 选中位置
     */
    private void setCurrentTab(int position) {
        itemIcons[prevIndex].setSelected(false);
        itemTexts[prevIndex].setTextColor(ContextCompat.getColor(this, R.color.text_title_color));
        itemIcons[position].setSelected(true);
        itemTexts[position].setTextColor(ContextCompat.getColor(this, R.color.main_color));
        prevIndex = position;
        setToolbarTitle(itemTexts[position].getText().toString());
        if (recordItem != null) {
            if (position == 1) {
                recordItem.setVisible(true);
            } else {
                recordItem.setVisible(false);
            }
        }
    }

    /**
     * 滑动主页到指定位置
     *
     * @param position 要滑动的位置
     */
    private void setSelection(int position) {
        if (prevIndex == position) {
            return;
        }
        setCurrentTab(position);
        viewPager.setCurrentItem(position);
    }

    // 检测新版本
    private void checkVersion() {

    }

    // 删除缓存路径的照片
    private void deleteTmepDir() {
        File file = new File(Tools.getTempPath(this));
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    f.delete();
                }
            }
        }
    }

    class MainPagerAdapter extends FragmentPagerAdapter {
        Fragment[] l;
        int viewCount;
        int currentRefreshIndex = -1; // 当前要刷新的位置
        Fragment currentRefreshFragment; // 当前要替换的fragment
        FragmentManager fm;

        public MainPagerAdapter(FragmentManager fm, Fragment[] l) {
            super(fm);
            this.fm = fm;
            this.l = l;
            viewCount = l.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            // 当前位置是刷新的位置时, 替换显示的fragment
            if (position == currentRefreshIndex) {
                currentRefreshIndex = -1;
                String fragmentTag = fragment.getTag();
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fragment);
                fragment = l[position];
                ft.add(container.getId(), fragment, fragmentTag);
                ft.attach(fragment);
                ft.commit();
            }
            return fragment;
        }

        @Override
        public Fragment getItem(int i) {
            return l[i];
        }

        @Override
        public int getItemPosition(Object object) {
            if (object == currentRefreshFragment) {
                currentRefreshFragment = null;
                return POSITION_NONE;
            } else {
                return POSITION_UNCHANGED;
            }
        }

        @Override
        public int getCount() {
            return viewCount;
        }

        /**
         * 通知viewpager刷新fragment
         *
         * @param index    要更新的位置
         * @param fragment 要更新的fragment
         */
        public void notifyDataSetChanged(int index, Fragment fragment) {
            currentRefreshIndex = index;
            currentRefreshFragment = this.l[index];
            this.l[index] = fragment;
            notifyDataSetChanged();
        }
    }

    // 检查是否有读写文件到sdcard
    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                explainDialog();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            }
            return false;
        }
        return true;
    }

    // 显示获取权限说明
    private void explainDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("程序多处需要用到位置权限,是否授权？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //请求权限
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_LOCATION_PERMISSION);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showToast("您不允许获取自己当前位置");
                    }
                })
                .create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                showToast("您将无法正常使用图片缓存功能");
            } else {
                LocationUtil.getInstance().startLocation(getApplicationContext()); // 获取定位
            }
        }
    }

    @Override
    protected void onBroadcastReceive(Intent intent) {
        if (myPeopleFragment != null) {
            myPeopleFragment.updateInfo();
        } else if (myDoctorFragment != null) {
            myDoctorFragment.updateInfo();
        }
        (MyApplication.getInstance()).saveUserCache();
    }

}
