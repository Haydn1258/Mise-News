package com.example.misenews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import android.widget.Button;
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
    String key = "vSrFiusUpPPUxpFdDrsj7gim6dcWzgMPqXCff3eiFjS3psE%2BnEJLZEuuUA7uqEFvQJA6HIJtK5oHxokQxwKuEQ%3D%3D";

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

    TextView tvLocation, tvDateTime, tvStatus, tvPm10Status, tvPm10concentration, tvPm25status, tvPm25concentration
            , tvNo2status, tvNo2concentration, tvO3status, tvO3concentration, tvCostatus, tvCoconcentration
            , tvSo2status, tvSo2concentration, tvDetailDateTime, tvDetailStation, tvDetailKhaiValue, tvDetailKhaiGrade;

    ImageView imgvStatus, imgvPm10, imgvPm25, imgvNo2, imgvO3, imgvCo, imgvSo2, imgvCached, imgvNav, drawer_imgvExit;

    Button drawer_btnExit;

    ConstraintLayout drawer;

    LinearLayout linearLayoutScrView;

    DrawerLayout drawer_layout;

    LatLng latLng = new LatLng(latitude, longitude);

    Window window;


    static Tm128 tm128;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.enableDefaults();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocation = (TextView)findViewById(R.id.txtvLocation);
        tvDateTime = (TextView)findViewById(R.id.txtvDateTime);
        tvStatus = (TextView)findViewById(R.id.txtvStatus);
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
        imgvNav = (ImageView) findViewById(R.id.imgvNav);
        drawer_imgvExit = (ImageView) findViewById(R.id.drawer_imgvExit);

        drawer_btnExit = (Button) findViewById(R.id.drawer_btnExit);

        drawer_layout =(DrawerLayout) findViewById(R.id.drawer_layout);

        drawer = (ConstraintLayout) findViewById(R.id.drawer);
        window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission();
        }
        getAddr();
        getData();
        if (!stationName.equals("")){
            setData();
        }else {
            latitude=37.5670135;
            longitude=126.9783740;
            subLocality = null;
            thoroughfare = "태평로1가";
            admin = "서울특별시";
            locationArray[locationArray.length-3] = "중구";
            locationArray[locationArray.length-2] = "태평로1가";
            getData();
            setData();
        }


        imgvCached.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                imgvCached.setClickable(false);
                if (!checkLocationServicesStatus()) {
                    showDialogForLocationServiceSetting();
                }else {
                    checkRunTimePermission();
                }
                getAddr();
                getData();
                if (!stationName.equals("")){
                    setData();
                }else {
                    latitude=37.5670135;
                    longitude=126.9783740;
                    subLocality = null;
                    thoroughfare = "태평로1가";
                    admin = "서울특별시";
                    locationArray[locationArray.length-3] = "중구";
                    locationArray[locationArray.length-2] = "태평로1가";
                    getData();
                    setData();
                }

                imgvCached.setClickable(true);
            }
        });

        imgvNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.openDrawer(drawer);
            }
        });

        drawer_imgvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.closeDrawer(drawer);
            }
        });

        drawer_btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.closeDrawer(drawer);
            }
        });

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
        stationName ="";
        instationName = false;
        try{

            URL url;
            url = new URL("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt?serviceKey="+ key+ "&numOfRows=10&pageNo=1&umdName="
                    +thoroughfare);


            InputStream is= url.openStream();
            Log.d("aaaf",admin+"aa");


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


                url = new URL("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt?serviceKey="+ key+ "&numOfRows=10&pageNo=1&umdName="
                        +thoroughfare.replaceAll("[0-9]", ""));


                InputStream is= url.openStream();
                Log.d("aaaf", thoroughfare.replaceAll("[0-9]", "")+"12aa");


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
            tvLocation.setText(e.toString());
        }



        try{

            URL url = new URL("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?serviceKey="+ key+
                    "&numOfRows=1&pageNo=1&stationName="+stationName+"&dataTerm=DAILY&ver=1.3");
            Log.d("asdff123",stationName);

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
                        else if(xpp.getName().equals("pm10Grade1h")){
                            inPm10Grade = true;
                        }
                        else if(xpp.getName().equals("pm25Grade1h")){
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
    }

    public void setData(){
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm a", new Locale("en", "US"));
        String nowTime = simpleDate.format(mDate);

        try{
            tvDateTime.setText(nowTime);

            if (!pm10Grade.replace(" ","").equals("")){
                tvPm10Status.setText(getStatus(Integer.parseInt(pm10Grade)));
            }else {
                tvPm10Status.setText("");
            }
            tvPm10concentration.setText(pm10value+" ㎍/m³");

            if (!pm25Grade.replace(" ","").equals("")){
                tvPm25status.setText(getStatus(Integer.parseInt(pm25Grade)));
            }else {
                tvPm25status.setText("");
            }

            tvPm25concentration.setText(pm25value+" ㎍/m³");

            if (!no2Grade.replace(" ","").equals("")){
                tvNo2status.setText(getStatus(Integer.parseInt(no2Grade)));
            }else {
                tvNo2status.setText("");
            }
            tvNo2concentration.setText(no2value+" ppm");

            if (!o3Grade.replace(" ","").equals("")){
                tvO3status.setText(getStatus(Integer.parseInt(o3Grade)));
            }else {
                tvO3status.setText("");
            }

            tvO3concentration.setText(o3value+" ppm");

            if (!coGrade.replace(" ","").equals("")){
                tvCostatus.setText(getStatus(Integer.parseInt(coGrade)));
            }else {
                tvCostatus.setText("");
            }

            tvCoconcentration.setText(covalue+" ppm");

            if (!so2Grade.replace(" ","").equals("")){
                tvSo2status.setText(getStatus(Integer.parseInt(so2Grade)));
            }else {
                tvSo2status.setText("");
            }

            tvSo2concentration.setText(so2value+" ppm");

            tvDetailDateTime.setText("업데이트 시간 : "+dateTime);
            tvDetailStation.setText("측정소 이름 : "+stationName);
            tvDetailKhaiValue.setText("통합지수 값 : "+khaiValue+" unit");
            if (!(khaiGrade.replace(" ","").equals(""))){
                Log.d("aattaa",khaiGrade);
                tvDetailKhaiGrade.setText("통합지수 상태 : " + getStatus(Integer.parseInt(khaiGrade)));

            }else {
                tvDetailKhaiGrade.setText("통합지수 상태 : ");

            }

            tvLocation.setText(locationArray[locationArray.length-3] + " " + locationArray[locationArray.length-2]);


        }catch (Exception e){
            Log.d("aattee", e.toString());

        }
        setImage();
    }

    public void setImage(){
        try{
            if (pm10Grade.replace(" ","").equals("") && pm25Grade.replace(" ","").equals("")){
                tvStatus.setText("정보없음");
            }else if (pm10Grade.replace(" ","").equals("")){
                tvStatus.setText(getStatus(Integer.parseInt(pm25Grade)));
                setColor(pm25Grade);
                changeImage(imgvStatus, pm25Grade);
            }else if (pm25Grade.replace(" ","").equals("")){
                tvStatus.setText(getStatus(Integer.parseInt(pm10Grade)));
                setColor(pm10Grade);
                changeImage(imgvStatus, pm10Grade);
            }else {
                if(Integer.parseInt(pm10Grade)>=Integer.parseInt(pm25Grade)){
                    tvStatus.setText(getStatus(Integer.parseInt(pm10Grade)));
                    setColor(pm10Grade);
                    changeImage(imgvStatus, pm10Grade);
                }else{
                    tvStatus.setText(getStatus(Integer.parseInt(pm25Grade)));
                    setColor(pm25Grade);
                    changeImage(imgvStatus, pm25Grade);
                }
            }

            changeImage(imgvCo,coGrade);
            changeImage(imgvNo2,no2Grade);
            changeImage(imgvO3,o3Grade);
            changeImage(imgvPm10, pm10Grade);
            changeImage(imgvPm25, pm25Grade);
            changeImage(imgvSo2, so2Grade);
        }catch (Exception e){
            Log.d("Exception", e.toString());
        }
    }

    public void setColor(String status){
        switch (status){
            case "1":
                window.setStatusBarColor(getColor(R.color.colorLightBlue));
                drawer_layout.setBackgroundResource(R.drawable.main_background_blue);
                drawer.setBackgroundColor(getColor(R.color.colorLightBlue));
                break;
            case "2":
                window.setStatusBarColor(getColor(R.color.colorLightGreen));
                drawer_layout.setBackgroundResource(R.drawable.main_background_green);
                drawer.setBackgroundColor(getColor(R.color.colorLightGreen));
                break;
            case "3":
                window.setStatusBarColor(getColor(R.color.colorAmber));
                drawer_layout.setBackgroundResource(R.drawable.main_backgound_orange);
                drawer.setBackgroundColor(getColor(R.color.colorAmber));
                break;
            default:
                window.setStatusBarColor(getColor(R.color.colorDeepOrange));
                drawer_layout.setBackgroundResource(R.drawable.main_background_red);
                drawer.setBackgroundColor(getColor(R.color.colorDeepOrange));
                break;
        }

    }

    public void changeImage(ImageView imgv, String status){
        switch (status){
            case "1":
                imgv.setImageResource(R.drawable.outline_sentiment_very_satisfied_white_36);
                break;
            case "2":
                imgv.setImageResource(R.drawable.outline_sentiment_satisfied_white_36);
                break;
            case "3":
                imgv.setImageResource(R.drawable.outline_sentiment_dissatisfied_white_36);
                break;
            default:
                imgv.setImageResource(R.drawable.outline_sentiment_very_dissatisfied_white_36);
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
