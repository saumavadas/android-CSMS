package com.iitnetsolutions.saumavaranjandas.coldstoragemanagementsystem;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

public class printProfile extends AppCompatActivity
{
    public String user_name;
    int user_id;
    public TextView txtProfile;
    public Button btnTestPrint, backScanPageFrmProfilePg;
    private static BluetoothSocket btsocket;
    private static OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_profile);

        Bundle bundle   = getIntent().getExtras();
        user_id         = bundle.getInt("ID");
        user_name       = bundle.getString("UserName");

        txtProfile = (TextView)findViewById(R.id.txtProfile);
        btnTestPrint = (Button) findViewById(R.id.btnTestPrint);
        backScanPageFrmProfilePg = (Button)findViewById(R.id.backScanPageFrmProfilePg);

        txtProfile.setText("Loggin As: "+user_name);

        btnTestPrint.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                testPrint();
            }
        });
        backScanPageFrmProfilePg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), barcodescan.class);
                intent.putExtra("ID", user_id);
                intent.putExtra("UserName", user_name);
                startActivity(intent);
            }
        });
    }

    public void testPrint()
    {
        if(btsocket == null)
        {
            Intent BTIntent = new Intent(getApplicationContext(), DeviceList.class);
            this.startActivityForResult(BTIntent, DeviceList.REQUEST_CONNECT_BT);
        }
        else
        {
            OutputStream opstream = null;
            try
            {
                opstream = btsocket.getOutputStream();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            outputStream = opstream;

            //print command
            try
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                outputStream = btsocket.getOutputStream();
                byte[] printformat = new byte[]{0x1B, 0x21, 0x03};
                outputStream.write(printformat);
                printCustom("User Name:"+user_name ,1,0);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void printCustom(String msg, int size, int align)
    {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B,0x21,0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text
        try
        {
            switch (size)
            {
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;
            }

            switch (align)
            {
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.LF);
            //outputStream.write(cc);
            //printNewLine();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void printImageFromPath(String ImgPath)
    {
        try
        {

            Bitmap bmp = BitmapFactory.decodeFile(ImgPath);
            if(bmp!=null)
            {
                //Log.e("Print Photo error", "the file is ok");
                byte[] command = Utils.decodeBitmap(bmp);
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(command);
            }
            else
            {
                Log.e("Print Photo error", "the file isn't exists");
                Toast.makeText(getApplicationContext(), "the file isn't exists",Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

    public void printPhoto(int img)
    {
        try
        {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), img);
            if(bmp!=null)
            {
                byte[] command = Utils.decodeBitmap(bmp);
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(command);
            }
            else
            {
                Log.e("Print Photo error", "the file isn't exists");
                Toast.makeText(getApplicationContext(), "the file isn't exists",Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }


    //print unicode
    public void printUnicode()
    {
        try
        {
            outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
            printText(Utils.UNICODE_TEXT);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //print new line
    private void printNewLine()
    {
        try
        {
            outputStream.write(PrinterCommands.FEED_LINE);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public static void resetPrint()
    {
        try
        {
            outputStream.write(PrinterCommands.ESC_FONT_COLOR_DEFAULT);
            outputStream.write(PrinterCommands.FS_FONT_ALIGN);
            outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
            outputStream.write(PrinterCommands.ESC_CANCEL_BOLD);
            outputStream.write(PrinterCommands.LF);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //print text
    private void printText(String msg)
    {
        try
        {
            // Print normal text
            outputStream.write(msg.getBytes());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    //print byte[]
    private void printText(byte[] msg)
    {
        try
        {
            // Print normal text
            outputStream.write(msg);
            printNewLine();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private String leftRightAlign(String str1, String str2)
    {
        String ans = str1 +str2;
        if(ans.length() <31)
        {
            int n = (31 - str1.length() + str2.length());
            ans = str1 + new String(new char[n]).replace("\0", " ") + str2;
        }
        return ans;
    }


    private String[] getDateTime()
    {
        final Calendar c = Calendar.getInstance();
        String dateTime [] = new String[2];
        dateTime[0] = c.get(Calendar.DAY_OF_MONTH) +"/"+ c.get(Calendar.MONTH) +"/"+ c.get(Calendar.YEAR);
        dateTime[1] = c.get(Calendar.HOUR_OF_DAY) +":"+ c.get(Calendar.MINUTE);
        return dateTime;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        try
        {
            if(btsocket!= null)
            {
                outputStream.close();
                btsocket.close();
                btsocket = null;
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        try
        {
            btsocket = DeviceList.getSocket();
            if(btsocket != null)
            {
                printText("JUST ON MESSAGE");
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        menu.findItem(R.id.show_user_profile).setTitle("Login As: "+user_name);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.show_user_profile:
                Toast.makeText(this, "Login as "+user_name, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), printProfile.class);
                intent.putExtra("UserName", user_name);
                startActivity(intent);
                break;
            case R.id.action_logout:
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
        return true;
    }
}
