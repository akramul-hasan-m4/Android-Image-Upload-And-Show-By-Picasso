package com.example.multipartfileandpicassoandeventbus;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.multipartfileandpicassoandeventbus.config.ViewPagerAdapter;

import java.util.ArrayList;

public class PicassoImageViewPagerActivity extends AppCompatActivity {

    private String[] imageUrls = new String[]{
            "https://cdn.pixabay.com/photo/2016/11/11/23/34/cat-1817970_960_720.jpg",
            "https://cdn.pixabay.com/photo/2017/12/21/12/26/glowworm-3031704_960_720.jpg",
            "https://cdn.pixabay.com/photo/2017/12/24/09/09/road-3036620_960_720.jpg",
            "https://cdn.pixabay.com/photo/2017/11/07/00/07/fantasy-2925250_960_720.jpg",
            "https://cdn.pixabay.com/photo/2017/10/10/15/28/butterfly-2837589_960_720.jpg"
    };

    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picasso_image_view_pager);

        String[] urls = getUrls();

        ViewPager viewPager = findViewById(R.id.view_pager);
        if (urls.length > 0){
             adapter = new ViewPagerAdapter(this, urls);
        }else{
             adapter = new ViewPagerAdapter(this, imageUrls);
        }

        viewPager.setAdapter(adapter);
    }

    private String[] getUrls(){
        ArrayList<String> list = getIntent().getStringArrayListExtra("urls");
        return list.toArray(new String[list.size()]);
    }
}
