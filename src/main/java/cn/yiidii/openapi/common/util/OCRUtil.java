package cn.yiidii.openapi.common.util;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.StrUtil;
import cn.yiidii.openapi.free.model.ex.DocumentException;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 * OCR工具
 *
 * @author YiiDii Wang
 * @create 2021-11-18 22:58
 */
@Slf4j
public class OCRUtil {

    private static final String TRAINED_DATA_PATH = "./tess4j";

    public static String ocr(File file) throws TesseractException {
        return ocr(file, true);
    }

    public static String ocr(File file, boolean zh) throws TesseractException {
        if (isPic(file)) {
            return ocrImg(file, zh);
        } else if (PDFUtil.isPdf(file)) {
            return PDFUtil.getText(file);
        }
        throw new DocumentException("file is not a picture(jpg/png/jpeg/bmp) or pdf");
    }

    /**
     * 图片OCR
     *
     * @param imgFile 图片文件
     * @param zh      知否中文
     * @return
     */
    public static String ocrImg(File imgFile, boolean zh) throws TesseractException {
        Tesseract instance = new Tesseract();
        instance.setTessVariable("user_defined_dpi", "300");
        // 设置中文简体训练库
        String dataPath = Thread.currentThread().getContextClassLoader().getResource(TRAINED_DATA_PATH).getPath().substring(1);
        instance.setDatapath(dataPath);
        if (zh) {
            // 中文识别
            instance.setLanguage("chi_sim");
        }
        return instance.doOCR(imgFile);
    }

    /**
     * 是不是图片
     *
     * @param file 图片文件
     * @return true 图片
     */
    public static boolean isPic(File file) {
        return StrUtil.containsAnyIgnoreCase(FileTypeUtil.getType(file), "jpg", "png", "bmp");
    }

    /**
     * 是不是图片
     *
     * @param path 图片路径
     * @return true 图片
     */
    public static boolean isPic(String path) {
        return StrUtil.containsAnyIgnoreCase(FileTypeUtil.getType(path), "jpg", "png", "bmp");
    }


}