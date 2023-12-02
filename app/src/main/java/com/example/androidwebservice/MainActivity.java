package com.example.androidwebservice;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidwebservice.model.Sinhvien;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Server chứa webservice. Các bạn đổi thành sever của mình
    final String SERVER = "http://172.17.9.177/ws/api.php";

    EditText txtMaSv, txtTenSv;
    Button btnLuu;
    ArrayList<Sinhvien> dsSv;
    ArrayAdapter<Sinhvien> adapter;
    ListView lvSv;
    Sinhvien chon = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        hienThiDanhsach();
        addEvents();
    }

    private void addControls() {
        txtMaSv = findViewById(R.id.txtMaSv);
        txtTenSv = findViewById(R.id.txtTenSv);
        btnLuu = findViewById(R.id.btnLuu);
        lvSv = findViewById(R.id.lvSv);
        dsSv = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                dsSv
        );

        lvSv.setAdapter(adapter);
    }

    private void addEvents() {
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int msv = Integer.parseInt(txtMaSv.getText().toString());
                String tensv = txtTenSv.getText().toString();
                Sinhvien sv = new Sinhvien(msv,tensv);
                themSinhVienApi(sv);
            }
        });
    }

    private void themSinhVienApi(Sinhvien sv) {
        // Hàng đợi các request lên server
        RequestQueue requestQueue = Volley.newRequestQueue(
                MainActivity.this
        );

        // Lắng nghe kết quả trả về
        Response.Listener<String> responseListener =
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                           JSONObject jsonObject = new JSONObject(response);
                           String messenge =  jsonObject.getString("message");
                           if(messenge.equals("true")){
                               Toast.makeText(MainActivity.this,"them thanh cong",Toast.LENGTH_LONG).show();
                               hienThiDanhsach();
                           }
                           else{
                               Toast.makeText(MainActivity.this,"them that bai",Toast.LENGTH_LONG).show();
                           }
                        } catch (Exception ex) {

                        }
                    }
                };

        // Lắng nghe lỗi trả về (thường là lỗi kết nối)
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(
                        MainActivity.this,
                        error.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        };

        // Tao url đến service
        Uri.Builder builder = Uri.parse(SERVER).buildUpon();

        // Chèn thêm tham số cho url, dùng trong phương thức $_GET
        builder.appendQueryParameter("action", "insert");
        builder.appendQueryParameter("masv", sv.getMaSV()+"");
        builder.appendQueryParameter("tensv", sv.getTenSV());
        String url = builder.build().toString();
        StringRequest request = new StringRequest(
                Request.Method.GET, // nếu dùng $_POST thì đổi thành POST
                url,
                responseListener,
                errorListener
        );

        // Volley có xu hướng thực hiện nhiều cuộc gọi đến máy chủ chậm
        // vì nó không nhận được phản hồi từ yêu cầu đầu tiên,
        // nên cần cấu hình thông tin thử lại (Retry)
        // DEFAULT_TIMEOUT_MS: Thời gian chờ tối đa trong mỗi lần thử lại. Mặc
        // định 2500ms.
        // DEFAULT_MAX_RETRIES: Số lần thử lại tối đa. Mặc định 1.
        // DEFAULT_BACKOFF_MULT: Hệ số được xác định thời gian theo hàm mũ
        // được gán cho socket trong mỗi lần thử lại. Mặc định 1.0f
        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(request);
    }


    private void hienThiDanhsach() {
        // Hàng đợi các request lên server
        RequestQueue requestQueue = Volley.newRequestQueue(
                MainActivity.this
        );

        // Lắng nghe kết quả trả về
        Response.Listener<String> responseListener =
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            dsSv.clear();
                            // Server trả về một chuỗi có dạng mảng JSON,
                            // nên ta ép nó thành JSONArray rồi lặp
                            // trên Array để lấy ra từng JSONObject
                            JSONArray jsonArray = new JSONArray(response);
                            int len = jsonArray.length();
                            for (int i = 0; i < len; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int ma = jsonObject.getInt("masv");
                                String ten = jsonObject.getString("tensv");
                                dsSv.add(new Sinhvien(ma, ten));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (Exception ex) {

                        }
                    }
                };

        // Lắng nghe lỗi trả về (thường là lỗi kết nối)
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(
                        MainActivity.this,
                        error.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        };

        // Tao url đến service
        Uri.Builder builder = Uri.parse(SERVER).buildUpon();

        // Chèn thêm tham số cho url, dùng trong phương thức $_GET
        builder.appendQueryParameter("action", "getall");
        String url = builder.build().toString();
        StringRequest request = new StringRequest(
                Request.Method.GET, // nếu dùng $_POST thì đổi thành POST
                url,
                responseListener,
                errorListener
        );

        // Volley có xu hướng thực hiện nhiều cuộc gọi đến máy chủ chậm
        // vì nó không nhận được phản hồi từ yêu cầu đầu tiên,
        // nên cần cấu hình thông tin thử lại (Retry)
        // DEFAULT_TIMEOUT_MS: Thời gian chờ tối đa trong mỗi lần thử lại. Mặc
        // định 2500ms.
        // DEFAULT_MAX_RETRIES: Số lần thử lại tối đa. Mặc định 1.
        // DEFAULT_BACKOFF_MULT: Hệ số được xác định thời gian theo hàm mũ
        // được gán cho socket trong mỗi lần thử lại. Mặc định 1.0f
        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(request);
    }

    private void xuliThemSv(Sinhvien sv) {

    }

    private void xuliXoaSv(Sinhvien sv) {

    }

    private void xuliCapnhatSv(Sinhvien sv) {

    }

}