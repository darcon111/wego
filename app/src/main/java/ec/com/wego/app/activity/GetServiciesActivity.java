package ec.com.wego.app.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.ContactsContract;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import ec.com.wego.app.R;
import ec.com.wego.app.clases.Spinner.MaterialSpinner;
import ec.com.wego.app.config.AppPreferences;
import ec.com.wego.app.config.Constants;
import ec.com.wego.app.holder.Serviciescarac;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


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
    private int idServicio;

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

                if(txtdia.getText().toString().equals("")){
                    new SweetAlertDialog(GetServiciesActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText(getResources().getString(R.string.error_dia))
                            .setConfirmText(getResources().getString(R.string.ok))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();

                                }
                            })
                            .show();

                    return;
                }


                Bundle extras = getIntent().getExtras();
                Intent intent = new Intent(GetServiciesActivity.this,GetServicies2Activity.class);
                intent.putExtra("fecha",txtdia.getText().toString());
                intent.putExtra("hora", list_horario.get(horario.getSelectedItemPosition()));

                Serviciescarac servicio = ServiciesCaractActivity.mListServicies.get(Integer.parseInt(extras.getString("id")));

                intent.putExtra("servicio_id", String.valueOf(servicio.getId()));
                intent.putExtra("costo", servicio.getCosto());
                intent.putExtra("id",extras.getString("id"));
                startActivity(intent);
                finish();

            }
        });

        list_horario = new ArrayList<String>();





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

        Bundle extras = getIntent().getExtras();

        idServicio= Integer.parseInt(extras.getString("id"));

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear = (++monthOfYear);
        String month = monthOfYear <= 9 ? "0" + monthOfYear : "" + monthOfYear;
        String day = dayOfMonth <= 9 ? "0" + dayOfMonth : "" + dayOfMonth;

        String date = year + "-" + month + "-" + day;


        Date fecha = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fecha_hoy =  hourdateFormat.format(fecha);

        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.add(Calendar.DAY_OF_MONTH, 1);

        String fecha_mañana = hourdateFormat.format(cal.getTime());



        //si la fecha actual es mayor que la escogida da error
        if(Constants.compareDate(date,fecha_hoy))
        {
            new SweetAlertDialog(GetServiciesActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getResources().getString(R.string.app_name))
                    .setContentText(getResources().getString(R.string.error_fecha_select))
                    .setConfirmText(getResources().getString(R.string.ok))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();

                        }
                    })
                    .show();
        }else
        {
            //si la fecha escogida no es mayor que mañana tomar en consideracion el tiempo
            if(Constants.compareDate(date,fecha_mañana))
            {
                //tiempo
                list_horario.clear();

                cal.setTime(fecha);
                cal.add(Calendar.MINUTE, ServiciesCaractActivity.mListServicies.get(idServicio).getTiempo());

                DateFormat formathora = new SimpleDateFormat("HH:mm");
                String hora_posible =  formathora.format(cal.getTime());

                String[] temp_hora= hora_posible.split(":");
                int hora = 7;
                if(Integer.parseInt(temp_hora[1])>1)
                {
                    hora = Integer.parseInt(temp_hora[0])+1;
                }else
                {
                    hora = Integer.parseInt(temp_hora[0]);
                }

                if(hora >=20)
                {
                    new SweetAlertDialog(GetServiciesActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText(getResources().getString(R.string.error_fecha_hora))
                            .setConfirmText(getResources().getString(R.string.ok))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();

                                }
                            })
                            .show();

                    return;

                }

                for (int x = hora ;x<20;x++)
                {
                    String temp = String.valueOf(x) + horario(x) + " - "+String.valueOf(x+1)+horario(x+1);
                    list_horario.add(temp);
                }

            }else{

                int hora = 7;
                list_horario.clear();
                for (int x = 7;x<17;x++)
                {
                    String temp = String.valueOf(x) + horario(x) + " - "+String.valueOf(x+1)+horario(x+1);
                    list_horario.add(temp);
                }

            }

            dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_horario);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            horario.setAdapter(dataAdapter);

            horario.setSelection(0);


            txtdia.setText(date);
        }





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
