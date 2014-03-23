package com.example.photocollageapp.activity;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

import android.widget.*;
import com.example.photocollageapp.loaders.ImageJSON;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;

import com.example.photocollageapp.R;
import com.example.photocollageapp.adapter.ImageAdapter;
import com.example.photocollageapp.layer.InstagramImage;
import com.example.photocollageapp.layer.LayoutCollage;
import com.example.photocollageapp.loaders.NameTask;

public class GalleryActivity extends Activity implements OnClickListener {
    public static final String TAG = "GALLERY";
    public static final String EXTRA_AUTHOR_ID = TAG + "_authorID";
    public static final String EXTRA_LAYOUT = TAG + "_layout";

    private Long mUserId;
    private LayoutCollage mLayout;
    private int mCount;
    private ArrayList<InstagramImage> mImages;
    public static ArrayList<InstagramImage> mResult;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        if (savedInstanceState == null) {

            mUserId = getIntent().getExtras().getLong(EXTRA_AUTHOR_ID);
            mLayout = (LayoutCollage) getIntent().getExtras().getSerializable(EXTRA_LAYOUT);
        } else {
            mUserId = savedInstanceState.getLong(EXTRA_AUTHOR_ID);
            mLayout = (LayoutCollage) savedInstanceState.getSerializable(EXTRA_LAYOUT);
        }
        mCount = mLayout.getCount();

        TextView stepView = (TextView) findViewById(R.id.title_step3);
        stepView.setText(getString(R.string.pattern_title_step, "3"));
        TextView titleView = (TextView) findViewById(R.id.title_step3_select_photos);
        titleView.setText(getString(R.string.pattern_title_step3_info, mCount));

        final String fullUrl = getString(R.string.pattern_instagram_image, mUserId);

        NameTask nameTask = new NameTask(this);
        nameTask.execute(fullUrl);
        String data = null;
        try {
            data = nameTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        try {
            Gson gson = new Gson();
            ImageJSON imageJSON = gson.fromJson(data, ImageJSON.class);
            mImages = new ArrayList<InstagramImage>();
            mResult = new ArrayList<InstagramImage>(mCount);
            if (imageJSON == null) {
                Toast.makeText(this, R.string.toast_not_public_image, Toast.LENGTH_SHORT).show();
            } else {

                for (ImageJSON.ImageDataType item : imageJSON.data) {
                    mImages.add(new InstagramImage(item.getRaiting(), item.getThumbnailUrl()));
                }

            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        Collections.sort(mImages, new Comparator<InstagramImage>() {
            @Override
            public int compare(InstagramImage lhs, InstagramImage rhs) {
                return rhs.rate - lhs.rate;
            }
        });


        GridView gridView = (GridView) findViewById(R.id.photo_list);
        gridView.setAdapter(new ImageAdapter(this, mImages));


        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {
                InstagramImage img = mImages.get(position);
                if (img.isSelected) {
                    if (mCount == 0) {
                        Button btnView = (Button) findViewById(R.id.button_generate_collage);
                        btnView.setEnabled(false);
                    }

                    final int zeroDimen = (int) getResources().getDimension(R.dimen.step_0);
                    ((ImageView) v).setPadding(zeroDimen, zeroDimen, zeroDimen, zeroDimen);
                    img.isSelected = false;
                    mCount++;
                    mResult.remove(img);

                } else if (mCount != 0) {
                    final int dimen = (int) getResources().getDimension(R.dimen.step_1_2);
                    ((ImageView) v).setPadding(dimen, dimen, dimen, dimen);

                    img.isSelected = true;
                    mCount--;
                    mResult.add(img);
                }

                if (mCount == 0) {
                    Button btnView = (Button) findViewById(R.id.button_generate_collage);
                    btnView.setEnabled(true);
                }

                TextView titleView = (TextView) findViewById(R.id.title_step3_select_photos);
                titleView.setText(getString(R.string.pattern_title_step3_info, mCount));
            }
        });

        Button generateButton = (Button) findViewById(R.id.button_generate_collage);
        generateButton.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(EXTRA_AUTHOR_ID, mUserId);
        outState.putSerializable(EXTRA_LAYOUT, mLayout);
        super.onSaveInstanceState(outState);
    }

    private void generate() {
        Intent intent = new Intent(this, FinalActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(FinalActivity.EXTRA_LAYOUT, mLayout);
        intent.putExtras(bundle);

        startActivity(intent);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_generate_collage:
                generate();
                break;
        }
    }
}
