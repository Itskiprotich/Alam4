package com.alam4.frags;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alam4.R;
import com.alam4.alerts.alerts;

import com.alam4.creategroup.creategroup;
import com.alam4.firebase.firebase;
import com.alam4.groups.responsegroup;
import com.alam4.joinresponse.joinrg;
import com.alam4.main.dashboard;
import com.alam4.manage.manage;
import com.alam4.manage.managealerts;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class home extends Fragment {

    CarouselView carouselView;

    int[] sampleImages = {R.drawable.one, R.drawable.banner, R.drawable.one, R.drawable.banner, R.drawable.one};
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ArrayList<String> arrayList;
    RecyclerView.LayoutManager mLayoutManager;
    int[] images = {
            R.drawable.alerts,
            R.drawable.manage,
            R.drawable.crrg,

            R.drawable.joinrg};

    public home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        carouselView = view.findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(imageListener);
        recyclerView = view.findViewById(R.id.recyclerView);
        arrayList = new ArrayList<>();
        myAdapter = new MyAdapter(getContext(), arrayList, images);
        mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myAdapter);
        loadMenu();
        return view;
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };

    private void loadMenu() {
        arrayList.clear();
        arrayList.add("Alerts");
        arrayList.add("Manage Alerts");
        arrayList.add("Create Response Group");

        arrayList.add("Join Response Group");


    }

    private class MyAdapter extends RecyclerView.Adapter<Holder> {
        Context context;
        ArrayList<String> arrayList;
        int[] images;

        public MyAdapter(Context context, ArrayList<String> arrayList, int[] img) {
            this.context = context;
            this.arrayList = arrayList;
            this.images = img;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.options, viewGroup, false);

            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder viewHolder, int i) {
            final String name = arrayList.get(i);
            viewHolder.textView.setText(name);
            viewHolder.imageView.setImageResource(images[i]);
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (name.equalsIgnoreCase("Alerts")) {
                        startActivity(new Intent(getContext(), alerts.class));
                        Animatoo.animateSlideLeft(context);
                        getActivity().finish();
                    }
                    if (name.equalsIgnoreCase("Manage Alerts")) {
                        startActivity(new Intent(getContext(), managealerts.class));
                        Animatoo.animateSlideLeft(context);
                        getActivity().finish();

                    }

                    if (name.equalsIgnoreCase("Join Response Group")) {
                        startActivity(new Intent(getContext(), joinrg.class));
                        Animatoo.animateSlideLeft(context);
                        getActivity().finish();

                    }

                    if (name.equalsIgnoreCase("firebase")) {
                        startActivity(new Intent(getContext(), firebase.class));
                        Animatoo.animateSlideLeft(context);
                        getActivity().finish();

                    }
                    if (name.equalsIgnoreCase("Create Response Group")) {
                        startActivity(new Intent(getContext(), creategroup.class));
                        Animatoo.animateSlideLeft(context);
                        getActivity().finish();
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    private class Holder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.aa);
            imageView = itemView.findViewById(R.id.bb);
        }
    }
}
