package com.kt.whose.subordinate.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kt.whose.subordinate.Broadcast.BroadcastTag;
import com.kt.whose.subordinate.R;
import com.kt.whose.subordinate.Utils.Tool;
import com.kt.whose.subordinate.Utils.sqlModel.DevicesInfoSql;

import org.litepal.LitePal;



public class RgbDevicesControlActivity extends BaseActivity {

    private static final String TAG = "DevicesControlActivity";
    Toolbar toolbar;
    RecyclerView pixel_screen;

    GridLayoutManager gridLayoutManager;

    ImageView connectStateImage;
    TextView devicesNameText, devicesIdText;


    //GridView pixel_screen;

    private DevicesInfoSql devicesInfo;

    @Override
    public int initLayoutId() {
        return R.layout.activity_devices_control;
    }

    @Override
    public void initView() {
        toolbar = findViewById(R.id.devices_control_toolbar);
        toolbar.setOnClickListener(toolBarOnClickListener);

        connectStateImage = findViewById(R.id.devices_control_connect_state);
        devicesNameText = findViewById(R.id.devices_control_name);
        devicesIdText = findViewById(R.id.devices_control_id);

        /*pixel_screen = findViewById(R.id.devices_control_pixel_screen);
        pixel_screen.setNumColumns(10);*/
        /*pixel_screen = findViewById(R.id.devices_control_pixel_screen);

        gridLayoutManager = new GridLayoutManager(this,29);

        pixel_screen.setLayoutManager(gridLayoutManager);
//        pixel_screen.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        pixel_screen.setAdapter(new PixelAdapter(29,21));
        pixel_screen.addItemDecoration(new GridSpaceItemDecoration(29,10,10));*/


    }

    @Override
    protected void initEvent() {

        Intent intent = getIntent();

        long id = intent.getLongExtra("info",0);

        devicesInfo = LitePal.find(DevicesInfoSql.class, id);

        devicesIdText.setText(devicesInfo.getDevicesId());
        devicesNameText.setText(devicesInfo.getName());

        broadcastFilter();


        /*pixel_screen.setAdapter(new Pixel(10,6));*/
    }

    // toolbar 退出
    private View.OnClickListener toolBarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    @Override
    public void broadcastFilter() {
        super.broadcastFilter();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastTag.ACTION_MQTT_DISCONNECTED);
        intentFilter.addAction(BroadcastTag.ACTION_DEVICES_CONNECTED);
        intentFilter.addAction(BroadcastTag.ACTION_DEVICES_DISCONNECTED);

        intentFilter.addAction(BroadcastTag.EXTRA_DATA_MESSAGE);
        intentFilter.addAction(BroadcastTag.EXTRA_DATA_TOPIC);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);




    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BroadcastTag.ACTION_MQTT_DISCONNECTED.equals(action)) {

                connectStateImage.setSelected(false);
            } else if (BroadcastTag.ACTION_DEVICES_DISCONNECTED.equals(action)) {

                if (intent.getStringExtra(BroadcastTag.ACTION_DEVICES_DISCONNECTED).equals(devicesInfo.getDevicesId())){
                    connectStateImage.setSelected(false);
                }
            } else if (BroadcastTag.ACTION_DEVICES_CONNECTED.equals(action)) {

                if (intent.getStringExtra(BroadcastTag.ACTION_DEVICES_CONNECTED).equals(devicesInfo.getDevicesId())){
                    connectStateImage.setSelected(true);
                }
            }
        }
    };





    // =======================================================================================================================================


    private class Pixel extends BaseAdapter {

        private int width;
        private int height;

        public Pixel(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public int getCount() {
            return width * height;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {


            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

            if (view == null) {
                view = inflater.inflate(R.layout.item_pixel, viewGroup, false);

                AbsListView.LayoutParams param = new AbsListView.LayoutParams(getItemWidth(width), getItemHeight(height));
                view.setLayoutParams(param);
            }

            return view;
        }
    }

    private int getItemWidth(int i) {

        int width = px2dip(getApplicationContext(), Tool.getWidth(getApplicationContext()));
        int re = width - (45 * 2) - i;

        return dip2px(getApplicationContext(), (re / i) * 2);
    }

    private int getItemHeight(int i) {
        int height = 210 - 100;

        return dip2px(getApplicationContext(), height / i);

    }


    /*
     *
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     * */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /*
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     * */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 描述 : RecyclerView GridLayoutManager 等间距。
     * <p>
     * 等间距需满足两个条件：
     * 1.各个模块的大小相等，即 各列的left+right 值相等；
     * 2.各列的间距相等，即 前列的right + 后列的left = 列间距；
     * <p>
     * 在{@link #getItemOffsets(Rect, View, RecyclerView, RecyclerView.State)} 中针对 outRect 的left 和right 满足这两个条件即可
     * <p>
     * 作者 : shiguotao
     * 版本 : V1
     * 创建时间 : 2020/3/19 4:54 PM
     */
    public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final String TAG = "GridSpaceItemDecoration";

        private int mSpanCount;//横条目数量
        private int mRowSpacing;//行间距
        private int mColumnSpacing;// 列间距

        /**
         * @param spanCount     列数
         * @param rowSpacing    行间距
         * @param columnSpacing 列间距
         */
        public GridSpaceItemDecoration(int spanCount, int rowSpacing, int columnSpacing) {
            this.mSpanCount = spanCount;
            this.mRowSpacing = rowSpacing;
            this.mColumnSpacing = columnSpacing;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // 获取view 在adapter中的位置。
            int column = position % mSpanCount; // view 所在的列

            outRect.left = column * mColumnSpacing / mSpanCount; // column * (列间距 * (1f / 列数))
            outRect.right = mColumnSpacing - (column + 1) * mColumnSpacing / mSpanCount; // 列间距 - (column + 1) * (列间距 * (1f /列数))

            Log.e(TAG, "position:" + position
                    + "    columnIndex: " + column
                    + "    left,right ->" + outRect.left + "," + outRect.right);

            // 如果position > 行数，说明不是在第一行，则不指定行高，其他行的上间距为 top=mRowSpacing
            if (position >= mSpanCount) {
                outRect.top = mRowSpacing; // item top
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
}
