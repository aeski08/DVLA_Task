package tests;

import java.util.ArrayList;
import java.util.List;

public class Sample2 {
    public static void main(String[] args) {


        // Tabloyu temsil eden verileri oluşturuyoruz
        String[][] excelData = {
                {"aB21xyZ", "BMW", "BEYAZ", "01-01-2022"},
                {"aB21 xyZ", "AUDI", "SİYAH", "15-12-2021"},
                {"cD34klM", "VW", "KIRMIZI", "30-09-2023"},
                {"eF56uv4", "MERCEDES", "MAVİ", "05-03-2020"},
                {"gH78abX", "BMW", "MAVİ", "10-06.2024"},
                {"iJ90cdY", "TOYOTA", "BEYAZ", "20-08/2022"},
                {"jK12 mnY", "FORD", "SİYAH", "27-11-2023"} // Test için eklenen veri
        };

        List<String[]> filteredData = filterExcelData(excelData);


        // Filtrelenmiş verileri yazdırma
        for (String[] row : filteredData) {
            String isValid = row[4].equals("valid") ? "VALIDET" : "INVALIDET";
            System.out.println(String.join(" ", row) + " " + isValid);
        }
    }

    public static List<String[]> filterExcelData(String[][] excelData) {
        List<String[]> filteredList = new ArrayList<>();

        for (String[] row : excelData) {
            String VRN = row[0]; // 1. sütundaki veriyi olduğu gibi alıyoruz
            String Make = row[1];
            String Colour = row[2];
            String dateOfManufacture = row[3];

            // Plaka formatı kontrolü
            if (VRN.matches("[A-Za-z]{2}\\d{2,3}[A-Za-z]{0,3}|[A-Za-z]{2}\\d{2,3}(\\s[A-Za-z]{0,3})?")) {
                // Araba markası kontrolü
                if (Make.equals("BMW") || Make.equals("AUDI") || Make.equals("VW") || Make.equals("MERCEDES")) {
                    // Renk kontrolü
                    if (Colour.equals("BEYAZ") || Colour.equals("SİYAH") || Colour.equals("KIRMIZI") || Colour.equals("MAVİ")) {
                        // Tarih kontrolü
                        if (isValidDate(dateOfManufacture)) {
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
