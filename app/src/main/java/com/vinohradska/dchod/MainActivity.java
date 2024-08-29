package com.vinohradska.dchod;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import com.vinohradska.dchod.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd:MM:yyyy");
    public long dtulozene;
    private int rok;
    private int mesic;
    private Calendar cal = Calendar.getInstance();
    private Calendar cal1 = Calendar.getInstance();
    private Calendar caldiff = Calendar.getInstance();
    public CountDownTimer ct = new CountDownTimer(50000000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            timclick();
        }
        @Override
        public void onFinish() {
            odpocet();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://vinohradska.com/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        zobrazdatum();
        ImageButton ib = (ImageButton) findViewById(R.id.ibsetdate);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new DatePickerFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(newFragment, "oznaceni2");
                fragmentTransaction.commit();
            }
        });
        odpocet();
    }
    public void odpocet() {
        ct.start();
    }
    public void timclick() {
        TextView tw = (TextView) findViewById(R.id.vysledek);
        TextView twm = (TextView) findViewById(R.id.vysledekmesic);
        TextView twt = (TextView) findViewById(R.id.vysledektyden);
        TextView twd = (TextView) findViewById(R.id.vysledekden);
        TextView twh = (TextView) findViewById(R.id.vysledekhodin);
        TextView twmi = (TextView) findViewById(R.id.vysledekminut);
        TextView twv = (TextView) findViewById(R.id.vysledekvterin);
        cal.setTimeInMillis(System.currentTimeMillis());
        LocalDateTime localDate =
                Instant.ofEpochMilli(cal.getTimeInMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        dtulozene = sharedPreferences.getLong("dtm", cal.getTimeInMillis());
        caldiff.setTimeInMillis(dtulozene);
        LocalDateTime localDateDiff = LocalDateTime.ofInstant(caldiff.toInstant(), caldiff.getTimeZone().toZoneId()).toLocalDate().atStartOfDay();
        localDateDiff = localDateDiff.minusMonths((rok * 12) + mesic);
        String mesdif = String.valueOf(ChronoUnit.MONTHS.between(localDate, localDateDiff));
        String daydif = String.valueOf(ChronoUnit.DAYS.between(localDate, localDateDiff));
        String hourdif = String.valueOf(ChronoUnit.HOURS.between(localDate, localDateDiff));
        String mindif = String.valueOf(ChronoUnit.MINUTES.between(localDate, localDateDiff));
        String tydendif = String.valueOf(ChronoUnit.WEEKS.between(localDate, localDateDiff));
        String vtedif = String.valueOf(ChronoUnit.SECONDS.between(localDate, localDateDiff));
        Period period = localDate.toLocalDate().until(localDateDiff.toLocalDate());
        String celkem = String.format("Roků: %d měsíců: %d dní: %d", period.getYears(), period.getMonths(), period.getDays());
        tw.setText(" " + celkem + " ");
        twm.setText(" " + mesdif + " ");
        twt.setText(" " + tydendif + " ");
        twd.setText(" " + daydif + " ");
        twh.setText(" " + hourdif + " ");
        twmi.setText(" " + mindif + " ");
        twv.setText(" " + vtedif + " ");
    }

    private void zobrazdatum() {
        cal.setTimeInMillis(System.currentTimeMillis());
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        mesic = sharedPreferences.getInt("mesic", 0);
        rok = sharedPreferences.getInt("rok", 0);
        dtulozene = sharedPreferences.getLong("dtm", cal.getTimeInMillis());
        SeekBar sr = (SeekBar) findViewById(R.id.seekBar);
        SeekBar sm = (SeekBar) findViewById(R.id.seekBar1);
        TextView tr = (TextView) findViewById(R.id.rok);
        TextView tm = (TextView) findViewById(R.id.rok1);
        sr.setProgress(rok);
        sm.setProgress(mesic);
        tr.setText("Předdůchod/předčasný duchod roků: " + String.valueOf(rok));
        tm.setText("Předdůchod/předčasný duchod měsíců: " + String.valueOf(mesic));
        sr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                rok = progress;
                editor.putInt("rok", progress);
                editor.commit();
                TextView tr = (TextView) findViewById(R.id.rok);
                tr.setText("Předdůchod/předčasný duchod roků: " + String.valueOf(rok));
                if (rok == 5) {
                    mesic = 0;
                    editor.putInt("mesic", 0);
                    editor.commit();
                    SeekBar sm = (SeekBar) findViewById(R.id.seekBar1);
                    sm.setProgress(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                mesic = progress;
                editor.putInt("mesic", progress);
                editor.commit();
                TextView tr = (TextView) findViewById(R.id.rok1);
                tr.setText("Předdůchod/předčasný duchod měsíců: " + String.valueOf(mesic));
                if (rok == 5) {
                    mesic = 0;
                    editor.putInt("mesic", 0);
                    editor.commit();
                    seekBar.setProgress(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        cal1.setTimeInMillis(dtulozene);
        TextView dtakt = (TextView) findViewById(R.id.dateakt);
        TextView dtul = (TextView) findViewById(R.id.dateodch);
        String date1 = formatter.format(cal.getTime());
        String date2 = formatter.format(cal1.getTime());
        dtakt.setText(" " + date1 + " ");
        dtul.setText(" " + date2 + " ");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        View view1;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker.
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(requireContext(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c2 = Calendar.getInstance();
            c2.setTimeInMillis(System.currentTimeMillis());
            c2.set(Calendar.YEAR, year);          // January  1st, 2001 BC
            c2.set(Calendar.DAY_OF_MONTH, day);
            c2.set(Calendar.MONTH, month);
            c2.set(Calendar.HOUR, 0);
            c2.set(Calendar.SECOND, 0);
            view1 = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
            SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("dtm", c2.getTimeInMillis());
            editor.commit();
            TextView dtul = (TextView) view1.findViewById(R.id.dateodch);
            SimpleDateFormat format = new SimpleDateFormat("dd:MM:yyyy");
            String date2 = format.format(c2.getTime());
            dtul.setText(date2);
        }
    }
}