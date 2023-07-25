package tests;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DvlaTask {
    @Test

    public void test() {
        String fileName = "src/test/java/resources/Vehicle.xlsx"; // Excel path
        List<String[]> excelData = readExcelData(fileName);

        List<String[]> filteredData = filterExcelData(excelData);

        //  Filtered data writing
//        for (String[] row : filteredData) {
//            //  System.out.println(String.join(" ", row));
//        }
        writeValidDataToExcel(filteredData);

    }

    public List<String[]> readExcelData(String filePath) {
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

    public List<String[]> filterExcelData(List<String[]> excelData) {
        List<String[]> filteredList = new ArrayList<>();

        // int countValid = 0;
        for (String[] row : excelData) {
            String VRN = row[0];
            String Make = row[1];
            String Colour = row[2];
            String dateOfManufacture = row[3];

            // VRN validation
            // if (VRN.matches("[A-Za-z]{2}\\d{2}[A-Za-z]{3}|[A-Za-z]{2}\\d{2}(\\s[A-Za-z]{3})?")) {
            String regex = "^[A-Z]{2}\\d{2}(\\s?[A-Z]{3})?$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(VRN);

            // VRN validation
            if (matcher.matches()) {
                // Make validation
                if (makeValidation(Make)) {
                    // Colour validation
                    if (colourValidation(Colour)) {
                        //  System.out.println("Colour = " + Colour);
                        // Date of Manufacture validation
                        if (isValidDate(dateOfManufacture)) {
                            // System.out.println("dateOfManufacture = " + dateOfManufacture);
                            filteredList.add(new String[]{VRN, Make, Colour, dateOfManufacture, "valid"});

                        } else {
                            filteredList.add(new String[]{VRN, Make, Colour, dateOfManufacture, "invalid"});
                        }
                    }
                }
            } else {
                filteredList.add(new String[]{VRN, Make, Colour, dateOfManufacture, "invalid"});

            }
        }

        return filteredList;
    }

    boolean makeValidation(String Make) {
        boolean flag = false;

        if (Make.equalsIgnoreCase("BMW") || Make.equalsIgnoreCase("AUDI") || Make.equalsIgnoreCase("VW") || Make.equalsIgnoreCase("MERCEDES")) {
            flag = true;
        } else {
            System.out.println(Make + " is invalid");
        }
        return flag;
    }

    boolean colourValidation(String Colour) {
        boolean flag = false;

        if (Colour.equalsIgnoreCase("WHITE") || Colour.equalsIgnoreCase("BLACK") || Colour.equalsIgnoreCase("RED") || Colour.equalsIgnoreCase("BLUE")) {
            flag = true;
        } else {
            System.out.println(Colour + " is invalid");
        }
        return flag;
    }

    // Current date validation
    public boolean isValidDate(String dateStr) {
        try {
            String[] dateParts;
            int day = 0;
            int month = 0;
            int year = 0;
            if (dateStr.contains("/")) {
                dateParts = dateStr.split("/");
            } else {
                dateParts = dateStr.split("-");
                System.out.println("invalid date " + dateStr);
            }
            day = Integer.parseInt(dateParts[0]);
            month = Integer.parseInt(dateParts[1]);
            year = Integer.parseInt(dateParts[2]);

            // Date validation
            if (day > 0 && day <= 31 && month > 0 && month <= 12 && year >= 1900) {
                // Future date validation

                LocalDate currentDate = LocalDate.now();

                // Format the date as day/month/year
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String currentDateStr = currentDate.format(formatter);

                //   System.out.println("currentDateStr = " + currentDateStr);

                String[] currentDateParts = currentDateStr.split("/");
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

    // Valid data write new excel file with this method
    public void writeValidDataToExcel(List<String[]> filteredData) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("ValidData");

        // ADD header on new Excel file
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("VRN");
        headerRow.createCell(1).setCellValue("Make");
        headerRow.createCell(2).setCellValue("Colour");
        headerRow.createCell(3).setCellValue("Date of Manufacture");
        headerRow.createCell(4).setCellValue("isValid");

        int validRowCount = 0;

        // Write filtered data on excel file
        for (int i = 0; i < filteredData.size(); i++) {
            String[] row = filteredData.get(i);

            if (row[4].equals("valid")) {
                validRowCount++;

                Row excelRow = sheet.createRow(validRowCount);
                excelRow.createCell(0).setCellValue(format_VRN(row[0]));
                excelRow.createCell(1).setCellValue(format_Make(row[1]));
                excelRow.createCell(2).setCellValue(format_Colour(row[2]));
                excelRow.createCell(3).setCellValue(format_Date(row[3]));
                excelRow.createCell(4).setCellValue(row[4]);
            }
        }

        // Excel file creation "valid.xlsx"
        try (FileOutputStream fileOut = new FileOutputStream("src/test/java/resources/valid.xlsx")) {
            workbook.write(fileOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Valid data count
        System.out.println("-------------");
        System.out.println("Valid data number: " + validRowCount);
    }

    //method to get valid VRN
    public String format_VRN(String formatVRN) {
        formatVRN = formatVRN.toUpperCase().replaceAll(" ", "");
        return formatVRN.substring(0, 4) + " " + formatVRN.substring(4);

    }

    //method to get valid Make
    public String format_Make(String formatMake) {
        formatMake = formatMake.toUpperCase();
        if (formatMake.equals("AUDI") || formatMake.equals("MERCEDES")) {
            formatMake = formatMake.charAt(0) + formatMake.substring(1).toLowerCase();
        }
        return formatMake;
    }

    //method to get valid Colour
    public String format_Colour(String formatColour) {
        formatColour = formatColour.toUpperCase();
        formatColour = formatColour.charAt(0) + formatColour.substring(1).toLowerCase();
        return formatColour;
    }

    //method to get valid Date of Manufacture
    public String format_Date(String formatDate) {
        DateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat targetFormat = new SimpleDateFormat("EEE, dd MMMM yyyy");
        Date date;
        try {
            date = originalFormat.parse(formatDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return targetFormat.format(date);
    }
}
