package com.example.transportapp.ui.favroutesuser;

import android.app.ActionBar;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.transportapp.R;
import com.example.transportapp.models.ListRequests;
import com.example.transportapp.models.UserFavRoutes;
import com.example.transportapp.providers.UserProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavRoutes_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavRoutes_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Fragment Controls
    View vista;
    Button btnCloseFragmentFavRoutes, btnAddRoute, btnAdd;
    DatabaseReference databaseReference;
    // popup add Route
    Dialog dialog;
    EditText txtNombre, txtDireccion;
    FirebaseAuth mAuth;
    TextView textView;

    public FavRoutes_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavRoutes_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavRoutes_Fragment newInstance(String param1, String param2) {
        FavRoutes_Fragment fragment = new FavRoutes_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        vista = inflater.inflate(R.layout.fragment_fav_routes_, container, false);
        btnCloseFragmentFavRoutes=(Button)vista.findViewById(R.id.exit);
        btnCloseFragmentFavRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Toast.makeText(getContext(), "Volver", Toast.LENGTH_LONG).show();
              getActivity().onBackPressed();
            }
        });
        //----get data
         onGetData();


        //------------
        btnAddRoute = (Button)vista.findViewById(R.id.btnAdd);
        btnAddRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 // Toast.makeText(getContext(), "Add", Toast.LENGTH_LONG).show();
                //containerRoutes
                LinearLayout relativeLayout = (LinearLayout)vista.findViewById(R.id.containerRoutes);
                Button boton = new Button(getContext());
                boton.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                boton.setText("Soy agregado por programacion");
              //  relativeLayout.addView(boton);
                onOpenpopup();
                /*
                android:id="@+id/btnAdd"
            android:shape="oval"
            android:layout_width="50dp"
            android:layout_height="50dp"
            android:background="@drawable/add1"
                 */
            }
        });

        //---------Autenticacion
        mAuth = FirebaseAuth.getInstance();
        return vista;
    }
    List<ListRequests> elements;
    String result;


    private void onGetData() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
       // databaseReference = FirebaseDatabase.getInstance().getReference().child("y7cdpw74HiOiqRtax5sOAm3pGzy2");
        elements = new ArrayList<>();
      //  Query q = databaseReference.child("FavPlaces").equalTo(mAuth.getCurrentUser().getUid());
      //  Query q = databaseReference.child("Users").orderByChild("y7cdpw74HiOiqRtax5sOAm3pGzy2");
       // String id = mAuth.getCurrentUser().getUid();
        Query q = databaseReference.child("y7cdpw74HiOiqRtax5sOAm3pGzy2").child("FavPlaces");
        // databaseReference.child(id).child("FavPlaces").child(userFavRoutes.getId()).setValue(userFavRoutes);

        q.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()) {
                   int cont = 0;
                   List<String> array;
                   array = new ArrayList<String>();
                   for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                   //    Toast.makeText(getContext(), dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                      // result = dataSnapshot.getValue().toString();
                       //dataSnapshot.child("createdAt").getValue().toString()
                   //    arrayString[cont] = dataSnapshot.getValue().toString();
                      // array[cont] = dataSnapshot.getValue().toString();
                       array.add(dataSnapshot.child("name_direction").getValue().toString());
                   //    result = dataSnapshot.child("name_direction").getValue().toString();
                      // textView.setText(result);
                       cont++;
                   }
                   LinearLayout relativeLayout = (LinearLayout)vista.findViewById(R.id.containerRoutes);
                   relativeLayout.removeAllViews();
                   for(int i=1; i <= cont; i++){

                       Button boton = new Button(getContext());
                       boton.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                       boton.setText(array.get(i-1));
                       relativeLayout.addView(boton);
                   }


                   //  relativeLayout.addView(boton);

               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        elements.add(new ListRequests("Pendiente", dataSnapshot.child("createdAt").getValue().toString() , "Miguel Angel", dataSnapshot.child("id").getValue().toString()));
                    }
                    ListRequestAdapter listRequestAdapter = new ListRequestAdapter(elements, getContext());
                    RecyclerView recyclerView = root.findViewById(R.id.ListRequest);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(listRequestAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
         */

    }


    public void onAddDirection(Dialog dialog) {
        btnAdd = (Button)dialog.findViewById(R.id.btnAdd);
        txtNombre = (EditText)dialog.findViewById(R.id.txtnombredireccion);
        txtDireccion = (EditText)dialog.findViewById(R.id.txtdireccion);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nombre = txtNombre.getText().toString();
                final String direccion = txtDireccion.getText().toString();
                if (nombre.isEmpty() || direccion.isEmpty()) {
                    Toast.makeText(getContext(), "Debe completar ambos campos", Toast.LENGTH_SHORT).show();
                } else {
                    String id = mAuth.getCurrentUser().getUid();
                    UserProvider prov = new UserProvider();

                    UserFavRoutes userOk = prov.createFavUser(nombre, direccion, id);
                    if (userOk != null) {
                        Toast.makeText(getContext(), "Dirección "+ nombre + ", agregada correctamente " + direccion , Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                       // onGetData();
                    } else {
                        Toast.makeText(getContext(), "Error registrando la dirección", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public interface OnFragmentInteractionListener {

    }

    ImageView imageView;

    public void onOpenpopup() {
        Dialog dialog =  new Dialog(getContext());
        dialog.setContentView(R.layout.style_addroute);
        //btnClose
        imageView = (ImageView)dialog.findViewById(R.id.btnClose);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        onAddDirection(dialog);
    }

}