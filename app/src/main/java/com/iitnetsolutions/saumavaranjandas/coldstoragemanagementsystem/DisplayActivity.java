package com.iitnetsolutions.saumavaranjandas.coldstoragemanagementsystem;

import android.app.Notification;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

public class DisplayActivity extends AppCompatActivity
{
    public TextView txtBarcode, txtCustomerName, txtAddress, txtCityVillage,
            txtPostOfficeCode, txtDistrict, txtPIN, txtContactNo,txtAADHAAR,
            txtTestWeight, txtTestWeight1,txtTestWeight2, txtTestWeight3, txtQuality, txtPrivateMarks,
            txtShadePosition, txtVehicleNo,txtRemarks, txtPacket;
    public ImageView barcode_image;
    public Button print_form, print_update, backScanPage;

    int bc_val;
    int user_id;
    String user_name, ConnectionResult,barcodeVal;

    byte FONT_TYPE;
    private static BluetoothSocket btsocket;
    private static OutputStream outputStream;

    Connection connect;

    Bitmap bitmap;

    int imgID;

    String cn_val, a_val,city, poc_val, dist_val, pin_val, tw1_val1, tw1_val2, tw1_val3, qty_val,sp_val,vn_val,r_val,pkt_val;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        //txtBarcode = (TextView)findViewById(R.id.txtBarcode);
        barcode_image = (ImageView)findViewById(R.id.barcode_image);

        txtCustomerName = (TextView)findViewById(R.id.txtCustomerName);
        txtAddress = (TextView)findViewById(R.id.txtAddress);
        txtCityVillage = (TextView)findViewById(R.id.txtCityVillage);
        txtPostOfficeCode = (TextView)findViewById(R.id.txtPostOfficeCode);
        txtDistrict = (TextView)findViewById(R.id.txtDistrict);
        txtPIN = (TextView)findViewById(R.id.txtPIN);
        //txtContactNo  = (TextView)findViewById(R.id.txtContactNo);
        //txtAADHAAR = (TextView)findViewById(R.id.txtAADHAAR);
        txtTestWeight = (TextView)findViewById(R.id.txtTestWeight);
        //txtTestWeight2 = (TextView)findViewById(R.id.txtTestWeight2);
        //txtTestWeight3 = (TextView)findViewById(R.id.txtTestWeight3);
        txtQuality = (TextView)findViewById(R.id.txtQuality);
        //txtPrivateMarks = (TextView)findViewById(R.id.txtPrivateMarks);
        txtShadePosition=(TextView)findViewById(R.id.txtShadePosition);
        txtVehicleNo = (TextView)findViewById(R.id.txtVehicleNo);
        txtRemarks = (TextView)findViewById(R.id.txtRemarks);
        txtPacket = (TextView)findViewById(R.id.txtPacket);


        print_form = (Button)findViewById(R.id.print_form);
        print_update = (Button)findViewById(R.id.print_update);
        backScanPage = (Button)findViewById(R.id.backScanPage);

        Bundle bundle = getIntent().getExtras();

        user_id         = bundle.getInt("ID");
        user_name       = bundle.getString("UserName");
        barcodeVal      = bundle.getString("barcodeVal");


        ConnectionHelper conStr = new ConnectionHelper();
        connect = conStr.connectionclasss();
        try
        {
            if (connect == null)
            {
                ConnectionResult = "Check Your Internet Access!";
                AlertDialog alertDialog = new AlertDialog.Builder(DisplayActivity.this).create();
                alertDialog.setTitle("Login Message");
                alertDialog.setMessage(ConnectionResult);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
            else
            {
                String query = "SELECT * FROM StoreReceive WHERE BondNo = "+ new BigInteger(barcodeVal) +" " ;
                Statement stmt = connect.createStatement();
                //Toast.makeText(DisplayActivity.this, query.toString(), Toast.LENGTH_LONG).show();
                //Log.e("stmt = ", stmt.toString());
                ResultSet rs = stmt.executeQuery(query);

                ConnectionResult = " successful";

                while (rs.next())
                {
                    bc_val      = rs.getInt("ReceiveID");
                    cn_val      = rs.getString("BondHolderName");
                    a_val       = rs.getString("Address1");
                    city        = rs.getString("CityVillage");
                    poc_val     = rs.getString("Post");
                    dist_val    = rs.getString("District");
                    pin_val     = rs.getString("PIN");

                    tw1_val1    = rs.getString("TestWeight1");
                    tw1_val2    = rs.getString("TestWeight2");
                    tw1_val3    = rs.getString("TestWeight3");
                    qty_val     = rs.getString("Quality");
                    sp_val      = rs.getString("ShadePosition");
                    vn_val      = rs.getString("VhicleNo");
                    r_val       = rs.getString("Remarks");
                    pkt_val     = rs.getString("Packet");
                }
                connect.close();
            }
        }
        catch (Exception ex)
        {
            ConnectionResult = ex.getMessage();
        }

        //txtBarcode.setText(bc_val);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try
        {
            BitMatrix bitMatrix  = multiFormatWriter.encode( String.valueOf(bc_val), BarcodeFormat.CODE_128, 400, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            barcode_image.setImageBitmap(bitmap);

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            //Toast.makeText(DisplayActivity.this, directory.toString(),Toast.LENGTH_LONG).show();
            //Log.e("DIR = ", directory.toString());
            File mypath = new File(directory,"barcode.jpg");
            //Log.e("DIR = ", mypath.toString());
            FileOutputStream fos = null;
            try
            {
                fos = new FileOutputStream(mypath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch(WriterException e)
        {
            e.printStackTrace();
        }

        //String cn_val = rs.getString("BondHolderName");
        if(cn_val.equals(""))
            txtCustomerName.setText(Html.fromHtml("<strong>Customer's name:</strong> - NA -"));
        else
            txtCustomerName.setText(Html.fromHtml("<strong>Customer's name:</strong> "+cn_val));


        if(a_val.equals(""))
            txtAddress.setText(Html.fromHtml("<strong>Address:</strong> - NA -"));
        else
            txtAddress.setText(Html.fromHtml("<strong>Address:</strong> "+a_val));


        if(city.equals(""))
            txtCityVillage.setText(Html.fromHtml("<strong>City/Village:</strong> - NA -"));

        else
            txtCityVillage.setText(Html.fromHtml("<strong>City/Village:</strong> "+city));

        if(poc_val.equals(""))
            txtPostOfficeCode.setText(Html.fromHtml("<strong>Post:</strong> - NA -"));
        else
            txtPostOfficeCode.setText(Html.fromHtml("<strong>Post:</strong> "+poc_val));


        if(dist_val.equals(""))
            txtDistrict.setText(Html.fromHtml("<strong>Agent:</strong> - NA -"));
        else
            txtDistrict.setText(Html.fromHtml("<strong>Agent:</strong> "+dist_val));


        if(pin_val.equals(""))
            txtPIN.setText(Html.fromHtml("<strong>Gate Sl:</strong> - NA -"));
        else
            txtPIN.setText(Html.fromHtml("<strong>Gate Sl:</strong> "+pin_val));


        txtTestWeight.setText(Html.fromHtml("<strong>Weight:</strong> "+tw1_val1+", "+tw1_val2+", "+tw1_val3));


        if(qty_val.equals(""))
            txtQuality.setText(Html.fromHtml("<strong>Quality:</strong> -NA-"));
        else
            txtQuality.setText(Html.fromHtml("<strong>Quality:</strong> "+qty_val));

        if(sp_val.equals(""))
            txtShadePosition.setText(Html.fromHtml("<strong>Shade Position:</strong> - NA -"));
        else
            txtShadePosition.setText(Html.fromHtml("<strong>Shade Position:</strong> "+sp_val));

        if(vn_val.equals(""))
            txtVehicleNo.setText(Html.fromHtml("<strong>Vehicle No:</strong> - NA -"));
        else
            txtVehicleNo.setText(Html.fromHtml("<strong>Vehicle No:</strong> "+vn_val));


        if(r_val.equals(""))
            txtRemarks.setText(Html.fromHtml("<strong>Remarks:</strong> - NA -"));
        else
            txtRemarks.setText(Html.fromHtml("<strong>Remarks:</strong> "+r_val));

        if(pkt_val.equals(""))
            txtPacket.setText(Html.fromHtml("<strong>Packet:</strong> - NA -"));
        else
            txtPacket.setText(Html.fromHtml("<strong>Packet:</strong> "+pkt_val));

        print_form.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                printMemo();
            }
        });

        print_update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                print_update();
            }
        });

        backScanPage.setOnClickListener(new View.OnClickListener()
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

    public void printMemo()
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
                byte[] printformat = new byte[]{0x1B,0x21,0x03};
                outputStream.write(printformat);

                //Toast.makeText(DisplayActivity.this, "Print Starts ",Toast.LENGTH_LONG).show();

                Bundle bundle = getIntent().getExtras();
                printCustom("Joy Mahaprovu \n Cold Storage(P) LTD.",3,1);
                printNewLine();
                printCustom("Bond # "+String.valueOf(barcodeVal),2,0);


                /*Image of the barcode*/

                String dir = "/data/user/0/com.iitnetsolutions.saumavaranjandas.coldstoragemanagementsystem/app_imageDir/";
                //File f = new File(dir, "barcode.jpg");
                printImageFromPath(dir.concat("barcode.jpg") );


                if(cn_val.equals(""))
                    printCustom("Name: -NA-" ,1,0);
                else
                    printCustom("Name: "+ cn_val,1,0);

                if(a_val.equals(""))
                    printCustom("Address: -NA-" ,1,0);
                else
                    printCustom("Address: "+ a_val ,1,0);

                if(city.equals(""))
                    printCustom("Village: -NA-" ,1,0);

                else
                    printCustom("Village: "+ city,1,0);

                if(poc_val.equals(""))
                    printCustom("Post: " + poc_val,1,0);
                else
                    printCustom("Post: -NA-",1,0);

                if(dist_val.equals(""))
                    printCustom("Agent: - NA -", 1, 0);
                else
                    printCustom("Agent: "+dist_val, 1, 0);

                if(pin_val.equals(""))
                    printCustom("Gate Sl: - NA -",1, 0);
                else
                    printCustom("Gate Sl: "+pin_val, 1, 0);



                printCustom("Weight: " + tw1_val1+", "+tw1_val2+", "+tw1_val3,1,0);

                if(qty_val.equals(""))
                    printCustom("Quality: -NA-",1,0);
                else
                    printCustom("Quality: " + qty_val,1,0);

                if(sp_val.equals(""))
                    printCustom("Shade: -NA-",1,0);
                else
                    printCustom("Shade: " + sp_val,1,0);

                if(vn_val.equals(""))
                    printCustom("Vehicle: -NA-",1,0);
                else
                    printCustom("Vehicle: " + vn_val,1,0);


                if(r_val.equals(""))
                    printCustom("Remarks: -NA-",1,0);
                else
                    printCustom("Remarks: " + r_val,1,0);


                printNewLine();
                printNewLine();
                if(pkt_val.equals(""))
                    printCustom("Packet: -NA-" + txtPacket.getText().toString(),2,0);
                else
                    printCustom("Packet: " + pkt_val,2,0);

                printCustom("Receipt by: " + user_name,1,2);
                printNewLine();
                printNewLine();

                outputStream.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    public void print_update()
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
                byte[] printformat = new byte[]{0x1B,0x21,0x03};
                outputStream.write(printformat);

                //Toast.makeText(DisplayActivity.this, "Print Starts ",Toast.LENGTH_LONG).show();

                Bundle bundle = getIntent().getExtras();


                printCustom("Joy Mahaprovu \n Cold Storage(P) LTD.",2,1);
                printNewLine();
                printCustom(Integer.toString(bc_val),3,1);
                printCustom("-----------------",3,1);
                printCustom(pkt_val,3,1);
                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();

                updateDataMode(); // update

                outputStream.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void updateDataMode()
    {
        ConnectionHelper conStr = new ConnectionHelper();
        connect = conStr.connectionclasss();
        String qry;
        if (connect == null)
        {
            ConnectionResult = "Check Your Internet Access!";
            AlertDialog alertDialog = new AlertDialog.Builder(DisplayActivity.this).create();
            alertDialog.setTitle("Login Message");
            alertDialog.setMessage(ConnectionResult);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }
        else
        {
            qry = "UPDATE StoreReceive SET DataMode='\"printed\"' WHERE BondNo = "+barcodeVal;
            //Toast.makeText(DisplayActivity.this, "Qry="+ qry,Toast.LENGTH_LONG).show();
            //Log.e("qry", qry);
            try
            {
                Statement stmt = connect.createStatement();
                stmt.executeQuery(qry);
                connect.close();
            }
            catch (SQLException e)
            {
                Log.e("ERROR2", e.getMessage());
            }
        }
        return;
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
                Toast.makeText(DisplayActivity.this, "the file isn't exists",Toast.LENGTH_LONG).show();
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
                Toast.makeText(DisplayActivity.this, "the file isn't exists",Toast.LENGTH_LONG).show();
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
                intent.putExtra("ID", user_id);
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
