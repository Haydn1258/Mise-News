package com.example.misenews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Tm128;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private GpsTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    String key = "mjfRpO4X3kZj357lQFtFSps%2FRAy1g%2FgZXIHYXcS7SFQN0uTpLkoi%2FWqc8fZvb3HrkpVqiGQzXdt7kSkNXRkaVQ%3D%3D";

    Boolean instationName = false, inTmX = false, inTmY = false, inDateTime=false, inSo2Value=false, inCoValue= false, inO3Value=false
            , inNo2Value= false, inPm10Value= false, inPm25Value= false, inSo2Grade= false, inCoGrade = false, inO3Grade = false
            , inNo2Grade = false, inPm10Grade = false, inPm25Grade=false, inKhaiValue= false, inKhaiGrade = false, inAdmin;
    String stationName = "", tmX = "", tmY = "", dateTime="", so2value="", covalue="", o3value="", no2value=""
            , pm10value="", pm25value="", so2Grade = "", coGrade = "", o3Grade="", no2Grade="", pm10Grade="", pm25Grade="",
            khaiValue = "",khaiGrade = "", location = "" ;
    String[] locationArray;

    Double latitude=37.5670135, longitude=126.9783740;

    String locality, admin;
    String subLocality;
    String thoroughfare;

    TextView tvLocation, tvDateTime, tvStatus, tvGuide, tvPm10Status, tvPm10concentration, tvPm25status, tvPm25concentration
            , tvNo2status, tvNo2concentration, tvO3status, tvO3concentration, tvCostatus, tvCoconcentration
            , tvSo2status, tvSo2concentration, tvDetailDateTime, tvDetailStation, tvDetailKhaiValue, tvDetailKhaiGrade;

    ImageView imgvStatus, imgvPm10, imgvPm25, imgvNo2, imgvO3, imgvCo, imgvSo2, imgvCached;

    ConstraintLayout constraintLayoutMain,constraintLayoutDetail;

    LinearLayout linearLayoutScrView;

    LatLng latLng = new LatLng(latitude, longitude);

    static Tm128 tm128;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.enableDefaults();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocation = (TextView)findViewById(R.id.txtvLocation);
        tvDateTime = (TextView)findViewById(R.id.txtvDateTime);
        tvStatus = (TextView)findViewById(R.id.txtvStatus);
        tvGuide =  (TextView)findViewById(R.id.txtvGuide);
        tvPm10Status =  (TextView)findViewById(R.id.txtvPm10status);
        tvPm10concentration = (TextView)findViewById(R.id.txtvPm10concentration);
        tvPm25status = (TextView)findViewById(R.id.txtvPm25status);
        tvPm25concentration = (TextView)findViewById(R.id.txtvPm25concentration);
        tvNo2status = (TextView)findViewById(R.id.txtvNo2status);
        tvNo2concentration = (TextView)findViewById(R.id.txtvNo2concentration);
        tvO3status = (TextView)findViewById(R.id.txtvO3status);
        tvO3concentration = (TextView)findViewById(R.id.txtvO3concentration);
        tvCostatus = (TextView)findViewById(R.id.txtvCostatus);
        tvCoconcentration = (TextView)findViewById(R.id.txtvCoconcentration);
        tvSo2status = (TextView)findViewById(R.id.txtvSo2status);
        tvSo2concentration = (TextView)findViewById(R.id.txtvSo2concentration);
        tvDetailDateTime = (TextView)findViewById(R.id.txtvDetailDateTime);
        tvDetailStation = (TextView)findViewById(R.id.txtvDetailStation);
        tvDetailKhaiValue = (TextView)findViewById(R.id.txtvDetailKhaiValue);
        tvDetailKhaiGrade = (TextView)findViewById(R.id.txtvDetailKhaiGrade);

        imgvStatus = (ImageView) findViewById(R.id.imgvStatus);
        imgvPm10 = (ImageView) findViewById(R.id.imgvPm10);
        imgvPm25 = (ImageView) findViewById(R.id.imgvPm25);
        imgvNo2 = (ImageView) findViewById(R.id.imgvNo2);
        imgvO3 = (ImageView) findViewById(R.id.imgvO3);
        imgvCo = (ImageView) findViewById(R.id.imgvCo);
        imgvSo2 = (ImageView) findViewById(R.id.imgvSo2);
        imgvCached = (ImageView) findViewById(R.id.imgvCached);

        constraintLayoutMain =(ConstraintLayout)findViewById(R.id.constraintLayoutMain);
        constraintLayoutDetail = (ConstraintLayout)findViewById(R.id.constraintLayoutDetail);

        linearLayoutScrView = (LinearLayout)findViewById(R.id.linearLayoutScrView);



        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission();
        }
        getAddr();
        getData();

        imgvCached.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (!checkLocationServicesStatus()) {
                    showDialogForLocationServiceSetting();
                }else {
                    checkRunTimePermission();
                }
                getAddr();
                getData();
            }
        });




        /*bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getAddr();
                getData();

                Log.d("aaafdd", locality+ " "+subLocality+" "+thoroughfare);

            }
        });*/
    }


    public void getAddr(){
        gpsTracker = new GpsTracker(MainActivity.this);
        double addratitude = gpsTracker.getLatitude();
        double addrlongitude = gpsTracker.getLongitude();
        if (addratitude==0.0 && addrlongitude==0.0){

        }else{
            latitude = addratitude;
            longitude = addrlongitude;
        }

        final Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;

        try {

            list = geocoder.getFromLocation(latitude,longitude, 10); // 얻어올 값의 개수

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list != null) {
            if (list.size()==0) {

            } else {
                admin = list.get(0).getAdminArea();
                locality = list.get(0).getLocality();
                subLocality = list.get(0).getLocality();
                thoroughfare = list.get(0).getThoroughfare();
                location = list.get(0).getAddressLine(0);
                locationArray = location.split(" ");


                Log.d("dddd",list.toString());
            }
        }
    }

    public void getData(){


        try{
            Log.d("aaaf",admin+"aa");
            URL url;
            url = new URL("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt?serviceKey="+ key+ "&numOfRows=10&pageNo=1&umdName="
                    +thoroughfare);


            InputStream is= url.openStream();


            //XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            //XmlPullParser parser = parserCreator.newPullParser();

            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser xpp= factory.newPullParser();

            xpp.setInput(new InputStreamReader(is, "UTF-8"));

            //parser.setInput(new InputStreamReader(url.openStream(), "UTF-8"));

            int parserEvent = xpp.getEventType();
            String addradmin = "";

            while (parserEvent != XmlPullParser.END_DOCUMENT){
                switch(parserEvent){
                    case XmlPullParser.START_TAG:
                        if(xpp.getName().equals("sidoName")){
                            xpp.next();
                            addradmin = xpp.getText();
                            tvLocation.setText(addradmin);
                        }
                        else if(xpp.getName().equals("tmX")){
                            inTmX = true;
                        }
                        else if (xpp.getName().equals("tmY")){
                            inTmY = true;
                        }

                        break;
                    case XmlPullParser.TEXT:

                        if (inTmX){
                            if (addradmin.equals(admin)){
                                tmX = xpp.getText();
                            }
                            inTmX = false;

                        }
                        else if (inTmY){
                            if (addradmin.equals(admin)){
                                tmY = xpp.getText();
                            }
                            inTmY= false;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                parserEvent = xpp.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if (tmX.replace(" ", "").equals("")){
            try{
                URL url;
                thoroughfare.replace("[0-9]", "");

                url = new URL("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt?serviceKey="+ key+ "&numOfRows=10&pageNo=1&umdName="
                        +thoroughfare);


                InputStream is= url.openStream();
                Log.d("aaaf",thoroughfare+"12aa");


                //XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
                //XmlPullParser parser = parserCreator.newPullParser();

                XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
                XmlPullParser xpp= factory.newPullParser();

                xpp.setInput(new InputStreamReader(is, "UTF-8"));

                //parser.setInput(new InputStreamReader(url.openStream(), "UTF-8"));

                int parserEvent = xpp.getEventType();
                String addradmin = "";

                while (parserEvent != XmlPullParser.END_DOCUMENT){
                    switch(parserEvent){
                        case XmlPullParser.START_TAG:
                            if(xpp.getName().equals("sidoName")){
                                xpp.next();
                                addradmin = xpp.getText();
                                tvLocation.setText(addradmin);
                            }
                            else if(xpp.getName().equals("tmX")){
                                inTmX = true;
                            }
                            else if (xpp.getName().equals("tmY")){
                                inTmY = true;
                            }

                            break;
                        case XmlPullParser.TEXT:

                            if (inTmX){
                                if (addradmin.equals(admin)){
                                    tmX = xpp.getText();
                                }
                                inTmX = false;

                            }
                            else if (inTmY){
                                if (addradmin.equals(admin)){
                                    tmY = xpp.getText();
                                }
                                inTmY= false;
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                    }
                    parserEvent = xpp.next();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        try{

            Log.d("asdf", tmX+"  "+ tmY);
            URL url = new URL("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList?serviceKey="+ key+
                    "&tmX="+tmX+"&tmY="+tmY);

            InputStream is= url.openStream();
            //XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            //XmlPullParser parser = parserCreator.newPullParser();

            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser xpp= factory.newPullParser();

            xpp.setInput(new InputStreamReader(is, "UTF-8"));

            int parserEvent = xpp.getEventType();

            while (parserEvent != XmlPullParser.END_DOCUMENT){
                switch(parserEvent){
                    case XmlPullParser.START_TAG:
                        if(xpp.getName().equals("stationName")){
                            instationName = true;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (instationName && stationName.equals("")){
                            stationName = xpp.getText();
                            instationName = false;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                parserEvent = xpp.next();
            }

        }catch (Exception e){
            e.printStackTrace();
        }



        try{

            URL url = new URL("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?serviceKey="+ key+
                    "&numOfRows=1&pageNo=1&stationName="+stationName+"&dataTerm=DAILY&ver=1.3");


            InputStream is= url.openStream();

            //XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            //XmlPullParser parser = parserCreator.newPullParser();

            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser xpp= factory.newPullParser();

            xpp.setInput(new InputStreamReader(is, "UTF-8"));

            int parserEvent = xpp.getEventType();

            while (parserEvent != XmlPullParser.END_DOCUMENT){
                switch(parserEvent){
                    case XmlPullParser.START_TAG:
                        if(xpp.getName().equals("dataTime")){
                            inDateTime = true;
                        }
                        else  if(xpp.getName().equals("so2Value")){
                            inSo2Value = true;
                        }
                        else if(xpp.getName().equals("coValue")){
                            inCoValue = true;
                        }
                        else if(xpp.getName().equals("o3Value")){
                            inO3Value = true;
                        }
                        else if(xpp.getName().equals("no2Value")){
                            inNo2Value = true;
                        }
                        else if(xpp.getName().equals("pm10Value")){
                            inPm10Value = true;
                        }
                        else if(xpp.getName().equals("pm25Value")){
                            inPm25Value = true;
                        }
                        else if(xpp.getName().equals("so2Grade")){
                            inSo2Grade = true;
                        }
                        else if(xpp.getName().equals("coGrade")){
                            inCoGrade = true;
                        }
                        else if(xpp.getName().equals("o3Grade")){
                            inO3Grade = true;
                        }
                        else if(xpp.getName().equals("no2Grade")){
                            inNo2Grade = true;
                        }
                        else if(xpp.getName().equals("pm10Grade")){
                            inPm10Grade = true;
                        }
                        else if(xpp.getName().equals("pm25Grade")){
                            inPm25Grade = true;
                        }
                        else if(xpp.getName().equals("khaiValue")){
                            inKhaiValue = true;
                        }
                        else if(xpp.getName().equals("khaiGrade")){
                            inKhaiGrade = true;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (inDateTime){
                            dateTime =  xpp.getText();
                            inDateTime = false;
                        }
                        else if (inSo2Value){
                            so2value =  xpp.getText();
                            inSo2Value = false;
                        }
                        else if (inCoValue){
                            covalue =  xpp.getText();
                            inCoValue = false;
                        }
                        else if (inO3Value){
                            o3value =  xpp.getText();
                            inO3Value = false;
                        }
                        else if (inNo2Value){
                            no2value =  xpp.getText();
                            inNo2Value = false;
                        }
                        else if (inPm10Value){
                            pm10value =  xpp.getText();
                            inPm10Value = false;
                        }
                        else if (inPm25Value){
                            pm25value =  xpp.getText();
                            inPm25Value = false;
                        }
                        else if (inSo2Grade){
                            so2Grade =  xpp.getText();
                            inSo2Grade = false;
                        }
                        else if (inCoGrade){
                            coGrade =  xpp.getText();
                            inCoGrade = false;
                        }
                        else if (inO3Grade){
                            o3Grade =  xpp.getText();
                            inO3Grade = false;
                        }
                        else if (inNo2Grade){
                            no2Grade =  xpp.getText();
                            inNo2Grade = false;
                        }
                        else if (inPm10Grade){
                            pm10Grade =  xpp.getText();
                            inPm10Grade = false;
                        }
                        else if (inPm25Grade){
                            pm25Grade =  xpp.getText();
                            inPm25Grade = false;
                        }
                        else if (inKhaiValue){
                            khaiValue =  xpp.getText();
                            inKhaiValue = false;
                        }
                        else if (inKhaiGrade){
                            khaiGrade =  xpp.getText();
                            inKhaiGrade = false;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                parserEvent = xpp.next();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        setData();
    }

    public void setData(){
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm a", new Locale("en", "US"));
        String nowTime = simpleDate.format(mDate);

        if (subLocality==null){
            tvLocation.setText(thoroughfare);
        }else{
            tvLocation.setText(locationArray[locationArray.length-3] + " " + locationArray[locationArray.length-2]);
        }
        tvDateTime.setText(nowTime);
        if(Integer.parseInt(pm10Grade)>Integer.parseInt(pm25Grade)){
            tvStatus.setText(getStatus(Integer.parseInt(pm10Grade)));
        }else{
            tvStatus.setText(getStatus(Integer.parseInt(pm25Grade)));
        }
        tvPm10Status.setText(getStatus(Integer.parseInt(pm10Grade)));
        tvPm10concentration.setText(pm10value+" ㎍/m³");

        tvPm25status.setText(getStatus(Integer.parseInt(pm25Grade)));
        tvPm25concentration.setText(pm25value+" ㎍/m³");

        tvNo2status.setText(getStatus(Integer.parseInt(no2Grade)));
        tvNo2concentration.setText(no2value+" ppm");

        tvO3status.setText(getStatus(Integer.parseInt(o3Grade)));
        tvO3concentration.setText(o3value+" ppm");

        tvCostatus.setText(getStatus(Integer.parseInt(coGrade)));
        tvCoconcentration.setText(covalue+" ppm");

        tvSo2status.setText(getStatus(Integer.parseInt(so2Grade)));
        tvSo2concentration.setText(so2value+" ppm");

        tvDetailDateTime.setText("업데이트 시간 : "+dateTime);
        tvDetailStation.setText("측정소 이름 : "+stationName);
        tvDetailKhaiValue.setText("통합지수 값 : "+khaiValue+" unit");
        tvDetailKhaiGrade.setText("통합지수 상태 : " + getStatus(Integer.parseInt(khaiGrade)));
        setImage();

    }

    public void setImage(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if(Integer.parseInt(pm10Grade)>=Integer.parseInt(pm25Grade)){
            tvStatus.setText(getStatus(Integer.parseInt(pm10Grade)));


            if (pm10Grade.equals("1")){
                Log.d("aasdf","hah");

                imgvStatus.setBackground(getDrawable(R.drawable.outline_sentiment_very_satisfied_white_36));
                constraintLayoutMain.setBackgroundColor(getColor(R.color.colorBlue));
                linearLayoutScrView.setBackground(getDrawable(R.drawable.rounded_blue));
                constraintLayoutDetail.setBackground(getDrawable(R.drawable.rounded_blue));
                window.setStatusBarColor(getColor(R.color.colorBlue));

            }else if (pm10Grade.equals("2")){

                imgvStatus.setBackground(getDrawable(R.drawable.outline_sentiment_satisfied_white_36));
                constraintLayoutMain.setBackgroundColor(getColor(R.color.colorGreen));
                linearLayoutScrView.setBackground(getDrawable(R.drawable.rounded_green));
                constraintLayoutDetail.setBackground(getDrawable(R.drawable.rounded_green));
                window.setStatusBarColor(getColor(R.color.colorGreen));

            }else if(pm10Grade.equals("3")){

                imgvStatus.setBackground(getDrawable(R.drawable.outline_sentiment_dissatisfied_white_36));
                constraintLayoutMain.setBackgroundColor(getColor(R.color.colorOrange));
                linearLayoutScrView.setBackground(getDrawable(R.drawable.rounded_orange));
                constraintLayoutDetail.setBackground(getDrawable(R.drawable.rounded_orange));
                window.setStatusBarColor(getColor(R.color.colorOrange));

            }else if (pm10Grade.equals("4")){
                imgvStatus.setBackground(getDrawable(R.drawable.outline_sentiment_very_dissatisfied_white_36));
                constraintLayoutMain.setBackgroundColor(getColor(R.color.colorRed));
                linearLayoutScrView.setBackground(getDrawable(R.drawable.rounded_red));
                constraintLayoutDetail.setBackground(getDrawable(R.drawable.rounded_red));
                window.setStatusBarColor(getColor(R.color.colorRed));
            }
        }else{
            tvStatus.setText(getStatus(Integer.parseInt(pm25Grade)));
            if (pm25Grade.equals("1")){
                Log.d("aasdf","hah");

                imgvStatus.setBackground(getDrawable(R.drawable.outline_sentiment_very_satisfied_white_36));
                constraintLayoutMain.setBackgroundColor(getColor(R.color.colorBlue));
                linearLayoutScrView.setBackground(getDrawable(R.drawable.rounded_blue));
                constraintLayoutDetail.setBackground(getDrawable(R.drawable.rounded_blue));
                window.setStatusBarColor(getColor(R.color.colorBlue));

            }else if (pm25Grade.equals("2")){

                imgvStatus.setBackground(getDrawable(R.drawable.outline_sentiment_satisfied_white_36));
                constraintLayoutMain.setBackgroundColor(getColor(R.color.colorGreen));
                linearLayoutScrView.setBackground(getDrawable(R.drawable.rounded_green));
                constraintLayoutDetail.setBackground(getDrawable(R.drawable.rounded_green));
                window.setStatusBarColor(getColor(R.color.colorGreen));

            }else if(pm25Grade.equals("3")){

                imgvStatus.setBackground(getDrawable(R.drawable.outline_sentiment_dissatisfied_white_36));
                constraintLayoutMain.setBackgroundColor(getColor(R.color.colorOrange));
                linearLayoutScrView.setBackground(getDrawable(R.drawable.rounded_orange));
                constraintLayoutDetail.setBackground(getDrawable(R.drawable.rounded_orange));
                window.setStatusBarColor(getColor(R.color.colorOrange));

            }else if (pm25Grade.equals("4")){
                imgvStatus.setBackground(getDrawable(R.drawable.outline_sentiment_very_dissatisfied_white_36));
                constraintLayoutMain.setBackgroundColor(getColor(R.color.colorRed));
                linearLayoutScrView.setBackground(getDrawable(R.drawable.rounded_red));
                constraintLayoutDetail.setBackground(getDrawable(R.drawable.rounded_red));
                window.setStatusBarColor(getColor(R.color.colorRed));
            }
        }
        changeImage(imgvCo,coGrade);
        changeImage(imgvNo2,no2Grade);
        changeImage(imgvO3,o3Grade);
        changeImage(imgvPm10, pm10Grade);
        changeImage(imgvPm25, pm25Grade);
        changeImage(imgvSo2, so2Grade);

    }

    public void changeImage(ImageView imgv, String status){
        switch (status){
            case "1":
                imgv.setBackground(getDrawable(R.drawable.outline_sentiment_very_satisfied_white_36));
                break;
            case "2":
                imgv.setBackground(getDrawable(R.drawable.outline_sentiment_satisfied_white_36));
                break;
            case "3":
                imgv.setBackground(getDrawable(R.drawable.outline_sentiment_dissatisfied_white_36));
                break;
            case "4":
                imgv.setBackground(getDrawable(R.drawable.outline_sentiment_very_dissatisfied_white_36));
                break;
        }
    }

    public String getStatus(Integer num){
        switch(num){
            case 1:
                return "좋음";
            case 2:
                return "보통";
            case 3:
                return "나쁨";
            case 4:
                return "매우나쁨";
        }
        return "";
    }


    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //위치 값을 가져올 수 있음
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해줌. 2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();


                }else {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음



        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }



    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
