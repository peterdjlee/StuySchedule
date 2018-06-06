package com.leebros.stuyschedule;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextView tvPhysEd;
    private TextView tvClock;
    private TextView tvEnd;
    private TextView tvTimeLeft;
    private TextView tvPeriod;
    private TextView tvNextClass;
    private Toolbar toolbar;
    private int iTime;
    private Firebase fbRef;

    //Separate method to change the given time in integers (HHMM) to a String (HH:MM).
    public String numberToString(int time) {
        //Converts military time into standard time.
        if (time / 100 > 12) {
            time = time - 1200;
        }
        String stringTime = Integer.toString(time);
        String formattedHour;
        String formattedMinute;
        String formattedTime;
        //If the number is less than 4 digits, we take a different index since we have a shorter length.
        if (stringTime.length() == 4) {
            formattedHour = stringTime.substring(0, 2);
            formattedMinute = stringTime.substring(2, 4);
            formattedTime = formattedHour + ":" + formattedMinute;
            return formattedTime;
        } else {
            formattedHour = stringTime.substring(0, 1);
            formattedMinute = stringTime.substring(1, 3);
            formattedTime = "0" + formattedHour + ":" + formattedMinute;
            return formattedTime;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageButton ibHomeroom;
        ImageButton ibNoHomeroom;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface customFont1 = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue Medium.ttf");
        Typeface customFont2 = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue Light.ttf");

        tvPhysEd = (TextView) findViewById(R.id.tvPhysEd);
        tvClock = (TextView) findViewById(R.id.tvClock);
        tvEnd = (TextView) findViewById(R.id.tvEnd);
        tvTimeLeft = (TextView) findViewById(R.id.tvTimeLeft);
        tvPeriod = (TextView) findViewById(R.id.tvPeriod);
        tvNextClass = (TextView) findViewById(R.id.tvNextClass);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        ibHomeroom = (ImageButton) findViewById(R.id.ibHomeroom);
        ibNoHomeroom = (ImageButton) findViewById(R.id.ibNoHomeroom);

        ibHomeroom.setOnClickListener(onClickListener);
        ibNoHomeroom.setOnClickListener(onClickListener);
        tvClock.setOnClickListener(onClickTime);

        tvPhysEd.setTypeface(customFont1);
        tvClock.setTypeface(customFont2);
        tvEnd.setTypeface(customFont1);
        tvTimeLeft.setTypeface(customFont1);
        tvPeriod.setTypeface(customFont1);
        tvNextClass.setTypeface(customFont1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, AppPreferences.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    private View.OnClickListener onClickTime = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
            MainActivity.this.startActivity(intent);
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String end = "End: ";
            final String timeLeft = "Time Left: ";
            final String minutes = " Minutes";
            final String nextClass = "Next Class: ";

            // Get current time.
            Calendar c = Calendar.getInstance();
            // Create a format for the time.
            SimpleDateFormat df = new SimpleDateFormat("HHmm");
            SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat df3 = new SimpleDateFormat("ddMMM");

            //Time for clock
            String currentTime = df2.format(c.getTime());

            //Time for comparison
            iTime = Integer.parseInt(df.format(c.getTime()));

            // Log.e("Parsed date", "date: " + iTime);
            tvClock.setText(currentTime);

            // Convert the time into the given format df
            String currentDate = df3.format(c.getTime());
            //Month
            String sMonth = "/" + currentDate.substring(2);
            //Day
            String sDay = "/" + currentDate.substring(0,2);

            String baseURL = "https://fireapp-65d05.firebaseio.com/ABList";
            String finalURL = baseURL + sMonth + sDay;

            fbRef = new Firebase(finalURL);

                fbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String value = dataSnapshot.getValue(String.class);
                        String display = "";

                        if (value == null) {
                            display = "N/A";
                        } else if (value.substring(0,1).equals("A")) {
                            display = value.substring(0,1) + ": You have Phys. Ed. today";
                        } else if (value.substring(0,1).equals("B")) {
                            display = value.substring(0, 1) + ": You do not have Phys. Ed. today";
                        } else {
                            display = value;
                        }
                        tvPhysEd.setText(display);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            boolean inSchool = 800 <= iTime && 1535 > iTime;

            switch(v.getId()) {
                case R.id.ibNoHomeroom:

                    if (inSchool) {

                        int firstEnd = 841;
                        int firstSecondEnd = 845;
                        int secondEnd = 926;
                        int secondThirdEnd = 931;
                        int thirdEnd = 1015;
                        int thirdFourEnd = 1020;
                        int fourEnd = 1101;
                        int fourFiveEnd = 1106;
                        int fiveEnd = 1147;
                        int fiveSixEnd = 1152;
                        int sixEnd = 1233;
                        int sixSevenEnd = 1238;
                        int sevenEnd = 1319;
                        int sevenEightEnd = 1324;
                        int eightEnd = 1405;
                        int eightNineEnd = 1409;
                        int nineEnd = 1450;
                        int nineTenEnd = 1454;
                        int tenEnd = 1535;

                        // String to output the formatted time in which the period ends.
                        String firstEndTime = end + numberToString(firstEnd);
                        String firstSecondEndTime = end + numberToString(firstSecondEnd);
                        String secondEndTime = end + numberToString(secondEnd);
                        String secondThirdEndTime = end + numberToString(secondThirdEnd);
                        String thirdEndTime = end + numberToString(thirdEnd);
                        String thirdFourEndTime = end + numberToString(thirdFourEnd);
                        String fourEndTime = end + numberToString(fourEnd);
                        String fourFiveEndTime = end + numberToString(fourFiveEnd);
                        String fiveEndTime = end + numberToString(fiveEnd);
                        String fiveSixEndTime = end + numberToString(fiveSixEnd);
                        String sixEndTime = end + numberToString(sixEnd);
                        String sixSevenEndTime = end + numberToString(sixSevenEnd);
                        String sevenEndTime = end + numberToString(sevenEnd);
                        String sevenEightEndTime = end + numberToString(sevenEightEnd);
                        String eightEndTime = end + numberToString(eightEnd);
                        String eightNineEndTime = end + numberToString(eightNineEnd);
                        String nineEndTime = end + numberToString(nineEnd);
                        String nineTenEndTime = end + numberToString(nineTenEnd);
                        String tenEndTime = end + numberToString(tenEnd);

                        // Booleans to check which period you're in
                        boolean firstPd = iTime >= 800 && iTime < firstEnd;
                        boolean firstSecond = iTime >= firstEnd && iTime < firstSecondEnd;
                        boolean secondPd = iTime >= firstSecondEnd && iTime < secondEnd;
                        boolean secondThird = iTime >= secondEnd && iTime < secondThirdEnd;
                        boolean thirdPd = iTime >= secondThirdEnd && iTime < thirdEnd;
                        boolean thirdFour = iTime >= thirdEnd && iTime < thirdFourEnd;
                        boolean fourPd = iTime >= thirdFourEnd && iTime < fourEnd;
                        boolean fourFive = iTime >= fourEnd && iTime < fourFiveEnd;
                        boolean fivePd = iTime >= fourFiveEnd && iTime < fiveEnd;
                        boolean fiveSix = iTime >= fiveEnd && iTime < fiveSixEnd;
                        boolean sixPd = iTime >= fiveSixEnd && iTime < sixEnd;
                        boolean sixSeven = iTime >= sixEnd && iTime < sixSevenEnd;
                        boolean sevenPd = iTime >= sixSevenEnd && iTime < sevenEnd;
                        boolean sevenEight = iTime >= sevenEnd && iTime < sevenEightEnd;
                        boolean eightPd = iTime >= sevenEightEnd && iTime < eightEnd;
                        boolean eightNine = iTime >= eightEnd && iTime < eightNineEnd;
                        boolean ninePd = iTime >= eightNineEnd && iTime < nineEnd;
                        boolean nineTen = iTime >= nineEnd && iTime < nineTenEnd;
                        boolean tenPd = iTime >= nineTenEnd && iTime < tenEnd;

                        int maxPeriod = 41;

                        // If statements to check which period it is.
                        if (firstPd) {
                            // Equation to get the time left in the period.
                            int check1 = firstEnd - iTime;
                            // Since minutes do not go above 60, we need to subtract 40 from the time difference.
                            if (check1 > maxPeriod) {
                                check1 -= 40;
                            }
                            String timeLeft1 = timeLeft + String.valueOf(check1) + minutes;
                            tvPeriod.setText("Current Period: 1st");
                            tvEnd.setText(firstEndTime);
                            tvTimeLeft.setText(timeLeft1);
                        }
                        if (firstSecond) {
                            int check12 = firstSecondEnd - iTime;
                            if (check12 > maxPeriod) {
                                check12 -= 40;
                            }
                            String timeLeft12 = timeLeft + String.valueOf(check12) + minutes;
                            tvPeriod.setText("Current Period: 1st - 2nd");
                            tvEnd.setText(firstSecondEndTime);
                            tvTimeLeft.setText(timeLeft12);
                        }

                        if (secondPd) {
                            int check2 = secondEnd - iTime;
                            if (check2 > maxPeriod) {
                                check2 -= 40;
                            }
                            String timeLeft2 = timeLeft + String.valueOf(check2) + minutes;
                            tvPeriod.setText("Current Period: 2nd");
                            tvEnd.setText(secondEndTime);
                            tvTimeLeft.setText(timeLeft2);
                        }
                        if (secondThird) {
                            int check23 = secondThirdEnd - iTime;
                            if (check23 > maxPeriod) {
                                check23 -= 40;
                            }
                            String timeLeft23 = timeLeft + String.valueOf(check23) + minutes;
                            tvPeriod.setText("Current Period: 2nd - 3rd");
                            tvEnd.setText(secondThirdEndTime);
                            tvTimeLeft.setText(timeLeft23);
                        }


                        if (thirdPd) {
                            int check3 = thirdEnd - iTime;
                            if (check3 > maxPeriod) {
                                check3 -= 40;
                            }
                            String timeLeft3 = timeLeft + String.valueOf(check3) + minutes;
                            tvPeriod.setText("Current Period: 3rd");
                            tvEnd.setText(thirdEndTime);
                            tvTimeLeft.setText(timeLeft3);
                        }
                        if (thirdFour) {
                            int check34 = thirdFourEnd - iTime;
                            if (check34 > maxPeriod) {
                                check34 -= 40;
                            }
                            String timeLeft34 = timeLeft + String.valueOf(check34) + minutes;
                            tvPeriod.setText("Current Period: 3th - 4th");
                            tvEnd.setText(thirdFourEndTime);
                            tvTimeLeft.setText(timeLeft34);
                        }


                        if (fourPd) {
                            int check4 = fourEnd - iTime;
                            if (check4 > maxPeriod) {
                                check4 -= 40;
                            }
                            String timeLeft4 = timeLeft + String.valueOf(check4) + minutes;
                            tvPeriod.setText("Current Period: 4th");
                            tvEnd.setText(fourEndTime);
                            tvTimeLeft.setText(timeLeft4);
                        }
                        if (fourFive) {
                            int check45 = fourFiveEnd - iTime;
                            if (check45 > maxPeriod) {
                                check45 -= 40;
                            }
                            String timeLeft45 = timeLeft + String.valueOf(check45) + minutes;
                            tvPeriod.setText("Current Period: 4th - 5th");
                            tvEnd.setText(fourFiveEndTime);
                            tvTimeLeft.setText(timeLeft45);
                        }

                        if (fivePd) {
                            int check5 = fiveEnd - iTime;
                            if (check5 > maxPeriod) {
                                check5 -= 40;
                            }
                            String timeLeft5 = timeLeft + String.valueOf(check5) + minutes;
                            tvPeriod.setText("Current Period: 5th");
                            tvEnd.setText(fiveEndTime);
                            tvTimeLeft.setText(timeLeft5);
                        }
                        if (fiveSix) {
                            int check56 = fiveSixEnd - iTime;
                            if (check56 > maxPeriod) {
                                check56 -= 40;
                            }
                            String timeLeft56 = timeLeft + String.valueOf(check56) + minutes;
                            tvPeriod.setText("Current Period: 5th - 6th");
                            tvEnd.setText(fiveSixEndTime);
                            tvTimeLeft.setText(timeLeft56);
                        }


                        if (sixPd) {
                            int check6 = sixEnd - iTime;
                            if (check6 > maxPeriod) {
                                check6 -= 40;
                            }
                            String timeLeft6 = timeLeft + String.valueOf(check6) + minutes;
                            tvPeriod.setText("Current Period: 6th");
                            tvEnd.setText(sixEndTime);
                            tvTimeLeft.setText(timeLeft6);
                        }
                        if (sixSeven) {
                            int check67 = sixSevenEnd - iTime;
                            if (check67 > maxPeriod) {
                                check67 -= 40;
                            }
                            String timeLeft67 = timeLeft + String.valueOf(check67) + minutes;
                            tvPeriod.setText("Current Period: 6th - 7th");
                            tvEnd.setText(sixSevenEndTime);
                            tvTimeLeft.setText(timeLeft67);
                        }


                        if (sevenPd) {
                            int check7 = sevenEnd - iTime;
                            if (check7 > maxPeriod) {
                                check7 -= 40;
                            }
                            String timeLeft7 = timeLeft + String.valueOf(check7) + minutes;
                            tvPeriod.setText("Current Period: 7th");
                            tvEnd.setText(sevenEndTime);
                            tvTimeLeft.setText(timeLeft7);
                        }
                        if (sevenEight) {
                            int check78 = sevenEightEnd - iTime;
                            if (check78 > maxPeriod) {
                                check78 -= 40;
                            }
                            String timeLeft78 = timeLeft + String.valueOf(check78) + minutes;
                            tvPeriod.setText("Current Period: 7th - 8th");
                            tvEnd.setText(sevenEightEndTime);
                            tvTimeLeft.setText(timeLeft78);
                        }


                        if (eightPd) {
                            int check8 = eightEnd - iTime;
                            if (check8 > maxPeriod) {
                                check8 -= 40;
                            }
                            String timeLeft8 = timeLeft + String.valueOf(check8) + minutes;
                            tvPeriod.setText("Current Period: 8th");
                            tvEnd.setText(eightEndTime);
                            tvTimeLeft.setText(timeLeft8);
                        }
                        if (eightNine) {
                            int check89 = eightNineEnd - iTime;
                            if (check89 > maxPeriod) {
                                check89 -= 40;
                            }
                            String timeLeft89 = timeLeft + String.valueOf(check89) + minutes;
                            tvPeriod.setText("Current Period: 8th - 9th");
                            tvEnd.setText(eightNineEndTime);
                            tvTimeLeft.setText(timeLeft89);
                        }


                        if (ninePd) {
                            int check9 = nineEnd - iTime;
                            if (check9 > maxPeriod) {
                                check9 -= 40;
                            }
                            String timeLeft9 = timeLeft + String.valueOf(check9) + minutes;
                            tvPeriod.setText("Current Period: 9th");
                            tvEnd.setText(nineEndTime);
                            tvTimeLeft.setText(timeLeft9);

                        }
                        if (nineTen) {
                            int check90 = nineTenEnd - iTime;
                            if (check90 > maxPeriod) {
                                check90 -= 40;
                            }
                            String timeLeft90 = timeLeft + String.valueOf(check90) + minutes;
                            tvPeriod.setText("Current Period: 9th - 10th");
                            tvEnd.setText(nineTenEndTime);
                            tvTimeLeft.setText(timeLeft90);
                        }

                        if (tenPd) {
                            int check10 = tenEnd - iTime;
                            if (check10 > maxPeriod) {
                                check10 -= 40;
                            }
                            String timeLeft10 = timeLeft + String.valueOf(check10) + minutes;
                            tvPeriod.setText("Current Period: 10th");
                            tvEnd.setText(tenEndTime);
                            tvTimeLeft.setText(timeLeft10);
                        }
                    } else {
                        tvPeriod.setText("Current Period:");
                        tvEnd.setText(end);
                        tvTimeLeft.setText(timeLeft);
                        tvNextClass.setText(nextClass);
                    }
                    break;

                case R.id.ibHomeroom:
                    if (inSchool) {

                        int firstEnd = 840;
                        int firstSecondEnd = 845;
                        int secondEnd = 925;
                        int secondThirdEnd = 929;
                        int thirdEnd = 1009;
                        int thirdHomeroomEnd = 1013;
                        int homeroomEnd = 1025;
                        int homeroomFourEnd = 1030;
                        int fourEnd = 1110;
                        int fourFiveEnd = 1114;
                        int fiveEnd = 1154;
                        int fiveSixEnd = 1158;
                        int sixEnd = 1238;
                        int sixSevenEnd = 1242;
                        int sevenEnd = 1322;
                        int sevenEightEnd = 1326;
                        int eightEnd = 1406;
                        int eightNineEnd = 1410;
                        int nineEnd = 1450;
                        int nineTenEnd = 1455;
                        int tenEnd = 1535;

                        String firstEndTime = end + numberToString(firstEnd);
                        String firstSecondEndTime = end + numberToString(firstSecondEnd);
                        String secondEndTime = end + numberToString(secondEnd);
                        String secondThirdEndTime = end + numberToString(secondThirdEnd);
                        String thirdEndTime = end + numberToString(thirdEnd);
                        String thirdHomeroomEndTime = end + numberToString(thirdHomeroomEnd);
                        String homeroomEndTime = end + numberToString(homeroomEnd);
                        String homeroomFourEndTime = end + numberToString(homeroomFourEnd);
                        String fourEndTime = end + numberToString(fourEnd);
                        String fourFiveEndTime = end + numberToString(fourFiveEnd);
                        String fiveEndTime = end + numberToString(fiveEnd);
                        String fiveSixEndTime = end + numberToString(fiveSixEnd);
                        String sixEndTime = end + numberToString(sixEnd);
                        String sixSevenEndTime = end + numberToString(sixSevenEnd);
                        String sevenEndTime = end + numberToString(sevenEnd);
                        String sevenEightEndTime = end + numberToString(sevenEightEnd);
                        String eightEndTime = end + numberToString(eightEnd);
                        String eightNineEndTime = end + numberToString(eightNineEnd);
                        String nineEndTime = end + numberToString(nineEnd);
                        String nineTenEndTime = end + numberToString(nineTenEnd);
                        String tenEndTime = end + numberToString(tenEnd);

                        // Booleans to check which period you're in
                        boolean firstPd = iTime >= 800 && iTime < firstEnd;
                        boolean firstSecond = iTime >= firstEnd && iTime < firstSecondEnd;
                        boolean secondPd = iTime >= firstSecondEnd && iTime < secondEnd;
                        boolean secondThird = iTime >= secondEnd && iTime < secondThirdEnd;
                        boolean thirdPd = iTime >= secondThirdEnd && iTime < thirdEnd;
                        boolean thirdHomeroom = iTime >= thirdEnd && iTime < thirdHomeroomEnd;
                        boolean homeroomPd = iTime >= thirdHomeroomEnd && iTime < homeroomEnd;
                        boolean homeroomFour = iTime >= homeroomEnd && iTime < homeroomFourEnd;
                        boolean fourPd = iTime >= homeroomFourEnd && iTime < fourEnd;
                        boolean fourFive = iTime >= fourEnd && iTime < fourFiveEnd;
                        boolean fivePd = iTime >= fourFiveEnd && iTime < fiveEnd;
                        boolean fiveSix = iTime >= fiveEnd && iTime < fiveSixEnd;
                        boolean sixPd = iTime >= fiveSixEnd && iTime < sixEnd;
                        boolean sixSeven = iTime >= sixEnd && iTime < sixSevenEnd;
                        boolean sevenPd = iTime >= sixSevenEnd && iTime < sevenEnd;
                        boolean sevenEight = iTime >= sevenEnd && iTime < sevenEightEnd;
                        boolean eightPd = iTime >= sevenEightEnd && iTime < eightEnd;
                        boolean eightNine = iTime >= eightEnd && iTime < eightNineEnd;
                        boolean ninePd = iTime >= eightNineEnd && iTime < nineEnd;
                        boolean nineTen = iTime >= nineEnd && iTime < nineTenEnd;
                        boolean tenPd = iTime >= nineTenEnd && iTime < tenEnd;

                        int maxPeriod = 41;

                        // If statements to check which period it is.
                        if (firstPd) {
                            // Equation to get the time left in the period.
                            int check1 = firstEnd - iTime;
                            // Since minutes do not go above 60, we need to subtract 40 from the time difference.
                            if (check1 > maxPeriod) {
                                check1 -= 40;
                            }
                            String timeLeft1 = timeLeft + String.valueOf(check1) + minutes;
                            tvPeriod.setText("Current Period: 1st");
                            tvEnd.setText(firstEndTime);
                            tvTimeLeft.setText(timeLeft1);
                        }
                        if (firstSecond) {
                            int check12 = firstSecondEnd - iTime;
                            if (check12 > maxPeriod) {
                                check12 -= 40;
                            }
                            String timeLeft12 = timeLeft + String.valueOf(check12) + minutes;
                            tvPeriod.setText("Current Period: 1th - 2th");
                            tvEnd.setText(firstSecondEndTime);
                            tvTimeLeft.setText(timeLeft12);
                        }

                        if (secondPd) {
                            int check2 = secondEnd - iTime;
                            if (check2 > maxPeriod) {
                                check2 -= 40;
                            }
                            String timeLeft2 = timeLeft + String.valueOf(check2) + minutes;
                            tvPeriod.setText("Current Period: 2nd");
                            tvEnd.setText(secondEndTime);
                            tvTimeLeft.setText(timeLeft2);
                        }
                        if (secondThird) {
                            int check23 = secondThirdEnd - iTime;
                            if (check23 > maxPeriod) {
                                check23 -= 40;
                            }
                            String timeLeft23 = timeLeft + String.valueOf(check23) + minutes;
                            tvPeriod.setText("Current Period: 2th - 3th");
                            tvEnd.setText(secondThirdEndTime);
                            tvTimeLeft.setText(timeLeft23);
                        }


                        if (thirdPd) {
                            int check3 = thirdEnd - iTime;
                            if (check3 > maxPeriod) {
                                check3 -= 40;
                            }
                            String timeLeft3 = timeLeft + String.valueOf(check3) + minutes;
                            tvPeriod.setText("Current Period: 3rd");
                            tvEnd.setText(thirdEndTime);
                            tvTimeLeft.setText(timeLeft3);
                        }
                        if (thirdHomeroom) {
                            int check3H = thirdHomeroomEnd - iTime;
                            if (check3H > maxPeriod) {
                                check3H -= 40;
                            }
                            String timeLeft3H = timeLeft + String.valueOf(check3H) + minutes;
                            tvPeriod.setText("Current Period: 3th - H");
                            tvEnd.setText(thirdHomeroomEndTime);
                            tvTimeLeft.setText(timeLeft3H);
                        }

                        if (homeroomPd) {
                            int checkH = homeroomEnd - iTime;
                            String timeLeftH = timeLeft + String.valueOf(checkH) + minutes;
                            tvPeriod.setText("Current Period: H");
                            tvEnd.setText(homeroomEndTime);
                            tvTimeLeft.setText(timeLeftH);
                        }
                        if (homeroomFour) {
                            int checkH4 = homeroomFourEnd - iTime;
                            if (checkH4 > maxPeriod) {
                                checkH4 -= 40;
                            }
                            String timeLeftH4 = timeLeft + String.valueOf(checkH4) + minutes;
                            tvPeriod.setText("Current Period: H - 4th");
                            tvEnd.setText(homeroomFourEndTime);
                            tvTimeLeft.setText(timeLeftH4);
                        }

                        if (fourPd) {
                            int check4 = fourEnd - iTime;
                            if (check4 > maxPeriod) {
                                check4 -= 40;
                            }
                            String timeLeft4 = timeLeft + String.valueOf(check4) + minutes;
                            tvPeriod.setText("Current Period: 4th");
                            tvEnd.setText(fourEndTime);
                            tvTimeLeft.setText(timeLeft4);
                        }
                        if (fourFive) {
                            int check45 = fourFiveEnd - iTime;
                            if (check45 > maxPeriod) {
                                check45 -= 40;
                            }
                            String timeLeft45 = timeLeft + String.valueOf(check45) + minutes;
                            tvPeriod.setText("Current Period: 4th - 5th");
                            tvEnd.setText(fourFiveEndTime);
                            tvTimeLeft.setText(timeLeft45);
                        }


                        if (fivePd) {
                            int check5 = fiveEnd - iTime;
                            if (check5 > maxPeriod) {
                                check5 -= 40;
                            }
                            String timeLeft5 = timeLeft + String.valueOf(check5) + minutes;
                            tvPeriod.setText("Current Period: 5th");
                            tvEnd.setText(fiveEndTime);
                            tvTimeLeft.setText(timeLeft5);
                        }
                        if (fiveSix) {
                            int check56 = fiveSixEnd - iTime;
                            if (check56 > maxPeriod) {
                                check56 -= 40;
                            }
                            String timeLeft56 = timeLeft + String.valueOf(check56) + minutes;
                            tvPeriod.setText("Current Period: 5th - 6th");
                            tvEnd.setText(fiveSixEndTime);
                            tvTimeLeft.setText(timeLeft56);
                        }


                        if (sixPd) {
                            int check6 = sixEnd - iTime;
                            if (check6 > maxPeriod) {
                                check6 -= 40;
                            }
                            String timeLeft6 = timeLeft + String.valueOf(check6) + minutes;
                            tvPeriod.setText("Current Period: 6th");
                            tvEnd.setText(sixEndTime);
                            tvTimeLeft.setText(timeLeft6);
                        }
                        if (sixSeven) {
                            int check67 = sixSevenEnd - iTime;
                            if (check67 > maxPeriod) {
                                check67 -= 40;
                            }
                            String timeLeft67 = timeLeft + String.valueOf(check67) + minutes;
                            tvPeriod.setText("Current Period: 6th - 7th");
                            tvEnd.setText(sixSevenEndTime);
                            tvTimeLeft.setText(timeLeft67);
                        }


                        if (sevenPd) {
                            int check7 = sevenEnd - iTime;
                            if (check7 > maxPeriod) {
                                check7 -= 40;
                            }
                            String timeLeft7 = timeLeft + String.valueOf(check7) + minutes;
                            tvPeriod.setText("Current Period: 7th");
                            tvEnd.setText(sevenEndTime);
                            tvTimeLeft.setText(timeLeft7);
                        }
                        if (sevenEight) {
                            int check78 = sevenEightEnd - iTime;
                            if (check78 > maxPeriod) {
                                check78 -= 40;
                            }
                            String timeLeft78 = timeLeft + String.valueOf(check78) + minutes;
                            tvPeriod.setText("Current Period: 7th - 8th");
                            tvEnd.setText(sevenEightEndTime);
                            tvTimeLeft.setText(timeLeft78);
                        }


                        if (eightPd) {
                            int check8 = eightEnd - iTime;
                            if (check8 > maxPeriod) {
                                check8 -= 40;
                            }
                            String timeLeft8 = timeLeft + String.valueOf(check8) + minutes;
                            tvPeriod.setText("Current Period: 8th");
                            tvEnd.setText(eightEndTime);
                            tvTimeLeft.setText(timeLeft8);
                        }
                        if (eightNine) {
                            int check89 = eightNineEnd - iTime;
                            if (check89 > maxPeriod) {
                                check89 -= 40;
                            }
                            String timeLeft89 = timeLeft + String.valueOf(check89) + minutes;
                            tvPeriod.setText("Current Period: 8th - 9th");
                            tvEnd.setText(eightNineEndTime);
                            tvTimeLeft.setText(timeLeft89);
                        }


                        if (ninePd) {
                            int check9 = nineEnd - iTime;
                            if (check9 > maxPeriod) {
                                check9 -= 40;
                            }
                            String timeLeft9 = timeLeft + String.valueOf(check9) + minutes;
                            tvPeriod.setText("Current Period: 9th");
                            tvEnd.setText(nineEndTime);
                            tvTimeLeft.setText(timeLeft9);

                        }
                        if (nineTen) {
                            int check90 = nineTenEnd - iTime;
                            if (check90 > maxPeriod) {
                                check90 -= 40;
                            }
                            String timeLeft90 = timeLeft + String.valueOf(check90) + minutes;
                            tvPeriod.setText("Current Period: 9th - 10th");
                            tvEnd.setText(nineTenEndTime);
                            tvTimeLeft.setText(timeLeft90);
                        }

                        if (tenPd) {
                            int check10 = tenEnd - iTime;
                            if (check10 > maxPeriod) {
                                check10 -= 40;
                            }
                            String timeLeft10 = timeLeft + String.valueOf(check10) + minutes;
                            tvPeriod.setText("Current Period: 10th");
                            tvEnd.setText(tenEndTime);
                            tvTimeLeft.setText(timeLeft10);
                        }
                    } else {
                        tvPeriod.setText("Current Period:");
                        tvEnd.setText(end);
                        tvTimeLeft.setText(timeLeft);
                        tvNextClass.setText(nextClass);
                    }
                    break;
            }
        }
    };
}