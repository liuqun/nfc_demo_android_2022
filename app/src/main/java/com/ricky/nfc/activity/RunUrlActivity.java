package com.ricky.nfc.activity;

import android.content.Intent;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ricky.nfc.R;
import com.ricky.nfc.base.BaseNfcActivity;

import java.io.IOException;

/**
 * Author:Created by Ricky on 2017/8/25.
 * Email:584182977@qq.com
 * Description: 打开网页
 */
public class RunUrlActivity extends BaseNfcActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_url);
    }

    private NdefMessage generateNdefMessage() {
        // 例子: NFC文字标签
        return new NdefMessage(new NdefRecord[] {
                NdefRecord.createTextRecord("zh", "你好，世界。"),
                NdefRecord.createTextRecord("en", "Hello, World."),
        });
    }

    private int formatNFCTag(Tag tag, NdefMessage ndefMessage) {
        NdefFormatable formatter = NdefFormatable.get(tag);
        //判断是否获得了 NdefFormatable 对象，有一些标签是只读的或者不允许格式化的
        if (null == formatter) {
            return -1;
        }
        try {
            formatter.connect();
            formatter.format(ndefMessage);
        } catch (IOException | FormatException e) {
            e.printStackTrace();
            return -2;
        }
        return 0;
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        NdefMessage ndefMessage = generateNdefMessage();

        //1.获取Tag对象
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (null == tag) {
            return;
        }
        // 2.判断NFC标签的数据类型（通过Ndef.get()方法）
        Ndef ndef = Ndef.get(tag);
        if (null == ndef) {
            int err;
            String notice = "写入成功";

            // 当我们买回来的NFC标签是没有格式化的或者没有分区的，执行此步
            err = formatNFCTag(tag, ndefMessage);
            if (err < 0) {
                notice = "写入失败";
            }

            // 格式化完毕
            Toast.makeText(this, notice, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            ndef.connect();
        } catch (IOException e) {
            e.printStackTrace();
            // 结束
            Toast.makeText(this, "写入失败", Toast.LENGTH_SHORT).show();
            return;
        }
        // 判断是否支持可写
        if (!ndef.isWritable()) {
            // 结束
            Toast.makeText(this, "当前NFC标签不可写", Toast.LENGTH_SHORT).show();
            return;
        }
        // 检查标签容量
        if (ndef.getMaxSize() < ndefMessage.toByteArray().length) {
            // 结束
            Toast.makeText(this, "当前NFC标签容量太小", Toast.LENGTH_SHORT).show();
            return;
        }
        // 3.写入数据
        try {
            ndef.writeNdefMessage(ndefMessage);
        } catch (FormatException | IOException e) {
            e.printStackTrace();
            // 结束
            Toast.makeText(this, "写入失败", Toast.LENGTH_SHORT).show();
        }

        // 正常结束
        Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show();
    }


//    /**
//     * 往标签写数据的方法
//     *
//     * @param tag
//     */
//    public void writeNFCTag(Tag tag) {
//        if (tag == null) {
//            return;
//        }
//        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{NdefRecord
//                .createUri(Uri.parse("http://www.baidu.com"))});
//        //转换成字节获得大小
//        int size = ndefMessage.toByteArray().length;
//        try {
//            //2.判断NFC标签的数据类型（通过Ndef.get方法）
//            Ndef ndef = Ndef.get(tag);
//            //判断是否为NDEF标签
//            if (ndef != null) {
//                ndef.connect();
//                //判断是否支持可写
//                if (!ndef.isWritable()) {
//                    return;
//                }
//                //判断标签的容量是否够用
//                if (ndef.getMaxSize() < size) {
//                    return;
//                }
//                //3.写入数据
//                ndef.writeNdefMessage(ndefMessage);
//                Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show();
//            } else { //当我们买回来的NFC标签是没有格式化的，或者没有分区的执行此步
//                //Ndef格式类
//                NdefFormatable format = NdefFormatable.get(tag);
//                //判断是否获得了NdefFormatable对象，有一些标签是只读的或者不允许格式化的
//                if (format != null) {
//                    //连接
//                    format.connect();
//                    //格式化并将信息写入标签
//                    format.format(ndefMessage);
//                    Toast.makeText(this, "写入成功",
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "写入失败", Toast.LENGTH_SHORT).show();
//                }
//            }
//        } catch (Exception e) {
//        }
//    }
}