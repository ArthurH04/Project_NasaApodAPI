package com.arthurapps.astronomypictureoftheday;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;

import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView resultado;
    ImageView image;

    Button calendarButton;
    DatePickerDialog datePickerDialog;
    String calendarDateFormatted, currentDateFormatted, requestDate;
    Calendar minDateCalendar, todayCalendar, currentCalendar;
    Date calendarDate;
    String urlImage, explanation, title, copyright;

    int currentDayOfYear, currentDay, currentMonth, currentYear;

    private CircularProgressDrawable circularProgressDrawable;

    //String urlApi = "https://api.nasa.gov/planetary/apod?api_key=SUA_API";
    String ApiKey = "SUA_API ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultado = findViewById(R.id.Result);
        image = findViewById(R.id.imageNasa);
        calendarButton = findViewById(R.id.textDate);

        circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(5);
        circularProgressDrawable.setCenterRadius(30);
        circularProgressDrawable.start();

        //Data atual
        Date currentDate = Calendar.getInstance().getTime();
        todayCalendar = Calendar.getInstance();
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
        currentDay = todayCalendar.get(Calendar.DAY_OF_MONTH);
        currentMonth = todayCalendar.get(Calendar.MONTH);
        currentYear = todayCalendar.get(Calendar.YEAR);
        currentDayOfYear = todayCalendar.get(Calendar.DAY_OF_YEAR);

        currentDateFormatted = formatDate(currentDate);

        //Data minima
        minDateCalendar = Calendar.getInstance();
        minDateCalendar.set(Calendar.YEAR, 1995);
        minDateCalendar.set(Calendar.MONTH, 6);
        minDateCalendar.set(Calendar.DAY_OF_MONTH, 16);
        Date minDateDate = minDateCalendar.getTime();

        //Request data atual
        currentCalendar = Calendar.getInstance();
        requestDate = currentDateFormatted;
        makeRequest(currentDateFormatted);

        //Date Picker
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                currentCalendar = Calendar.getInstance();
                currentCalendar.set(Calendar.YEAR, year);
                currentCalendar.set(Calendar.MONTH, month);
                currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
                calendarDate = currentCalendar.getTime();
                calendarDateFormatted = formatDate(calendarDate);
                requestDate = calendarDateFormatted;
                makeRequest(calendarDateFormatted);
            }
        }, currentYear, currentMonth, currentDay);
        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMaxDate(new Date().getTime());
        datePicker.setMinDate(minDateDate.getTime());

    }

    //Faz request da API
    public void makeRequest(String date) {

        String url = "https://api.nasa.gov/planetary/apod?date=" + date + "&api_key=" + ApiKey;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Pegando a request da api e buscando determinados
                    //criterios no JSON
                    explanation = response.getString("explanation");
                    urlImage = response.getString("hdurl");
                    title = response.getString("title");
                    if (response.has("copyright")) {
                        copyright = response.getString("copyright");
                    }
                    updateGUI();
                } catch (JSONException e) {
                    //Se a request JSON não for requisitada
                    //este catch tratará a exceção
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            //Se obter um erro ao fazer o request
            //uma mensagem será apresentada ao usuário
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Falha ao obter os dados", Toast.LENGTH_SHORT).show();
            }
        });
        //Acessa a request pela classe MySingleton
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void updateGUI() {
        Glide.with(this).load(urlImage).placeholder(circularProgressDrawable).dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).
                into(image);

        resultado.setText("Copyright: " + copyright + "\n\nTitulo: " + title + "\n\nDescrição: " + explanation);
        resultado.setMovementMethod(new ScrollingMovementMethod());
    }

    public String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate;
        formattedDate = dateFormat.format(date);
        return formattedDate;
    }

    public void showCalendar(View view) {
        datePickerDialog.show();
    }
}