package com.example.lenovo.misgastos;

import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lenovo.misgastos.Utils.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;


public class RegistrarIngresosFragment extends Fragment {

    Button save;
    EditText id, monto, desc, fecha;
    SessionManager session;
    String idU;

    public RegistrarIngresosFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RegistrarIngresosFragment newInstance() {
        RegistrarIngresosFragment fragment = new RegistrarIngresosFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getActivity());
        HashMap<String, String> datos = session.getUserDetails();
        idU = datos.get(SessionManager.KEY_ID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_registrar_ingresos, container, false);
        save = (Button) v.findViewById(R.id.form_guardar);
        id = (EditText) v.findViewById(R.id.form_id);
        monto = (EditText) v.findViewById(R.id.form_monto);
        desc = (EditText) v.findViewById(R.id.form_desc);
        fecha = (EditText) v.findViewById(R.id.form_fecha);

        if(idU!="id"){
            id.setText(idU);
            id.setEnabled(false);
        }
        fecha.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String f;
                if(s.length()==2){
                    f=fecha.getText().toString()+"/";
                    fecha.setText(f);
                    fecha.setSelection(f.length());
                }
                if(s.length()==5){
                    f=fecha.getText().toString()+"/";
                    fecha.setText(f);
                    fecha.setSelection(f.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String m = monto.getText().toString();
                String d = desc.getText().toString();
                String f=fecha.getText().toString();
                RegistrarIngreso(idU, m, d, "Abono",f);
            }
        });
        return v;
    }

    public void RegistrarIngreso(String id, final String m, String d, String c,String f) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        final String url = "https://gastos-service.herokuapp.com/GuardarGasto?id=" + id +
                "&desc=" + d + "&monto=" + m + "&categoria=" + c+"&fecha="+f;

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        //swipeContainer.setRefreshing(false);
                        System.out.println("RegisterFragment " + response);
                        monto.setText("");
                        desc.setText("");
                        fecha.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                        //swipeContainer.setRefreshing(false);
                    }
                }
        );

        // add it to the RequestQueue
        getRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 15,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(getRequest);
    }



}
