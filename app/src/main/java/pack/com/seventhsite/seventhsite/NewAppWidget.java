package pack.com.seventhsite.seventhsite;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;


/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
    varGlobal vg=new varGlobal();
    //InputStream is;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        /*final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);

        }*/
        context.startService(new Intent(context,UpdateService.class));
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);




        //views.setTextViewText(R.id.txtProduk, widgetText);

        // Instruct the widget manager to update the widget
        //appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    public static class UpdateService extends Service {
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            System.out.println("called");

            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        //Your code goes here
                        Context context = getApplicationContext();
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
                        ComponentName thisWidget = new ComponentName(context, NewAppWidget.class);
                        InputStream is;
                        String temp="coba";
                        String gambar="";
                        try {
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpPost httppost = new HttpPost("http://"+varGlobal.ipconnection+"/service_android/listnewitem.php");
                            HttpResponse response = httpclient.execute(httppost);
                            HttpEntity entity = response.getEntity();
                            is = entity.getContent();

                            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
                            StringBuilder sb = new StringBuilder();
                            String line = null;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                            is.close();
                            temp=sb.toString();

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        JSONObject jObject;
                        JSONArray jArray;
                        jObject = new JSONObject(temp);
                        jArray = jObject.getJSONArray("item");
                        for (int i = 0; i < jArray.length(); i++) {
                            try {
                                if(i==0)
                                {
                                    JSONObject oneObject = jArray.getJSONObject(i);
                                    System.out.println(oneObject.getString("ProdName"));

                                    DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
                                    DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

                                    formatRp.setCurrencySymbol("Rp. ");
                                    formatRp.setMonetaryDecimalSeparator(',');
                                    formatRp.setGroupingSeparator('.');

                                    kursIndonesia.setDecimalFormatSymbols(formatRp);


                                    remoteViews.setTextViewText(R.id.txtProduk, oneObject.getString("ProdName"));
                                    String harga=kursIndonesia.format(Integer.parseInt(oneObject.getString("Price")) * ((100-Double.parseDouble(oneObject.getString("Disc")))/100));
                                    //CharSequence harga2="testing aja";
                                    remoteViews.setTextViewText(R.id.txtHarga,harga);

                                    gambar=oneObject.getString("ThumbnailFile");
                                    int ratingnya=Integer.parseInt(oneObject.getString("Rating"));
                                    if(ratingnya==0)
                                    {
                                        remoteViews.setImageViewResource(R.id.imgRate,R.drawable.rate0);
                                    }
                                    else if(ratingnya==1)
                                    {
                                        remoteViews.setImageViewResource(R.id.imgRate,R.drawable.rate1);
                                    }
                                    else if(ratingnya==2)
                                    {
                                        remoteViews.setImageViewResource(R.id.imgRate,R.drawable.rate2);
                                    }
                                    else if(ratingnya==3)
                                    {
                                        remoteViews.setImageViewResource(R.id.imgRate,R.drawable.rate3);
                                    }
                                    else if(ratingnya==4)
                                    {
                                        remoteViews.setImageViewResource(R.id.imgRate,R.drawable.rate4);
                                    }
                                    else if(ratingnya==5)
                                    {
                                        remoteViews.setImageViewResource(R.id.imgRate,R.drawable.rate5);
                                    }
                                }
                            } catch (Exception e) {
                                //dialog.dismiss();
                                //Toast.makeText(DetailItemActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                System.out.println(e.toString());
                            }

                            //gambar
                            AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
                            HttpGet getRequest = new HttpGet("http://"+varGlobal.ipconnection+"/service_android/sitefiles/image/products/" + gambar);
                            try {
                                HttpResponse response = client.execute(getRequest);
                                final int statusCode = response.getStatusLine().getStatusCode();
                                if (statusCode != HttpStatus.SC_OK) {
                                    Log.w("ImageDownloader", "Error " + statusCode
                                            + " while retrieving bitmap from ");
                                }
                                HttpEntity entity = response.getEntity();
                                if (entity != null) {
                                    InputStream inputStream = null;
                                    try {
                                        inputStream = entity.getContent();
                                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                        remoteViews.setImageViewBitmap(R.id.imgItem,bitmap);
                                    } finally {
                                        if (inputStream != null) {
                                            inputStream.close();
                                        }
                                        entity.consumeContent();
                                    }
                                }
                            } catch (Exception e) {
                                // Could provide a more explicit error message for IOException or
                                // IllegalStateException
                                getRequest.abort();
                                //Log.w("ImageDownloader", "Error while retrieving bitmap from " + url);
                            }
                            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            thread.start();


            /*AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
            int[] appWidgetIds =intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);





            if (appWidgetIds.length > 0) {
                for (int widgetId : appWidgetIds) {
                    RemoteViews views = new RemoteViews(getPackageName(), R.layout.new_app_widget);
                    views.setTextViewText(R.id.txtProduk, "Text");
                    appWidgetManager.updateAppWidget(widgetId, views);
                }
                stopSelf(startId);
            }
            // Instruct the widget manager to update the widget

            */
            stopSelf(startId);
            return START_STICKY;
        }




        @Override
        public IBinder onBind(Intent intent) {
            // We don't need to bind to this service
            return null;
        }
    }
}


