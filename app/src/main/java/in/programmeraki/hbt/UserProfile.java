package in.programmeraki.hbt;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import in.programmeraki.hbt.utils.Constant;

public class UserProfile extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    TextView title_tv, full_name_tv;
    EditText fname_et, lname_et, email_et, dob_et, height_et, weight_et, pulse_min_et,
            pulse_max_et, temp_min_et, temp_max_et;
    ViewGroup top_head_rl;
    Button submit_btn;
    ImageView imgviewprofile;

    String fname, lname, email, dob, height, weight, pulse_min, pulse_max, temp_min, temp_max, imgstr;

    private static final int PICK_IMAGE_REQUEST = 1;
    private int STORAGE_PERMISSION_CODE = 23;
    private String imagepath=null;
    Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        top_head_rl = (ViewGroup) findViewById(R.id.top_head_rl);
        title_tv = (TextView) findViewById(R.id.title_tv);
        full_name_tv = (TextView) findViewById(R.id.full_name_tv);
        fname_et = (EditText) findViewById(R.id.fname_et);
        lname_et = (EditText) findViewById(R.id.lname_et);
        email_et = (EditText) findViewById(R.id.email_et);
        dob_et = (EditText) findViewById(R.id.dob_et);
        height_et = (EditText) findViewById(R.id.height_et);
        weight_et = (EditText) findViewById(R.id.weight_et);
        pulse_min_et = (EditText) findViewById(R.id.pulse_min_et);
        pulse_max_et = (EditText) findViewById(R.id.pulse_max_et);
        temp_max_et = (EditText) findViewById(R.id.temp_max_et);
        temp_min_et = (EditText) findViewById(R.id.temp_min_et);
        submit_btn = (Button) findViewById(R.id.submit_btn);
        imgviewprofile = (ImageView) findViewById(R.id.avatar_iv);

        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        imgstr = sharedPreferences.getString("imageString", "");
        fname = sharedPreferences.getString("fname", "");
        lname = sharedPreferences.getString("lname", "");
        email = sharedPreferences.getString("email", "");
        dob = sharedPreferences.getString("dob", "");
        height = sharedPreferences.getString("height", "");
        weight = sharedPreferences.getString("weight", "");
        pulse_min = sharedPreferences.getString("pulse_min", "");
        pulse_max = sharedPreferences.getString("pulse_max", "");
        temp_max = sharedPreferences.getString("temp_max", "");
        temp_min = sharedPreferences.getString("temp_min", "");

        if(imgstr != null && !imgstr.isEmpty() && !imgstr.equals("null")) {
//            try {
//                filePath = Uri.parse(sharedPreferences.getString("url", ""));
//                imgviewprofile.setImageURI(filePath);
//            } catch (IllegalArgumentException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }

            //decode base64 string to image
            byte[] imageBytes = Base64.decode(imgstr, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imgviewprofile.setImageBitmap(decodedImage);
        }

        fname_et.setText(fname);
        lname_et.setText(lname);
        email_et.setText(email);
        dob_et.setText(dob);
        height_et.setText(height);
        weight_et.setText(weight);
        pulse_min_et.setText(pulse_min);
        pulse_max_et.setText(pulse_max);
        temp_min_et.setText(temp_min);
        temp_max_et.setText(temp_max);
        String Fullname = fname + " " + lname;
        full_name_tv.setText(Fullname);

        imgviewprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                fname = fname_et.getText().toString().trim();
                lname = lname_et.getText().toString().trim();
                email = email_et.getText().toString().trim();
                dob = dob_et.getText().toString().trim();
                height = height_et.getText().toString().trim();
                weight = weight_et.getText().toString().trim();
                pulse_min = pulse_min_et.getText().toString().trim();
                pulse_max = pulse_max_et.getText().toString().trim();
                temp_max = temp_max_et.getText().toString().trim();
                temp_min = temp_min_et.getText().toString().trim();

                sharedPreferences = getSharedPreferences("MyPref", 0);
                editor = sharedPreferences.edit();
                editor.putString("fname", fname);
                editor.putString("lname", lname);
                editor.putString("email", email);
                editor.putString("dob", dob);
                editor.putString("height", height);
                editor.putString("weight", weight);
                editor.putString("pulse_min", pulse_min);
                editor.putString("pulse_max", pulse_max);
                editor.putString("temp_max", temp_max);
                editor.putString("temp_min", temp_min);
                editor.putString(Constant.p_min, pulse_min);
                editor.putString(Constant.p_max, pulse_max);
                editor.putString(Constant.t_min, temp_min);
                editor.putString(Constant.t_max, temp_max);
                editor.putString(Constant.height, height);
                editor.putString(Constant.weight, weight);
                editor.commit();

                showDialog();

            }
        });

        findViewById(R.id.back_iv).setOnClickListener(view -> {
            finish();
        });

    }

    void showDialog(){
        final Dialog dialog = new Dialog(this);
        if (dialog.getActionBar() != null) {
            dialog.getActionBar().hide();
        }
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        dialog.setContentView(R.layout.dialog_box_simple_order_confirmed);
        TextView txtNext = (TextView) dialog.findViewById(R.id.txt_next);
        TextView txtTitle = (TextView) dialog.findViewById(R.id.txt_title);
        TextView message = (TextView) dialog.findViewById(R.id.message);
        LinearLayout root_ll = dialog.findViewById(R.id.root_ll);
        ImageView iv = dialog.findViewById(R.id.iv);

        message.setText("User details updated.");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        ViewGroup.LayoutParams layoutParams = root_ll.getLayoutParams();
        layoutParams.width = (int) (width*0.80);
        dialog.show();

        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    //Requesting permission
    private void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getRealPathFromURI(FragmentActivity context, Uri contentUri) {
        Cursor cursor = null;
        String returnVal="";
        try {

            try {
                String wholeID = DocumentsContract.getDocumentId(contentUri);
                String id = wholeID.split(":")[1];
                String[] column = { MediaStore.Images.Media.DATA };
                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";
                cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{ id }, null);
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    returnVal = cursor.getString(columnIndex);
                }
                cursor.close();
            } catch (Exception e) {
                //e.printStackTrace();
            }

            if(returnVal.isEmpty()) {
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                returnVal = cursor.getString(column_index);
            }
            return returnVal;
        }
        catch (Exception e) {
            //e.printStackTrace();
            return returnVal;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();


            // Split at colon, use second item in the array


            if(isReadStorageAllowed()){
                //If permission is already having then showing the toast
//                Toast.makeText(Sendmessage.this,"You already have the permission",Toast.LENGTH_LONG).show();
                //Existing the method with return

            }

            //If the app has not the permission then asking for the permission
            requestStoragePermission();
            grantUriPermission(this.getPackageName(),filePath,Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String tmpPath =getRealPathFromURI(this,filePath);
            imagepath= tmpPath;


            //
            try {
                //Getting the Bitmap from Gallery
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);


                //Setting the Bitmap to ImageView
                // imageView.setImageBitmap(bitmap);
                imgviewprofile.setImageURI(filePath);

                sharedPreferences = getSharedPreferences("MyPref", 0);
                editor = sharedPreferences.edit();
                editor.putString("imageString", imageString);
                editor.commit();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
