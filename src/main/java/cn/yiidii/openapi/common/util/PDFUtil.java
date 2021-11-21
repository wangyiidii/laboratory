package cn.yiidii.openapi.common.util;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.yiidii.openapi.free.model.ex.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * PDF工具
 *
 * @author YiiDii Wang
 * @create 2021-11-21 20:31
 */
public class PDFUtil {

    public static final String PDF_SUFFIX = "pdf";

    /**
     * 获取pdf文本
     *
     * @param file pdf文件
     * @return 文本
     */
    public static String getText(File file) {
        BufferedInputStream bis = null;
        PdfReader pdfReader = null;
        StringBuilder textSB = new StringBuilder();
        try {
            bis = FileUtil.getInputStream(file);
            pdfReader = new PdfReader(bis);
            int pages = pdfReader.getNumberOfPages();
            for (int i = 1; i <= pages; i++) {
                // 读取第i页的文档内容
                textSB = textSB.append(PdfTextExtractor.getTextFromPage(pdfReader, i));
            }
            return textSB.toString();
        } catch (IOException e) {
            throw new DocumentException(StrUtil.format("Exception occurred when reading pdf text! ({})", e.getMessage()));
        } finally {
            if (Objects.nonNull(bis)) {
                try {
                    bis.close();
                } catch (IOException ignored) {
                }
            }
            if (Objects.nonNull(pdfReader)) {
                pdfReader.close();
            }
        }
    }

    /**
     * 是否是pdf结尾
     *
     * @param fileName 文件名称
     * @return true 是pdf结尾
     */
    public static boolean isPdf(String fileName) {
        return StrUtil.equalsIgnoreCase(FileTypeUtil.getType(fileName), PDF_SUFFIX);
    }

    /**
     * 是否是pdf结尾
     *
     * @param file 文件名称
     * @return true 是pdf结尾
     */
    public static boolean isPdf(File file) {
        return StrUtil.equalsIgnoreCase(FileTypeUtil.getType(file), PDF_SUFFIX);
    }

}
