package com.example.lenovo.misgastos;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lenovo.misgastos.Utils.SessionManager;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class DashBoardFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private GraphView graph;
    DataPoint[] d;
    private ColumnChartView chart;
    private ColumnChartData data;
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLabels = false;
    private boolean hasLabelForSelected = false;
    /*******************************************/
    private PieChartView chartP;
    private PieChartData dataP;
    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = false;
    private boolean hasCenterText1 = false;
    private boolean hasCenterText2 = false;
    private boolean isExploded = false;
    /*******************************************/
    private static final int DEFAULT_DATA = 0;
    private int dataType = DEFAULT_DATA;
    private List<SubcolumnValue> values;
    private int numColumns;
    private int numValues;
    JSONArray ja;

    String idU="";
    SessionManager session;

    public DashBoardFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DashBoardFragment newInstance() {
        DashBoardFragment fragment = new DashBoardFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //GRAPH GASTOS
        View v = inflater.inflate(R.layout.fragment_dash_board, container, false);

        /***** SET ID USER ****/
        session=new SessionManager(getActivity());
        HashMap<String,String> datos=session.getUserDetails();
        idU=datos.get(SessionManager.KEY_ID);

        graph = (GraphView) v.findViewById(R.id.graph);

        chart = (ColumnChartView) v.findViewById(R.id.chart);
        chartP = (PieChartView) v.findViewById(R.id.chartP);
        chart.setOnValueTouchListener(new ValueTouchListener());
        chartP.setOnValueTouchListener(new ValueTouchListenerPie());

        //layout.addView(chart);
        GetGastos(idU);
        return v;
    }

    private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            try {
                String desc = ja.getJSONObject(columnIndex).getString("desc");
                Toast.makeText(getActivity(),desc + "-" + value.getValue(), Toast.LENGTH_SHORT).show();
            }catch (Exception e){

            }
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }

    private class ValueTouchListenerPie implements PieChartOnValueSelectListener {


        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            try {
                String desc = ja.getJSONObject(arcIndex).getString("desc");
                Toast.makeText(getActivity(),desc + "-" + value.getValue(), Toast.LENGTH_SHORT).show();
            }catch (Exception e){

            }
        }

        @Override
        public void onValueDeselected() {

        }
    }

    public void GetGastos(String id) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        final String url = "https://gastos-service.herokuapp.com/ListarGastos?id=" + id;
// prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        //swipeContainer.setRefreshing(false);
                        System.out.println("***** DashBoard" + response);
                        //SET DATAPOINTS
                        try {
                           ja= (JSONArray) response.get("data");
                            d = new DataPoint[ja.length()];
                            numColumns=ja.length();
                            numValues=ja.length();
                            for (int i = 0; i < ja.length(); i++) {
                                try {
                                    Log.d("monto: ",String.valueOf(response.getJSONArray("data").getJSONObject(i).getDouble("monto")));
                                    if(response.getJSONArray("data").getJSONObject(i).getDouble("monto")<0){
                                        Log.d("negativo","true");
                                        d[i] = new DataPoint((i + 1), Math.abs(response.getJSONArray("data").getJSONObject(i).getDouble("monto")));
                                    }else{
                                        d[i]= new DataPoint((i + 1), 0);
                                    }

                                } catch (Exception e) {
                                    System.out.println("DATAPOINTS: " + e);
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

                            graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
                            graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling


                            // styling
                            series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                                @Override
                                public int get(DataPoint data) {
                                    return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
                                }
                            });

                            series.setOnDataPointTapListener(new OnDataPointTapListener() {
                                @Override
                                public void onTap(Series series, DataPointInterface dataPoint) {
                                    Toast.makeText(getActivity(), "Monto: " + dataPoint, Toast.LENGTH_SHORT).show();
                                }
                            });

                            series.setSpacing(30);

                            // draw values on top
                            series.setDrawValuesOnTop(true);
                            series.setValuesOnTopColor(Color.RED);
                            series.setValuesOnTopSize(20);


                            /************************** Column Chart ***********************************/
                            int numSubcolumns = 1;

                            // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
                            List<Column> columns = new ArrayList<Column>();

                            for (int i = 0; i < numColumns; ++i) {
                                Column column = new Column(values);
                                values = new ArrayList<>();
                                for (int j = 0; j < numSubcolumns; ++j) {
                                    if(response.getJSONArray("data").getJSONObject(i).getDouble("monto")<0){
                                        values.add(new SubcolumnValue(Math.abs((float) response.getJSONArray("data").getJSONObject(i).getDouble("monto"))
                                                , ChartUtils.pickColor()));
                                    }else{
                                        values.add(new SubcolumnValue(0, ChartUtils.pickColor()));
                                    }

                                }


                                column.setHasLabels(hasLabels);
                                column.setHasLabelsOnlyForSelected(hasLabelForSelected);
                                columns.add(column);
                            }

                            data = new ColumnChartData(columns);

                            if (hasAxes) {
                                Axis axisX = new Axis();
                                Axis axisY = new Axis().setHasLines(true);
                                if (hasAxesNames) {
                                    axisX.setName("Dia");
                                    axisY.setName("Monto");
                                }
                                data.setAxisXBottom(axisX);
                                data.setAxisYLeft(axisY);
                            } else {
                                data.setAxisXBottom(null);
                                data.setAxisYLeft(null);
                            }

                            chart.setColumnChartData(data);

                            /******************** Pie Chart *************************************/
                            List<SliceValue> values = new ArrayList<SliceValue>();
                            for (int i = 0; i < numValues; ++i) {
                                //System.out.println("entra al for");
                                if(ja.getJSONObject(i).getDouble("monto")<0){
                                    //System.out.println("entra al if");
                                    double value=Math.abs(ja.getJSONObject(i).getDouble("monto"));
                                    SliceValue sliceValue = new SliceValue((float)value, ChartUtils.pickColor());
                                    values.add(sliceValue);
                                }else{
                                    SliceValue sliceValue = new SliceValue(0, ChartUtils.pickColor());
                                    values.add(sliceValue);
                                }

                            }

                            dataP = new PieChartData(values);
                            dataP.setHasLabels(hasLabels);
                            dataP.setHasLabelsOnlyForSelected(hasLabelForSelected);
                            dataP.setHasLabelsOutside(hasLabelsOutside);
                            dataP.setHasCenterCircle(hasCenterCircle);

                            if (isExploded) {
                                dataP.setSlicesSpacing(24);
                            }

                            if (hasCenterText1) {
                                dataP.setCenterText1("Hello!");

                                // Get roboto-italic font.
                                Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");
                                dataP.setCenterText1Typeface(tf);
                            }

                            if (hasCenterText2) {
                                dataP.setCenterText2("Charts (Roboto Italic)");

                                Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");

                                dataP.setCenterText2Typeface(tf);
                            }

                            chartP.setPieChartData(dataP);
                        } catch (Exception e) {
                            System.out.println("PieChart: "+e);
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
