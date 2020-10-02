package com.example.stresstestencryption;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class utils {
    @SuppressLint("StaticFieldLeak")
    static Context outterContext;
    static String logPath;

    public static void init(Context context)
    {
        outterContext = context;
        logPath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Android"+File.separator+"data"+File.separator+outterContext.getPackageName()+"files"+File.separator+"Logs"+File.separator;

        File logDir = new File(logPath);
        if(!logDir.exists())
        {
            if(logDir.mkdirs())
            {
                Log.i("LOGS", "Log Directory Created");
            }
            else
            {
                Log.i("LOGS", "Logs Directory Cannot Be Created");
            }
        }
    }

    public static void log(String tag, String line)
    {
        @SuppressLint("SimpleDateFormat") String fileTimeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String fileName = "log_"+fileTimeStamp+".txt";

        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(new File(logPath + File.separator + fileName),true)    );

            @SuppressLint("SimpleDateFormat") String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").format(Calendar.getInstance().getTime());

            final String printline = "["+ timestamp + "]" + tag + " : " + line + "\n";

            pw.append(printline);
            pw.close();
        }
        catch (IOException e)
        {
            Log.e("LOGS", "Unable to establish log file: "+e.getMessage());
        }
    }
}
