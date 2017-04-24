package com.skkk.ww.skrecyclerviewitemdemo;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecyclerViewFragment extends Fragment {

    @InjectView(R.id.rv_fragment)
    RecyclerView mRvFragment;
    @InjectView(R.id.btn_insert)
    Button btnInsert;

    public final static int ALBUM_REQUEST_CODE = 1;
    public final static int CROP_REQUEST = 2;
    public final static int CAMERA_REQUEST_CODE = 3;
    public static String SAVED_IMAGE_DIR_PATH =
            Environment.getExternalStorageDirectory().getPath()
                    + "/SKRecyclerViewDemo/camera/";// 拍照路径
    String cameraPath;


    private List<DataModel> mDataList;
    private MyItemTouchHelperCallback callback;
    private ItemTouchHelper itemTouchHelper;
    private MyAdapter adapter;
    private LinearLayoutManager layoutManager;


    public RecyclerViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        ButterKnife.inject(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDataList = loadData();
        //设置RecyclerView...
        layoutManager = new LinearLayoutManager(getContext());
        mRvFragment.setLayoutManager(layoutManager);
        adapter = new MyAdapter(getContext(), mDataList);
        mRvFragment.setAdapter(adapter);
        mRvFragment.setItemAnimator(new DefaultItemAnimator());

        //设置添加照片点击事件
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 指定相机拍摄照片保存地址
                String state = Environment.getExternalStorageState();
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    cameraPath = SAVED_IMAGE_DIR_PATH + System.currentTimeMillis() + ".png";
                    Intent intent = new Intent();
                    // 指定开启系统相机的Action
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    String out_file_path = SAVED_IMAGE_DIR_PATH;
                    File dir = new File(out_file_path);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    // 把文件地址转换成Uri格式
                    Uri uri;
                    if (Build.VERSION.SDK_INT < 24) {
                        // 从文件中创建uri
                        uri = Uri.fromFile(new File(cameraPath));
                    } else {
                        //兼容android7.0 使用共享文件的形式
                        ContentValues contentValues = new ContentValues(1);
                        contentValues.put(MediaStore.Images.Media.DATA, cameraPath);
                        uri = FileProvider.getUriForFile(getContext().getApplicationContext(), getContext().getPackageName() + ".fileprovider", new File(cameraPath));
                    }
                    // 设置系统相机拍摄照片完成后图片文件的存放地址
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                } else {
                    Toast.makeText(getContext().getApplicationContext(), "请确认已经插入SD卡",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        //设置ItemTouchHelper
        callback = new MyItemTouchHelperCallback(getContext(), adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRvFragment);

        adapter.setOnStartDragListener(new OnStartDragListener() {
            @Override
            public void onStartDragListener(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }
        });
    }

    /**
     * 加载数据
     */
    private List<DataModel> loadData() {
        List<DataModel> dates = new ArrayList<>();
        dates.add(new DataModel("这里是第" + 1 + "条item", DataModel.Flag.TEXT, null));
        return dates;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                mDataList.add(new DataModel("", DataModel.Flag.IMAGE, cameraPath));
                mDataList.add(new DataModel("", DataModel.Flag.TEXT, null));
                adapter.notifyDataSetChanged();
            }
        }
    }
}
