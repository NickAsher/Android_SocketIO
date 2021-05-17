package in.rafique.androidbasics.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Date;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private Socket socket ;
    Context context ;
    private static final String LOG_TAG = "MainActivity => " ;

    EditText chatEditText ;
    ImageButton sendMsgButton ;
    LinearLayout chatContainer ;

    {
        try{
            socket = IO.socket("https://www.chat.rafique.in") ;
        }catch (Exception e){

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this ;


        getReferences() ;
        setupSocketIO() ;

    }

    private void getReferences(){
        chatEditText = findViewById(R.id.activityMain_EditText_Msg) ;
        sendMsgButton = findViewById(R.id.activityMain_Btn_SendMsg) ;
        chatContainer = findViewById(R.id.activityMain_LnLt_ChatContainer) ;
    }


    private void setupSocketIO(){
        socket.connect() ;


        socket.on("whatsapp_msg", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(LOG_TAG, args[0].toString()) ;
                        showIncomingMessage((JSONObject)(args[0]));
                    }
                });
            }
        }) ;
    }

    private void showIncomingMessage(JSONObject jsonObject){
        try{
            String senderName = jsonObject.getString("senderName") ;
            String msg = jsonObject.getString("chatMessage") ;
            String time = jsonObject.getString("time") ;

            if(!senderName.equals(android.os.Build.MANUFACTURER + android.os.Build.MODEL)){

                TextView textView = new TextView(this) ;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) ;
                params.setMargins(32, 32, 32, 32);
                textView.setLayoutParams(params);
                textView.setText(senderName + " \n " + msg);
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundColor(Color.WHITE);
                textView.setPadding(24,24,24,24);
                textView.setGravity(Gravity.LEFT| Gravity.START);

                chatContainer.addView(textView);
            }else{

                TextView textView = new TextView(this) ;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) ;
                params.gravity = Gravity.RIGHT ;
                params.setMargins(32, 32, 32, 32);
                textView.setLayoutParams(params);
                textView.setText(msg);
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundColor(Color.parseColor("#E1FFC7"));
                textView.setPadding(24,24,24,24);

                chatContainer.addView(textView);
            }






        }catch (Exception e){
            Log.e(LOG_TAG, jsonObject.toString()) ;
            Log.e(LOG_TAG, e.toString()) ;
        }

    }

    public void onSentClicked(View v){
        String msg = chatEditText.getText().toString() ;
        chatEditText.getText().clear();


//        TextView textView = new TextView(this) ;
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) ;
//        params.gravity = Gravity.RIGHT ;
//        params.setMargins(32, 32, 32, 32);
//        textView.setLayoutParams(params);
//        textView.setText(msg);
//        textView.setTextColor(Color.BLACK);
//        textView.setBackgroundColor(Color.parseColor("#E1FFC7"));
//        textView.setPadding(24,24,24,24);
//
//        chatContainer.addView(textView);

        try{
            JSONObject jsonObject = new JSONObject() ;
            jsonObject.put("senderName", android.os.Build.MANUFACTURER + android.os.Build.MODEL) ;
            jsonObject.put("type", "text") ;
            jsonObject.put("chatMessage", msg) ;
            jsonObject.put("time", new Date().getTime()) ;

            socket.emit("whatsapp_msg", jsonObject, new Ack() {
                @Override
                public void call(Object... args) {

                }
            }) ;
        }catch (Exception e){
            Log.e(LOG_TAG, "Failed to send message" + e.toString()) ;
        }


    }
}