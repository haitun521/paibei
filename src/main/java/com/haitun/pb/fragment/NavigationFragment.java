package com.haitun.pb.fragment;


import android.app.Fragment;
import android.content.Intent;

import com.haitun.pb.R;
import com.haitun.pb.ui.ClassActivity_;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_navigation)
public class NavigationFragment extends Fragment {

    public static final String CLASS_KEY="class";

    //0 酒店 1 医院 2 周边*旅游
    @Click(R.id.nav_btn_hotel)
    public void clickHotel(){
        ToActivity(0);
    }

    @Click(R.id.nav_btn_hospital)
    public void clickHospital(){
        ToActivity(1);
    }
    @Click(R.id.nav_btn_trip)
    public void clickTrip(){
        ToActivity(2);
    }


    public void ToActivity(int index){
        Intent intent=new Intent(getActivity(),ClassActivity_.class);
        intent.putExtra(CLASS_KEY,index);
        startActivity(intent);
    }
}
