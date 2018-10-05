import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;

public class JavaWorkingTime {

    public static void main(String[] args) {
        System.out.println(new JavaTime(1, 30).compareTo(new JavaTime(0, 45)) > 0);
        System.out.println(new JavaTime(1, 30).minus(new JavaTime(0, 45)));
        System.out.println(new Workday(new JavaTime(8), new JavaTime(16)).contains(new JavaTime(10, 30)));
        System.out.println(new Workday().contains(new JavaTime(7)));

        System.out.println(new Workday(new JavaTime(8), new JavaTime(17, 15)).calculateOvertime());
        System.out.println(new Workday(new JavaTime(8), new JavaTime(15)).calculateOvertime());

        System.out.println(JavaTimeUtil.convertToTime(3.5));

        try {
            new Workday(new JavaTime(-1), new JavaTime(12));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    static final class IntFormattingUtils {
        private IntFormattingUtils() {
        }

        static String padZeros(int length, Integer number) {
            return (Math.signum(number) >= 0 ? "" : '-') + format("%0" + length + "d", Math.abs(number));
        }
    }

    static final class JavaTimeUtil {
        private JavaTimeUtil() {
        }

        static JavaTime convertToTime(Number number) {
            if (number instanceof Double || number instanceof Float) {
                double time = number.doubleValue();
                double fullHours = Math.floor(time);
                return new JavaTime(Double.valueOf(fullHours).intValue(), Double.valueOf((time - fullHours) * 60).intValue());
            } else {
                int time = number.intValue();
                return new JavaTime(time);
            }
        }

        static double convertToDouble(JavaTime time) {
            return time.hours + time.minutes / 60.0;
        }
    }

    static class JavaTime implements Comparable<JavaTime> {
        final Integer hours;
        final Integer minutes;

        JavaTime(Integer hours) {
            this.hours = Optional.ofNullable(hours).orElse(0);
            this.minutes = 0;
        }

        JavaTime(Integer hours, Integer minutes) {
            this.hours = hours;
            this.minutes = minutes;
        }

        JavaTime minus(JavaTime other) {
            return JavaTimeUtil.convertToTime(JavaTimeUtil.convertToDouble(this) - JavaTimeUtil.convertToDouble(other));
        }

        @Override
        public int compareTo(@NotNull JavaTime o) {
            if (this.hours.equals(o.hours)) {
                return this.minutes.compareTo(o.minutes);
            }
            return this.hours.compareTo(o.hours);
        }

        @Override
        public String toString() {
            return IntFormattingUtils.padZeros(2, hours) + ":" + IntFormattingUtils.padZeros(2, minutes);
        }
    }

    static class Workday {
        static final int DEFAULT_WORKING_HORUS = 8;

        final JavaTime start;
        final JavaTime endInclusive;

        public Workday() {
            this.start = new JavaTime(8);
            this.endInclusive = new JavaTime(16);
        }

        public Workday(JavaTime start, JavaTime endInclusive) {
            this.start = start;
            this.endInclusive = endInclusive;

            Objects.requireNonNull(start, "Hours must not be null");
            Objects.requireNonNull(start, "Minutes must not be null");

            if (!isValid()) {
                throw new IllegalArgumentException(format("Range `%s to %s` does not define a valid workday.", start.toString(), endInclusive.toString()));
            }
        }

        private boolean isValid() {
            return start.compareTo(new JavaTime(0)) >= 0 && endInclusive.compareTo(new JavaTime(23, 59)) < 0;
        }

        JavaTime calculateOvertime() {
            return endInclusive.minus(start).minus(new JavaTime(DEFAULT_WORKING_HORUS));
        }

        boolean contains(JavaTime javaTime) {
            return this.start.compareTo(javaTime) < 0 && this.endInclusive.compareTo(javaTime) >= 0;
        }
    }

}
