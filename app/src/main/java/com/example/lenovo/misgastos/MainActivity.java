package com.example.lenovo.misgastos;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout form;
    private GraphView graph;
    private SwipeRefreshLayout swipeContainer;
    private EditText form_id,form_monto,form_desc,gastos_id;
    private Button btn_guardar,btn_mostrar;
    RecyclerView reco;
    GastosRecycler adapter;
    List<JSONObject> l=new ArrayList<>();
    JSONArray g=new JSONArray();
    DataPoint[] d;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    graph.setVisibility(View.GONE);
                    form.setVisibility(View.GONE);
                    swipeContainer.setVisibility(View.VISIBLE);
                    form_id.setEnabled(false);
                    form_desc.setEnabled(false);
                    form_monto.setEnabled(false);
                    return true;
                case R.id.navigation_dashboard:
                    swipeContainer.setVisibility(View.GONE);
                    form.setVisibility(View.GONE);
                    graph.setVisibility(View.VISIBLE);
                    form_id.setEnabled(false);
                    form_desc.setEnabled(false);
                    form_monto.setEnabled(false);
                    return true;
                case R.id.navigation_notifications:
                    swipeContainer.setVisibility(View.GONE);
                    graph.setVisibility(View.GONE);
                    form.setVisibility(View.VISIBLE);
                    form_id.setEnabled(true);
                    form_desc.setEnabled(true);
                    form_monto.setEnabled(true);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        form=(LinearLayout)findViewById(R.id.form);
        swipeContainer=(SwipeRefreshLayout)findViewById(R.id.swipeContainer);
        form_id=(EditText)findViewById(R.id.form_id);
        form_monto=(EditText)findViewById(R.id.form_monto);
        form_desc=(EditText)findViewById(R.id.form_desc);
        gastos_id=(EditText)findViewById(R.id.gastos_id);
        btn_guardar=(Button)findViewById(R.id.form_guardar);
        btn_mostrar=(Button)findViewById(R.id.gastos_btn);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //GRAPH GASTOS
        graph = (GraphView) findViewById(R.id.graph);


        //REFRESH ON SWIPE
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String id=gastos_id.getText().toString();
                if(id=="" || id=="0"){
                    Toast.makeText(MainActivity.this, "Ingrese un ID", Toast.LENGTH_SHORT).show();
                }else{
                    GetGastos(id);
                }

            }
        });

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id=form_id.getText().toString();
                final String monto=form_monto.getText().toString();
                final String desc=form_desc.getText().toString();
                InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
                GuardarGastos(id,monto,desc);
            }
        });

        //CALL GASTOS JSON
        btn_mostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id=gastos_id.getText().toString();
                InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
                if(id=="" || id=="0"){
                    Toast.makeText(MainActivity.this, "Ingrese un ID", Toast.LENGTH_SHORT).show();
                }else{
                    GetGastos(id);
                }
            }
        });

        //LLENAR LISTA DE GASTOS
        reco=(RecyclerView)findViewById(R.id.recycler_view_gastos);
        reco.setLayoutManager(new LinearLayoutManager(this));
        adapter=new GastosRecycler(l);

        reco.setAdapter(adapter);
    }

    public void GetGastos(String id){
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "https://gastos-service.herokuapp.com/ListarGastos?id="+id;
// prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        swipeContainer.setRefreshing(false);
                        System.out.println("***** " + response);
                        //SET DATAPOINTS
                        try {
                            JSONArray ja = (JSONArray) response.get("data");
                            d = new DataPoint[ja.length()];
                            System.out.println("*** JSON Length *** " + ja.length());
                            for (int i = 0; i < ja.length(); i++) {
                                try {
                                    d[i] = new DataPoint((i+1), response.getJSONArray("data").getJSONObject(i).getDouble("monto"));
                                } catch (Exception e) {
                                    System.out.println("DATAPOINTS: "+e);
                                }
                            }
                            BarGraphSeries<DataPoint> series = new BarGraphSeries<>(d);
                            graph.addSeries(series);
                            //SET DEFAULT MAX & MIN VALUES on X and Y axis
                            graph.getViewport().setYAxisBoundsManual(true);
                            graph.getViewport().setMinY(0);
                            graph.getViewport().setXAxisBoundsManual(true);
                            graph.getViewport().setMinX(0);
                            graph.getViewport().setMaxX(31);

                            // styling
                            series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                                @Override
                                public int get(DataPoint data) {
                                    return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
                                }
                            });

                            series.setSpacing(30);

                            // draw values on top
                            series.setDrawValuesOnTop(true);
                            series.setValuesOnTopColor(Color.RED);
                            series.setValuesOnTopSize(20);
                        }catch(Exception e){
                            System.out.println(e);
                        }
                        //SET RECYCLER ADAPTER DATA
                        try {
                            l.clear();
                            JSONArray ja = (JSONArray) response.get("data");

                            for(int i=0;i<ja.length();i++){
                                l.add((JSONObject)ja.get(i));
                            }
                            adapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            //Toast.makeText(getActivity(),e.toString(), Toast.LENGTH_SHORT).show();
                            System.out.println("Gastos Error: "+e.toString());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                        swipeContainer.setRefreshing(false);
                    }
                }
        );

        // add it to the RequestQueue
        getRequest.setRetryPolicy(new DefaultRetryPolicy(10000,15,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(getRequest);
    }

    public void GuardarGastos(final String id,String monto,String desc){
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "https://gastos-service.herokuapp.com/GuardarGasto?id="+id+"&monto="+monto+"&desc="+desc;

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        //mTextMessage.setText(response.toString());
                        Toast.makeText(MainActivity.this, "Gastos guardados", Toast.LENGTH_SHORT).show();
                        GetGastos(id);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // add it to the RequestQueue
        getRequest.setRetryPolicy(new DefaultRetryPolicy(10000,15,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(getRequest);
    }

}
