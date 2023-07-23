package tests;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DvlaTask {

    public static void main(String[] args) {
        String fileName = "src/test/java/resources/Vehicle.xlsx"; // Excel path
        List<String[]> excelData = readExcelData(fileName);

        List<String[]> filteredData = filterExcelData(excelData);


       //  Filtrelenmiş verileri yazdırma
        for (String[] row : filteredData) {
            System.out.println(String.join(" ", row));

        }
        writeValidDataToExcel(filteredData);
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
            String VRN = row[0];
            String Make = row[1];
            String Colour = row[2];
            String dateOfManufacture = row[3];

            // VRN validation
            if (VRN.matches("[A-Za-z]{2}\\d{2}[A-Za-z]{3} || [A-Za-z]{2}\\d{2}(\\s[A-Za-z]{3})?")) {
                // Make validation
                if (Make.equalsIgnoreCase("BMW") || Make.equalsIgnoreCase("AUDI") || Make.equalsIgnoreCase("VW") || Make.equalsIgnoreCase("MERCEDES")) {
                    // Colour validation
                    if (Colour.equalsIgnoreCase("WHITE") || Colour.equalsIgnoreCase("BLACK") || Colour.equalsIgnoreCase("RED") || Colour.equalsIgnoreCase("BLUE")) {
                        // Date of Manufacture validation
                        if (isValidDate(dateOfManufacture)) {
                            filteredList.add(new String[]{VRN, Make, Colour, dateOfManufacture, " --->valid"});



                        } else {
                            filteredList.add(new String[]{VRN, Make, Colour, dateOfManufacture, " --->invalid"});
                        }
                    }
                }
            } else {
                filteredList.add(new String[]{VRN, Make, Colour, dateOfManufacture, " ->invalid"});
            }
        }

        return filteredList;
    }

    // Current date validation
    public static boolean isValidDate(String dateStr) {
        try {
            String[] dateParts = dateStr.split("-");
            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int year = Integer.parseInt(dateParts[2]);

            // Date validation
            if (day > 0 && day <= 31 && month > 0 && month <= 12 && year >= 1900) {
                // Future date validation
                String currentDateStr = "26-07-2023"; // Current date
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

    // Geçerli verileri yeni bir Excel dosyasına yazma metodu
    public static void writeValidDataToExcel(List<String[]> filteredData) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("ValidData");

        // Excel dosyasına başlık (header) ekliyoruz
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("VRN");
        headerRow.createCell(1).setCellValue("Make");
        headerRow.createCell(2).setCellValue("Colour");
        headerRow.createCell(3).setCellValue("Date of Manufacture");

        int validRowCount = 0;

        // Filtrelenmiş verileri Excel dosyasına yazıyoruz
        for (int i = 0; i < filteredData.size(); i++) {
            String[] row = filteredData.get(i);

            if (row[4].equals("valid")) {
                validRowCount++;

                Row excelRow = sheet.createRow(validRowCount);
                excelRow.createCell(0).setCellValue(row[0]);
                excelRow.createCell(1).setCellValue(row[1]);
                excelRow.createCell(2).setCellValue(row[2]);
                excelRow.createCell(3).setCellValue(row[3]);
                excelRow.createCell(4).setCellValue("VALID");
            }
        }

        // Excel file creation "valid.xlsx"
        try (FileOutputStream fileOut = new FileOutputStream("src/test/java/resources/valid.xlsx")) {
            workbook.write(fileOut);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Valid data count
        System.out.println("Valid data number: " + validRowCount);
    }

}
