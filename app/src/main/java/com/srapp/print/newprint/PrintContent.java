package com.srapp.print.newprint;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.gprinter.command.EscCommand;
import com.srapp.R;
import com.srapp.print.newprint.App;

import java.util.Vector;

/**
 * Created by Administrator on 2018/4/16.
 */

public class PrintContent {
    /*  *//**
       * 简体中文
       * 票据打印测试页
       * @return
       *//*
      public static Vector<Byte> getReceipt() {
            EscCommand esc = new EscCommand();
            //初始化打印机
            esc.addInitializePrinter();
            //打印走纸多少个单位
            esc.addPrintAndFeedLines((byte) 3);
            // 设置打印居中
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            // 设置为倍高倍宽
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);
            // 打印文字
            esc.addText("票据测试\n");
            //打印并换行
            esc.addPrintAndLineFeed();
            // 取消倍高倍宽
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
            // 设置打印左对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            // 打印文字
            esc.addText("打印文字测试:\n");
            // 打印文字
            esc.addText("欢迎使用打印机!\n");
            esc.addPrintAndLineFeed();
            esc.addText("打印对齐方式测试:\n");
            // 设置打印左对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            esc.addText("居左");
            esc.addPrintAndLineFeed();
            // 设置打印居中对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            esc.addText("居中");
            esc.addPrintAndLineFeed();
            // 设置打印居右对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.RIGHT);
            esc.addText("居右");
            esc.addPrintAndLineFeed();
            esc.addPrintAndLineFeed();
            // 设置打印左对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            esc.addText("打印Bitmap图测试:\n","BIG5");
            Bitmap b = BitmapFactory.decodeResource(App.getContext().getResources(), R.mipmap.ic_priter);
            // 打印图片  光栅位图  384代表打印图片像素  0代表打印模式
            // 58mm打印机 可打印区域最大点数为 384 ，80mm 打印机 可打印区域最大点数为 576
            esc.addRastBitImage(b, 384, 0);
            esc.addPrintAndLineFeed();
            // 打印文字
            esc.addText("打印条码测试:\n");
            esc.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.BELOW);
            // 设置条码可识别字符位置在条码下方
            // 设置条码高度为60点
            esc.addSetBarcodeHeight((byte) 60);
            // 设置条码宽窄比为2
            esc.addSetBarcodeWidth((byte) 2);
            // 打印Code128码
            esc.addCODE128(esc.genCodeB("barcode128"));
            esc.addPrintAndLineFeed();
            *//*
             * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
             *//*
            esc.addText("打印二维码测试:\n");
            // 设置纠错等级
            esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31);
            // 设置qrcode模块大小
            esc.addSelectSizeOfModuleForQRCode((byte) 4);
            // 设置qrcode内容
            esc.addStoreQRCodeData("www.smarnet.cc");
            // 打印QRCode
            esc.addPrintQRCode();
            //打印并走纸换行
            esc.addPrintAndLineFeed();
            // 设置打印居中对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            //打印fontB文字字体
            esc.addSelectCharacterFont(EscCommand.FONT.FONTB);
            esc.addText("测试完成!\r\n");
            //打印并换行
            esc.addPrintAndLineFeed();
            //打印走纸n个单位
            esc.addPrintAndFeedLines((byte) 4);
            //开启切刀
            esc.addCutPaper();
            Vector<Byte> datas = esc.getCommand();
            return datas;
      }
      *//**
       * 繁體中文
       * 票据打印测试页
       * @return
       *//*
      public static Vector<Byte> getTraditionalReceipt() {
            EscCommand esc = new EscCommand();
            //初始化打印机
            esc.addInitializePrinter();
            //打印走纸多少个单位
            esc.addPrintAndFeedLines((byte) 3);
            // 设置打印居中
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            // 设置为倍高倍宽
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);
            // 打印文字
            esc.addText("票據測試\n","BIG5");
            //打印并换行
            esc.addPrintAndLineFeed();
            // 取消倍高倍宽
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
            // 设置打印左对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            // 打印文字
            esc.addText("打印文字測試:\n","BIG5");
            // 打印文字
            esc.addText("歡迎使用打印機!\n","BIG5");
            esc.addPrintAndLineFeed();
            esc.addText("打印對齊方式測試:\n","BIG5");
            // 设置打印左对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            esc.addText("居左","BIG5");
            esc.addPrintAndLineFeed();
            // 设置打印居中对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            esc.addText("居中","BIG5");
            esc.addPrintAndLineFeed();
            // 设置打印居右对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.RIGHT);
            esc.addText("居右","BIG5");
            esc.addPrintAndLineFeed();
            esc.addPrintAndLineFeed();
            // 设置打印左对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            esc.addText("打印Bitmap圖測試:\n","BIG5");
            Bitmap b = BitmapFactory.decodeResource(App.getContext().getResources(), R.mipmap.ic_priter);
            // 打印图片  光栅位图  384代表打印图片像素  0代表打印模式
            // 58mm打印机 可打印区域最大点数为 384 ，80mm 打印机 可打印区域最大点数为 576
            esc.addRastBitImage(b, 384, 0);
            esc.addPrintAndLineFeed();
            // 打印文字
            esc.addText("打印條碼測試:\n","BIG5");
            esc.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.BELOW);
            // 设置条码可识别字符位置在条码下方
            // 设置条码高度为60点
            esc.addSetBarcodeHeight((byte) 60);
            // 设置条码宽窄比为2
            esc.addSetBarcodeWidth((byte) 2);
            // 打印Code128码
            esc.addCODE128(esc.genCodeB("barcode128"));
            esc.addPrintAndLineFeed();
            *//*
             * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
             *//*
            esc.addText("打印二維碼測試:\n","BIG5");
            // 设置纠错等级
            esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31);
            // 设置qrcode模块大小
            esc.addSelectSizeOfModuleForQRCode((byte) 4);
            // 设置qrcode内容
            esc.addStoreQRCodeData("www.smarnet.cc");
            // 打印QRCode
            esc.addPrintQRCode();
            //打印并走纸换行
            esc.addPrintAndLineFeed();
            // 设置打印居中对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            //打印fontB文字字体
            esc.addSelectCharacterFont(EscCommand.FONT.FONTB);
            esc.addText("測試完成!\r\n","BIG5");
            //打印并换行
            esc.addPrintAndLineFeed();
            //打印走纸n个单位
            esc.addPrintAndFeedLines((byte) 4);
            //开启切刀
            esc.addCutPaper();
            Vector<Byte> datas = esc.getCommand();
            return datas;
      }
      *//**
       * 票据打印文字测试
       *
       * @return
       *//*
      public static Vector<Byte> getText() {
            EscCommand esc = new EscCommand();
            //初始化打印机
            esc.addInitializePrinter();
            //打印走纸多少个单位
            esc.addPrintAndFeedLines((byte) 3);
            // 设置打印文字居中
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            // 打印文字
            esc.addText("票據測試\n","BIG5");
            //打印并换行
            esc.addPrintAndLineFeed();
            // 设置打印左对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            //打印并换行
            esc.addPrintAndLineFeed();
            // 打印文字
            esc.addText("打印對齊方式測試:\n","BIG5");
            // 设置打印左对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            esc.addText("居左","BIG5");
            esc.addPrintAndLineFeed();
            // 设置打印居中对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            esc.addText("居中","BIG5");
            esc.addPrintAndLineFeed();
            // 设置打印居右对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.RIGHT);
            esc.addText("居右","BIG5");
            esc.addPrintAndLineFeed();
            // 设置打印左对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            // 打印文字
            esc.addText("正常字體\n","BIG5");
            //打印并换行
            esc.addPrintAndLineFeed();
            //设置放大倍数为宽高两倍
            esc.addSetCharcterSize(EscCommand.WIDTH_ZOOM.MUL_2, EscCommand.HEIGHT_ZOOM.MUL_2);
            esc.addText("兩倍字體\n","BIG5");
            //打印并换行
            esc.addPrintAndLineFeed();
            //设置放大倍数为宽高三倍
            esc.addSetCharcterSize(EscCommand.WIDTH_ZOOM.MUL_3, EscCommand.HEIGHT_ZOOM.MUL_3);
            esc.addText("三倍字體\n","BIG5");
            //设置放大倍数为正常大小
            esc.addSetCharcterSize(EscCommand.WIDTH_ZOOM.MUL_1, EscCommand.HEIGHT_ZOOM.MUL_1);
            //打印并换行
            esc.addPrintAndLineFeed();
            //设置汉字模式下划线
            esc.addSetKanjiFontMode(EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF,EscCommand.ENABLE.ON);
            //设置英数字正常大小下划线
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON);
            esc.addText("正常文字下劃線ABC123\n","BIG5");
            //打印并换行
            esc.addPrintAndLineFeed();
            //设置英数字倍宽倍高下划线
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON);
            //设置汉字倍宽倍高下划线
            esc.addSetKanjiFontMode(EscCommand.ENABLE.ON, EscCommand.ENABLE.ON,EscCommand.ENABLE.ON);
            esc.addText("倍寬倍高下劃線ABC123\n","BIG5");
            //打印并换行
            esc.addPrintAndLineFeed();
            //打印走纸n个单位
            esc.addPrintAndFeedLines((byte) 6);
            Vector<Byte> datas = esc.getCommand();
            return datas;
      }
      *//**
       * 票据打印58菜单
       *
       * @return
       *//*
      public static Vector<Byte> get58Menu() {
            EscCommand esc = new EscCommand();
            //初始化打印机
            esc.addInitializePrinter();
            //打印走纸多少个单位
            esc.addPrintAndFeedLines((byte) 3);
            // 设置打印文字居中
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            // 打印文字
            esc.addText("58菜單測試\n","BIG5");
            //打印并换行
            esc.addPrintAndLineFeed();
            // 设置打印左对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            //打印并换行
            esc.addPrintAndLineFeed();
            esc.addText("品名","BIG5");
            //设置绝对打印位置，距离左边距离200点
            esc.addSetAbsolutePrintPosition((byte)200);
            esc.addText("單價","BIG5");
            esc.addPrintAndLineFeed();
            esc.addText("測試測試","BIG5");
            //设置绝对打印位置，距离左边距离200点
            esc.addSetAbsolutePrintPosition((byte)200);
            esc.addText("13300","BIG5");
            esc.addPrintAndLineFeed();
            esc.addText("測試aab","BIG5");
            //设置绝对打印位置，距离左边距离200点
            esc.addSetAbsolutePrintPosition((byte)200);
            esc.addText("1120","BIG5");
            //打印并换行
            esc.addPrintAndLineFeed();
            //打印走纸n个单位
            esc.addPrintAndFeedLines((byte) 6);
            Vector<Byte> datas = esc.getCommand();
            return datas;
      }
      *//**
       * 票据打印80菜单
       *
       * @return
       *//*
      public static Vector<Byte> get80Menu() {
            EscCommand esc = new EscCommand();
            //初始化打印机
            esc.addInitializePrinter();
            //打印走纸多少个单位
            esc.addPrintAndFeedLines((byte) 3);
            // 设置打印文字居中
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            // 打印文字
            esc.addText("80菜單測試\n","BIG5");
            //打印并换行
            esc.addPrintAndLineFeed();
            // 设置打印左对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            //打印并换行
            esc.addPrintAndLineFeed();
            esc.addText("品名","BIG5");
            //设置绝对打印位置，距离左边距离200点
            esc.addSetAbsolutePrintPosition((short)300);
            esc.addText("單價","BIG5");
            esc.addPrintAndLineFeed();
            esc.addText("測試測試","BIG5");
            //设置绝对打印位置，距离左边距离200点
            esc.addSetAbsolutePrintPosition((short)300);
            esc.addText("13300","BIG5");
            esc.addPrintAndLineFeed();
            esc.addText("測試aab","BIG5");
            //设置绝对打印位置，距离左边距离200点
            esc.addSetAbsolutePrintPosition((short) 300);
            esc.addText("1120","BIG5");
            //打印并换行
            esc.addPrintAndLineFeed();
            //打印走纸n个单位
            esc.addPrintAndFeedLines((byte) 6);
            Vector<Byte> datas = esc.getCommand();
            return datas;
      }
      *//**
       * 票据打印图片
       *
       * @return
       *//*
      public static Vector<Byte> getBarCode() {
            EscCommand esc = new EscCommand();
            //初始化打印机
            esc.addInitializePrinter();
            esc.addText("打印二維碼測試:\n","BIG5");
            // 设置纠错等级
            esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31);
            // 设置qrcode模块大小
            esc.addSelectSizeOfModuleForQRCode((byte) 4);
            // 设置qrcode内容
            esc.addStoreQRCodeData("http:www.tysso.com");
            // 打印QRCode
            esc.addPrintQRCode();
            // 打印文字
            esc.addText("打印條碼測試:\n","BIG5");
            // 设置条码可识别字符位置在条码下方
            esc.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.BELOW);
            // 设置条码高度为60点
            esc.addSetBarcodeHeight((byte) 60);
            // 设置条码宽窄比为2
            esc.addSetBarcodeWidth((byte) 2);
            // 打印Code128码
            esc.addCODE39("01234");
            //打印并换行
            esc.addPrintAndLineFeed();
            //打印走纸n个单位
            esc.addPrintAndFeedLines((byte) 6);
            Vector<Byte> datas = esc.getCommand();
            return datas;
      }
      *//**
       * 票据打印图片
       *
       * @return
       */
      public static Vector<Byte> getPhoto(Bitmap b) {
            EscCommand esc = new EscCommand();
            //初始化打印机
          //  esc.addInitializePrinter();
            //打印走纸多少个单位
         //   esc.addPrintAndFeedLines((byte) 1);
          //  esc.addText("打印Bitmap圖測試:\n","BIG5");

            // 打印图片  光栅位图  384代表打印图片像素  0代表打印模式
            // 58mm打印机 可打印区域最大点数为 384 ，80mm 打印机 可打印区域最大点数为 576
            esc.addRastBitImage(b, 570, 0);
            //打印并换行
          //  esc.addPrintAndLineFeed();
            //打印走纸n个单位
           // esc.addPrintAndFeedLines((byte) -1);
            Vector<Byte> datas = esc.getCommand();
            return datas;
      }
      /**
       * 票据使用切刀
       *
       * @return
       */
      public static Vector<Byte> openCut() {
            EscCommand esc = new EscCommand();
            //初始化打印机
            esc.addInitializePrinter();
            //打印走纸多少个单位
           // esc.addPrintAndFeedLines((byte) 3);
            //esc.addText("切刀並走紙6行:\n","BIG5");
            //打印走纸多少个单位
            esc.addPrintAndFeedLines((byte) 1);
            //切刀并走纸多少个单位
            esc.addCutAndFeedPaper((byte)2);
            Vector<Byte> datas = esc.getCommand();
            return datas;
      }
      /**
       * 票据控制蜂鸣器
       *
       * @return
       */
      public static Vector<Byte> openSound( ) {
            EscCommand esc = new EscCommand();
            //初始化打印机
            esc.addInitializePrinter();
            //打印走纸多少个单位
            esc.addPrintAndFeedLines((byte) 3);
            esc.addText("蜂鳴器響三次，間隔時間: 250ms\n","BIG5");
            //打印并换行
            esc.addPrintAndLineFeed();
            //打印走纸n个单位
            esc.addPrintAndFeedLines((byte) 6);
            ///添加蜂鸣器响声 3次  时间间隔 5*50ms
            esc.addSound((byte) 0x03,(byte) 0x05);
            Vector<Byte> datas = esc.getCommand();
            return datas;
      }
}
