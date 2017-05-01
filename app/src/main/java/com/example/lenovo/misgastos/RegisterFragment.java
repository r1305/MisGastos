package com.example.lenovo.misgastos;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lenovo.misgastos.Utils.SessionManager;


import org.json.simple.parser.JSONParser;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RegisterFragment extends Fragment {

    Button save;
    EditText id, monto, desc, fecha;
    Spinner categoria;
    SessionManager session;
    String idU;
    String data;

    public RegisterFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ListarCategorias();
        session = new SessionManager(getActivity());
        HashMap<String, String> datos = session.getUserDetails();
        idU = datos.get(SessionManager.KEY_ID);
        data=datos.get(SessionManager.KEY_CAT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        save = (Button) v.findViewById(R.id.form_guardar);
        id = (EditText) v.findViewById(R.id.form_id);
        monto = (EditText) v.findViewById(R.id.form_monto);
        desc = (EditText) v.findViewById(R.id.form_desc);
        categoria = (Spinner) v.findViewById(R.id.form_category);
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
                String c =(String) categoria.getSelectedItem();
                String f=fecha.getText().toString();
                RegistrarGasto(idU, m, d, c,f);
            }
        });

        List<String> list = new ArrayList<>();
        try {
            JSONParser p=new JSONParser();
            org.json.simple.JSONObject o=(org.json.simple.JSONObject)p.parse(data);
            //Toast.makeText(getActivity(),o.toString(), Toast.LENGTH_SHORT).show();
            org.json.simple.JSONArray a =(org.json.simple.JSONArray)o.get("data");
            for (int i = 0; i < a.size(); i++) {
                org.json.simple.JSONObject ob=(org.json.simple.JSONObject)a.get(i);
                list.add(ob.get("desc").toString());
            }
        } catch (Exception e) {
            Log.d("Register_Categorias",e.toString());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoria.setAdapter(dataAdapter);
        return v;
    }

    public void RegistrarGasto(String id, final String m, String d, String c,String f) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        final String url = "https://gastos-service.herokuapp.com/GuardarGasto?id=" + id +
                "&desc=" + d + "&monto=" + ("-"+m) + "&categoria=" + c+"&fecha="+f;

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
