package tests;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Sample3 {
    public static void main(String[] args) {
        String dosyaAdi = "src/test/java/resources/Vehicles.xlsx"; // Excel dosyasının adını ve yolunu buraya girin.
        List<String[]> excelData = readExcelData(dosyaAdi);

        List<String[]> filteredData = filterExcelData(excelData);


        // Filtrelenmiş verileri yazdırma
        for (String[] row : filteredData) {
            System.out.println(String.join(" ", row));
        }
    }

    public static List<String[]> readExcelData(String filePath) {
        List<String[]> dataList = new ArrayList<>();

        try {
            FileInputStream file = new FileInputStream(new File(filePath));

            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();

                List<String> rowData = new ArrayList<>();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    if (cell.getCellType() == CellType.STRING) {
                        rowData.add(cell.getStringCellValue());
                    } else if (cell.getCellType() == CellType.NUMERIC) {
                        rowData.add(String.valueOf(cell.getNumericCellValue()));
                    } else if (cell.getCellType() == CellType.BLANK) {
                        rowData.add("");
                    }
                }

                dataList.add(rowData.toArray(new String[0]));
            }

            file.close();
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataList;
    }

    public static List<String[]> filterExcelData(List<String[]> excelData) {
        List<String[]> filteredList = new ArrayList<>();

        for (String[] row : excelData) {
            String plaka = row[0];
            String marka = row[1];
            String renk = row[2];
            String tarih = row[3];

            // Plaka formatı kontrolü
            if (plaka.matches("[A-Za-z]{2}\\d{2,3}[A-Za-z]{0,3}|[A-Za-z]{2}\\d{2,3}(\\s[A-Za-z]{0,3})?")) {
                // Araba markası kontrolü
                if (marka.equals("BMW") || marka.equals("AUDI") || marka.equals("VW") || marka.equals("MERCEDES")) {
                    // Renk kontrolü
                    if (renk.equals("WHITE") || renk.equals("BLACK") || renk.equals("RED") || renk.equals("BLUE")) {
                        // Tarih kontrolü
                        if (isValidDate(tarih)) {
                            filteredList.add(new String[]{plaka, marka, renk, tarih});
                        }
                    }
                }
            }
        }

        return filteredList;
    }

    // Geçerli tarih kontrolü metodu
    public static boolean isValidDate(String dateStr) {
        try {
            String[] dateParts = dateStr.split("-");
            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int year = Integer.parseInt(dateParts[2]);

            // Tarihin geçerli olup olmadığını kontrol ediyoruz
            if (day > 0 && day <= 31 && month > 0 && month <= 12 && year >= 1900) {
                // Şu anki tarihten ileri bir tarih mi kontrol ediyoruz
                String currentDateStr = "23-07-2023"; // Bugünün tarihi (örnek olarak)
                String[] currentDateParts = currentDateStr.split("-");
                int currentDay = Integer.parseInt(currentDateParts[0]);
                int currentMonth = Integer.parseInt(currentDateParts[1]);
                int currentYear = Integer.parseInt(currentDateParts[2]);

                if (year > currentYear || (year == currentYear && month > currentMonth) || (year == currentYear && month == currentMonth && day > currentDay)) {
                    return false;
                }

                return true;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
        }

        return false;
    }
    }

