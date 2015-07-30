package gaftech.reeltour;

/**
 * Created by r.suleymanov on 08.07.2015.
 * email: ruslancer@gmail.com
 */

public class Configuration {
    public static final String HELPSHIFT_APIKEY = "d6a03a4e35d7842ce1bc143d56aab22b";
    public static final String HELPSHIFT_DOMAIN = "reeltour.helpshift.com";
    public static final String HELPSHIFT_APPID = "reeltour_platform_20150707195856808-3ad0a3e5f73dfe7";

    public interface ACTION {
        public static String RESTART_ACTION = "rsuleymanov.realty.foregroundservice.action.main";
        public static String STARTFOREGROUND_ACTION = "rsuleymanov.realty.foregroundservice.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "rsuleymanov.realty.foregroundservice.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
