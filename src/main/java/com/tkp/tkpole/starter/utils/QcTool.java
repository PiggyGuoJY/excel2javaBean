package com.tkp.tkpole.starter.utils;

import com.google.common.base.Charsets;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.tkp.tkpole.starter.utils.model.Msg;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

import static com.tkp.tkpole.starter.utils.Assert.notNul;
import static com.tkp.tkpole.starter.utils.Assert.notNull;
import static com.tkp.tkpole.starter.utils.model.Msg.msg;

/**
 * 二维码工具
 *
 * <p> 创建时间：2019/1/4
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor( access = AccessLevel.PRIVATE)
public final class QcTool {

    /**
     * 生成二维码
     * */

    public static Msg<byte[]> encode2Bytes (@NonNull URI uri) {
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        ) {
            ImageIO.write( createImage( uri, ( byte[])null, true), FORMAT, byteArrayOutputStream);
            return msg( byteArrayOutputStream.toByteArray());
        } catch ( WriterException | IOException e) {
            return msg( e);
        }
    }
    public static Msg<byte[]> encode2Bytes (@NonNull URI uri, @NonNull byte[] logoImage) {
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        ) {
            ImageIO.write( createImage( uri, logoImage, true), FORMAT, byteArrayOutputStream);
            return msg( byteArrayOutputStream.toByteArray());
        } catch ( WriterException | IOException e) {
            return msg( e);
        }
    }
    public static Msg<byte[]> encode2Bytes (@NonNull URI uri, @NonNull byte[] logoImage, boolean needCompress) {
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        ) {
            ImageIO.write( createImage( uri, logoImage, needCompress), FORMAT, byteArrayOutputStream);
            return msg( byteArrayOutputStream.toByteArray());
        } catch ( WriterException | IOException e) {
            return msg( e);
        }
    }
    public static Msg<BufferedImage> encode2Image (@NonNull URI uri) {
        try {
            return msg( createImage( uri, ( Path)null, true));
        } catch ( WriterException | IOException e) {
            return msg( e);
        }
    }
    public static Msg<BufferedImage> encode (@NonNull URI uri, @NonNull Path logoImage) {
        try {
            return msg( createImage( uri, logoImage, true));
        } catch ( WriterException | IOException e) {
            return msg( e);
        }
    }
    public static Msg<BufferedImage> encode (@NonNull URI uri, @NonNull Path logoImage, boolean needCompress) {
        try {
            return msg( createImage( uri, logoImage, needCompress));
        } catch ( WriterException | IOException e) {
            return msg( e);
        }
    }


    /**
     * 解析二维码
     * */

    public static Msg<URI> decode (@NonNull Path qcPath) {
        try {
            return msg( parse( qcPath));
        } catch ( IOException | NotFoundException e) {
            return msg( e);
        }
    }
    public static Msg<URI> decode (@NonNull byte[] bytes) {
        try {
            return msg( parse( bytes));
        } catch ( IOException | NotFoundException e) {
            return msg( e);
        }
    }

    //==== 华丽的分割线 === 私有资源

    private static final String FORMAT = "JPG";

    private static final Map<EncodeHintType,Object> HINTS = new EnumMap<>( EncodeHintType.class);
    private static final Map<DecodeHintType,Object> HINTS2 = new EnumMap<>( DecodeHintType.class);
    static {
        HINTS.put( EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        HINTS.put( EncodeHintType.CHARACTER_SET, Charsets.UTF_8.name());
        HINTS.put( EncodeHintType.MARGIN, 1);
        HINTS2.put( DecodeHintType.CHARACTER_SET, Charsets.UTF_8.name());
    }

    private static final int QRCODE_SIZE = 300;
    private static final int QRCODE_LOGO_WIDTH = 60;
    private static final int QRCODE_LOGO_HEIGHT = 60;
    private static final int MARK = 0xFF000000;
    private static final int BLANK = 0xFFFFFFFF;

    private static BufferedImage createImage( URI uri, byte[] logoImage, boolean needCompress) throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode( uri.toString(), BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, HINTS);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) { for (int y = 0; y < height; y++) { image.setRGB( x, y, bitMatrix.get( x, y) ? MARK : BLANK); } }
        if ( !notNul( logoImage)) { return image; }
        QcTool.insertImage( image, logoImage, needCompress);
        return image;
    }
    private static BufferedImage createImage( URI uri, Path logoImage, boolean needCompress) throws WriterException, IOException{
        BitMatrix bitMatrix = new MultiFormatWriter().encode( uri.toString(), BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, HINTS);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) { for (int y = 0; y < height; y++) { image.setRGB( x, y, bitMatrix.get( x, y) ? MARK : BLANK); } }
        if ( !notNull( logoImage)) { return image; }
        QcTool.insertImage( image, logoImage, needCompress);
        return image;
    }
    private static void insertImage( BufferedImage source, byte[] logoImage, boolean needCompress) throws IOException{
        try ( InputStream inputStream = new ByteArrayInputStream( logoImage)) { doInsertImage( source, inputStream, needCompress); }
    }
    private static void insertImage( BufferedImage source, Path logoImgPath, boolean needCompress) throws IOException {
        if ( !Files.exists( logoImgPath)||!Files.isRegularFile( logoImgPath)) {
            log.warn( "二维码LOGO文件 {} 不存在或不是一个文件, 忽略添加LOGO过程", logoImgPath);
            return;
        }
        try ( InputStream inputStream = Files.newInputStream( logoImgPath)) { doInsertImage( source, inputStream, needCompress); }
    }
    private static void doInsertImage( BufferedImage source, InputStream inputStream, boolean needCompress) throws IOException {
        Image src = ImageIO.read( inputStream);
        int width = src.getWidth( null);
        int height = src.getHeight( null);
        if ( needCompress) {
            if ( width > QRCODE_LOGO_WIDTH) { width = QRCODE_LOGO_WIDTH; }
            if ( height > QRCODE_LOGO_HEIGHT) { height = QRCODE_LOGO_HEIGHT; }
            Image image = src.getScaledInstance( width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage( image, 0, 0, null);
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage( src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float( x, y, width, width, 6, 6);
        graph.setStroke( new BasicStroke(3f));
        graph.draw( shape);
        graph.dispose();
    }

    private static URI parse( Path path) throws IOException, NotFoundException {
        try ( InputStream inputStream = Files.newInputStream( path)) { return parser( inputStream); }
    }
    private static URI parse( byte[] bytes) throws IOException, NotFoundException {
        try ( InputStream inputStream = IOUtils.toInputStream( new String( bytes, Charsets.UTF_8), Charsets.UTF_8)) { return parser( inputStream); }
    }
    private static URI parser( InputStream inputStream) throws IOException, NotFoundException {
        BufferedImage image;
        image = ImageIO.read( inputStream);
        if ( image == null) { return null; }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource( image);
        BinaryBitmap bitmap = new BinaryBitmap( new HybridBinarizer( source));
        Result result;
        result = new MultiFormatReader().decode( bitmap, HINTS2);
        String resultStr = result.getText();
        return URI.create( resultStr);
    }
}