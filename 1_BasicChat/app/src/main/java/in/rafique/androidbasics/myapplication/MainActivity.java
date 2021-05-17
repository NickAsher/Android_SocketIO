package in.rafique.androidbasics.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private Socket socket ;
    Context context ;
    private static final String LOG_TAG = "MainActivity => " ;
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


        socket.connect() ;


        socket.on("whatsapp_msg", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(LOG_TAG, args[0].toString()) ;
                    }
                });
            }
        }) ;
    }
}