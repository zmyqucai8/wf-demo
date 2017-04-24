package com.example.testing.demo.http;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testing.demo.R;
import com.example.testing.demo.upload.AmUtlis;
import com.example.testing.demo.upload.BitmapUtils;
import com.example.testing.demo.upload.Constant;
import com.example.testing.demo.upload.FileUtils;
import com.example.testing.demo.upload.PhotoPop;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class UploadActivity extends AppCompatActivity {
    Button btn_upload;
    Button upload;
    ImageView img;
    ProgressBar bar;
    TextView text;
    EditText ed;
    String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        btn_upload = (Button) findViewById(R.id.btn_upload);
        upload = (Button) findViewById(R.id.upload);
        img = (ImageView) findViewById(R.id.img);
        bar = (ProgressBar) findViewById(R.id.bar);
        text = (TextView) findViewById(R.id.text);
        ed = (EditText) findViewById(R.id.ed);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPop.getInstance().showPop(UploadActivity.this);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postUpload();
            }
        });

        imgPath = AmUtlis.getPhotoFile().getAbsolutePath();
        if (!TextUtils.isEmpty(imgPath)) {
            img.setImageBitmap((BitmapUtils.getDiskBitmap(imgPath)));
        }
    }

    /**
     * 上传照片的方法
     */
    private void postUpload() {
        String url = "http://192.168.0.12:8900/weboa/km/kmattach.nsf/FileUploadForm?CreateDocument";
        String name = "%%File.48257f7900293e55.86ec149b6a75b27848257a4700542d89.$Body.0.1E6";
        if (!TextUtils.isEmpty(imgPath)) {
            File file = new File(imgPath);
//            imgUrl为图片位置
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(name, file.getName(), createCustomRequestBody(MediaType.parse("image/jpg"), file, new ProgressListener() {
                        @Override
                        public void onProgress(long totalBytes, long remainingBytes, boolean done) {
                            final int p = (int) ((totalBytes - remainingBytes) * 100 / totalBytes);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    bar.setProgress(p);
                                    text.setText(p + "%");
                                }
                            });

                        }
                    }))
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            OkHttpUtils.getInstance().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("图片响应失败=", call.request().toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UploadActivity.this, "图片上传成功", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });

        } else {
            Toast.makeText(this, "图片路径为null", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.CAPTURE && resultCode == RESULT_OK) {
            //拍照返回
            img.setImageBitmap((BitmapUtils.getDiskBitmap(AmUtlis.getPhotoFile().getAbsolutePath())));
            imgPath = AmUtlis.getPhotoFile().getAbsolutePath();
        } else if (data != null && requestCode == Constant.ALBUM && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            if (null == cursor) {
                imgPath = data.getData().getPath();
            } else {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgPath = cursor.getString(columnIndex);
                cursor.close();
            }
            Bitmap bitmap = null;

            bitmap = BitmapUtils.getDiskBitmap(imgPath);

            if (bitmap == null) {
                AmUtlis.showToast("图片已被删除，或不存在");
                return;
            }
            img.setImageBitmap(bitmap);
        }

    }


    /**
     * 创建一个包装好的body， 自带上传进度
     *
     * @param contentType
     * @param file
     * @param listener
     * @return
     */
    public static RequestBody createCustomRequestBody(final MediaType contentType, final File file, final ProgressListener listener) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
//                return file.length();
                return file.length();
            }


            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {

                    source = Okio.source(file);
                    //sink.writeAll(source);
                    Buffer buf = new Buffer();
                    Long remaining = contentLength();
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                        listener.onProgress(contentLength(), remaining -= readCount, remaining == 0);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    interface ProgressListener {
        void onProgress(long totalBytes, long remainingBytes, boolean done);
    }


    public void uploadTxt(View v) {
        //上传txt文件
        String s = ed.getText().toString();
        String url = "http://192.168.0.12:8900/weboa/km/kmattach.nsf/FileUploadForm?CreateDocument";
        String name = "%%File.48257f7900293e55.86ec149b6a75b27848257a4700542d89.$Body.0.1E6";
        String txtFile = Environment.getExternalStorageDirectory() + "/" + "testFile.txt";
        File file = FileUtils.writeTxt(txtFile, "我只是一个用来上传的测试文本 内容：\n" + s);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(name, file.getName(), createCustomRequestBody(MediaType.parse("text/plain"), file, new ProgressListener() {
                    @Override
                    public void onProgress(long totalBytes, long remainingBytes, boolean done) {
                        final int p = (int) ((totalBytes - remainingBytes) * 100 / totalBytes);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bar.setProgress(p);
                                text.setText(p + "%");
                            }
                        });

                    }
                }))

                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpUtils.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("上传失败=", call.request().toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UploadActivity.this, "txt文件上传成功", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}
