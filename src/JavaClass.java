public class JavaClass {

    public static void main(String[] args) {
        System.out.println(new Workday(new Time(8), new Time(15)).calculateOvertime());
        System.out.println(Workday.getDefaultWorkingHours());
        System.out.println(Time.convertToTime(2.75));
        System.out.println(new Time(null));
    }

}
