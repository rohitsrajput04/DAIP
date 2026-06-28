package com.db.daip.rag;

import com.db.daip.exception.DaipException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Extracts plain text from PDF, Word (.doc/.docx), Excel (.xls/.xlsx), and text files.
 */
@Service
@Slf4j
public class TextExtractionService {

    public String extract(Path filePath, String fileName, String contentType) {
        String lowerName = fileName != null ? fileName.toLowerCase() : "";

        try (InputStream inputStream = Files.newInputStream(filePath)) {
            if (isPdf(lowerName, contentType)) {
                return extractPdf(filePath);
            }
            if (isDocx(lowerName, contentType)) {
                return extractDocx(inputStream);
            }
            if (isDoc(lowerName, contentType)) {
                return extractDoc(inputStream);
            }
            if (isXlsx(lowerName, contentType)) {
                return extractExcel(inputStream, true);
            }
            if (isXls(lowerName, contentType)) {
                return extractExcel(inputStream, false);
            }
            if (isText(lowerName, contentType)) {
                return Files.readString(filePath);
            }
            throw new DaipException("Unsupported file type: " + fileName);
        } catch (IOException e) {
            throw new DaipException("Failed to extract text from " + fileName + ": " + e.getMessage(), e);
        }
    }

    private String extractPdf(Path filePath) throws IOException {
        try (PDDocument document = Loader.loadPDF(filePath.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document).trim();
        }
    }

    private String extractDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText().trim();
        }
    }

    private String extractDoc(InputStream inputStream) throws IOException {
        try (HWPFDocument document = new HWPFDocument(inputStream);
             WordExtractor extractor = new WordExtractor(document)) {
            return extractor.getText().trim();
        }
    }

    private String extractExcel(InputStream inputStream, boolean xlsx) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (Workbook workbook = xlsx ? new XSSFWorkbook(inputStream) : new HSSFWorkbook(inputStream)) {
            DataFormatter formatter = new DataFormatter();
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                builder.append("Sheet: ").append(sheet.getSheetName()).append('\n');
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        builder.append(formatter.formatCellValue(cell)).append('\t');
                    }
                    builder.append('\n');
                }
                builder.append('\n');
            }
        }
        return builder.toString().trim();
    }

    private boolean isPdf(String name, String contentType) {
        return name.endsWith(".pdf") || (contentType != null && contentType.contains("pdf"));
    }

    private boolean isDocx(String name, String contentType) {
        return name.endsWith(".docx")
                || "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType);
    }

    private boolean isDoc(String name, String contentType) {
        return name.endsWith(".doc") || "application/msword".equals(contentType);
    }

    private boolean isXlsx(String name, String contentType) {
        return name.endsWith(".xlsx")
                || "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType);
    }

    private boolean isXls(String name, String contentType) {
        return name.endsWith(".xls") || "application/vnd.ms-excel".equals(contentType);
    }

    private boolean isText(String name, String contentType) {
        return name.endsWith(".txt") || name.endsWith(".csv")
                || (contentType != null && (contentType.startsWith("text/") || contentType.contains("csv")));
    }
}
