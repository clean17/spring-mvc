package hello.mvc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
public class JavaTest {

    @Test
    @DisplayName("날짜 테스트")
    void getDate() {
        String inputDate = "2010. 10. 04."; // 또는 null일 경우 오늘 날짜 처리
        String addDate = processDate(inputDate);

        // 처리된 날짜 출력
        System.out.println("Processed addDate: " + addDate);

        String inputDate2 = "6시간전"; // 또는 null일 경우 오늘 날짜 처리
        String addDate2 = processDate(inputDate2);

        // 처리된 날짜 출력
        System.out.println("Processed addDate2: " + addDate2);
    }

    private static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";
    private static final String INPUT_DATE_FORMAT = "yyyy. MM. dd.";

    public static String processDate(String inputDate) {
        if (ObjectUtils.isEmpty(inputDate)) {
            return getTodayDate();
        }

        // yyyy. MM. dd. 형식이 아닐 경우
        if (!isValidFormat(inputDate, INPUT_DATE_FORMAT)) {
            return getTodayDate();
        }

        return convertToYYYYMMDD(inputDate);
    }

    public static String getTodayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return dateFormat.format(new Date());
    }

    public static boolean isValidFormat(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // 엄격한 형식 검사
        try {
            // 지정된 형식으로 변환해보고 성공하면 true, 실패하면 false
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false; // 형식이 맞지 않으면 false 반환
        }
    }

    // 입력된 날짜를 yyyyMMdd 형식으로 변환
    public static String convertToYYYYMMDD(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(INPUT_DATE_FORMAT);
        SimpleDateFormat outputFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        try {
            // String > Date
            Date date = inputFormat.parse(inputDate);
            // yyyyMMdd 형식 변환
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
