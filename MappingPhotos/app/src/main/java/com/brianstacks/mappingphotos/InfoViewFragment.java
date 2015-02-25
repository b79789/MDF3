/**
 * Created by Brian Stacks
 * on 2/23/15
 * for FullSail.edu.
 */
package com.brianstacks.mappingphotos;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class InfoViewFragment extends Fragment {

    public static final String TAG = "InfoViewFragment.TAG";

    private static final String Arg_Data="data";

    TextView tv1;
    TextView tv2;
    ImageView iv1;
    Button closeButtton;
    ArrayList<EnteredData> myArrayList;

    Button showInfoButt;

    EnteredData enteredData;
    public static InfoViewFragment newInstance() {
        return new InfoViewFragment();
    }

    public InfoViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        //myArrayList = (ArrayList<EnteredData>)getActivity().getIntent().getSerializableExtra("myArrayList");
        enteredData = (EnteredData)getActivity().getIntent().getSerializableExtra("marker_data");
        tv1 =(TextView)getActivity().findViewById(R.id.detailsTV1);
        tv2 =(TextView)getActivity().findViewById(R.id.detailsTV2);
        iv1 =(ImageView)getActivity().findViewById(R.id.detailsViewImage);
        closeButtton =(Button)getActivity().findViewById(R.id.backButton);
        tv1.setText(enteredData.getName());
        tv2.setText(enteredData.getAge());
        iv1.setImageBitmap(BitmapFactory.decodeFile(enteredData.getPic()));
        closeButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}
