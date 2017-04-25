package com.example.testing.demo.upload;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testing.demo.R;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
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

    List<File> listFile = new ArrayList<File>();

    /**
     * 上传照片的方法
     */
    private void postUpload() {
        String url = "http://192.168.0.12:8900/weboa/km/kmattach.nsf/FileUploadForm?CreateDocument";
        String name = "%%File.48257f7900293e55.86ec149b6a75b27848257a4700542d89.$Body.0.1E6";
        if (!TextUtils.isEmpty(imgPath)) {
            File file = new File(imgPath);
//            listFile.add(file);
//            listFile.add(file);
//            listFile.add(file);
            OkGo.post(url)//
                    .tag(this)//
                    .isMultipart(true)       // 强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
//                .params("MemberID", "1005467")
//                .headers("appID", "1005467")
                    // 这里可以上传参数
//                .headers("appToken", "7444b9b85b5364022ed5895495c2df2cc530fa17bf49aa544f493d5d047891ec99178ad36f6ffe79")        // 这里可以上传参数
//                .params("myPhoto", new File(imgPath))   // 可以添加文件上传
                    .params(name, file)   // 可以添加文件上传
//                .params("dataType", "common")   // 可以添加文件上传
//                .addFileParams("uploadfiles", listFile)    // 这里支持一个key传多个文件
//                    .addFileParams(name, listFile)    // 这里支持一个key传多个文件
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            //上传成功
                            AmUtlis.showToast("上传成功");
                        }


                        @Override
                        public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                            //这里回调上传进度(该回调在主线程,可以直接更新ui)
                            AmUtlis.showLog("progress=" + progress);
                            AmUtlis.showLog("currentSize=" + currentSize);
                            AmUtlis.showLog("totalSize=" + totalSize);
                            int i = (int) (currentSize / totalSize * 100);
                            bar.setProgress(i);
                            text.setText(i + "%");
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
        listFile.add(file);
        OkGo.post(url)
                .tag(this)
                .isMultipart(true)
                .addFileParams(name, listFile)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        AmUtlis.showToast("上传多个文本文件成功");
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.upProgress(currentSize, totalSize, progress, networkSpeed);
                        AmUtlis.showLog("progress=" + progress);
                        AmUtlis.showLog("currentSize=" + currentSize);
                        AmUtlis.showLog("totalSize=" + totalSize);
                        int i = (int) (currentSize / totalSize * 100);
                        bar.setProgress(i);
                        text.setText(i + "%");
                    }
                });

    }
}
