package com.iitnetsolutions.saumavaranjandas.coldstoragemanagementsystem;

import android.content.DialogInterface;
import android.content.Intent;
//import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity
{
    EditText editUserName, editPassword;
    Button btnEnter;
    Connection connect;
    String ConnectionResult = "";
    int RecCount=0;
    int RecId;
    String UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editUserName    = (EditText)findViewById(R.id.editUserName);
        editPassword    = (EditText)findViewById(R.id.editPassword);
        btnEnter        = (Button)findViewById(R.id.btnEnter);

        btnEnter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Toast.makeText(MainActivity.this, editUserName.getText().toString(), Toast.LENGTH_LONG).show();
                if (editUserName.getText().toString().equals(""))
                {
                    editUserName.setError("Username is required");
                }
                else if (editPassword.getText().toString().equals(""))
                {
                    editPassword.setError("Password is required");
                }
                else
                {
                    try
                    {
                        ConnectionHelper conStr = new ConnectionHelper();
                        connect = conStr.connectionclasss();        // Connect to database
                        if (connect == null)
                        {
                            ConnectionResult = "Check Your Internet Access!";
                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
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
                            String query = "SELECT * FROM Users WHERE UserName = '" + editUserName.getText().toString() + "' AND PassWord = '" + editPassword.getText().toString() + "'";
                            Statement stmt = connect.createStatement();
                            //Toast.makeText(MainActivity.this, query.toString(), Toast.LENGTH_LONG).show();
                            ResultSet rs = stmt.executeQuery(query);

                            ConnectionResult = " successful";

                            while (rs.next())
                            {
                                RecId = (int) rs.getInt(1);
                                UserName = rs.getString("UserName");
                                RecCount++;
                            }
                            //Toast.makeText(MainActivity.this, "RecID = "+RecId+" RecCount="+RecCount, Toast.LENGTH_LONG).show();
                            connect.close();
                        }
                    }
                    catch (Exception ex)
                    {
                        ConnectionResult = ex.getMessage();
                    }

                    if (RecCount > 0)
                    {
                        Toast.makeText(MainActivity.this, "Login Successful.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, barcodescan.class);
                        intent.putExtra("ID", RecId);
                        intent.putExtra("UserName", UserName);
                        startActivity(intent);
                    }
                    else
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                        alertDialog.setTitle("Login Message");
                        alertDialog.setMessage("Username or Password mismatch\nPlease try again");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                                editUserName.setText("");
                                editPassword.setText("");
                                editUserName.requestFocus();
                            }
                        });
                        alertDialog.show();
                        //Toast.makeText(MainActivity.this, "Username or Password mismatch\\nPlease try again", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
