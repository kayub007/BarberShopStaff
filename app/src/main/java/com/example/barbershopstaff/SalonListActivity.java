package com.example.barbershopstaff;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barbershopstaff.Common.Common;
import com.example.barbershopstaff.Common.SpacesItemDecoration;
import com.example.barbershopstaff.Interface.IBranchLoadListener;
import com.example.barbershopstaff.Interface.IOnLoadCountSalon;
import com.example.barbershopstaff.Model.Salon;
import com.example.barbershopstaff.adapter.MySalonAdapter;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

public class SalonListActivity extends AppCompatActivity implements IOnLoadCountSalon, IBranchLoadListener {

    @BindView(R.id.txt_salon_count)
    TextView txt_salon_count;

    @BindView(R.id.recycler_salon)
    RecyclerView recycler_salon;

    IOnLoadCountSalon iOnLoadCountSalon;
    IBranchLoadListener iBranchLoadListener;

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon_list);

        ButterKnife.bind(this);

        initView();

        init();

        loadSalonBaseOnCity(Common.State_name);
    }

    private void loadSalonBaseOnCity(String name) {
        dialog.dismiss();

        FirebaseFirestore.getInstance().collection("AllSalon")
                .document(name)
                .collection("Branch")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            List<Salon> salons = new ArrayList<>();
                            iOnLoadCountSalon.onLoadCountSalonSuccess(task.getResult().size());
                            for (DocumentSnapshot salonSnapshot:task.getResult())
                            {
                                Salon salon = salonSnapshot.toObject(Salon.class);
                                salons.add(salon);
                            }
                            iBranchLoadListener.onBranchLoadSuccess(salons);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBranchLoadListener.onBranchLoadFailed(e.getMessage());
            }
        });
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this)
                .setCancelable(false)
                .build();
        iOnLoadCountSalon = this;
        iBranchLoadListener = this;
    }

    private void initView() {
        recycler_salon.setHasFixedSize(true);
        recycler_salon.setLayoutManager(new GridLayoutManager(this, 2));
        recycler_salon.addItemDecoration(new SpacesItemDecoration(8));
    }

    @Override
    public void onLoadCountSalonSuccess(int count) {
        txt_salon_count.setText(new StringBuilder("All Salon(")
                .append(count)
                .append(")"));

    }

    @Override
    public void onBranchLoadSuccess(List<Salon> branchList) {
        MySalonAdapter salonAdapter = new MySalonAdapter(this, branchList);
        recycler_salon.setAdapter(salonAdapter);

        dialog.dismiss();
    }

    @Override
    public void onBranchLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }
}
