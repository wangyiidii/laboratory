package cn.yiidii.openapi.common.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.yiidii.openapi.free.model.ex.DocumentException;
import com.aspose.cells.Workbook;
import com.aspose.slides.Presentation;
import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.google.common.collect.Sets;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import javax.activation.UnsupportedDataTypeException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Office 工具
 *
 * @author YiiDii Wang
 * @create 2021-11-19 17:43
 */
@Slf4j
@UtilityClass
@SuppressWarnings("unused")
public class Office2Pdf {

    private static final String TEMP_DIR = "./temp";
    private static final Set<String> DOC_SUFFIX;
    private static final Set<String> EXCEL_SUFFIX;
    private static final Set<String> PPT_SUFFIX;

    static {
        DOC_SUFFIX = Sets.newHashSet("doc", "docx", "docm", "dotx", "dotm", "txt");
        EXCEL_SUFFIX = Sets.newHashSet("xls", "xlsx", "xlsm", "xltx", "xltm", "xlsb", "xlam", "csv");
        PPT_SUFFIX = Sets.newHashSet("ppt", "pptx", "pptm", "ppsx", "ppsm", "potx", "potm", "ppam");
    }

    /**
     * doc转Pdf
     *
     * @param file file
     * @return pdf
     */
    public static File doc2Pdf(File file) {
        // 检查license
        checkLic();

        File pdfFile = FileUtil.file(getTempFilePath(file));
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            bos = FileUtil.getOutputStream(pdfFile);
            bis = FileUtil.getInputStream(file);
            Document doc = new Document(bis);
            // 全面支持DOC, DOCX, OOXML, RTF HTML,
            doc.save(bos, SaveFormat.PDF);
            return pdfFile;
        } catch (Exception e) {
            pdfFile.delete();
            throw new DocumentException("转换失败");
        } finally {
            if (Objects.nonNull(bos)) {
                try {
                    bos.close();
                } catch (IOException ignored) {
                }
            }
            if (Objects.nonNull(bis)) {
                try {
                    bis.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * excel转pdf
     *
     * @param file file
     * @return pdf
     */
    public static File excel2Pdf(File file) {
        // 检查license
        checkCellsLic();

        File pdfFile = FileUtil.file(getTempFilePath(file));
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            bos = FileUtil.getOutputStream(pdfFile);
            bis = FileUtil.getInputStream(file);
            Workbook wb = new Workbook(file.getAbsolutePath());
            wb.save(bos, com.aspose.cells.SaveFormat.PDF);
            bos.close();
            return pdfFile;
        } catch (Exception e) {
            pdfFile.delete();
            throw new DocumentException("转换失败");
        } finally {
            if (Objects.nonNull(bos)) {
                try {
                    bos.close();
                } catch (IOException ignored) {
                }
            }
            if (Objects.nonNull(bis)) {
                try {
                    bis.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * ppt转pdf
     *
     * @param file file
     * @return pdf
     */
    public static File ppt2Pdf(File file) {
        // 检查license
        checkSlideLic();

        File pdfFile = FileUtil.file(getTempFilePath(file));
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            bos = FileUtil.getOutputStream(pdfFile);
            bis = FileUtil.getInputStream(file);
            Presentation pres = new Presentation(bis);
            pres.save(bos, com.aspose.slides.SaveFormat.Pdf);
            return pdfFile;
        } catch (Exception e) {
            pdfFile.delete();
            throw new DocumentException("转换失败");
        } finally {
            if (Objects.nonNull(bos)) {
                try {
                    bos.close();
                } catch (IOException ignored) {
                }
            }
            if (Objects.nonNull(bis)) {
                try {
                    bis.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 转换pdf通用方法
     *
     * @param file 待转换的文件
     * @return pdf文件
     * @throws UnsupportedDataTypeException 异常
     */
    public static File convert(File file) throws UnsupportedDataTypeException {
        String suffix = FileUtil.getSuffix(file);
        if (containsIgnoreCase(DOC_SUFFIX, suffix)) {
            return doc2Pdf(file);
        } else if (containsIgnoreCase(EXCEL_SUFFIX, suffix)) {
            return excel2Pdf(file);
        } else if (containsIgnoreCase(PPT_SUFFIX, suffix)) {
            return ppt2Pdf(file);
        }
        throw new UnsupportedDataTypeException(StrUtil.format("file with suffix '{}' is not support", suffix));
    }

    private static boolean containsIgnoreCase(Collection<? extends String> collection, String s) {
        return collection.stream().filter(e -> StrUtil.containsIgnoreCase(e, s)).findFirst().isPresent();
    }

    private void checkLic() {
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("aspose/license.xml");
            License license = new License();
            license.setLicense(is);
        } catch (Exception e) {
            log.error("获取aspose license异常, e: {}", e.getMessage());
            throw new DocumentException(-1, e.getMessage());
        }
    }

    private void checkCellsLic() {
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("aspose/license.xml");
            com.aspose.cells.License license = new com.aspose.cells.License();
            license.setLicense(is);
        } catch (Exception e) {
            log.error("获取aspose slide license异常, e: {}", e.getMessage());
            throw new DocumentException(-1, e.getMessage());
        }
    }

    private void checkSlideLic() {
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("aspose/license.xml");
            com.aspose.slides.License license = new com.aspose.slides.License();
            license.setLicense(is);
        } catch (Exception e) {
            log.error("获取aspose slide license异常, e: {}", e.getMessage());
            throw new DocumentException(-1, e.getMessage());
        }
    }

    private String getTempFilePath(File file) {
        return TEMP_DIR.concat(File.separator)
                .concat(DateUtil.formatDate(new Date())).concat(File.separator)
                .concat(FileUtil.mainName(file)).concat(".pdf");
    }
}
