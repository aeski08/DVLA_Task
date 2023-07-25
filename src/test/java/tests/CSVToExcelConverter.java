package tests;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class CSVToExcelConverter {


    public static void main(String[] args) {
        String csvFilePath = "src/test/java/resources/vehicles.csv";
        String excelFilePath = "src/test/java/resources/Vehicle.xlsx";

        try {
            FileInputStream fileInputStream = new FileInputStream(csvFilePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Sheet1");
            String line;
            int rowNumber = 0;

            while ((line = reader.readLine()) != null) {
                Row row = sheet.createRow(rowNumber++);
                String[] data = line.split(",");

                int cellNumber = 0;
                for (String value : data) {
                    Cell cell = row.createCell(cellNumber++);
                    cell.setCellValue(value);
                }
            }

            reader.close();

            FileOutputStream fileOutputStream = new FileOutputStream(excelFilePath);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            workbook.close();

            System.out.println("CSV data has been successfully converted to Excel.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
