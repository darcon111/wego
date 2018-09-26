package ec.com.wego.app.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import ec.com.wego.app.R;
import ec.com.wego.app.clases.Spinner.MaterialSpinner;
import ec.com.wego.app.config.AppPreferences;

import java.util.ArrayList;
import java.util.Calendar;



public class GetServiciesActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener{

    private EditText txtdia;
    private AppPreferences appPreferences;
    private Toolbar toolbar;
    private String TAG = GetServiciesActivity.class.getName();
    private MaterialSpinner horario;
    private ArrayAdapter<String> dataAdapter;
    private Button btnContinuar;

    private ArrayList<String> list_horario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_servicies);



        appPreferences = new AppPreferences(GetServiciesActivity.this);

        /* toolbar*/
        toolbar = (Toolbar) findViewById(R.id.toolbaruser);

        TextView title = (TextView) findViewById(R.id.txtTitle);

        title.setText(getString(R.string.app_name));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow));
        }

        horario=(MaterialSpinner) findViewById(R.id.horario);
        btnContinuar=(Button) findViewById(R.id.btncontinuar);

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GetServiciesActivity.this,GetServicies2Activity.class);
                startActivity(intent);
            }
        });

        list_horario = new ArrayList<String>();

        int hora = 7;
        for (int x = 7;x<17;x++)
        {
            String temp = String.valueOf(x) + horario(x) + " - "+String.valueOf(x+1)+horario(x+1);
            list_horario.add(temp);
        }

        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_horario);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        horario.setAdapter(dataAdapter);

        horario.setSelection(1);



        txtdia = (EditText) findViewById(R.id.txtdia);


        txtdia.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            GetServiciesActivity.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                }
                Log.i("click text", "kakak");
                return false;
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear = (++monthOfYear);
        String month = monthOfYear <= 9 ? "0" + monthOfYear : "" + monthOfYear;
        String day = dayOfMonth <= 9 ? "0" + dayOfMonth : "" + dayOfMonth;

        String date = year + "-" + month + "-" + day;
        txtdia.setText(date);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //onBackPressed();
                finish();
                //------------
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    private String horario(int hora)
    {
        return (hora<=12)?"am":"pm";
    }
}
