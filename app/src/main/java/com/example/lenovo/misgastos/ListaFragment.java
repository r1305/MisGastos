package com.example.lenovo.misgastos;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lenovo.misgastos.Utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ListaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private SessionManager session;

    private String idU;
    private SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView gastos;
    GastosRecycler adapter;

    List<JSONObject> l = new ArrayList<>();

    public ListaFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ListaFragment newInstance() {
        ListaFragment fragment = new ListaFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getActivity());
        HashMap<String, String> datos = session.getUserDetails();
        idU = datos.get(SessionManager.KEY_ID);
        if (idU != ("id")) {
            GetGastos(idU);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        swipeRefreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.swipeContainer);
        gastos = (RecyclerView) v.findViewById(R.id.recycler_view_gastos);
        gastos.setLayoutManager(new LinearLayoutManager(getActivity()));

        session = new SessionManager(getActivity());
        adapter = new GastosRecycler(l);

        gastos.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                GetGastos(idU);
            }
        });

        return v;
    }

    public void GetGastos(String id) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        final String url = "https://gastos-service.herokuapp.com/ListarGastos?id=" + id;

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        //swipeContainer.setRefreshing(false);
                        System.out.println("ListaFragment " + response);
                        try {
                            l.clear();

                            JSONArray ja = (JSONArray) response.get("data");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject ob = (JSONObject) ja.get(i);
                                l.add(ob);
                            }
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (Exception e) {
                            System.out.println("ListaFragmentError" + e);
                        }

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
